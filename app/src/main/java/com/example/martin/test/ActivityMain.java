package com.example.martin.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static com.example.martin.test.Value.ID_NOTIFICATION;
import static com.example.martin.test.Value.IND_ATTENTE;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_PLATEFORME;
import static com.example.martin.test.Value.verifPermissionLocation;

public class ActivityMain extends Activity
		implements FragmentSelectPlateforme.OnPlateformeSelectedListener {


	final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
	final int MY_PERMISSIONS_REQUEST_INTERNET=2;
	final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;
	final int MY_PERMISSIONS_REQUEST= 4;

	private TextView textStatut;
    private Intent intentRecording;
    private Intent intentAction;
    private PendingIntent pendingRecording;
    private boolean isWorking = false;
    private Switch switchStart;
    private Button btnStartAndGo;
    private Button btnCustomer;
    private Button btnWaiting;
    private Button btnRestaurant;
	private int zone=1;
	private int pays=0;
    private Button btnHistorique;
    private Button btnExport;
	private Button btnSetting;
    private LinearLayout layoutAction;
	private Button btnExportDebug;

    private Button btnPlateforme;
    private Button btnPlateforme0;
    private Button btnPlateforme1;
	private Button btnPlateforme2;
    private Button btnPlateforme3;
	private Button btnPlateforme4;
   // private int plateformeEnCours=-1;

    private int plateformeEnCours=1;

	private SharedPreferences preferences;

    private LinearLayout layoutCgtPlateforme;



	private DialogFragment selectPlateformeFragment;
	private MyReceiver receiver;

    @SuppressLint("MissingPermission")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ActivityMain", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


		preferences = PreferenceManager.getDefaultSharedPreferences(this);
        intentRecording = new Intent(ActivityMain.this, ServiceRecording.class);
        intentAction=new Intent(ActivityMain.this, BroadcastAction.class);
        textStatut = findViewById(R.id.idStatut);
        btnStartAndGo = findViewById(R.id.idStartAndStop);
        btnCustomer = findViewById(R.id.idCustomer);
        btnWaiting = findViewById(R.id.idWaiting);
        btnRestaurant = findViewById(R.id.idRestaurant);
        btnPlateforme = findViewById(R.id.idPlateforme);
        layoutAction = findViewById((R.id.idLayoutAction));
        layoutCgtPlateforme = findViewById((R.id.idChangementPlateforme));
        btnPlateforme0 = findViewById(R.id.idPlateforme1);
        btnPlateforme1 = findViewById(R.id.idPlateforme2);
        btnPlateforme2 = findViewById(R.id.idPlateforme3);
		btnPlateforme3 = findViewById(R.id.idPlateforme4);
		btnPlateforme4 = findViewById(R.id.idPlateforme5);
        btnHistorique=findViewById(R.id.idHistorique);
        btnExport=findViewById((R.id.idExport));
		btnSetting=findViewById((R.id.idSetting));
		btnExportDebug=findViewById((R.id.button3));




		//derniere plateforme utilisée
        BDDAction bddAction = new BDDAction(this);
		bddAction.openForRead();
        plateformeEnCours = bddAction.getLastPlateforme();
        bddAction.close();
        if (plateformeEnCours >= 0&&plateformeEnCours<IND_PLATEFORME.length) {
			setUIPlateforme(plateformeEnCours);
        }
		//permission de localisation et ecriture carte SD







//zone
		BDDZone bddZone=new BDDZone(this);
		bddZone.openForRead();
		boolean bddEmpty=bddZone.isEmpty();
		bddZone.close();
		if(bddEmpty){

			Log.d("MainActivity","bddZone Empty");
			// on déclare notre Broadcast Receiver
			receiver = new MyReceiver();
			IntentFilter filter = new IntentFilter(MyReceiver.ACTION_RESP);
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			registerReceiver(receiver, filter);


			Intent i = new Intent(ActivityMain.this, ServiceInstallZone.class);
			startService(i);



		}else{
			updateZone();
		}



    }

    @Override
    protected void onResume() {
        Log.d("ActivityMain", "onResume()");
        super.onResume();



        //masquage de layoutPlateforme

        layoutCgtPlateforme.setVisibility(View.INVISIBLE);

        //test pour savoir si le serviceAction est lancé ou pas

        isWorking = (PendingIntent.getService(ActivityMain.this, 2989, intentRecording, PendingIntent.FLAG_NO_CREATE) != null);
        Log.d("ActivityMain", "serviceAction is " + (isWorking ? "" : "not") + " working");
        if (isWorking) {
            textStatut.setText(R.string.StatutStart);
            btnStartAndGo.setText(R.string.textStop);
            layoutAction.setVisibility(View.VISIBLE);
        } else {
            textStatut.setText(R.string.StatutStop);
            btnStartAndGo.setText(R.string.textStart);
            layoutAction.setVisibility(View.INVISIBLE);
        }

	//setOnClickListener des boutons

        btnStartAndGo.setOnClickListener(new View.OnClickListener() {


            @SuppressLint("MissingPermission")
			public void onClick(View view) {

                //si l'enregistrement de la position était lancé: arrêt
                if (isWorking) {

                    isWorking = false;

                    //fin enregistrement position
                    pendingRecording = PendingIntent.getService(ActivityMain.this, 2989, intentRecording, PendingIntent.FLAG_UPDATE_CURRENT);
                    LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (myLocationManager != null) {
                       // myLocationManager.removeUpdates(pendingRecording);
                    }
					FusedLocationProviderClient mFusedLocationClient= LocationServices.getFusedLocationProviderClient(ActivityMain.this);
					mFusedLocationClient.removeLocationUpdates(pendingRecording);
					Log.d("mainActivite","arret recording" );

                    pendingRecording.cancel();

                   //mise a jour interface
                    textStatut.setText(R.string.StatutStop);
                    btnStartAndGo.setText(R.string.textStart);
                    layoutAction.setVisibility(View.INVISIBLE);
                    //anulation Notification
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel(ID_NOTIFICATION);
                    }

                }

                //sinon, l'enregistrement etait arrêté: abonnement aux changements de positions
                else {
                    Log.d("myActivity", "clic demarreService");

                    //controle plateforme

                    if (plateformeEnCours==-1){
                        Log.d("ActivityMain", "!!!!!!pas de plateforme!!!!");
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        selectPlateformeFragment = FragmentSelectPlateforme.newInstance();
                        selectPlateformeFragment.show(ft, "selectPlateforme");
                    }


					boolean gpsModeActif=preferences.getBoolean("GPSModeActif",false);
					Log.d("MainActivity","gpsModeActif : "+String.valueOf(gpsModeActif));
					//controle permission et GPS activé
					if(verifPermissionLocation(ActivityMain.this)){

						isWorking = true;

						//enregistrement position
						pendingRecording = PendingIntent.getService(ActivityMain.this, 2989, intentRecording, PendingIntent.FLAG_UPDATE_CURRENT);

						LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

						if (myLocationManager != null) {
							//myLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_UPDATE_LOCATION, MIN_DISTANCE_UPDATE_LOCATION, pendingRecording);
						}
						LocationRequest mLocationRequest = new LocationRequest();
						if(gpsModeActif) {
							mLocationRequest.setFastestInterval(50);
							mLocationRequest.setInterval(1000);
							mLocationRequest.setMaxWaitTime(25000);
							mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
						}
						else {
							mLocationRequest.setFastestInterval(50);
							mLocationRequest.setInterval(51);
							mLocationRequest.setMaxWaitTime(25000);
							mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
						}

						FusedLocationProviderClient mFusedLocationClient= LocationServices.getFusedLocationProviderClient(ActivityMain.this);
						mFusedLocationClient.requestLocationUpdates(mLocationRequest,pendingRecording);

						//mise a jour interface
						textStatut.setText(R.string.StatutStart);
						layoutAction.setVisibility(View.VISIBLE);
						btnStartAndGo.setText(R.string.textStop);

					}
					else{
					Toast.makeText(ActivityMain.this, R.string.taostPermissionRefusee, Toast.LENGTH_LONG).show();
				}
			}




		}
        });
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if(isWorking) {


                    intentAction.removeExtra("action");
                    intentAction.putExtra("action", IND_CLIENT);
                    sendBroadcast(intentAction);
                    intentAction.removeExtra("action");
                }
            }
        });

        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if(isWorking) {

					Intent i = new Intent(ActivityMain.this, ActivitySelectRestaurant.class);
					startActivity(i);
                }
            }
        });

        btnWaiting.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                if(isWorking) {

                    intentAction.removeExtra("action");
                    intentAction.putExtra("action", IND_ATTENTE);
                    sendBroadcast(intentAction);
                    intentAction.removeExtra("action");

                }
            }
        });



        btnHistorique.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Log.d("myActivity","clicHistorique");
                Intent i = new Intent(ActivityMain.this, ActivityHistory.class);
                startActivity(i);

            }
        });

		btnExport.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
				Log.d("myActivity","clicExport");
				Intent i = new Intent(ActivityMain.this, ActivityExport.class);
				startActivity(i);
            }
        });

		btnExportDebug.setOnClickListener(new View.OnClickListener() {
			@Override

			public void onClick(View view) {
				Log.d("myActivity","clicExport");
				Intent i = new Intent(ActivityMain.this, ServiceExportDebug.class);
				startService(i);
			}
		});


        btnSetting.setOnClickListener(new View.OnClickListener() {
			@Override

			public void onClick(View view) {
				Log.d("myActivity","clicSetting");
				Intent i = new Intent(ActivityMain.this, ActivitySettings.class);
				startActivity(i);
			}
		});




