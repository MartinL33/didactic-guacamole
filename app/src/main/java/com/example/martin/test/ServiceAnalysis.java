package com.example.martin.test;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.example.martin.test.Value.DUREE_MIN_FIN;
import static com.example.martin.test.Value.DUREE_MIN_RESTO;
import static com.example.martin.test.Value.DUREE_MIN_SAUVEGARDE_PAS;
import static com.example.martin.test.Value.IND_ARRET_INCONNU;
import static com.example.martin.test.Value.IND_ATTENTE;
import static com.example.martin.test.Value.IND_CLIENT;
import static com.example.martin.test.Value.IND_CLIENT_CONFIRME;
import static com.example.martin.test.Value.IND_DEFAUT;
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
import static com.example.martin.test.Value.RAYONTERRE;
import static com.example.martin.test.Value.rayonPetitCercle;

public class ServiceAnalysis extends IntentService {

	private List<Localisation> listeLoca=new ArrayList<>();
	private List<Localisation> listeInsertLoca=new ArrayList<>();
	private BDDLocalisation localisationBDD;


	Boolean finShift=false;
	Boolean debutShift=false;
	Boolean resto=false;

	public ServiceAnalysis() {        super("ServiceAnalysis");     }



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



		if(initialiation()) {

			pointAberrant();

			moyennePointProche(MIN_DISTANCE_MOYENNE, 1);

			moyennePointProche(MIN_DISTANCE_MOYENNE, 2);

			moyennePointProche(MIN_DISTANCE_MOYENNE2, 4);

			moyennePointProche(MIN_DISTANCE_MOYENNE2, 2);

			moyennePointProche(MIN_DISTANCE_MOYENNE3, 3);

			indication();

			douglasPeucker(0, listeLoca.size() - 1);

			removePoint();

			if (rechResto()) rechClient();

			ecritureBBD();
		}

