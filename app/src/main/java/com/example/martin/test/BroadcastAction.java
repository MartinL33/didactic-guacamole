package com.example.martin.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.example.martin.test.Value.IND_ATTENTE;
import static com.example.martin.test.Value.IND_CLIENT;

public class BroadcastAction extends BroadcastReceiver {
	Context broadcastContext;
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
				l = bddAction.insertAction(time, indication);
			}
			else{
				l = bddAction.insertAction(time, indication);
			}

			bddAction.close();
			Log.d("BroadcastAction", "insertAction = " + String.valueOf(l)+ " indication = "+ String.valueOf(indication));
			if (indication == IND_ATTENTE || indication == IND_CLIENT) {
				String[] textIndication = context.getResources().getStringArray(R.array.indication);
				Toast.makeText(context, textIndication[indication], Toast.LENGTH_LONG).show();
			}

		}
	}



}
