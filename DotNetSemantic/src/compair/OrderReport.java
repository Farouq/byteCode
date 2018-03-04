package compair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.Collections; 
import java.util.*;

import configuration.Configuration;

public class OrderReport {

	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// to load the project address. Project address used to process project path
		Configuration config=Configuration.initialize(args[0]);


		
		String reportAddress=config.reportAddress+"\\InstructionLevenshtien0.8.xml";

		 ArrayList<ArrayList<String>> clones = parseCloneReport(config,reportAddress);
		//ArrayList<String> clone =new ArrayList<String>();

		
		Collections.sort(clones, new Comparator<ArrayList<String>>() {
		    @Override
		    public int compare(ArrayList<String> one, ArrayList<String> two) {
		        return two.get(0).compareTo(one.get(0));
		    }
		});

		for (ArrayList<String> clone:clones) {
			System.out.println(clone.toString());
			
		}
		
	}
	
	public static ArrayList<ArrayList<String>> parseCloneReport (Configuration config, String rawFunctionsFileName) throws IOException{

	     ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();
		File projectAddress= new File(config.projectAddress);

	File fileName = new File(rawFunctionsFileName);

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try{
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(fileName);
		doc.getDocumentElement().normalize();
		Element root = doc.getDocumentElement();

			//	System.out.println(root.getNodeName()+"--------");

		NodeList nl = root.getElementsByTagName("cloneinfo");

				//	System.out.println(nl.getLength());
				//	System.out.println(nl.item(0).getAttributes().getNamedItem("npairs").getFirstChild().getNodeValue());

				//	nl = root.getElementsByTagName("class");  //in case of xml classes report
		
		nl = root.getElementsByTagName("clone_pair");


				//System.out.println(nl.getLength()+"**************************");

		if(nl.getLength()>0){

			
			for (int group=0;group<nl.getLength();group++)
			{
				NodeList sourceList = nl.item(group).getChildNodes();
//			System.out.println( sourceList.getLength()+"----------------------");


				Node nNode = nl.item(group);
				Element eElement = (Element) nNode;

			//for(int file=1;file<sourceList.getLength();file+=2){

				//	int similarity=Integer.parseInt(sourceList.item(0).getAttributes("id"));
				
				 //   System.out.println(eElement.getAttribute("Similarity"));
				
				    double similarity=Double.parseDouble(eElement.getAttribute("Similarity"));
					String file1= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

					
					String file2= sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline2 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline2 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

				//	System.out.println(projectHomeAddress);
					//file1=file1.substring(pos);

					
			//	    pos = file2.indexOf(projectAddress.getName());
			//		file2=file2.substring(pos);
					
					
					ArrayList<String> clonePair=new ArrayList<String>();
					clonePair.add(Double.toString(similarity));
					clonePair.add(file1);
					clonePair.add(startline1);
					clonePair.add(endline1);
					clonePair.add(file2);
					clonePair.add(startline2);
					clonePair.add(endline2);
					reportClones.add(clonePair);


				//    System.out.println(clonePair);


			//	}
			}

		//	System.out.println("Done");


		}
	}
	

	catch (Exception e) {
		e.printStackTrace();
	}
	return reportClones;

}
}
