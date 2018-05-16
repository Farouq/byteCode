package validation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import configuration.Configuration;

public class Precision {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = args[0];
		
		 ArrayList<ClonePair> validatedClones=readValidatedData(args[0]);
		 System.out.println(" Number of clones validated: "+ validatedClones.size());
		 System.out.println(" Number of true clones: "+  trueClones(validatedClones));
		 System.out.println(" the precion for this tool is "+ (double) trueClones(validatedClones) /validatedClones.size()  );
		 

	}


	public static int trueClones( ArrayList<ClonePair> validatedClones) {
		int c=0;
		
		for ( ClonePair clone : validatedClones ){
			if (clone.valid) c++;
		}
		
		return c;
	}
	
	
	
	public static ArrayList<ClonePair> readValidatedData(String directory) {


		ArrayList<ClonePair> validatedClones= new ArrayList<ClonePair>();
		
		File parentDir =new File (directory);
		
		for (File file : parentDir.listFiles()) {
			
	
		String line = null;

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			//line = bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {

			//	System.out.println(line);

				line = line.trim();
				String[] parts = line.split(",");
			    //int toolid = Integer.parseInt(parts[0]);
			    //int cloneid = Integer.parseInt(parts[1]);
			    Boolean validation;
			    if (parts[2].equals("undecided"))
			    {
			      validation = Boolean.valueOf(false);;
			      System.out.println(" undecided clone");
			    }
			    else
			    {
			      if (parts[2].equals("true")) {
			        validation = Boolean.valueOf(true);
			      } else {
			        validation = Boolean.valueOf(false);
			      }
			    }
			    
			    String fragment1 = parts[3];
			    String fragment2 = parts[4];

			    fragment1=fragment1.trim();
			    int i = fragment1.lastIndexOf(" ");
			    int end1= Integer.parseInt(fragment1.substring(i).trim());
			    fragment1=fragment1.substring(0, i).trim();

			    i = fragment1.lastIndexOf(" ");
			    int start1= Integer.parseInt(fragment1.substring(i).trim());
			    String file1=fragment1.substring(0, i).trim();

			    
			    fragment2=fragment2.trim();
			    i = fragment2.lastIndexOf(" ");
			    int end2= Integer.parseInt(fragment2.substring(i).trim());
			    fragment2=fragment2.substring(0, i).trim();
			    i = fragment2.lastIndexOf(" ");
			    int start2= Integer.parseInt(fragment2.substring(i).trim());
			    String file2=fragment2.substring(0, i).trim();

			    
			    
			    
			    
				
				ClonePair clone=new ClonePair(validation, file1, start1,end1,file2,start2,end2);
				//System.out.println(clone.resultString());

				validatedClones.add(clone);
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