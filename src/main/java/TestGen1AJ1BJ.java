import java.lang.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
		String fichier = "file/in.csv";
		main.lectureCSV(fichier);		
		
	}

	/**
	 * id / date_naiss / CP / situation / nb_enfants / net_imposable / code_revenu
	 * @param fichier
	 */
	public void lectureCSV(String fichier) {
		
		long id;
		int date_naissance;
		int code_postal;
		String sit_fam;
		int nombre_enfants;
		String net_imposable;
		String codes_revenu;		

		//lecture du fichier texte	
		BufferedReader br = null;
		String ligne = "";
		String split = ","; // délimiteur du csv		
		
		try {

			br = new BufferedReader(new FileReader(fichier));
			int i=0;
			
			// tans qu'on a des lignes à anlyser
			while ((ligne = br.readLine()) != null) {
				
				i++;
				// si m'a ligne n'est pas la ligne d'entête
				if (!ligne.startsWith("id")) {

					String[] tab = ligne.split(split);
					
					id = i;
					date_naissance = Integer.parseInt(tab[0]);
					sit_fam = tab[1];
					nombre_enfants = Integer.parseInt(tab[2]);
					int zone = computeZone();
					// net_imposable = genRevenu(sit_fam, zone, true);
					codes_revenu = genRevenu(sit_fam, zone, false);
					// calcul du net imposable à partir du codes_revenus
					
					// split #
					String[] revenus = codes_revenu.split("#");
					// on a obligatoirement le revenu 1AJ
					String aj = revenus[0];
					int salaires = Integer.parseInt(aj.substring(3)); // on extrait après le code revenu
					if(revenus.length > 1){
						// a les revenus du conjoint à ajouter
						String bj = revenus[1];
						salaires += Integer.parseInt(bj.substring(3));
					}
					String salairesText = Integer.toString(salaires);
					
					/*System.out.println("id=" + tab[0] 
							+ ", date_naissance=" + tab[1] 
							+ ", code_postal=" + tab[2] 
							+ ", sit_fam=" + tab[3]
							+ ", nombre_enfants=" + tab[4]
							+ ", net_imposable=" + net_imposable
							+ ", codes_revenu=" + codes_revenu);*/
					
					// ajout du déficit foncier
					
					int deficitFoncier = revenuFoncier(salaires);
					// si deficite foncier != 0 on l'ajoute au code reveus
					if(deficitFoncier < 0){
						codes_revenu += "#" + genCodeRevenuFoncier(deficitFoncier);
					}					
					
					// calcul de la pension alimentaire				
					int pensionAlimentaire = pensionAlimentaire(salaires, date_naissance);
					if(pensionAlimentaire > 0){
						codes_revenu += "#" + genCodePensionAlimentaire(pensionAlimentaire);
					}
					
					// calcul de la souscription au capital PME
					int souscriptionPME = souscriptionPME(salaires);
					if(souscriptionPME > 0){
						codes_revenu += "#" + genCodeSouscitpionPME(souscriptionPME);
					}
					
					// calcul de l'emploi d'un salarié à docmicile
					int emploiSalarieDomicile = emploiSalarieDomicile(salaires);
					if(emploiSalarieDomicile > 0){
						codes_revenu += "#" + genCodeEmploiSalarieDomicile(emploiSalarieDomicile);
					}
					
					generationCSV(id, date_naissance, sit_fam, nombre_enfants, salairesText, codes_revenu);
				
				}
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
	
	public int emploiSalarieDomicile(int salaires){
		
		// dans 40% des cas on met du salarié à domicile
		int i = new Random().nextInt(100) + 1;
		i++;
		
		if(i <= 40){
			if(salaires < 35000){
				return (int)0;
			} else if(salaires < 70000){
				// on retourne 4000 plus ou moins 1000
				return new Random().nextInt(1000) + 1 + 4000;
			} else {
				// on bombarde au max cad 8000 plus ou moins 10000
				return new Random().nextInt(2000) + 1 + 8000;
			}
		} else {
			return (int)0;
		}
		
	}
	
	public String genCodeEmploiSalarieDomicile(int montantVerse){
		return "7DB" + Integer.toString(montantVerse);
	}
	
	public int souscriptionPME(int salaires){
		
		// dans 40% des cas on met PME
		int i = new Random().nextInt(100) + 1;
		i++;
		
		if(i <= 40){
			
			if(salaires < 50000){
				return (int)0;
			} else if(salaires <70000){
				return new Random().nextInt(2000) + 1 + 4000;
			} else {
				return new Random().nextInt(2000) + 1 + 10000;
			}
			
		} else {
			return (int)0;
		}	
		
	}
	
	public String genCodeSouscitpionPME(int souscription){
		return "7CF" + Integer.toString(souscription);
	}	
	
	/*
	 * Generation du code pension alimentaire versée à un enfant majeur
	 */
	public String genCodePensionAlimentaire(int pension){
		
		if(pension > 0){
			return "6GI" + Integer.toString(pension); 
		} else {
			return null;
		}
		
	}	
	
	/*
	 * calcul d'une penseion alimentaire en fonction du salaire imposable et de l'année de naissance
	 */
	public int pensionAlimentaire(int salairesImposable, int anneeNaissance){
		
		// la pension alimentaire maxi déductible est de 5732€
		int pensionMax = 5732;
		
		// pour avoir des enfant majeur il faut que le contribuable ai 36 ans
		int age = (int)2014 - anneeNaissance;
		
		if(age < 36){
			
			return (int)0;
			
		} else if (age < 40){
			
			// par défaut 90% des gens ne verse pas
			int limite = 90;
			
			// si les salaires sont important on diminue le nombre de personne qui ne verse pas
			if(salairesImposable > 26000){
				limite = 40;
			}
			
			// dans 90% des cas on a pas de pension alimentaire versée
			int p = new Random().nextInt(100) + 1;
			
			if(p <= limite){
				
				return (int)0;
				
			} else {
				
				// on verse entre 2000 et 5000;
				return new Random().nextInt(9732) + 1 - 2000;
				
			}
			
		} else {
			
			// dans 60% des cas on a pas de pension aliemntaire de versée
			int p = new Random().nextInt(100) + 1;
			
			int limite = 90;
			
			// si les salaires sont important on diminue le nombre de personne qui ne verse pas
			if(salairesImposable > 26000){
				limite = 40;
			}
			
			if(p <= limite){
				
				return (int)0;
				
			} else {
				
				// on verse entre 3000 et 5000;
				return new Random().nextInt(10732) + 1 - 3000;
				
			}
			
		}		
		
	}
	
	/*
	 * Public fonction qui calcul le déficit foncier on passe en entrée le ni
	 * 
	 */
	public int revenuFoncier(int salairesImposable){
		
		int res = 0;
		
		// dans 50% des cas on met du déficit foncier
		int i = new Random().nextInt(100) + 1;
		i++;
		
		if(i >= 50){		
		
			int r = new Random().nextInt(4000) + 1;
			
			if(salairesImposable < 18000){
				
				int min = - 4000;				
				res = min + r;
				
			} else if(salairesImposable < 32000){
				
				int min = -8000;
				res = min + r;
				
			} else {
				
				int min = -10700;
				res = min + r;
				
			}
		
		}
		
		return res;
		
	}
	
	/*
	 * Génère le code du déficite foncier
	 */
	public String genCodeRevenuFoncier(int revenu){
		
		String codeRF = "4BC";
		return codeRF + Math.abs(revenu);
		
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
		else if (i<65) {
			return 2;
		}
		else if (i<90) {
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
	public boolean estMarie (String situation) {
		
		char situ = situation.charAt(1);
		
		if (situ != 'M' && situ != 'O') {
			return false;
		}
		return true;
	}
	
	/**
	 * en fonction de estMarie, appelle genMarie ou genNonMarie
	 * @params situation, zone (-> générée avec computeZone())
	 */
	public String genRevenu (String situation, int zone, boolean nombre) {
		if (estMarie(situation)==true)
			return genMarie(zone, nombre);
		else
			return genCelib(zone, nombre);
	}	

	/**
	 * Génération de revenu annuel pour un statut "marié"
	 * 70% des cas: 1AJ + 1BJ
	 * 30% des cas: 1AJ (seulement)
	 * @param zone
	 * @return
	 */
	public String genMarie (double zone, boolean nombre) {
		
		int i = new Random().nextInt(100);
		i++;
		if (i < 30) 
			return genCelib(zone, nombre);
		else {			
			
				String revenu1AJ, revenu1BJ;
				String revenufinal;
				
				int higher = (int) (10000 * Math.exp((double)zone) - zone * Math.random()*10000);
				int lower = (int) (10000 * Math.exp((double)zone) + zone * Math.random()*10000);
				
				int random = (int)(Math.random() * (higher-lower)) + lower;
				int random2 = (int)(Math.random() * (higher-lower)) + lower;
				
				if (!nombre) { 
					revenu1AJ = "1AJ" + random;					
					revenu1BJ = "1BJ" + random2; 
					revenufinal = revenu1AJ + "#" + revenu1BJ;
					return revenufinal;	
				}
				else
					return Integer.toString(random + random2);
			}
	
		}
			
	

	/**
	 * Génération de revenu annuel pour un statut non marié
	 * 100% des cas: 1AJ (même chose pour divorcé(e)/veuf(ve)
	 * @param zone
	 * @return
	 */
	public String genCelib (double zone, boolean nombre) {

		String revenuCelib;
		int higher = (int) (10000 * Math.exp((double)zone) - zone * Math.random()*10000); 
		int lower = (int) (10000 * Math.exp((double)zone) + zone * Math.random()*10000);
		int random = (int)(Math.random() * (higher-lower)) + lower;
		if (!nombre) {
			revenuCelib = "1AJ" + random; 
			return revenuCelib;
		}
		else 
			return Integer.toString(random);
		
	}
	
	public void generationCSV (long id, int date_naissance, String sit_fam, int nombre_enfants, String salairesText, String codes_revenu) {
		
		try {

			File file = new File("insert_data.sql");
	
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
	
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			String line = "INSERT INTO declarants (id, date_naissance, code_postal,"
					+ " sit_fam, nombre_enfants, salaires, codes_revenu,"
					+ " montant_ir, cluster) VALUES (" + id + ", " + date_naissance + ", 00000, "
					+ sit_fam + ", " + nombre_enfants + ", " + salairesText + ", " + "'" + codes_revenu + "'" + ", NULL, NULL);";
			
			System.out.println(line);
			
			bw.write(line);
			bw.write("\n");
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}