package semantic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import configuration.Configuration;

public class XmlWriter {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		ArrayList<String> answers= new ArrayList<String>(){{
			   add("Question tiltile");
			   add("Answer2");
			   add("Answer3");
			   }};


				ArrayList<ArrayList<String>> questions= new ArrayList<ArrayList<String>>(){{
					   add(answers);
					   add(answers);
					   add(answers);
					   }};
					   
					   writeToXML(questions);
		
	}

	
	private static void writeToXML(ArrayList<ArrayList<String>> questions) throws Exception
	{
		String outputFileAddress="filename.xml";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));
		bufferedWriter.write("<StackOverflow>");
		bufferedWriter.newLine();

		for(int i=0; i<questions.size();i++ ){

			bufferedWriter.write( "<Question Title=\""+ questions.get(i).get(0)+"\" >");
			bufferedWriter.newLine();
			//System.out.println(d );
			// first fragment
			for(int j=1; i<questions.size();i++ ){
			bufferedWriter.write( "<Answer ID=\""+" you can add id here"+"\" Score=\""+ "you can add score or anything" +"\">");
			bufferedWriter.newLine();
			bufferedWriter.write("<![CDATA["+ questions.get(i).get(j)+"]]>");
			bufferedWriter.newLine();
			bufferedWriter.write("</Answer>");
			bufferedWriter.newLine();
			}

			bufferedWriter.write("</Question>");
			bufferedWriter.newLine();
		}

		bufferedWriter.write("</StackOverflow>");
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();


	}
	
	private static void writeToText(ArrayList<ArrayList<String>> clones) throws Exception
	{
		String outputFileAddress="fileNames.txt";
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileAddress));


		for(int i=0; i<clones.size();i++ ){


			bufferedWriter.write(clones.get(i).get(0));
			bufferedWriter.newLine();
			bufferedWriter.write(clones.get(i).get(3));
			bufferedWriter.newLine();
		//	bufferedWriter.write(clones.get(i).get(1));
		//	bufferedWriter.newLine();


		}

		bufferedWriter.flush();
		bufferedWriter.close();


	}
	
	
}
