package gw2.market.main;

import gw2.market.hibernate.HibernateUtils;
import gw2.market.hibernate.HibernateUtils.AutoCommit;
import gw2.market.hibernate.Item;
import gw2.market.hibernate.Listing;
import gw2.market.hibernate.Listing.ListingType;
import gw2.market.hibernate.MarketEvent.EventType;
import gw2.market.hibernate.MarketEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.annotations.Parent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Joiner;

public class ListingUpdater {
	public static void main(String args[]) throws Exception {
//		while(true){
//			Session session = HibernateUtils.getSession();
//			Transaction t = session.beginTransaction();
//			
//			Listing listing = new Listing();
//			listing.setItem_id(1);
//			listing.setQuantity(2);
//			listing.setType(ListingType.BUY);
//			
//			session.save(listing);
//			t.commit();
//			HibernateUtils.close();
		while(true){
			System.out.println("Working...");
			try{
			String idString = null;
			List<Integer> idsToUpdate = null;
			try(AutoCommit ac = new AutoCommit()){
				idsToUpdate = ac.getSession().createSQLQuery("SELECT id from all_items where auto_update = 'Y' order by timestampadd(SECOND, update_interval, last_updated_at) asc limit 100").list();
				idString = Joiner.on(",").join(idsToUpdate);
			}
			
			String listingUrlString = "https://api.guildwars2.com/v2/commerce/listings?ids=" + idString;
			URL url = new URL(listingUrlString);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			JSONArray listingsJson = (JSONArray) JSONValue.parse(reader.readLine());
			Date updateTimestamp = new Date();
			Map<Integer, Collection<Listing>> newBuyMap = parseBuys(listingsJson, updateTimestamp);
			Map<Integer, Collection<Listing>> newSellMap = parseSells(listingsJson, updateTimestamp);
			
			try(AutoCommit ac = new AutoCommit()){
				for(Integer itemId : idsToUpdate){
					TreeMap<Integer, Integer> newBuys = getPriceToQuantity(newBuyMap.get(itemId));
					TreeMap<Integer, Integer> newSells = getPriceToQuantity(newSellMap.get(itemId));
					
					TreeMap<Integer, Integer> previousBuys = getPriceToQuantity(ac.getSession().createQuery("From Listing where item_id = :item_id and type = 'BUY'").setInteger("item_id", itemId).list());
					TreeMap<Integer, Integer> previousSells = getPriceToQuantity(ac.getSession().createQuery("From Listing where item_id = :item_id and type = 'SELL'").setInteger("item_id", itemId).list());
					
					if(!previousBuys.isEmpty() && !previousSells.isEmpty()){
											
						for(Entry<Integer, Integer> entry : previousBuys.descendingMap().entrySet()){
							int price = entry.getKey();
							int previousQuantity = entry.getValue();
							Integer newQuantity = newBuys.get(price);
							
							if(newQuantity == null){
								publishSellEvent(itemId, previousQuantity, price, updateTimestamp, ac.getSession());
							} else if(newQuantity < previousQuantity){
								publishSellEvent(itemId, previousQuantity - newQuantity, price, updateTimestamp, ac.getSession());
								if(newQuantity > 0){
									break;
								}
							}
						}
						
						for(Entry<Integer, Integer> entry : previousSells.entrySet()){
							int price = entry.getKey();
							int previousQuantity = entry.getValue();
							Integer newQuantity = newSells.get(price);
							
							if(newQuantity == null){
								publishSellEvent(itemId, previousQuantity, price, updateTimestamp, ac.getSession());
								break;
							} else if(newQuantity < previousQuantity){
								publishSellEvent(itemId, previousQuantity - newQuantity, price, updateTimestamp, ac.getSession());
								if(newQuantity > 0){
									break;
								}
							}
						}
					}
					
					ac.getSession().createQuery("delete from Listing where item_id = :item_id").setInteger("item_id", itemId).executeUpdate();
					if(newBuyMap.containsKey(itemId)){
						for(Listing listing : newBuyMap.get(itemId)){
							ac.getSession().save(listing);
						}
					}
					if(newSellMap.containsKey(itemId)){
						for(Listing listing : newSellMap.get(itemId)){
							ac.getSession().save(listing);
						}
					}
					
					Item item = (Item) ac.getSession().get(Item.class, itemId);
					item.setUpdateInterval(item.getUpdateInterval()+4);
					item.setLastUpdatedAt(updateTimestamp);
					ac.getSession().update(item);
				}
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("Sleeping");
			Thread.sleep(5*1000);
			
		}
	}
	
	public static void publishSellEvent(int itemId, int quantity, int price, Date updateTimestamp, Session session){
		session.save(new MarketEvent(itemId, EventType.SELL, price, quantity, updateTimestamp));
		
		Item item = (Item) session.get(Item.class, itemId);
		item.setUpdateInterval(item.getUpdateInterval()/2);
		session.update(item);
	}
	
	public static TreeMap<Integer, Integer> getPriceToQuantity(Collection<Listing> listings){
		TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		if(listings == null || listings.isEmpty()){
			return map;
		}
		
		for(Listing listing : listings){
			map.put(listing.getPrice(), listing.getQuantity());
		}
		return map;
	}
	
	public static Map<Integer, Collection<Listing>> parseBuys(JSONArray listingsArray, Date createdAt){
		Map<Integer, Collection<Listing>> listingMap = new TreeMap<Integer, Collection<Listing>>();
		
		for(JSONObject listingsJson : (Collection<JSONObject>)listingsArray){
			int itemId = ((Long)listingsJson.get("id")).intValue();
			Collection<Listing> listings = new ArrayList<Listing>();
			
			for(JSONObject buy : (Collection<JSONObject>) listingsJson.get("buys")){
				listings.add(parseListing(buy, itemId, ListingType.BUY, createdAt));
			}
			
			listingMap.put(itemId, listings);
		}
		return listingMap;
	}
	
	public static Map<Integer, Collection<Listing>> parseSells(JSONArray listingsArray, Date createdAt){
		Map<Integer, Collection<Listing>> listingMap = new TreeMap<Integer, Collection<Listing>>();
		
		for(JSONObject listingsJson : (Collection<JSONObject>)listingsArray){
			int itemId = ((Long)listingsJson.get("id")).intValue();
			Collection<Listing> listings = new ArrayList<Listing>();

			for(JSONObject sell : (Collection<JSONObject>) listingsJson.get("sells")){
				listings.add(parseListing(sell, itemId, ListingType.SELL, createdAt));
			}
			listingMap.put(itemId, listings);
		}
		return listingMap;
	}
	
	private static Listing parseListing(JSONObject listingJson, int itemId, ListingType type, Date createdAt){
		return new Listing(itemId, type, ((Long)listingJson.get("unit_price")).intValue(),
				((Long)listingJson.get("quantity")).intValue(), createdAt);
	}
}
