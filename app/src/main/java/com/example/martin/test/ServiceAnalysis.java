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
import static com.example.martin.test.Value.DUREE_MIN_FIN;
import static com.example.martin.test.Value.DUREE_MIN_SAUVEGARDE_PAS;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.IND_ARRET_INCONNU;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_CLIENT_CONFIRME;
import static com.example.martin.test.Value.IND_DEPLACEMENT_INCONNU;
import static com.example.martin.test.Value.IND_DEPLACEMENT_VERS_CLIENT;
import static com.example.martin.test.Value.IND_DEPLACEMENT_VERS_RESTO;
import static com.example.martin.test.Value.IND_END;
import static com.example.martin.test.Value.IND_HYPO_CLIENT;
import static com.example.martin.test.Value.IND_HYPO_RESTO;
import static com.example.martin.test.Value.IND_RESTO;
import static com.example.martin.test.Value.IND_RESTO_CONFIRME;
import static com.example.martin.test.Value.IND_START;
import static com.example.martin.test.Value.MAX_DISTANCE_ABERANT;
import static com.example.martin.test.Value.MAX_DISTANCE_DOUGLAS;
import static com.example.martin.test.Value.MIN_DISTANCE_ABERANT;
import static com.example.martin.test.Value.MIN_DISTANCE_MOYENNE;
import static com.example.martin.test.Value.MIN_DISTANCE_MOYENNE2;
import static com.example.martin.test.Value.MIN_DISTANCE_MOYENNE3;
import static com.example.martin.test.Value.MIN_DISTANCE_UPDATE_LOCATION;
import static com.example.martin.test.Value.MIN_TIME_UPDATE_LOCATION;
import static com.example.martin.test.Value.NUM_COL_LATDEG_TEMP;
import static com.example.martin.test.Value.NUM_COL_LONDEG_TEMP;
import static com.example.martin.test.Value.NUM_COL_PRECISION_TEMP;
import static com.example.martin.test.Value.NUM_COL_TIME_TEMP;
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.rayonPetitCercle;



public class ServiceAnalysis extends IntentService {



    public ServiceAnalysis() {        super("ServiceAnalysis");     }

	long[] t;
	int[] d;
	int[] p;
	float[] x;
	float[] y;
	//int i;
    int zone;
    int plateformeEnCours;
	int nbPoint = 0;
	double origineLatitude = 0;
	double origineLongitude = 0;
	long origineTime = 0;

	//changement coordonnee
	int nbPoint2=0;

	float latRad[];
	float lonRad[];
	int[] idR;
	int[] ind;
	long[] tim;
	int[] dur;

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

			x = new float[nbPoint];
			y = new float[nbPoint];






            c.moveToFirst();
			origineLatitude = Math.toRadians(c.getDouble(NUM_COL_LATDEG_TEMP));
			origineLongitude = Math.toRadians(c.getDouble(NUM_COL_LONDEG_TEMP));
			origineTime = c.getLong(NUM_COL_TIME_TEMP);



            x[0] = 0;
            y[0] = 0;
            t[0] = 0;
            p[0] = c.getInt(NUM_COL_PRECISION_TEMP)/100;

			d[0] = DUREE_DEFAUT;

            int i = 1;

            for (c.moveToPosition(1); !c.isAfterLast(); c.moveToNext()) {


                t[i] = (int) (c.getLong(NUM_COL_TIME_TEMP) - origineTime);
                x[i] = (float) (RAYONTERRE * (Math.toRadians(c.getDouble(NUM_COL_LATDEG_TEMP)) - origineLatitude));
                y[i] = (float) (rayonPetitCercle * (Math.toRadians(c.getDouble(NUM_COL_LONDEG_TEMP)) - origineLongitude));
                p[i] = c.getInt(NUM_COL_PRECISION_TEMP)/100;
                d[i] = DUREE_DEFAUT;

                i++;
            }
            c.close();
            tempBDD.close();


            //début des calculs algo

            pointAberrant();

            moyennePointProche(MIN_DISTANCE_MOYENNE, 1);

			moyennePointProche(MIN_DISTANCE_MOYENNE, 2);

            moyennePointProche(MIN_DISTANCE_MOYENNE2, 2);

            moyennePointProche(MIN_DISTANCE_MOYENNE2,2);

            calculPas();

			moyennePointProche(MIN_DISTANCE_MOYENNE3,3);

			calculPas();

            douglasPeucker(2, nbPoint - 1);

			changementCoordoonee();

            action();

            //ecriture des resultats dans la bdd


            BDDLocalisation localisationBDD= new BDDLocalisation(this);
            localisationBDD.openForWrite();
            localisationBDD.removeAll();
			Log.d("ServiceAnalysis", "debut ecriture");
            for (int k = 0; k < nbPoint2; k++) {
				if(tim[k]>0) {
					localisationBDD.insertLocalisation(new Localisation
							(tim[k], latRad[k], lonRad[k], dur[k], ind[k], idR[k]));
				}

            }
            localisationBDD.close();