//plateforme

        btnPlateforme.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            /*
                layoutCgtPlateforme.setVisibility(View.VISIBLE);
                btnPlateforme.setVisibility(View.INVISIBLE);
              */

            }
        });

        btnPlateforme0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (plateformeEnCours!=0) {
					onPlateformeChange(0);
                }
                layoutCgtPlateforme.setVisibility(View.INVISIBLE);
                btnPlateforme.setVisibility(View.VISIBLE);

            }
        });

        btnPlateforme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (plateformeEnCours!=1) {
					onPlateformeChange(1);
                }
                layoutCgtPlateforme.setVisibility(View.INVISIBLE);
                btnPlateforme.setVisibility(View.VISIBLE);

            }
        });
		btnPlateforme2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (plateformeEnCours!=2) {
					onPlateformeChange(2);
				}
				layoutCgtPlateforme.setVisibility(View.INVISIBLE);
				btnPlateforme.setVisibility(View.VISIBLE);

			}
		});
        btnPlateforme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (plateformeEnCours!=3) {
					onPlateformeChange(3);
                }
                layoutCgtPlateforme.setVisibility(View.INVISIBLE);
                btnPlateforme.setVisibility(View.VISIBLE);
            }
        });
		btnPlateforme4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (plateformeEnCours!=4) {
					onPlateformeChange(4);
				}
				layoutCgtPlateforme.setVisibility(View.INVISIBLE);
				btnPlateforme.setVisibility(View.VISIBLE);
			}
		});
    }



	protected void onDestroy () {

        super.onDestroy();
        Log.d("myActivity","onDestroy ()");

    }
    protected void onPause() {

        super.onPause();
        Log.d("myActivity","onPause()");
    }
    protected void onStop() {

        super.onStop();
        Log.d("myActivity","onStop()");
    }

	@Override
	public void onPlateformeSelected(int plateforme) {
		Log.d("myActivity","btn "+String.valueOf(plateforme));
		onPlateformeChange(plateforme);
		selectPlateformeFragment.dismiss();
	}

