package validation;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

import java.io.LineNumberReader;

import configuration.Configuration;

public class Write {
	private static String projectHomeAddress="";

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		// to load the project address. Project address used to process project path
		Configuration config=Configuration.initialize(args[0]);

		System.out.println(config.reportAddress);
		File projectAddress= new File(config.projectAddress);
		System.out.println(projectAddress.getName());
		// need tow files. 1- Nicad report and must be copied into Rrport folder of the project
		// and semantic report

	
		
		String reportAddress=config.reportAddress+"\\FinalCloneReportWeighted Similarities.0.75.xml";
		String nicadReportAddress=config.reportAddress+"\\scriptcs-0.17.1_functions-blind-clones-0.30.xml";

		// Load the clone reports data into ArrayList
		ArrayList<ArrayList<String>> clones =parseCloneReport (config, reportAddress );

		ArrayList<ArrayList<String>> nicadClones=parseNiCadPairs(config, nicadReportAddress);		 

		System.out.println("NiCad Clone Pairs: "+nicadClones.size());
		System.out.println("My Clone Pairs: "+clones.size());
		

	//	test(nicadClones);



		//int numberOfGroups=1;
		int groupSize=40;
 
		ArrayList<ArrayList<String>> selected=selectUniqueCloneGroups(config, clones, groupSize);
		
	
		writeToTextFile(config,selected,projectAddress.getName()+ "00");
	
	
		ArrayList<ArrayList<String>> toolSelected=selectUniqueCloneGroups(config,nicadClones, groupSize);
		
		
		writeClonesToTextFile(config,toolSelected,projectAddress.getName()+ "01");
	

	
		
	}

	public static void generateTestGroup(Configuration config, ArrayList<ArrayList<String>> clones, String toolIndex)throws Exception {
		int groupSize=40;
		ArrayList<ArrayList<String>> toolSelected=selectUniqueCloneGroups(config,clones, groupSize);
		File projectAddress= new File(config.projectAddress);
		
		writeClonesToTextFile(config,toolSelected,projectAddress.getName()+ toolIndex);
		
		
	}
	
	public static void test(ArrayList<ArrayList<String>> nicadClones)throws Exception {
		
		for (int i=0; i<10;i++) {
			File file=new File(nicadClones.get(i).get(0));
			int l= Integer.parseInt(nicadClones.get(i).get(1));
			int r= Integer.parseInt(nicadClones.get(i).get(2));
			
			System.out.println(nicadClones.get(i).get(0)+"  "+nicadClones.get(i).get(1)+" "+nicadClones.get(i).get(2));
			System.out.println( readSourceFile(file,l,r) );
			System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
			
			System.out.println(nicadClones.get(i).get(3)+"  "+nicadClones.get(i).get(4)+" "+nicadClones.get(i).get(5));

			File file2=new File(nicadClones.get(i).get(3));
			int l2= Integer.parseInt(nicadClones.get(i).get(4));
			int r2= Integer.parseInt(nicadClones.get(i).get(5));
			
			System.out.println( readSourceFile(file,l,r) );
			System.out.println("=======================================================================================================================================");
		}

		
	}
	
	public static ArrayList<ArrayList<String>> selectUniqueCloneGroups( Configuration config, ArrayList<ArrayList<String>> unique,  int groupSize){

		ArrayList<ArrayList<String>> selected = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> selectedAll = new ArrayList<ArrayList<String>>();
	
		Random rand = new Random();
						
			int j=0;
			while(j<groupSize){
				
				int  n = rand.nextInt(unique.size());
				
				if( !selectedAll.contains(unique.get(n))) {
					selectedAll.add(unique.get(n));
					selected.add(unique.get(n));	
					j++;
				}		
				
			}



		return selected;
	}

	

	private static void writeClonesToTextFile(Configuration config,ArrayList<ArrayList<String>> meregedClones, String fileName) throws Exception
	{
		String outputFileAddress=config.testing+"\\"+fileName;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


		for(int i=0; i<meregedClones.size();i++ ){
			bufferedWriter.write("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			bufferedWriter.newLine();
			bufferedWriter.write("0");
			bufferedWriter.newLine();
			bufferedWriter.write("0");
			bufferedWriter.newLine();

		//	SelectRandomUniqueClone();  //to Do

			bufferedWriter.write(meregedClones.get(i).get(0).trim()+" "+ meregedClones.get(i).get(1).trim() +" "+ meregedClones.get(i).get(2).trim());
			bufferedWriter.newLine();
			bufferedWriter.write(meregedClones.get(i).get(3).trim()+" "+ meregedClones.get(i).get(4).trim() +" "+ meregedClones.get(i).get(5).trim());
			bufferedWriter.newLine();
			bufferedWriter.write("----------------------------------------");
			bufferedWriter.newLine();
			bufferedWriter.write( readSourceFile(new File(meregedClones.get(i).get(0)),Integer.parseInt(meregedClones.get(i).get(1).trim()),Integer.parseInt(meregedClones.get(i).get(2).trim()) ));
		//	bufferedWriter.newLine();
			bufferedWriter.write("----------------------------------------");
			bufferedWriter.newLine();
			bufferedWriter.write( readSourceFile(new File(meregedClones.get(i).get(3)),Integer.parseInt(meregedClones.get(i).get(4).trim()),Integer.parseInt(meregedClones.get(i).get(5).trim()) ));
			bufferedWriter.write("----------------------------------------");
			bufferedWriter.newLine();
			

		}
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	
	
	

	
	private static void writeToTextFile(Configuration config,ArrayList<ArrayList<String>> meregedClones, String fileName) throws Exception
	{
		String outputFileAddress=config.testing+"\\"+fileName;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


		for(int i=0; i<meregedClones.size();i++ ){
			bufferedWriter.write("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			bufferedWriter.newLine();
			bufferedWriter.write("0");
			bufferedWriter.newLine();
			bufferedWriter.write("0");
			bufferedWriter.newLine();

		//	SelectRandomUniqueClone();  //to Do

			bufferedWriter.write(meregedClones.get(i).get(0)+" "+ meregedClones.get(i).get(1) +" "+ meregedClones.get(i).get(2));
			bufferedWriter.newLine();
			bufferedWriter.write(meregedClones.get(i).get(3)+" "+ meregedClones.get(i).get(4) +" "+ meregedClones.get(i).get(5));
			bufferedWriter.newLine();
			bufferedWriter.write("----------------------------------------");
		//	bufferedWriter.newLine();
			bufferedWriter.write( getSourceCode( config, meregedClones.get(i).get(0), meregedClones.get(i).get(2)));
		//	bufferedWriter.newLine();
			bufferedWriter.write("----------------------------------------");
		//	bufferedWriter.newLine();
			bufferedWriter.write(getSourceCode( config, meregedClones.get(i).get(3), meregedClones.get(i).get(5)));
			bufferedWriter.write("----------------------------------------");
			bufferedWriter.newLine();
			

		}
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	

	
	private static String readSourceFile(File fileName,int startLine, int endLine) throws IOException {
		
		String source="";
		String str="";
		int line=0;

		 try {
	            LineNumberReader lr = new LineNumberReader(new FileReader(fileName));

	            while((str = lr.readLine())!=null){
	            	line++;
	            	if(line >=startLine && line<=endLine)
	            		source=source+str+"\n";
	            	//System.out.println(str);
	            	
	            	
	            }
	
		

	        }catch(Exception e){e.printStackTrace();}
	
		
		return source;
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

	public static ArrayList<ArrayList<String>> parseNiCadPairs( Configuration config, String rawFunctionsFileName) throws Exception{

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

			//	System.out.println(root.getNodeName()+"--------");

			NodeList nl = root.getElementsByTagName("cloneinfo");

			//	System.out.println(nl.getLength());
			//	System.out.println(nl.item(0).getAttributes().getNamedItem("npairs").getFirstChild().getNodeValue());

			//	nl = root.getElementsByTagName("class");  //in case of xml classes report

			nl = root.getElementsByTagName("clone");

			//	System.out.println(nl.getLength()+"**************************");

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
					//				file1=file1.replace("Monoo-2.10","mono-2.10");

					int pos = file1.indexOf(projectAddress.getName());
					file1=file1.substring(pos);
					file1=projectHomeAddress.concat(file1);
					//	file1=file1.substring(42);
					//	file1=c.concat(file1);

					file22=file22.replace(".ifdefed","");
					file22=file22.replace("/","\\");
					//				file22=file22.replace("Monoo-2.10","mono-2.10");

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


			System.out.println(nl.getLength()+"**************************");


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


	public static ArrayList<ArrayList<String>> parseNiCadPairs2( Configuration config, String rawFunctionsFileName) throws Exception{

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

			nl = root.getElementsByTagName("clone");


			//			System.out.println(nl.getLength()+"**************************");


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
					file1=file1.replace("Monoo-2.10","mono-2.10");

					int pos = file1.indexOf(projectAddress.getName());
					file1=file1.substring(pos);
					file1=projectHomeAddress.concat(file1);
					//	file1=file1.substring(42);
					//	file1=c.concat(file1);

					file22=file22.replace(".ifdefed","");
					file22=file22.replace("/","\\");
					file22=file22.replace("Monoo-2.10","mono-2.10");

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

					Node nNode = nl.item(group);
					Element eElement = (Element) nNode;
					String  similarity=eElement.getAttribute("Similarity");

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
					//clonePair.add(similarity);
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


	public static ArrayList<ArrayList<String>> sortReport( ArrayList<ArrayList<String>> clones){

		Collections.sort(clones, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> one, ArrayList<String> two) {
				return two.get(0).compareTo(one.get(0));
			}
		});

		return clones;
	}



}
