package com.example.martin.test;

import android.support.annotation.NonNull;

import java.util.Comparator;

import static com.example.martin.test.Value.DUREE_DEFAUT;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.IND_DEFAUT;
import static com.example.martin.test.Value.PRECISION_DEFAUT;

/**
 * Created by martin on 02/02/18.
 */

class Localisation implements Comparable,Comparator {

    private int id;
    private long time;
    private float latitude;
    private float longitude;
    private int duree;    //en ms
    private int indication;
    private int idResto;
    private float precision;

    Localisation(){
		duree=DUREE_DEFAUT;
		idResto=ID_RESTO_DEFAUT;
		indication=IND_DEFAUT;
		precision=PRECISION_DEFAUT;
	}

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

    void fixPosition(Localisation l){
    	this.latitude=l.getLatitude();
		this.longitude=l.getLongitude();
	}

    void setId(int id){
        this.id=id;
    }

    void setPrecision(float precision){
    	this.precision=precision;
	}

	float getPrecision(){
    	return precision;
	}

    int getId(){
        return id;
    }

    int getIdResto() {
        return idResto;
    }

    long getTime(){
    	return time;
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


		String res ="time : ";
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

	@Override
	public int compare(Object o1, Object o2) {
		if((o1 instanceof Localisation)&&(o2 instanceof Localisation)) return ((Localisation) o1).compareTo(o2);
		else throw new AssertionError("compare Localisation to Objet");
	}

	@Override
    public boolean equals(Object o){
    	if(!(o instanceof Localisation)) return false;
    	return this.equals((Localisation) o);
	}

	public boolean equals(Localisation o){
		if(this==o) return true;
		if(o==null) return false;
		else 	return time==o.time;

	}

	@Override
	public int hashCode(){
		return (int) (time);
	}


	@Override
	public int compareTo(@NonNull Object o) {
    	if(o instanceof Localisation) {
    		if(this.time-((Localisation) o).time>0) return 1;
    		else if (this.time==((Localisation) o).time) return 0;
			else return -1;
		}
		else throw new AssertionError("compare Localisation to Objet");
	}
}
