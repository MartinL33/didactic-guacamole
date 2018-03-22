package com.example.martin.test;


import android.content.Context;
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

/**
 * Created by martin on 16/03/18.
 */

class ListLocations {

	private List<Localisation> listeLoca;
	private ArrayList<Localisation> listeTempLoca;
	private List<Long> timePointRemove;
	private Boolean finShift=false;
	private Boolean debutShift=false;
	private Boolean boolResto=false;
	private Resto resto;
	private int nbNewPoint=0;
	private long timeLastAnalyse=0;
	private int lastIndication=0;

	ListLocations() {

		listeLoca=null;
		timePointRemove=new ArrayList<>();
	}

	Boolean hasResto() {
		return boolResto;
	}

	Resto getResto() {
		return resto;
	}

	int getLastInd() {

		if(listeLoca==null||listeLoca.size()==0) return -1;

		return lastIndication;
	}

	void addLocalisations(ArrayList<Localisation> dataTemp){
		listeTempLoca=dataTemp;
	}

	void analyse(Context context) {


		Log.d("ListLocations", "debut analyse");

		if(initialiation(context)) {

			pointAberrant();

			moyennePointProche(MIN_DISTANCE_MOYENNE, 1);

			moyennePointProche(MIN_DISTANCE_MOYENNE, 2);

			moyennePointProche(MIN_DISTANCE_MOYENNE2, 4);

			moyennePointProche(MIN_DISTANCE_MOYENNE2, 2);

			moyennePointProche(MIN_DISTANCE_MOYENNE3, 3);

			indication();

			douglasPeucker(0, listeLoca.size() - 1);

			removePoint();

			if (rechResto(context)) rechClient(context);

			ecritureBBD(context);

		}
		Log.d("ListLocations", "fin analyse");

	}

	private boolean initialiation(Context context) {



		finShift=false;
		debutShift=false;
		boolResto=false;

		if(listeTempLoca!=null&&listeTempLoca.size()>0) {

			if(listeLoca==null) {
				BDDLocalisation bddLocalisation=new BDDLocalisation(context);
				bddLocalisation.openForRead();
				listeLoca =bddLocalisation.getLastLocations();
				bddLocalisation.close();

				if(listeLoca==null) {
					debutShift = true;
					listeLoca = new ArrayList<>();

				}
			}

			if(debutShift){
				Localisation l=new Localisation();
				l.setIndication(IND_START);
				l.setDuree(1);
				l.setLatitude(listeTempLoca.get(0).getLatitude());
				l.setLongitude(listeTempLoca.get(0).getLongitude());
				l.setTime(listeTempLoca.get(0).getTime()-1);
				add(l);
				Log.d("ListLocations", "insertion debut shift1 :"+String.valueOf(l.getTime()));

			}

			listeLoca.addAll(listeTempLoca);

			nbNewPoint+=listeTempLoca.size();

		}
		listeTempLoca=null;

		if(listeLoca==null||listeLoca.size()<3) {
			Log.d("ListLocation","listeLoca.size()<3");
			return false;

		}

		Localisation l=listeLoca.get(listeLoca.size() - 1);
		if(l.getIndication()!=IND_END) {
			finShift = (System.currentTimeMillis() - l.getDuree() - l.getTime()) > DUREE_MIN_FIN;
		}

		if (nbNewPoint < 10&&!finShift ) {

			Log.d("ListLocations", "nbNewPoint "+String.valueOf(nbNewPoint)+" <10");
			return false;
		}

		if(finShift){
			l=new Localisation();
			l.setIndication(IND_END);
			l.setDuree(1);
			l.setLatitude(listeLoca.get(listeLoca.size() -1 ).getLatitude());
			l.setLongitude(listeLoca.get(listeLoca.size() -1 ).getLongitude());
			l.setTime(listeLoca.get(listeLoca.size() - 1).getTime() + 1);
			add(l);
			Log.d("ListLocations", "insertion fin shift :"+String.valueOf(l.getTime()));
		}

		for (int i =  0; i < listeLoca.size() - 1; i++) {

//si le pas est supérieur a DUREE_MIN_FIN, on considere que le shift 'est arreté puis redemarré

			if (listeLoca.get(i + 1).getTime() - listeLoca.get(i).getTime()-listeLoca.get(i).getDuree() > DUREE_MIN_FIN) {
				Localisation l1=listeLoca.get(i);
				Localisation l2=listeLoca.get(i+1);
				if(l1.getIndication()!=IND_END) {
					l = new Localisation();
					l.setIndication(IND_END);
					l.setDuree(1);
					l.setLatitude(l1.getLatitude());
					l.setLongitude(l1.getLongitude());
					l.setTime(l1.getTime() + 1);
					listeLoca.add(i+1,l);
					Log.d("ListLocations", "insertion fin shift2 :"+String.valueOf(l.getTime()));
					i++;
				}

				if(l2.getIndication()!=IND_START) {
					l = new Localisation();
					l.setIndication(IND_START);
					l.setDuree(1);
					l.setLatitude(l2.getLatitude());
					l.setLongitude(l2.getLongitude());
					l.setTime(l2.getTime() - 1);
					listeLoca.add(i+1,l);
					Log.d("ListLocations", "insertion debut shift2 :"+String.valueOf(l.getTime()));
					i++;
					debutShift=true;
				}

			}
		}
		Log.d("ListLocations", "fin initialisation");

		for(Localisation l3:listeLoca) {
			Log.d("listeLoca",l3.toString());
		}
		return true;
	}

