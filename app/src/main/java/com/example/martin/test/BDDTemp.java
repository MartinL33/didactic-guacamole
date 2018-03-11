package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static com.example.martin.test.Value.COL_LATRAD_TEMP;
import static com.example.martin.test.Value.COL_LONRAD_TEMP;
import static com.example.martin.test.Value.COL_PRECISION_TEMP;
import static com.example.martin.test.Value.COL_TIME_TEMP;
import static com.example.martin.test.Value.NOM_BDD_TEMP;
import static com.example.martin.test.Value.NUM_COL_LATRAD_TEMP;
import static com.example.martin.test.Value.NUM_COL_LONRAD_TEMP;
import static com.example.martin.test.Value.NUM_COL_PRECISION_TEMP;
import static com.example.martin.test.Value.NUM_COL_TIME_TEMP;
import static com.example.martin.test.Value.TABLE_TEMP;

/**
 * Created by martin on 13/02/18.
 */

class BDDTemp {

	private static final int VERSION = 1;
	private SQLiteDatabase bdd;
	private BaseSQLiteTemp temp;


	BDDTemp(Context context) {
		temp = new BaseSQLiteTemp(context, NOM_BDD_TEMP, null, VERSION);

	}

	void openForWrite() {
		bdd = temp.getWritableDatabase();
	}

	void openForRead() {
		bdd = temp.getReadableDatabase();
	}
	void close() {
		bdd.close();
	}

	long insertTemp(long time, double latRad,double lonRad,int precision) {

		ContentValues content = new ContentValues();
		content.put(COL_TIME_TEMP, time);
		content.put(COL_LATRAD_TEMP, latRad);
		content.put(COL_LONRAD_TEMP, lonRad);
		content.put(COL_PRECISION_TEMP, precision);
		return bdd.insert(TABLE_TEMP, null, content);
	}

	void removeTempExceptLast(){
		long stop=getLastTime();
		if(stop>0) bdd.execSQL("DELETE FROM " + TABLE_TEMP+ " WHERE " + COL_TIME_TEMP+" < "+ stop);
	}
	long getLastTime(){
		long result=-1;

		Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_TEMP +" ORDER BY "+ COL_TIME_TEMP +" DESC LIMIT 1",null);

		if (c.getCount()==1) {
			c.moveToFirst();
			result= c.getLong(NUM_COL_TIME_TEMP);
		}

		c.close();
		return result;
	}

	long getFirstTime(){
		long result=-1;

		Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_TEMP +" ORDER BY "+ COL_TIME_TEMP +" LIMIT 1",null);

		if (c.getCount()==1) {
			c.moveToFirst();
			result= c.getLong(NUM_COL_TIME_TEMP);
		}

		c.close();
		return result;
	}



	Cursor getCursor(){
		return bdd.rawQuery("SELECT * FROM " + TABLE_TEMP ,null);
	}

	ArrayList<Localisation> getAllLocalisation(){

		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_TEMP,null);

		if(c.getCount()==0) {
			c.close();
			return null;
		}
		ArrayList<Localisation> res= new ArrayList<>();

		while(c.moveToNext()){
			Localisation l=new Localisation();
			l.setTime(c.getLong(NUM_COL_TIME_TEMP));
			l.setLatitude(c.getFloat(NUM_COL_LATRAD_TEMP));
			l.setLongitude(c.getFloat(NUM_COL_LONRAD_TEMP));
			l.setPrecision(c.getFloat(NUM_COL_PRECISION_TEMP));

			res.add(l);
		}
		c.close();
		return res;
	}


}
