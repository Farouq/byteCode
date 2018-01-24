package compair;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configuration.Configuration;

public class CompairToConQAT {
	
	private static String projectHomeAddress="";

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// to load the project address. Project address used to process project path
		Configuration config=Configuration.initialize(args[0]);

//		System.out.println(config.reportAddress);
		// need tow files. 1- simcad report and must be copied into Rrport folder of the project
		// and semantic report
		
		String reportAddress=config.reportAddress+"\\Semantic Clones Report.xml";
		String conQATReportAddress=config.reportAddress+"\\clones.xml";
		
		

		// Load the clone reports data into ArrayList
		ArrayList<ArrayList<String>> clones =parseCloneReport (config, reportAddress );
		// ArrayList<ArrayList<String>> clonesEnd = parseCloneReport2 (config, reportAddress );
		System.out.println(clones.size());
		System.out.println(clones);


		

		 ArrayList<ArrayList<String>> conQATClones=parseconQATPairs(config, conQATReportAddress);		 
		// ArrayList<ArrayList<String>> conQATClonesEnd= parsesimcadPairs2(config, conQATReportAddress);
		 writeToXMLFileAsSimCad(config,conQATClones,"conQAT clones in SimCad format");
		 
		 ArrayList<ArrayList<String>> both = new ArrayList<ArrayList<String>>();
		 ArrayList<ArrayList<String>> semOnly = new ArrayList<ArrayList<String>>();
		 
			System.out.println(conQATClones.size());
			System.out.println(conQATClones);
			int counter=0;
			
			for(int i=0; i<clones.size();i++ ){			
				
				if (foundInConQAT(clones.get(i), conQATClones )){
					both.add(clones.get(i));
					counter++;
				}else{
					semOnly.add(clones.get(i));

				}
				
			}
			System.out.println(counter);

			
			
//			System.out.println(conQATClonesEnd);
			

//			System.out.println(clonesEnd);

