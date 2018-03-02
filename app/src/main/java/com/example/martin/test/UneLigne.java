package com.example.martin.test;

import static com.example.martin.test.Value.ID_RESTO_DEFAUT;

/**
 * Created by martin on 02/03/18.
 */

class UneLigne {
	int indi;
	long date;
	int idResto;
	int duree; //en seconde
	int distance;  //en m
	String nomResto;

	//constructeur pour arret
	UneLigne(int indi,long date,int distance,int duree,int idResto){
		this.indi=indi;
		this.date=date;
		this.idResto=idResto;
		this.distance=distance;
		this.duree=duree;
		this.nomResto="";
	}
	//constructeur pour deplacement
	UneLigne(int indi,long date,int distance,int duree){
		this(indi,date,distance,duree,ID_RESTO_DEFAUT);
	}

	void setNomResto(String nomResto) {
		this.nomResto = nomResto;
	}

	int getIndi(){
		return indi;
	}

	long getDate(){ return date;}

	String getNomResto(){ return nomResto;}

	int getIdResto(){
		return idResto;
	}

	int getDuree(){
		return duree;
	}

	int getDistance(){
		return distance;
	}


	@Override
	public String toString() {
		String res="date : ";
		res += String.valueOf(date);
		res +=" indi: ";
		res += String.valueOf(indi);
		res +=" duree : ";
		res += String.valueOf(duree);
		res +=" distance: ";
		res += String.valueOf(distance);
		res +=" idResto: ";
		res += String.valueOf(idResto);
		return res;
	}





}
