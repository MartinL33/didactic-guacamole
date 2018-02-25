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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_RESTO_CONFIRME;
import static com.example.martin.test.Value.NUM_COL_DUREE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IND_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LATRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LONRAD_LOCAL;
import static com.example.martin.test.Value.NUM_COL_TIME_LOCAL;
import static com.example.martin.test.Value.distence2;


public class ActivityHistory extends Activity {

    private DatePicker myDatePicker;
    private Button selectDate;
    private Button retourSelectDate;
    private ConstraintLayout layoutSelectDate;
    private ConstraintLayout layoutHistory;
    private TextView textViewSelectDate;
	private long dateCalendar;
	List<UneLigne> data=new ArrayList<>();


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


    protected void onResume() {
        super.onResume();
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
			layoutSelectDate.setVisibility(View.INVISIBLE);
			layoutHistory.setVisibility(View.VISIBLE);
			Log.d("history",String.valueOf(nbPoint)+ " points");



			int indicationPrecedante=-1;

			int distancePrecedante=-1;
			float latRadPrecedante=0;
			float lonRadPrecedante=0;
			long dateFin=0;
			long dateDebut=0;
			int dureeFin=0;
			int dureeDebut=0;

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				if (indicationPrecedante==c.getInt(NUM_COL_IND_LOCAL)){
					float latRad= c.getFloat(NUM_COL_LATRAD_LOCAL);
					float lonRad= c.getFloat(NUM_COL_LONRAD_LOCAL);
					distancePrecedante= distancePrecedante+(int)(Math.sqrt(distence2(latRadPrecedante,latRad,lonRadPrecedante,lonRad)));
					dateFin=c.getLong(NUM_COL_TIME_LOCAL);
					dureeFin=c.getInt(NUM_COL_DUREE_LOCAL);
					latRadPrecedante=latRad;
					lonRadPrecedante=lonRad;
				}
				else{ //nouvelle ligne

					if(!data.isEmpty()) {
						//ajout ligne précédante
						data.get(data.size()-1).setDistance(distancePrecedante);
						data.get(data.size()-1).setDuree((int) ((dateFin-dateDebut+dureeFin+dureeDebut)/1000));
					}


					indicationPrecedante=c.getInt(NUM_COL_IND_LOCAL);
					distancePrecedante=0;
					latRadPrecedante= c.getFloat(NUM_COL_LATRAD_LOCAL);
					lonRadPrecedante=c.getFloat(NUM_COL_LONRAD_LOCAL);
					dateDebut=c.getLong(NUM_COL_TIME_LOCAL);
					dateFin=c.getLong(NUM_COL_TIME_LOCAL);
					dureeFin=0;
					dureeDebut=c.getInt(NUM_COL_DUREE_LOCAL);
					int heure=(int) ((dateDebut-dateCalendar)/1000);
					int idResto=(c.getInt(NUM_COL_IDRESTO_LOCAL));

					data.add(new UneLigne(indicationPrecedante,heure,idResto));
				}

			}
			c.close();
			localisationBDD.close();

			if(!data.isEmpty()) {
				//ajout ligne précédante
				data.get(data.size()-1).setDistance(distancePrecedante);
				data.get(data.size()-1).setDuree((int) ((dateFin-dateDebut+dureeFin+dureeDebut)/1000));

			}


			Log.d("history","Nb liste : "+String.valueOf(data.size()));
			Log.d("history","Derniere Distance : "+String.valueOf(data.get(data.size()-1).getDistance()));
			Log.d("history","Derniere Duree : "+String.valueOf(data.get(data.size()-1).getDuree()));
			ListView myListView=(ListView) findViewById(R.id.listHistory);
			myAdapter adapter =new myAdapter(ActivityHistory.this,R.layout.item_liste_history);
			myListView.setAdapter(adapter);

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yy");
			String historyDate = sdf1.format(dateCalendar);
			((TextView) findViewById(R.id.dateHistory)).setText(historyDate);

