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
import static com.example.martin.test.Value.COL_LATITUDE_RESTO;
import static com.example.martin.test.Value.COL_LONGITUDE_RESTO;
import static com.example.martin.test.Value.COL_PLATEFORME_RESTO;
import static com.example.martin.test.Value.COL_TEXT_RESTO;
import static com.example.martin.test.Value.COL_ZONE_RESTO;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.NOM_BDD_RESTO;
import static com.example.martin.test.Value.NUM_COL_ID_RESTO;
import static com.example.martin.test.Value.NUM_COL_LATITUDE_RESTO;
import static com.example.martin.test.Value.NUM_COL_LONGITUDE_RESTO;
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


	private static final int VERSION = 4;
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

	long insertResto(double latDegres,double lonDegres,String restoName,int zone,int plateforme) {
		latDegres=Math.toRadians(latDegres);
		lonDegres=Math.toRadians(lonDegres);

		ContentValues content = new ContentValues();

		content.put(COL_LATITUDE_RESTO, latDegres);
		content.put(COL_LONGITUDE_RESTO, lonDegres);
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
			res=c.getString(NUM_COL_TEXT_RESTO);
		}
		c.close();
		close();
		return res;
	}

	int getIdResto(double lat1,double lon1,int zone,int plateforme){

		openForRead();
		rayonPetitCercle = (int) (RAYONTERRE*Math.cos(lat1));
		int res=-1;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_RESTO+" WHERE "+COL_ZONE_RESTO + " = " + zone + " AND "+COL_PLATEFORME_RESTO + " = " + plateforme,null);

		if (c.getCount()>0){
			double lat2;
			double lon2;
			int index=0;
			int minDistence2=2147483646;  //valeur max int soit plusieurs fois le rayon de la terre
			int d2=0;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				lat2=c.getDouble(NUM_COL_LATITUDE_RESTO);
				lon2=c.getDouble(NUM_COL_LONGITUDE_RESTO);
				d2=distence2(lat1,lat2,lon1,lon2);
				if (d2<minDistence2) {
					index=c.getPosition();
					minDistence2=d2;
				}
			}
			if(minDistence2<SEUILRESTO*SEUILRESTO){
				c.moveToPosition(index);
				res=c.getInt(NUM_COL_ID_RESTO);
			}
			else{
				Log.d("resto","pas de resto connu");
			}
		}
		else{
			Log.d("resto","table resto vide");

		}
		c.close();
		close();
		Log.d("resto","res : "+res);
		return res;
	}

	boolean bddHasResto(double latDegres,double lonDegres,int zone,int plateforme){
		if (getIdResto(Math.toRadians(latDegres),Math.toRadians(lonDegres),zone,plateforme)==-1) return false;
		else return true;
	}

	void selectResto(double lat1,double lon1,int zone,int plateforme){
		lat1=Math.toRadians(lat1);
		lon1=Math.toRadians(lon1);

		List<Integer> idResto=new ArrayList<Integer>();
		List<String> nameResto=new ArrayList<String>();


		rayonPetitCercle = (int) (RAYONTERRE*Math.cos(lat1));

		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_RESTO+" WHERE "+COL_ZONE_RESTO + " = " + zone + " AND "+COL_PLATEFORME_RESTO + " = " + plateforme,null);

		if (c.getCount()>0){
			double lat2;
			double lon2;

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				lat2=c.getDouble(NUM_COL_LATITUDE_RESTO);
				lon2=c.getDouble(NUM_COL_LONGITUDE_RESTO);

				if (distence2(lat1,lat2,lon1,lon2)<SEUILSELECTRESTO*SEUILSELECTRESTO) {

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


 }
