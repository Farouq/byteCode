package injectioValidation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.io.FileUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configuration.Configuration;
import starter.Starter_Preparation_Step1;





public class ReadInjectedClones {

	public static ArrayList<InjectedClone> clonesToInject = new  ArrayList<InjectedClone>();
	
	// load the location of all methods in the system only once athe the begining to choose injection location
	public static ArrayList<ArrayList<String>> methodData = new ArrayList<ArrayList<String>>();
	
	// save the file data temporarlty to reverse is after injection
	public static ArrayList<String> orginalFile1Content =new ArrayList<String>();
	public static ArrayList<String> orginalFile2Content =new ArrayList<String>();
	
	public static  ArrayList<InjectionLocation> injectionHistory =new ArrayList<InjectionLocation>();
	
	public static ArrayList<String> reportedClonepairs=new ArrayList<String>();
	
	

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub


		/*
		 * Step1 load created clones 
		 * the path of the files that contain created clones to inject to the system
		 */
		
	File clonesFilePath = new File("C:\\Users\\win10Admin\\Desktop\\InjectionBases\\ClonesToInject");
	clonesToInject=loadClonesToInjectFromFiles(clonesFilePath); 
	System.out.println(clonesToInject.size()+"  Clone pairs to inject are loaded  ");
	
//	 print the clone to be injected
//	for(InjectedClone clone :clonesToInject ) {
//		System.out.println(clone.frag1);
//		System.out.println(getFragmentSize(clone.frag1)+"------------------------------------------------------------------------------------------------------------------------------------------------------------");
//		System.out.println(clone.frag2);
//		System.out.println(getFragmentSize(clone.frag2)+"============================================================================================================================================================");
//	}
	
	/*
	 * Step2 Build target system and read its method to select injection location
	 */

	boolean b=buildSolution();

	
	//Project project= new Project(args[0]);
	//System.out.println(project.address);
	

	/*
	 * Step 3
	 * Disassimble the code for the first time to identify the files we are going to inject into and the position of methods to inside the files
	 * 
	 */
	Configuration config=Configuration.initialize(args[0]);
	Starter_Preparation_Step1.makeOutputFolders(config);
	 Disassimble.start(config);
	 // just trying to detect clones before injection
	 DetectClones.detectClones(config,  "before Injection");
	 
	 /*
	  * Step 4
	  * Iterative over the following steps
	  * {
	  * Read the methods information from the file allFiles.xml_0_source.xml 
	  * Choose randomly a location of the injection  save the location of the clones in a data structure
	  * Build the solution
	  * Disassimble and detect clones 
	  * Save the output file
	  * }
	  * Seach for each corresponding report for the injected clone
	  * 
	  */

	 
	
	 methodData=parse(config.disassebledAddress+"\\allFiles.xml_0_binary.xml");
	  
	 System.out.println("System methods loaded from the file: "+config.xmlByteCode);
	 
	 System.out.println(" Number of Methods in the subject system are :"+methodData.size());

	 
	 /*
	  * Select the loction of injection randomly
	  * 
	  */
	 int i=0;
	 
