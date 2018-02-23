package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.martin.test.Value.COL_LATITUDE_ZONE;
import static com.example.martin.test.Value.COL_LONGITUDE_ZONE;
import static com.example.martin.test.Value.COL_TEXT_ZONE;
import static com.example.martin.test.Value.NOM_BDD_ZONE;
import static com.example.martin.test.Value.NUM_COL_ID_ZONE;
import static com.example.martin.test.Value.NUM_COL_LATITUDE_ZONE;
import static com.example.martin.test.Value.NUM_COL_LONGITUDE_ZONE;
import static com.example.martin.test.Value.NUM_COL_TEXT_ZONE;
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.rayonPetitCercle;
import static com.example.martin.test.Value.SEUILZONE;
import static com.example.martin.test.Value.TABLE_ZONE;
import static com.example.martin.test.Value.distence2;


/**
 * Created by martin on 19/02/18.
 */

class BDDZone {


	private static final int VERSION = 2;
	private SQLiteDatabase bdd;
	private BaseSQLiteZone zone;

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

	long insertZone(double lat,double lon,String zoneName) {
		lat=Math.toRadians(lat);
		lon=Math.toRadians(lon);

		ContentValues content = new ContentValues();

		content.put(COL_LATITUDE_ZONE, lat);
		content.put(COL_LONGITUDE_ZONE, lon);
		content.put(COL_TEXT_ZONE, zoneName);
		return bdd.insert(TABLE_ZONE, null, content);
	}

	/**
	 *
	 * @param lat1  latitude en degres
	 * @param lon1  longitude en degres
	 * @return une chaine de caractère correspondant à la zone indiquée par les paramétres
	 */
	String getTextZone(double lat1,double lon1){
		lat1=Math.toRadians(lat1);
		lon1=Math.toRadians(lon1);

		String res="";
		rayonPetitCercle = (int) (RAYONTERRE*Math.cos(lat1));

		openForRead();
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_ZONE,null);

		if (c.getCount()>0){
			double lat2;
			double lon2;
			int index=0;
			int minDistence2=2147483646;  //valeur max int soit 21 fois le seuil de 10km
			int d2=0;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				lat2=c.getDouble(NUM_COL_LATITUDE_ZONE);
				lon2=c.getDouble(NUM_COL_LONGITUDE_ZONE);
				d2=distence2(lat1,lat2,lon1,lon2);
				if (d2<minDistence2) {
					index=c.getPosition();
					minDistence2=d2;
				}
			}
			if(minDistence2<SEUILZONE*SEUILZONE){
				c.moveToPosition(index);
				res=c.getString(NUM_COL_TEXT_ZONE);
			}
			else{
				Log.d("zone","zone inconnue");
			}
		}
		else{
			Log.d("zone","table zone vide");

		}
		c.close();
		close();
		Log.d("zone","res : "+res);

		return res;
	}

	int getIdZone(double lat1,double lon1){
		lat1=Math.toRadians(lat1);
		lon1=Math.toRadians(lon1);

		openForRead();
		rayonPetitCercle = (int) (RAYONTERRE*Math.cos(lat1));
		int res=1;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_ZONE,null);

		if (c.getCount()>0){
			double lat2;
			double lon2;
			int idMinDistence=-1;
			int minDistence2=2147483646;  //valeur max int soit 21 fois le seuil de 10km
			int d2;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				lat2=c.getDouble(NUM_COL_LATITUDE_ZONE);
				lon2=c.getDouble(NUM_COL_LONGITUDE_ZONE);
				d2=distence2(lat1,lat2,lon1,lon2);
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
		close();
		Log.d("zone","res : "+res);
		return res;
	}



}
