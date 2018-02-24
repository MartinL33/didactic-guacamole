package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.example.martin.test.Value.COL_IND_ACTION;
import static com.example.martin.test.Value.COL_TIME_ACTION;
import static com.example.martin.test.Value.IND_PLATEFORME_1;
import static com.example.martin.test.Value.NOM_BDD_ACTION;
import static com.example.martin.test.Value.NUM_COL_IND_ACTION;
import static com.example.martin.test.Value.TABLE_ACTIONS;

/**
 * Created by martin on 13/02/18.
 */

class BDDAction {

	private static final int VERSION = 1;
	private SQLiteDatabase bdd;
	private BaseSQLiteAction actions;


	BDDAction(Context context) {
		actions = new BaseSQLiteAction(context, NOM_BDD_ACTION, null, VERSION);

	}

	void openForWrite() {
		bdd = actions.getWritableDatabase();
	}

	void openForRead() {
		bdd = actions.getReadableDatabase();
	}

	void close() {
		bdd.close();
	}

	long insertAction(long time,int indication) {

		ContentValues content = new ContentValues();
		content.put(COL_TIME_ACTION, time);
		content.put(COL_IND_ACTION, indication);
		return bdd.insert(TABLE_ACTIONS, null, content);
	}

	int updateAction(long time,int indication) {
		ContentValues content = new ContentValues();
		content.put(COL_TIME_ACTION, time);
		content.put(COL_IND_ACTION, indication);
		return bdd.update(TABLE_ACTIONS, content, COL_TIME_ACTION + " = " + time, null);
	}

	int removeAction(long time){
		return bdd.delete(TABLE_ACTIONS,COL_TIME_ACTION+" = "+time,null);
	}

	int getLastPlateforme(){

		int result;
		bdd = actions.getReadableDatabase();
		Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_ACTIONS +" WHERE "+ COL_IND_ACTION+ " >= "+IND_PLATEFORME_1+" ORDER BY "+ COL_TIME_ACTION +" DESC LIMIT 1",null);

		if (c.getCount()==0) {
			result= -1;
		}

		else {
			c.moveToFirst();
			result= c.getInt(NUM_COL_IND_ACTION);
		}
		c.close();
		bdd.close();
		return result;
	}

	Cursor getCursorFrom(int start){
		return bdd.rawQuery("SELECT * FROM " + TABLE_ACTIONS + " WHERE " + COL_TIME_ACTION + " >= "+ start ,null);
	}
}
