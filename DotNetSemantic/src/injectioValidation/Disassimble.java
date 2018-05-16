package injectioValidation;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.lang.*;
import org.apache.commons.io.FileUtils;

import publisher.Crawler;




import configuration.Configuration;
import nicad.LevenshteinForOneFile;




public class Disassimble {

	/**
	 * @param args
	 * @throws Exception 
	 * Farouq modified
	 * Second commit
	 */



	public static void main(String[] args) throws Exception {

			//Configuration config=Configuration.loadFromFile();
			Configuration config=Configuration.initialize(args[0]);
	}

	public static void start(Configuration config) throws Exception
	{

		ArrayList<String> files=step1_findExeFiles(config);
        moveExe(config,files);
		
		//Consider only moved files. No dublicated files.  findMovedExeFiles is a better name for this method
		files = movedFiles(config);
	//	System.out.println("Number of Portable Executable files found and used are: "+ files.size());
		
		ArrayList<ArrayList<String>> disassembledCodeList= step2_disAssemble(config,files);
		step5_Filter_ExportAllToXML(config,files,disassembledCodeList);

		//System.out.println("Used source code and byte code are moved to output directory");
		//System.out.println("Methods are extracted into xml files");

		//		System.out.println("ver plusma");
		//		System.out.println("ver plusma DONE");

	}


	private static ArrayList<String> step1_findExeFiles(Configuration config)
	{
		Crawler crwl=new Crawler("exe");
		ArrayList<String> files=crwl.findExeFiles(config.projectAddress);
		
		Crawler crwl2=new Crawler("dll");
		ArrayList<String> files2=crwl2.findExeFiles(config.projectAddress);
		files.addAll(files2);
		return(files);
	}


		
	private static void moveExe(Configuration config, ArrayList<String> files)throws Exception{
		File destination= new File(config.byteCodeAddress);
		for( String file:files){
			File exeFile=new File(file);
			
			String pdbFileName=	file.endsWith("exe") ?   file.replace("exe","pdb") :file.replace("dll","pdb");
			
//			if (file.endsWith("exe")) 
//			{ pdbFileName = file.replace("exe","pdb");
//				}
//			else
//			{	 pdbFileName = file.replace("dll","pdb");
//						}
						
			File pdbFile= new File(pdbFileName);
			FileUtils.copyFileToDirectory(exeFile,destination);
			FileUtils.copyFileToDirectory(pdbFile,destination);
			
		}
	}
	
	private static ArrayList<String> movedFiles (Configuration config) throws Exception
	{
		ArrayList<String> listOfExeFiles= new ArrayList<String>();
		
		File byteAddesss= new File(config.byteCodeAddress);
		for(File exeFile : byteAddesss.listFiles()){
			String fileName =exeFile.getName();
			if(fileName.endsWith("exe")||fileName.endsWith("dll")){
				listOfExeFiles.add(exeFile.getPath() );
			}
		}
		
		return listOfExeFiles;
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
			String command=config.disassembeler_EXE_path+"   "+file + "  /source /text ";
			// String command=config.disassembeler_EXE_path+"   "+file + "  /source /text /linenum ";

			

	//		String command="C:\\Program Files (x86)\\Microsoft SDKs\\Windows\\v10.0A\\bin\\NETFX 4.7 Tools\\ildasm.exe "+file + "  /source /text ";
			System.out.println("Disassembling the file "+ file.substring(file.lastIndexOf("\\")+1));
			Process pr = rt.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line=null;

			while((line=input.readLine()) != null) {
				//   System.out.println(line);
				disassebledCode.add(line);
			}

//			int exitVal = pr.waitFor();
//			if(exitVal!=0)
//			{
//				throw(new Exception("error in disassembler cf45n7zsl4sg4e6"));
//			}

			disassebledCodeList.add(disassebledCode);
		}

