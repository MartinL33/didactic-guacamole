package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.example.martin.test.Value.COL_ID_ACTION;
import static com.example.martin.test.Value.COL_IND_ACTION;
import static com.example.martin.test.Value.COL_TIME_ACTION;
import static com.example.martin.test.Value.IND_PLATEFORME;
import static com.example.martin.test.Value.IND_START;
import static com.example.martin.test.Value.NOM_BDD_ACTION;
import static com.example.martin.test.Value.NUM_COL_ID_ACTION;
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

		return insertAction(time,indication,-1);
	}

	long insertAction(long time,int indication,int idResto) {

		ContentValues content = new ContentValues();
		content.put(COL_TIME_ACTION, time);
		content.put(COL_IND_ACTION, indication);
		content.put(COL_ID_ACTION, idResto);
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

		int result=-1;

		Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_ACTIONS +" WHERE "+ COL_IND_ACTION+ " >= "+IND_PLATEFORME[0]+" ORDER BY "+ COL_TIME_ACTION +" DESC LIMIT 1",null);

		if (c.getCount()==1) {
			c.moveToFirst();
			result= c.getInt(NUM_COL_IND_ACTION)-IND_PLATEFORME[0];
		}

		c.close();
		return result;
	}

	int getIndicationBeetween(long start,long stop){
		int result=-1;
		Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_ACTIONS +" WHERE "+ COL_IND_ACTION+ " < "+IND_START+ " AND "+COL_TIME_ACTION+ " BETWEEN "+start +" AND "+ stop +" ORDER BY "+COL_TIME_ACTION+" DESC LIMIT 1",null);
		if (c.getCount()==1) {
			c.moveToFirst();
			result = c.getInt(NUM_COL_IND_ACTION);
		}
		c.close();
		return result;
	}

	int getIdRestoBeetween(long start,long stop){
		int result=-1;
		Cursor c = bdd.rawQuery("SELECT * FROM "+ TABLE_ACTIONS +" WHERE "+ COL_IND_ACTION+ " < "+IND_START+ " AND "+COL_TIME_ACTION+ " BETWEEN "+start +" AND "+ stop +" ORDER BY "+COL_TIME_ACTION+" DESC LIMIT 1",null);
		if (c.getCount()==1) {
			c.moveToFirst();
			result = c.getInt(NUM_COL_ID_ACTION);
		}
		c.close();
		return result;
	}

}