	 while ( i<clonesToInject.size())
	 {
		Random rand = new Random();
		int  n = rand.nextInt(methodData.size());
		int  m = rand.nextInt(methodData.size());
		
		String file1=  methodData.get(n).get(0);
		int start1= Integer.parseInt(methodData.get(n).get(2));
		int end1= start1+getFragmentSize(clonesToInject.get(0).frag1);
		
		// to avoid adding method at the end of the file
//		while(start1+4>= fileSize(file1)) {
//			 n = rand.nextInt(methodData.size());
//			 file1=  methodData.get(n).get(0);
//			 start1= Integer.parseInt(methodData.get(n).get(2));
//			 end1= start1+getFragmentSize(clonesToInject.get(0).frag1);
//		}

		
		String file2=  methodData.get(m).get(0);
		int start2= Integer.parseInt(methodData.get(m).get(2));
		int end2= start2+getFragmentSize(clonesToInject.get(0).frag2);
		
		// make sure the injection take place in to two differnt files
		while(file2.equals(file1)) {
			  m = rand.nextInt(methodData.size());
			   file2=  methodData.get(m).get(0);
				 start2= Integer.parseInt(methodData.get(m).get(2));
				 end2= start2+getFragmentSize(clonesToInject.get(0).frag2);
		}
		
		

		InjectionLocation cloneLocation= new InjectionLocation(file1,start1,end1,file2,start2,end2);
		
		
		
		 System.out.println("Clone injected ");//+cloneLocation.toString());
		 copyFilesIntoTempLocation(cloneLocation, config);
		 
		injectMethodsInlocations(clonesToInject.get(i), cloneLocation);
		
		if (buildSolution()) {

			injectionHistory.add(cloneLocation);
			Disassimble.start(config);
			
			// to do
			// detectClones(config,outputfileNameForInjection+1);
			reportedClonepairs.add(Integer.toString( DetectClones.detectClones(config,  "report"+i)));
			System.out.println(" clone Pair # "+ i+"  Injected succesfully ************************************************");

			i++;
		
		}else
		{
			
			 System.out.println("**********************************************compilation error Injection failed  clone Pair # "+ i+"*************************************************************");
			 System.out.println(" The Injected fragment is ");
			 System.out.println(clonesToInject.get(i).frag1);
			 System.out.println(" -------------------------------------------------------------------------------------------------------------------------");
			 System.out.println(clonesToInject.get(i).frag2);
			 
			 
		}
		 CopyFilesBack(cloneLocation,config);
		// reversefilecontents(cloneLocation);
	 }
	
		writeInjectionHistoryToFile(injectionHistory, config);
		//Measure recall and precison from report files
	
	
	}
	
	public static int fileSize(String fileName) throws IOException {
		
	
		File file =new File(fileName);
		
		   FileReader fr = new FileReader(file);
		   LineNumberReader lnr = new LineNumberReader(fr);
		    
		    int linenumber = 0;
		    
	            while (lnr.readLine() != null){
	        	linenumber++;
	            }
	            lnr.close();
	           return linenumber;
		}

	