		return(disassebledCodeList);
	}



	private static void step5_Filter_ExportAllToXML(Configuration config,ArrayList<String> files,ArrayList<ArrayList<String>> disassembledCodeList) throws Exception
	{
		//System.out.println("export to filtered  XML file" +disassembledCodeList.get(0).size());

		String lin;
		int methodCounter=0;
		int totalMethodCounter=0;
		
		String currentSourceFileAddress="NULL";
		ArrayList<String> out_lines_binary=new ArrayList<String>();
		ArrayList<String> out_lines_source=new ArrayList<String>();
		ArrayList<String> methodCalls =new ArrayList<String>();
		ArrayList<String> methodSigniture =new ArrayList<String>();
		
		

		Set <String> sourceCodeFileSet = new HashSet<String>();

		out_lines_binary.add("<project><name></name><description></description><prog_language></prog_language><source_elements>");
		out_lines_source.add("<project><name></name><description></description><prog_language></prog_language><source_elements>");
		methodCalls.add("<project><name></name><description></description><prog_language></prog_language><source_elements>");
		
		methodSigniture.add("<project><name></name><description></description><prog_language></prog_language><source_elements>");

		
		//System.out.println(files.get(0));

		for(int i=0;i< files.size();i++)	
		{			
			

			ArrayList<String> in_lines=disassembledCodeList.get(i);
			ArrayList<String> methodBlockBuffer_binary=new ArrayList<String>();
			ArrayList<String> methodBlockBuffer_source=new ArrayList<String>();
			ArrayList<String> methodCallsBuffer = new ArrayList<String>();
			
			ArrayList<String> methodSignitureBuffer = new ArrayList<String>();

			boolean bInsideMethodBlock=false;
			int methoBlockStartLine=Integer.MAX_VALUE;
			int methoBlockEndLine=Integer.MAX_VALUE;

			for(int j=0;j<in_lines.size();j++)
			{

				if(in_lines.get(j).trim().startsWith("// Source File '"))
				{

					String t=in_lines.get(j).replace("// Source File '", "");
					t=t.substring(0,t.lastIndexOf("'"));
					currentSourceFileAddress=t;
				}

				if(in_lines.get(j).startsWith("  .method"))
				{
					methodSignitureBuffer.add(in_lines.get(j));
					while(!in_lines.get(j).trim().equals("{"))
					{
						j++;
						methodSignitureBuffer.add(in_lines.get(j));
					
					}
					
				//	System.out.println(methodSignitureBuffer);
				//	System.out.println("--------------------------------------------------------------------------------------------------------------------");

					
					
					bInsideMethodBlock=true;
					methoBlockStartLine=Integer.MAX_VALUE;
					methoBlockEndLine=Integer.MAX_VALUE;
				}


				// filtering out .local unit block no need for this filter since we take only sentences started with IL_00
				/*	if(in_lines.get(j).trim().startsWith(".locals init"))
				{
					j++;
					while(!in_lines.get(j).trim().endsWith(")"))
					{
						j++;
					}
				//	j++;  // Bug
				} 
				 */

				if(in_lines.get(j).startsWith("//"))
				{
					try
					{
						String[] cols= in_lines.get(j).split(":");
						int ln=Integer.parseInt(cols[0].replace("//",""));
						//System.out.println(ln);

						if(bInsideMethodBlock)
						{
							if(methoBlockStartLine==Integer.MAX_VALUE)
							{
								methoBlockStartLine=ln;


							}
							methoBlockEndLine=ln;
						}
					}
					catch(Exception ex)
					{

					}
				}


				if(bInsideMethodBlock && in_lines.get(j).startsWith("//")  )
				{

					try
					{
						String[] cols= in_lines.get(j).split(":");
						int ln=Integer.parseInt(cols[0].replace("//",""));

						methodBlockBuffer_source.add(cols[1]);
					}
					catch(Exception ex)
					{

					}
				}


				//Filtering out these instructions
				if((bInsideMethodBlock && in_lines.get(j).trim().startsWith("IL_") ))
					/* New condition is better than this
					 * 	!(  (in_lines.get(j).trim().startsWith("//") 
								|| (in_lines.get(j).trim().startsWith("} // end of method"))
								|| (in_lines.get(j).trim().startsWith("// Code size"))
								|| (in_lines.get(j).trim().startsWith(".maxstack"))
								|| (in_lines.get(j).trim().startsWith(".language"))
								|| (in_lines.get(j).trim().startsWith(".custom instance"))
								|| (in_lines.get(j).trim().startsWith(".entrypoint"))
								|| (in_lines.get(j).trim().startsWith("{"))))))
					 */
				{


					// filter out the IL_XXXX
					lin=in_lines.get(j);

					if (lin.trim().startsWith("IL_"))
					{
						int w= lin.indexOf(":")+1;
						lin = lin.substring(w);
					}
					
					// this block creates called methods set in the method
					if (lin.trim().startsWith("call"))
					{
						String calledFunction;
						int w= lin.indexOf("::")+2;
						calledFunction=lin.substring(w,lin.indexOf("("));
						methodCallsBuffer.add(calledFunction);
					//	System.out.println(calledFunction);					
					}

					// second Filter
					if (lin.trim().startsWith("br.s")||
							lin.trim().startsWith("brtrue.s")||
							lin.trim().startsWith("leave.s")||
							lin.trim().startsWith("blt.s")||
							lin.trim().startsWith("bge.s")||
							lin.trim().startsWith("beq")||
							lin.trim().startsWith("beq.s")||
							lin.trim().startsWith("bge")||
							lin.trim().startsWith("bge.un")||
							lin.trim().startsWith("bge.un.s ")||
							lin.trim().startsWith("bgt")||
							lin.trim().startsWith("bgt.s")||
							lin.trim().startsWith("bgt.un")||
							lin.trim().startsWith("bgt.un.s")||
							lin.trim().startsWith("ble")||
							lin.trim().startsWith("ble.s")||
							lin.trim().startsWith("ble.un")||
							lin.trim().startsWith("ble.un.s")||
							lin.trim().startsWith("blt")||
							lin.trim().startsWith("blt.un")||
							lin.trim().startsWith("blt.un.s")||
							lin.trim().startsWith("bne.un")||
							lin.trim().startsWith("bne.un.s")||			
							lin.trim().startsWith("br")||
							lin.trim().startsWith("brfalse")||	
							lin.trim().startsWith("brfalse.s")||
							lin.trim().startsWith("brnull")||	
							lin.trim().startsWith("brnull.s")||
							lin.trim().startsWith("brzero")||	
							lin.trim().startsWith("brzero.s")||
							lin.trim().startsWith("brtrue")||	
							lin.trim().startsWith("brinst")||
							lin.trim().startsWith("brinst.s")||	
							lin.trim().startsWith("leave"))

					{
						String[] temp=lin.trim().split(" ");
						lin = temp[0];
					}


					// filter out all suffixes after the . and all arguments .this is optional filter

					lin=lin.trim();
					int k= lin.indexOf(".");
					if(k>0)
						lin = lin.substring(0,k);

					k=lin.indexOf(" ");
					if(k>0)
						lin = lin.substring(0,k);

					if(lin.trim().startsWith("int")||
							lin.trim().startsWith("float")||
							lin.trim().startsWith("string")||
							lin.trim().startsWith("bool")||
							lin.trim().startsWith("uint")||
							lin.trim().startsWith("IL_")

							)
						continue;

					//end of optional filter



					if(!lin.equals("call")) methodBlockBuffer_binary.add(lin);
				//	methodBlockBuffer_binary.add(lin);
				//	System.out.println(lin);
					
					
				}
				







				if(bInsideMethodBlock && in_lines.get(j).trim().startsWith("} // end of method") )
				{
					
					// this loop is to remove comments and empty lines from method block source code. so it has a real size
					ArrayList<String> temp= new ArrayList<String>();
					for(String llll : methodBlockBuffer_source)
					{						
				     	llll=llll.trim();
					//	if(llll.startsWith("//") ) System.out.println(llll);
						if (llll == null || ("".equals(llll)) || llll.startsWith("//")) continue;
							temp.add(llll);		
					}
					methodBlockBuffer_source.clear();
					methodBlockBuffer_source=temp;
					
					
					
					/*boolean b=(methodBlockBuffer_binary.size()>5);
					System.out.print(b+"   ");
					b=methoBlockStartLine !=Integer.MAX_VALUE;
					System.out.print(b+"  ");
					b=!currentSourceFileAddress.endsWith(".xaml");
					System.out.print(b+"  ");
					b=methodBlockBuffer_source.size()>5;
					System.out.println(b+"  ");
					*/

					
					// if statement is to filter out small fragments && methods with no source code
					
					// This old condition.  methoBlockStartLine =Integer.MAX_VALUE inly if it could not find the start line and end line
					//if (methodBlockBuffer_binary.size()>5 && methoBlockStartLine !=Integer.MAX_VALUE && !currentSourceFileAddress.endsWith(".xaml") && methodBlockBuffer_source.size()>4)

					totalMethodCounter++;
				    if (methodBlockBuffer_binary.size()>5  && !currentSourceFileAddress.endsWith(".xaml") && methodBlockBuffer_source.size()>4 )
					{

						methodCounter++;
						out_lines_binary.add("<source file=\""+currentSourceFileAddress+"\" startline=\""+methoBlockStartLine+"\" endline=\""+methoBlockEndLine+"\"><![CDATA[");
						methodCalls.add("<source file=\""+currentSourceFileAddress+"\" startline=\""+methoBlockStartLine+"\" endline=\""+methoBlockEndLine+"\"><![CDATA[");
						
						methodSigniture.add("<source file=\""+currentSourceFileAddress+"\" startline=\""+methoBlockStartLine+"\" endline=\""+methoBlockEndLine+"\"><![CDATA[");

						//
						sourceCodeFileSet.add(currentSourceFileAddress);
						
						for(String llll : methodBlockBuffer_binary)
						{
							out_lines_binary.add(llll);
						}


						out_lines_binary.add("]]></source>");

						
						out_lines_source.add("<source file=\""+currentSourceFileAddress+"\" startline=\""+methoBlockStartLine+"\" endline=\""+methoBlockEndLine+"\"><![CDATA[");

					//	System.out.println(currentSourceFileAddress);
						//// Added to creat XML for Nicad
					//	System.out.println(currentSourceFileAddress);
						
						
						for(String llll : methodBlockBuffer_source)
						{
							
							StringBuffer out = new StringBuffer(); // Used to hold the output.
							char current; // Used to reference the current character.

							llll=llll.replace("]]","]" );
					     	llll=llll.trim();
						//	if(llll.startsWith("//") ) System.out.println(llll);
							if (llll == null || ("".equals(llll)) || llll.startsWith("//")) continue;
							
							for (int ii = 0; ii < llll.length(); ii++) {
								current = llll.charAt(ii); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
								if ((current == 0x9) ||
										(current == 0xA) ||
										(current == 0xD) ||
										((current >= 0x20) && (current <= 0xD7FF)) ||
										((current >= 0xE000) && (current <= 0xFFFD)) ||
										((current >= 0x10000) && (current <= 0x10FFFF)))
									out.append(current);
							}
							
							
							
								out_lines_source.add(out.toString());

								//// Added to creat XML for Nicad
						
						}

						out_lines_source.add("]]></source>");

						
						for(String s : methodCallsBuffer)
						{
							methodCalls.add(s);
						//	System.out.println(s);

						}						
						methodCalls.add("]]></source>");
						
						
						// It will be better to create a method fot the following code
						// to be done
						String sig="";
						for(String s : methodSignitureBuffer)
						{
							s=s.trim();
							s=s.replaceAll("\n"," ");
							sig=sig+" "+s;
						//	methodSigniture.add(s);
						//	System.out.println(s);

						}
						//System.out.println(sig);
						
						// parse method signiture into Return tupe + nethod name+ list of arguments data type
						int pos = sig.indexOf("(");
						String name= sig.substring(0,pos);
						name=name.trim();
						int pos3= name.lastIndexOf(" ");
						String name2=name.substring(pos3+1);
						name=name.substring(0,pos3);
						name=name.trim();
						pos3= name.lastIndexOf(" ");
						String returnType=name.substring(pos3+1);
						
						int pos2=sig.indexOf(")");
						String args=sig.substring(pos+1,pos2);
						if (args!=null){
							String[] para=args.split(",");
							args="";
							for(int s=0;s<para.length;s++){
								if(para[s].indexOf(" ")>0)
								para[s]=para[s].substring(0,para[s].indexOf(" "));
								args=args+para[s]+" ";
							}
						}
						sig=returnType+" "+name2+" "+args;
						

						methodSigniture.add(sig);
						
						methodSigniture.add("]]></source>");

					}


					methodBlockBuffer_binary.clear();
					methodBlockBuffer_source.clear();
					methodCallsBuffer.clear();
					
					methodSignitureBuffer.clear();
					bInsideMethodBlock=false;
					methoBlockStartLine=Integer.MAX_VALUE;
					methoBlockEndLine=Integer.MAX_VALUE;
				}

			}

		}

		out_lines_binary.add("</source_elements></project>");
		out_lines_source.add("</source_elements></project>");
		methodCalls.add("</source_elements></project>");
		
		methodSigniture.add("</source_elements></project>");


		writeToXMLFile(config,"allFiles.xml",00,"binary",out_lines_binary);
		writeToXMLFile(config,"allFiles.xml",00,"source",out_lines_source);
		writeToXMLFile(config,"method",00,"calls",methodCalls);
		//// create xml file for NiCad
		writeToXMLFile(config,"method",00,"Signiture",methodSigniture);


		
		config.xmlByteCode= config.disassebledAddress+"/allFiles.xml_0_binary"+".xml";
		config.xmlSourceCode= config.disassebledAddress+"/allFiles.xml_0_source"+".xml";
		config.xmlCalledMethods= config.disassebledAddress+"/method_0_calls"+".xml";
		config.xmlmethodSignature=config.disassebledAddress+"/method_0_Signature"+".xml";
		
		//System.out.println("Number of methods extracted : "+totalMethodCounter+ "Number of methods used"+ methodCounter);
		System.out.println("Number of methods extracted and used in dedection:"+ methodCounter);
		

	}





	
	private static void writeToXMLFile(Configuration config,String filename,int fileID,String format,ArrayList<String> lines) throws Exception
	{


		String outputFileAddress=config.disassebledAddress+"/"+"XXXXXXXXXXXXXXXXXXXXX";

		File f=new File(filename);
		outputFileAddress=config.disassebledAddress+"/"+f.getName()+"_"+fileID+"_"+format+".xml";


		BufferedWriter bufferedWriter = null;
		bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));

		for(String line :lines)
		{
			//System.out.println(line);
			bufferedWriter.write(line);
			bufferedWriter.newLine();
		}
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	

	
}