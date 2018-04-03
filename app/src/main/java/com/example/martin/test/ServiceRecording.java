package com.example.martin.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.martin.test.ActivityMain.MyReceiver.LASTIND;
import static com.example.martin.test.ActivityMain.MyReceiver.NAMERESTO;
import static com.example.martin.test.Value.ID_NOTIFICATION;
import static com.example.martin.test.Value.IND_ATTENTE;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_DEFAUT;
import static com.example.martin.test.Value.IND_DEPLACEMENT_INCONNU;
import static com.example.martin.test.Value.IND_END;
import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_START;
import static com.google.android.gms.location.LocationResult.extractResult;
import static com.google.android.gms.location.LocationResult.hasResult;

public class ServiceRecording extends Service {

	private ListLocations data;
	private boolean hasNotify=false;

	@Override
	public void onCreate() {
		super.onCreate();
		data=new ListLocations();

		Log.d("ServiceAnalysis", "onCreate()");

	}

	@Override
	public void  onStart(Intent intent, int startId){
		new Thread(new Runnable() {

			public void run() {
				ArrayList<Localisation> listeTempLoca = new ArrayList<>();

				Log.d("ServiceAnalysis", "onStart");
				if (hasResult(intent)) {

					List<Location> listLocation = extractResult(intent).getLocations();
					if (listLocation.size() > 0) {
						BDDTemp tempBDD = new BDDTemp(ServiceRecording.this);
						tempBDD.openForWrite();

						for (Location l : listLocation) {

							if (l != null) {


								float latRad = (float) (Math.toRadians(l.getLatitude()));
								float lonRad = (float) (Math.toRadians(l.getLongitude()));
								long time = l.getTime();

								int precision = (int) l.getAccuracy();
								tempBDD.replaceTemp(time, latRad, lonRad, precision);


								Localisation localisation = new Localisation();
								localisation.setTime(time);
								localisation.setLatitude(latRad);
								localisation.setLongitude(lonRad);
								localisation.setPrecision(precision);
								listeTempLoca.add(localisation);

							}
						}
						tempBDD.close();

						Log.d("ServiceAnalysis", "nb New Location = " + String.valueOf(listLocation.size()));

					}
				}

				data.addLocalisations(listeTempLoca);
				data.analyse(ServiceRecording.this);
				int lastInd = data.getLastInd();

				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction(ActivityMain.MyReceiver.ACTION_RESP);
				broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
				broadcastIntent.putExtra(LASTIND,lastInd);


				if (data.hasResto()) {

					afficheNotification(lastInd, data.getResto().getName());
					broadcastIntent.putExtra(NAMERESTO,data.getResto().getName());
				} else {
					afficheNotification(lastInd);
				}


				sendBroadcast(broadcastIntent);


			}
		}).start();

	}

	@Override
	public void onDestroy() {
		Log.d("ServiceAnalysis", " onDestroy()");
		data=null;
	}
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void afficheNotification(int lastInd){
		afficheNotification(lastInd,"");
	}

	private void afficheNotification(int lastInd,String nameResto){


		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager != null) {


			if(!hasNotify&&(lastInd==IND_START||lastInd==IND_END||lastInd==IND_HYPO_RESTO)) {

				Intent notificationIntent = new Intent(this, ActivityMain.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent notificationPending = PendingIntent.getActivity(this, 0, notificationIntent, 0);

				Notification.Builder builder = new Notification.Builder(this);

				builder.setAutoCancel(true);

				builder.setSmallIcon(R.drawable.ic_notification_recording);
				builder.setContentIntent(notificationPending);
				builder.setOngoing(false);
				builder.setPriority(Notification.PRIORITY_HIGH);
				builder.setContentTitle(getResources().getString(R.string.app_name));

					//debut shift
				if(lastInd==IND_START) {
					builder.setContentText(getResources().getString(R.string.notification_title_Debut_Shift));
				}
				//fin shift
				else if(lastInd==IND_END) {
					builder.setContentText(getResources().getString(R.string.notification_title_Fin_Shift));
				}

				//resto
				else if(lastInd==IND_HYPO_RESTO) {

					builder.setContentText("Pick up : "+nameResto+" ?");
					Intent intentResto = new Intent(this, BroadcastAction.class);
					intentResto.putExtra("action", IND_RESTO);
					PendingIntent pendingResto = PendingIntent.getBroadcast(this, 1, intentResto, PendingIntent.FLAG_UPDATE_CURRENT);

					Intent intentClient = new Intent(this, BroadcastAction.class);
					intentClient.putExtra("action", IND_CLIENT);
					PendingIntent pendingClient = PendingIntent.getBroadcast(this, 2, intentClient, PendingIntent.FLAG_UPDATE_CURRENT);

					Intent intentAttente = new Intent(this, BroadcastAction.class);
					intentAttente.putExtra("action", IND_ATTENTE);
					PendingIntent pendingAttente = PendingIntent.getBroadcast(this, 3, intentClient, PendingIntent.FLAG_UPDATE_CURRENT);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
						builder.addAction(new Notification.Action(R.drawable.ic_restaurant, getResources().getString(R.string.ok), pendingResto));
						builder.addAction(new Notification.Action(R.drawable.ic_client, "client", pendingClient));
						builder.addAction(new Notification.Action(R.drawable.ic_stat_name, "pause", pendingAttente));
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
							builder.setVisibility(Notification.VISIBILITY_PUBLIC);
						}
					}
				}

				builder.build();
				Notification myNotication = builder.getNotification();

				notificationManager.notify(ID_NOTIFICATION, myNotication);
				hasNotify=true;

			}
			else if(lastInd==IND_DEFAUT||lastInd==IND_DEPLACEMENT_INCONNU){
                hasNotify=false;
				notificationManager.cancel(ID_NOTIFICATION);
			}
		}
	}


}




