package com.example.martin.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.martin.test.Value.COL_IDBASE_RESTO;
import static com.example.martin.test.Value.COL_ID_RESTO;
import static com.example.martin.test.Value.COL_LATRAD_RESTO;
import static com.example.martin.test.Value.COL_LONRAD_RESTO;
import static com.example.martin.test.Value.COL_PLATEFORME_RESTO;
import static com.example.martin.test.Value.COL_TEXT_RESTO;
import static com.example.martin.test.Value.COL_ZONE_RESTO;
import static com.example.martin.test.Value.ID_RESTO_DEFAUT;
import static com.example.martin.test.Value.TABLE_RESTO;

/**
 * Created by martin on 20/02/18.
 */

public class BaseSQLiteRestaurant extends SQLiteOpenHelper {

	private static final String CREATE_BDD="CREATE TABLE " + TABLE_RESTO + " (" +
			COL_ID_RESTO + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_LATRAD_RESTO + " REAL, "+
			COL_LONRAD_RESTO + " REAL, "+ COL_TEXT_RESTO + "  TEXT, "+COL_ZONE_RESTO+ " INTEGER, "+
			COL_PLATEFORME_RESTO+ " INTEGER, "+COL_IDBASE_RESTO+ " INTEGER);";


	public BaseSQLiteRestaurant(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BDD);
		//insertion delivroo bordeaux
		double[] tabLat={44.8341382, 44.8428704, 44.8386806, 44.8350042, 44.8368786, 44.8324969, 44.8348331, 44.825363, 44.824053, 44.8181798, 44.8396271, 44.8317064, 44.838937, 44.839623, 44.820726, 44.8214815, 44.8636243, 44.8135488, 44.8640042, 44.838858, 44.8285958, 44.8316979, 44.8513783, 44.83005, 44.8402331, 44.8422748, 44.8370592, 44.836908, 44.824626, 44.832616, 44.82624, 44.8562287, 44.832411, 44.838744, 44.842094, 44.8291726, 44.840233, 44.8578411, 44.834961, 44.842044, 44.864209, 44.8439942, 44.840381, 44.8408366, 44.824087, 44.863798, 44.8365146, 44.8185182, 44.831353, 44.833817, 44.831774, 44.840459, 44.8435459, 44.840405, 44.840025, 44.828325, 44.840063, 44.830795, 44.852334, 44.8402673, 44.831951, 44.838935, 44.836907, 44.84585, 44.825417, 44.84112, 44.8355249, 44.8368852, 44.8412699, 44.83864, 44.8378599, 44.852549, 44.807438, 44.839507, 44.831913, 44.837121, 44.8413836, 44.8383281, 44.836034, 44.8381799, 44.835961, 44.83651, 44.836909, 44.834645, 44.853403, 44.831072, 44.838242, 44.8138963, 44.8466666, 44.8385662, 44.837648, 44.8626248, 44.867457, 44.839656, 44.8413741, 44.841217, 44.8405765, 44.841248, 44.824625, 44.8268624, 44.840917, 44.834625, 44.86017, 44.8412626, 44.8405, 44.841483, 44.8381695, 44.830828, 44.837056, 44.841167, 44.8340782, 44.8236891, 44.8386386, 44.832979, 44.843394, 44.839134, 44.8535906, 44.832423, 44.851338, 44.840147, 44.859144, 44.844491, 44.813445, 44.8499755, 44.8421707, 44.8551673, 44.8396467, 44.834456, 44.854134, 44.837057, 44.846466, 44.8403984, 44.8397736, 44.8637622, 44.874158, 44.837264, 44.836059, 44.841894, 44.812786, 44.8639992, 44.831961, 44.8293539, 44.8556864, 44.841153, 44.8710115, 44.851927, 44.852107, 44.8338927, 44.839658, 44.8412637, 44.841317, 44.8385512, 44.841149, 44.839218, 44.842769, 44.84436, 44.841248, 44.864074, 44.8424729, 44.843046, 44.854719, 44.836804, 44.828699, 44.829958, 44.831685, 44.83394, 44.834735, 44.856406, 44.839076, 44.8513783, 44.8396583, 44.835448, 44.8324125, 44.840896, 44.839272, 44.850492, 44.8414919, 44.840325, 44.8642109, 44.832538, 44.8412126, 44.834662, 44.8325029, 44.8448274, 44.876799, 44.835504, 44.832272, 44.8637139, 44.8399155, 44.829914, 44.852981, 44.837132, 44.864383, 44.841313, 44.839294, 44.840894, 44.83796, 44.86472, 44.834795, 44.8358886, 44.838132, 44.83939, 44.83046, 44.8505514, 44.832888, 44.838401, 44.821806, 44.835094, 44.840595, 44.843664, 44.8362992, 44.832945, 44.862582, 44.8401352, 44.8408486, 44.864488, 44.8480726, 44.834592, 44.8634124, 44.83558, 44.86239 };
		double[] tabLon={-0.6102025, -0.5813832, -0.5815838, -0.5745432, -0.5718339, -0.5729007, -0.5746548, -0.589691, -0.591721, -0.5851184, -0.5811747, -0.5729526, -0.5718936, -0.571871, -0.582998, -0.5824793, -0.5811391, -0.5898022, -0.558602, -0.581687, -0.6662008, -0.5727051, -0.5736549, -0.568758, -0.5998, -0.5761143, -0.5794856, -0.579767, -0.5833091, -0.566811, -0.55765, -0.6179313, -0.5982718, -0.615177, -0.579899, -0.6553113, -0.579537, -0.5599072, -0.5752961, -0.599026, -0.599874, -0.5766527, -0.570581, -0.5593338, -0.55792, -0.581703, -0.5702467, -0.5998772, -0.572566, -0.566494, -0.572785, -0.581402, -0.5736664, -0.582118, -0.579553, -0.561148, -0.599146, -0.602949, -0.5730402, -0.5734485, -0.572825, -0.573405, -0.57219, -0.6322076, -0.589614, -0.55873, -0.572921, -0.5716527, -0.6473654, -0.61585, -0.5704657, -0.617195, -0.56108, -0.577639, -0.572926, -0.6192546, -0.5714161, -0.572444, -0.581512, -0.5782126, -0.573487, -0.575308, -0.571754, -0.575348, -0.567139, -0.578086, -0.570855, -0.5522376, -0.5800207, -0.5815454, -0.567094, -0.5568454, -0.551536, -0.579349, -0.571738, -0.599204, -0.5602842, -0.581898, -0.578536, -0.5562382, -0.570802, -0.566637, -0.609542, -0.6470513, -0.57355, -0.573704, -0.5850243, -0.577358, -0.572636, -0.581885, -0.5731654, -0.5551231, -0.5815721, -0.563214, -0.571298, -0.581458, -0.5727499, -0.571184, -0.574286, -0.559077, -0.5549399, -0.580841, -0.564082, -0.5857874, -0.5726722, -0.59482, -0.5744983, -0.563651, -0.59307, -0.575948, -0.588019, -0.5989142, -0.5596632, -0.5562149, -0.582592, -0.573685, -0.572963, -0.581125, -0.573552, -0.5576428, -0.572934, -0.6800608, -0.5960775, -0.5732177, -0.5621973, -0.571068, -0.572758, -0.5913209, -0.572068, -0.572565, -0.57248, -0.6172249, -0.573692, -0.577987, -0.550532, -0.639585, -0.581898, -0.5567995, -0.6602624, -0.581431, -0.571899, -0.580034, -0.665389, -0.567657, -0.5780661, -0.566443, -0.567846, -0.59668, -0.571369, -0.5736549, -0.5705352, -0.575272, -0.5712953, -0.579732, -0.570583, -0.570067, -0.5724944, -0.570607, -0.5572502, -0.6622943, -0.5727145, -0.587149, -0.5716775, -0.5800034, -0.621615, -0.573509, -0.585403, -0.5590956, -0.5750668, -0.581104, -0.573161, -0.571616, -0.550812, -0.581906, -0.581435, -0.571458, -0.573522, -0.59961, -0.56491, -0.5730848, -0.569629, -0.57204, -0.56774, -0.5720965, -0.562102, -0.568509, -0.572568, -0.574211, -0.570448, -0.554554, -0.5838697, -0.652158, -0.56095, -0.5750038, -0.5597803, -0.616232, -0.5709511, -0.571305, -0.5577095, -0.571431, -0.602317 };
		String[] tabNom={"Cantine Gourmande" , "Edmond Pure Burger - Gambetta" , "Eat Salad - Mériadeck" , "Edmond Pure Burger - Victor Hugo" , "Kokomo" , "Nobi Nobi" , "CPP" , "Mario e Lillo Caffe" , "Casa Ferretti - Bordeaux Barrière de" , "Haru Haru" , "Sushi Shop - Gambetta" , "Pitaya - Victoire" , "Bibibap" , "Phood" , "Woking" , "Billy Factory" , "Eat Salad - Ravezies" , "Campo Verde" , "Pizza Enzo" , "Basilic & Co - Bordeaux Gambetta" , "Eat Salad - Mérignac" , "Fresh Burritos - Victoire" , "Max à Table" , "Sushi Chef" , "Sushi Shop - Caudéran" , "Influence Wok" , "Le Jardin Pékinois" , "Phénix d'Or" , "Le Flap's" , "Tenka" , "Burger Fermier - Saint-Jean" , "Casa Ferretti - Caudéran" , "Sicilia in Bocca" , "Sudissima" , "Häagen Dazs" , "231 East Street - Mérignac" , "Catering Bagels & Hot-dogs - Gambetta" , "Eat Salad - Chartrons" , "Les Mijotés du Bocal" , "Passion Japonaise" , "Basilic & Co - Le Bouscat" , "French Burgers - Tourny" , "Osteria Pizzeria da Bartolo" , "Pitaya - La Bastide" , "Sushi Design - Saint-Jean" , "Sushiman" , "Bocce" , "China Express" , "Falapit" , "Le Rizana" , "5 Saveurs" , "Bagelstein - Bordeaux Gambetta" , "Fuxia - Quinconces" , "La Crêpe d'Angèle" , "La Mama" , "Mammamia Brdx" , "Ici Argentine" , "Le Beaubourg" , "Le Murano - Chartrons" , "Taj Mahal - Bordeaux" , "Wok to Walk - Bordeaux" , "Arbol" , "Catering Bagels & Hot-dogs - Fernand-Lafargue" , "Ché moi" , "Ciluya" , "Litalia" , "My Terroir" , "Pitaya - Fernand-Lafargue" , "Pitaya - Mérignac" , "Poulet d'Antan" , "Que Toi" , "Rico Pizza" , "Yaki Yaki - Bègles" , "Spok Bordeaux" , "La Cabane à Bagels" , "Le Céleste Gourmand" , "Maison Darnauzan" , "Mokoji" , "OriJInes OJI" , "Palazzo - Bordeaux" , "Tacos Avenue" , "Thaï Paradise" , "Umami Ramen" , "Wok Way" , "Yumi" , "Chez George" , "Jardin de Phnom Penh" , "La Brasserie Terres Neuves" , "Le Bol de Riz - Bordeaux" , "Le Safran - Bordeaux" , "Luna Pizza" , "oBento" , "Osakyo" , "Taïwan Connection" , "Les Drôles" , "Casa Ferretti - Bordeaux Wilson" , "Catering Bagels & Hot-dogs - La Bastide" , "Cosy Tacos" , "Côté Terrasse" , "IT Trattoria - Bordeaux" , "Koh I Noor" , "Moony" , "Pizza Villa Roma" , "Sushi Shop - Mérignac" , "Taj Mahal - Saint-Pierre" , "Wa" , "Bagel Corner - Mériadeck" , "Beija Flor" , "Café Kokomo" , "Kenchic - Bordeaux" , "Mangez-Moi" , "Metsens - Bordeaux" , "Noorn Akorn" , "Pizzeria Filippo" , "Sushi Design - Saint-Pierre" , "Sushi Dozo" , "Sushi Shop - Chartrons" , "Chez Dude" , "Delice Rolls" , "Eat Salad - Stalingrad" , "French Burgers - Chartrons" , "La Djaf" , "La Fabrique" , "Le 188" , "Le Murano - Chapeau-Rouge" , "Le Murano - Le Bouscat" , "Matsuri - Bordeaux" , "Palo Alto" , "Quicky Pizz" , "Subway Pey Berland" , "Tam Tam Saigon" , "Téranka" , "Wasabi Café" , "SNS Bordeaux" , "Au Bistr'o" , "Bagels Kook - Saint-Pierre" , "Brooklyn - Bordeaux" , "Charlie & Tom" , "Delice Rolls - Nansouty" , "Eat Salad - Bassin Flot" , "Ebisu" , "French Burgers - Mérignac" , "Ginza - Le Bouscat" , "Guy & Sons - Bordeaux" , "I Fratelli - Bordeaux" , "La Bocca" , "La Vie en Rose" , "Le 100" , "Michel's" , "Pitaya - Saint-Pierre" , "Punjab - Bordeaux" , "Sushi Design - Mondésir" , "Amorino - Bordeaux Saint-Rémi" , "Asiana" , "Bagel & Goodies" , "Baila Pizza" , "Burger Fermier - Gambetta" , "Compoz'Eat" , "Domo" , "Douceurs du Palais Gallien" , "Kenchic - Bordeaux - Chartrons" , "La Gamelle" , "La Scala - Mérignac" , "Le Bistrot des Capucins" , "Le Samouraï" , "Les Saveurs de l'Atlas" , "Lili & Léon" , "Maison Burgalières" , "Masaniello - Bordeaux" , "Max à Table - Chartrons Bordeaux" , "Max à Table - Saint-Pierre Bordeaux" , "Messieurs Croquent" , "Momoda" , "My Little Café" , "Nyam Baï" , "OK ! Brasserie" , "Punjab - Saint-Pierre" , "Rajwal" , "Subway Bordeaux Bassins à Flot" , "Thaï Paradise inbox" , "Un Soir à Shibuya" , "Vach' Et Vous" , "Vinayaka Restaurant" , "Al Mounia - Bordeaux" , "Arigato - Bruges" , "Bagel Corner Sainte-Catherine" , "Bonjour Bdx" , "Boulangerie B3" , "Buffalo Burger - Bordeaux" , "Burdiga Pizza" , "Chez Frango" , "Côté Sushi - Bordeaux" , "Daily D. - Bassins à Flot" , "English Country Kitchen" , "Gaston" , "Glouton Le Bistrot" , "Hook's" , "Joan Lartigau" , "Julo" , "Kitchen Garden" , "La Capitainerie" , "La Fabrique Givrée - Bordeaux" , "La Fine Epicerie" , "La Pelle Café" , "La Tupiña" , "Le Shambhala" , "Les Trois Pinardiers" , "Locadillos" , "Mélodie" , "Mille & Une Saveurs" , "N Café" , "Naga" , "O Secret Bien Gardé" , "Ô Sorbet d'Amour - Saint-Pierre" , "Pause Nat" , "Plateau Sushi" , "Sicilia in Bocca - Quais" , "Spot B" , "Standby Coffee & Tea" , "Tata Yoyo" , "The Bouscat Dinner" };
		
		
		
		if(tabLat.length==tabLon.length&&tabLat.length==tabNom.length){
			for(int i=0;i<tabLat.length;i++){
				insertResto(db,tabLat[i],tabLon[i],tabNom[i],3,0);
			}
		}
		else Log.d("BDD Resto","erreur taille table différente");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL("DROP TABLE " + TABLE_RESTO);
		onCreate(db);
	}

	void insertResto(SQLiteDatabase db,double latDeg,double lonDeg,String restoName,int zone,int plateforme) {
		double latRad=Math.toRadians(latDeg);
		double lonRad=Math.toRadians(lonDeg);
		ContentValues content = new ContentValues();
		content.put(COL_LATRAD_RESTO, latRad);
		content.put(COL_LONRAD_RESTO, lonRad);
		content.put(COL_TEXT_RESTO, restoName);
		content.put(COL_ZONE_RESTO, zone);
		content.put(COL_PLATEFORME_RESTO, plateforme);
		content.put(COL_IDBASE_RESTO, ID_RESTO_DEFAUT);
		db.insert(TABLE_RESTO, null, content);

	}

}
