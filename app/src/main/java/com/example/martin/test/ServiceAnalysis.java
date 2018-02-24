package com.example.martin.test;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.util.Log;

import static com.example.martin.test.Value.DUREE_DEFAUT;
import static com.example.martin.test.Value.DUREE_MIN_SAUVEGARDE_PAS;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.IND_DEFAUT;
import static com.example.martin.test.Value.MAX_DISTANCE_ABERANT;
import static com.example.martin.test.Value.MAX_DISTANCE_DOUGLAS;
import static com.example.martin.test.Value.MIN_DISTANCE_ABERANT;
import static com.example.martin.test.Value.MIN_DISTANCE_MOYENNE;
import static com.example.martin.test.Value.MIN_DISTANCE_MOYENNE2;
import static com.example.martin.test.Value.MIN_DISTANCE_UPDATE_LOCATION;
import static com.example.martin.test.Value.MIN_TIME_UPDATE_LOCATION;
import static com.example.martin.test.Value.NUM_COL_LATITUDE_TEMP;
import static com.example.martin.test.Value.NUM_COL_LONGITUDE_TEMP;
import static com.example.martin.test.Value.NUM_COL_PRECISION_TEMP;
import static com.example.martin.test.Value.NUM_COL_TIME_TEMP;
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.rayonPetitCercle;



public class ServiceAnalysis extends IntentService {



    public ServiceAnalysis() {        super("ServiceAnalysis");     }

	long[] t;
	int[] p;
	int[] d;
	int[] idR;
	int[] ind;
	float[] x;
	float[] y;
	int i;
    int zone;
    int plateformeEnCours;
	int nbPoint = 0;
	double origineLatitude = 0;
	double origineLongitude = 0;
	long origineTime = 0;

    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("ServiceAnalysis", "debut analyse");



        //si l'enregistrment etait en cours, on l'arrete et on le redemarre a la fin de l'analyse
        Boolean isWorking = intent.getBooleanExtra("isWorkingName", false);
        if (isWorking) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Intent intentRecording = new Intent(ServiceAnalysis.this, BroadcastRecording.class);
            PendingIntent pendingRecording = PendingIntent.getBroadcast(ServiceAnalysis.this, 1989, intentRecording, PendingIntent.FLAG_UPDATE_CURRENT);
            if (myLocationManager != null) myLocationManager.removeUpdates(pendingRecording);

        }


