package com.example.martin.test;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.martin.test.Value.NUM_COL_DUREE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IND_ACTION;
import static com.example.martin.test.Value.NUM_COL_IND_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_TIME_ACTION;
import static com.example.martin.test.Value.NUM_COL_TIME_LOCAL;
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.rayonPetitCercle;


public class ServiceExport extends IntentService {


	public ServiceExport() {
		super("ServiceExport");
	}


	@Override
	protected void onHandleIntent(Intent intent) {


		//variable
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy-hh-mm");
		String currentDate = sdf1.format(new Date());
		File fileResult;
		String mess;
		long[] t;
		float[] lat;
		float[] lon;
		int i;




		//base localisation
		BDDLocalisation localisationBDD = new BDDLocalisation(ServiceExport.this);
		localisationBDD.openForRead();

		Cursor c = localisationBDD.getCursorFrom(1);

		int nbPoint = c.getCount();

		Log.d("ServiceExport", "nbPoint =" + String.valueOf(nbPoint));

		if (nbPoint > 0) {
			t = new long[nbPoint];
			lat = new float[nbPoint];
			lon = new float[nbPoint];
			int[] ind = new int[nbPoint];
			int[] d = new int[nbPoint];
			int[] idResto = new int[nbPoint];
			i = 0;

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				t[i] = c.getLong(NUM_COL_TIME_LOCAL);
				lat[i] = (float) c.getDouble(NUM_COL_LATRAD_LOCAL);
				lon[i] = (float) c.getDouble(NUM_COL_LONRAD_LOCAL);
				d[i] = c.getInt(NUM_COL_DUREE_LOCAL);
				ind[i]=c.getInt(NUM_COL_IND_LOCAL);
				idResto[i] = c.getInt(NUM_COL_IDRESTO_LOCAL);
				i++;
			}
			c.close();
			localisationBDD.close();


//si le dossier test n'existe pas, on le crée
			File fileD = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name));
			if (!fileD.exists()) {
				if (!fileD.mkdirs()) throw new AssertionError();
			}

			//ecriture fichier résultat

			fileResult = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name) + "/tableLocalisation-" + currentDate + ".csv");
			try {
				if(!fileResult.exists()) {

					if (!fileResult.createNewFile()) throw new AssertionError();
				}

				FileOutputStream output = new FileOutputStream(fileResult, false);
				mess = "time;latitude;longitude;durée attente;indication;idResto;x; y\n";
				output.write(mess.getBytes());

				for (i = 0; i < nbPoint; i++) {
					float x =  (RAYONTERRE * (lat[i] - lat[0]));
					float y =  (rayonPetitCercle * (lon[i] - lon[0]));
					mess = String.valueOf(t[i]) + ";" + String.valueOf(Math.toDegrees(lat[i])) + ";" + String.valueOf(Math.toDegrees(lon[i])) + ";" + String.valueOf(d[i]) + ";" + String.valueOf(ind[i])+ ";" + String.valueOf(idResto[i]) + ";" + String.valueOf(x) + ";" + String.valueOf(y) + "\n";
					output.write(mess.getBytes());

				}
				output.close();


			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//base action

		}
		BDDAction bddAction = new BDDAction(ServiceExport.this);
		bddAction.openForRead();
		c = bddAction.getCursorFrom(1);
		nbPoint = c.getCount();

		Log.d("ServiceExport", "nbPointAction =" + String.valueOf(nbPoint));

		if (nbPoint > 0) {
			t = new long[nbPoint];
			int[] in = new int[nbPoint];
			i = 0;

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				t[i] = c.getLong(NUM_COL_TIME_ACTION);
				in[i] = c.getInt(NUM_COL_IND_ACTION);

				i++;
			}
			c.close();
			bddAction.close();


			fileResult = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name) + "/tableActions-" + currentDate + ".csv");

			try {
				if(!fileResult.exists()) {
					if (!fileResult.createNewFile()) throw new AssertionError();
				}
				FileOutputStream output = new FileOutputStream(fileResult, false);
				mess = "time;indication \n";
				output.write(mess.getBytes());

				for (i = 0; i < nbPoint; i++) {

					mess = String.valueOf(t[i]) + ";" + String.valueOf(in[i]) + "\n";
					output.write(mess.getBytes());

				}
				output.close();


			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}


		}
/*

//base temp

		BDDTemp tempBDD = new BDDTemp(ServiceExport.this);
		tempBDD.openForRead();
		c = tempBDD.getCursor();
		nbPoint = c.getCount();

		Log.d("ServiceExport", "nbPointTemp =" + String.valueOf(nbPoint));

		if (nbPoint > 0) {
			t = new long[nbPoint];
			int[] p = new int[nbPoint];
			lat = new float[nbPoint];
			lon = new float[nbPoint];
			i = 0;

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				t[i] = c.getLong(NUM_COL_TIME_TEMP);
				lat[i] = (float) c.getDouble(NUM_COL_LATDEG_TEMP);
				lon[i] = (float) c.getDouble(NUM_COL_LONDEG_TEMP);
				p[i] = c.getInt(NUM_COL_PRECISION_TEMP);

				i++;
			}
			c.close();
			bddAction.close();

				//changement coordonnées

			x = new float[nbPoint];
			y = new float[nbPoint];
			origineLatitude = Math.toRadians(lat[0]);
			origineLongitude = Math.toRadians(lon[0]);



			x[0] = 0;
			y[0] = 0;

			for (i = 1; i < nbPoint; i++) {
				x[i] = (float) (RAYONTERRE * (Math.toRadians(lat[i]) - origineLatitude));
				y[i] = (float) (rayonPetitCercle * (Math.toRadians(lon[i]) - origineLongitude));
			}






			fileResult = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name) + "/tableTemp-" + currentDate + ".csv");

			try {
				if(!fileResult.exists()) {
					if (!fileResult.createNewFile()) throw new AssertionError();
				}
				FileOutputStream output = new FileOutputStream(fileResult, false);
				mess = "time;latitude;longitude;precision;x;y \n";
				output.write(mess.getBytes());

				for (i = 0; i < nbPoint; i++) {

					mess = String.valueOf(t[i]) + ";" + String.valueOf(lat[i]) + ";" + String.valueOf(lon[i]) + ";" + String.valueOf(p[i]) + ";" + String.valueOf(x[i]) + ";" + String.valueOf(y[i]) + "\n";
					output.write(mess.getBytes());

				}
				output.close();


			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}


		}

*/

		stopSelf();
	}
}
