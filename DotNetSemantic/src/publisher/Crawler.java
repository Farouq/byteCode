package publisher;

import java.io.File;
import java.util.ArrayList;
 
public class Crawler {

	
	 private String __targetFileType="exe";
	 
	 public Crawler(String targetFileType)
	 {
		 __targetFileType=targetFileType;
	 }
	 
	private ArrayList<String> exeFileList=null;
	
	public ArrayList<String> findExeFiles(String rootAddress )
	{
		exeFileList=new ArrayList<String>( );
		//System.out.println(__targetFileType);
		findRecursively(new File(rootAddress));
		return(exeFileList);
	}
	 
	 
	    private void findRecursively(File current)// throws Exception 
	    {

	    	if (current.isDirectory() && !current.isHidden() && current.getName() != ".svn") {
	    		for (File f : current.listFiles())
	    			try {

	    				this.findRecursively(f);
	    			} catch (Exception ex) {

	    		 
	    			} catch (Error e) {
	    		 	}
	    	} else if (current.isFile()) {
	    		detectExeFile(current);
	    	}
	    }

	    
	    private void detectExeFile(File current)
	    {

	    	try {
	    		String fileName = current.getName();
	    		int dotPos = fileName.lastIndexOf('.', fileName.length());
	    		if (dotPos > -1) {
	    			String extension = fileName.substring(dotPos + 1);
	    			if ((extension.equalsIgnoreCase(__targetFileType)) && exeHasPdb(current) ||(extension.equalsIgnoreCase("dll")) && dllHasPdb(current)) {

	    				exeFileList.add(current.getPath());
	    		//		System.out.println(current);

	    			}
	    			
	    		}
	    	}catch(Exception ee)
	    	{
	 	} 
	    }
	    
	    
	    private boolean dllHasPdb(File current)
	    {
	    	String pdbPath= current.getPath().replace("dll","pdb");
	    	File pdbFile= new File(pdbPath);
	    	
	     boolean exist= pdbFile.exists();
	     return exist;
	    }
	    // easy way if file.exsist();
	    private boolean exeHasPdb(File current){
	    	boolean found=false;
	    	
	    	File parent = current.getParentFile();
	    //	System.out.println(current+"   "+current.getName().substring(0,current.getName().length()-4)+"     "+current.getName().substring(current.getName().length()-3));
	    	
	    	for (File f : parent.listFiles())
    			try {
    				String fileName = f.getName();
    	    		if (f.isFile()) {
    	    			
    	    			if (fileName.endsWith("pdb") && fileName.substring(0,fileName.length()-4).equals(current.getName().substring(0,current.getName().length()-4)) ) {
    	    				found=true;
    	    			}

    	    			}
    	    			
    				
    			} catch (Exception ex) {

    		 
    			} catch (Error e) {
    		 	}
	    	return found;
	    }
	    
}
