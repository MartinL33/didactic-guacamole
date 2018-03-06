package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.martin.test.Value.COL_IDBASE_RESTO;
import static com.example.martin.test.Value.COL_ID_RESTO;
import static com.example.martin.test.Value.COL_LATRAD_RESTO;
import static com.example.martin.test.Value.COL_LONRAD_RESTO;
import static com.example.martin.test.Value.COL_PLATEFORME_RESTO;
import static com.example.martin.test.Value.COL_TEXT_RESTO;
import static com.example.martin.test.Value.COL_ZONE_RESTO;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.TABLE_RESTO;

/**
 * Created by martin on 20/02/18.
 */

class BaseSQLiteRestaurant extends SQLiteOpenHelper {

	private static final String CREATE_BDD="CREATE TABLE " + TABLE_RESTO + " (" +
			COL_ID_RESTO + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_LATRAD_RESTO + " REAL, "+
			COL_LONRAD_RESTO + " REAL, "+ COL_TEXT_RESTO + "  TEXT, "+COL_ZONE_RESTO+ " INTEGER, "+
			COL_PLATEFORME_RESTO+ " INTEGER, "+COL_IDBASE_RESTO+ " INTEGER);";


	public BaseSQLiteRestaurant(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL("DROP TABLE " + TABLE_RESTO);
		onCreate(db);
	}

	private void insertResto(SQLiteDatabase db, double latDeg, double lonDeg, String restoName, int zone, int plateforme) {
		double latRad=Math.toRadians(latDeg);
		double lonRad=Math.toRadians(lonDeg);
		ContentValues content = new ContentValues();
		content.put(COL_LATRAD_RESTO, latRad);
		content.put(COL_LONRAD_RESTO, lonRad);
		content.put(COL_TEXT_RESTO, restoName);
		content.put(COL_ZONE_RESTO, zone);
		content.put(COL_PLATEFORME_RESTO, plateforme);
		content.put(COL_IDBASE_RESTO, ID_RESTO_DEFAUT);
		db.insert(TABLE_RESTO, null, content);

	}

}
