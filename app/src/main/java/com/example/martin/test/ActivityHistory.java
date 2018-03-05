package com.example.martin.test;

import android.app.Activity;
import android.content.Context;
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
import java.util.Locale;

import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_RESTO_CONFIRME;
import static com.example.martin.test.Value.intToString;
import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;


public class ActivityHistory extends Activity {

    private DatePicker myDatePicker;
    private Button selectDate;
    private Button retourSelectDate;
    private Button cancel;
    private ConstraintLayout layoutSelectDate;
    private ConstraintLayout layoutHistory;
    private TextView textViewSelectDate;
	private long dateCalendar;
	private	ArrayList<UneLigne> data;
	BDDLocalisation localisationBDD;
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
		cancel=findViewById(R.id.idCancelHistory);


		cancel.setOnClickListener(new View.OnClickListener(){

			@Override public void onClick(View v) {
			finish();
			}
		});

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

                layoutHistory.setVisibility(View.INVISIBLE);
                layoutSelectDate.setVisibility(View.VISIBLE);
            }
        });

    }

	private void setList(){

		localisationBDD = new BDDLocalisation(ActivityHistory.this);
		localisationBDD.openForRead();

		if (localisationBDD.isEmptyBetween(dateCalendar,dateCalendar+86400000)){
			textViewSelectDate.setText(R.string.textPasDeMouvement);
			localisationBDD.close();
		}

		else {
			new Thread(new Runnable() {

				public void run() {

					data=localisationBDD.getCommandeBetween(ActivityHistory.this,dateCalendar,dateCalendar+86400000);
					localisationBDD.close();

					runOnUiThread(new Runnable() {

						public void run() {


							layoutSelectDate.setVisibility(View.INVISIBLE);
							layoutHistory.setVisibility(View.VISIBLE);

							ListView myListView= findViewById(R.id.listHistory);
							myAdapter adapter =new myAdapter(ActivityHistory.this);
							myListView.setAdapter(adapter);


							DateFormat df=getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
							((TextView) findViewById(R.id.dateHistory)).setText(df.format(dateCalendar));
							((TextView) findViewById(R.id.cmdHistory)).setText(String.valueOf(localisationBDD.nbCommande)+" cmd");


							int d=localisationBDD.duree;
							int h=  d/3600000;
							int m= (d%3600000)/60000;

							((TextView) findViewById(R.id.dureeHistory)).setText(intToString(h)+"h "+intToString(m)+"m ");

							if(localisationBDD.distanceTotale>0) {
								int di = localisationBDD.distanceTotale / 1000;
								int di2 = (localisationBDD.distanceTotale % 1000) / 10;
								((TextView) findViewById(R.id.distanceHistory)).setText(String.valueOf(di)+"."+intToString(di2) + "km ");
							}
						}

					});

				}

			}).start();

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
				if(ligne.getDuree()>1) {
					if (du < 10) textDuree.setText("  " + String.valueOf(du) + " min");
					else textDuree.setText(String.valueOf(du) + " min");
				}

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
