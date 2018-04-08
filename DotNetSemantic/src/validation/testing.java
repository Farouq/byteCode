package validation;

import java.util.ArrayList;

public class testing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<ArrayList<String>> cloneList = new ArrayList<ArrayList<String>>();
		
		
		for( int j=0;j<10;j++) {
			ArrayList<String> clone = new ArrayList<String>();
		for (int i=0;i<=5; i++) {
			clone.add("A"+i);
			}
		cloneList.add(clone);
		}
		
		
		System.out.println(cloneList.size());
		for(ArrayList<String> clones:cloneList) {
			System.out.println(clones);
		}
	
		
		for (int i=0;i<cloneList.size(); i++) {
			if (i%3==0) {
			cloneList.remove(cloneList.size()-1);
			cloneList.get(i).set(1, "changed");
			
			}
			}
		System.out.println(cloneList.size());
		for(ArrayList<String> clones:cloneList) {
			System.out.println(clones);
		}

		
		
		
	}

}
