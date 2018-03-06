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

import static com.example.martin.test.Value.textPays;


public class ServiceInstall extends IntentService {


	public ServiceInstall() {
		super("ServiceInstall");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		//condition a deplacer dans main par la suite
		ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
			if(networkInfo!=null&&networkInfo.isConnected()){

				//zone
				if(intent.hasExtra("bddZone")) {
					if (intent.getBooleanExtra("bddZone", true)) {


						HttpURLConnection httpURLConnection = null;
						String adresseURL = "http://informatique-services-bordeaux.fr/listeVille.csv";
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
								if (words.length == 4) {
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
					}
				}

				//telechargement des restaurant du pays
				if(intent.hasExtra("pays")){
					int pays=intent.getIntExtra("pays",0);
					if(pays>=0&&pays<textPays.length) {
						String paysText = textPays[pays];
						HttpURLConnection httpURLConnection = null;
						String adresseURL = "http://informatique-services-bordeaux.fr/"+paysText+".csv";
						Log.d("Install", "resto : " + adresseURL);
						BDDRestaurant bddRestaurant = new BDDRestaurant(this);
						bddRestaurant.openForWrite();



						try {
							URL url = new URL(adresseURL);
							httpURLConnection = (HttpURLConnection) url.openConnection();
							BufferedReader is = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
							String ligne = is.readLine();
							String resto;
							float latDeg;
							float lonDeg;
							int plateforme;
							int zone;
							String[] words;
							while ((ligne = is.readLine()) != null) {

								resto = "";
								latDeg = (float) 0;
								lonDeg = (float) 0;
								plateforme=0;
								zone=0;

								words=ligne.split(";");
								if (words.length == 5) {
									resto = words[0];
									lonDeg = (float) Math.toRadians(Float.parseFloat(words[1]));
									latDeg =(float) Math.toRadians(Float.parseFloat(words[2]));
									zone=Integer.parseInt(words[3]);
									plateforme=Integer.parseInt(words[4]);
								}

								if (!resto.equals("") && latDeg != 0 && lonDeg != 0) {
									bddRestaurant.insertResto(latDeg, lonDeg, resto,zone,plateforme);
								}

							}

							is.close();
							httpURLConnection.disconnect();

						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {

							bddRestaurant.close();
						}







					}
				}






			}
		}
	}
}
