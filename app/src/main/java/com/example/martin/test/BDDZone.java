package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.martin.test.Value.COL_LATRAD_ZONE;
import static com.example.martin.test.Value.COL_LONRAD_ZONE;
import static com.example.martin.test.Value.COL_PAYS_ZONE;
import static com.example.martin.test.Value.COL_TEXT_ZONE;
import static com.example.martin.test.Value.NOM_BDD_ZONE;
import static com.example.martin.test.Value.NUM_COL_LATRAD_ZONE;
import static com.example.martin.test.Value.NUM_COL_LONRAD_ZONE;
import static com.example.martin.test.Value.NUM_COL_PAYS_ZONE;
import static com.example.martin.test.Value.NUM_COL_TEXT_ZONE;
import static com.example.martin.test.Value.SEUILZONE;
import static com.example.martin.test.Value.TABLE_ZONE;
import static com.example.martin.test.Value.distence2;


/**
 * Created by martin on 19/02/18.
 */

class BDDZone {


	private static final int VERSION = 1;
	private SQLiteDatabase bdd;
	private BaseSQLiteZone zone;
	String textZoneActual="Ville inconnue";
	int paysActual=0;
	BDDZone(Context context) {
		zone = new BaseSQLiteZone(context, NOM_BDD_ZONE, null, VERSION);

	}

	void openForWrite() {
		bdd = zone.getWritableDatabase();
	}

	void openForRead() {
		bdd = zone.getReadableDatabase();
	}

	void close() {
		bdd.close();
	}

	void removeAllZone(){
		bdd.execSQL("DELETE FROM "+TABLE_ZONE);
	}

	long insertZone(double latDeg,double lonDeg,String zoneName,int pays) {
		double latRad=Math.toRadians(latDeg);
		double lonRad=Math.toRadians(lonDeg);

		ContentValues content = new ContentValues();

		content.put(COL_LATRAD_ZONE, latRad);
		content.put(COL_LONRAD_ZONE, lonRad);
		content.put(COL_TEXT_ZONE, zoneName);
		content.put(COL_PAYS_ZONE,pays);
		return bdd.insert(TABLE_ZONE, null, content);
	}

	/**
	 *
	 * @param latRad1  latitude en radian
	 * @param lonRad1  longitude en radian
	 * @return une chaine de caractère correspondant à la zone indiquée par les paramétres
	 */
	int getIdZone(double latRad1,double lonRad1){

		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_ZONE,null);
		int index=1;
		if (c.getCount()>1){
			double latRad2;
			double lonRad2;
			int minDistence2=2147483646;  //valeur max int soit 21 fois le seuil de 10km
			int d2;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				latRad2=c.getDouble(NUM_COL_LATRAD_ZONE);
				lonRad2=c.getDouble(NUM_COL_LONRAD_ZONE);


				d2=distence2(latRad1,latRad2,lonRad1,lonRad2);
				if (d2<minDistence2) {
					index=c.getPosition();
					minDistence2=d2;
				}

			}
			if(minDistence2<SEUILZONE*SEUILZONE){
				c.moveToPosition(index);
				textZoneActual=c.getString(NUM_COL_TEXT_ZONE);
				paysActual=c.getInt(NUM_COL_PAYS_ZONE);
			}
			else{
				index=1;
				Log.d("zone","zone inconnue");
			}
		}
		else{
			Log.d("zone","table zone vide");

		}
		c.close();

		Log.d("zone","res : "+index);

		return index;
	}

	/*
	int getIdZone(double latRad,double lonRad){

		int res=1;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_ZONE,null);

		if (c.getCount()>0){
			double latRad2;
			double lonRad2;
			int idMinDistence=-1;
			int minDistence2=2147483646;  //valeur max int soit 21 fois le seuil de 10km
			int d2;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				latRad2=c.getDouble(NUM_COL_LATRAD_ZONE);
				lonRad2=c.getDouble(NUM_COL_LONRAD_ZONE);
				d2=distence2(latRad,latRad2,lonRad,lonRad2);
				if (d2<minDistence2) {
					idMinDistence=c.getInt(NUM_COL_ID_ZONE);
					minDistence2=d2;
				}
			}
			if(minDistence2<SEUILZONE*SEUILZONE && idMinDistence!=-1){

				res=idMinDistence;
			}
			else{
				Log.d("zone","zone inconne");
			}
		}
		else{
			Log.d("zone","tableVide");

		}
		c.close();

		Log.d("zone","res : "+res);
		return res;
	}
*/
	Boolean isEmpty(){
		Boolean result=true;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_ZONE,null);
		if(c.getCount()>1) result=false;
		c.close();
		return result;
	}

}
