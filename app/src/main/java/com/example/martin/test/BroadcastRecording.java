package com.example.martin.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import java.util.List;

import static com.google.android.gms.location.LocationResult.extractResult;
import static com.google.android.gms.location.LocationResult.hasResult;

/**
 * Created by martin on 05/02/18.
 */

public class BroadcastRecording extends BroadcastReceiver {


	public BroadcastRecording() { 	super(); }


	@Override
	public void onReceive(Context context, Intent intent) {

		if (hasResult(intent)) {
			Log.d("BroadcastRecording", "FusedLocationProviderClient");
			List<Location> listLocation = extractResult(intent).getLocations();
			if (listLocation.size() > 0) {
				BDDTemp tempBDD = new BDDTemp(context);
				tempBDD.openForWrite();
				for (Location l : listLocation) {

					if (l != null) {
						double latRad = Math.toRadians(l.getLatitude());
						double lonRad = Math.toRadians(l.getLongitude());
						long time = l.getTime();

						int precision = (int) l.getAccuracy();
						long index = tempBDD.insertTemp(time, latRad, lonRad, precision);
						Log.d("BroadcastRecording", "insertLocalisation = " + String.valueOf(index));
					}
				}
				tempBDD.close();


			}

		}

		/*
		Location l = (Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if(l!=null){
			BDDTemp tempBDD = new BDDTemp(context);
			tempBDD.openForWrite();
			Log.d("BroadcastRecording", "LocationManager");
			double latDeg = l.getLatitude();
			double lonDeg = l.getLongitude();
			long time = l.getTime();
			int precision = (int) l.getAccuracy();
			long index = tempBDD.insertTemp(time, latDeg, lonDeg, precision);
			tempBDD.close();
			Log.d("BroadcastRecording", "insertLocalisation = " + String.valueOf(index));
		}
		*/
	}
}