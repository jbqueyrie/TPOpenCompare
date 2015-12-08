package org.opencompare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
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
import org.opencompare.api.java.value.BooleanValue;
import org.opencompare.api.java.value.IntegerValue;
import org.opencompare.api.java.value.RealValue;
import org.opencompare.api.java.value.StringValue;

public class StatisticsPlusPlusTest {

	@Test
	public void DSLtest() throws IOException {

		
		//On lit le fichier de paramètres .fact crée avec le DSL :
	 	
		//Initialisation des paramètres :
		double threshold= 0.9;
		
		//Si threshold est <0.5, on le met à 0.5
		if(threshold<0.5){
			threshold=0.5;
		}
		
		int maxFacts=5;
		ArrayList<String> features = new ArrayList<String>();
		features.add("number");
		features.add("string");
		features.add("boolean");
		
		//On vérifie les valeurs présentes dans la liste features du fichier de paramètres :
		//On initialise tout à false :
		boolean feature_nombre = false;
		boolean feature_string = false;
		boolean feature_boolean = false;
		
		//number : Si l'utilisateur veut les quantiles
		if (isIn("number",features)){
			feature_nombre = true;
		}
		
		//string : Si l'utilisateur les modalités les plus présentes
		if (isIn("string",features)){
			feature_string=true;
		}
		
		//boolean : Si l'utilisateur veut les booleans les plus présents
		if (isIn("boolean",features)){
			feature_boolean=true;
		}

		//Initialisation du chemin pour créer le dossier txt:
		String path = "/home/nicolasd/Bureau/OpenCompare_data/data";
		String path_txt = "/home/nicolasd/Bureau/OpenCompare_data/txt";
		File theDir = new File(path_txt);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory txt :");
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
		
		
		File pcmFile = new File(path);
		File[] filesInDir = pcmFile.listFiles();
		PCMLoader loader = new KMFJSONLoader();


		for (File f : filesInDir) {
			List<PCMContainer> pcmContainers = loader.load(f);
			
			//Récupération du nom de fichier
			String name = f.getName();
			name = name.substring(0,name.length()-4);
			
			//Création des fichiers dans lesquels on stocke les faits
			File file = new File(path_txt + "/" + name + ".txt");
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for (PCMContainer pcmContainer : pcmContainers ) {
				
				System.out.println("--------------------------------------");
				writer.write("--------------------------------------");
				writer.newLine();
				System.out.println("Fichier : "+f.getName());
				writer.write("Fichier : "+f.getName());
				writer.newLine();
				System.out.println("--------------------------------------");
				writer.write("--------------------------------------");
				System.out.println("\n\n");
				writer.newLine();

				// Get the PCM
				PCM pcm = pcmContainer.getPcm();
				ArrayList<String> facts = new ArrayList<String>();

				// On parcourt les colonnes
				for (Feature feature : pcm.getConcreteFeatures()) {
					//On repère le nom de la colonne
					String nom_variable = feature.getName();

					//On initialise des booleens pour pouvoir connaitre le type des variables
					boolean nombre = true;
					boolean chaine_caract = true;
					boolean bool = true;


					ArrayList<Cell> colonne = new ArrayList<Cell>();

					// On parcourt les lignes
					for (Product product : pcm.getProducts()) {
						// Find the cell corresponding to the current feature and product
						Cell cell = product.findCell(feature);
						Value interp = cell.getInterpretation();

						//On vérifie le type de la colonne
						if (!(interp instanceof IntegerValue || interp instanceof RealValue)){
							nombre=false;
						}

						if (!(interp instanceof StringValue)){
							chaine_caract=false;
						}

						if (!(interp instanceof BooleanValue)){
							bool=false;
						}

						//On stocke les variables dans une liste
						colonne.add(cell);
					}
					
					
					

					// Si la clonne contient des entiers, on fait un calcul des quantiles si l'utilisateur l'a demandé
					if (nombre & feature_nombre){

						//On stocke le contenu de la colonne dans un tableau de Doubles
						double[] valeurs = new double[colonne.size()];
						for (int i=0;i<colonne.size();i++){
							if(colonne.get(i).getInterpretation() instanceof IntegerValue){
								valeurs[i]= Integer.parseInt(colonne.get(i).getContent());
							}
							else if(colonne.get(i).getInterpretation() instanceof RealValue){
								valeurs[i]= Double.parseDouble(colonne.get(i).getContent());
							}
							else{
								System.out.println("Problème de type");
								writer.write("Problème de type");
								writer.newLine();
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


					//Si la colonne contient des chaînes de caractères, on fait un compte par catégorie si l'utilisateur l'a demandé
					if(chaine_caract & feature_string){

						//On stocke le contenu de la colonne dans un tableau de Strings
						ArrayList<String> valeurs = new ArrayList<String>();
						for (int i=0;i<colonne.size();i++){
							valeurs.add(colonne.get(i).getContent());
						}

						ArrayList<String> categoriePrincipale = categorie(valeurs,threshold);
						if (categoriePrincipale.size() != 0){
							String fact = "Plus de "+(threshold*100)+"% des produits ont une valeur de " +nom_variable+" égale à ";
							fact+= categoriePrincipale.get(0);
							if(categoriePrincipale.size()>1){
								for (int i=1;i<categoriePrincipale.size()-1;i++){
									fact+= ", " +categoriePrincipale.get(i);
								}
								fact+= " et "+categoriePrincipale.get(categoriePrincipale.size()-1);
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


					// Si la clonne contient des booleens, on regarde le pourcentae de true et false 
					if (bool & feature_boolean){

						//On stocke le contenu de la colonne dans un tableau de booleens
						boolean[] valeurs = new boolean[colonne.size()];
						for (int i=0;i<colonne.size();i++){
							if(colonne.get(i).getInterpretation() instanceof BooleanValue){
								valeurs[i]= Boolean.parseBoolean(colonne.get(i).getContent());
							}
							else{
								System.out.println("Problème de type");
								writer.write("Problème de type");
								writer.newLine();
							}
						}

						String bool_value = pourcentage(valeurs,threshold);
						if (!(bool_value.equals(""))){
							String fact = "Plus de "+(threshold*100)+"% des produits ont une valeur de " +nom_variable+" égale à "+bool_value; 
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
						writer.write(facts.get(i));
						writer.newLine();
					}
				}
				else{
					System.out.println("La matrice ne contient pas de fait intéressant d'après les features demandées.");
					writer.write("La matrice ne contient pas de fait intéressant d'après les features demandées.");
					writer.newLine();
				}
				System.out.println("\n\n");
				writer.newLine();
				writer.newLine();

			}
			writer.close();
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
			double[] occurences = new double[modalites.size()];
			for (int i=0;i<occurences.length;i++){
				occurences[i]=0;
			}

			//On parcourt chaque valeur de la colonne
			for (int i=0;i<values.size();i++){
				//On la stocke dans une valeur temporaire
				String temp = values.get(i);

				//On incrémente la modalité reliée
				for (int j=0;j<modalites.size();j++) {
					if(temp.equals(modalites.get(j))){
						occurences[j]++;
					}
				}
			}

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

	public static String pourcentage(boolean[] values, double threshold) {

		String res = "";
		//On vérifie que la matrice a une ligne
		if (!(values == null || values.length == 0)) {
			double nb_bool=0;
			//On parcours le tableau de booleans et on calcule le pourcentage de true et false
			for (int i=0;i<values.length;i++){
				if(values[i]){
					nb_bool++;
				}
			}

			//On regarde la valeur du pourcentage pour regarder la valeur à retourner
			double percent = nb_bool/values.length;
			if(percent>=threshold){
				res="True";
			}
			else{
				res="False";
			}
		}
		return res;
	}
	
	
	//Fonction qui vérifie si une chaine de caractères est présente dans une liste :
	public boolean isIn(String element, ArrayList<String> liste){
		boolean res=false;
		for (int i=0;i<liste.size();i++){
			if(element.equals(liste.get(i))){
				res=true;
			}
		}
		return res;
	}
}
