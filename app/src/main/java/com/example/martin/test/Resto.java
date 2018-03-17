package com.example.martin.test;

/**
 * Created by martin on 16/03/18.
 */

public class Resto {

	private int id;
	private String name;
	private int idWeb;
	private float lat;
	private float lon;

	Resto(){}

	int getId() {
		return id;
	}

	int getIdWeb() {
		return idWeb;
	}

	String getName() {
		return name;
	}

	float getLat() {
		return lat;
	}

	float getLon() {
		return lon;
	}

	void setId(int id) {
		this.id = id;
	}

	void setName(String name) {
		this.name = name;
	}

	void setIdWeb(int idWeb) {
		this.idWeb = idWeb;
	}

	void setLat(float lat) {
		this.lat = lat;
	}

	void setLon(float lon) {
		this.lon = lon;
	}

	@Override
	public String toString() {
		return name+": id= "+id+"/n";
	}
}
