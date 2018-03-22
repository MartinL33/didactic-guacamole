package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.martin.test.Value.COL_IND_ACTION;
import static com.example.martin.test.Value.COL_IND_ID_ACTION;
import static com.example.martin.test.Value.COL_TIME_ACTION;
import static com.example.martin.test.Value.IND_PLATEFORME;
import static com.example.martin.test.Value.TABLE_ACTIONS;


/**
 * Created by martin on 13/02/18.
 */

class BaseSQLiteAction extends SQLiteOpenHelper {
	private static final String CREATE_BDD="CREATE TABLE " + TABLE_ACTIONS + " (" +
			COL_TIME_ACTION + "  INTEGER PRIMARY KEY, " +
			COL_IND_ACTION + " INTEGER, " +
			COL_IND_ID_ACTION + " INTEGER);";


	public BaseSQLiteAction(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
		ContentValues content = new ContentValues();
		//a supprimer : plateforme Delivroo
		content.put(COL_TIME_ACTION, 10);
		content.put(COL_IND_ACTION, IND_PLATEFORME[0]);
		db.insert(TABLE_ACTIONS, null, content);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL("DROP TABLE " + TABLE_ACTIONS);
		onCreate(db);
	}
}