			/*	 		
			 ArrayList<ArrayList<String>> both = new ArrayList<ArrayList<String>>();
			 ArrayList<ArrayList<String>> semOnly = new ArrayList<ArrayList<String>>();
			 ArrayList<ArrayList<String>> simcadOnly = new ArrayList<ArrayList<String>>();
			 
			 for(int i=0; i<clonesEnd.size();i++ ){
					
					ArrayList<String> clonePair=clonesEnd.get(i);
					ArrayList<String> clonePairSwaped= swapFileOrder(clonePair);
					
					if( (simcadClonesEnd.contains(clonePair) || simcadClonesEnd.contains(clonePairSwaped))  ) {
						//System.out.println(clonePair);
						both.add(clones.get(i));
						}
					else{
						semOnly.add(clones.get(i));
					}
			 }
			 
				System.out.println("Bothhhhhhh     "+both.size());
				System.out.println("Extraaaa    "+semOnly.size());
				
				
				
				 for(int i=0; i<simcadClonesEnd.size();i++ ){
						
						ArrayList<String> clonePair=simcadClonesEnd.get(i);
						ArrayList<String> clonePairSwaped= swapFileOrder(clonePair);
						
						if( (clonesEnd.contains(clonePair) || clonesEnd.contains(clonePairSwaped))  ) {
							//System.out.println(clonePair);
							}else
							{
								simcadOnly.add(simcadClones.get(i));

							}
						
				 }
			 
					System.out.println("missed    "+simcadOnly.size());
					System.out.println(both);
					System.out.println(semOnly);
					System.out.println(simcadOnly);



					writeToXMLFile(config,both,"both simcad and Sem");
					writeToXMLFile(config,semOnly, "Semantic Only");
					writeToXMLFile(config,simcadOnly, "simcad only");
					*/

	}

	public static boolean foundInConQAT(ArrayList<String> clonePair,  ArrayList<ArrayList<String>> conQATClones){
		boolean found=false;
		//System.out.println(clonePair);
		
		String fA1= clonePair.get(0);
		int sA1 =Integer.parseInt( clonePair.get(1));
		int eA1 =Integer.parseInt( clonePair.get(2));
		String fB1= clonePair.get(3);
		int sB1 =Integer.parseInt( clonePair.get(4));
		int eB1 =Integer.parseInt( clonePair.get(5));
		
		
		
		for(int i=0; i<conQATClones.size();i++ ){
			
			String fA2= conQATClones.get(i).get(0);
			int sA2 =Integer.parseInt( conQATClones.get(i).get(1));
			int eA2 =Integer.parseInt( conQATClones.get(i).get(2));
			
			String fB2= conQATClones.get(i).get(3);
			int sB2 =Integer.parseInt( conQATClones.get(i).get(4));
			int eB2 =Integer.parseInt( conQATClones.get(i).get(5));
			
			//System.out.println(fA1+"  "+fA2);

			
			if((fA1.equals(fA2) && sA1<=eA2 && eA1>=sA2) && (fB1.equals(fB2) && sB1<= eB2 && eB1>=sB2)
				||	(fA1.equals(fB2) && sA1<=eB2 && eA1>=sB2) && (fB1.equals(fA2) && sB1<= eA2 && eB1>=sA2) ){
				found=true;
				System.out.println(clonePair);
				break;
			}
		}
		
		
		return found;
	}
	
	
	
	public static ArrayList<String> swapFileOrder ( ArrayList<String> clonePair) throws IOException{
		ArrayList<String> temp=new ArrayList<String>();

		temp.add(clonePair.get(2));
		temp.add(clonePair.get(3));
		temp.add(clonePair.get(0));
		temp.add(clonePair.get(1));
		return temp;
		}
	
	private static void writeToXMLFile(Configuration config,ArrayList<ArrayList<String>> meregedClones, String fileName) throws Exception
	{
		String outputFileAddress=config.reportAddress+"\\simcad\\"+fileName+".xml";
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
			bufferedWriter.write("<![CDATA["+ getSourceCode( config, meregedClones.get(i).get(0), meregedClones.get(i).get(2))+"]]>");
			bufferedWriter.newLine();
			bufferedWriter.write("</clone_fragment>");
			bufferedWriter.newLine();
			//second fragment
			bufferedWriter.write( "<clone_fragment file=\""+meregedClones.get(i).get(3)+"\" startline=\""+ meregedClones.get(i).get(4) +"\" endline=\""+ meregedClones.get(i).get(5)+"\">");
			bufferedWriter.newLine();
			bufferedWriter.write("<![CDATA["+getSourceCode( config, meregedClones.get(i).get(3), meregedClones.get(i).get(5))+"]]>");
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
	
	public static String getSourceCode(Configuration config, String fileName, String end) throws IOException{

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
					String endlines = sources.getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					String contents = sources.getFirstChild().getTextContent();

					if(fileName.equals(files) && end.equals(endlines))
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
	
	public static ArrayList<ArrayList<String>> parseconQATPairs( Configuration config, String rawFunctionsFileName) throws Exception{

		//		Configuration config=Configuration.loadFromFile();

		ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> cloneClass = new ArrayList<ArrayList<String>>();
		ArrayList<String> clonePair = new ArrayList<String>();


		File projectAddress= new File(config.projectAddress);

		//		System.out.println(config.projectAddress);
		//		System.out.println(projectAddress.getName());
		//		System.out.println(projectAddress.getPath());

		ArrayList<String> listFiles=new ArrayList<String>();

		File fileName = new File(rawFunctionsFileName);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileName);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();

			//		System.out.println(root.getNodeName()+"--------");

			NodeList nl = root.getElementsByTagName("cloneReport");
			nl = root.getElementsByTagName("sourceFile");
			for (int group=0;group<nl.getLength();group++)
			{
				Node nNode=nl.item(group);
				Element e = (Element) nNode;
				// System.out.println("ID "       + e.getAttribute("id"));
				// System.out.println("Path "       + e.getAttribute("location"));
				listFiles.add(e.getAttribute("location"));
			}

		////	System.out.println("number of files "+ listFiles.size() );


			nl = root.getElementsByTagName("cloneClass");

			//System.out.println(nl.getLength()+"**************************");

			
			for (int group=0;group<nl.getLength();group++)
			{
				NodeList sourceList = nl.item(group).getChildNodes();
				for(int file=1;file<sourceList.getLength();file+=2){
					
					String file1= sourceList.item(file).getAttributes().getNamedItem("sourceFileId").getFirstChild().getNodeValue();
					String startline1 = sourceList.item(file).getAttributes().getNamedItem("startLine").getFirstChild().getNodeValue();
					String endline1 = sourceList.item(file).getAttributes().getNamedItem("endLine").getFirstChild().getNodeValue();
//					System.out.println(file1);
//					System.out.println(startline1);
//					System.out.println(endline1);
					int fileID=Integer.parseInt(file1);
					ArrayList<String> clone = new ArrayList<String>();
					clone.add(listFiles.get(fileID));
					clone.add(startline1);
					clone.add(endline1);
					cloneClass.add(clone);
				}
				
				// System.out.println(cloneClass.size());
				// System.out.println(cloneClass);
				
				// build clone pairs from clone classes 
				for (int i=0; i<cloneClass.size()-1; i++){
					
					for (int j=i+1; j<cloneClass.size(); j++){
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
	
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return reportClones;
	}

	
	
	
	public static Boolean foundInLevenshtien(Configuration config, String fileA, String stA, String enA, String fileB, String stB, String enB) throws IOException{
		boolean found = false;

		String rawFunctionsFileName=config.reportAddress+"\\LevenshtienAll"+ config.threshold+".xml";	

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


	//		System.out.println(nl.getLength()+"**************************");


			if(nl.getLength()>0){

				for (int group=0;group<nl.getLength();group++)
				{
					NodeList sourceList = nl.item(group).getChildNodes();
					//				System.out.println( sourceList.getLength()+"----------------------");



					for(int file=1;file<sourceList.getLength();file+=2){

						String file1= sourceList.item(file).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
						int startline=Integer.parseInt(sourceList.item(file).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue() );
						int endline=Integer.parseInt(sourceList.item(file).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue() );



					}


					System.out.println("Done");
				}
			}


		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return found;


	}

	
	public static ArrayList<ArrayList<String>> parsesimcadPairs2( Configuration config, String rawFunctionsFileName) throws Exception{

//		Configuration config=Configuration.loadFromFile();

		ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();


//		System.out.println(config.projectAddress);
		File projectAddress= new File(config.projectAddress);
//		System.out.println(projectAddress.getName());
//		System.out.println(projectAddress.getPath());
		
	

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

			nl = root.getElementsByTagName("ClonePair");


	//		System.out.println(nl.getLength()+"**************************");


			if(nl.getLength()>0){

				for (int group=0;group<nl.getLength();group++)
				{
					NodeList sourceList = nl.item(group).getChildNodes();

					//		System.out.println( sourceList.getLength()+"----------------------");


					//
					//				for(int file=1;file<sourceList.getLength();file+=2){
					//						
					//						String file1= sourceList.item(file).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					//    					int startline=Integer.parseInt(sourceList.item(file).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue() );
					//						int endline=Integer.parseInt(sourceList.item(file).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue() );
					//						
					//						st=startline;
					//						en=endline;
					//
					//					}

					String file1= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();


					String file22= sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline22 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline22 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();



					file1=file1.replace(".ifdefed","");
					file1=file1.replace("/","\\");
					int pos = file1.indexOf(projectAddress.getName());
					file1=file1.substring(pos);
					file1=projectHomeAddress.concat(file1);
				//	file1=file1.substring(42);
				//	file1=c.concat(file1);

					file22=file22.replace(".ifdefed","");
					file22=file22.replace("/","\\");
					pos = file22.indexOf(projectAddress.getName());
					file22=file22.substring(pos);
					file22=projectHomeAddress.concat(file22);
				//	file22=file22.substring(42);
				//	file22=c.concat(file22);

//					if(!foundInLevenshtien( config, file1, startline1, endline1,file22, startline22,endline22 )){
//
//
//					}
					
					ArrayList<String> clonePair=new ArrayList<String>();
					clonePair.add(file1);
					//clonePair.add(startline1);
					clonePair.add(endline1);
					clonePair.add(file22);
					//clonePair.add(startline22);
					clonePair.add(endline22);
					reportClones.add(clonePair);
					
				}



			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return reportClones;
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
//				System.out.println( sourceList.getLength()+"----------------------");



				//for(int file=1;file<sourceList.getLength();file+=2){

						
						String file1= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
						String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
						String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

						
						String file2= sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
						String startline2 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
						String endline2 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					
						int pos = file1.indexOf(projectAddress.getName());
						projectHomeAddress= file1.substring(0,pos);
					//	System.out.println(projectHomeAddress);
						//file1=file1.substring(pos);

						
				//	    pos = file2.indexOf(projectAddress.getName());
				//		file2=file2.substring(pos);
						
						
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
	
	
	
	public static ArrayList<ArrayList<String>> parseCloneReport2 (Configuration config, String rawFunctionsFileName) throws IOException{

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



			//for(int file=1;file<sourceList.getLength();file+=2){

					
					String file1= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline1 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline1 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();

					
					String file2= sourceList.item(3).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline2 = sourceList.item(3).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline2 = sourceList.item(3).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
				
		
					
					ArrayList<String> clonePair=new ArrayList<String>();
					clonePair.add(file1);
					//clonePair.add(startline1);
					clonePair.add(endline1);
					clonePair.add(file2);
					//clonePair.add(startline2);
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
	
	
	private static void writeToXMLFileAsSimCad(Configuration config,ArrayList<ArrayList<String>> meregedClones, String fileName) throws Exception
	{
		String outputFileAddress=config.reportAddress+"\\simcad\\"+fileName+".xml";
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
			bufferedWriter.write("<![CDATA["+ getSourceCode( config, meregedClones.get(i).get(0), meregedClones.get(i).get(1), meregedClones.get(i).get(2))+"]]>");
			bufferedWriter.newLine();
			bufferedWriter.write("</clone_fragment>");
			bufferedWriter.newLine();
			//second fragment
			bufferedWriter.write( "<clone_fragment file=\""+meregedClones.get(i).get(3)+"\" startline=\""+ meregedClones.get(i).get(4) +"\" endline=\""+ meregedClones.get(i).get(5)+"\">");
			bufferedWriter.newLine();
			bufferedWriter.write("<![CDATA["+getSourceCode( config, meregedClones.get(i).get(3),meregedClones.get(i).get(4), meregedClones.get(i).get(5))+"]]>");
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
		
	
	
	
	
	public static String getSourceCode(Configuration config, String fileName, String start, String end) throws IOException{

		 String line = null;
		 String source = null;
		
		try{
			   FileReader fileReader =  new FileReader(fileName);
			   BufferedReader bufferedReader =   new BufferedReader(fileReader);

			   int startLine= Integer.parseInt(start);
			   int endLine= Integer.parseInt(end);
			   
			   int i=0;
			   

			   while((line = bufferedReader.readLine()) != null) {
	              //  System.out.println(line);
	                i++;
	                if(i>=startLine && i<=endLine)
	                	source+="\n"+line;
	                
	            }   
			   
	            bufferedReader.close();         

		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return source;
	}
	
}
