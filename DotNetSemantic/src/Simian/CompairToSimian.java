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

	static float [][] blockAvgSize=new float[10][10];
	static float[][] blockStd = new float [10][10];
	static ArrayList<int[]> blockLoc= new ArrayList<int[]>();

	static ArrayList<String> out =new ArrayList<String>();

	public static void main(String[] args) throws Exception{

		Configuration config=Configuration.initialize(args[0]);
		
		
		String reportAddress=config.reportAddress+"\\Semantic Clones Report.xml";
		
		String semianReportAddress=config.reportAddress+"\\Asxgui_Simian.txt";
		
		File file=new File(semianReportAddress);
	
		//System.out.println(convert( file));

		//Load clone classes into Arraylist; each class in one entry.
		 ArrayList<ArrayList<String>> cloneClasses=convert( file);
		 
		 ArrayList<ArrayList<String>> clonePairs=convertClassIntoPairs(cloneClasses);

	
		// we dont need this here	
//		parseNiCadPairs(cloneClasses);


	}


	
	  public static ArrayList<ArrayList<String>> convertClassIntoPairs(ArrayList<ArrayList<String>> cloneClasses) {
		  
		  ArrayList<ArrayList<String>> allClones=new ArrayList<ArrayList<String>>();
		  for (int i=0; i<cloneClasses.size();i++) {
			  		  
			
			  if (cloneClasses.get(i).size()==6) {
				  allClones.add(cloneClasses.get(i));
				  System.out.println(cloneClasses.get(i).size()+ " This class is a clone pair");
			  }else
			  { // make a loop inside the class and get all clone pairs
				  
				  for(int j=0; j<cloneClasses.get(i).size()-3;j+=3) {
					  
					  for(int k=j+3; k<cloneClasses.get(i).size();k+=3) {
						  
					
					  System.out.print(j+"-"+k+"   " );
					  
					  }
					  System.out.println();
				  }
				  System.out.println();
				  		  
				  
			  }
		  
	  }
		  
		  return allClones;
	  }

	// parse nicad results and convert it to windows
	
	    public static ArrayList<ArrayList<String>> convert(File file) {
	        boolean    found          = false;
	        int        cloneClassId   = 1;     // starting clone class id. Each clone class has different id
	        int        cloneId        = 1;     // starting clone id. Each clone has different id.
	        String     output         = "";    // final output of the function
	        String     cloneInfo      = "";    // string that holds cloned fragments information for a class
	        int        numOfFragments = 0;     // numf of fragments in a clone class
	        int        numOfLines     = 0;     // num of Lines in a clone class
	        FileReader fr             = null;
	        String tool="";
	        String processingTime="";
	        String processedLines="";
	        String processedFiles="";
	        String duplicateLines="";
	        String duplicateFiles="";
	        String duplicateBlocks="";
	        int c=0;
	        ArrayList<ArrayList<String>> allClones=new ArrayList<ArrayList<String>>();
	        
	        try {
	            LineNumberReader lr = new LineNumberReader(new FileReader(file));
	            lr.skip(Long.MAX_VALUE);
	            int totalLinesInFile = lr.getLineNumber();
	            lr.close();
	            lr = new LineNumberReader(new FileReader(file));
	            tool = lr.readLine();
	            //System.out.print(tool);
	            for (int i = 1; i < totalLinesInFile - 3; i++) {
	                lr.readLine();
	            }
	            String s  = lr.readLine();
	            String tokenizedStr[] =s.split("\\s+");
	            //System.out.println("??"+s+"TotalLinesInFIles = "+totalLinesInFile);

	            duplicateLines = tokenizedStr[1];
	            duplicateFiles = tokenizedStr[5];
	            duplicateBlocks= tokenizedStr[8];

	            tokenizedStr = lr.readLine().split("\\s+");
	            processedFiles = tokenizedStr[tokenizedStr.length-2];
	            processedLines = tokenizedStr[4];

	            tokenizedStr = lr.readLine().split("\\s+");
	            processingTime = tokenizedStr[2];

	            lr.close();

	        } catch (FileNotFoundException ex) {
	           // Logger.getLogger(FileConverterForSimian.class.getName()).log(Level.SEVERE, null, ex);
	        }
	        catch(Exception e){e.printStackTrace();}

	       //output=;
	       String info = "\n<info " +  " tool=\""+tool+"\""+
	               "    processedLines=\""+processedLines+"\""+
	               "    processedFiles=\""+processedFiles+"\""+
	               "    duplicatedLines=\"" + duplicateLines + "\""
	               + "  duplicatedFiles=\"" + duplicateFiles + "\""
	               + "  duplicatedBlocks=\"" + duplicateBlocks + "\""
	               +"   processingTime=\""+ processingTime + "\""

	               +" />" ;
	       output ="";
	       output = output + info;
	         try {
	            fr = new FileReader(file);

	            BufferedReader br      = new BufferedReader(fr);
	            String         lineStr = null;

	            try {
	                while ((found == true) || (lineStr = br.readLine()) != null) {
	                	ArrayList<String> clone=new ArrayList<String>();
	                    if ((found == true) || (lineStr.startsWith("Found") && lineStr.endsWith("files:"))) {    // Starting of a clone class
	                        found = false;    // reset found
	                        /* process each cloned fragment belong to a class */
	                        while (true) {
	                            lineStr = br.readLine();

	                            /* A line starting  with 'found' indicate the end of a clone class */
	                            if (lineStr.startsWith("Found")) {
	                                if (lineStr.startsWith("Found") && lineStr.endsWith("files:")) {
	                                    found = true;    // found is set to true to bypass the next lineread in the while loop, posssibly we found another clone class
	                                }

	                                break;
	                            }

	                            String splittedStr[] = lineStr.trim().split(" ");    // split the line containg a cloned frgament info
	                            String filePath="";
	                            for(int i=6;i<splittedStr.length;i++){
	                                filePath =filePath+" "+splittedStr[i];
	                            }
	                            clone.add(filePath.trim());
	                            clone.add(splittedStr[2]);
	                            clone.add(splittedStr[4]);
	                          //  System.out.println(clone.get(1));
	                            cloneInfo = cloneInfo + "\n<source file =\"" + filePath.trim() + "\"  startline=\""
	                                        + splittedStr[2] + "\"  endline=\"" + splittedStr[4] + "\"  pcid=\"" + cloneId
	                                        + "\">" + "</source>";
	                            numOfFragments++;
	                            numOfLines = numOfLines + Integer.parseInt(splittedStr[2])
	                                         + Integer.parseInt(splittedStr[4]);
	                            cloneId++;    // increase the  clone id to uniquely identify the clones
	            
	              
	                        }
	                        c++;
	                     //   System.out.println(c+ "_______________________________");
	                    //    System.out.println(clone.size());
	                       
	                        
	                        allClones.add(clone);
	                      //  System.out.println(allClones.get(c-1));
	                       // clone.clear();
	                       
	                        output = output + "\n<class ccid=\"" + cloneClassId + "\" nlines=\"" + numOfLines
	                                 + "\" nfragments=\"" + numOfFragments + "\">" + cloneInfo + "\n" + "</class>";

	                        // Now reset the variables for the next clone class
	                        numOfFragments = 0;
	                        numOfLines     = 0;
	                        cloneInfo      = "";
	                        cloneClassId++;    // increase clone class Id to uniquely identify each clone class

	                    }
	                }

	                br.close();
	                fr.close();
	            } catch (IOException ioe) {
	                ioe.printStackTrace();
	            }
	        } catch (FileNotFoundException ex) {
	        //    Logger.getLogger(FileConverterForSimian.class.getName()).log(Level.SEVERE, null, ex);
	        } finally {
	            try {
	                fr.close();
	            } catch (IOException ex) {
	         //       Logger.getLogger(FileConverterForSimian.class.getName()).log(Level.SEVERE, null, ex);
	            }

	            /* Finally encode the clone detection tool and other information */
	            output = "<cloneDetectionResult>" + output + "\n</cloneDetectionResult>";
	            System.out.println(" number of clone classes: "+c);

	        //    System.out.println(allClones.get(2));
	            return allClones;    // return the formatted result
	        }
	    }
	
	    public static void parseNiCadPairs( ArrayList<ArrayList<String>> allClones) throws Exception{
		ArrayList<String> output=new ArrayList<String>();
		int w=0;
		output.add("<html>");
		output.add("<head>");
		output.add("<style type=\"text/css\">");
		
	//	output.add("<title>  Clone Report </title>");
		output.add("body {font-family:Arial}");
		output.add("table {background-color:white; border:0px solid white; width:95%; margin-left:auto; margin-right: auto}");
		output.add("td {background-color:#b0c4de; padding:16px; border:4px solid white}");
		output.add("pre {background-color:white; padding:4px}");
		output.add("</style>");
		output.add("<title> Clone Report");
		output.add("</title>");
		output.add("</head>");
		output.add("<body>");
		
	//	System.out.println(allClones.size());
	//	ArrayList<String> clone=allClones.get(0);
	//	System.out.println(allClones.get(1).get(1));
		
			for(int i=0;i<allClones.size();i++){
				
						w++;
						System.out.println(w+"  "+i+allClones.get(i).get(0));
						String file1=allClones.get(i).get(0);
						int startline=Integer.parseInt(allClones.get(i).get(1));
						int endline=Integer.parseInt(allClones.get(i).get(2));
	
						String file2=allClones.get(i).get(3);;
						int startline2= Integer.parseInt(allClones.get(i).get(4));		
						int endline2= Integer.parseInt(allClones.get(i).get(5));

						

						output.add("<h3>Clone Pair id: "+w);
						output.add("</h3>");
						output.add("<table cellpadding=12 border=2 frame=\"box\" width=\"90%\">");
						output.add("<tr><td>");
						output.add("Lines "+startline+" - " + endline +" of "+file1 );
						output.add("<pre>");
						output.add(readSourceFile(new File(file1),startline,endline));
						output.add("</pre>");
						output.add("</td></tr>");
						output.add("<tr><td>");
						output.add("Lines "+startline2+" - " + endline2 +" of "+file2);
						output.add("<pre>");
						output.add(readSourceFile(new File(file2),startline2,endline2));
						output.add("</pre>");
						output.add("</td></tr>");
						output.add("</table>");
	
			}
						

				output.add("</body>");
				output.add("</html>");
				writeToHTMLFile(output);
				System.out.println("Done");
				System.out.println("number of clone pairs: "+ w );



		
		

	
		
	}

	private static String readSourceFile(File fileName,int startLine, int endLine) throws IOException {
	
		String source="initianl string";
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

	private static String getSourceCode(String rawFunctionsFileName, String sourceFile,String startLine) throws IOException{
		String source="initianl string";
		
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


					nl = root.getElementsByTagName("class");  //in case of xml classes report
		//	allc=nl.getLength();

					System.out.println(nl.getLength()+"************* number of clone classes *************");

			int w=0;

			if(nl.getLength()>0){

				for (int group=0;group<nl.getLength();group++)
				{
					NodeList sourceList = nl.item(group).getChildNodes();

					System.out.println( sourceList.getLength()+"----------------------");





					for(int file=1;file<sourceList.getLength();file+=2){
						w++;
						String file1= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
						int startline=Integer.parseInt(sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue() );
						int endline=Integer.parseInt(sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue() );
					
						
						
//						String file11= sourceList.item(1).getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
//						String startline11 = sourceList.item(1).getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
//						String endline11 = sourceList.item(1).getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
//						

						System.out.println(file1);
						

					}

								
			//			readFile(file2,st,en);
				}

				System.out.println("Done");



			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		

		
		return source;
	}

	private static boolean crossGame(int[] freq){
		int c=0;
		for(int i=0;i<10;i++){
			if(freq[i]>0) c++;
		}
		if(c>1) 
			return true;
		else
			return false;


	}

	private static void writeToHTMLFile(ArrayList<String> lines) throws Exception
	{
		File f=new File("PhP Clone report.html");
		
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(f));

		for(String line :lines)
		{
			//System.out.println(line);
			bufferedWriter.write(line);
			bufferedWriter.newLine();
		}
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	private static void readFile(String file, int s, int en){

		BufferedReader br = null;

		try {


			String sCurrentLine;

			br = new BufferedReader(new FileReader(file));

			//			while ((sCurrentLine = br.readLine()) != null) {
			//				System.out.println(sCurrentLine);
			//			}
			out.add("-------------------------------------------------------------------------------------------------------------------------");	
			out.add(file);
			for(int i=1;i<s;i++){
				br.readLine();
			}

			for(int i=s;i<=en;i++){
				sCurrentLine=br.readLine();
				System.out.println(sCurrentLine);
				out.add(sCurrentLine);
			}



		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void write() throws Exception
	{
		try {



			File file=new File("c:/game/Java.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);


			for(String line:out)
			{

				//System.out.println(line);
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}






