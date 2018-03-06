package com.example.martin.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityExport extends Activity {
	private Button cancel;
	private Button ok;
	BDDLocalisation localisationBDD;
	Long dateStop;
	Long dateStart;
	TextView textPath;
	String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		cancel = findViewById(R.id.cancelExport);
		ok = findViewById(R.id.okExport);
		textPath= findViewById(R.id.pathExport);
	}

	protected void onResume() {
		super.onResume();



		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});

		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("serviceEx", "on click");

				DatePicker datePickerStart = findViewById(R.id.datePickerStart);
				int day = datePickerStart.getDayOfMonth();
				int month = datePickerStart.getMonth();
				int year = datePickerStart.getYear();
				Calendar myCalendar = Calendar.getInstance();
				myCalendar.set(year, month, day, 0, 0, 0);
				dateStart = myCalendar.getTimeInMillis();


				DatePicker datePickerStop = findViewById(R.id.datePickerStop);
				day = datePickerStop.getDayOfMonth();
				month = datePickerStop.getMonth();
				year = datePickerStop.getYear();
				myCalendar.set(year, month, day, 23, 59, 59);
				dateStop = myCalendar.getTimeInMillis();


				localisationBDD = new BDDLocalisation(ActivityExport.this);
				localisationBDD.openForRead();

				if (localisationBDD.isEmptyBetween(dateStart, dateStop)) {
					localisationBDD.close();
				} else {
					new Thread(new Runnable() {

						public void run() {


							ArrayList<UneLigne> data = localisationBDD.getCommandeBetween(ActivityExport.this, dateStart, dateStop);
							localisationBDD.close();

							path = Environment.getExternalStorageDirectory().getPath() + "/" + getResources().getString(R.string.app_name);

							File fileD = new File(path);
							if (!fileD.exists()) {
								Log.d("export", "creation dossier");
								if (!fileD.mkdirs()) throw new AssertionError();
							}
							SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy-hh-mm");
							String currentDate = sdf1.format(new Date());
							path = path + getResources().getString(R.string.nameFile) + currentDate + ".csv";
							File fileResult = new File(path);
							String mess;

							try {
								if (!fileResult.exists()) {
									if (!fileResult.createNewFile()) throw new AssertionError("erreur création fichier");
								}

								FileOutputStream output = new FileOutputStream(fileResult, false);
								mess = "date;indication;durée attente(en min);distance (en Km)\n";
								output.write(mess.getBytes());


								for (UneLigne l : data) {
									output.write((l.toString(ActivityExport.this)+"\n").getBytes());
								}

								runOnUiThread(new Runnable() {

									public void run() {

										textPath.setText(getResources().getString(R.string.textPath)+path);

									}

								});




							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}


						}

					}).start();


				}
			}
		});
	}

}
