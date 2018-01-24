package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configuration.Configuration;


public class Merge {
	
	private static ArrayList<ArrayList<String>> LevInstructions = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> jaccInstructions = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> lcsCalls = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> jaccCalls = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> mergedClones = new ArrayList<ArrayList<String>>();



	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Configuration config= Configuration.loadFromFile();
		mergeReports(config);
		
	}

	public static void mergeReports( Configuration config) throws Exception{
		
		LevInstructions= parseCloneReport(config.reportAddress+"\\InstructionLevenshtien"+ config.instructionLevThreshold+".xml");
		jaccInstructions= parseCloneReport(config.reportAddress+"\\InstructionsJaccard"+ config.instructionJaccThreshold+".xml");
		lcsCalls= parseCloneReport(config.reportAddress+"\\CalledMethodsLCS"+ config.callsLCSThreshold+".xml");
		jaccCalls= parseCloneReport(config.reportAddress+"\\CalledMethodsJacc"+ config.callsJaccardThreshold+".xml");
//		System.out.println(LevInstructions.size()+"**************************");
//		System.out.println(jaccInstructions.size()+"**************************");
//		System.out.println(lcsCalls.size()+"**************************");
//		System.out.println(jaccCalls.size()+"**************************");

		int clonePairs =0;
		
		for(int i=0; i<LevInstructions.size();i++ ){
			
			ArrayList<String> clonePair=LevInstructions.get(i);
			ArrayList<String> clonePairSwaped= swapFileOrder(clonePair);
			
			if( (jaccInstructions.contains(clonePair) || jaccInstructions.contains(clonePairSwaped)) &&
				(lcsCalls.contains(clonePair) || lcsCalls.contains(clonePairSwaped)) &&
				(jaccCalls.contains(clonePair) || jaccCalls.contains(clonePairSwaped)) 					
					) {
				//System.out.println(clonePair);
				mergedClones.add(clonePair);

				clonePairs++;
			}


		}
		
		writeToXMLFile(config,mergedClones);
		
		System.out.println("The final report genarated, Semantic Clones Report Total number of clones "+clonePairs);
	//	System.out.println(mergedClones.size());



		
	}
	
	private static void writeToXMLFile(Configuration config,ArrayList<ArrayList<String>> meregedClones) throws Exception
	{
		String outputFileAddress=config.reportAddress+"\\Semantic Clones Report.xml";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		bufferedWriter.write("<clones>");
		bufferedWriter.newLine();
		


		for(int i=0; i<meregedClones.size();i++ ){
			
			bufferedWriter.write( "<clone_pair>");
			bufferedWriter.newLine();
			//System.out.println(d );
			// first fragment
			bufferedWriter.write( "<clone_fragment file=\""+meregedClones.get(i).get(0)+"\" startline=\""+ meregedClones.get(i).get(1) +"\" endline=\""+ meregedClones.get(i).get(2)+"\">");
			bufferedWriter.newLine();
			bufferedWriter.write("<![CDATA["+ getSourceCode( config, meregedClones.get(i).get(0), meregedClones.get(i).get(1))+"]]>");
			bufferedWriter.newLine();
			bufferedWriter.write("</clone_fragment>");
			bufferedWriter.newLine();
			//second fragment
			bufferedWriter.write( "<clone_fragment file=\""+meregedClones.get(i).get(3)+"\" startline=\""+ meregedClones.get(i).get(4) +"\" endline=\""+ meregedClones.get(i).get(5)+"\">");
			bufferedWriter.newLine();
			bufferedWriter.write("<![CDATA["+getSourceCode( config, meregedClones.get(i).get(3), meregedClones.get(i).get(4))+"]]>");
			bufferedWriter.newLine();
			bufferedWriter.write("</clone_fragment>");
			bufferedWriter.newLine();
			//close pair
			bufferedWriter.write("</clone_pair>");
			bufferedWriter.newLine();
			
		}
		
		bufferedWriter.write("</clones>");
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	
	public static String getSourceCode(Configuration config, String fileName, String startLine) throws IOException{

		String source=null;
		DocumentBuilderFactory dbfs = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder dbs = dbfs.newDocumentBuilder();
			Document docs = dbs.parse(config.disassebledAddress+"/allFiles.xml_0_source.xml");
			docs.getDocumentElement().normalize();
			Element roots = docs.getDocumentElement();
			NodeList nls = roots.getElementsByTagName("source_elements");

			//		System.out.println(nls.getLength());

			if(nls.getLength()>0){
				NodeList sourceLists = nls.item(0).getChildNodes();
				boolean found=false;
				int k=0;

				while(!found && k<sourceLists.getLength()){
					Node sources = sourceLists.item(k);
					k++;
					if (sources.getNodeType() != Node.ELEMENT_NODE) 
						continue;

					String files = sources.getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startlines = sources.getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					//String endlines = sources.getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					String contents = sources.getFirstChild().getTextContent();

					if(fileName.equals(files) && startLine.equals(startlines))
					{
						found=true;
						source=contents;

					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return source;
	}
	
	public static ArrayList<String> swapFileOrder ( ArrayList<String> clonePair) throws IOException{
		ArrayList<String> temp=new ArrayList<String>();

		temp.add(clonePair.get(3));
		temp.add(clonePair.get(4));
		temp.add(clonePair.get(5));
		temp.add(clonePair.get(0));
		temp.add(clonePair.get(1));
		temp.add(clonePair.get(2));

		return temp;
		
	}
	
	public static ArrayList<ArrayList<String>> parseCloneReport ( String rawFunctionsFileName) throws IOException{

		 ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();


		
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
//				System.out.println( sourceList.getLength()+"----------------------");



				//for(int file=1;file<sourceList.getLength();file+=2){

						
						String file1= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
						String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
						String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

						
						String file2= sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
						String startline2 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
						String endline2 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					
						ArrayList<String> clonePair=new ArrayList<String>();
						clonePair.add(file1);
						clonePair.add(startline1);
						clonePair.add(endline1);
						clonePair.add(file2);
						clonePair.add(startline2);
						clonePair.add(endline2);
						reportClones.add(clonePair);



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
