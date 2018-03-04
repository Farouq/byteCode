package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

public class SignitureMatching {

	// private static ArrayList<ArrayList<String>> calledMethodSet=new
	// ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> methodsData1 = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> methodSigniture = new ArrayList<ArrayList<String>>();
	private static ArrayList<String> cscode=new ArrayList<String>();

	
	private static ArrayList<Set<String>> methodInstructionSet = new ArrayList<Set<String>>();

	//private static ArrayList<Set<String>> methodsDataSet = new ArrayList<Set<String>>();

	private static final int NEITHER = 0;
	private static final int UP = 1;
	private static final int LEFT = 2;
	private static final int UP_AND_LEFT = 3;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Configuration config=Configuration.initialize(args[0]);

		//Configuration config=Configuration.loadFromFile();
		config.xmlmethodSignature=config.disassebledAddress+"\\method_0_Signiture.xml";
		
		parse3(config.xmlmethodSignature);

	//	lcsCalledMethods(config);
	//	levenstien(config,config.xmlmethodSignature);
		jaccInstructions(config);
		
	}

	public static void lcsCalledMethods(Configuration config) throws Exception{
		
		int clonePairs=0;
		String outputFileAddress=config.reportAddress+"\\SignitureLCS"+ config.callsLCSThreshold+".xml";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		bufferedWriter.write("<clones>");
		bufferedWriter.newLine();

	
		config.callsLCSThreshold=0.85;
		System.out.println("Number of method extracted: "+methodSigniture.size());
		System.out.println("Similarity Threshold: "+config.callsLCSThreshold);

		


		// LCS similarity for called methods

		for (int v = 0; v < methodSigniture.size(); v++)
		{
			for (int c = v + 1; c < methodSigniture.size(); c++) 
			{

				{
					if (methodSigniture.get(v).size() > 0	&& methodSigniture.get(c).size() > 0)
					{
						double d = (double) LCSAlgorithm(methodSigniture.get(v), methodSigniture.get(c)).size()	* 2	/ (methodSigniture.get(v).size() + methodSigniture.get(c).size());
						if (d>config.callsLCSThreshold){
							d=Math.round(d * 100.0) / 100.0;
							
						bufferedWriter.write("<clone_pair Similarity=\""+d +"\" Verified= \"N\" >");
						bufferedWriter.newLine();
						//System.out.println(d );
						// first fragment
						bufferedWriter.write( "<clone_fragment file=\""+methodsData1.get(v).get(0)+"\" startline=\""+ methodsData1.get(v).get(1) +"\" endline=\""+ methodsData1.get(v).get(2)+"\">");
						bufferedWriter.newLine();
						bufferedWriter.write("<![CDATA["+ getSourceCode( config, methodsData1.get(v).get(0), methodsData1.get(v).get(1))+"]]>");
						bufferedWriter.newLine();
						bufferedWriter.write("</clone_fragment>");
						bufferedWriter.newLine();
						//second fragment
						bufferedWriter.write( "<clone_fragment file=\""+methodsData1.get(c).get(0)+"\" startline=\""+ methodsData1.get(c).get(1) +"\" endline=\""+ methodsData1.get(c).get(2)+"\">");
						bufferedWriter.newLine();
						bufferedWriter.write("<![CDATA["+getSourceCode( config, methodsData1.get(c).get(0), methodsData1.get(c).get(1))+"]]>");
						bufferedWriter.newLine();
						bufferedWriter.write("</clone_fragment>");
						bufferedWriter.newLine();
						//close pair
						bufferedWriter.write("</clone_pair>");
						bufferedWriter.newLine();
					//	System.out.println(methodSigniture.get(v));
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


		System.out.println("Number of clone pairs detected using Callss using LCS: "+ clonePairs);
	}

	public static void jaccInstructions(Configuration config)throws Exception{

	//	parse(config.xmlByteCode);


		// convert list to set of called methods
		for (int v = 0; v < methodSigniture.size(); v++) {

			Set<String> set = new HashSet<String>(methodSigniture.get(v));
			methodInstructionSet.add(set);
		}

		config.signitureJaccThreshold=0.75;
		System.out.println("Jacc threshold is: "+ config.signitureJaccThreshold);

		int clonePairs=0;
		String outputFileAddress=config.reportAddress+"\\SignitureJaccard"+ config.signitureJaccThreshold+".xml";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		bufferedWriter.write("<clones>");
		bufferedWriter.newLine();

	//parse(config.xmlByteCode);

	
		

		for (int v = 0; v < methodInstructionSet.size(); v++) 
		{
			for (int c = v + 1; c < methodInstructionSet.size(); c++)
			{

				{
					if (methodInstructionSet.get(v).size() > 0 && methodInstructionSet.get(c).size() > 0) 
					{
						double d = jaccardAlg(methodInstructionSet.get(v),methodInstructionSet.get(c));

						d=Math.round(d * 100.0) / 100.0;
						//if (d>config.threshold){
							if(d>config.signitureJaccThreshold){
							bufferedWriter.write( "<clone_pair Similarity=\""+d +"\" Verified= \"N\" >");
							bufferedWriter.newLine();
							//System.out.println(d );
							// first fragment
							bufferedWriter.write( "<clone_fragment file=\""+methodsData1.get(v).get(0)+"\" startline=\""+ methodsData1.get(v).get(1) +"\" endline=\""+ methodsData1.get(v).get(2)+"\">");
							bufferedWriter.newLine();
							bufferedWriter.write("<![CDATA["+ getSourceCode( config, methodsData1.get(v).get(0), methodsData1.get(v).get(1))+"]]>");
							bufferedWriter.newLine();
							bufferedWriter.write("</clone_fragment>");
							bufferedWriter.newLine();
							//second fragment
							bufferedWriter.write( "<clone_fragment file=\""+methodsData1.get(c).get(0)+"\" startline=\""+ methodsData1.get(c).get(1) +"\" endline=\""+ methodsData1.get(c).get(2)+"\">");
							bufferedWriter.newLine();
							bufferedWriter.write("<![CDATA["+getSourceCode( config, methodsData1.get(c).get(0), methodsData1.get(c).get(1))+"]]>");
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
		
		System.out.println("Number of clone pairs detected using Jaccard on  Signiture set: "+ clonePairs);

	}
	
	public static void levenstien(Configuration config, String filename)throws Exception	
	{ 

		//Configuration config=Configuration.loadFromFile();

		config.signitureLevThreshold=0.95;
		
		String outputFileAddress2=config.reportAddress+"\\SignitureLevenshtien"+ config.signitureLevThreshold+".xml";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress2));
		bufferedWriter.write("<clones>");
		bufferedWriter.newLine();

		int clonePairs=0;

		System.out.println("Levnstien threshold is: "+ config.signitureLevThreshold);
	

		parseForLev(config.xmlmethodSignature);


		for (int v = 0; v < cscode.size(); v++)
		{
			for (int c = v + 1; c < cscode.size(); c++) 
			{

						
					double max= (cscode.get(v).length()>cscode.get(c).length())? cscode.get(v).length():cscode.get(c).length();
					double d=(double)1-(getLevenshteinDistance(cscode.get(v),cscode.get(c))/max);

					//out.write(i+"	"+d+"\n");


					d=Math.round(d * 100.0) / 100.0;
						
					if (d>=config.signitureLevThreshold){
						
						bufferedWriter.write("<clone_pair Similarity=\""+d +"\" Verified= \"N\" >");
						bufferedWriter.newLine();
						//System.out.println(d );
						// first fragment
						bufferedWriter.write( "<clone_fragment file=\""+methodsData1.get(v).get(0)+"\" startline=\""+ methodsData1.get(v).get(1) +"\" endline=\""+ methodsData1.get(v).get(2)+"\">");
						bufferedWriter.newLine();
						bufferedWriter.write("<![CDATA["+ getSourceCode( config, methodsData1.get(v).get(0), methodsData1.get(v).get(1))+"]]>");
						bufferedWriter.newLine();
						bufferedWriter.write("</clone_fragment>");
						bufferedWriter.newLine();
						//second fragment
						bufferedWriter.write( "<clone_fragment file=\""+methodsData1.get(c).get(0)+"\" startline=\""+ methodsData1.get(c).get(1) +"\" endline=\""+ methodsData1.get(c).get(2)+"\">");
						bufferedWriter.newLine();
						bufferedWriter.write("<![CDATA["+getSourceCode( config, methodsData1.get(c).get(0), methodsData1.get(c).get(1))+"]]>");
						bufferedWriter.newLine();
						bufferedWriter.write("</clone_fragment>");
						bufferedWriter.newLine();
						//close pair
						bufferedWriter.write("</clone_pair>");
						bufferedWriter.newLine();
					//	System.out.println(methodSigniture.get(v));
						clonePairs++;
					
					
				}
				
			}
			
		}
		
		bufferedWriter.write("</clones>");
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();
		
		System.out.println("Number of clone pairs detected using Levention on Method Signiture is: "+ clonePairs);

	}

	public static void parse3(String cs) throws IOException {

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
			} else {
				error = true;
			}

			nl = root.getElementsByTagName("description");
			if (nl.getLength() > 0) {

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

					ArrayList<String> csd = new ArrayList<String>();
					csd.add(file);
					csd.add(startline);
					csd.add(endline);
					methodsData1.add(csd);

					String[] csA = content.split(" ");

					ArrayList<String> csl = new ArrayList<String>();

					for (String t : csA) {

						if (t.trim() == null || t.trim().isEmpty())
							continue;
						csl.add(t.trim());

					}

					methodSigniture.add(csl);


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void parseForLev(String cs) throws IOException {

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
			} else {
				error = true;
			}

			nl = root.getElementsByTagName("description");
			if (nl.getLength() > 0) {

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

					ArrayList<String> csd = new ArrayList<String>();
					csd.add(file);
					csd.add(startline);
					csd.add(endline);
					methodsData1.add(csd);

					String[] csA = content.split(" ");

					String temp=null;
					//ArrayList<String> csl = new ArrayList<String>();

					for (String t : csA) {

						if (t.trim() == null || t.trim().isEmpty())
							continue;
						//csl.add(t.trim());
						temp+=t.trim();

					}

					cscode.add(temp);


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static ArrayList<String> LCSAlgorithm(ArrayList<String> a,
			ArrayList<String> b) {
		int n = a.size();
		int m = b.size();
		int S[][] = new int[n + 1][m + 1];
		int R[][] = new int[n + 1][m + 1];
		int ii, jj;

		// It is important to use <=, not <. The next two for-loops are
		// initialization
		for (ii = 0; ii <= n; ++ii) {
			S[ii][0] = 0;
			R[ii][0] = UP;
		}
		for (jj = 0; jj <= m; ++jj) {
			S[0][jj] = 0;
			R[0][jj] = LEFT;
		}

		// This is the main dynamic programming loop that computes the score and
		// backtracking arrays.
		for (ii = 1; ii <= n; ++ii) {
			for (jj = 1; jj <= m; ++jj) {

				if (a.get(ii - 1).equals(b.get(jj - 1))) {

					S[ii][jj] = S[ii - 1][jj - 1] + 1;
					R[ii][jj] = UP_AND_LEFT;
				}

				else {
					S[ii][jj] = S[ii - 1][jj - 1] + 0;
					R[ii][jj] = NEITHER;
				}

				if (S[ii - 1][jj] >= S[ii][jj]) {
					S[ii][jj] = S[ii - 1][jj];
					R[ii][jj] = UP;
				}

				if (S[ii][jj - 1] >= S[ii][jj]) {
					S[ii][jj] = S[ii][jj - 1];
					R[ii][jj] = LEFT;
				}
			}
		}

		// The length of the longest substring is S[n][m]
		ii = n;
		jj = m;
		int pos = S[ii][jj] - 1;
		ArrayList<String> lcs = new ArrayList<String>();

		// Trace the backtracking matrix.

		while (ii > 0 || jj > 0) {
			if (R[ii][jj] == UP_AND_LEFT) {
				ii--;
				jj--;
				lcs.add(a.get(ii));

			}

			else if (R[ii][jj] == UP) {
				ii--;
			}

			else if (R[ii][jj] == LEFT) {
				jj--;
			}
		}

		ArrayList<String> lcs2 = new ArrayList<String>();
		for (int k = lcs.size() - 1; k >= 0; k--)
			lcs2.add(lcs.get(k));

		return lcs2;
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

	public static int getLevenshteinDistance (String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}


		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			p[i] = i;
		}

		for (j = 1; j<=m; j++) {
			t_j = t.charAt(j-1);
			d[0] = j;

			for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
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
	

}