            Log.d("ServiceAnalysis", "fin ecriture");


        }
//on redemarre le service s'il est actif avant le traitement
        if (isWorking) {
            Intent intentRecording = new Intent(ServiceAnalysis.this, BroadcastRecording.class);
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            PendingIntent pendingRecording = PendingIntent.getBroadcast(ServiceAnalysis.this, 1989, intentRecording, PendingIntent.FLAG_UPDATE_CURRENT);
			assert myLocationManager != null;
			myLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME_UPDATE_LOCATION, MIN_DISTANCE_UPDATE_LOCATION, pendingRecording);

        }
        stopSelf();
    }

    private void pointAberrant(){



        for (int i=0;i<nbPoint-3;i++){

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


            for(int i=indexDebut; i<indexFin;i++){

				ponderation=1.0/p[i]+ponderation;
				moyenneX = moyenneX + x[i] / p[i];
				moyenneY = moyenneY + y[i] / p[i];
			}
			moyenneX= moyenneX/ponderation;
			moyenneY=moyenneY/ponderation;

            for(int i= indexDebut; i<indexFin;i++){
                x[i]=(float) moyenneX;
                y[i]=(float) moyenneY;
            }
            for(int i= indexDebut+1; i<indexFin;i++){
                d[i]=-1;
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
    	if (index <nbPoint-1) {
			index++;
			while (index < nbPoint && d[index] == -1) {
				index++;
			}
		}
        if ( index>=nbPoint)   return  nbPoint-1;
        else return  index;
    }

    private boolean moyennePointProche(int distanceMax,int mode){
        boolean result=false;

        //moyenne des points proches

        int indexF;
        int i=0;

        while(i<nbPoint-2) {

			if(d[i]!=-1) {

				indexF = i + 1;

				while ( indexF < nbPoint - 1 && conditionDistance(mode,i,indexF,distanceMax)) {
					result = true;
					indexF++;
				}

				if (result) {
					moyenne(i, indexF);
					i = indexF - 1;
				}
			}
			i++;
        }
        return result;
    }

	private boolean conditionDistance(int mode,int indexD,int indexF,int distanceMax){
    	if(d[indexD]==-1) throw new AssertionError("erreur d["+String.valueOf(indexD)+"] = -1");

    	if (d[indexF]==-1) return true;

    	switch (mode) {
			case 1:
				return distance2(indexD, indexF) < ((distanceMax + p[indexF]) * (distanceMax + p[indexF]));

			case 3:
				return  d[indexF]> DUREE_MIN_SAUVEGARDE_PAS&& d[indexD]> DUREE_MIN_SAUVEGARDE_PAS && (distance2(indexD, indexF) < (distanceMax * distanceMax));

			default:
				return distance2(indexD, indexF) < (distanceMax * distanceMax);

		}


	}

    private void calculPas(){

        for(int i=0;i<nbPoint-1;i++){

            if (d[i]!=-1) {
                d[i]=(int) (t[nextIndex(i)]-t[i]);

				//if (d[i] > DUREE_MIN_SAUVEGARDE_PAS) {
				//	ind[i]=IND_ARRET_INCONNU;
				//}
            }
        }
    }

    private void douglasPeucker(int indexDebut, int indexFin){

        if (indexDebut+1<indexFin) {
            int dmax = 0;
            boolean pointInterressant = false;
            int index = -1;

            for (int i = indexDebut + 1; i < indexFin; i++) {


                if (d[i] > DUREE_MIN_SAUVEGARDE_PAS) {
                    index = i;
                    pointInterressant = true;
                    break; //si le point est interressant, on le sauve en arretant la boucle
                }
            }
            //si on n'a pas trouvé de point interressant, on cherche la distance max
            if (!pointInterressant) {
                for (int i = indexDebut + 1; i < indexFin; i++) {
                    int distance = distanceDroite2(i, indexDebut, indexFin);
                    if (dmax < distance && d[i] != -1) {
                        dmax = distance;
                        index = i;
                    }
                }
            }

            if (!pointInterressant && dmax < MAX_DISTANCE_DOUGLAS * MAX_DISTANCE_DOUGLAS) {

                for (int i = indexDebut + 1; i < indexFin; i++) {
                    d[i] = -1;
                }
            } else if (indexDebut < index && index < indexFin) {
                douglasPeucker(indexDebut, index);
                douglasPeucker(index, indexFin);
            }
        }
    }

    private void changementCoordoonee(){
    	nbPoint2=0;
		for(int i=0;i<nbPoint-1;i++){
			if (d[i]!=-1) nbPoint2++;
			if(t[i+1]-t[i]>DUREE_MIN_FIN) nbPoint2++;
		}
		Log.d("analyse","nbPoint2 : "+String.valueOf(nbPoint2));
		latRad=new float[nbPoint2];
		lonRad=new float[nbPoint2];
		ind =new int[nbPoint2];
		idR =new int[nbPoint2];

		tim = new long[nbPoint2];
		dur= new int[nbPoint2];
		int k=0;
		for(int i=0;i<nbPoint-1;i++) {
			if (d[i] != -1) {
				tim[k]=t[i] + origineTime;
				latRad[k] = (float) (origineLatitude + (x[i] / RAYONTERRE));
				lonRad[k] = (float) (origineLongitude + (y[i] / rayonPetitCercle));
				dur[k]=d[i];

				if(d[i]<DUREE_MIN_SAUVEGARDE_PAS) ind[k]=IND_DEPLACEMENT_INCONNU;
				else ind[k]=IND_ARRET_INCONNU;


				idR[k]=ID_RESTO_DEFAUT;
				k++;
			}

//si le pas est supérieur a DUREE_MIN_FIN, on considere que le shift 'est arreté puis redemarré
			if(k>0&&k<nbPoint2&&t[i+1]-t[i]>DUREE_MIN_FIN&&ind[k-1] != IND_END) {

				ind[k] = IND_END;
				dur[k] = 5000; //5s
				tim[k]=tim[k-1] + 5000;
				latRad[k] = (float) (origineLatitude + (x[i] / RAYONTERRE));
				lonRad[k] = (float) (origineLongitude + (y[i] / rayonPetitCercle));
				idR[k]=ID_RESTO_DEFAUT;

				if(k>0&&i>0){
					dur[k-1]=(int) (t[i]-t[i-1]);
				}

				k++;
			}
		}
		for(k=0;k<nbPoint2;k++){
			if (ind[k]==IND_END&&k<nbPoint2-1) {
				ind[k+1]=IND_START;
			}
		}

	}

    private void action(){

		//zone


		BDDZone bddZone = new BDDZone(this);
		bddZone.openForRead();
		zone = bddZone.getIdZone(origineLatitude, origineLongitude);
		bddZone.close();


		//plateforme

		BDDAction bddAction = new BDDAction(this);
		bddAction.openForRead();
		plateformeEnCours = bddAction.getLastPlateforme();

    	//recherche action utilisateur sur tout les points à l'arrêt

		for(int k=0;k<nbPoint2;k++){
			if (ind[k]==IND_ARRET_INCONNU) {

			}
		}
		bddAction.close();



		//recherche idResto sur tout les points à l'arrêt inconnu ou resto

		BDDRestaurant bddRestaurant=new BDDRestaurant(this);
		bddRestaurant.openForRead();

		for(int k=0;k<nbPoint2;k++){
			if (ind[k]==IND_ARRET_INCONNU||ind[k]==IND_RESTO||ind[k]==IND_RESTO_CONFIRME) {
				int id=bddRestaurant.getIdResto(latRad[k],lonRad[k],zone,plateformeEnCours);
				//si on trouve un resto
				if(id!=-1) {
					idR[k]=id;
					if(ind[k]==IND_ARRET_INCONNU) ind[k]=IND_HYPO_RESTO;
					Log.d("Analyse","resto trouvé!");
				}

			}
		}
		bddRestaurant.close();

		//recherche client
		int mode=0;  //1>
		int maxDuree=0;
		int indexClient=-1;
		for(int k=nbPoint2-1;k>=0;k--) {
			if(ind[k]==IND_ARRET_INCONNU||ind[k]==IND_CLIENT||ind[k]==IND_CLIENT_CONFIRME){
				if(maxDuree<dur[k]) {
					maxDuree=dur[k];
					indexClient=k;
				}
			}
			//a rajouter dernier resto?
			else if(indexClient!=-1&&ind[k]==IND_RESTO || ind[k]==IND_HYPO_RESTO || ind[k]==IND_RESTO_CONFIRME){
				ind[indexClient]=IND_HYPO_CLIENT;
				maxDuree=0;
			}
		}


		//clean petit arret
		for(int k=0;k<nbPoint2;k++) {
			if(ind[k]==IND_ARRET_INCONNU) ind[k]=IND_DEPLACEMENT_INCONNU;
		}


		//deplacement
		mode=0;   //1-> deplacement vers resto  2-> deplacement vers client
		for(int k=nbPoint2-1;k>=0;k--) {

			switch (ind[k]) {

				case IND_DEPLACEMENT_INCONNU:
					if (mode == 1) ind[k] = IND_DEPLACEMENT_VERS_RESTO;
					else if (mode == 2) ind[k] = IND_DEPLACEMENT_VERS_CLIENT;
					break;
				case IND_RESTO:
				case IND_HYPO_RESTO:
				case IND_RESTO_CONFIRME:
					mode = 1;
					break;
				case IND_CLIENT:
				case IND_HYPO_CLIENT:
				case IND_CLIENT_CONFIRME:
					mode = 2;
					break;
				default:
					mode = 0;
					break;
			}
		}



	}



}




