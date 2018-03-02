package com.example.martin.test;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.IND_ARRET_INCONNU;
import static com.example.martin.test.Value.IND_ATTENTE_CONFIRME;
import static com.example.martin.test.Value.IND_DEPLACEMENT_INCONNU;
import static com.example.martin.test.Value.IND_DEPLACEMENT_VERS_CLIENT;
import static com.example.martin.test.Value.IND_DEPLACEMENT_VERS_RESTO;
import static com.example.martin.test.Value.IND_END;
import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_RESTO_CONFIRME;
import static com.example.martin.test.Value.IND_START;
import static com.example.martin.test.Value.NUM_COL_DUREE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IND_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_TIME_LOCAL;
import static com.example.martin.test.Value.distence2;
import static com.example.martin.test.Value.intToString;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;


public class ActivityHistory extends Activity {

    private DatePicker myDatePicker;
    private Button selectDate;
    private Button retourSelectDate;
    private ConstraintLayout layoutSelectDate;
    private ConstraintLayout layoutHistory;
    private TextView textViewSelectDate;
	private long dateCalendar;
	private List<UneLigne> data=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //initialisation variable
        myDatePicker=findViewById(R.id.datePicker);
        selectDate=findViewById(R.id.idBtnDate);
        layoutSelectDate=findViewById(R.id.idLayoutSelectionDate);
        layoutHistory=findViewById(R.id.idLayoutHistorique);
        retourSelectDate=findViewById(R.id.idRetourSelectionDate);
        textViewSelectDate=findViewById(R.id.idTextSelectionDate);


        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

				data.clear();
                int day=myDatePicker.getDayOfMonth();
                int month=myDatePicker.getMonth();
                int year=myDatePicker.getYear();
                Calendar myCalendar= Calendar.getInstance();
                myCalendar.set(year,month,day,0,0,0);

                dateCalendar =myCalendar.getTimeInMillis();
                Log.d("history","time = "+String.valueOf(dateCalendar));

