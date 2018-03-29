package Simian;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configuration.Configuration;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * this file reads the output report that generated from SimCad and generate our report
 */
public class CompairToSimian {

	static float[][] blockAvgSize = new float[10][10];
	static float[][] blockStd = new float[10][10];
	static ArrayList<int[]> blockLoc = new ArrayList<int[]>();

	static ArrayList<String> out = new ArrayList<String>();

	public static void main(String[] args) throws Exception {

		Configuration config = Configuration.initialize(args[0]);

		String reportAddress = config.reportAddress + "\\FinalCloneReportWeighted Similarities.0.61.xml";

		String semianReportAddress = config.reportAddress + "\\Asxgui_Simian.txt";

		File file = new File(semianReportAddress);

		// System.out.println(convert( file));

		// Load clone classes into Arraylist; each class in one entry.
		ArrayList<ArrayList<String>> semianClones = loadSimianReport(file, config);
		System.out.println(" number of clone pairs detected in Simian is: " + semianClones.size());


		String myreportAddress = config.reportAddress + "\\FinalCloneReportWeighted Similarities.0.61.xml";
		ArrayList<ArrayList<String>> clones = parseCloneReport(config, myreportAddress);
		// ArrayList<ArrayList<String>> clonesEnd = parseCloneReport2 (config,
		// reportAddress );
		System.out.println("Number of clone pairs detected by my tool " + clones.size());

		ArrayList<ArrayList<String>> both = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> semOnly = new ArrayList<ArrayList<String>>();
		int counter = 0;

		for (int i = 0; i < clones.size(); i++) {

			if (foundInSimian(clones.get(i), semianClones)) {
				both.add(clones.get(i));
				counter++;
			} else {
				semOnly.add(clones.get(i));

			}
		}
		
		
		 Set<String> sFiles= new HashSet<String>();
		 
			for (int i = 0; i < semianClones.size(); i++) {
				sFiles.add(semianClones.get(i).get(0));
				sFiles.add(semianClones.get(i).get(3));
			}
		 
			System.out.println("Number of fies used in Semian "+ sFiles.size());
			System.out.println();
			System.out.println();
			
			for (String s:  sFiles) {
				System.out.println(s);
			}
		
		System.out.println("Number of common clones "+ both.size());
		System.out.println("number of Extra clones " +semOnly.size());
		System.out.println("number of Missed clones  take the difference" );

		 
		for (int i = 0; i < semianClones.size(); i++) {
			System.out.println(semianClones.get(i).size() + "  " + semianClones.get(i));
		}

		
		
		
		// ArrayList<ArrayList<String>> clonePairs=convertClassIntoPairs(cloneClasses);

		// we dont need this here
		// parseNiCadPairs(cloneClasses);

	}
	
	public static boolean foundInSimian(ArrayList<String> clonePair, ArrayList<ArrayList<String>> conQATClones) {
		boolean found = false;
		// System.out.println(clonePair);

		String fA1 = clonePair.get(0).toLowerCase();
		int sA1 = Integer.parseInt(clonePair.get(1));
		int eA1 = Integer.parseInt(clonePair.get(2));
		String fB1 = clonePair.get(3).toLowerCase();
		int sB1 = Integer.parseInt(clonePair.get(4));
		int eB1 = Integer.parseInt(clonePair.get(5));

		for (int i = 0; i < conQATClones.size(); i++) {

			String fA2 = conQATClones.get(i).get(0).toLowerCase();
			int sA2 = Integer.parseInt(conQATClones.get(i).get(1).trim());
			int eA2 = Integer.parseInt(conQATClones.get(i).get(2).trim());

			String fB2 = conQATClones.get(i).get(3).toLowerCase();
			int sB2 = Integer.parseInt(conQATClones.get(i).get(4).trim());
			int eB2 = Integer.parseInt(conQATClones.get(i).get(5).trim());

			// System.out.println(fA1+" "+fA2);

			if (((fA1.equals(fA2) && sA1 <= eA2 && eA1 >= sA2) && (fB1.equals(fB2) && sB1 <= eB2 && eB1 >= sB2))
					|| ((fA1.equals(fB2) && sA1 <= eB2 && eA1 >= sB2)
							&& (fB1.equals(fA2) && sB1 <= eA2 && eB1 >= sA2))) {
				found = true;
				// System.out.println(clonePair);
				break;
			}
		}

		return found;
	}

