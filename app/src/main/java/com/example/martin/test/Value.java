package com.example.martin.test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by martin on 04/02/18.
 */

 class Value {


	final static int IND_DEFAUT=1;
	final static int IND_DEPLACEMENT_INCONNU=2;
	final static int IND_DEPLACEMENT_VERS_RESTO=3;
	final static int IND_DEPLACEMENT_VERS_CLIENT=4;
    final static int IND_RESTO=5;
    final static int IND_CLIENT=6;
    final static int IND_ATTENTE=7;
	final static int IND_HYPO_RESTO=8;
	final static int IND_HYPO_CLIENT=9;
    final static int IND_RESTO_CONFIRME=10;
    final static int IND_CLIENT_CONFIRME=11;
    final static int IND_ATTENTE_CONFIRME=12;


	final static int IND_START=13;
	final static int IND_END=14;

    final static int[] IND_PLATEFORME={15,16,17,18,19};

    final static int IND_PLATEFORME_1=11;
    final static int IND_PLATEFORME_2=12;
    final static int IND_PLATEFORME_3=13;
    final static int IND_PLATEFORME_4=14;
	final static int IND_PLATEFORME_5=15;



	final static int DUREE_DEFAUT=0;
    final static int ID_RESTO_DEFAUT=-1;

//base de donnee localisation
    static final String NOM_BDD_LOCAL = "localisations.db";
    static final String TABLE_LOCALISATIONS = "table_localisations";

    static final String COL_TIME_LOCAL = "TIME";
    static final int NUM_COL_TIME_LOCAL = 0;
    static final String COL_LATRAD_LOCAL = "LATITUDE";
    static final int NUM_COL_LATRAD_LOCAL = 1;
    static final String COL_LONRAD_LOCAL = "LONGITUDE";
    static final int NUM_COL_LONRAD_LOCAL = 2;
    static final String COL_DUREE_LOCAL="DUREE";
    static final int NUM_COL_DUREE_LOCAL = 3;
	static final String COL_IND_LOCAL="IND";
	static final int NUM_COL_IND_LOCAL = 4;
   	static final String COL_IDRESTO_LOCAL = "IDRESTO";
  	static final int NUM_COL_IDRESTO_LOCAL = 5;

  	//base de donnee action
	static final String NOM_BDD_ACTION = "actions.db";
	static final String TABLE_ACTIONS = "table_actions";
	static final String COL_TIME_ACTION = "TIME";
	static final int NUM_COL_TIME_ACTION = 0;
	static final String COL_IND_ACTION = "INDICATION";
	static final int NUM_COL_IND_ACTION = 1;

//base de donnee temp
	static final String NOM_BDD_TEMP = "temp.db";
	static final String TABLE_TEMP = "table_temp";
	static final String COL_TIME_TEMP = "TIME";
	static final int NUM_COL_TIME_TEMP = 0;
	static final String COL_LATDEG_TEMP = "LATITUDE";
	static final int NUM_COL_LATDEG_TEMP = 1;
	static final String COL_LONDEG_TEMP = "LONGITUDE";
	static final int NUM_COL_LONDEG_TEMP = 2;
	static final String COL_PRECISION_TEMP="PRECISION";
	static final int NUM_COL_PRECISION_TEMP = 3;

//base de donnee zone
	static final String NOM_BDD_ZONE = "zone.db";
	static final String TABLE_ZONE = "table_zone";
	static final String COL_ID_ZONE = "ID";
	static final int NUM_COL_ID_ZONE = 0;
	static final String COL_LATRAD_ZONE = "LATITUDE";
	static final int NUM_COL_LATRAD_ZONE = 1;
	static final String COL_LONRAD_ZONE = "LONGITUDE";
	static final int NUM_COL_LONRAD_ZONE = 2;
	static final String COL_TEXT_ZONE = "TEXT";
	static final int NUM_COL_TEXT_ZONE = 3;

	//base de donne restaurant

	static final String NOM_BDD_RESTO = "resto.db";
	static final String TABLE_RESTO = "table_resto";
	static final String COL_ID_RESTO = "ID";
	static final int NUM_COL_ID_RESTO = 0;
	static final String COL_LATRAD_RESTO = "LATITUDE";
	static final int NUM_COL_LATRAD_RESTO = 1;
	static final String COL_LONRAD_RESTO = "LONGITUDE";
	static final int NUM_COL_LONRAD_RESTO = 2;
	static final String COL_TEXT_RESTO = "TEXT";
	static final int NUM_COL_TEXT_RESTO = 3;
	static final String COL_ZONE_RESTO = "ZONE";
	static final int NUM_COL_ZONE_RESTO = 4;
	static final String COL_PLATEFORME_RESTO = "PLATEFORME";
	static final int NUM_COL_PLATEFORME_RESTO = 5;
	static final String COL_IDBASE_RESTO = "IDBASE";
	static final int NUM_COL_IDBASE_RESTO = 6;

	//constante

   static final int MIN_TIME_UPDATE_LOCATION = 1000;   //en ms
   static final int MIN_DISTANCE_UPDATE_LOCATION = 2;  //en m
    // suppression oncreate ondestroy
    static final int DUREE_MAX_INTERRUPTION = 100000;    //en ms

    //si le pas si supérieur, on considere que le shift 'est arreté puis redemarré
    static final int DUREE_MIN_FIN = 600000;    //en ms soit 10min

   static final int MIN_DISTANCE_MOYENNE = 5;     //en m
    static final int MIN_DISTANCE_MOYENNE2 = 10;     //en m

    static final int MAX_DISTANCE_DOUGLAS = 30;   //en m

    // en fait on garde tous les points pour lesquels on est resté DUREE_MIN_SAUVEGARDE_PAS à la même postion
    // (à MIN_DISTANCE_MOYENNE2 metre près)
    static final int DUREE_MIN_SAUVEGARDE_PAS = 20000; //en ms


   /*point aberant:
     si la distance entre un point et le point suivant est sup à MAX_DISTANCE_ABERANT
     et que la distance entre le premier point
    et le 2e point suivant est inférieur à MIN_DISTANCE_ABERANT on le supprime
    */
    static final int MAX_DISTANCE_ABERANT= 200;  //en m
    static final int MIN_DISTANCE_ABERANT=100;  //en m



	static final int SEUILZONE=10000; //en m
	static final int SEUILRESTO =10; //en m
	static final int SEUILSELECTRESTO =20; //en m



	static final int RAYONTERRE = 6378137; //en m
	static int rayonPetitCercle=0;			//en m

	/**
	 *
	 * @param latRad1
	 * @param latRad2
	 * @param lonRad1
	 * @param lonRad2
	 * @return la distance au carré en m2 entre la position 1 et la position 2
	 */
	static int distence2(double latRad1,double latRad2,double lonRad1,double lonRad2){
		if (rayonPetitCercle==0) rayonPetitCercle = (int) (RAYONTERRE * Math.cos(latRad1));
		return (int) ((RAYONTERRE * (latRad2 - latRad1)) *(RAYONTERRE *(latRad2 - latRad1) ) +
				(rayonPetitCercle *(lonRad2 - lonRad1)) *( (lonRad2 - lonRad1) * rayonPetitCercle));
	}
	static boolean verifPermissionLocation(Context context) {
		boolean res = false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
					context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				res = true;
			}
		} else {
			//si la version est inférieur à 6, pas besoin de vérifier les permissions
			res = true;
		}
		return res;
	}
	// test git

}
