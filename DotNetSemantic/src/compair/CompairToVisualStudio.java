package compair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configuration.Configuration;
import validation.Write;

public class CompairToVisualStudio {

	public static void main(String[] args) throws Exception {

		Configuration config = Configuration.initialize(args[0]);

		String vSReportAddress = config.reportAddress + "\\ASXGui.txt";
		ArrayList<ArrayList<String>> VSClones = readVSCloneReport(config, vSReportAddress);
		System.out.println("Number of clone pairs detected by Visual Studio  " + VSClones.size());
		
		
		Write.generateTestGroup( config,  VSClones, "05");
		System.out.println("-----------------------group selected and printed---------------------");
		

		String reportAddress = config.reportAddress + "\\FinalCloneReportWeighted Similarities.0.75.xml";
		ArrayList<ArrayList<String>> clones = parseCloneReport(config, reportAddress);
		// ArrayList<ArrayList<String>> clonesEnd = parseCloneReport2 (config,
		// reportAddress );
		System.out.println("Number of clone pairs detected by my tool " + clones.size());

		ArrayList<ArrayList<String>> both = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> semOnly = new ArrayList<ArrayList<String>>();
		int counter = 0;
		
		
		
		Set<ArrayList<String>> myCloneSet = generateListOfFiles(clones);
		System.out.println("Number of clone fragments  detected by my tool: " + myCloneSet.size());
		ArrayList<ArrayList<String>> cloneList = new ArrayList<ArrayList<String>>();
		//ArrayList<String> clone=new ArrayList<String>();
		cloneList.addAll(myCloneSet);
		cloneList=sortReport(cloneList);
//		for (ArrayList<String> clone:cloneList) {			
//			System.out.println(clone);
//		}
		
		System.out.println("Total number of cloned LOC by my tool: " +clonedLoc(cloneList));
		
		
		Set<ArrayList<String>> VSCloneSet = generateListOfFiles(VSClones);
		System.out.println("Number of clone fragments  detected by VS : " + VSCloneSet.size());
		ArrayList<ArrayList<String>> VSCloneList = new ArrayList<ArrayList<String>>();
		//ArrayList<String> clone=new ArrayList<String>();
		VSCloneList.addAll(VSCloneSet);
		VSCloneList=sortReport(VSCloneList);
//		for (ArrayList<String> clone:VSCloneList) {			
//			System.out.println(clone);
//		}
//		
		
		System.out.println("Total number of cloned LOC by Visual Studio: " +clonedLoc(VSCloneList));
		System.out.println("Total number of common Loc between two detectors: " + commonLoc(cloneList,VSCloneList));

		
//		for(int i=0; i<VSClones.size();i++ ){	
//		
//		System.out.println(VSClones.get(i));
//		
//	}
		
		for (int i = 0; i < clones.size(); i++) {

			if (foundInVS(clones.get(i), VSClones)) {
				both.add(clones.get(i));
				counter++;
			} else {
				semOnly.add(clones.get(i));

			}
		}
		
		
		System.out.println("Number of common clones "+ both.size());
		System.out.println("number of Extra clones " +semOnly.size());
		System.out.println("number of Missed clones  take the difference" );

	}
	
	public static int max( int a, int b){
		if (a>b) {
			return a;
		}
		else { 
			return b;
		}
		
	}
	
	public static int min( int a, int b){
		if (a<b) {
			return a;
		}
		else { 
			return b;
		}
		
	}
	
	public static int commonLoc(ArrayList<ArrayList<String>> myList, ArrayList<ArrayList<String>> detectorList) {

		int loc = 0;

		for (ArrayList<String> clone : myList) {
			
			for (ArrayList<String> cloneToCompare : detectorList) {
				
				if(clonesIntersect(clone,cloneToCompare)) {
					
					int l1 = Integer.parseInt(clone.get(1).trim());
					int r1 = Integer.parseInt(clone.get(2).trim());
										
					int l2 = Integer.parseInt(cloneToCompare.get(1).trim());
					int r2 = Integer.parseInt(cloneToCompare.get(2).trim());
					
					loc+= min(r1,r2)- max(l1,l2)+1;
				}

			}
			

		}

		return loc;
	}
	
