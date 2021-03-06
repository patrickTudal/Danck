package com.example.tudal.danck.analysis;

import com.example.tudal.danck.analysis.myDTW;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import fr.enseeiht.danck.voice_analyzer.DTWHelper;
import fr.enseeiht.danck.voice_analyzer.Extractor;
import fr.enseeiht.danck.voice_analyzer.Field;
import fr.enseeiht.danck.voice_analyzer.MFCC;
import fr.enseeiht.danck.voice_analyzer.WindowMaker;
import fr.enseeiht.danck.voice_analyzer.defaults.DTWHelperDefault;

public class myPersonalTest {

	protected static final int MFCCLength = 13;
	// Fonction permettant de calculer la taille des Fields 
			//c'est-�-dire le nombre de MFCC du Field 
	static int FieldLength(String fileName) throws IOException {
		int counter= 0;
		File file = new File(System.getProperty("user.dir") + fileName);
        for (String line : Files.readAllLines(file.toPath(), Charset.defaultCharset())) 
        	counter++;
        
        return 2*Math.floorDiv(counter, 512);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
				
	DTWHelper myDTWHelper= new myDTW();
	DTWHelper DTWHelperDefault= new DTWHelperDefault();
	int MFCCLength;
				 
	// Chemin de recherche des fichiers sons
		    
	// Appel a l'extracteur par defaut (calcul des MFCC)
		Extractor extractor = Extractor.getExtractor();
		String base="./corpus/base/";
		String test = "./corpus/test/";
		File dossierSource = new File (base);
		File dossierTest = new File (test);
		
		if(!dossierSource.canRead())
			System.out.println("Error lire dossier base");
		if (!dossierTest.canRead())
			System.out.println("Error lire dossiere test");
		
		File[] listSource = dossierSource.listFiles();
		File[] listTest = dossierTest.listFiles();
		//int loop=0;
		List<Field> reference = new ArrayList<>();
		List<Field> unknown = new ArrayList<>();
		int nbBase=0;
		int nbTest=0;
		if (listSource != null) {
			for ( File child : listSource) {		        
				    
				nbBase++;
				// Etape 1. Lecture
				List<String> files = new ArrayList<>();
				files.add( child.getPath() );
				System.out.println("je lis " + child);
				WindowMaker windowMaker = new MultipleFileWindowMaker(files);
						    
				// Etape 2. Recuperation des MFCC
				 MFCCLength= FieldLength(child.getPath());
				    MFCC[] mfccsAlpha = new MFCC[MFCCLength];
				for (int i = 0; i < mfccsAlpha.length; i++) 
					mfccsAlpha[i] = extractor.nextMFCC(windowMaker);
				
					        
				// Etape 3. Construction du Field (ensemble de MFCC)
				Field alphaField= new Field(mfccsAlpha);
					        
				// On lit les sons de reference
					reference.add(alphaField);
			 }
		}
		else 
			System.out.println("Lecture du dossier base impossible");
		
		
		if (listTest != null) {
			for ( File child : listTest) {		        
				    
				nbTest++;
				// Etape 1. Lecture 
				List<String> files = new ArrayList<>();
				files.add( child.getPath() );
				System.out.println("je lis " + child);
				WindowMaker windowMaker = new MultipleFileWindowMaker(files);
						    
				// Etape 2. Recuperation des MFCC 
				 MFCCLength= FieldLength(child.getPath());
				 MFCC[] mfccsAlpha = new MFCC[MFCCLength];
				for (int i = 0; i < mfccsAlpha.length; i++) 
					mfccsAlpha[i] = extractor.nextMFCC(windowMaker);
				
					        
				// Etape 3. Construction du Field (ensemble de MFCC) 
				Field alphaField= new Field(mfccsAlpha);
					        
				//on lit les sons a tester 
				unknown.add(alphaField);
			 }
		}
		else 
			System.out.println("Lecture du dossier test impossible");

		        float[][] mesDistances = new float[nbBase][nbTest];
		        //float[][] profDistances = new float[13][13];
		        float[] minimum = new float[nbTest];
		        int[] indice = new int[nbTest];
		        for(int i=0;i<nbTest;i++) {minimum[i]=999;}
		        
		        for(int i=0;i<nbBase;i++)
		        	for(int j=0;j<nbTest;j++) {
		        		mesDistances[i][j]=myDTWHelper.DTWDistance(reference.get(i), unknown.get(j));
		        		
		        		//profDistances[i][j]=DTWHelperDefault.DTWDistance(reference.get(i), unknown.get(j));
		        	
		        		if(mesDistances[i][j]<minimum[j]) {
		        			minimum[j]=mesDistances[i][j];
		        			indice[j]=i%13;		//le mot que l'on reconnait pour l'enregistrement num j
		        		}
		        	}
		        
//		        System.out.println("myDTW");
//		        for(int i=0;i<nbBase;i++) {
//		        	for(int j=0;j<nbTest;j++) {
//		        		System.out.print("["+ i + "][" + j + "] = " + mesDistances[i][j]+"  ");
//		        	}
//		        	System.out.println();
//		        }
//		        System.out.println("DefaultDTW");
//		        for(int i=0;i<nbBase;i++) {
//		        	for(int j=0;j<nbTest;j++) {
//		        		System.out.print("["+ i + "][" + j + "] = " + profDistances[i][j]+"  ");
//		        	}
//		        	System.out.println();
//		        }

	int[][] maMatriceConfusion = new int[13][nbTest];
	for(int i=0;i<13;i++)
		for(int j=0;j<nbTest;j++) {
			if(indice[j]==i)
				maMatriceConfusion[i][j]=1;
			else
				maMatriceConfusion[i][j]=0;
		}
	
	System.out.println("Ma Matrice de confusion");
    for(int i=0;i<13;i++) {
    	for(int j=0;j<nbTest;j++) {
    		System.out.print( maMatriceConfusion[i][j]+"  ");
    	}
    	System.out.println();
    }

//end main
	}
}
