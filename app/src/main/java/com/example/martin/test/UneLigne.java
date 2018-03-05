package com.example.martin.test;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_RESTO_CONFIRME;
import static com.example.martin.test.Value.intToString;
import static java.text.DateFormat.getTimeInstance;

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



	public String toString(Context context) {
		String [] stringIndication=context.getResources().getStringArray(R.array.indication);

		String res="";
		DateFormat df=getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
		res+=df.format(new Date(date));
		res+=";";
		if (indi == IND_HYPO_RESTO || indi == IND_RESTO || indi == IND_RESTO_CONFIRME) {
			res+=nomResto;
		} else res += stringIndication[indi];

		res +=";";

		int du=duree/60;
		if(duree>1) {
			if (du < 10) res +=String.valueOf(du) + " min";
			else res +=String.valueOf(du) + " min";
					}

		res +=";";
		if(distance>0) {
			final int di = distance / 1000;
			final int di2 = ((distance % 1000) / 10);

			res += String.valueOf(di) + "." + intToString(di2) + "km";
		}


		return res;
	}





}