	public static ArrayList<ArrayList<String>> parseCloneReport(Configuration config, String rawFunctionsFileName)
			throws IOException {

		ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();
		File projectAddress = new File(config.projectAddress);

		File fileName = new File(rawFunctionsFileName);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileName);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();

			// System.out.println(root.getNodeName()+"--------");

			NodeList nl = root.getElementsByTagName("cloneinfo");

			// System.out.println(nl.getLength());
			// System.out.println(nl.item(0).getAttributes().getNamedItem("npairs").getFirstChild().getNodeValue());

			// nl = root.getElementsByTagName("class"); //in case of xml classes report

			nl = root.getElementsByTagName("clone_pair");

			// System.out.println(nl.getLength()+"**************************");

			if (nl.getLength() > 0) {

				for (int group = 0; group < nl.getLength(); group++) {
					NodeList sourceList = nl.item(group).getChildNodes();
					// System.out.println( sourceList.getLength()+"----------------------");

					// for(int file=1;file<sourceList.getLength();file+=2){

					String file1 = sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild()
							.getNodeValue();
					String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild()
							.getNodeValue();
					String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild()
							.getNodeValue();

					String file2 = sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild()
							.getNodeValue();
					String startline2 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild()
							.getNodeValue();
					String endline2 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild()
							.getNodeValue();

					ArrayList<String> clonePair = new ArrayList<String>();
					clonePair.add(file1);
					clonePair.add(startline1);
					clonePair.add(endline1);
					clonePair.add(file2);
					clonePair.add(startline2);
					clonePair.add(endline2);
					reportClones.add(clonePair);

					// myListofFiles.add(file1);
					// myListofFiles.add(file2);

					// }
				}

				// System.out.println("Done");

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return reportClones;

	}

	public static ArrayList<ArrayList<String>> convertClassIntoPairs(ArrayList<ArrayList<String>> cloneClasses) {

		ArrayList<ArrayList<String>> allClones = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < cloneClasses.size(); i++) {

			if (cloneClasses.get(i).size() == 6) {
				allClones.add(cloneClasses.get(i));
				System.out.println(cloneClasses.get(i).size() + " This class is a clone pair");
			} else { // make a loop inside the class and get all clone pairs

				for (int j = 0; j < cloneClasses.get(i).size() - 3; j += 3) {

					for (int k = j + 3; k < cloneClasses.get(i).size(); k += 3) {

						System.out.print(j + "-" + k + "   ");

					}
					System.out.println();
				}
				System.out.println();

			}

		}