//lecture BDD



		//temp

        BDDTemp tempBDD = new BDDTemp(this);
		tempBDD.openForRead();
        Cursor c = tempBDD.getCursor();

        nbPoint = c.getCount();
        Log.d("ServiceAnalysis", "nbPoint =" + String.valueOf(nbPoint));


        if (nbPoint < 15) {
            c.close();
			tempBDD.close();
            stopSelf();

        } else {


			//intialisation variable


			t = new long[nbPoint];
			p = new int[nbPoint];
			d = new int[nbPoint];
			ind =new int[nbPoint];
			idR =new int[nbPoint];
			x = new float[nbPoint];
			y = new float[nbPoint];
			i=0;


            c.moveToFirst();
			origineLatitude = Math.toRadians(c.getDouble(NUM_COL_LATITUDE_TEMP));
			origineLongitude = Math.toRadians(c.getDouble(NUM_COL_LONGITUDE_TEMP));
			origineTime = c.getLong(NUM_COL_TIME_TEMP);



            x[0] = 0;
            y[0] = 0;
            t[0] = 0;
            p[0] = c.getInt(NUM_COL_PRECISION_TEMP)/100;
			idR[0]=ID_RESTO_DEFAUT;
			d[0] = DUREE_DEFAUT;

            i = 1;

            for (c.moveToPosition(1); !c.isAfterLast(); c.moveToNext()) {


                t[i] = (int) (c.getLong(NUM_COL_TIME_TEMP) - origineTime);
                x[i] = (float) (RAYONTERRE * (Math.toRadians(c.getDouble(NUM_COL_LATITUDE_TEMP)) - origineLatitude));
                y[i] = (float) (rayonPetitCercle * (Math.toRadians(c.getDouble(NUM_COL_LONGITUDE_TEMP)) - origineLongitude));
                p[i] = c.getInt(NUM_COL_PRECISION_TEMP)/100;
                d[i] = DUREE_DEFAUT;
                ind[i]=IND_DEFAUT;
                idR[i]=ID_RESTO_DEFAUT;
                i++;
            }
            c.close();
            tempBDD.close();

			//zone
			origineLatitude = Math.toDegrees(origineLatitude);
			origineLongitude = Math.toDegrees(origineLongitude);

			BDDZone bddZone = new BDDZone(this);
			zone = bddZone.getIdZone(Math.toDegrees(origineLatitude), Math.toDegrees(origineLongitude));

			//plateforme

			BDDAction bddAction = new BDDAction(this);
			plateformeEnCours = bddAction.getLastPlateforme();

            //début des calculs algo

            pointAberrant();

            moyennePointProche(MIN_DISTANCE_MOYENNE, true);
			moyennePointProche(MIN_DISTANCE_MOYENNE, false);
            moyennePointProche(MIN_DISTANCE_MOYENNE2, false);

            moyennePointProche(MIN_DISTANCE_MOYENNE2, false);

           // calculPas();

          //  douglasPeucker(2, nbPoint - 1);



            //ecriture des resultats dans la bdd

            float latDeg;
            float longiDeg;
            long time;


            BDDLocalisation localisationBDD= new BDDLocalisation(this);
            localisationBDD.openForWrite();
            localisationBDD.removeAll();
			Log.d("ServiceAnalysis", "debut ecriture");
            for (i = 0; i < nbPoint - 1; i++) {


              //  if (d[i] != -1) {
                    time = (t[i] + origineTime);
                    latDeg = (float) (origineLatitude + Math.toDegrees(x[i] / RAYONTERRE));
                    longiDeg = (float) (origineLongitude + Math.toDegrees(y[i] / rayonPetitCercle));
                    localisationBDD.insertLocalisation(new Localisation(time, latDeg, longiDeg, d[i], IND_DEFAUT,idR[i]));

               // }

            }
            localisationBDD.close();


            Log.d("ServiceAnalysis", "fin ecriture");


        }