		stopSelf();
	}

	private boolean initialiation() {


		boolean res=false;
//lecture BDD

//localisation : lecture des deux derniers

		localisationBDD = new BDDLocalisation(this);
		localisationBDD.openForRead();

		listeLoca=localisationBDD.getLastLocations();
		localisationBDD.close();

		if(listeLoca==null) {
			debutShift=true;
			listeLoca=new ArrayList<>();
		}else if(listeLoca.size()<=2){
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			float precisionlastPointAnalysed = preferences.getFloat("precisionLastPointAnalysed", 1);
			listeLoca.get(listeLoca.size()-1).setPrecision(precisionlastPointAnalysed);
		}

		else throw new AssertionError("localisationBDD.getLastLocations contain more than deux localisations");
		//temp
		Log.d("ServiceAnalysis", "debut lecture tempBDD");
		BDDTemp tempBDD = new BDDTemp(this);
		tempBDD.openForRead();
		List<Localisation> listeTempLoca=tempBDD.getAllLocalisation(this);
		tempBDD.close();
		if(listeTempLoca!=null) {

			if(!listeLoca.isEmpty()&&!debutShift){
				debutShift=(listeTempLoca.get(0).getTime() - listeLoca.get(listeLoca.size() - 1).getTime() > DUREE_MIN_FIN);

			}
			if(debutShift){
				Localisation l;
				l = listeTempLoca.get(0);
				l.setIndication(IND_START);
				l.setDuree(1);
				l.setTime(l.getTime() - 1);
				listeLoca.add(l);
			}
			listeLoca.addAll(listeTempLoca);

		}


		if(!listeLoca.isEmpty()) {
			finShift = (System.currentTimeMillis() - listeLoca.get(listeLoca.size() - 1).getTime() > DUREE_MIN_FIN);
			if(finShift){
				Localisation l;
				l=listeLoca.get(listeLoca.size() - 1);
				l.setIndication(IND_END);
				l.setDuree(1);
				l.setTime(l.getTime() + 1);
				listeLoca.add(l);
			}


		}
		listeTempLoca=null;

		if (listeLoca.size() < 15 && !finShift) {


			Log.d("ServiceAnalysis", "fin nbPoint<15");

			stopSelf();
			onDestroy();

			Log.d("ServiceAnalysis", "appel stopSelf() raté");

		} else {

			res = true;
			Log.d("ServiceAnalysis", "fin lecture tempBDD");

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);



			SharedPreferences.Editor editor=preferences.edit();
			editor.putFloat("precisionLastPointAnalysed",listeLoca.get(listeLoca.size()-1).getPrecision());


			long lastPointAnalysed = listeLoca.get(listeLoca.size() - 1).getTime();

			editor.putLong("lastPointAnalysed", lastPointAnalysed);
			editor.apply();


			Localisation l;
			for (int i =  1; i < listeLoca.size() - 1; i++) {

//si le pas est supérieur a DUREE_MIN_FIN, on considere que le shift 'est arreté puis redemarré

				if (listeLoca.get(i + 1).getTime() - listeLoca.get(i).getTime() > DUREE_MIN_FIN) {

					l = listeLoca.get(i);
					l.setIndication(IND_END);
					l.setDuree(1);
					l.setTime(l.getTime() + 1);
					listeLoca.add(i,l);

					l = listeLoca.get(i + 1);
					l.setIndication(IND_START);
					l.setDuree(1);
					l.setTime(l.getTime() - 1);
					listeInsertLoca.add(i+1,l);
					i+=2;
				}
			}





		}

		return res;

	}


	private void pointAberrant(){

		for (int k=0;k<listeLoca.size()-3;k++){



			boolean dist = distance2(k, k+1) > MAX_DISTANCE_ABERANT * MAX_DISTANCE_ABERANT;
			boolean dist2 = distance2(k, k+2) < MIN_DISTANCE_ABERANT * MIN_DISTANCE_ABERANT;
			boolean dist3 = distance2(k, k+3) < MIN_DISTANCE_ABERANT * MIN_DISTANCE_ABERANT;

			if (dist && dist2) {
				listeLoca.get(k+1).fixPosition(listeLoca.get(k));


			} else if (dist && dist3) {
				listeLoca.get(k+1).fixPosition(listeLoca.get(k));
				listeLoca.get(k+2).fixPosition(listeLoca.get(k));
			}
		}
	}

	// calcule la moyenne des points entre indexDebut et indexFin (non compris) pondérée par l'inverse de la précision de la mesure,
	private void moyenne (int indexDebut, int indexFin){

		if (indexFin>indexDebut+1){

			double moyenneLat=0;
			double moyenneLon=0;
			double ponderation=0.0;


			for(int i=indexDebut; i<indexFin;i++){

				double p=listeLoca.get(i).getPrecision();
				double lat=listeLoca.get(i).getLatitude();
				double lon=listeLoca.get(i).getLongitude();

				ponderation=(1.0/p)+ponderation;
				moyenneLat = moyenneLat + (lat / p);
				moyenneLon = moyenneLon + (lon / p);
			}
			moyenneLat= moyenneLat/ponderation;
			moyenneLon=moyenneLon/ponderation;

			Localisation l =listeLoca.get(indexDebut);
			l.setLatitude((float) moyenneLat);
			l.setLongitude((float) moyenneLon);
			l.setPrecision((float) (1.0/ponderation));
			l.setDuree((int) (listeLoca.get(indexFin-1).getTime()-l.getTime()));


			for(int i= indexDebut+1; i<indexFin;i++){
				listeLoca.get(i).setIndication(-1);
			}
		}
	}

	private int distance2(int indexD, int indexF) {
		Localisation lD=listeLoca.get(indexD);
		Localisation lF=listeLoca.get(indexF);
		return Value.distence2(lD.getLatitude(),lF.getLatitude(),
				lD.getLongitude(),lF.getLongitude());

	}

	private boolean moyennePointProche(int distanceMax,int mode){
		boolean result=false;

		//moyenne des points proches

		int indexF;

		for(int i=0;i<listeLoca.size()-1;i++) {
			indexF = i + 1;
			while ((indexF < listeLoca.size())  && conditionDistance(mode, i, indexF, distanceMax)) {
				result = true;
				indexF++;
			}

			if (result) {
				moyenne(i, indexF);
				i = indexF - 1;
			}
		}

		if(result){
			removePoint();
		}

		return result;
	}

	private boolean conditionDistance(int mode,int indexD,int indexF,int distanceMax){
		if(listeLoca.get(indexF).getIndication()>=IND_START
				||listeLoca.get(indexD).getIndication()>=IND_START) return false;

		switch (mode) {
			case 1:
				return distance2(indexD, indexF) < ((distanceMax + listeLoca.get(indexF).getPrecision()) * (distanceMax + listeLoca.get(indexF).getPrecision()));

			case 3:
				return  listeLoca.get(indexF).getDuree()> DUREE_MIN_SAUVEGARDE_PAS&& listeLoca.get(indexD).getDuree()> DUREE_MIN_SAUVEGARDE_PAS && (distance2(indexD, indexF) < (distanceMax * distanceMax));

			case 4: return (indexF<listeLoca.size()-2) && listeLoca.get(indexF).getDuree()<DUREE_MIN_SAUVEGARDE_PAS&&distance2(indexD,indexF+1)<(distanceMax * distanceMax);

			default:
				return distance2(indexD, indexF) < (distanceMax * distanceMax);
		}
	}

	private void indication(){

/*
		//calcul pas
		for(int i=0;i<listeLoca.size()-1;i++){
			if(listeLoca.get(i).getDuree()==DUREE_DEFAUT) {
				listeLoca.get(i).setDuree((int) (listeLoca.get(i + 1).getTime() - listeLoca.get(i).getTime()));
			}
		}
*/
		for(Localisation l: listeLoca) {

			if(l.getIndication()==IND_DEFAUT) {

				if (l.getDuree() < DUREE_MIN_SAUVEGARDE_PAS) l.setIndication(IND_DEPLACEMENT_INCONNU);
				else l.setIndication(IND_ARRET_INCONNU) ;
			}
		}
	}

	private int distanceDroite2(int indexPoint,int indexDebut, int indexFin){

		if (indexDebut<indexPoint&&indexPoint<indexFin){
			Localisation Ld=listeLoca.get(indexDebut);
			Localisation Lf=listeLoca.get(indexFin);
			Localisation Lp=listeLoca.get(indexPoint);

			if(rayonPetitCercle==0) rayonPetitCercle = (int) (RAYONTERRE * Math.cos(Lp.getLatitude()));

			float xDF=rayonPetitCercle*(Lf.getLongitude()-Ld.getLongitude());
			float yDF=RAYONTERRE*(Lf.getLatitude()-Ld.getLatitude());

			float xDP=rayonPetitCercle*(Lp.getLongitude()-Ld.getLongitude());
			float yDP=RAYONTERRE*(Lp.getLatitude()-Ld.getLatitude());

			return (int)( (xDP*yDF-yDP*xDF)*(xDP*yDF-yDP*xDF)/(xDF*xDF+yDF*yDF));
		}
		else return 0;

	}

	private void douglasPeucker(int indexDebut, int indexFin){

		if (indexDebut+1<indexFin) {
			int dmax = 0;
			boolean pointInterressant = false;
			int index = -1;

			for (int i = indexDebut + 1; i < indexFin; i++) {

				if (listeLoca.get(i).getIndication()!=IND_DEPLACEMENT_INCONNU) {
					index = i;
					pointInterressant = true;
					break; //si le point est interressant, on le sauve en arretant la boucle
				}
			}
			//si on n'a pas trouvé de point interressant, on cherche la distance max
			if (!pointInterressant) {
				for (int i = indexDebut + 1; i < indexFin; i++) {
					int distance = distanceDroite2(i, indexDebut, indexFin);
					if (dmax < distance) {
						dmax = distance;
						index = i;
					}
				}
			}

			if (!pointInterressant && dmax < MAX_DISTANCE_DOUGLAS * MAX_DISTANCE_DOUGLAS) {

				for (int i = indexDebut + 1; i < indexFin; i++) {
					listeLoca.get(i).setIndication(-1);
				}
			}
			else if (indexDebut < index && index < indexFin) {
				douglasPeucker(indexDebut, index);
				douglasPeucker(index, indexFin);
			}
		}
	}

	private void removePoint(){

		for(int i=0;i<listeLoca.size();i++){
			if(listeLoca.get(i).getIndication()==-1) {
				listeLoca.remove(i);
				i--;
			}
		}


	}

	private boolean rechResto() {
		boolean result=false;
		//zone

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int zone = preferences.getInt("zone", 1);


		Log.d("ServiceAnalysis", "zone : " + String.valueOf(zone));

		//plateforme

		BDDAction bddAction = new BDDAction(this);
		bddAction.openForRead();
		int plateformeEnCours = bddAction.getLastPlateforme();
		Log.d("ServiceAnalysis", "plateformeEncours : " + String.valueOf(plateformeEnCours));

		//recherche action utilisateur sur tout les points à l'arrêt

		for (Localisation l : listeLoca) {

			if (l.getIndication() == IND_ARRET_INCONNU) {
				int ind = bddAction.getIndicationBeetween(l.getTime(), l.getTime() + l.getDuree());
				if (ind == IND_RESTO || ind == IND_CLIENT || ind == IND_ATTENTE)
					l.setIndication(ind);
			}

		}
		bddAction.close();

		//recherche idResto sur tout les points à l'arrêt inconnu ou resto

		BDDRestaurant bddRestaurant = new BDDRestaurant(this);
		bddRestaurant.openForRead();


		for (Localisation l : listeLoca) {
			int ind = l.getIndication();
			int dur = l.getDuree();
			if (dur > DUREE_MIN_RESTO && (ind == IND_ARRET_INCONNU || ind == IND_RESTO || ind == IND_RESTO_CONFIRME)) {
				int id = bddRestaurant.getIdResto(l.getLatitude(), l.getLongitude(), zone, plateformeEnCours);
				//si on trouve un resto
				if (id != -1) {

					l.setIdResto(id);
					if (ind == IND_ARRET_INCONNU) {
						result=true;
						resto=true;
						l.setIndication(IND_HYPO_RESTO);
						Log.d("Analyse", "resto trouvé!");
					}
				}

			}

		}

		bddRestaurant.close();
		if(finShift) result=true;
		return  result;
	}

	private void rechClient(){
		//recherche client
		boolean hasCmd=false;
		int maxDuree=0;
		int indexClient=-1;
		int k=0;
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

		//set indication deplacement
		int mode=0;   //1-> deplacement vers resto  2-> deplacement vers client
//parcours de la liste à l'envers
		ListIterator<Localisation> iterator = listeLoca.listIterator(listeLoca.size()); // On précise la position initiale de l'iterator. Ici on le place à la fin de la liste
		while(iterator.hasPrevious()){

			Localisation l = iterator.previous();
			int ind=l.getIndication();
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
		}
	}

	private void ecritureBBD(){
		//ecriture des resultats dans la bdd

		localisationBDD.openForWrite();

		Log.d("ServiceAnalysis", "debut ecriture");

		for (Localisation l: listeLoca) {
			localisationBDD.replaceLocalisation(l);
		}

		localisationBDD.close();

//	tempBDD.openForWrite();
//	tempBDD.removeTempExceptLast();
//	tempBDD.close();
		Log.d("ServiceAnalysis", "fin ecriture");

		listeLoca=null;
		listeInsertLoca=null;




	}

}




