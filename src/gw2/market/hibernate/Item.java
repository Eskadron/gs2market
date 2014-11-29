package gw2.market.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "all_items")
public class Item {
	
	@Id
	@Column(name="id")
	private int id;
	
	@Column(name="type")
	private String type;
	
	@Column(name="level")
	private int level;
	
	@Column(name="rarity")
	private String rarity;
	
	@Column(name="name")
	private String name;
	
	@Column(name="flags")
	private String flags;
	
	@Column(name="game_types")
	private String game_types;
	
	@Column(name="auto_update")
	private String autoUpdate;
	
	@Column(name="update_interval")
	private int updateInterval;
	
	@Column(name="last_updated_at")
	private Date lastUpdatedAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public String getGame_types() {
		return game_types;
	}

	public void setGame_types(String game_types) {
		this.game_types = game_types;
	}

	public String getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(String autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}
}