	private void pointAberrant(){

		if(listeLoca.size()>3) {
			for (int k = 0; k < listeLoca.size() - 3; k++) {

				boolean dist = distance2(k, k + 1) > MAX_DISTANCE_ABERANT * MAX_DISTANCE_ABERANT;
				boolean dist2 = distance2(k, k + 2) < MIN_DISTANCE_ABERANT * MIN_DISTANCE_ABERANT;
				boolean dist3 = distance2(k, k + 3) < MIN_DISTANCE_ABERANT * MIN_DISTANCE_ABERANT;
				boolean ind=listeLoca.get(k+1).getIndication()==IND_DEFAUT;
				boolean ind2=listeLoca.get(k+2).getIndication()==IND_DEFAUT;

				if (dist && dist3&&ind&&ind2) {
					listeLoca.get(k + 1).fixPosition(listeLoca.get(k));
					listeLoca.get(k + 2).fixPosition(listeLoca.get(k));
				}

				else if (dist && dist2&&ind) {
					listeLoca.get(k + 1).fixPosition(listeLoca.get(k));
				}
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


		//calcul pas
		for(int i=0;i<listeLoca.size()-1;i++){
			if(listeLoca.get(i).getIndication()<IND_START) {
				listeLoca.get(i).setDuree((int) (listeLoca.get(i + 1).getTime() - listeLoca.get(i).getTime()));
			}
		}

		for(Localisation l: listeLoca) {

			if(l.getIndication()==IND_DEFAUT||l.getIndication()==IND_DEPLACEMENT_INCONNU) {

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
			Localisation l=listeLoca.get(i);
			if(l.getIndication()==-1) {
				if(l.getTime()<=timeLastAnalyse) {
					timePointRemove.add(l.getTime());
				}
				listeLoca.remove(i);
				i--;
			}
		}


	}

	//resto a faire
	private boolean rechResto(Context context) {
		boolean result=false;

		//zone
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		int zone = preferences.getInt("zone", 1);
		Log.d("ListLocations", "zone : " + String.valueOf(zone));

		//plateforme
		BDDAction bddAction = new BDDAction(context);
		bddAction.openForRead();
		int plateformeEnCours = bddAction.getLastPlateforme();
		Log.d("ListLocations", "plateformeEncours : " + String.valueOf(plateformeEnCours));

		//recherche action utilisateur sur tout les points à l'arrêt

		for (Localisation l : listeLoca) {

			if (l.getIndication() >= IND_ARRET_INCONNU) {
				int ind = bddAction.getIndicationBeetween(l.getTime(), l.getTime() + l.getDuree());
				if (ind == IND_RESTO || ind == IND_CLIENT || ind == IND_ATTENTE){
					l.setIndication(ind);
					if(ind == IND_RESTO){
						int idResto=bddAction.getIdRestoBeetween(l.getTime(), l.getTime() + l.getDuree());
						if(idResto>0) l.setIdResto(idResto);
					}
				}

			}

		}
		bddAction.close();

		//recherche idResto sur tout les points à l'arrêt inconnu ou resto
		BDDRestaurant bddRestaurant = new BDDRestaurant(context);
		bddRestaurant.openForRead();


		for (Localisation l : listeLoca) {
			int ind = l.getIndication();
			int dur = l.getDuree();
			if (dur > DUREE_MIN_RESTO && (ind == IND_ARRET_INCONNU || ind == IND_RESTO || ind == IND_RESTO_CONFIRME)) {

				resto = bddRestaurant.getBestResto(l, zone, plateformeEnCours);
				//si on trouve un resto
				if (resto != null) {

					l.setIdResto(resto.getId());
					if (ind == IND_ARRET_INCONNU) {
						result=true;
						boolResto=true;
						l.setIndication(IND_HYPO_RESTO);
						Log.d("Analyse", "resto trouvé!");
					}
				}
			}
			if(l.getIndication()==IND_END){
				result=true;
			}
		}

		bddRestaurant.close();
		return  result;
	}

	private void rechClient(Context context){

		Log.d("Analyse", "recherche client");
		//get last location

		BDDLocalisation bddLocalisation=new BDDLocalisation(context);
		bddLocalisation.openForRead();
		ArrayList<Localisation> arrayTemp =bddLocalisation.getLocalisationsInconnues(listeLoca.get(0).getTime()-1);
		bddLocalisation.close();
		if(arrayTemp!=null){
			listeLoca.addAll(0,arrayTemp);
		}

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




		//set indication deplacement
		int mode=0;   //1-> deplacement vers resto  2-> deplacement vers client
//parcours de la liste à l'envers
		ListIterator<Localisation> iterator = listeLoca.listIterator(listeLoca.size()); // On précise la position initiale de l'iterator. Ici on le place à la fin de la liste
		while(iterator.hasPrevious()){

			Localisation l = iterator.previous();
			int ind=l.getIndication();
			switch (ind) {

				case IND_DEPLACEMENT_INCONNU:
				case IND_ARRET_INCONNU:
					if (mode == 1) l.setIndication(IND_DEPLACEMENT_VERS_RESTO);
					else if (mode == 2) l.setIndication(IND_DEPLACEMENT_VERS_CLIENT);
					else if (mode == 3) l.setIndication(IND_ATTENTE);
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

				case IND_END:
					mode=3;
					break;
				default:
					mode = 0;
					break;
			}
		}
	}

	private void ecritureBBD(Context context){
		//ecriture des resultats dans la bdd
		BDDLocalisation bddLocalisation=new BDDLocalisation(context);
		bddLocalisation.openForWrite();

		Log.d("ListLocations", "debut ecriture, nb Point= "+String.valueOf(listeLoca.size()));

		for (Localisation l: listeLoca) {
			bddLocalisation.replaceLocalisation(l);
		}
		if(timePointRemove.size()>0) {
			for (long time : timePointRemove) {
				bddLocalisation.removeLocalisation(time);
			}

		}
		bddLocalisation.close();


		nbNewPoint=0;
		int debut=listeLoca.size()-10;
		if(debut<0) debut=0;
		listeLoca= listeLoca.subList(debut,listeLoca.size());
		timeLastAnalyse=listeLoca.get(listeLoca.size()-1).getTime();
		timePointRemove.clear();

		if(boolResto) lastIndication=IND_HYPO_RESTO;
		else if(debutShift) lastIndication=IND_START;
		else if(finShift) lastIndication=IND_END;
		else lastIndication=listeLoca.get(listeLoca.size()-1).getIndication();

		Log.d("ListLocations", "fin ecriture");

		for(Localisation l:listeLoca) {
			Log.d("ListLoca",l.toString());
		}


	}

	private void add(Localisation l1){
		if(listeLoca.size()>0) {
			if (l1.compareTo(listeLoca.get(listeLoca.size() - 1)) > 0) listeLoca.add(l1);
			else {
				int index=0;
				for (Localisation l2 : listeLoca) {
					if (l1.compareTo(l2) < 0) {
						listeLoca.add(index,l1);
						break;
					}
					index++;
				}

			}
		}
		else{
			listeLoca.add(l1);
		}
	}

}


