package com.example.martin.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.verifPermissionLocation;

public class ActivitySelectRestaurant extends Activity {

boolean restoConnu=true;

double lat=0;
double lon=0;
int z=1;
int p=1;
BDDRestaurant bddRestaurant=new BDDRestaurant(ActivitySelectRestaurant.this);


	@SuppressLint("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_restaurant);

		if(verifPermissionLocation(this)){
			FusedLocationProviderClient mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
			mFusedLocationClient.getLastLocation()
					.addOnSuccessListener(new OnSuccessListener<Location>() {
						@Override
						public void onSuccess(Location location) {
							// Got last known location. In some rare situations this can be null.
							if (location != null) {
								Log.d("ActivitySelectResto","actualisation position");

								useLocation(location);
							}
						}
					});

		}
	}


	@Override
	protected void onResume() {
		super.onResume();
	}



	void useLocation (Location location){
		lat=location.getLatitude();
		lon=location.getLongitude();



		BDDZone bddZone = new BDDZone(ActivitySelectRestaurant.this);
		z= bddZone.getIdZone(location.getLatitude(), location.getLongitude());

		BDDAction bddAction = new BDDAction(ActivitySelectRestaurant.this);
		p = bddAction.getLastPlateforme();



		restoConnu =bddRestaurant.bddHasResto(lat, lon,z,p);

		Log.d("ActivitySelectResto","restaurant connu? : "+String.valueOf(restoConnu));

		setUI(restoConnu);

	}

	void setUI(boolean selectResto){

		if (selectResto){


			Log.d("ActivitySelectResto","selectresto");
			((LinearLayout) findViewById(R.id.idNewResto)).setVisibility(View.INVISIBLE);
			((LinearLayout) findViewById(R.id.idSelectResto)).setVisibility(View.VISIBLE);

			bddRestaurant.openForRead();
			bddRestaurant.selectResto(lat, lon, z, p);
			bddRestaurant.close();

			//assertion
			if(BuildConfig.DEBUG&&!(bddRestaurant.nameRestoSelect.length>0)) throw new AssertionError();
			if(BuildConfig.DEBUG&&!(bddRestaurant.idRestoSelect.length==bddRestaurant.nameRestoSelect.length)) throw new AssertionError();


			ArrayAdapter adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,bddRestaurant.nameRestoSelect);

			ListView mylistView=findViewById(R.id.idListViewSelectResto);
			mylistView.setAdapter(adapter);

			mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int positionClic, long l) {
					Log.d("ActivitySelectResto","clic position : "+String.valueOf(positionClic));

					Intent intentAction=new Intent(ActivitySelectRestaurant.this, BroadcastAction.class);
					intentAction.removeExtra("action");
					intentAction.putExtra("action", IND_RESTO);
					int idResto=bddRestaurant.idRestoSelect[positionClic];
					if (idResto>0) intentAction.putExtra("idResto", idResto);
					sendBroadcast(intentAction);

					finish();



				}
			});



			Button cancel=findViewById(R.id.idCancelSelectResto);

			cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					finish();
				}
			});

			Button newRestoButton=findViewById(R.id.idButtonNewResto);
			newRestoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					setUI(false);
				}
			});




		}
		else{

			Log.d("ActivitySelectResto","new restaurant");
			((LinearLayout) findViewById(R.id.idNewResto)).setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.idSelectResto)).setVisibility(View.INVISIBLE);

			Button cancel=findViewById(R.id.CancelNewRestaurant);
			Button ok = findViewById(R.id.OkNewRestaurant);

			cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				if(restoConnu){
					setUI(true);
				}
				else{
					finish();
				}


				}
			});


			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					EditText editText=findViewById(R.id.editTextNewRestaurant);
					String NomResto=editText.getText().toString();
					bddRestaurant.openForWrite();
					long idResto=bddRestaurant.insertResto(lat,lon,NomResto,z,p);
					bddRestaurant.close();

					Intent intentAction=new Intent(ActivitySelectRestaurant.this, BroadcastAction.class);
					intentAction.removeExtra("action");
					intentAction.putExtra("action", IND_RESTO);
					if (idResto>0) intentAction.putExtra("idResto", idResto);
					sendBroadcast(intentAction);

					finish();
				}
			});

		}





	}
}




