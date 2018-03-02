package com.example.martin.test;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.example.martin.test.Value.DUREE_DEFAUT;
import static com.example.martin.test.Value.DUREE_MIN_FIN;
import static com.example.martin.test.Value.DUREE_MIN_SAUVEGARDE_PAS;
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
import static com.example.martin.test.Value.NUM_COL_LATDEG_TEMP;
import static com.example.martin.test.Value.NUM_COL_LONDEG_TEMP;
import static com.example.martin.test.Value.NUM_COL_PRECISION_TEMP;
import static com.example.martin.test.Value.NUM_COL_TIME_TEMP;
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.distence2;
import static com.example.martin.test.Value.rayonPetitCercle;



public class ServiceAnalysis extends IntentService {



    public ServiceAnalysis() {        super("ServiceAnalysis");     }

	private long[] t;
	private int[] d;
	private int[] p;
	private float[] x;
	private float[] y;
	private int nbPoint = 0;
	private double origineLatitude = 0;
	private double origineLongitude = 0;
	private long origineTime = 0;

	//changement coordonnee

	private List<Localisation> listeLoca=new ArrayList<>();

	private int ind;
	private boolean firstLocation=true; //Est-ce le premier point?
	private Localisation firstLocalisation;



    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("ServiceAnalysis", "debut analyse");


/*
        //si l'enregistrment etait en cours, on l'arrete et on le redemarre a la fin de l'analyse
        Boolean isWorking = intent.getBooleanExtra("isWorkingName", false);
        if (isWorking) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Intent intentRecording = new Intent(ServiceAnalysis.this, BroadcastRecording.class);
            PendingIntent pendingRecording = PendingIntent.getBroadcast(ServiceAnalysis.this, 1989, intentRecording, PendingIntent.FLAG_UPDATE_CURRENT);
            if (myLocationManager != null) myLocationManager.removeUpdates(pendingRecording);

        }*/


//lecture BDD


//localisation : lecture du dernier point

		BDDLocalisation localisationBDD=new BDDLocalisation(this);
		localisationBDD.openForRead();
		firstLocation=localisationBDD.isEmpty();
		if(!firstLocation) firstLocalisation=localisationBDD.getLastLocation();
		localisationBDD.close();

		firstLocation=true;  //a supprimer

		Log.d("ServiceAnalysis", "firstLocation: "+String.valueOf(firstLocation));
		//temp
		Log.d("ServiceAnalysis", "debut lecture tempBDD");
        BDDTemp tempBDD = new BDDTemp(this);
		tempBDD.openForRead();
        Cursor c = tempBDD.getCursor();
        nbPoint = c.getCount();

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

			//initialisation petit cercle:
			if(rayonPetitCercle==0) {
				distence2(origineLatitude,origineLatitude,0,1);
			}

			int i = 1;
			if(firstLocation){
				p[0] = c.getInt(NUM_COL_PRECISION_TEMP);
				d[0] = DUREE_DEFAUT;
				x[0] = 0;
				y[0] = 0;
				t[0] = 0;


			} else{

			//pour i=0  FirstLocalisation
				p[0] = 5;
				d[0] = firstLocalisation.getDuree()*1000;
				x[0] = (float) (RAYONTERRE * firstLocalisation.getLatitude() - origineLatitude);
				y[0] = (float) (rayonPetitCercle * firstLocalisation.getLongitude() - origineLongitude);
				t[0] = (int) (firstLocalisation.getTime() - origineTime);


				//pour i=1  :
				x[1] = 0;
				y[1] = 0;
				t[1] = 0;
				p[1] = c.getInt(NUM_COL_PRECISION_TEMP);
				d[1] = DUREE_DEFAUT;
				i=2;
			}

            for (c.moveToPosition(1); !c.isAfterLast(); c.moveToNext()) {
                t[i] = (int) (c.getLong(NUM_COL_TIME_TEMP) - origineTime);
                x[i] = (float) (RAYONTERRE * (Math.toRadians(c.getDouble(NUM_COL_LATDEG_TEMP)) - origineLatitude));
                y[i] = (float) (rayonPetitCercle * (Math.toRadians(c.getDouble(NUM_COL_LONDEG_TEMP)) - origineLongitude));
                p[i] = c.getInt(NUM_COL_PRECISION_TEMP);
                d[i] = DUREE_DEFAUT;
                i++;
            }
            c.close();
            tempBDD.close();
			Log.d("ServiceAnalysis", "fin lecture tempBDD");





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

            localisationBDD.openForWrite();
            localisationBDD.removeAll();
			Log.d("ServiceAnalysis", "debut ecriture");
            for (Localisation l: listeLoca) {
				if(l.getTime()>0) {
					localisationBDD.insertLocalisation(l);
				}
            }
            localisationBDD.close();


            Log.d("ServiceAnalysis", "fin ecriture");


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

		int k;

		long tim;
		float lonRad;
		float latRad;
		if(firstLocation) {
			tim =t[0] + origineTime-1;
			latRad = (float) (origineLatitude);
			lonRad = (float) (origineLongitude);
			listeLoca.add(new Localisation(tim, latRad, lonRad,IND_START));
		}

		int indPrecedante=0;

