package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static com.example.martin.test.Value.COL_DUREE_LOCAL;
import static com.example.martin.test.Value.COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.COL_IND_LOCAL;
import static com.example.martin.test.Value.COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.COL_TIME_LOCAL;
import static com.example.martin.test.Value.IND_ARRET_INCONNU;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_CLIENT_CONFIRME;
import static com.example.martin.test.Value.IND_DEFAUT;
import static com.example.martin.test.Value.IND_DEPLACEMENT_INCONNU;
import static com.example.martin.test.Value.IND_DEPLACEMENT_VERS_CLIENT;
import static com.example.martin.test.Value.IND_DEPLACEMENT_VERS_RESTO;
import static com.example.martin.test.Value.IND_END;
import static com.example.martin.test.Value.IND_HYPO_CLIENT;
import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_RESTO_CONFIRME;
import static com.example.martin.test.Value.IND_START;
import static com.example.martin.test.Value.NOM_BDD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_DUREE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IND_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_TIME_LOCAL;
import static com.example.martin.test.Value.TABLE_LOCALISATIONS;
import static com.example.martin.test.Value.distence2;

/**
 * Created by martin on 02/02/18.
 */

 class BDDLocalisation {
    private static final int VERSION = 2;
    private SQLiteDatabase bdd;
    private BaseSQLiteLocalisation localisations;
	int distanceTotale=0;
	int nbCommande=0;
	int dureeDate=0;  //en ms

     BDDLocalisation(Context context) {
        localisations = new BaseSQLiteLocalisation(context, NOM_BDD_LOCAL, null, VERSION);

    }

     void openForWrite() {
        bdd = localisations.getWritableDatabase();
    }

     void openForRead() {
        bdd = localisations.getReadableDatabase();
    }

     void close() {
        bdd.close();
    }

     long insertLocalisation(Localisation localisation) {

        ContentValues content = new ContentValues();
        content.put(COL_TIME_LOCAL, localisation.getTime());
        content.put(COL_LATRAD_LOCAL, localisation.getLatitude());
        content.put(COL_LONRAD_LOCAL, localisation.getLongitude());
		content.put(COL_DUREE_LOCAL, localisation.getDuree());
		content.put(COL_IND_LOCAL,localisation.getIndication());
		if(localisation.getIndication()==IND_RESTO||localisation.getIndication()==IND_RESTO_CONFIRME||
				localisation.getIndication()==IND_HYPO_RESTO){
			content.put(COL_IDRESTO_LOCAL, localisation.getIdResto());
		}
		else {
			content.put(COL_IDRESTO_LOCAL, (int) (100*localisation.getPrecision()));
		}

        return bdd.insert(TABLE_LOCALISATIONS, null, content);
    }

     int updateLocalisation(Localisation localisation) {
        ContentValues content = new ContentValues();
        content.put(COL_TIME_LOCAL, localisation.getTime());
        content.put(COL_LATRAD_LOCAL, localisation.getLatitude());
        content.put(COL_LONRAD_LOCAL, localisation.getLongitude());
		content.put(COL_DUREE_LOCAL, localisation.getDuree());
		content.put(COL_IND_LOCAL,localisation.getIndication());
        content.put(COL_IDRESTO_LOCAL, localisation.getIdResto());
        return bdd.update(TABLE_LOCALISATIONS, content, COL_TIME_LOCAL + " = " + localisation.getTime(), null);
    }


	ArrayList<Localisation> getLastLocations(){

		ArrayList<Localisation> res= new ArrayList<>();

		Cursor c=bdd.rawQuery("SELECT * FROM "+ TABLE_LOCALISATIONS+
				" ORDER BY "+ COL_TIME_LOCAL +" DESC LIMIT '10'" ,null);


		if(c.getCount()==0) {
			c.close();
			return null;
		}
		c.moveToLast();
		do{
			Localisation l=new Localisation();
			l.setTime(c.getLong(NUM_COL_TIME_LOCAL));
			l.setLatitude(c.getFloat(NUM_COL_LATRAD_LOCAL));
			l.setLongitude(c.getFloat(NUM_COL_LONRAD_LOCAL));
			l.setIndication(c.getInt(NUM_COL_IND_LOCAL));
			l.setDuree(c.getInt(NUM_COL_DUREE_LOCAL));
			l.setIdResto(c.getInt(NUM_COL_IDRESTO_LOCAL));
			res.add(l);
		}
		while(c.moveToPrevious());

		c.close();


		return res;
	}

	Cursor getCursorFrom(long start){
        return bdd.rawQuery("SELECT * FROM " + TABLE_LOCALISATIONS
				+" WHERE " + COL_TIME_LOCAL + " >= "+start
				+" ORDER BY "+COL_TIME_LOCAL ,null);
    }

    private Cursor getCursorBetween(long start,long stop){
        return bdd.rawQuery("SELECT * FROM " + TABLE_LOCALISATIONS + " WHERE "
				+ COL_TIME_LOCAL + " BETWEEN "+start + " AND " +stop
				+ " ORDER BY "+COL_TIME_LOCAL,null);
    }

	boolean isEmptyBetween(long start,long stop){
		Cursor c=getCursorBetween(start,stop);
		boolean res=c.getCount()==0;
		c.close();
		return res;
	}

	long replaceLocalisation(Localisation l){
		ContentValues content = new ContentValues();
		content.put(COL_TIME_LOCAL, l.getTime());
		content.put(COL_LATRAD_LOCAL, l.getLatitude());
		content.put(COL_LONRAD_LOCAL, l.getLongitude());
		content.put(COL_DUREE_LOCAL, l.getDuree());
		content.put(COL_IND_LOCAL,l.getIndication());
		content.put(COL_IDRESTO_LOCAL, l.getIdResto());
		return bdd.replace(TABLE_LOCALISATIONS,null,content);
	}

	int removeLocalisation(long time){
        return bdd.delete(TABLE_LOCALISATIONS,COL_TIME_LOCAL+" = "+time,null);
    }

    void removeAll(){
        bdd.delete(TABLE_LOCALISATIONS,COL_TIME_LOCAL+" >= '0' ",null);
    }

	ArrayList<Localisation> getAllLocalisation(){
        Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_LOCALISATIONS,null);

        if(c.getCount()==0) {
            c.close();
            return null;
        }
        ArrayList<Localisation> res= new ArrayList<>();

        while(c.moveToNext()){
            Localisation l=new Localisation();
            l.setTime(c.getLong(NUM_COL_TIME_LOCAL));
            l.setLatitude(c.getFloat(NUM_COL_LATRAD_LOCAL));
            l.setLongitude(c.getFloat(NUM_COL_LONRAD_LOCAL));
			l.setIndication(c.getInt(NUM_COL_IND_LOCAL));
            l.setDuree(c.getInt(NUM_COL_DUREE_LOCAL));
            l.setIdResto(c.getInt(NUM_COL_IDRESTO_LOCAL));
            res.add(l);
        }
        c.close();
        return res;
    }

    ArrayList<Localisation> getLocalisationsInconnues(long stop){

     	long timeFirstLocatisationInconnue;
		Cursor c=bdd.rawQuery("SELECT * FROM "+TABLE_LOCALISATIONS +
				" WHERE "+ COL_IND_LOCAL+" = "+IND_DEFAUT+
				" OR "+COL_IND_LOCAL+" = "+IND_DEPLACEMENT_INCONNU+
				" OR "+COL_IND_LOCAL+" = "+IND_ARRET_INCONNU +
				" ORDER BY "+COL_TIME_LOCAL +" LIMIT '1'",null);

		if(!(c.getCount()==1)) {
			c.close();
            Log.d("BBD Localisation","timeFirstLocatisationInconnue non trouvée");
			return null;

		}
		c.moveToFirst();
		timeFirstLocatisationInconnue=c.getLong(NUM_COL_TIME_LOCAL);
		c.close();

		c=getCursorBetween(timeFirstLocatisationInconnue,stop);
		if(c.getCount()>0) {
			c.close();
            Log.d("BBD Localisation","pas de Localisation entre "+timeFirstLocatisationInconnue+" et "+stop);
			return null;
		}
		ArrayList<Localisation> res= new ArrayList<>();

		while(c.moveToNext()){
			Localisation l=new Localisation();
			l.setTime(c.getLong(NUM_COL_TIME_LOCAL));
			l.setLatitude(c.getFloat(NUM_COL_LATRAD_LOCAL));
			l.setLongitude(c.getFloat(NUM_COL_LONRAD_LOCAL));
			l.setIndication(c.getInt(NUM_COL_IND_LOCAL));
			l.setDuree(c.getInt(NUM_COL_DUREE_LOCAL));
			l.setIdResto(c.getInt(NUM_COL_IDRESTO_LOCAL));
			res.add(l);
		}
		c.close();
		return res;
	}

	ArrayList<UneLigne> getCommandeBetween(Context context, long start,long stop){

		distanceTotale=0;
		nbCommande=0;
		dureeDate=0;

		ArrayList<UneLigne> data= new ArrayList<>();
		Cursor c= getCursorBetween(start,stop);
		int nbPoint = c.getCount();
		Log.d("BBDLocalisation",String.valueOf(nbPoint)+ " points");


		long date;
		float latRad;
		float lonRad;
		int indication;
		int distance=0;
		int duree;
		int indicationPrecedante=-1;
		float latRadPrecedante=5; //latitude non initialisé car >Pi
		float lonRadPrecedante=5;
		long dateArretPrecedant=0;
		int dureeArretPrecedent=0;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			indication=c.getInt(NUM_COL_IND_LOCAL);
			latRad= c.getFloat(NUM_COL_LATRAD_LOCAL);
			lonRad= c.getFloat(NUM_COL_LONRAD_LOCAL);
			date=c.getLong(NUM_COL_TIME_LOCAL);
			duree=c.getInt(NUM_COL_DUREE_LOCAL);
			if(latRadPrecedante!=5) {
				distance += (int) (Math.sqrt(distence2(latRadPrecedante, latRad, lonRadPrecedante, lonRad)));



			}

			//deplacement
			if(indication==IND_DEPLACEMENT_INCONNU||indication==IND_DEPLACEMENT_VERS_CLIENT||indication==IND_DEPLACEMENT_VERS_RESTO){
				if(c.isFirst()){
					dateArretPrecedant= date;
				}

				if(c.isLast()) {

					data.add(
							new UneLigne(indication, dateArretPrecedant+dureeArretPrecedent,distance,(int)(date-dateArretPrecedant))
					);
				}


			}
			//arrêt
			else if(indication>=IND_ARRET_INCONNU&&indication<=IND_CLIENT_CONFIRME){

				//ajout déplacement
				if(!c.isFirst()&&distance!=0) {
					//cas particulier dans lequel il n'y pas de point en déplacement
					if(indicationPrecedante>=IND_ARRET_INCONNU) {
						switch (indicationPrecedante) {
							case IND_CLIENT:
							case IND_HYPO_CLIENT:
							case IND_CLIENT_CONFIRME:
								indicationPrecedante=IND_DEPLACEMENT_VERS_RESTO;
								break;
							case IND_HYPO_RESTO:
							case IND_RESTO:
							case IND_RESTO_CONFIRME:
								indicationPrecedante=IND_DEPLACEMENT_VERS_CLIENT;
								break;
							default:
								indicationPrecedante=IND_DEPLACEMENT_INCONNU;
						}
					}
					data.add(
							new UneLigne(indicationPrecedante, dateArretPrecedant+dureeArretPrecedent, distance, (int) (date - dateArretPrecedant-dureeArretPrecedent))
					);

				}
				//ajout arret

				int idResto=(c.getInt(NUM_COL_IDRESTO_LOCAL));

				data.add(new UneLigne(indication,date,0,duree,idResto));
				//mise a jour variable
				dateArretPrecedant=date;
				dureeArretPrecedent=duree;
				distance=0;
			}
			//fin shift
			else if(indication==IND_END) {

				//ajout déplacement
				if (!c.isFirst() && distance != 0) {
					//cas particulier dans lequel il n'y pas de point en déplacement
					if (indicationPrecedante >= IND_ARRET_INCONNU) {
						switch (indicationPrecedante) {
							case IND_CLIENT:
							case IND_HYPO_CLIENT:
							case IND_CLIENT_CONFIRME:
								indicationPrecedante = IND_DEPLACEMENT_VERS_RESTO;
								break;
							case IND_HYPO_RESTO:
							case IND_RESTO:
							case IND_RESTO_CONFIRME:
								indicationPrecedante = IND_DEPLACEMENT_VERS_CLIENT;
								break;
							default:
								indicationPrecedante = IND_DEPLACEMENT_INCONNU;
						}
					}
					data.add(
							new UneLigne(indicationPrecedante, dateArretPrecedant + dureeArretPrecedent, distance, (int) (date - dateArretPrecedant - dureeArretPrecedent))
					);

				}


				data.add(new UneLigne(indication, date, 0, duree));
				distance = 0;
			}
			else if(indication==IND_START)	{

				data.add(new UneLigne(indication,date,0,duree));
				distance=0;

				dateArretPrecedant= date;

			}

			if(indication<IND_START) {
				latRadPrecedante = latRad;
				lonRadPrecedante = lonRad;
			}

			indicationPrecedante = indication;
		}
		c.close();


		int index;
		//suppression des lignes avec seulement ind Start et ind End

		for (index=0;index<data.size()-1;index++) {
			if(data.get(index).getIndi()==IND_START&&data.get(index+1).getIndi()==IND_END){
				data.remove(index);
				data.remove(index);
				index--;
			}
		}



//boucle pour trouver le nom des resto
		BDDRestaurant bddRestaurant=new BDDRestaurant(context);
		bddRestaurant.openForRead();
		int lastStart=0;
		index=0;
		dureeDate=0;

		for (UneLigne l:data) {
			distanceTotale=distanceTotale+l.getDistance();
			if(l.getIdResto()!=-1){
				l.setNomResto(bddRestaurant.getTextRestaurant(l.getIdResto()));
				nbCommande++;
			}
			if(l.getIndi()==IND_START){
				lastStart=index;
			}
			else if(l.getIndi()==IND_END||index== data.size()-1){
				dureeDate+=(int) (l.getDate()-data.get(lastStart).getDate());
			}
			index++;
		}
		bddRestaurant.close();

		for (UneLigne l:data) {
			Log.d("BBD Localisation","get Cmd: "+l.toString(context));
		}


		return data;
	}

}
