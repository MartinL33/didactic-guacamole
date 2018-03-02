package com.example.martin.test;

import static com.example.martin.test.Value.DUREE_DEFAUT;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;

/**
 * Created by martin on 02/02/18.
 */

class Localisation {

    private int id;
    private long time;
    private float latitude;
    private float longitude;
    private int duree;    //en seconde
    private int indication;
    private int idResto;

    Localisation(){}

    Localisation(long time,float latitude,float longitude, int indication){
    	this(time,latitude,longitude,indication,DUREE_DEFAUT,ID_RESTO_DEFAUT);
	}
	Localisation(long time,float latitude,float longitude, int indication,int duree){
		this(time,latitude,longitude,indication,duree, ID_RESTO_DEFAUT);
	}


    private Localisation(long time, float latitude, float longitude, int indication, int duree, int idResto){
        this.time=time;
        this.latitude=latitude;
        this.longitude=longitude;
        this.duree=duree;
        this.indication=indication;
        this.idResto=idResto;
    }
    void setId(int id){
        this.id=id;
    }
    int getId(){
        return id;
    }

    int getIdResto() {
        return idResto;
    }

    long getTime(){  return time;
    }
	float getLatitude(){ return latitude;}

	float getLongitude() {
        return longitude;
    }

    int getIndication(){
    	return indication;
	}
    int getDuree(){return duree;}

    void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    void setLongitude(float longitude) {
        this.longitude = longitude;

    }
    void setIndication(int indication){
    	this.indication=indication;
	}

    void setIdResto(int idResto) {
        this.idResto = idResto;
    }

    void setTime(long time) {
        this.time = time;
    }

    void setDuree(int duree) {this.duree=duree;}

    @Override
    public String toString() {

        String res="id: ";
        res += String.valueOf(id);
		res +="time : ";
		res += String.valueOf(time);
		res +=" lat : ";
		res += String.valueOf(latitude);
		res +=" long : ";
		res += String.valueOf(longitude);
		res +=" duree : ";
		res += String.valueOf(duree);
		res +=" indication: ";
		res += String.valueOf(indication);
		res +=" idResto: ";
		res += String.valueOf(idResto);
        return res;
    }
}
