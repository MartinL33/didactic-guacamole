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
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.IND_ATTENTE;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_DEPLACEMENT_INCONNU;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.SEUILSELECTRESTO;
import static com.example.martin.test.Value.verifPermissionLocation;

public class ActivitySelectRestaurant extends Activity {

private boolean restoConnu=true;

private float latRad=0;
private float lonRad=0;
private int z=1;
private int p=1;
private BDDRestaurant bddRestaurant=new BDDRestaurant(ActivitySelectRestaurant.this);


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




	private void useLocation(Location location){
		latRad=(float) Math.toRadians(location.getLatitude());
		lonRad=(float) Math.toRadians(location.getLongitude());
		BDDZone bddZone = new BDDZone(ActivitySelectRestaurant.this);
		bddZone.openForRead();
		z= bddZone.getIdZone(latRad, lonRad);
        bddZone.close();

		BDDAction bddAction = new BDDAction(ActivitySelectRestaurant.this);
        bddAction.openForRead();
		p = bddAction.getLastPlateforme();
        bddAction.close();
		Localisation l=new Localisation();
		l.setLatitude(latRad);
		l.setLongitude(lonRad);

        bddRestaurant.openForRead();
		restoConnu =bddRestaurant.bddHasResto(latRad,lonRad,z,p,SEUILSELECTRESTO);
        bddRestaurant.close();
		Log.d("ActivitySelectResto","restaurant connu? : "+String.valueOf(restoConnu));

//a vocation à etre supprimer:  insertion des nouveau resto a faire
		if(!restoConnu) {
			Toast.makeText(this, "Pas de restaurant connu à proximité", Toast.LENGTH_LONG).show();
			finish();
		}

		setUI(restoConnu);

	}

	private void setUI(boolean selectResto){

		if (selectResto){


			Log.d("ActivitySelectResto","selectresto");
			findViewById(R.id.idNewResto).setVisibility(View.INVISIBLE);
			findViewById(R.id.idSelectResto).setVisibility(View.VISIBLE);
			ArrayList<Resto> restos;


			bddRestaurant.openForRead();
			restos=bddRestaurant.getArrayResto(latRad, lonRad, z, p,SEUILSELECTRESTO);
			bddRestaurant.close();

			String[] nameRestos=new String[restos.size()+3];
			int[] idRestos=new int[restos.size()+3];
			int i=0;
			for(Resto r:restos){
				nameRestos[i]=r.getName();
				idRestos[i]=r.getId();
				i++;
			}
			nameRestos[nameRestos.length-3]="trajet";
			idRestos[nameRestos.length-3]=-1;
			nameRestos[nameRestos.length-2]="pause";
			idRestos[nameRestos.length-2]=-1;
			nameRestos[nameRestos.length-1]="client";
			idRestos[nameRestos.length-1]=-1;

			ArrayAdapter<String> adapter= new android.widget.ArrayAdapter<>(this,android.R.layout.simple_list_item_1,nameRestos);

			ListView mylistView=findViewById(R.id.idListViewSelectResto);
			mylistView.setAdapter(adapter);

			mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int positionClic, long l) {
					Log.d("ActivitySelectResto","clic position : "+String.valueOf(positionClic));
					Intent intentAction = new Intent(ActivitySelectRestaurant.this, BroadcastAction.class);
					intentAction.removeExtra("action");

					if(positionClic>=0&&positionClic<nameRestos.length-3) {

						intentAction.putExtra("action", IND_RESTO);
						int idResto = idRestos[positionClic];
						if (idResto > 0) intentAction.putExtra("idResto", idResto);

					}
					else if(positionClic>=nameRestos.length-3&&positionClic<nameRestos.length){
						if(positionClic==nameRestos.length-3) intentAction.putExtra("action", IND_DEPLACEMENT_INCONNU);
						if(positionClic==nameRestos.length-2) intentAction.putExtra("action", IND_ATTENTE);
						if(positionClic==nameRestos.length-1) intentAction.putExtra("action", IND_CLIENT);
					}
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
			findViewById(R.id.idNewResto).setVisibility(View.VISIBLE);
			findViewById(R.id.idSelectResto).setVisibility(View.INVISIBLE);

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
					long idResto=bddRestaurant.insertResto((float)latRad,(float)lonRad,NomResto,z,p,ID_RESTO_DEFAUT);
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




