package com.example.martin.test;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import static com.example.martin.test.Value.NUM_COL_DUREE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_IDRESTO_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LATITUDE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_LONGITUDE_LOCAL;
import static com.example.martin.test.Value.NUM_COL_TIME_LOCAL;


public class ActivityHistory extends Activity {

    private DatePicker myDatePicker;
    private Button selectDate;
    private Button retourSelectDate;
    private ConstraintLayout layoutSelectDate;
    private ConstraintLayout layoutHistory;
    private TextView textViewSelectDate;

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

                long debut =myCalendar.getTimeInMillis();
                Log.d("history","time = "+String.valueOf(debut));

                BDDLocalisation localisationBDD = new BDDLocalisation(ActivityHistory.this);
                localisationBDD.openForRead();
                Cursor c= localisationBDD.getCursorBetween(debut,debut+86400);
                int nbPoint = c.getCount();

                if (nbPoint==0){
                    textViewSelectDate.setText(R.string.textPasDeMouvement);
                }
                else {
                    layoutSelectDate.setVisibility(View.INVISIBLE);
                    layoutHistory.setVisibility(View.VISIBLE);
                    Log.d("history",String.valueOf(nbPoint)+ " points");

                    long[] t = new long[nbPoint];
                    float[] lat=new float[nbPoint];
                    float[] lon=new float[nbPoint];
                    int[] d=new int[nbPoint];
                    int[] idResto=new int[nbPoint];
                    int i=0;
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                        t[i] = c.getLong(NUM_COL_TIME_LOCAL);
                        lat[i]=(float) c.getDouble(NUM_COL_LATITUDE_LOCAL);
                        lon[i]=(float) c.getDouble(NUM_COL_LONGITUDE_LOCAL);
                        d[i] = c.getInt(NUM_COL_DUREE_LOCAL);
                        idResto[i]=c.getInt(NUM_COL_IDRESTO_LOCAL);
                        i++;
                    }


                }

                c.close();
                localisationBDD.close();


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


    protected void onResume() {
        super.onResume();





    }



}
