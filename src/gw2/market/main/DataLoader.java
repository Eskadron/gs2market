package gw2.market.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import gw2.market.hibernate.HibernateUtils;

public class DataLoader {
	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws IOException {
//		BufferedReader reader = new BufferedReader(new FileReader(
//				"D:\\Ruby21-x64\\bin\\item_details.txt"));
//		while (reader.ready()) {
//			JSONObject json = (JSONObject) JSONValue.parse(reader
//					.readLine());
//			long id = (long) json.get("id");
//			String type = (String) json.get("type");
//			
//			parseJson(json, "all_items", id);
////			break;
//		}
//
//		Session session = HibernateUtils.getSession();
//		Transaction t = session.beginTransaction();
//		for(Entry<String, Set<String>> entry : tableColumns.entrySet()){
//			String table = entry.getKey();
//			Set<String> colStrings = entry.getValue();
//			
//			StringBuilder createSQL = new StringBuilder( "CREATE TABLE IF NOT EXISTS " + table + " (id int not null primary key ");
//			
//			for(String colString : colStrings){
//				createSQL.append(colString);
//			}
//			
//			createSQL.append(", key `level` (level), key `rarity` (rarity), key `name` (name));");
//			
//			
//			session.createSQLQuery(createSQL.toString()).executeUpdate();
//			
//		}
//		t.commit();
//		
//	
//		session = HibernateUtils.getSession();
//		t = session.beginTransaction();
//		
//		for(String insert : inserts){
//			session.createSQLQuery(insert).executeUpdate();
//		}	
//		t.commit();
//		
//		
//		HibernateUtils.close();
		
	}
	
	static Map<String, Set<String>> tableColumns = new HashMap<String, Set<String>>();
	static List <String> inserts = new ArrayList();
	
	static String attributeNames[] = {"Power", "ConditionDamage", "Precision", "CritDamage", "Vitality", "Toughness", "Healing"}	;
	
	public static void parseJson(JSONObject json, String table, Long id){
		
		Set<String> colStrings = tableColumns.get(table);
		if(colStrings == null){
			colStrings = new LinkedHashSet<String>();
			tableColumns.put(table, colStrings);
		}
		
	//	StringBuilder createSQL = new StringBuilder( "CREATE TABLE IF NOT EXISTS " + table + " (id int not null primary key ");
		StringBuilder insertSQL = new StringBuilder( "INSERT INTO " + table + " set id = " + id);

		insertColumn("type", json, colStrings, insertSQL);
		insertColumn("level", json, colStrings, insertSQL);
		insertColumn("rarity", json, colStrings, insertSQL);
		insertColumn("name", json, colStrings, insertSQL);
		
//		if(json.containsKey("details")){
//			JSONObject detailsJson = (JSONObject) json.get("details");
//			
//			if(detailsJson.containsKey("infix_upgrade")){
//				JSONObject infixUpgrade = (JSONObject)detailsJson.get("infix_upgrade");
//	
//				for(String attName : attributeNames){
//					colStrings.add(",\n `" + attName.toLowerCase() + "` int default 0" );
//				}
//				
//				JSONArray attributes = (JSONArray) infixUpgrade.get("attributes");
//				for(JSONObject attribute : (Collection<JSONObject>)attributes){
//					insertSQL.append(", `" + ((String)attribute.get("attribute")).toLowerCase() + "` = '" + attribute.get("modifier") + "'");
//				}
//			}
//			
//			for(String key : (Collection<String>)detailsJson.keySet()){
//				switch (key) {
//				case "infix_upgrade":
//					break;
//				case "flags":
//					insertColumn("detail_flags", detailsJson, colStrings, insertSQL);
//					break;
//				default:	
//					insertColumn(key, detailsJson, colStrings, insertSQL);
//				}
//			}
//		}
		insertColumn("vendor_value", json, colStrings, insertSQL);
		insertColumn("flags", json, colStrings, insertSQL);
		insertColumn("game_types", json, colStrings, insertSQL);
		insertColumn("restrictions", json, colStrings, insertSQL);
		

//
//		for (String key : (Collection<String>) json.keySet()) {
//			Object value = json.get(key);
//
//			switch (key) {
//			case "details":
//			case "type":
//			case "rarity":
//			case "name":
//			case "level":
//			case "id":
//				break;
//			default:
//				insertColumn(key, value, createSQL, insertSQL);
//			}
//		}
		insertSQL.append(";");
		
		inserts.add(insertSQL.toString());
		
		
	}
	
	public static void insertColumn(String key, JSONObject json, Set<String> colStrings, StringBuilder insertSQL){
		Object value = json.get(key);
		if(value == null)
			return;
		String colType = value instanceof Long? "int" : "varchar(255)";
		colStrings.add(",\n `" + key + "` " + colType);
		String valuestr= value.toString().replaceAll("'", "");
		insertSQL.append(", `" + key + "` = '" + valuestr.substring(0, Math.min(valuestr.length(), 255)) + "'");
	}
}
