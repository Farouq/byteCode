package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simhash.CloneGroup;
import simhash.SourceItem;
import simhash.TestHash2;
import ca.usask.cs.srlab.util.XMLUtil;
import configuration.Configuration;

public class InstructionJacc {

	// private static ArrayList<ArrayList<String>> methodInstructionset=new
	// ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> methodsData = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> methodInstructions = new ArrayList<ArrayList<String>>();

	private static ArrayList<Set<String>> methodInstructionSet = new ArrayList<Set<String>>();

	private static final int NEITHER = 0;
	private static final int UP = 1;
	private static final int LEFT = 2;
	private static final int UP_AND_LEFT = 3;

	public static void main(String[] args) throws Exception {
		
		// TODO Auto-generated method stub
		Configuration config=Configuration.loadFromFile();
		config.xmlByteCode=config.disassebledAddress+"\\allFiles.xml_0_binary.xml";
		jaccmethodInstructions(config);
		

	}

	
	
	public static void jaccmethodInstructions(Configuration config)throws Exception{

		parse(config.xmlByteCode);


		// convert list to set of called methods
		for (int v = 0; v < methodInstructions.size(); v++) {

			Set<String> set = new HashSet<String>(methodInstructions.get(v));
			methodInstructionSet.add(set);
		}

		int clonePairs=0;
		String outputFileAddress=config.reportAddress+"\\InstructionsJaccard"+ config.instructionJaccThreshold+".xml";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		bufferedWriter.write("<clones>");
		bufferedWriter.newLine();

	//parse(config.xmlByteCode);

		System.out.println();
		

		for (int v = 0; v < methodInstructionSet.size(); v++) 
		{
			for (int c = v + 1; c < methodInstructionSet.size(); c++)
			{

				{
					if (methodInstructionSet.get(v).size() > 0 && methodInstructionSet.get(c).size() > 0) 
					{
						double d = jaccardAlg(methodInstructionSet.get(v),methodInstructionSet.get(c));

						//if (d>config.threshold){
							if(d>config.instructionJaccThreshold){
							bufferedWriter.write( "<clone_pair>");
							bufferedWriter.newLine();
							//System.out.println(d );
							// first fragment
							bufferedWriter.write( "<clone_fragment file=\""+methodsData.get(v).get(0)+"\" startline=\""+ methodsData.get(v).get(1) +"\" endline=\""+ methodsData.get(v).get(2)+"\">");
							bufferedWriter.newLine();
							bufferedWriter.write("<![CDATA["+ getSourceCode( config, methodsData.get(v).get(0), methodsData.get(v).get(1))+"]]>");
							bufferedWriter.newLine();
							bufferedWriter.write("</clone_fragment>");
							bufferedWriter.newLine();
							//second fragment
							bufferedWriter.write( "<clone_fragment file=\""+methodsData.get(c).get(0)+"\" startline=\""+ methodsData.get(c).get(1) +"\" endline=\""+ methodsData.get(c).get(2)+"\">");
							bufferedWriter.newLine();
							bufferedWriter.write("<![CDATA["+getSourceCode( config, methodsData.get(c).get(0), methodsData.get(c).get(1))+"]]>");
							bufferedWriter.newLine();
							bufferedWriter.write("</clone_fragment>");
							bufferedWriter.newLine();
							//close pair
							bufferedWriter.write("</clone_pair>");
							bufferedWriter.newLine();
							
							clonePairs++;
						}
					}
				}
			}
		}

		bufferedWriter.write("</clones>");
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();
		
		System.out.println("Number of clone pairs detected using Jaccard on  instruction set: "+ clonePairs);

	}



	public static void parse(String cs) throws IOException {

		boolean error = false;
		File fileName = new File(cs);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fileName);
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();

			NodeList nl = root.getElementsByTagName("name");
			if (nl.getLength() > 0) {
				// projectName = nl.item(0).getFirstChild().getNodeValue();
			} else {
				error = true;
			}

			nl = root.getElementsByTagName("description");
			if (nl.getLength() > 0) {
				// projectDesc =
				// nl.item(0).getChildNodes().item(0).getNodeValue();
			} else {
				error = true;
			}

			nl = root.getElementsByTagName("source_elements");
			if (nl.getLength() > 0) {
				NodeList sourceList = nl.item(0).getChildNodes();

				long start = System.currentTimeMillis();
				long items = 0;
				for (int i = 0; i < sourceList.getLength(); i++) {
					Node source = sourceList.item(i);
					if (source.getNodeType() != Node.ELEMENT_NODE)
						continue;

					String file = source.getAttributes().getNamedItem("file").getFirstChild().getNodeValue();
					String startline = source.getAttributes().getNamedItem("startline").getFirstChild().getNodeValue();
					String endline = source.getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					String content = source.getFirstChild().getTextContent();

					// int loc = computeLoc(content);

					/*
					 * if(content.split("\n").length-1 != loc){
					 * System.out.println("gotcha!"); }
					 */

					ArrayList<String> csd = new ArrayList<String>();
					csd.add(file);
					csd.add(startline);
					csd.add(endline);
					methodsData.add(csd);
					// System.out.println( file+"\n"+ startline+ "\n"+
					// endline+"\n");

					String[] csA = content.split("\n");

					ArrayList<String> csl = new ArrayList<String>();

					for (String t : csA) {

						if (t.trim() == null || t.trim().isEmpty())
							continue;
						csl.add(t.trim());
						// System.out.println(t +"\n");

					}

					methodInstructions.add(csl);
					// System.out.println(csl.size());

					/*
					 * items++;
					 * 
					 * 
					 * System.out.println(i+"================================ "+file
					 * ); System.out.println(" start line = "+startline );
					 * System.out.println(csl.toString());
					 */
					// System.out.println("node size "+loc);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	public static double jaccardAlg(Set<String> a, Set<String> b) {
		double jaccard;

		Set<String> intersection = new HashSet<String>(a);
		Set<String> union = new HashSet<String>(a);

		intersection.retainAll(b);
		union.addAll(b);

		jaccard = (double) intersection.size() / union.size();
		return jaccard;
	}


	public static String getSourceCode(Configuration config, String fileName, String startLine) throws IOException{

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
					//String endlines = sources.getAttributes().getNamedItem("endline").getFirstChild().getNodeValue();
					String contents = sources.getFirstChild().getTextContent();

					if(fileName.equals(files) && startLine.equals(startlines))
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

}