public static void CopyFilesBack(InjectionLocation location, Configuration config)  throws Exception {
		
		File file1 =new File(location.file1);
		String name1=file1.getName();
		file1= new File(config.sourceCodeAddress+"\\"+name1);
		
		File file2= new File(location.file2);
		String name2 =file2.getName();
		file2=new File(config.sourceCodeAddress+"\\"+name2);
			
		File dest1=new File(location.file1);
		File dest2=new File(location.file2);;
			
		FileUtils.copyFile(file1, dest1);
		FileUtils.copyFile(file2,dest2);
		
	}

	public static void copyFilesIntoTempLocation(InjectionLocation location, Configuration config)  throws Exception {
		
		File file1 =new File(location.file1);
		File file2= new File(location.file2);
		File destination = new File(config.sourceCodeAddress);
		FileUtils.copyFileToDirectory(file1,destination);
		FileUtils.copyFileToDirectory(file2,destination);
		
	}
	public static void reversefilecontents(InjectionLocation location)  throws Exception {
		String outputFileAddress=location.file1+"Orginal";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


		for(int i=0; i<orginalFile1Content.size();i++ ){
			bufferedWriter.write(orginalFile1Content.get(i));
			bufferedWriter.newLine();
		}
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();
		
		orginalFile1Content.clear();
		
		String outputFileAddress2=location.file2+"Orginal";
		BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(outputFileAddress2));


		for(int i=0; i<orginalFile2Content.size();i++ ){
			bufferedWriter2.write(orginalFile2Content.get(i));
			bufferedWriter2.newLine();
		}
		bufferedWriter2.newLine();
		bufferedWriter2.flush();
		bufferedWriter2.close();
		
		orginalFile2Content.clear();
		
		System.out.println("File content reversed into before injection status");	
	}
		
	
	private static void writeInjectionHistoryToFile(ArrayList<InjectionLocation> clonesToInject ,Configuration config) throws Exception
	{
		String outputFileAddress=config.reportAddress+"\\Injection History.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		
		bufferedWriter.write("Locations of injected clones ");
		bufferedWriter.newLine();

		for(InjectionLocation locations : clonesToInject ){
			bufferedWriter.write( locations.toString());
			bufferedWriter.newLine();
		}

		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	
	private static void writeClonePairsDetectedintoFile(ArrayList<String> CP ,Configuration config) throws Exception
	{
		String outputFileAddress=config.reportAddress+"\\ClonePairs.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		
		bufferedWriter.write("Iteration_number, Number of clone pairs detected ");
		bufferedWriter.newLine();

		
		for(int i=0;i<=CP.size();i++ ){
			bufferedWriter.write(i+", "+ CP.get(i));
			bufferedWriter.newLine();
		}

		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	
	public static int getFragmentSize(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length+1;
		}
	
	public static void injectMethodsInlocations(InjectedClone clone, InjectionLocation location)  throws Exception {
		
		String fragment1= clone.frag1;
		String file1= location.file1;
		int afterLine1= location.start1;
		
		ArrayList<String> fileData =new ArrayList<String>();
		String line;
			FileReader fileReader = new FileReader(file1);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

		//	System.out.println("Fragment should be at line"+afterLine1);
			for (int i=0; i<afterLine1; i++) {
				line = bufferedReader.readLine();
				fileData.add(line);
				orginalFile1Content.add(line);
	//			System.out.println(i+"- "+line);
				
			}
			fileData.add(fragment1);
		//	System.out.println("_____________________________Here______________________________________________");
			
			while ((line = bufferedReader.readLine()) != null) {
				fileData.add(line);
				orginalFile1Content.add(line);
			//	System.out.println(line);
			}
			bufferedReader.close();
				
			
			/* 
			 * overwrite the file
			 * in the next loop reverse the file contents
			 * 
			 */
			File filetodelete=new File(file1);
			boolean deleted=filetodelete.delete();
		
			String outputFileAddress=file1;
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


			for(int i=0; i<fileData.size();i++ ){
				bufferedWriter.write(fileData.get(i));
				bufferedWriter.newLine();
			}
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.close();
			
			/*
			 * Do the same changes for the second fragment
			 */
			
			String fragment2= clone.frag2;
			String file2= location.file2;
			int afterLine2= location.start2;
			
			ArrayList<String> fileData2 =new ArrayList<String>();
			//String line;
				FileReader fileReader2 = new FileReader(file2);
				BufferedReader bufferedReader2 = new BufferedReader(fileReader2);

		//		System.out.println("Fragment should be at line"+afterLine2);
				for (int i=0; i<afterLine2; i++) {
					line = bufferedReader2.readLine();
					fileData2.add(line);
					orginalFile2Content.add(line);
			//		System.out.println(i+"- "+line);
					
				}
				fileData2.add(fragment2);
			//	System.out.println("_____________________________Here______________________________________________");
				
				while ((line = bufferedReader2.readLine()) != null) {
					fileData2.add(line);
					orginalFile2Content.add(line);
			//		System.out.println(line);
				}
				bufferedReader2.close();
					
				
				/* 
				 * overwrite the file
				 * in the next loop reverse the file contents
				 * 
				 */
				File file2todelete=new File(file2);
				boolean deleted2= file2todelete.delete();
				String outputFileAddress2=file2;
				BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(outputFileAddress2));


				for(int i=0; i<fileData2.size();i++ ){
					bufferedWriter2.write(fileData2.get(i));
					bufferedWriter2.newLine();
				}
				bufferedWriter2.newLine();
				bufferedWriter2.flush();
				bufferedWriter2.close();

				
				
				
		
	}
	
	public static void injectMethod(String fragment, String file, String afterLine) throws Exception {
		System.out.println("Selected file is \n"+ file );
		System.out.println("new file is \n ClonedFile"+ file );
		
		int position= Integer.parseInt(afterLine);
		
		ArrayList<String> fileData =new ArrayList<String>();
		String line;
		
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			for (int i=0; i<=position; i++) {
				line = bufferedReader.readLine();
				fileData.add(line);
			}
			fileData.add(fragment);
			
			while ((line = bufferedReader.readLine()) != null) {
				fileData.add(line);
			}
			bufferedReader.close();
			
			
			
			/* 
			 * overwrite the file
			 * in the next loop reverse the file contents
			 * 
			 */
			String outputFileAddress=file+".copied";
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


			for(int i=0; i<fileData.size();i++ ){
				bufferedWriter.write(fileData.get(i));
				bufferedWriter.newLine();
			}
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.close();

		
		
	}
	
	public static boolean buildSolution() throws Exception {
		boolean build;
		String systemSlnFile="C:\\Users\\win10Admin\\Desktop\\InjectionBases\\ASXGUI\\Trunk\\ASXGui.sln";
		String command= "C:\\Program Files (x86)\\Microsoft Visual Studio\\2017\\Professional\\MSBuild\\15.0\\Bin\\amd64\\MSbuild.exe "+ systemSlnFile;
		
		//System.out.println("Building solution");
		
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(command);
		
		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line=null;
		while((line=input.readLine()) != null) {
			//  System.out.println(line);
		}
		

		int exitVal = pr.waitFor();
		if(exitVal!=0)
		{
			 System.out.println("Build Build error ");
		//	throw(new Exception("error inVisual studio MS Builder"));
			 build=false;
		} else {
			 System.out.println("Build successfully ");
			 build=true;
		}
		return build;
	}
	
	private static  ArrayList<InjectedClone> loadClonesToInjectFromFiles(File parentDir) throws IOException {
		
		ArrayList<InjectedClone> loadedClones = new  ArrayList<InjectedClone>();
		
		for (File file : parentDir.listFiles()) {
		//	System.out.println(file);
			String line = null;

		 try {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);

	    		String source1="";
	    		String source2="";
	    		boolean firstfrag=true;
	    		
	            while ((line = bufferedReader.readLine()) != null) {
  	
	            	if  (line.startsWith("$$$$$")) firstfrag=false;
	            	
	            	if(line.startsWith("$$$$$")) continue;
	            	if (firstfrag) {
	            		source1=source1+line+"\n";
	            	} else {
	            		source2=source2+line+"\n";
	            	}
	        
	            	
	            	
	            //	System.out.println(line);
	            }
	            
	            InjectedClone clone= new InjectedClone(source1,source2);
	            loadedClones.add(clone);
	            bufferedReader.close();
	            

	        }catch(Exception e){e.printStackTrace();}
	
		}
		
		return loadedClones;

	}
	
	public static  ArrayList<ArrayList<String>> parse(String xmlFileName ) throws IOException{

		ArrayList<ArrayList<String>> methodData=new ArrayList<ArrayList<String>>();

		boolean error = false;
		File fileName = new File(xmlFileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileName);
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();

			NodeList nl = root.getElementsByTagName("name");
			if(nl.getLength()>0){
				//projectName = nl.item(0).getFirstChild().getNodeValue();
			}else{
				error = true;
			}

			nl = root.getElementsByTagName("description");
			if(nl.getLength()>0){
				//projectDesc = nl.item(0).getChildNodes().item(0).getNodeValue();
			}else{
				error = true;
			}

			nl = root.getElementsByTagName("source_elements");
			if(nl.getLength()>0){
				NodeList sourceList = nl.item(0).getChildNodes();

				long start = System.currentTimeMillis();
				long items =0;
				for(int i =0; i < sourceList.getLength(); i++){
					Node source = sourceList.item(i);
					if (source.getNodeType() != Node.ELEMENT_NODE) 
						continue;

					String file = source.getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline = source.getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline = source.getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					String content = source.getFirstChild().getTextContent();

					//	int loc = computeLoc(content);

					/*if(content.split("\n").length-1 != loc){
						System.out.println("gotcha!");
					}*/

					ArrayList<String> csd=new ArrayList<String>();
					csd.add(file);
					csd.add(startline);
					csd.add(endline);
					methodData.add(csd);


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return methodData;

	}


	
	

}

class Project{
	String address;
	String disassimbledAddress;
	String reportsAdress;
	
	Project(String address){
		this.address=address;
		this.disassimbledAddress=address+"_Disassimbled";
		this.reportsAdress=address+"_Reports";
		boolean success =  (new File(disassimbledAddress)).mkdirs();
		 success =  (new File(reportsAdress)).mkdirs();
		
	}
	
	
}

class InjectedClone{
	String frag1;
	String frag2;
	
	InjectedClone(){
		this.frag1=null;
		this.frag2=null;		
	}
	InjectedClone(String frag1,String frag2){
		this.frag1=frag1;
		this.frag2=frag2;		
	}
	
	public String frgment1() {
		return frag1;
	}
	public String frgment2() {
		return frag2;
	}
	
	public String toString() {
		return frag1+"--------------------------------------------------------------------------------------------------------\n"+frag2;
	}
}



class InjectionLocation{
	String file1;
	int start1 ;
	int end1;
	String file2;
	int start2;
	int end2;
	
	
	
	InjectionLocation(){
		this.file1=null;
		this.start1=0;
		this.end1=0;
	}
	InjectionLocation(String file,int line, int end){
		this.file1=file;
		this.start1=line;	
		this.end1=end;
		this.file2=null;
		this.start2=0;	
		this.end2=0;
		}
	
	InjectionLocation(String file,int line, int end, String file2, int s2,int e2){
		this.file1=file;
		this.start1=line;	
		this.end1=end;
		this.file2=file2;
		this.start2=s2;	
		this.end2=e2;
		}
	
	public  void setFrag1(String f, int s, int e) {
		this.file1=f;
		this.start1=s;
		this.end1=e;
		
	}
	
	public  void setFrag2(String f, int s, int e) {
		this.file2=f;
		this.start2=s;
		this.end2=e;
		
	}
	
	
	public String getFileName() {
		return file1;
	}
	public int getLineNumber() {
		return start1;
	}
	
	public String toString() {
		return "First location"+ file1 +"  at: "+start1+ " and "+ end1 +"\n"+
				"Second location"+ file2+"  at: "+start2+ " and "+ end2;
	}
}


class ClonePair {
	Boolean valid;
	int toolId=0;
	String file1;
	int startLine1;
	int endLine1;
	String file2;
	int startLine2;
	int endLine2;

	ClonePair(){
		valid = false;
		file1 = "";
		startLine1 = 0;
		endLine1 = 0;
		file2 = "";
		startLine2 = 0;
		endLine2 = 0;
	}
	
	ClonePair(boolean v, String f1, int s1, int e1, String f2, int s2, int e2) {

		valid = v;
		toolId=0;
		file1 = f1;
		startLine1 = s1;
		endLine1 = e1;
		file2 = f2;
		startLine2 = s2;
		endLine2 = e2;

	}
	
	ClonePair(boolean v,int tool, String f1, int s1, int e1, String f2, int s2, int e2) {

		valid = v;
		toolId=tool;
		file1 = f1;
		startLine1 = s1;
		endLine1 = e1;
		file2 = f2;
		startLine2 = s2;
		endLine2 = e2;

	}
	
	public String resultString()
	  {
	    if (this.valid==null) {
		      return  "Undecided " + "Tool number "+ this.toolId+ ", " + this.file1 + ", " + this.startLine1+", "+ this.endLine1+", "+ this.file2+  " , " + this.startLine2+", "+ this.endLine2;
		    }
	    
	    if (this.valid) {
	      return  "true" + ", "+ "Tool number "+ this.toolId+ ", " + this.file1 + ", " + this.startLine1+", "+ this.endLine1+" , "+ this.file2+  ", " + this.startLine2+", "+ this.endLine2;
	    }

	    return  "false" +", "+ "Tool number "+ this.toolId+  ", " + this.file1 + ", " + this.startLine1+", "+ this.endLine1+" , "+ this.file2+  ", " + this.startLine2+", "+ this.endLine2;
	  }
	

}