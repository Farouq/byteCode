package starter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import configuration.Configuration;

public class ExecuteIldasm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		 try {
	            Process p = Runtime.getRuntime().exec("C:\\Program Files (x86)\\Microsoft SDKs\\Windows\\v7.0A\\Bin\\ildasm.exe C:\\Users\\win10Admin\\Desktop\\ASXGUI_Clone\\2-ByteCodes\\ASXGui.exe  /source /text ");
	            BufferedReader in = new BufferedReader(
	                                new InputStreamReader(p.getInputStream()));
	            
	            
	            String outputFileAddress="ASXGUI.txt";


	    		BufferedWriter bufferedWriter = null;
	    		bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));

	            String line = null;
	            
	            while ((line = in.readLine()) != null) {
	            	bufferedWriter.write(line);
	    			bufferedWriter.newLine();
	    			System.out.println(line);
	            }
	            
	    		bufferedWriter.flush();
	    		bufferedWriter.close();
	    		
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	}




private static ArrayList<ArrayList<String>> step2_disAssemble(Configuration config,ArrayList<String> files ) throws Exception
{
	//int ii=0;
	ArrayList<ArrayList<String>> disassebledCodeList=new ArrayList<ArrayList<String>>();
	for(String file : files)
	{
		
		//System.out.println("r"+ii++);
		if(file.contains(" "))
		{
			throw(new Exception("error in disassembler s7fh6fh6fh645"));
		} 
		ArrayList<String> disassebledCode=new ArrayList<String>();
		Runtime rt = Runtime.getRuntime();
		//String command=config.disassembeler_EXE_path+"   "+file + "  /source /text ";
		// String command=config.disassembeler_EXE_path+"   "+file + "  /source /text /linenum ";


		String command="ildasm "+file + "  /source /text ";
		Process pr = rt.exec(command);
		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

		String line=null;

		while((line=input.readLine()) != null) {
			//   System.out.println(line);
			disassebledCode.add(line);
		}

		int exitVal = pr.waitFor();
		if(exitVal!=0)
		{
			throw(new Exception("error in disassembler cf45n7zsl4sg4e6"));
		}

		disassebledCodeList.add(disassebledCode);
	}

	return(disassebledCodeList);
}



}