		return allClones;
	}

	// parse nicad results and convert it to windows

	public static ArrayList<ArrayList<String>> loadSimianReport(File file, Configuration config) {
		
		String projectHomeAddress=config.projectAddress;
		File projectAddress=new File(projectHomeAddress);
		projectHomeAddress=projectHomeAddress.substring(0, projectHomeAddress.indexOf(projectAddress.getName())-1);
		
		boolean found = false;
		int cloneClassId = 1; // starting clone class id. Each clone class has different id
		int cloneId = 1; // starting clone id. Each clone has different id.
		String cloneInfo = ""; // string that holds cloned fragments information for a class
		int numOfFragments = 0; // numf of fragments in a clone class
		int numOfLines = 0; // num of Lines in a clone class
		FileReader fr = null;
		String tool = "";
		String processingTime = "";
		String processedLines = "";
		String processedFiles = "";
		String duplicateLines = "";
		String duplicateFiles = "";
		String duplicateBlocks = "";
		int c = 0;
		ArrayList<ArrayList<String>> allClones = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> cloneClass = new ArrayList<ArrayList<String>>();

		try {
			LineNumberReader lr = new LineNumberReader(new FileReader(file));
			lr.skip(Long.MAX_VALUE);
			int totalLinesInFile = lr.getLineNumber();
			lr.close();
			lr = new LineNumberReader(new FileReader(file));
			tool = lr.readLine();
			// System.out.print(tool);
			for (int i = 1; i < totalLinesInFile - 3; i++) {
				lr.readLine();
			}
			String s = lr.readLine();
			String tokenizedStr[] = s.split("\\s+");
			// System.out.println("??"+s+"TotalLinesInFIles = "+totalLinesInFile);

			duplicateLines = tokenizedStr[1];
			duplicateFiles = tokenizedStr[5];
			duplicateBlocks = tokenizedStr[8];

			tokenizedStr = lr.readLine().split("\\s+");
			processedFiles = tokenizedStr[tokenizedStr.length - 2];
			processedLines = tokenizedStr[4];

			tokenizedStr = lr.readLine().split("\\s+");
			processingTime = tokenizedStr[2];

			lr.close();

			
		} catch (FileNotFoundException ex) {
			// Logger.getLogger(FileConverterForSimian.class.getName()).log(Level.SEVERE,
			// null, ex);
		} catch (Exception e) {
			e.printStackTrace();
		}


		try {
			fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String lineStr = null;

			try {
				while ((found == true) || (lineStr = br.readLine()) != null) {
					ArrayList<String> clone = new ArrayList<String>();
					
					if ((found == true) || (lineStr.startsWith("Found") && lineStr.endsWith("files:"))) { // Starting of
																											// a clone
																											// class
						found = false; // reset found

						while (true) {
							lineStr = br.readLine();

							/* A line starting with 'found' indicate the end of a clone class */
							if (lineStr.startsWith("Found")) {
								if (lineStr.startsWith("Found") && lineStr.endsWith("files:")) {
									found = true; // found is set to true to bypass the next lineread in the while loop,
													// posssibly we found another clone class
								}

								break;
							}

							String splittedStr[] = lineStr.trim().split(" "); // split the line containg a cloned
																				// frgament info
							String filePath = "";
							for (int i = 6; i < splittedStr.length; i++) {
								filePath = filePath + " " + splittedStr[i];
							}
						
							filePath=filePath.replace("/","\\");

							int pos = filePath.indexOf(projectAddress.getName());
							filePath=filePath.substring(pos-1);
							filePath=projectHomeAddress.concat(filePath);					
							clone.add(filePath.trim());
							clone.add(splittedStr[2]);
							clone.add(splittedStr[4]);

							
						}
						c++;

						cloneClass.add(clone);
						
						
						//allClones.add(clone);
						// System.out.println(allClones.get(c-1));
						// clone.clear();


						// Now reset the variables for the next clone class
						numOfFragments = 0;
						numOfLines = 0;
						cloneInfo = "";
						cloneClassId++; // increase clone class Id to uniquely identify each clone class

					} // end of clone class
					
					System.out.println(cloneClass.size()+ " "+cloneClass);
					System.out.println(cloneClass.get(0));
					
					if ( cloneClass.size()>1) {
						
					for (int i = 0; i < cloneClass.get(0).size() - 4; i+=3) {

						for (int j = i +3; j <  cloneClass.get(0).size(); j+=3) {
							ArrayList<String> temp = new ArrayList<String>();
							temp.add(cloneClass.get(0).get(i));
							temp.add(cloneClass.get(0).get(i+1));
							temp.add(cloneClass.get(0).get(i+2));

							temp.add(cloneClass.get(0).get(j+0));
							temp.add(cloneClass.get(0).get(j+1));
							temp.add(cloneClass.get(0).get(j+2));
							allClones.add(temp);
						}
					}

					} //if 
					
					cloneClass.clear();

				}

				br.close();
				fr.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			// Logger.getLogger(FileConverterForSimian.class.getName()).log(Level.SEVERE,
			// null, ex);
		} finally {
			try {
				fr.close();
			} catch (IOException ex) {
				// Logger.getLogger(FileConverterForSimian.class.getName()).log(Level.SEVERE,
				// null, ex);
			}

			System.out.println(" number of clone classes: " + c);

			// System.out.println(allClones.get(2));
			return allClones; // return the formatted result
		}
	}



}