			int d=  data.get(0).getDuree()+(data.get(data.size()-1).getHeure()-data.get(0).getHeure());
			int h=  d/3600;
			int m= (d%3600)/60;

			((TextView) findViewById(R.id.dureeHistory)).setText(String.valueOf(h)+"h "+String.valueOf(m)+"m ");
			int distanceTotale=0;
			for (UneLigne l:data) {
				distanceTotale=distanceTotale+l.getDistance();
			}
			if(distanceTotale>0) {
				int di = distanceTotale / 1000;
				int di2 = (distanceTotale % 1000) / 10;
				String valueDistance;
				if (di2 < 10) valueDistance = String.valueOf(di) + ".0" + String.valueOf(di2);
				else valueDistance = String.valueOf(di) + "." + String.valueOf(di2);
				((TextView) findViewById(R.id.distanceHistory)).setText(valueDistance + "km ");
			}




		}
	}

	class UneLigne{

		int indi;
		int heure; //en seconde
		int idResto;
		int duree; //en seconde
		int distance;  //en m

		UneLigne(int indi,int heure,int idResto){
			this.indi=indi;
			this.heure=heure;
			this.idResto=idResto;
			this.distance=0;
			this.duree=0;
		}

		void setDuree(int duree) {
			this.duree = duree;
		}

		void setDistance(int distance) {
			this.distance = distance;
		}
		int getIndi(){
			return indi;
		}
		int getHeure(){
			return heure;

		}
		int getIdResto(){
			return idResto;
		}
		int getDuree(){
			return duree;
		}
		int getDistance(){
			return distance;
		}

	}

	class myAdapter extends ArrayAdapter<UneLigne>{
    	private Context context;
		String[] stringIndication;
		int resource;

 		public myAdapter(@NonNull Context context, int resource) {
			super(context, resource);
			this.context=context;
			this.resource=resource;
			this.stringIndication=context.getResources().getStringArray(R.array.indication);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			if(convertView==null){
				LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				assert layoutInflater != null;
				convertView=layoutInflater.inflate(resource,parent,false);
				final UneLigne ligne=data.get(position);
				final TextView textIndication = (TextView) convertView.findViewById(R.id.itemHistoryIndication);
				if (ligne.getIndi()>0&&ligne.getIndi()<stringIndication.length){
					textIndication.setText(stringIndication[ligne.getIndi()]);
				}
				final TextView textHeure = (TextView) convertView.findViewById(R.id.itemHistoryHeure);
				if (ligne.getHeure()>0&&ligne.getHeure()<86400){
					int h=  ligne.getHeure()/3600;
					int m= (ligne.getHeure()%3600)/60;
					int s=  (ligne.getHeure()%60);
					textHeure.setText(String.valueOf(h)+"h "+String.valueOf(m)+"m "+String.valueOf(s)+"s");
				}
				final TextView textDistance = (TextView) convertView.findViewById(R.id.itemHistoryDistance);

				if(ligne.getDistance()>0) {
					int di = ligne.getDistance() / 1000;
					int di2 = ((ligne.getDistance() % 1000) / 10);
					String valueDistance;
					if (di2 < 10) valueDistance = String.valueOf(di) + ".0" + String.valueOf(di2);
					else valueDistance = String.valueOf(di) + "." + String.valueOf(di2);
					textDistance.setText(valueDistance + "km ");
				}
				else textDistance.setText("");

				final TextView textDuree = (TextView) convertView.findViewById(R.id.itemHistoryDuree);
				int du=ligne.getDuree()/60;
				textDuree.setText(String.valueOf(du)+"min ");

				final TextView textResto=convertView.findViewById(R.id.itemHistoryResto);
				if(ligne.getIndi()==IND_HYPO_RESTO||ligne.getIndi()==IND_RESTO||ligne.getIndi()==IND_RESTO_CONFIRME){
					textResto.setText("resto connu");
				}
				else textResto.setText("");

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
