package com.example.martin.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.martin.test.Value.COL_DUREE_LOCAL;
import static com.example.martin.test.Value.COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.COL_IND_LOCAL;
import static com.example.martin.test.Value.COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.COL_TIME_LOCAL;
import static com.example.martin.test.Value.TABLE_LOCALISATIONS;

/**
 * Created by martin on 02/02/18.
 */

public class BaseSQLiteLocalisation extends SQLiteOpenHelper {

   	private static final String CREATE_BDD="CREATE TABLE " + TABLE_LOCALISATIONS + " (" +
            COL_TIME_LOCAL + "  INTEGER PRIMARY KEY, " +
            COL_LATRAD_LOCAL + " REAL, "+COL_LONRAD_LOCAL + " REAL, "+ COL_DUREE_LOCAL +
            "  INTEGER,"+ COL_IND_LOCAL+ " INTEGER, "+ COL_IDRESTO_LOCAL + "  INTEGER);";

    public BaseSQLiteLocalisation(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + TABLE_LOCALISATIONS);
        onCreate(db);
    }

}


