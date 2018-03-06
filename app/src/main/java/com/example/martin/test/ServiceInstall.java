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

				HttpURLConnection httpURLConnection=null;
				String adresseURL="http://informatique-services-bordeaux.fr/listeVille.csv";
				Log.d("Install","adresse : "+adresseURL);
				BDDZone bddZone = new BDDZone(this);
				bddZone.openForWrite();


				try{
					URL url=new URL(adresseURL);
					httpURLConnection=(HttpURLConnection) url.openConnection();
					BufferedReader is= new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
					String ligne=is.readLine();
					String ville;
					Float latDeg;
					Float lonDeg;

					while((ligne=is.readLine())!=null){

						ville="";
						latDeg=(float) 0;
						lonDeg=(float) 0;
						if(ligne.split(";").length==3){
							ville=ligne.split(";")[0];
							lonDeg= Float.parseFloat(ligne.split(";")[1]);
							latDeg= Float.parseFloat(ligne.split(";")[2]);

						}

						if(!ville.equals("")&&latDeg!=0&&lonDeg!=0){
							bddZone.insertZone(latDeg,lonDeg,ville);
						}

					}

					is.close();
					httpURLConnection.disconnect();

				}
				catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {

					bddZone.close();
				}


				//restaurant









			}
		}
	}
}
