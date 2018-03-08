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

public class ServiceInstallRestaurant extends IntentService {


	public ServiceInstallRestaurant() {
		super("ServiceInstallRestaurant");
	}

	@Override
	protected void onHandleIntent(Intent intent) {


		//condition a deplacer dans main par la suite
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {


				//telechargement des restaurant du pays
				if (intent.hasExtra("pays")) {
					int pays = intent.getIntExtra("pays", 0);
					if (pays >= 0 && pays < textPays.length) {
						String paysText = textPays[pays];
						HttpURLConnection httpURLConnection = null;
						String adresseURL = "http://informatique-services-bordeaux.fr/" + paysText + ".csv";
						Log.d("Install", "resto : " + adresseURL);
						BDDRestaurant bddRestaurant = new BDDRestaurant(this);
						bddRestaurant.openForWrite();


						try {
							URL url = new URL(adresseURL);
							httpURLConnection = (HttpURLConnection) url.openConnection();
							BufferedReader is = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
							String ligne = is.readLine();
							String resto;
							float latRad;
							float lonRad;
							int plateforme;
							int zone;
							int idWeb;
							String[] words;
							while ((ligne = is.readLine()) != null) {

								resto = "";
								latRad = (float) 0;
								lonRad = (float) 0;
								plateforme = 0;
								zone = 0;
								idWeb=0;
								words = ligne.split(";");
								if (words.length >= 6) {
									zone = Integer.parseInt(words[0]);
									resto = words[1];
									latRad = (float) Math.toRadians(Float.parseFloat(words[2]));
									lonRad = (float) Math.toRadians(Float.parseFloat(words[3]));

									idWeb= Integer.parseInt(words[4]);
									plateforme = Integer.parseInt(words[5]);
								}

								if (!resto.equals("") && latRad != 0 && lonRad != 0) {
									bddRestaurant.insertResto(latRad, lonRad, resto, zone, plateforme,idWeb);
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
		Log.d("InstallResto", "fin install resto");
	}
}