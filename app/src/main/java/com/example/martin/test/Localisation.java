package com.example.martin.test;

/**
 * Created by martin on 02/02/18.
 */

public class Localisation {

    private int id;
    private long time;
    private double latitude;
    private double longitude;
    private int duree;
    private int idResto;



    public Localisation(){}

    public Localisation(long time,double latitude,double longitude,int duree, int idResto){
        this.time=time;
        this.latitude=latitude;
        this.longitude=longitude;
        this.duree=duree;
        this.idResto=idResto;
    }
    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return id;
    }

    public int getIdResto() {
        return idResto;
    }

    public long getTime(){  return time;
    }
    public double getLatitude(){ return latitude;}

    public double getLongitude() {
        return longitude;
    }


    int getDuree(){return duree;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;

    }

    public void setIdResto(int idResto) {
        this.idResto = idResto;
    }

    public void setTime(long time) {
        this.time = time;
    }

    void setDuree(int duree) {this.duree=duree;}

    @Override
    public String toString() {
        StringBuilder res=new StringBuilder();
        res.append("id: ");
        res.append(id);
        res.append("time : ");
        res.append(time);
        res.append(" lat : ");
        res.append(latitude);
        res.append(" long : ");
        res.append(longitude);
        res.append(" duree : ");
        res.append(duree);
        res.append(" idResto: ");
        res.append(idResto);
        return res.toString();
    }
}
