package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.example.martin.test.Value.COL_LATITUDE_TEMP;
import static com.example.martin.test.Value.COL_LONGITUDE_TEMP;
import static com.example.martin.test.Value.COL_PRECISION_TEMP;
import static com.example.martin.test.Value.COL_TIME_TEMP;
import static com.example.martin.test.Value.NOM_BDD_TEMP;
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

	long insertTemp(long time, double latDeg,double lonDeg,int precision) {

		ContentValues content = new ContentValues();
		content.put(COL_TIME_TEMP, time);
		content.put(COL_LATITUDE_TEMP, latDeg);
		content.put(COL_LONGITUDE_TEMP, lonDeg);
		content.put(COL_PRECISION_TEMP, precision);
		return bdd.insert(TABLE_TEMP, null, content);
	}

	void removeAllTemp(){
		bdd.execSQL("DELETE FROM "+TABLE_TEMP);
	}

	Cursor getCursor(){
		return bdd.rawQuery("SELECT * FROM " + TABLE_TEMP ,null);
	}

}
