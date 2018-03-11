package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by martin on 19/02/18.
 */

class BaseSQLiteZone extends SQLiteOpenHelper {



	//base de donnee zone
	final String NOM_BDD_ZONE = "zone.db";
	final String TABLE_ZONE = "table_zone";
	final String COL_ID_ZONE = "ID";
	final int NUM_COL_ID_ZONE = 0;
	final String COL_LATRAD_ZONE = "LATITUDE";
	final int NUM_COL_LATRAD_ZONE = 1;
	final String COL_LONRAD_ZONE = "LONGITUDE";
	final int NUM_COL_LONRAD_ZONE = 2;
	final String COL_TEXT_ZONE = "TEXT";
	final int NUM_COL_TEXT_ZONE = 3;
	final String COL_PAYS_ZONE = "PAYS";
	final int NUM_COL_PAYS_ZONE = 4;



	private final String CREATE_BDD="CREATE TABLE " + TABLE_ZONE + " (" +
			COL_ID_ZONE + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_LATRAD_ZONE + " REAL, "+COL_LONRAD_ZONE
			+ " REAL, "+ COL_TEXT_ZONE + "  TEXT,"+COL_PAYS_ZONE+" INTEGER);";




	public BaseSQLiteZone(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
		insertZone(db,0,0,"Ville inconnue",0); //zone inconnue

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL("DROP TABLE " + TABLE_ZONE);
		onCreate(db);

	}
	private void insertZone(SQLiteDatabase db, double latDeg, double lonDeg, String zoneName,int pays){

		double latRad=Math.toRadians(latDeg);
		double lonRad=Math.toRadians(lonDeg);

		ContentValues content = new ContentValues();
		content.put(COL_LATRAD_ZONE, latRad);
		content.put(COL_LONRAD_ZONE, lonRad);
		content.put(COL_TEXT_ZONE, zoneName);
		content.put(COL_PAYS_ZONE,pays);
		db.insert(TABLE_ZONE, null, content);

	}

}
