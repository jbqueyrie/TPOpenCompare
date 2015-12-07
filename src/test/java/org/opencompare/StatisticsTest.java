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
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.PCMLoader;

public class StatisticsTest {

	@Test
	public void testcalculCellules() throws IOException {
		int total = 0;
		float mean = 0;
		int min = 9999999;
		int max = 0;

		String path = "/home/jean-baptiste/Documents/GenieLogiciel/TP_OC/dataset-0.6.1";
		File pcmFile = new File(path);
		File[] filesInDir = pcmFile.listFiles();
		PCMLoader loader = new KMFJSONLoader();


		for (File f : filesInDir) {
			List<PCMContainer> pcmContainers = loader.load(f);
			int nbcell = 0;

			for (PCMContainer pcmContainer : pcmContainers ) {
				// Get the PCM
				PCM pcm = pcmContainer.getPcm();


				// Browse the cells of the PCM
				for (Product product : pcm.getProducts()) {
					for (Feature feature : pcm.getConcreteFeatures()) {

						// Find the cell corresponding to the current feature and product
						Cell cell = product.findCell(feature);
						total += 1;
						nbcell += 1;
					}
					if (nbcell < min){
						min=nbcell;
					}
					if (nbcell >= max) {
						max = nbcell;
					}
				}
			}
		}
		System.out.println("Il y a " + total + " cellules au total dans le jeu de données !");
		mean = total/filesInDir.length;
		System.out.println("En moyenne, il y a " + mean + " cellules par matrice.");
		System.out.println("Min : " + min + " | Max : " + max + "\n");
	}

	@Test
	public void testcalculProduits() throws IOException {
		int total = 0;
		float mean = 0;
		int min = 9999999;
		int max = 0;

		String path = "/home/jean-baptiste/Documents/GenieLogiciel/TP_OC/dataset-0.6.1";
		File pcmFile = new File(path);
		File[] filesInDir = pcmFile.listFiles();
		PCMLoader loader = new KMFJSONLoader();


		for (File f : filesInDir) {
			List<PCMContainer> pcmContainers = loader.load(f);
			int nbprod = 0;

			for (PCMContainer pcmContainer : pcmContainers ) {
				// Get the PCM
				PCM pcm = pcmContainer.getPcm();


				// Browse the cells of the PCM
				for (Product product : pcm.getProducts()) {
					for (Feature feature : pcm.getConcreteFeatures()) {

						// Find the cell corresponding to the current feature and product
						Cell cell = product.findCell(feature);
					}
					total += 1;
					nbprod += 1;
				}
				if (nbprod < min){
					min=nbprod;
				}
				if (nbprod >= max) {
					max = nbprod;
				}
			}
		}
		System.out.println("Il y a " + total + " produits au total dans le jeu de données !");
		mean = total/filesInDir.length;
		System.out.println("En moyenne, il y a " + mean + " produits par matrice");
		System.out.println("Min : " + min + " | Max : " + max + "\n");
	}

	@Test
	public void testcalculFeatures() throws IOException {
		int total = 0;
		float mean = 0;
		int min = 9999999;
		int max = 0;

		String path = "/home/jean-baptiste/Documents/GenieLogiciel/TP_OC/dataset-0.6.1";
		File pcmFile = new File(path);
		File[] filesInDir = pcmFile.listFiles();
		PCMLoader loader = new KMFJSONLoader();


		for (File f : filesInDir) {
			List<PCMContainer> pcmContainers = loader.load(f);
			int nbfeat = 0;

			for (PCMContainer pcmContainer : pcmContainers ) {
				// Get the PCM
				PCM pcm = pcmContainer.getPcm();

				// Browse the cells of the PCM {
				for (Feature feature : pcm.getConcreteFeatures()) {
					total += 1;
					nbfeat += 1;
				}
				
				if (nbfeat < min){
					min=nbfeat;
				}
				if (nbfeat >= max) {
					max = nbfeat;
				}
			}
		}
		System.out.println("Il y a " + total + " features au total dans le jeu de données !");
		mean = total/filesInDir.length;
		System.out.println("En moyenne, il y a " + mean + " features par matrice");
		System.out.println("Min : " + min + " | Max : " + max + "\n");
	}
}
