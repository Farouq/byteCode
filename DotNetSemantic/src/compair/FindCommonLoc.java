package compair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class FindCommonLoc {

	
	
	
	public static void locSummery(ArrayList<ArrayList<String>> clones ,ArrayList<ArrayList<String>> VSClones ) {
		
		Set<ArrayList<String>> myCloneSet = generateListOfFiles(clones);
		System.out.println("-------------------------------------------------------------------------------- ");
		System.out.println("Number of clone fragments  detected by my tool: " + myCloneSet.size());
		ArrayList<ArrayList<String>> cloneList = new ArrayList<ArrayList<String>>();
		//ArrayList<String> clone=new ArrayList<String>();
		cloneList.addAll(myCloneSet);
		cloneList=sortReport(cloneList);
//		for (ArrayList<String> clone:cloneList) {			
//			System.out.println(clone);
//		}
		
		System.out.println("Total number of cloned LOC by my tool: " +clonedLoc(cloneList));
		
		
		Set<ArrayList<String>> VSCloneSet = generateListOfFiles(VSClones);
		System.out.println("Number of clone fragments  detected by Other tool: " + VSCloneSet.size());
		ArrayList<ArrayList<String>> VSCloneList = new ArrayList<ArrayList<String>>();
		//ArrayList<String> clone=new ArrayList<String>();
		VSCloneList.addAll(VSCloneSet);
		VSCloneList=sortReport(VSCloneList);
//		for (ArrayList<String> clone:VSCloneList) {			
//			System.out.println(clone);
//		}
//		
		System.out.println("Total number of cloned LOC by Used tool : " +clonedLoc(VSCloneList));
		
		ArrayList<ArrayList<String>> VSCloneListClean=deleteDuplication(VSCloneList);
		System.out.println("Number of clone fragments  detected by Other tool after deletion of duplication : " + VSCloneListClean.size());
		
		System.out.println("Total number of cloned LOC by Used tool : " +clonedLoc(VSCloneList));
		System.out.println("Total number of cloned LOC by Used tool after clean up: " +clonedLoc(VSCloneListClean));
		System.out.println("Total number of common Loc between two detectors: " + commonLoc(cloneList,VSCloneList));
		
		System.out.println("-------------------------------------------------------------------------------- ");

		
		
	}
	
	
	
	public static int max( int a, int b){
		if (a>b) {
			return a;
		}
		else { 
			return b;
		}
		
	}
	
	public static int min( int a, int b){
		if (a<b) {
			return a;
		}
		else { 
			return b;
		}
		
	}
	
	public static int commonLoc(ArrayList<ArrayList<String>> myList, ArrayList<ArrayList<String>> detectorList) {

		int loc = 0;

		for (ArrayList<String> clone : myList) {
			
			for (ArrayList<String> cloneToCompare : detectorList) {
				
				if(clonesIntersect(clone,cloneToCompare)) {
					
					int l1 = Integer.parseInt(clone.get(1).trim());
					int r1 = Integer.parseInt(clone.get(2).trim());
										
					int l2 = Integer.parseInt(cloneToCompare.get(1).trim());
					int r2 = Integer.parseInt(cloneToCompare.get(2).trim());
					
					loc+= min(r1,r2)- max(l1,l2)+1;
				}

			}
			

		}

		return loc;
	}
	
	
	public static boolean clonesIntersect(ArrayList<String> cloneA, ArrayList<String> cloneB) {
		boolean intersect = false;


		String f1 = cloneA.get(0).toLowerCase();
		int l1 = Integer.parseInt(cloneA.get(1).trim());
		int r1 = Integer.parseInt(cloneA.get(2).trim());
		
		String f2 = cloneB.get(0).toLowerCase();
		int l2 = Integer.parseInt(cloneB.get(1).trim());
		int r2 = Integer.parseInt(cloneB.get(2).trim());

			if (f1.equals(f2) && l1 <= r2 && r1 >= l2) {
				intersect = true;
			}

		return intersect;
	}
	
	public static Set<ArrayList<String>> generateListOfFiles( ArrayList<ArrayList<String>> clonePairs){
		Set<ArrayList<String>> files = new HashSet<ArrayList<String>>();
		
		for (int i=0; i<clonePairs.size(); i++){
			ArrayList<String> clone1=new ArrayList<String>();
			ArrayList<String> clone2=new ArrayList<String>();
			clone1.add(clonePairs.get(i).get(0));
			clone1.add(clonePairs.get(i).get(1));
			clone1.add(clonePairs.get(i).get(2));
			clone2.add(clonePairs.get(i).get(3));
			clone2.add(clonePairs.get(i).get(4));
			clone2.add(clonePairs.get(i).get(5));
			files.add(clone1);
			files.add(clone2);
			
		}
		
		return files;
	}

	public static ArrayList<ArrayList<String>> deleteDuplication(ArrayList<ArrayList<String>> myList) {

		ArrayList<ArrayList<String>> cloneList = new ArrayList<ArrayList<String>>();
		
		 for (int i=0; i< myList.size()-1;i++) {

			for (int j=i+1; j< myList.size();j++ ) {

				if (clonesIntersect(myList.get(i),myList.get(j) ) ){

				//	System.out.println("Duplicate file deleted ");
					int l1 = Integer.parseInt(myList.get(i).get(1).trim());
					int r1 = Integer.parseInt(myList.get(i).get(2).trim());
					int l2 = Integer.parseInt(myList.get(j).get(1).trim());
					int r2 = Integer.parseInt(myList.get(j).get(2).trim());
					
					l1=min(l1,l2);
					r1=max(r1,r2);
				//	myList.get(i).set(1,Integer.toString(l1));
				//	myList.get(i).set(2,Integer.toString(r1));
					myList.remove(j);
					
				
				}
			}
		 }
		 
			
			return myList;

	}
	
	public static ArrayList<ArrayList<String>> deleteDuplication2(ArrayList<ArrayList<String>> myList) {

		Set<ArrayList<String>> cloneSet = new HashSet<ArrayList<String>>();
		
		ArrayList<ArrayList<String>> cloneList = new ArrayList<ArrayList<String>>();
		
		 for (int i=0; i< myList.size()-1;i++) {

			for (int j=i+1; j< myList.size();j++ ) {

				if (clonesIntersect(myList.get(i),myList.get(j) ) ){

					//System.out.println("Duplicate file deleted ");
					int l1 = Integer.parseInt(myList.get(i).get(1).trim());
					int r1 = Integer.parseInt(myList.get(i).get(2).trim());
					int l2 = Integer.parseInt(myList.get(j).get(1).trim());
					int r2 = Integer.parseInt(myList.get(j).get(2).trim());
					
					l1=min(l1,l2);
					r1=max(r1,r2);
					myList.get(j).set(1,Integer.toString(l1));
					myList.get(j).set(2,Integer.toString(r1));
					//myList.remove(j);
									
				} else{
					cloneSet.add(myList.get(i));
				}
				
			}
		 }
		 
		 cloneList.addAll(cloneSet);			
			return cloneList;

	}

	public static ArrayList<ArrayList<String>> sortReport( ArrayList<ArrayList<String>> clones){

		Collections.sort(clones, new Comparator<ArrayList<String>>() {
			@Override
			public int compare(ArrayList<String> one, ArrayList<String> two) {
				return two.get(0).compareTo(one.get(0));
			}
		});

		return clones;
	}
	
	
	public static int clonedLoc( ArrayList<ArrayList<String>> clonesList){

		int loc=0;

		for (ArrayList<String> clone:clonesList) {			
		loc+= Integer.parseInt(clone.get(2).trim())-Integer.parseInt(clone.get(1).trim())+1;
	}

		return loc;
	}
	
}
