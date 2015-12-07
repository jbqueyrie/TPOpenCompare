package org.opencompare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.opencompare.api.java.Cell;
import org.opencompare.api.java.Feature;
import org.opencompare.api.java.PCM;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.Product;
import org.opencompare.api.java.Value;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.PCMLoader;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;

public class StatisticsPlusPlusTest {

	@Test
	public void DSLtest() throws IOException {

		//Initialisation des paramètres :
		double threshold= 0.1;
		int maxFacts=6;
		ArrayList<String> features = new ArrayList<String>();
		//features.add("quantile");



		//Initialisation du chemin :
		String path = "/home/nicolasd/Bureau/OpenCompare_data/data";
		File pcmFile = new File(path);
		File[] filesInDir = pcmFile.listFiles();
		PCMLoader loader = new KMFJSONLoader();


		for (File f : filesInDir) {
			List<PCMContainer> pcmContainers = loader.load(f);

			for (PCMContainer pcmContainer : pcmContainers ) {
				System.out.println("--------------------------------------");
				System.out.println("Fichier : "+f.getName());
				System.out.println("--------------------------------------");
				System.out.println("\n\n");
						
				int max = 0;
				// Get the PCM
				PCM pcm = pcmContainer.getPcm();
				ArrayList<String> facts = new ArrayList<String>();

				// On parcourt les colonnes
				for (Feature feature : pcm.getConcreteFeatures()) {
					//On repère le nom de la colonne
					String nom_variable = feature.getName();
					System.out.println(nom_variable);

					//Au début, on considère que la colonne contient des entiers
					boolean nombre = true;

					ArrayList<Cell> colonne = new ArrayList<Cell>();

					// On parcourt les lignes
					for (Product product : pcm.getProducts()) {
						// Find the cell corresponding to the current feature and product
						Cell cell = product.findCell(feature);
						Value interp = cell.getInterpretation();
						if (!(interp instanceof IntegerValue | interp instanceof RealValue)){
							nombre=false;
						}

						//On stocke les variables dans une liste
						colonne.add(cell);
					}

					// Si la clonne contient des entiers, on fait un calcul des quantiles 
					if (nombre){

						//On stocke le contenu de la colonne dans un tableau de Doubles
						double[] valeurs = new double[colonne.size()];
						for (int i=0;i<colonne.size();i++){
							if(colonne.get(i).getInterpretation() instanceof IntegerValue){
								valeurs[i]= Integer.parseInt(colonne.get(i).getContent());
							}
							else{
								
							}
						}

						double quant = quantile(valeurs,threshold);
						if (!(quant==-1)){
							String fact = "Plus de "+(threshold*100)+"% des produits ont une valeur de " +nom_variable+" supérieure à "+quant; 
							//On vérifie si le nombre max de faits est atteint
							if(facts.size()>=maxFacts){
								//System.out.println("Le nombre de faits max pour la matrice est atteint.");
							}
							else{
								facts.add(fact);
							}
						}

					}


					// Sinon, on fait un compte par catégorie
					else{

						//On stocke le contenu de la colonne dans un tableau de Strings
						ArrayList<String> valeurs = new ArrayList<String>();
						for (int i=0;i<colonne.size();i++){
							valeurs.add(colonne.get(i).getContent());
						}

						ArrayList<String> categoriePrincipale = categorie(valeurs,threshold);
						if (categoriePrincipale.size() != 0){
							String fact = "Plus de "+(threshold*100)+"% des produits ont une valeur de " +nom_variable+" égale à ";
								fact+= categoriePrincipale.get(0);
							for (int i=1;i<categoriePrincipale.size();i++){
								fact+= " et " +categoriePrincipale.get(i);
							}
							
							//On vérifie si le nombre max de faits est atteint
							if(facts.size()>=maxFacts){
								//System.out.println("Le nombre de faits max pour la matrice est atteint.");
							}
							else{
								facts.add(fact);
							}
						}

					}
				}
				
				if(facts.size()!=0){
					for (int i=0;i<facts.size();i++){
						System.out.println(facts.get(i));
					}
				}
				else{
					System.out.println("La matrice ne contient pas de fait intéressant d'après les features demandées.");
				}
				System.out.println("\n\n");

			}
		}
	}

	public static double quantile(double[] values, double threshold) {

		double res = -1;
		//On vérifie que la matrice a une ligne
		if (!(values == null || values.length == 0)) {
			// Tri des valeurs et calcul du quantile
			double[] v = new double[values.length];
			System.arraycopy(values, 0, v, 0, values.length);
			Arrays.sort(v);

			int n = (int) Math.round(v.length * threshold / 100);

			res = v[n];
		}
		return res;
	}

	
	
	
	public static ArrayList<String> categorie(ArrayList<String> values, double threshold) {

		ArrayList<String> res = new ArrayList<String>();
		if (!(values == null || values.size() == 0)) {
			//On récupère toutes les différentes modalités
			HashSet<String> uniqueValues = new HashSet<String>(values); 
			//On transforme le set en liste 
			List<String> modalites = new ArrayList<>(uniqueValues);

			
			//On initialise un tableau vide qui contiendra les occurences de chaque modalité
			int[] occurences = new int[modalites.size()];
			for (int i=0;i<occurences.length;i++){
				occurences[i]=0;
			}

			//On parcourt chaque valeur de la colonne
			for (int i=0;i<values.size();i++){
				//On la stocke dans une valeur temporaire
				String temp = values.get(i);

				//On incrémente la modalité reliée
				for (int j=0;j<modalites.size();j++) {
					if(temp==modalites.get(j)){
						occurences[j]+=1;
					}
				}
			}
			
			System.out.print("Modalités différentes : ");
			for (int i=0;i<modalites.size();i++){
				System.out.print(modalites.get(i)+ " - ");
			}
			System.out.println("\n");
			System.out.print("Occurences : ");
			for (int i=0;i<occurences.length;i++){
				System.out.print(occurences[i]+ " - ");
			}
			System.out.println("\n");
			
			//On regarde si une modalité est présente dans plus de threshold % des cas
			for (int k=0;k<occurences.length;k++){
				double percent = occurences[k]/values.size();
				if(percent>=threshold){
					//Si la modalité est une chaine vide, on ne la prend pas en compte
					if(!(modalites.get(k).equals(""))){
						res.add(modalites.get(k));
					}
				}
			}

		}
		return res;
	}
}
