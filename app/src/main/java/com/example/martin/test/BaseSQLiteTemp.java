package com.example.martin.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.martin.test.Value.COL_LATDEG_TEMP;
import static com.example.martin.test.Value.COL_LONDEG_TEMP;
import static com.example.martin.test.Value.COL_PRECISION_TEMP;
import static com.example.martin.test.Value.COL_TIME_TEMP;
import static com.example.martin.test.Value.TABLE_TEMP;

/**
 * Created by martin on 13/02/18.
 */

class BaseSQLiteTemp extends SQLiteOpenHelper {

	private static final String CREATE_BDD="CREATE TABLE " + TABLE_TEMP + " (" +
			COL_TIME_TEMP + "  INTEGER PRIMARY KEY, " + COL_LATDEG_TEMP + " REAL, "+COL_LONDEG_TEMP
			+ " REAL, "+ COL_PRECISION_TEMP + "  INTEGER);";

	public BaseSQLiteTemp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL("DROP TABLE " + TABLE_TEMP);
		onCreate(db);
	}
}
