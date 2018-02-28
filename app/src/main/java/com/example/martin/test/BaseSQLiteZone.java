package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.martin.test.Value.COL_ID_ZONE;
import static com.example.martin.test.Value.COL_LATRAD_ZONE;
import static com.example.martin.test.Value.COL_LONRAD_ZONE;
import static com.example.martin.test.Value.COL_TEXT_ZONE;
import static com.example.martin.test.Value.TABLE_ZONE;

/**
 * Created by martin on 19/02/18.
 */

public class BaseSQLiteZone extends SQLiteOpenHelper {

	private static final String CREATE_BDD="CREATE TABLE " + TABLE_ZONE + " (" +
			COL_ID_ZONE + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_LATRAD_ZONE + " REAL, "+COL_LONRAD_ZONE
			+ " REAL, "+ COL_TEXT_ZONE + "  TEXT);";




	public BaseSQLiteZone(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
		insertZone(db,0,0,"Ville inconnue"); //zone inconnue
		insertZone(db,48.866667,2.333333,"Paris");
		insertZone(db,44.8333,-0.5667,"Bordeaux");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL("DROP TABLE " + TABLE_ZONE);
		onCreate(db);

	}
	void insertZone(SQLiteDatabase db,double latDeg,double lonDeg,String zoneName){

		double latRad=Math.toRadians(latDeg);
		double lonRad=Math.toRadians(lonDeg);

		ContentValues content = new ContentValues();
		content.put(COL_LATRAD_ZONE, latRad);
		content.put(COL_LONRAD_ZONE, lonRad);
		content.put(COL_TEXT_ZONE, zoneName);
		db.insert(TABLE_ZONE, null, content);

	}

}
