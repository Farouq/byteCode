package validation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.*;

import java.io.File;
import configuration.Configuration;

public class Recall {

	public static void main(String[] args)  throws IOException{
		// TODO Auto-generated method stub
		String validatedClonesPath = args[0];
		 String detectorClonesPath = args[1];

		ArrayList<ClonePair> validatedClones = loadTrueClones(validatedClonesPath);

		 ArrayList<ClonePair> detector0Clones=loadDetector0Clones(detectorClonesPath);
		 

		System.out.println(" Number of  validated true Clones: " + validatedClones.size());
		System.out.println(" Number of clones detected by Tool Number 00: " + detector0Clones.size());
		System.out.println(" Number of clones detected int the validatedClones are  : " + trueClonesIntersect(validatedClones,detector0Clones));

	}
	
	
	public static int trueClonesIntersect(ArrayList<ClonePair> validatedClones,ArrayList<ClonePair> detector0Clones) {
		int counter=0;
		
		for(ClonePair trueClone: validatedClones) {
			
			for(ClonePair detected: detector0Clones) {
				
				if (isOverlap(trueClone,detected))
				{
					counter++;
					break;
				}
				
				
			}
			
		}
		
		
		
		return counter;
		
	}
	
	public static boolean isOverlap(ClonePair clone1, ClonePair clone2) {
		boolean found = false;


		if (  (  (clone1.file1.equals(clone2.file1) && clone1.startLine1<=clone2.endLine1 && clone1.endLine1>=clone2.startLine1 )&&
				 (clone1.file2.equals(clone2.file2) && clone1.startLine2<=clone2.endLine2 && clone1.endLine2>=clone2.startLine2 ))
				||	
				(  (clone1.file1.equals(clone2.file2) && clone1.startLine1<=clone2.endLine2 && clone1.endLine1>=clone2.startLine2 )&&
				   (clone1.file2.equals(clone2.file1) && clone1.startLine2<=clone2.endLine1 && clone1.endLine2>=clone2.startLine1 ))
				)
		{
			found = true;
		}
		


		return found;
	}
	

	public static int trueClones(ArrayList<ClonePair> validatedClones) {
		int c = 0;

		for (ClonePair clone : validatedClones) {
			if (clone.valid)
				c++;
		}

		return c;
	}

	public static ArrayList<ClonePair> loadTrueClones(String directory) {

		ArrayList<ClonePair> validatedClones = new ArrayList<ClonePair>();

		File parentDir = new File(directory);

		for (File file : parentDir.listFiles()) {

			String line = null;

			try {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);

				// line = bufferedReader.readLine();

				while ((line = bufferedReader.readLine()) != null) {

					// System.out.println(line);

					line = line.trim();
					String[] parts = line.split(",");
					// int toolid = Integer.parseInt(parts[0]);
					// int cloneid = Integer.parseInt(parts[1]);
					Boolean validation;
					if (parts[2].equals("undecided")) {
						validation = Boolean.valueOf(false);
						//System.out.println(" undecided clone");
					} else {
						if (parts[2].equals("true")) {
							validation = Boolean.valueOf(true);
						} else {
							validation = Boolean.valueOf(false);
						}
					}

					String fragment1 = parts[3];
					String fragment2 = parts[4];

					fragment1 = fragment1.trim();
					int i = fragment1.lastIndexOf(" ");
					int end1 = Integer.parseInt(fragment1.substring(i).trim());
					fragment1 = fragment1.substring(0, i).trim();

					i = fragment1.lastIndexOf(" ");
					int start1 = Integer.parseInt(fragment1.substring(i).trim());
					String file1 = fragment1.substring(0, i).trim();

					fragment2 = fragment2.trim();
					i = fragment2.lastIndexOf(" ");
					int end2 = Integer.parseInt(fragment2.substring(i).trim());
					fragment2 = fragment2.substring(0, i).trim();
					i = fragment2.lastIndexOf(" ");
					int start2 = Integer.parseInt(fragment2.substring(i).trim());
					String file2 = fragment2.substring(0, i).trim();

					ClonePair clone = new ClonePair(validation, file1, start1, end1, file2, start2, end2);
					// System.out.println(clone.resultString());

					if (validation) {
						validatedClones.add(clone);
					}
				}

				bufferedReader.close();
			} catch (FileNotFoundException ex) {
				System.out.println("Unable to open file '" + file + "'");
			} catch (IOException ex) {
				System.out.println("Error reading file '" + file + "'");
				// Or we could just do this:
				// ex.printStackTrace();
			}

		} // for (File file:

		return validatedClones;
	}

	public static ArrayList<ClonePair> loadDetector0Clones(String directory) throws IOException {

		ArrayList<ClonePair> validatedClones = new ArrayList<ClonePair>();

		File parentDir = new File(directory);

		for (File file : parentDir.listFiles()) {

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

				DocumentBuilder db = dbf.newDocumentBuilder();

				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
				Element root = doc.getDocumentElement();

				// System.out.println(root.getNodeName()+"--------");

				NodeList nl = root.getElementsByTagName("cloneinfo");
				nl = root.getElementsByTagName("clone_pair");

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

						Boolean validation = false;

						ClonePair clone = new ClonePair(validation, file1, Integer.parseInt(startline1),Integer.parseInt(endline1), file2, Integer.parseInt(startline2),Integer.parseInt(endline2));
						validatedClones.add(clone);

					}

					// System.out.println("Done");

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} // for (File file:

		return validatedClones;
	}
	
	public static ArrayList<ClonePair> loadNiCadClones(String directory) throws IOException {

		ArrayList<ClonePair> validatedClones = new ArrayList<ClonePair>();

		File parentDir = new File(directory);

		for (File file : parentDir.listFiles()) {

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

				DocumentBuilder db = dbf.newDocumentBuilder();

				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
				Element root = doc.getDocumentElement();

				// System.out.println(root.getNodeName()+"--------");

				NodeList nl = root.getElementsByTagName("cloneinfo");
				nl = root.getElementsByTagName("clone_pair");

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

						Boolean validation = false;

						ClonePair clone = new ClonePair(validation, file1, Integer.parseInt(startline1),Integer.parseInt(endline1), file2, Integer.parseInt(startline2),Integer.parseInt(endline2));
						validatedClones.add(clone);

					}

					// System.out.println("Done");

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} // for (File file:

		return validatedClones;
	}

}

class ClonePair2 {
	Boolean valid;
	String file1;
	int startLine1;
	int endLine1;
	String file2;
	int startLine2;
	int endLine2;

	ClonePair2() {
		valid = false;
		file1 = "";
		startLine1 = 0;
		endLine1 = 0;
		file2 = "";
		startLine2 = 0;
		endLine2 = 0;
	}

	ClonePair2(boolean v, String f1, int s1, int e1, String f2, int s2, int e2) {

		valid = v;
		file1 = f1;
		startLine1 = s1;
		endLine1 = e1;
		file2 = f2;
		startLine2 = s2;
		endLine2 = e2;

	}

	public String resultString() {
		if (this.valid == null) {
			return "Undecided " + "," + this.file1 + "," + this.startLine1 + " , " + this.endLine1 + " , " + this.file2
					+ " , " + this.startLine2 + " , " + this.endLine2;
		}

		if (this.valid) {
			return "true" + "," + this.file1 + "," + this.startLine1 + " , " + this.endLine1 + " , " + this.file2
					+ " , " + this.startLine2 + " , " + this.endLine2;
		}

		return "false" + "," + this.file1 + "," + this.startLine1 + " , " + this.endLine1 + " , " + this.file2 + " , "
				+ this.startLine2 + " , " + this.endLine2;
	}

}