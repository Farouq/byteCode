
package compair;
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

public class CompairToSimcad {
	private static String projectHomeAddress="";

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// to load the project address. Project address used to process project path
		Configuration config=Configuration.initialize(args[0]);

//		System.out.println(config.reportAddress);
		// need tow files. 1- simcad report and must be copied into Rrport folder of the project
		// and semantic report
		
		String reportAddress=config.reportAddress+"\\Semantic Clones Report.xml";
		String simcadReportAddress=config.reportAddress+"\\simcad_function_clone-pairs_type-1-2-3_generous.xml";
		
		

		// Load the clone reports data into ArrayList
		 ArrayList<ArrayList<String>> clones =parseCloneReport (config, reportAddress );
		 ArrayList<ArrayList<String>> clonesEnd = parseCloneReport2 (config, reportAddress );


		 ArrayList<ArrayList<String>> simcadClones=parsesimcadPairs(config, simcadReportAddress);		 
		 ArrayList<ArrayList<String>> simcadClonesEnd= parsesimcadPairs2(config, simcadReportAddress);
		 

		 
		 
			System.out.println(simcadClones.size());
			System.out.println(clones.size());
			
			System.out.println(simcadClones);
			System.out.println(simcadClonesEnd);
			
			System.out.println(clones);
			System.out.println(clonesEnd);

			
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
	
	public static ArrayList<ArrayList<String>> parsesimcadPairs( Configuration config, String rawFunctionsFileName) throws Exception{

//		Configuration config=Configuration.loadFromFile();

		ArrayList<ArrayList<String>> reportClones = new ArrayList<ArrayList<String>>();


		File projectAddress= new File(config.projectAddress);
		
//		System.out.println(config.projectAddress);
//		System.out.println(projectAddress.getName());
//		System.out.println(projectAddress.getPath());
		
		

		File fileName = new File(rawFunctionsFileName);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileName);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();

		//		System.out.println(root.getNodeName()+"--------");

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
					clonePair.add(startline1);
					clonePair.add(endline1);
					clonePair.add(file22);
					clonePair.add(startline22);
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
	
	
	
		
	
}
