/**
 * 
 */
package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import publisher.StarterPublisher;
import starter.Starter_Preparation_Step1;
import configuration.Configuration;

/**
 * @author faa634
 *
 */

public class DetectClones {

	/**
	 * @param args
	 */
	private static ArrayList<String> byteCode=new ArrayList<String>();
	private static ArrayList<ArrayList<String>> methodData=new ArrayList<ArrayList<String>>();

	//methodsData contain the same contents as methodData but extrateced form another xml file
	private static ArrayList<ArrayList<String>> calledMethods = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> methodsDataForCalls = new ArrayList<ArrayList<String>>();
	
	// ArrayList contain Method Signiture
	private static ArrayList<ArrayList<String>> methodSigniture = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> methodsDataForSignature = new ArrayList<ArrayList<String>>();

	private static final int NEITHER = 0;
	private static final int UP = 1;
	private static final int LEFT = 2;
	private static final int UP_AND_LEFT = 3;

	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		Configuration config=Configuration.initialize(args[0]);

		Starter_Preparation_Step1.makeOutputFolders(config);
		
		StarterPublisher.start(config);
		
		loadDataIntoArraylists(config);
		
	//	detectWeightAverage(config);
	
		/* 
		 * Just to check if all methods data are the same 
		System.out.println( isTwoArrayListsWithSameValues(methodData,methodsDataForCalls));
		System.out.println( isTwoArrayListsWithSameValues(methodsDataForSignature,methodsDataForCalls));
		System.out.println (methodData.size());
		System.out.println (methodsDataForCalls.size());
		System.out.println (methodsDataForSignature.size());
		*/

		// selected method of combination		
//		detectUnionSimilarities(config);
		