	public static boolean clonesIntersect(ArrayList<String> cloneA, ArrayList<String> cloneB) {
		boolean intersect = false;


		String f1 = cloneA.get(0).toLowerCase();
		int l1 = Integer.parseInt(cloneA.get(1).trim());
		int r1 = Integer.parseInt(cloneA.get(2).trim());
		
		String f2 = cloneB.get(0).toLowerCase();
		int l2 = Integer.parseInt(cloneB.get(1).trim());
		int r2 = Integer.parseInt(cloneB.get(2).trim());

			if (f1.equals(f2) && l1 <= r2 && r1 >= l2) {
				intersect = true;
			}

		return intersect;
	}
	
	public static Set<ArrayList<String>> generateListOfFiles( ArrayList<ArrayList<String>> clonePairs){
		Set<ArrayList<String>> files = new HashSet<ArrayList<String>>();
		
		for (int i=0; i<clonePairs.size(); i++){
			ArrayList<String> clone1=new ArrayList<String>();
			ArrayList<String> clone2=new ArrayList<String>();
			clone1.add(clonePairs.get(i).get(0));
			clone1.add(clonePairs.get(i).get(1));
			clone1.add(clonePairs.get(i).get(2));
			clone2.add(clonePairs.get(i).get(3));
			clone2.add(clonePairs.get(i).get(4));
			clone2.add(clonePairs.get(i).get(5));
			files.add(clone1);
			files.add(clone2);
			
		}
		
		return files;
	}

	public static ArrayList<ArrayList<String>> sortReport( ArrayList<ArrayList<String>> clones){

		Collections.sort(clones, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> one, ArrayList<String> two) {
				return two.get(0).compareTo(one.get(0));
			}
		});

		return clones;
	}
	
	
	public static int clonedLoc( ArrayList<ArrayList<String>> clonesList){

		int loc=0;

		for (ArrayList<String> clone:clonesList) {			
		loc+= Integer.parseInt(clone.get(2).trim())-Integer.parseInt(clone.get(1).trim())+1;
	}

		return loc;
	}
	
	
	public static boolean foundInVS(ArrayList<String> clonePair, ArrayList<ArrayList<String>> conQATClones) {
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
					|| ((fA1.equals(fB2) && sA1 <= eB2 && eA1 >= sB2) && (fB1.equals(fA2) && sB1 <= eA2 && eB1 >= sA2))) {
				found = true;
				// System.out.println(clonePair);
				break;
			}
		}

		return found;
	}

	public static ArrayList<ArrayList<String>> readVSCloneReport(Configuration config, String file) {

		int classSize = 0;
		String fileName = "";
		String s_e_Line = "";
		String startLine = "";
		String endLine = "";

		ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> cloneClass = new ArrayList<ArrayList<String>>();

		String line = null;

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			line = bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {

				line = line.trim();
				classSize = Integer.parseInt(line.substring(line.length() - 3, line.length()).trim());


			//	System.out.println("Clas size " + classSize);

				for (int i = 0; i < classSize; i++) {
					line = bufferedReader.readLine();
					line = line.trim();

				/*	String tokenizedStr[] = line.split("\\s+");
					fileName = tokenizedStr[2];
					s_e_Line = tokenizedStr[4];
					tokenizedStr = s_e_Line.split("-");
					startLine = tokenizedStr[0];
					endLine = tokenizedStr[1];
*/
					fileName=line.substring(line.indexOf("C:"), line.indexOf("lines")).trim();
					s_e_Line=line.substring(line.lastIndexOf(" "));
					String tokenizedStr[] = s_e_Line.split("-");
					startLine = tokenizedStr[0];
					endLine = tokenizedStr[1];
					
				//	System.out.println(fileName+ "  "+startLine+"   "+endLine);
					
					ArrayList<String> clone = new ArrayList<String>();

					clone.add(fileName);
					clone.add(startLine);
					clone.add(endLine);
					cloneClass.add(clone);
				}

		//		System.out.println("Clas size from the arrayList  " + cloneClass.size());

				for (int i = 0; i < cloneClass.size() - 1; i++) {

					for (int j = i + 1; j < cloneClass.size(); j++) {
						ArrayList<String> temp = new ArrayList<String>();
						temp.add(cloneClass.get(i).get(0));
						temp.add(cloneClass.get(i).get(1));
						temp.add(cloneClass.get(i).get(2));

						temp.add(cloneClass.get(j).get(0));
						temp.add(cloneClass.get(j).get(1));
						temp.add(cloneClass.get(j).get(2));
						reportClones.add(temp);
					}
				}

				cloneClass.clear();

			}

			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + file + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + file + "'");
			// Or we could just do this:
			// ex.printStackTrace();
		}

		return reportClones;
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

					String file1 = sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

					String file2 = sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline2 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline2 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

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

}
