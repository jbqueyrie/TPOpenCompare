package org.opencompare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.opencompare.api.java.PCMContainer;
import org.opencompare.api.java.impl.io.KMFJSONLoader;
import org.opencompare.api.java.io.CSVExporter;
import org.opencompare.api.java.io.PCMLoader;

public class StatisticsPlusPlusPlusTest {

	@Test
	public void testExportCSV() throws IOException { //Ici, on ne teste que l'export d'une matrice

		// Define a file representing a PCM to load
		String path = "/home/nicolasd/Bureau/OpenCompare_data/data";
		File file = new File(path);
		File[] filesInDir = file.listFiles();

		// Creation du dossier ou seront situés les .csv
		String path_csv = "/home/nicolasd/Bureau/OpenCompare_data/csv";
		File theDir = new File(path_csv);

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


		for (File f : filesInDir) {
			String name = f.getName();

			PCMLoader loader = new KMFJSONLoader();
			File pcmFile = new File (path + "/" + name);
			List<PCMContainer> pcmContainers = loader.load(pcmFile);

			for (PCMContainer pcmContainer : pcmContainers) {
				//PCM pcm = pcmContainer.getPcm();

				// Export the PCM container to CSV
				CSVExporter csvExporter = new CSVExporter();
				String csv = csvExporter.export(pcmContainer);

				name = name.substring(0, (name.length()-4));

				// Write CSV content to file
				Path chemin = Paths.get(path_csv+"/" + name +".csv");
				Path outputFile = Files.createFile(chemin);
			
				Files.write(outputFile, csv.getBytes());
				System.out.println("PCM exported to " + outputFile);
			}
		}
	}

}