//si la plateforme a deja ete selectionné: il faut seulement afficher la plateforme dans le bouton
	private void setUIPlateforme(int plateforme){
		String[] nomPlateforme=getResources().getStringArray(R.array.plateforme);
		if(IND_PLATEFORME.length!=nomPlateforme.length)
			throw new AssertionError("taille nomPlateforme et IND_PLATEFORME differente");

    	if(plateforme>=0&&plateforme<nomPlateforme.length){

			btnPlateforme.setText(nomPlateforme[plateforme]);
    	}
    	else throw new AssertionError("plateforme invalide");
	}

	private void onPlateformeChange(int plateforme){
		if(plateforme>=0&&plateforme<IND_PLATEFORME.length) {
			plateformeEnCours = plateforme;

			intentAction.removeExtra("action");
			intentAction.putExtra("action", IND_PLATEFORME[plateforme]);
			sendBroadcast(intentAction);
			intentAction.removeExtra("action");
			setUIPlateforme(plateformeEnCours);
		}
		else throw new AssertionError("plateforme invalide");
	}

	@SuppressLint("MissingPermission")
	private void updateZone(){

		if (verifPermissionLocation(this)){

			FusedLocationProviderClient mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
			mFusedLocationClient.getLastLocation()
					.addOnSuccessListener(this, new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							// Got last known location. In some rare situations this can be null.
							if (location != null) {
								useLocation(location);
							}
						}
					});

		}else {
			Log.d("activite", "Missing Permission");

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				Boolean boolLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
				Boolean boolInternet = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED;

				Boolean boolExternalStorage = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;

				if (boolLocation && boolExternalStorage) {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET},
							MY_PERMISSIONS_REQUEST);

				} else if (boolLocation) {
					requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
							MY_PERMISSIONS_REQUEST_LOCATION);

				} else if (boolExternalStorage) {

					requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
				}
				if (boolInternet) {
					requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET},
							MY_PERMISSIONS_REQUEST_INTERNET);

				}


			}

		}
	}

	private void useLocation(Location loc){
		Log.d("activite","actualisation zone");
		BDDZone bddZone = new BDDZone(this);
		bddZone.openForRead();

		zone = bddZone.getIdZone(Math.toRadians(loc.getLatitude()), Math.toRadians(loc.getLongitude()));
		bddZone.close();

		SharedPreferences.Editor editor=preferences.edit();
		editor.putInt("zone",zone);
		editor.apply();
		((TextView) findViewById(R.id.idZone)).setText(bddZone.textZoneActual);
		pays=bddZone.paysActual;

		BDDRestaurant bddRestaurant=new BDDRestaurant(this);
		bddRestaurant.openForRead();
		Boolean bddRestaurantEmpty=bddRestaurant.isEmpty();
		bddRestaurant.close();
		if(bddRestaurantEmpty&&pays>=0){
			Log.d("MainActivity","bddRestaurant Empty");
			Intent i = new Intent(ActivityMain.this, ServiceInstallRestaurant.class);
			i.putExtra("pays",pays);
			startService(i);
		}
	}

	public class MyReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP ="com.myapp.intent.action.ZoneInstalled";

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("MainActivity","onReceiveBroadcast");
			updateZone();
			// on désenregistre notre broadcast
			unregisterReceiver(receiver);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions,
										   @NonNull int[] grantResults){

		if((requestCode==MY_PERMISSIONS_REQUEST_LOCATION||requestCode==MY_PERMISSIONS_REQUEST)&&grantResults[0]==PERMISSION_GRANTED){
			updateZone();
		}else{
			((TextView) findViewById(R.id.idZone)).setText(R.string.missingPermission);
		}

	}



}