				setList();




            }
        });

        retourSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				data.clear();
                layoutHistory.setVisibility(View.INVISIBLE);
                layoutSelectDate.setVisibility(View.VISIBLE);
            }
        });


    }


	private void setList(){
		BDDLocalisation localisationBDD = new BDDLocalisation(ActivityHistory.this);
		localisationBDD.openForRead();

		Cursor c= localisationBDD.getCursorBetween(dateCalendar,dateCalendar+86400000);
		int nbPoint = c.getCount();

		if (nbPoint==0){
			textViewSelectDate.setText(R.string.textPasDeMouvement);
			c.close();
			localisationBDD.close();
		}
		else {
			data.clear();
			layoutSelectDate.setVisibility(View.INVISIBLE);
			layoutHistory.setVisibility(View.VISIBLE);
			Log.d("history",String.valueOf(nbPoint)+ " points");



			int indicationPrecedante=-1;

			int distance=0;
			float latRadPrecedante=0;
			float lonRadPrecedante=0;
			long date;
			long datePrecedante=0;


			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				int indication=c.getInt(NUM_COL_IND_LOCAL);

				//deplacement
				if(indication==IND_DEPLACEMENT_INCONNU||indication==IND_DEPLACEMENT_VERS_CLIENT||indication==IND_DEPLACEMENT_VERS_RESTO){
					if(c.isFirst()||indicationPrecedante==IND_START){
						latRadPrecedante = c.getFloat(NUM_COL_LATRAD_LOCAL);
						lonRadPrecedante= c.getFloat(NUM_COL_LONRAD_LOCAL);
						datePrecedante= c.getLong(NUM_COL_TIME_LOCAL);
					}


					float latRad= c.getFloat(NUM_COL_LATRAD_LOCAL);
					float lonRad= c.getFloat(NUM_COL_LONRAD_LOCAL);

					distance+= (int)(Math.sqrt(distence2(latRadPrecedante,latRad,lonRadPrecedante,lonRad)));
					latRadPrecedante=latRad;
					lonRadPrecedante=lonRad;

					if(c.isLast()) {
						date=c.getLong(NUM_COL_TIME_LOCAL);
						data.add(
								new UneLigne(indication, datePrecedante,distance,(int)(date-datePrecedante)/1000)
						);


					}

				}
				//arrêt
				else if(indication>=IND_ARRET_INCONNU&&indication<=IND_ATTENTE_CONFIRME){
					date=c.getLong(NUM_COL_TIME_LOCAL);



					//ajout déplacement
					if(!c.isFirst()&&distance!=0) {
						if(indicationPrecedante>=IND_ARRET_INCONNU) indicationPrecedante=IND_DEPLACEMENT_INCONNU;
						data.add(
								new UneLigne(indicationPrecedante, datePrecedante, distance, (int) (date - datePrecedante)/1000)
						);
					}
					//ajout arret

					int idResto=(c.getInt(NUM_COL_IDRESTO_LOCAL));
					int duree=c.getInt(NUM_COL_DUREE_LOCAL);
					data.add(new UneLigne(indication,date,0,duree,idResto));
					//mise a jour variable

					float latRad= c.getFloat(NUM_COL_LATRAD_LOCAL);
					float lonRad= c.getFloat(NUM_COL_LONRAD_LOCAL);
					if(!c.isFirst()) distance = (int) (Math.sqrt(distence2(latRadPrecedante, latRad, lonRadPrecedante, lonRad)));

					latRadPrecedante= latRad;
					lonRadPrecedante=lonRad;

					datePrecedante=date;
				}
				//fin shift
				else if(indication==IND_END||indication==IND_START){
					date=c.getLong(NUM_COL_TIME_LOCAL);
					int idResto=(c.getInt(NUM_COL_IDRESTO_LOCAL));
					int duree=c.getInt(NUM_COL_DUREE_LOCAL);
					data.add(new UneLigne(indication,date,0,duree,idResto));
					datePrecedante=0;

				}

				indicationPrecedante=indication;
			}
			c.close();
			localisationBDD.close();

			int distanceTotale=0;
			int nbCommande=0;
			int count=0;
			BDDRestaurant bddRestaurant=new BDDRestaurant(this);
			bddRestaurant.openForRead();

			for (UneLigne l:data) {
				count++;
				distanceTotale=distanceTotale+l.getDistance();
				if(l.getIdResto()!=-1){
					l.setNomResto(bddRestaurant.getTextRestaurant(l.getIdResto()));
					nbCommande++;
				}
				Log.d("history","ligne "+ intToString(count)+" : "+l.toString());
			}
			bddRestaurant.close();


			ListView myListView= findViewById(R.id.listHistory);
			myAdapter adapter =new myAdapter(ActivityHistory.this);
			myListView.setAdapter(adapter);


			DateFormat df=getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
			((TextView) findViewById(R.id.dateHistory)).setText(df.format(dateCalendar));
			((TextView) findViewById(R.id.cmdHistory)).setText(String.valueOf(nbCommande)+" cmd");
			Log.d("h",String.valueOf(data.get(data.size()-1).getDate()));
			Log.d("h",String.valueOf(data.get(0).getDate()));

			int d=  (int) (data.get(data.size()-1).getDate()-data.get(0).getDate());
			int h=  d/3600000;
			int m= (d%3600000)/60000;

			((TextView) findViewById(R.id.dureeHistory)).setText(intToString(h)+"h "+intToString(m)+"m ");

			if(distanceTotale>0) {
				int di = distanceTotale / 1000;
				int di2 = (distanceTotale % 1000) / 10;
				((TextView) findViewById(R.id.distanceHistory)).setText(String.valueOf(di)+"."+intToString(di2) + "km ");
			}

		}
	}

	class UneLigne{

		int indi;
		long date;
		int idResto;
		int duree; //en seconde
		int distance;  //en m
		String nomResto;

		//constructeur pour arret
		UneLigne(int indi,long date,int distance,int duree,int idResto){
			this.indi=indi;
			this.date=date;
			this.idResto=idResto;
			this.distance=distance;
			this.duree=duree;
			this.nomResto="";
		}
		//constructeur pour deplacement
		UneLigne(int indi,long date,int distance,int duree){
			this(indi,date,distance,duree,ID_RESTO_DEFAUT);
		}

		void setNomResto(String nomResto) {
			this.nomResto = nomResto;
		}

		int getIndi(){
			return indi;
		}

		long getDate(){ return date;}

		String getNomResto(){ return nomResto;}

		int getIdResto(){
			return idResto;
		}

		int getDuree(){
			return duree;
		}

		int getDistance(){
			return distance;
		}


		@Override
		public String toString() {
			String res="date : ";
			res += String.valueOf(date);
			res +=" indi: ";
			res += String.valueOf(indi);
			res +=" duree : ";
			res += String.valueOf(duree);
			res +=" distance: ";
			res += String.valueOf(distance);
			res +=" idResto: ";
			res += String.valueOf(idResto);
			return res;
		}


    }

	class myAdapter extends ArrayAdapter<UneLigne>{
    	private Context context;
		String[] stringIndication;
		int resource;

		public myAdapter(@NonNull Context context) {
			this(context,R.layout.item_liste_history);
		}

		public myAdapter(@NonNull Context context, int resource) {
			super(context, resource);
			this.resource=resource;
			this.context=context;
			this.stringIndication=context.getResources().getStringArray(R.array.indication);
		}
		@NonNull
		@Override
		public View getView(int position, View convertView, @NonNull ViewGroup parent){
			if(convertView==null) {
				LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				assert layoutInflater != null;
				convertView = layoutInflater.inflate(resource, parent, false);
				final UneLigne ligne = data.get(position);
				final TextView textIndication = convertView.findViewById(R.id.itemHistoryIndication);
				if (ligne.getIndi() > 0 && ligne.getIndi() < stringIndication.length) {
					if (ligne.getIndi() == IND_HYPO_RESTO || ligne.getIndi() == IND_RESTO || ligne.getIndi() == IND_RESTO_CONFIRME) {
						textIndication.setText(String.valueOf(ligne.getNomResto()));
					} else textIndication.setText(stringIndication[ligne.getIndi()]);

				}
				final TextView textHeure = convertView.findViewById(R.id.itemHistoryHeure);
				if (ligne.getDate()>0){

					DateFormat df=getTimeInstance(DateFormat.MEDIUM, Locale.getDefault());
					textHeure.setText(df.format(new Date(ligne.getDate())));
				}
				final TextView textDistance = convertView.findViewById(R.id.itemHistoryDistance);

				if(ligne.getDistance()>0) {
					final int di = ligne.getDistance() / 1000;
					final int di2 = ((ligne.getDistance() % 1000) / 10);

					textDistance.setText(String.valueOf(di)+"."+intToString(di2) + "km");
					textDistance.setVisibility(View.VISIBLE);
				}
				else textDistance.setText("");

				final TextView textDuree = convertView.findViewById(R.id.itemHistoryDuree);
				int du=ligne.getDuree()/60;
				if(du<10) textDuree.setText("  "+String.valueOf(du)+" min");
				else textDuree.setText(String.valueOf(du)+" min");

			}
			return convertView;
		}
		@Override
		public int getCount(){
			if(data!=null) return data.size();
			return 0;
		}
	}

}
