package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static com.example.martin.test.Value.COL_DUREE_LOCAL;
import static com.example.martin.test.Value.COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.COL_IND_LOCAL;
import static com.example.martin.test.Value.COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.COL_TIME_LOCAL;
import static com.example.martin.test.Value.NOM_BDD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_DUREE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IND_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_TIME_LOCAL;
import static com.example.martin.test.Value.TABLE_LOCALISATIONS;
/**
 * Created by martin on 02/02/18.
 */

 class BDDLocalisation {
    private static final int VERSION = 1;
    private SQLiteDatabase bdd;
    private BaseSQLiteLocalisation localisations;


     BDDLocalisation(Context context) {
        localisations = new BaseSQLiteLocalisation(context, NOM_BDD_LOCAL, null, VERSION);

    }

     void openForWrite() {
        bdd = localisations.getWritableDatabase();
    }

     void openForRead() {
        bdd = localisations.getReadableDatabase();
    }

     void close() {
        bdd.close();
    }

     long insertLocalisation(Localisation localisation) {

        ContentValues content = new ContentValues();
        content.put(COL_TIME_LOCAL, localisation.getTime());
        content.put(COL_LATRAD_LOCAL, localisation.getLatitude());
        content.put(COL_LONRAD_LOCAL, localisation.getLongitude());
		content.put(COL_DUREE_LOCAL, localisation.getDuree());
		content.put(COL_IND_LOCAL,localisation.getIndication());
        content.put(COL_IDRESTO_LOCAL, localisation.getIdResto());
        return bdd.insert(TABLE_LOCALISATIONS, null, content);
    }

     int updateLocalisation(int id, Localisation localisation) {
        ContentValues content = new ContentValues();
        content.put(COL_TIME_LOCAL, localisation.getTime());
        content.put(COL_LATRAD_LOCAL, localisation.getLatitude());
        content.put(COL_LONRAD_LOCAL, localisation.getLongitude());
		content.put(COL_DUREE_LOCAL, localisation.getDuree());
		content.put(COL_IND_LOCAL,localisation.getIndication());
        content.put(COL_IDRESTO_LOCAL, localisation.getIdResto());
        return bdd.update(TABLE_LOCALISATIONS, content, COL_TIME_LOCAL + " = " + id, null);
    }

    boolean isEmpty(){
		Boolean result=true;
		Cursor c=bdd.rawQuery("SELECT * FROM "+ TABLE_LOCALISATIONS ,null);
		if(c.getCount()>0) result=false;
        c.close();
		return result;
    }
	Localisation getLastLocation(){

		Cursor c=bdd.rawQuery("SELECT * FROM "+ TABLE_LOCALISATIONS+" ORDER BY "+ COL_TIME_LOCAL +" DESC LIMIT 1" ,null);
		c.moveToFirst();
		Localisation l=new Localisation();
		l.setTime(c.getLong(NUM_COL_TIME_LOCAL));
		l.setLatitude(c.getDouble(NUM_COL_LATRAD_LOCAL));
		l.setLongitude(c.getDouble(NUM_COL_LONRAD_LOCAL));
		l.setDuree(c.getInt(NUM_COL_DUREE_LOCAL));
		l.setIndication(c.getInt(NUM_COL_IND_LOCAL));
		l.setIdResto(c.getInt(NUM_COL_IDRESTO_LOCAL));
		return l;
	}

    Cursor getCursorFrom(int start){
        return bdd.rawQuery("SELECT * FROM " + TABLE_LOCALISATIONS + " WHERE " + COL_TIME_LOCAL + " >= "+String.valueOf(start) ,null);
    }

    Cursor getCursorBetween(long start,long stop){
        return bdd.rawQuery("SELECT * FROM " + TABLE_LOCALISATIONS + " WHERE " + COL_TIME_LOCAL + " BETWEEN "+start + " AND " +stop,null);
    }


     int removeLocalisation(int id){
        return bdd.delete(TABLE_LOCALISATIONS,COL_TIME_LOCAL+" = "+id,null);
    }

    void removeAll(){
        bdd.delete(TABLE_LOCALISATIONS,COL_TIME_LOCAL+" >= '0' ",null);
    }

     ArrayList<Localisation> getAllLocalisation(){
        Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_LOCALISATIONS,null);

        if(c.getCount()==0) {
            c.close();
            return null;
        }
        ArrayList<Localisation> res= new ArrayList<>();

        while(c.moveToNext()){
            Localisation l=new Localisation();
            l.setTime(c.getLong(NUM_COL_TIME_LOCAL));
            l.setLatitude(c.getDouble(NUM_COL_LATRAD_LOCAL));
            l.setLongitude(c.getDouble(NUM_COL_LONRAD_LOCAL));
            l.setDuree(c.getInt(NUM_COL_DUREE_LOCAL));
            l.setIndication(c.getInt(NUM_COL_IND_LOCAL));
            l.setIdResto(c.getInt(NUM_COL_IDRESTO_LOCAL));
            res.add(l);
        }
        c.close();
        return res;
    }


}
