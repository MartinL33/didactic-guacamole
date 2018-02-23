package com.example.martin.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by martin on 05/02/18.
 */

public class BroadcastRecording extends BroadcastReceiver {


    public BroadcastRecording() { super();}


    @Override
    public void onReceive(Context context, Intent intent) {



        Location position = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);


        if (position != null) {
            double latitude = position.getLatitude();
            double longitude = position.getLongitude();
            long time = position.getTime();
			int precision= (int) (100*position.getAccuracy());

            BDDTemp tempBDD = new BDDTemp(context);
			tempBDD.openForWrite();
            long l = tempBDD.insertTemp(time, latitude, longitude,precision);
            Log.d("BroadcastRecording", "insertLocalisation = " + String.valueOf(l));
			tempBDD.close();
        }
    }
}


