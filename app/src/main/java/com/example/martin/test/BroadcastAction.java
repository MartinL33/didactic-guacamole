package com.example.martin.test;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.example.martin.test.Value.ID_NOTIFICATION;
import static com.example.martin.test.Value.IND_ATTENTE;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_RESTO;

public class BroadcastAction extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {


		Log.d("BroadcastAction", "onReceive");

		//Si l'usager appuie sur un bouton:

		if (intent.hasExtra("action")) {
			long l;
			long time= System.currentTimeMillis();
			int indication = intent.getIntExtra("action", 0);
			BDDAction bddAction = new BDDAction(context);
			bddAction.openForWrite();

			if(intent.hasExtra("idResto")){
				int idResto = intent.getIntExtra("idResto", 0);
				l = bddAction.insertAction(time, indication,idResto);
			}
			else{
				l = bddAction.insertAction(time, indication);
			}

			bddAction.close();
			Log.d("BroadcastAction", "insertAction = " + String.valueOf(l)+ " indication = "+ String.valueOf(indication));
			if (indication == IND_ATTENTE || indication == IND_CLIENT||indication == IND_RESTO) {
				String[] textIndication = context.getResources().getStringArray(R.array.indication);
				Toast.makeText(context, textIndication[indication], Toast.LENGTH_LONG).show();
			}

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(ID_NOTIFICATION);
            }
		}
	}



}
