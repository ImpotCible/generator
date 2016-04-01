import java.lang.*;
import java.util.Random;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestGen1AJ1BJ {

	public static void main(String[] args) {
		
		/*
		 *  en sortie indiquer combien de revenue generé et le montant arrondi à la dizaine d euro
		 * 1 / 100 1 enfant majeur
		 * Il va nous falloir générer des données de manières pseudo-aléatoire.
		 * à partir d'une entrée contenant le statut
		 * (célibataire, marié) et d'une date de naissance en sortir des codes de revenus comme le 1AJ et le 1BJ
		 * (revenu individu 1, et revenu individu 2).
		 *
		 * Donc les codes de revenu pourraient être structuré dans une MAP<String, String> et l'objectif et d'avoir en sortie 
		 * une chaine comme ça : 1AJ23000#1BJ42000
		 */

		TestGen1AJ1BJ main = new TestGen1AJ1BJ();
		String fichier = "test.csv";
		main.lectureCSV(fichier);
		
		
		//Vérification du générateur de zone: 30% zone1, 30% zone2, 25% zone3, 14% zone 4, 1% zone1.
		
		/*int zone = main.computeZone();
		int tab[] = new int[1000];
		System.out.println("zone" + zone + ":");
		System.out.println(main.genMarie(zone));
		
		for (int i=0; i<1000; i++) {
			zone = main.computeZone();
			tab[i]= zone;
		}
		
		int occ1=0;
		int occ2=0;
		int occ3=0;
		int occ4=0;
		int occ5=0;
		for (int y=0; y < tab.length; y++){
			if (tab[y]==1)
				occ1 ++;
			if (tab[y]==2)
				occ2 ++;
			if (tab[y]==3)
				occ3 ++;
			if (tab[y]==4)
				occ4 ++;
			if (tab[y]==5)
				occ5 ++;
		}
		System.out.println("nb de 1: " + occ1 + "\n" +
				"nb de 2: " + occ2 + "\n" +
				"nb de 3: " + occ3 + "\n" +
				"nb de 4: " + occ4 + "\n" +
				"nb de 5: " + occ5 + "\n");
		*/
	}

	/**
	 * id / date_naiss / CP / situation / nb_enfants / net_imposable / code_revenu
	 * @param fichier
	 */
	public void lectureCSV(String fichier) {
		
		long id;
		int date_naissance;
		int code_postal;
		char sit_fam;
		// C : célibataire
		// M : marié
		int nombre_enfants;
		long net_imposable;
		

		//lecture du fichier texte	
		BufferedReader br = null;
		String ligne = "";
		String split = ","; // délimiteur du csv
		
		
		try {

			br = new BufferedReader(new FileReader(fichier));
			while ((ligne = br.readLine()) != null) {

				String[] tab = ligne.split(split);

				System.out.println("id=" + tab[0] 
						+ ", date_naissance=" + tab[1] 
						+ ", code_postal=" + tab[2] 
						+ ", sit_fam=" + tab[3]
						+ ", nombre_enfants=" + tab[4]
						+ ", net_imposable=" + tab[5]);

				id = Long.parseLong(tab[0]);
				date_naissance = Integer.parseInt(tab[1]);
				code_postal = Integer.parseInt(tab[2]);
				sit_fam = tab[0].charAt(3);
				nombre_enfants = Integer.parseInt(tab[4]);
				net_imposable = Long.parseLong(tab[5]);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 30% zone 1, 30% zone 2, 25% zone 3, 14% zone 4, 1% zone 5
	 * Permet la génération aléatoire de zones, pour ensuite calculer le revenu annuel
	 */  
	public int computeZone() {
		int i = new Random().nextInt(100);
		i++;
		if (i < 30) {
			return 1;
		} 
		else if (i<60) {
			return 2;
		}
		else if (i<85) {
			return 3;
		}
		else if (i<99) {
			return 4;
	    } 
		else {
	        return 5;
	    }
	}

	/**
	 * Vérifie le statut "marié" en vue d'effectuer le revenu 1AJ uniquement ou 1AJ / 1BJ.
	 * @param situation
	 * @return
	 */
	public boolean estMarie (char situation) {
		if (situation != 'M')
			return false;
		return true;
	}
	
	/**
	 * en fonction de estMarie, appelle genMarie ou genNonMarie
	 * @params situation, zone (-> générée avec computeZone())
	 */
	public void genRevenu (char situation, int zone) {
		if (estMarie(situation)==true)
			genMarie(zone);
		else
			genCelib(zone);
	}	

	/**
	 * Génération de revenu annuel pour un statut "marié"
	 * 70% des cas: 1AJ + 1BJ
	 * 30% des cas: 1AJ (seulement)
	 * @param zone
	 * @return
	 */
	public String genMarie (double zone) {
		int i = new Random().nextInt(100);
		i++;
		if (i < 30) 
			return genCelib(zone);
		else {
			String revenu1AJ, revenu1BJ;
			String revenufinal;
			int higher = (int) (10000 * Math.exp((double)zone) - zone * Math.random()*10000);
			int lower = (int) (10000 * Math.exp((double)zone) + zone * Math.random()*10000);
			int random = (int)(Math.random() * (higher-lower)) + lower;
			revenu1AJ = "1AJ" + random;
			random = (int)(Math.random() * (higher-lower)) + lower;
			revenu1BJ = "1BJ" + random; 
			revenufinal = revenu1AJ + "#" + revenu1BJ;
	
			return revenufinal;	
		}
	}

	/**
	 * Génération de revenu annuel pour un statut non marié
	 * 100% des cas: 1AJ (même chose pour divorcé(e)/veuf(ve)
	 * @param zone
	 * @return
	 */
	public String genCelib (double zone) {

		String revenuCelib;
		int higher = (int) (10000 * Math.exp((double)zone) - zone * Math.random()*10000); 
		int lower = (int) (10000 * Math.exp((double)zone) + zone * Math.random()*10000);
		int random = (int)(Math.random() * (higher-lower)) + lower;
		revenuCelib = "1AJ" + random; 
		
		return revenuCelib;
	}
}