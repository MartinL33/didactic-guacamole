package com.example.martin.test;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ServiceInstallZone extends IntentService {



	public ServiceInstallZone() {
		super("ServiceInstall");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		//condition a deplacer dans main par la suite
		ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
			if(networkInfo!=null&&networkInfo.isConnected()){


				HttpURLConnection httpURLConnection = null;
				String adresseURL = "http://informatique-services-bordeaux.fr/ville.csv";
				Log.d("Install", "adresse : " + adresseURL);
				BDDZone bddZone = new BDDZone(this);
				bddZone.openForWrite();


				try {
					URL url = new URL(adresseURL);
					httpURLConnection = (HttpURLConnection) url.openConnection();
					BufferedReader is = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
					String ligne = is.readLine();
					String ville;
					Float latDeg;
					Float lonDeg;
					int pays;
					String[] words;
					while ((ligne = is.readLine()) != null) {

						ville = "";
						latDeg = (float) 0;
						lonDeg = (float) 0;
						pays=-1;
						words=ligne.split(";");
						if (words.length >= 4) {
							pays=Integer.parseInt(words[0]);
							ville = words[1];
							lonDeg = Float.parseFloat(words[2]);
							latDeg = Float.parseFloat(words[3]);
						}

						if (!ville.equals("") && latDeg != 0 && lonDeg != 0) {
							bddZone.insertZone(latDeg, lonDeg, ville,pays);
						}
					}
					is.close();
					httpURLConnection.disconnect();

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {

					bddZone.close();
				}
				Intent broadcastIntent = new Intent();

				broadcastIntent.setAction(ActivityMain.MyReceiver.ACTION_RESP);
				broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
				sendBroadcast(broadcastIntent);

			}
		}


	}
}
