package starter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import publisher.StarterPublisher;
import configuration.Configuration;
import simhash.SimhashCloneTest6e;
import simhash.SimhashCloneTest6o;
import simhash.SimhashCloneTest6;
import filters.Filter1;
import filters.Filter2;
import report.Report;
import lcs.LcsForTowFiles;
import levenshtien.Levenshtein;


import semantic.*;



public class Starter_FactExtraction_Step2 {

	/**
	 * @param args
	 * @throws Exception 
	 * Push test
	 * Final test
	 * Farouq
	 */
	public static void main(String[] args) throws Exception {

		// load configuration from file
	//	Configuration config=Configuration.loadFromFile();
		if (args.length==0) throw new Exception("Please enter project address");;
		//System.out.println(args.length);
		Configuration config=Configuration.initialize(args[0]);
		ArrayList<String> arguments= new ArrayList<String>();
		
		for (int i = 0; i < args.length; i++) {
			arguments.add(args[i]);			
		}
		if (args.length==2)config.instructionLevThreshold= Double.parseDouble(arguments.get(1));
		
		if (args.length==3){ 
			config.instructionLevThreshold= Double.parseDouble(arguments.get(1));
			config.instructionJaccThreshold= Double.parseDouble(arguments.get(2));
		}
		
		if (args.length==4){ 
			config.instructionLevThreshold= Double.parseDouble(arguments.get(1));
			config.instructionJaccThreshold= Double.parseDouble(arguments.get(2));
			config.callsLCSThreshold= Double.parseDouble(arguments.get(3));
		}
		
		if (args.length>=4){  
			config.instructionLevThreshold= Double.parseDouble(arguments.get(1));
			config.instructionJaccThreshold= Double.parseDouble(arguments.get(2));
			config.callsLCSThreshold= Double.parseDouble(arguments.get(3));
			config.callsJaccardThreshold= Double.parseDouble(arguments.get(4));
		}
		

	
//		if(!arguments.get(0).equals("") || arguments.get(1)==null)  config.instructionLevThreshold= Double.parseDouble(arguments.get(1));
//		if(!arguments.get(0).equals("") || arguments.get(2)==null)  config.instructionJaccThreshold= Double.parseDouble(arguments.get(2));
//		if(!arguments.get(0).equals("") || arguments.get(3)==null)  config.callsLCSThreshold= Double.parseDouble(arguments.get(3));
//		if(!arguments.get(0).equals("") || arguments.get(4)==null)  config.callsJaccardThreshold= Double.parseDouble(arguments.get(4));
		
		// first step: create output folders
		Starter_Preparation_Step1.makeOutputFolders(config);
		
		// extract bytecode into xml files
		// move used source code and exe files
		
		//System.out.println(config.disassembeler_EXE_path); // correct disasem
		StarterPublisher.start(config);
		
		
	 
		// clone detection using Lev distance on byte code
		
		InstructionLev.levenstien(config, config.xmlByteCode);
		
		// detection using Jaccard on byte code
		InstructionJacc.jaccInstructions(config);
		
		// clone detection using LCS on called methods
		
		CalledMethodsMatching. lcsCalledMethods(config);
		
		CalledMethodsMatching. jaccCalledMethods(config);
		
		Merge.mergeReports(config);
		
		
		System.out.println(config.instructionLevThreshold);
		System.out.println(config.instructionJaccThreshold);
		
		System.out.println(config.callsJaccardThreshold);
		System.out.println(config.callsLCSThreshold);
		
		System.out.println(config.signitureLevThreshold);
		System.out.println(config.signitureJaccThreshold);
		
		
	//	System.out.println("Reports Generated look for files in folder : 5_Report");

		 
/*		
		Configuration config=Configuration.loadFromFile2();

		System.out.println(config.detectionMethod);
		System.out.println(config.threshold);
		System.out.println(config.ReportingMethod);
		System.out.println(config.Filterusage);
		
		if (config.detectionMethod.equals("Lev")){
			
			System.out.println("Levenshtien is running");
			if(config.ReportingMethod.equals("R"))
				Levenshtein.LevenReport(config.disassebledAddress+"/allFiles.xml_0_binary.xml");
			else
				Levenshtein.LevenPairs(config.disassebledAddress+"/allFiles.xml_0_binary.xml");
			
		}else if(config.detectionMethod.equals("Lcs")){
			
			System.out.println("Lcs algorithem is running");
			if(config.ReportingMethod.equals("R"))
				lcs.LcsForTowFiles.lcsReport(config.disassebledAddress+"/allFiles.xml_0_binary.xml");

			else
				lcs.LcsForTowFiles.lcsPairs(config.disassebledAddress+"/allFiles.xml_0_binary.xml");

		}
		
		*/
/*		

				switch (config.comparisonMethod)
		{
		case 1:		

//			for (int i=0;i<=16;i++){
	//		SimhashCloneTest6e.simThreshold=13;
			//allFiles.xml_0_binary.xml this file contains the extracted IL code for the functions of the system under study.
			// SimhashCloneTest6e used in the DotCad project
			SimhashCloneTest6o.findClone(config.disassebledAddress+"\\allFiles.xml_0_binary.xml");
			// allFiles.xml_0-secco_clones-multi_index-"+SimhashCloneTest6e.simThreshold+".xml" is the name of the report generated by SimCad
			// next function generate source code report
//			System.out.print(i+"\t");
			report.Report.parseSimCad(config, config.disassebledAddress+"\\allFiles.xml_0-secco_clones-multi_index-"+SimhashCloneTest6e.simThreshold+".xml");
//			}

			break;

		case 2:

			lcs.LcsForTowFiles.lcs(config.disassebledAddress+"\\allFiles.xml_0_binary.xml");
			break;
		case 3:
			System.out.println("Levnshtien is runing");
			Levenshtein.Lev(config.disassebledAddress+"\\allFiles.xml_0_binary.xml");

			//Levenshtein.LevenPairs(config.disassebledAddress+"\\allFiles.xml_0_binary.xml");
			break;
		default:

		System.out.println(" wrong comparison method entry");
		}
*/
		/*	


		filters.Filter1.filter1(config.disassebledAddress+"\\vb.xml");
		filters.Filter4.filter4(config.disassebledAddress+"\\vb_f1.xml");

		filters.Filter2.filter2(config.disassebledAddress+"\\vb_f1_4.xml");
		filters.Filter4.filter5(config.disassebledAddress+"\\vb_f1_4.xml");
		filters.Filter4.filter6(config.disassebledAddress+"\\vb_f1_4.xml");
		filters.Filter4.filter7(config.disassebledAddress+"\\vb_f1_4.xml");
		filters.Filter4.filter8(config.disassebledAddress+"\\vb_f1_4.xml");
		filters.Filter3.filter3(config.disassebledAddress+"\\vb_f1_4.xml");

j
		filters.Filter1.filter1(config.disassebledAddress+"\\cs.xml");
		filters.Filter4.filter4(config.disassebledAddress+"\\cs_f1.xml");

		filters.Filter2.filter2(config.disassebledAddress+"\\cs_f1_4.xml");
		filters.Filter4.filter5(config.disassebledAddress+"\\cs_f1_4.xml");
		filters.Filter4.filter6(config.disassebledAddress+"\\cs_f1_4.xml");
		filters.Filter4.filter7(config.disassebledAddress+"\\cs_f1_4.xml");
		filters.Filter4.filter8(config.disassebledAddress+"\\cs_f1_4.xml");
		filters.Filter3.filter3(config.disassebledAddress+"\\cs_f1_4.xml");
		 */
		//		filters.Filter1.filter1(config.disassebledAddress+"\\chap (26).xml");
		//		filters.Filter4.filter4(config.disassebledAddress+"\\chap (26)_f1.xml");
		//		filters.Filter4.filter8(config.disassebledAddress+"\\chap (26)_f1_4.xml");

		//		SimhashCloneTest6e.findClone(config.disassebledAddress+"\\chap (26)_f1_4_8.xml");



		//		for(int i=1;i<=25;i++){
		//	filters.Filter1.filter1(config.disassebledAddress+"\\chap ("+i+").xml");
		//		filters.Filter4.filter4(config.disassebledAddress+"\\chap ("+i+")_f1.xml");
		//		filters.Filter4.filter8(config.disassebledAddress+"\\chap ("+i+")_f1_4.xml");
		/*	filters.Filter4.filter5(config.disassebledAddress+"\\f ("+i+")_f1_4.xml");
		filters.Filter4.filter6(config.disassebledAddress+"\\f ("+i+")_f1_4.xml");
		filters.Filter4.filter7(config.disassebledAddress+"\\f ("+i+")_f1_4.xml");
		filters.Filter4.filter8(config.disassebledAddress+"\\f ("+i+")_f1_4.xml");
		filters.Filter3.filter3(config.disassebledAddress+"\\f ("+i+")_f1_4.xml");
		 */
		//	System.out.println(" file number iiiii"+i+"------------------------------------------------------"+i);
		//	SimhashCloneTest6e.findClone(config.disassebledAddress+"\\chap ("+i+")_f1_4_8.xml");
		//	}
		//	

		//SimhashCloneTest6e.findClone(config.disassebledAddress+"\\chap (15).xml");

		//filters.Filter1.filter(config.disassebledAddress+"\\chap (1).xml");
		//filters.Filter2.filter(config.disassebledAddress+"\\chap (1)_filtered2.xml");
		//filters.Filter3.filter(config.disassebledAddress+"\\chap (1)_filtered2_filtered2.xml");
		//filters.Filter4.filter(config.disassebledAddress+"\\chap (1)_filtered2_filtered2.xml");

	}




}