		// is good

		
		// intersection is not effectine 57 clone pairs common 51 extra 6 missed 44
	    // detectIntersectionSimilarities(config);
		
		
		
//		System.out.println(byteCode.size());
//		System.out.println(methodData.size());
//		System.out.println(calledMethods.size());
//		System.out.println(methodsData.size());
//
//		System.out.println(methodSigniture.size());
//		System.out.println(methodsData1.size());
//		
//		if(methodData.equals(methodsData1)) 		System.out.println("equal");

	}
	
	public static void loadDataIntoArraylists(Configuration config) throws Exception {
		
		config.xmlByteCode=config.disassebledAddress+"\\allFiles.xml_0_binary.xml";
		parse(config.xmlByteCode);
		
		config.xmlCalledMethods=config.disassebledAddress+"\\method_0_calls.xml";
		parse2(config.xmlCalledMethods);
		
		config.xmlmethodSignature=config.disassebledAddress+"\\Method_0_Signiture.xml";
		parse3(config.xmlmethodSignature);
		
	}
	
	public static void detectWeightAverage(Configuration config)throws Exception	
	{ 

		String outputFileAddress2=config.reportAddress+"\\FinalCloneReportWeighted Similarities."+ config.threshold+".xml";
		
		BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(outputFileAddress2));
		bufferedWriter2.write("<clones>");
		bufferedWriter2.newLine();
		
		int progress=0;
		int clonePairs=0;
		int progressStep=byteCode.size()/100;
		
		System.out.println("detcting... ");

		for(int v =0; v<byteCode.size()-1;v++){
			//System.out.print(v);
			for(int c =v+1; c<byteCode.size(); c++){ //cscode.size();c++){

				if(byteCode.get(v).length()<2000 && byteCode.get(c).length()<2000)
				{
					double max= (byteCode.get(v).length()>byteCode.get(c).length())? byteCode.get(v).length():byteCode.get(c).length();
					double dInstruction=(double)1-(getLevenshteinDistance(byteCode.get(v),byteCode.get(c))/max);

					//out.write(i+"	"+d+"\n");
										
				
					double dMethod;
					if (calledMethods.get(v).size() > 0	&& calledMethods.get(c).size() > 0)
					{
						 dMethod = (double) LCSAlgorithm(calledMethods.get(v), calledMethods.get(c)).size()	* 2	/ (calledMethods.get(v).size() + calledMethods.get(c).size());
					} else{
						 dMethod=0f;
					}
					
					double dSigniture;
					if (methodSigniture.get(v).size() > 0	&& methodSigniture.get(c).size() > 0)
					{
						dSigniture = (double) LCSAlgorithm(methodSigniture.get(v), methodSigniture.get(c)).size()	* 2	/ (methodSigniture.get(v).size() + methodSigniture.get(c).size());
					}else{
						dSigniture=0;
					}

					


					// old condition 
					double finalSimilarity=dInstruction*0.6 + dMethod*0.2 + dSigniture*0.2;
					if (finalSimilarity>=config.threshold /*|| (dSigniture>=0.8 && dInstruction>=0.8)*/) {
					
						//double finalSimilarity=dInstruction*0.6+ dMethod *0.2+ dSigniture*0.3;
						//if (finalSimilarity>=0.7  ){

						// write to report  / all detected cloned using binarcode 
						clonePairs++;

						finalSimilarity=Math.round(finalSimilarity*100.0)/100.0;
						
						bufferedWriter2.write( "<clone_pair pairid=\""+ clonePairs+"\" semantic_similarity= \""+finalSimilarity+"\" >");
						bufferedWriter2.newLine();
						//System.out.println(d );
						// first fragment
						bufferedWriter2.write( "<clone_fragment file=\""+methodData.get(v).get(0)+"\" startline=\""+ methodData.get(v).get(1) +"\" endline=\""+ methodData.get(v).get(2)+"\">");
						bufferedWriter2.newLine();
						bufferedWriter2.write("<![CDATA["+ getsource( config, methodData.get(v).get(0), methodData.get(v).get(1))+"]]>");
						bufferedWriter2.newLine();
						bufferedWriter2.write("</clone_fragment>");
						bufferedWriter2.newLine();
						//second fragment
						bufferedWriter2.write( "<clone_fragment file=\""+methodData.get(c).get(0)+"\" startline=\""+ methodData.get(c).get(1) +"\" endline=\""+ methodData.get(c).get(2)+"\">");
						bufferedWriter2.newLine();
						bufferedWriter2.write("<![CDATA["+getsource( config, methodData.get(c).get(0), methodData.get(c).get(1))+"]]>");
						bufferedWriter2.newLine();
						bufferedWriter2.write("</clone_fragment>");
						bufferedWriter2.newLine();
						//close pair
						bufferedWriter2.write("</clone_pair>");
						bufferedWriter2.newLine();

						// check if this clone pairs  Did not detected in nicad	then save these clones in a seperate file

					}


				} //outer If


			}	// inner loop 
			
			int i= v%progressStep;
			if(i==0) {
				progress=((v+1)*100 /byteCode.size());
				System.out.println("progress: "+ progress+"%");
			}
			
			
	}// outer loop

		
		System.out.println("Number of clone pairs detected using byteCode, Calls and Signiture :"+ clonePairs );


		// close file		

		bufferedWriter2.write("</clones>");
		bufferedWriter2.newLine();
		bufferedWriter2.flush();
		bufferedWriter2.close();

	}


	
	public static void detectUnionSimilarities(Configuration config)throws Exception	
	{ 
		
		String outputFileAddress2=config.reportAddress+"\\FinalCloneReport.xml";
		
		BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(outputFileAddress2));
		bufferedWriter2.write("<clones>");
		bufferedWriter2.newLine();
		
		int progress=0;
		int clonePairs=0;
		
		System.out.println("detcting... ");

		for(int v =0; v<byteCode.size()-1;v++){
			//System.out.print(v);
			for(int c =v+1; c<byteCode.size(); c++){ //cscode.size();c++){

				double dInstruction=0;
				if(byteCode.get(v).length()<2000 && byteCode.get(c).length()<2000)
				{
					double max= (byteCode.get(v).length()>byteCode.get(c).length())? byteCode.get(v).length():byteCode.get(c).length();
					 dInstruction=(double)1-(getLevenshteinDistance(byteCode.get(v),byteCode.get(c))/max);
				}

			
					double dMethod;
					if (calledMethods.get(v).size() > 0	&& calledMethods.get(c).size() > 0)
					{
						 dMethod = (double) LCSAlgorithm(calledMethods.get(v), calledMethods.get(c)).size()	* 2	/ (calledMethods.get(v).size() + calledMethods.get(c).size());
					} else{
						 dMethod=0f;
					}
					
					double dSigniture;
					if (methodSigniture.get(v).size() > 0	&& methodSigniture.get(c).size() > 0)
					{
						dSigniture = (double) LCSAlgorithm(methodSigniture.get(v), methodSigniture.get(c)).size()	* 2	/ (methodSigniture.get(v).size() + methodSigniture.get(c).size());
					}else{
						dSigniture=0;
					}


					if (dInstruction>=config.instructionLevThreshold||dMethod>=config.callsLCSThreshold||dSigniture>=config.signitureLCSThreshold){

						clonePairs++;
						dInstruction=Math.round(dInstruction * 100.0) / 100.0;
						dMethod=Math.round(dMethod * 100.0) / 100.0;
						dSigniture=Math.round(dSigniture * 100.0) / 100.0;

						bufferedWriter2.write( "<clone_pair  Instruction_Sim=\""+dInstruction +"\""+" Calls_Sim=\""+dMethod +"\"" + " Signiture_Sim=\""+dSigniture +"\"" +" Verified= \"N\" >");
						bufferedWriter2.newLine();
						//System.out.println(d );
						// first fragment
						bufferedWriter2.write( "<clone_fragment file=\""+methodData.get(v).get(0)+"\" startline=\""+ methodData.get(v).get(1) +"\" endline=\""+ methodData.get(v).get(2)+"\">");
						bufferedWriter2.newLine();
						bufferedWriter2.write("<![CDATA["+ getsource( config, methodData.get(v).get(0), methodData.get(v).get(1))+"]]>");
						bufferedWriter2.newLine();
						bufferedWriter2.write("</clone_fragment>");
						bufferedWriter2.newLine();
						//second fragment
						bufferedWriter2.write( "<clone_fragment file=\""+methodData.get(c).get(0)+"\" startline=\""+ methodData.get(c).get(1) +"\" endline=\""+ methodData.get(c).get(2)+"\">");
						bufferedWriter2.newLine();
						bufferedWriter2.write("<![CDATA["+getsource( config, methodData.get(c).get(0), methodData.get(c).get(1))+"]]>");
						bufferedWriter2.newLine();
						bufferedWriter2.write("</clone_fragment>");
						bufferedWriter2.newLine();
						//close pair
						bufferedWriter2.write("</clone_pair>");
						bufferedWriter2.newLine();


					}
				

			}	// inner loop 
			progress++;
	}// outer loop

		
		System.out.println("Number of clone pairs detected using Union all similarities:"+ clonePairs );


		// close file		

		bufferedWriter2.write("</clones>");
		bufferedWriter2.newLine();
		bufferedWriter2.flush();
		bufferedWriter2.close();
	}
	
	
	public static void detectIntersectionSimilarities(Configuration config)throws Exception	
	{ 
		
		String outputFileAddress2=config.reportAddress+"\\FinalCloneReportIntersection"+ config.instructionLevThreshold+".xml";
		
		BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(outputFileAddress2));
		bufferedWriter2.write("<clones>");
		bufferedWriter2.newLine();
		
		int progress=0;
		int clonePairs=0;
		
		System.out.println("detcting... ");

		for(int v =0; v<byteCode.size()-1;v++){
			//System.out.print(v);
			for(int c =v+1; c<byteCode.size(); c++){ //cscode.size();c++){

				double dInstruction=0;
				if(byteCode.get(v).length()<2000 && byteCode.get(c).length()<2000)
				{
					double max= (byteCode.get(v).length()>byteCode.get(c).length())? byteCode.get(v).length():byteCode.get(c).length();
					 dInstruction=(double)1-(getLevenshteinDistance(byteCode.get(v),byteCode.get(c))/max);
				}

			
					double dMethod;
					if (calledMethods.get(v).size() > 0	&& calledMethods.get(c).size() > 0)
					{
						 dMethod = (double) LCSAlgorithm(calledMethods.get(v), calledMethods.get(c)).size()	* 2	/ (calledMethods.get(v).size() + calledMethods.get(c).size());
					} else{
						 dMethod=0f;
					}
					
					double dSigniture;
					if (methodSigniture.get(v).size() > 0	&& methodSigniture.get(c).size() > 0)
					{
						dSigniture = (double) LCSAlgorithm(methodSigniture.get(v), methodSigniture.get(c)).size()	* 2	/ (methodSigniture.get(v).size() + methodSigniture.get(c).size());
					}else{
						dSigniture=0;
					}


					if (dInstruction>=config.instructionLevThreshold && dMethod>=config.callsLCSThreshold && dSigniture>=config.signitureLCSThreshold){

						clonePairs++;

						bufferedWriter2.write( "<clone_pair>");
						bufferedWriter2.newLine();
						//System.out.println(d );
						// first fragment
						bufferedWriter2.write( "<clone_fragment file=\""+methodData.get(v).get(0)+"\" startline=\""+ methodData.get(v).get(1) +"\" endline=\""+ methodData.get(v).get(2)+"\">");
						bufferedWriter2.newLine();
						bufferedWriter2.write("<![CDATA["+ getsource( config, methodData.get(v).get(0), methodData.get(v).get(1))+"]]>");
						bufferedWriter2.newLine();
						bufferedWriter2.write("</clone_fragment>");
						bufferedWriter2.newLine();
						//second fragment
						bufferedWriter2.write( "<clone_fragment file=\""+methodData.get(c).get(0)+"\" startline=\""+ methodData.get(c).get(1) +"\" endline=\""+ methodData.get(c).get(2)+"\">");
						bufferedWriter2.newLine();
						bufferedWriter2.write("<![CDATA["+getsource( config, methodData.get(c).get(0), methodData.get(c).get(1))+"]]>");
						bufferedWriter2.newLine();
						bufferedWriter2.write("</clone_fragment>");
						bufferedWriter2.newLine();
						//close pair
						bufferedWriter2.write("</clone_pair>");
						bufferedWriter2.newLine();


					}
				

			}	// inner loop 
			progress++;
	}// outer loop

		
		System.out.println("Number of clone pairs detected using Intersection all similarities:"+ clonePairs );


		// close file		

		bufferedWriter2.write("</clones>");
		bufferedWriter2.newLine();
		bufferedWriter2.flush();
		bufferedWriter2.close();
	}
	

	public static String getsource(Configuration config, String fileName, String startLine) throws IOException{

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


	public static void parse(String xmlFileName ) throws IOException{


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


					String []csA = content.split("\n");
					String temp=null;

					ArrayList<String> csl=new ArrayList<String>();

					for(String t:csA){

						if(t.trim()==null || t.trim().isEmpty())
							continue;
						csl.add(t.trim());
						temp+=t.trim();
					}

					byteCode.add(temp);

					/*
					items++;


					System.out.println(i+"================================ "+file);
					System.out.println(" start line = "+startline );
					System.out.println(csl.toString());
					 */
					//System.out.println("node size "+loc);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		

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
	
	
	public static void parse2(String cs) throws IOException {

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
					methodsDataForCalls.add(csd);
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

					calledMethods.add(csl);
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
					methodsDataForSignature.add(csd);

					String[] csA = content.split("\n");

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
	
	
    public static boolean isTwoArrayListsWithSameValues(ArrayList<ArrayList<String>> list1, ArrayList<ArrayList<String>> list2)
    {
    	//ArrayList<String> temp= new ArrayList<String>();
        //null checking
        if(list1==null && list2==null)
            return true;
        if((list1 == null && list2 != null) || (list1 != null && list2 == null))
            return false;

        if(list1.size()!=list2.size())
            return false;
        for(ArrayList<String> itemList1: list1)
        {
            if(!list2.contains(itemList1))
                return false;
        }

        return true;
    }
	
}
