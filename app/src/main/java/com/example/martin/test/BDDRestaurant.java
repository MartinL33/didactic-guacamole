package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.martin.test.Value.COL_IDBASE_RESTO;
import static com.example.martin.test.Value.COL_ID_RESTO;
import static com.example.martin.test.Value.COL_LATRAD_RESTO;
import static com.example.martin.test.Value.COL_LONRAD_RESTO;
import static com.example.martin.test.Value.COL_PLATEFORME_RESTO;
import static com.example.martin.test.Value.COL_TEXT_RESTO;
import static com.example.martin.test.Value.COL_ZONE_RESTO;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.NOM_BDD_RESTO;
import static com.example.martin.test.Value.NUM_COL_ID_RESTO;
import static com.example.martin.test.Value.NUM_COL_LATRAD_RESTO;
import static com.example.martin.test.Value.NUM_COL_LONRAD_RESTO;
import static com.example.martin.test.Value.NUM_COL_TEXT_RESTO;
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.SEUILRESTO;
import static com.example.martin.test.Value.SEUILSELECTRESTO;
import static com.example.martin.test.Value.TABLE_RESTO;
import static com.example.martin.test.Value.distence2;
import static com.example.martin.test.Value.rayonPetitCercle;


/**
 * Created by martin on 20/02/18.
 */

 class BDDRestaurant {


	private static final int VERSION = 2;
	private SQLiteDatabase bdd;
	private BaseSQLiteRestaurant restos;
	Integer[] idRestoSelect;
	String[] nameRestoSelect;


	BDDRestaurant(Context context) {
		restos = new BaseSQLiteRestaurant(context, NOM_BDD_RESTO, null, VERSION);

	}

	void openForWrite() {
		bdd = restos.getWritableDatabase();
	}

	void openForRead() {
		bdd = restos.getReadableDatabase();
	}

	void close() {
		bdd.close();
	}

	void removeAllResto(){
		bdd.execSQL("DELETE FROM "+TABLE_RESTO);
	}

	long insertResto(float latRad,float lonRad,String restoName,int zone,int plateforme) {

		ContentValues content = new ContentValues();

		content.put(COL_LATRAD_RESTO, latRad);
		content.put(COL_LONRAD_RESTO, lonRad);
		content.put(COL_TEXT_RESTO, restoName);
		 content.put(COL_ZONE_RESTO, zone);
		 content.put(COL_PLATEFORME_RESTO, plateforme);
		 content.put(COL_IDBASE_RESTO, ID_RESTO_DEFAUT);
		return bdd.insert(TABLE_RESTO, null, content);
	}

	/**
	 *
	 * @param id identifiant
	 * @return une chaine de caractère correspondant au restaurant indiquée
	 */
	String getTextRestaurant(int id){
		String res="";
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_RESTO+" WHERE "+COL_ID_RESTO + " = " + id,null);
		if (c.getCount()==1){
			c.moveToFirst();
			res=c.getString(NUM_COL_TEXT_RESTO);
		}
		c.close();
		return res;
	}

	int getIdResto(double latRad1,double lonRad1,int zone,int plateforme){


		rayonPetitCercle = (int) (RAYONTERRE*Math.cos(latRad1));
		int res=-1;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_RESTO+" WHERE "+COL_ZONE_RESTO + " = " + zone + " AND "+COL_PLATEFORME_RESTO + " = " + plateforme,null);

		if (c.getCount()>0){
			double latRad2;
			double lonRad2;
			int index=0;
			int minDistence2=2147483646;  //valeur max int soit plusieurs fois le rayon de la terre
			int d2;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				latRad2=c.getDouble(NUM_COL_LATRAD_RESTO);
				lonRad2=c.getDouble(NUM_COL_LONRAD_RESTO);
				d2=distence2(latRad1,latRad2,lonRad1,lonRad2);
				if (d2<minDistence2) {
					index=c.getPosition();
					minDistence2=d2;
				}
			}
			if(minDistence2<SEUILRESTO*SEUILRESTO){
				c.moveToPosition(index);
				res=c.getInt(NUM_COL_ID_RESTO);
				Log.d("resto","idResto : "+res);
			}
			else{
				Log.d("resto","pas de resto connu");
			}
		}
		else{
			Log.d("resto","table resto vide");

		}
		c.close();


		return res;
	}

	boolean bddHasResto(double latRad,double lonRad,int zone,int plateforme){
		return getIdResto(latRad, lonRad, zone, plateforme) != -1;
	}

	void selectResto(double latRad1,double lonRad1,int zone,int plateforme){

		List<Integer> idResto= new ArrayList<>();
		List<String> nameResto= new ArrayList<>();


		rayonPetitCercle = (int) (RAYONTERRE*Math.cos(latRad1));

		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_RESTO+" WHERE "+COL_ZONE_RESTO + " = " + zone + " AND "+COL_PLATEFORME_RESTO + " = " + plateforme,null);

		if (c.getCount()>0){
			double latRad2;
			double lonRad2;

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				latRad2=c.getDouble(NUM_COL_LATRAD_RESTO);
				lonRad2=c.getDouble(NUM_COL_LONRAD_RESTO);

				if (distence2(latRad1,latRad2,lonRad1,lonRad2)<SEUILSELECTRESTO*SEUILSELECTRESTO) {

					idResto.add(c.getInt(NUM_COL_ID_RESTO));
					nameResto.add(c.getString(NUM_COL_TEXT_RESTO));
					Log.d("resto",c.getString(NUM_COL_TEXT_RESTO));
				}
			}
		}
		else{
			Log.d("resto","table resto vide");

		}
		c.close();
		if( idResto.isEmpty()) Log.d("activity","erreur : bddRestaurant.idResto.isEmpty");
		if( nameResto.isEmpty()) Log.d("activity","erreur : bddRestaurant.nameResto.isEmpty");
		Integer[] para={0};
		idRestoSelect=idResto.toArray(para);

		String [] parametre={" "};
		nameRestoSelect= nameResto.toArray(parametre);


	}

	Cursor getCursorFrom(int start){
		return bdd.rawQuery("SELECT * FROM " + TABLE_RESTO ,null);
	}

	Boolean isEmpty(){
		Boolean result=true;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_RESTO,null);
		if(c.getCount()>0) result=false;
		c.close();
		return result;
	}

 }
