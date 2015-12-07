package org.opencompare;

import java.io.File;
import java.io.IOException;
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

public class StatisticsPlusTest {

	@Test
	public void testcalculTypeCellules() throws IOException {
		int nbInt_total = 0;
		int nbString_total = 0;
		int nbBoolean_total = 0;

		int minInt = 9999999;
		int maxInt = 0;

		int minString = 9999999;
		int maxString = 0;

		int minBoolean = 9999999;
		int maxBoolean = 0;

		String path = "/home/nicolasd/Bureau/OpenCompare_data/data";
		File pcmFile = new File(path);
		File[] filesInDir = pcmFile.listFiles();
		PCMLoader loader = new KMFJSONLoader();


		for (File f : filesInDir) {
			List<PCMContainer> pcmContainers = loader.load(f);
			int nbInt_matrice = 0;
			int nbString_matrice = 0;
			int nbBoolean_matrice = 0;
			int nbReal_matrice = 0;
			for (PCMContainer pcmContainer : pcmContainers ) {
				// Get the PCM
				PCM pcm = pcmContainer.getPcm();


				// Browse the cells of the PCM
				for (Product product : pcm.getProducts()) {
					for (Feature feature : pcm.getConcreteFeatures()) {

						// Find the cell corresponding to the current feature and product
						Cell cell = product.findCell(feature);
						Value interp = cell.getInterpretation();

						if (interp instanceof IntegerValue){
							nbInt_total +=1;
							nbInt_matrice +=1;
						}
						if (interp instanceof StringValue){
							nbString_total +=1;
							nbString_matrice +=1;
						}
						if (interp instanceof BooleanValue){
							nbBoolean_total +=1;
							nbBoolean_matrice+=1;
						}
					}

					//Indicateurs par matrice pour les entiers :
					if (nbInt_matrice < minInt){
						minInt=nbInt_matrice;
					}
					if (nbInt_matrice > maxInt) {
						maxInt = nbInt_matrice;
					}

					//Indicateurs par matrice pour les strings :
					if (nbString_matrice < minString){
						minString=nbString_matrice;
					}
					if (nbString_matrice > maxString) {
						maxString = nbString_matrice;
					}

					//Indicateurs par matrice pour les booleans :
					if (nbBoolean_matrice < minBoolean){
						minBoolean=nbBoolean_matrice;
					}
					if (nbBoolean_matrice > maxBoolean) {
						maxBoolean = nbBoolean_matrice;
					}
				}	
			}
		}
		System.out.println("Il y a "+nbInt_total+" cellules contenant des entiers.");
		System.out.println("Il y a "+nbString_total+" cellules contenant des caractères.");
		System.out.println("Il y a "+nbBoolean_total+" cellules contenant des booleens.\n\n");

		int nbFiles = filesInDir.length;

		System.out.println("Il y a en moyenne "+nbInt_total/nbFiles+" cellules contenant des entiers par matrice. Min : "+minInt+"| Max : "+maxInt);
		System.out.println("Il y a en moyenne "+nbString_total/nbFiles+" cellules contenant des caractères par matrice. Min : "+minString+"| Max : "+maxString);
		System.out.println("Il y a en moyenne "+nbBoolean_total/nbFiles+" cellules contenant des booleens par matrice. Min : "+minBoolean+"| Max : "+maxBoolean);


	}
}