//on redemarre le service s'il est actif avant le traitement
        if (isWorking) {
            Intent intentRecording = new Intent(ServiceAnalysis.this, BroadcastRecording.class);
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            PendingIntent pendingRecording = PendingIntent.getBroadcast(ServiceAnalysis.this, 1989, intentRecording, PendingIntent.FLAG_UPDATE_CURRENT);
            myLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_UPDATE_LOCATION, MIN_DISTANCE_UPDATE_LOCATION, pendingRecording);

        }
        stopSelf();
    }

    private void pointAberrant(){



        for (i=0;i<nbPoint-3;i++){

            boolean dist = distance2(i, i + 1) > MAX_DISTANCE_ABERANT * MAX_DISTANCE_ABERANT;
            boolean dist2 = distance2(i, i + 2) < MIN_DISTANCE_ABERANT * MIN_DISTANCE_ABERANT;
            boolean dist3 = distance2(i, i + 3) < MIN_DISTANCE_ABERANT * MIN_DISTANCE_ABERANT;



            if (dist && dist2) {
                x[i + 1] = x[i];
                y[i + 1] = y[i];

            } else if (dist && dist3) {
                x[i + 1] = x[i];
                y[i + 1] = y[i];
                x[i + 2] = x[i];
                y[i + 2] = y[i];
            }
        }
    }




	// calcule la moyenne des points entre indexDebut et indexFin (non compris) pondérée par l'inverse de la précision de la mesure,
    private void moyenne (int indexDebut, int indexFin){

        if (indexFin>indexDebut){

            double moyenneX=0;
			double moyenneY=0;
            double ponderation=0.0;


            for(i=indexDebut; i<indexFin;i++){

				ponderation=1.0/p[i]+ponderation;
				moyenneX = moyenneX + x[i] / p[i];
				moyenneY = moyenneY + y[i] / p[i];
			}
			moyenneX= moyenneX/ponderation;
			moyenneY=moyenneY/ponderation;

            for(i= indexDebut; i<indexFin;i++){
                x[i]=(float) moyenneX;
                y[i]=(float) moyenneY;
            }
            for(i= indexDebut+1; i<indexFin;i++){
              //  d[i]=-1;
            }
        }
    }

    private int distanceDroite2(int indexPoint,int indexDebut, int indexFin){

        if (indexDebut<indexPoint&&indexPoint<indexFin){
            float num=(x[indexPoint]-x[indexDebut])*(y[indexFin]-y[indexDebut])-(x[indexFin]-x[indexDebut])*(y[indexPoint]-y[indexDebut]);
            float dem=(x[indexFin]-x[indexDebut])*(x[indexFin]-x[indexDebut])+(y[indexFin]-y[indexDebut])*(y[indexFin]-y[indexDebut]);
            return Math.abs((int)( num*num/dem));
        }
        else return 0;

    }

    private float distance2(int indexD, int indexF){
        return (x[indexF]-x[indexD])*(x[indexF]-x[indexD])+(y[indexF]-y[indexD])*(y[indexF]-y[indexD]);
    }

    private int nextIndex(int index){
        index++;
        while( index<nbPoint&&d[index]==-1){
            index++;
        }
        if ( index>=nbPoint)   return  nbPoint-1;
        else return  index;
    }

    private boolean moyennePointProche(int distanceMax,boolean precision){
        boolean result=false;

        //moyenne des points proches

        int indexF;
        int k=0;
        boolean conditionDistance1;
        boolean conditionFin;

        while(k<nbPoint-2) {

            indexF=k+1;

            do{
                conditionFin=indexF<nbPoint-1;

                if (precision) conditionDistance1=distance2(k,indexF)<((distanceMax+p[indexF])*(distanceMax+p[indexF]));
                else conditionDistance1=distance2(k,indexF)<(distanceMax*distanceMax);

                indexF++;


            }while(conditionDistance1&&conditionFin);


            if(indexF>k+2&&indexF<nbPoint-1){
                moyenne(k,indexF);
				k=indexF;
                result=true;
            }
			k++;
        }
        return result;
    }

    private void calculPas(){

        for(int k=0;k<nbPoint-2;k++){
            if (d[k]!=-1&&x[k+1]==x[k]) {
                d[k]=(int) (t[nextIndex(k)-1]-t[k]);
                t[k]=(t[nextIndex(k)-1]-t[k])/2;
            }
        }
    }

    private void douglasPeucker(int indexDebut, int indexFin){

        if (indexDebut+1<indexFin) {
            int dmax = 0;
            boolean pointInterressant = false;
            int index = -1;

            for (int k = indexDebut + 1; k < indexFin; k++) {


                if (d[k] > DUREE_MIN_SAUVEGARDE_PAS) {
                    index = k;
                    pointInterressant = true;
                    break; //si le point est interressant, on le sauve en arretant la boucle
                }
            }
            //si on n'a pas trouvé de point interressant, on cherche la distance max
            if (!pointInterressant) {
                for (int k = indexDebut + 1; k < indexFin; k++) {
                    int distance = distanceDroite2(k, indexDebut, indexFin);
                    if (dmax < distance && d[k] != -1) {
                        dmax = distance;
                        index = k;
                    }
                }
            }

            if (!pointInterressant && dmax < MAX_DISTANCE_DOUGLAS * MAX_DISTANCE_DOUGLAS) {

                for (int k = indexDebut + 1; k < indexFin; k++) {
                    d[k] = -1;
                }
            } else if (indexDebut < index && index < indexFin) {
                douglasPeucker(indexDebut, index);
                douglasPeucker(index, indexFin);
            }
        }
    }

    private void action(){

	}



}




