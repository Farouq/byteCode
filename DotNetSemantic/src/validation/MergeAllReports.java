package validation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import configuration.Configuration;



public class MergeAllReports {
	
	//Union of all reports clones
	public static ArrayList<ClonePair> clonesUnion = new  ArrayList<ClonePair>();

	
	public static void main(String[] args)  throws Exception{
	
	
		String asxGuiReportsPath="C:\\Users\\win10Admin\\Desktop\\Test_Cases\\validation\\Systems\\ASXGUI";
		String netGoreReportsPath="C:\\Users\\win10Admin\\Desktop\\Test_Cases\\validation\\Systems\\NetGore";
		String OpenRaReportsPath="C:\\Users\\win10Admin\\Desktop\\Test_Cases\\validation\\Systems\\OpenRa";
		String ScriptsReportsPath="C:\\Users\\win10Admin\\Desktop\\Test_Cases\\validation\\Systems\\Scriptcs";
		
	
		
		loadAllclonesInFolder(asxGuiReportsPath);
		loadAllclonesInFolder(ScriptsReportsPath);
		loadAllclonesInFolder(OpenRaReportsPath);
		loadAllclonesInFolder(netGoreReportsPath);
		
		
		 
		 System.out.println(" Number of clones detected by All Tools is " + clonesUnion.size());

		 ArrayList<ClonePair> selectedSample=selectUniqueCloneGroups(3000);
		 
		 System.out.println(" Size of selected sample is  " + selectedSample.size());
		 
//		 for (ClonePair clone2 :selectedSample) {
//			 System.out.println(clone2.resultString());
//		 }
		 
		 
		 // I need to divide the sample into 100 clone pairs mini samples 
		 // 3000 pairs I will create 30 groups 100 each
		 int k=0;

		 for(int i=0; i<3000; i+=50) {
			 ArrayList<ClonePair> set = new  ArrayList<ClonePair>();		 
			for(int j=i; j<i+50; j++) {
				  set.add(selectedSample.get(j));
			}
			
			 writeClonesToTextFile(set,Integer.toString(k) );
			 k++;
				  
		 }
		 
	
	   writeClonesToTextFile(selectedSample, "all");


	}
	
	
	public static ArrayList<ClonePair> selectUniqueCloneGroups(   int groupSize){
		
		int [] toolId={0,0,0,0,0,0};

		
		

		ArrayList<ClonePair> selected = new ArrayList<ClonePair>();
		ArrayList<ClonePair> selectedAll = new ArrayList<ClonePair>();
	
		Random rand = new Random();
						
			int j=0;
			
			while(j<groupSize){
				
				int  n = rand.nextInt(clonesUnion.size());
				
				if( !selectedAll.contains(clonesUnion.get(n))) {
					int tool=clonesUnion.get(n).toolId;
					
					if (toolId[tool]<500 ) {
						toolId[tool]++;
					selectedAll.add(clonesUnion.get(n));
					selected.add(clonesUnion.get(n));	
					j++;
					}
					
				}		
				
			}



		return selected;
	}

	

	private static void writeClonesToTextFile( ArrayList<ClonePair> meregedClones, String fileName) throws Exception
	{
		String outputFileAddress="C:\\Users\\win10Admin\\Desktop\\Test_Cases\\validation\\Systems\\SampleToValidate\\"+fileName;
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


		for(int i=0; i<meregedClones.size();i++ ){
			bufferedWriter.write("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			bufferedWriter.newLine();
			bufferedWriter.write( Integer.toString(meregedClones.get(i).toolId));
			// System.out.println(meregedClones.get(i).toolId);
			
			bufferedWriter.newLine();
			bufferedWriter.write("0");
			bufferedWriter.newLine();

		//	SelectRandomUniqueClone();  //to Do

			bufferedWriter.write(meregedClones.get(i).file1+" "+ meregedClones.get(i).startLine1 +" "+ meregedClones.get(i).endLine1);
			bufferedWriter.newLine();
			bufferedWriter.write(meregedClones.get(i).file2+" "+ meregedClones.get(i).startLine2 +" "+ meregedClones.get(i).endLine2);
			bufferedWriter.newLine();
			bufferedWriter.write("----------------------------------------");
			bufferedWriter.newLine();
			bufferedWriter.write( readSourceFile(new File(meregedClones.get(i).file1),meregedClones.get(i).startLine1,meregedClones.get(i).endLine1 ));
		//	bufferedWriter.newLine();
			bufferedWriter.write("----------------------------------------");
			bufferedWriter.newLine();
			bufferedWriter.write( readSourceFile(new File(meregedClones.get(i).file2),meregedClones.get(i).startLine2,meregedClones.get(i).endLine2 ));
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
  	
	            }

	        }catch(Exception e){e.printStackTrace();}
	
		
		return source;
	}
	
	public static void loadAllclonesInFolder(String directory) throws IOException {

		ArrayList<ClonePair> validatedClones = new ArrayList<ClonePair>();

		File parentDir = new File(directory);

		for (File file : parentDir.listFiles()) {
			 System.out.println(file.getName());
			 int tool= Integer.parseInt(file.getName());

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

						ClonePair clone = new ClonePair(validation,tool, file1, Integer.parseInt(startline1),Integer.parseInt(endline1), file2, Integer.parseInt(startline2),Integer.parseInt(endline2));
						
						if (!isOverlap(clone,clonesUnion))							
						clonesUnion.add(clone);

					}

					// System.out.println("Done");

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} // for (File file:

		//return validatedClones;
	}
	
	
	public static boolean isOverlap(ClonePair clone1, ArrayList<ClonePair>  cloneUnion) {
		boolean found = false;

		

		for (ClonePair clone2 :cloneUnion) {
			
		if (  (  (clone1.file1.equals(clone2.file1) && clone1.startLine1<=clone2.endLine1 && clone1.endLine1>=clone2.startLine1 )&&
				 (clone1.file2.equals(clone2.file2) && clone1.startLine2<=clone2.endLine2 && clone1.endLine2>=clone2.startLine2 ))
				||	
				(  (clone1.file1.equals(clone2.file2) && clone1.startLine1<=clone2.endLine2 && clone1.endLine1>=clone2.startLine2 )&&
				   (clone1.file2.equals(clone2.file1) && clone1.startLine2<=clone2.endLine1 && clone1.endLine2>=clone2.startLine1 ))
				)
		{
			found = true;
			break;
		}
	}


		return found;
	}
	
}
