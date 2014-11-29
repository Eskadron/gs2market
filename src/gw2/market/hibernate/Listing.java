package gw2.market.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "listings")
public class Listing {
	public Listing(){
		
	}
	
	public Listing(int item_id, ListingType type, int price, int quantity, Date createdAt) {
		super();
		this.id = null;
		this.item_id = item_id;
		this.type = type;
		this.price = price;
		this.quantity = quantity;
		this.createdAt = new Date(createdAt.getTime());
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name="item_id")
	private int item_id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type")
	private ListingType type;
	
	@Column(name="price")
	private int price;
	
	@Column(name="quantity")
	private int quantity;
	
	@Column(name="created_at")
	private Date createdAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public ListingType getType() {
		return type;
	}

	public void setType(ListingType type) {
		this.type = type;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public static enum ListingType{
		BUY, SELL
	}
}