		for(int i=0;i<nbPoint-1;i++) {
			if (d[i] != -1) {
				tim =t[i] + origineTime;
				latRad = (float) (origineLatitude + (x[i] / RAYONTERRE));
				lonRad = (float) (origineLongitude + (y[i] / rayonPetitCercle));

				if(d[i]<DUREE_MIN_SAUVEGARDE_PAS) ind=IND_DEPLACEMENT_INCONNU;
				else ind=IND_ARRET_INCONNU;

				int dur = d[i] / 1000;
				listeLoca.add(new Localisation(tim, latRad, lonRad,ind, dur));

			}

//si le pas est supérieur a DUREE_MIN_FIN, on considere que le shift 'est arreté puis redemarré
			if(t[i+1]-t[i]>DUREE_MIN_FIN&&indPrecedante != IND_END) {

				tim = t[i]+ 1000+ origineTime;
				latRad = (float) (origineLatitude + (x[i] / RAYONTERRE));
				lonRad = (float) (origineLongitude + (y[i] / rayonPetitCercle));

				if(i>0){
					//dur[k-1]=(int) (t[i]-t[i-1]);
					listeLoca.get(listeLoca.size()-1).setDuree((int)((t[i]-t[i-1]))/1000);
				}
				listeLoca.add(new Localisation(tim, latRad, lonRad,IND_END));
			}
			indPrecedante=ind;

		}
		k=0;
		for (Localisation l: listeLoca) {
			if (l.getIndication()==IND_END&&k<listeLoca.size()-1) {
				listeLoca.get(k+1).setIndication(IND_START);
			}
			k++;
		}

		if(listeLoca.get(listeLoca.size()-1).getIndication()!=IND_END){
			Localisation l =listeLoca.get(listeLoca.size()-1);
			tim =l.getTime()+1;
			latRad = l.getLatitude();
			lonRad = l.getLongitude();
			listeLoca.add(new Localisation(tim, latRad, lonRad,IND_END));
		}

		//liberation des tableaux initiaux
		t = null;
		p = null;
		d = null;
		x = null;
		y = null;

	}

    private void action(){

		//zone


		BDDZone bddZone = new BDDZone(this);
		bddZone.openForRead();
		int zone = bddZone.getIdZone(origineLatitude, origineLongitude);
		bddZone.close();
		Log.d("analyse","zone : "+String.valueOf(zone));

		//plateforme

		BDDAction bddAction = new BDDAction(this);
		bddAction.openForRead();
		int plateformeEnCours = bddAction.getLastPlateforme();
		Log.d("analyse","plateformeEncours : "+String.valueOf(plateformeEnCours));

    	//recherche action utilisateur sur tout les points à l'arrêt
		int k=0;
		for (Localisation l: listeLoca) {

			if(l.getIndication()==IND_ARRET_INCONNU){

			}
			k++;
		}


		bddAction.close();



		//recherche idResto sur tout les points à l'arrêt inconnu ou resto

		BDDRestaurant bddRestaurant=new BDDRestaurant(this);
		bddRestaurant.openForRead();


		for (Localisation l: listeLoca) {
			int ind=l.getIndication();
			if(ind==IND_ARRET_INCONNU||ind==IND_RESTO||ind==IND_RESTO_CONFIRME){
				int id=bddRestaurant.getIdResto(l.getLatitude(),l.getLongitude(), zone, plateformeEnCours);
				//si on trouve un resto
				if(id!=-1) {
					l.setIdResto(id);
					if(ind==IND_ARRET_INCONNU) l.setIndication(IND_HYPO_RESTO);
					Log.d("Analyse","resto trouvé!");

				}

			}

		}

		bddRestaurant.close();

		//recherche client
		boolean hasCmd=false;
		int maxDuree=0;
		int indexClient=-1;
		k=0;
		for (Localisation l: listeLoca) {
			int ind=l.getIndication();

			if(hasCmd&&(ind==IND_ARRET_INCONNU||ind==IND_CLIENT||ind==IND_CLIENT_CONFIRME)){
				if(maxDuree<l.getDuree()) {
					maxDuree=l.getDuree();
					indexClient=k;
				}
			}
			else if(ind==IND_RESTO || ind==IND_HYPO_RESTO || ind==IND_RESTO_CONFIRME){
				if(indexClient!=-1) listeLoca.get(indexClient).setIndication(IND_HYPO_CLIENT);
				maxDuree=0;
				indexClient=-1;
				hasCmd=true;
			}
			else if(ind==IND_END){
				if(indexClient!=-1) listeLoca.get(indexClient).setIndication(IND_HYPO_CLIENT);
				maxDuree=0;
				indexClient=-1;
				hasCmd=false;
			}
		k++;
		}


		//clean petit arret
		for (Localisation l: listeLoca) {
			if(l.getIndication()==IND_ARRET_INCONNU) l.setIndication(IND_DEPLACEMENT_INCONNU);
		}

		//deplacement
		int mode=0;   //1-> deplacement vers resto  2-> deplacement vers client

		ListIterator<Localisation> iterator = listeLoca.listIterator(listeLoca.size()); // On précise la position initiale de l'iterator. Ici on le place à la fin de la liste
		while(iterator.hasPrevious()){
			Localisation l = iterator.previous();
			ind=l.getIndication();
			switch (ind) {

				case IND_DEPLACEMENT_INCONNU:
					if (mode == 1) l.setIndication(IND_DEPLACEMENT_VERS_RESTO);
					else if (mode == 2) l.setIndication(IND_DEPLACEMENT_VERS_CLIENT);
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


			// ...
		}





	}



}




