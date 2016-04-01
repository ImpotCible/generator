import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		 * 
		 *  en sortie indiquer combien de revenue generé et le montant arrondi à la dizaine d euro
		 * 
		 * 1 / 100 1 enfant majeur
		 * 
		 *  
		 *  Il va nous falloir générer des données de manières pseudo-aléatoire.
Pour commencer, il faudrait faire un petit code JAVA qui, à partir d'une entrée contenant le statut
 (célibataire, marié) et d'une date de naissance en sortir des codes de revenus comme le 1AJ et le 1BJ
  (revenu individu 1, et revenu individu 2).

Donc les codes de revenu pourraient être structuré dans une MAP<String, String> et l'objectif et d'avoir en sortie 
une chaine comme ça : 1AJ23000#1BJ42000

		 */
		
		Main main = new Main();
		String fichier = "nomFichier.csv";
		main.lectureCSV(fichier);
		

	}
	
	public void lectureCSV(String fichier) {
		
		int nbPersonnesFoyer;
		// 1 : célibataire
		// 2 : marié
		// 3 : marié et 1 enfant
		
		//lecture du fichier texte	
		BufferedReader br = null;
		String ligne = "";
		String split = ","; // délimiteur du csv

		try {

			br = new BufferedReader(new FileReader(fichier));
			while ((ligne = br.readLine()) != null) {

			String[] tab = ligne.split(split);

			System.out.println("nbPersonnesFoyer= " + tab[0] + " , dateDeNaissance=" + tab[1] + " , revenu= " + tab[2]);

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

}