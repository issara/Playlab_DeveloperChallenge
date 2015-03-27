import java.io.*;
import java.util.Scanner;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Collections;

public class playlabParse {
	public static void main(String args[]){
		
		String[] temp = new String[15];
		String name;
		
		ArrayList<Integer> countMsgDyno = new ArrayList<>();
		ArrayList<Integer> getMsgDyno = new ArrayList<>();
		ArrayList<Integer> progressDyno = new ArrayList<>();
		ArrayList<Integer> scoreDyno = new ArrayList<>();
		ArrayList<Integer> getMethodsDyno = new ArrayList<>();
		ArrayList<Integer> postMethodsDyno = new ArrayList<>();
		
		ArrayList<Integer> countMsgTime = new ArrayList<>();
		ArrayList<Integer> getMsgTime = new ArrayList<>();
		ArrayList<Integer> progressTime = new ArrayList<>();
		ArrayList<Integer> scoreTime = new ArrayList<>();
		ArrayList<Integer> getMethodsTime = new ArrayList<>();
		ArrayList<Integer> postMethodsTime = new ArrayList<>();
		
		int cMsgCounter = 0;
		int gMsgCounter = 0;
		int progressCounter = 0;
		int scoreCounter = 0;
		int getCounter = 0;
		int postCounter = 0;
		
		try {
			Scanner lineScanner = new Scanner(new FileInputStream("sample.log"));
			
			while (lineScanner.hasNext()) { 
				name = lineScanner.nextLine();
				temp = name.split("\\s");
				if(temp[3].matches("method=GET") && temp[4].matches("path=/api/users/\\d+/count_pending_messages")){
					countMsgDyno.add(cMsgCounter,dynoNum(temp[7]));
					countMsgTime.add(cMsgCounter,calcResponseTime(temp[8],temp[9]));
					cMsgCounter++;
				}
				else if(temp[3].matches("method=GET") && temp[4].matches("path=/api/users/\\d+/get_messages")){
					getMsgDyno.add(gMsgCounter,dynoNum(temp[7]));
					getMsgTime.add(gMsgCounter,calcResponseTime(temp[8],temp[9]));
					gMsgCounter++;
				}
				else if(temp[3].matches("method=GET") && temp[4].matches("path=/api/users/\\d+/get_friends_progress")){
					progressDyno.add(progressCounter,dynoNum(temp[7]));
					progressTime.add(progressCounter,calcResponseTime(temp[8],temp[9]));
					progressCounter++;
				}
				else if(temp[3].matches("method=GET") && temp[4].matches("path=/api/users/\\d+/get_friends_score")){
					scoreDyno.add(scoreCounter,dynoNum(temp[7]));
					scoreTime.add(scoreCounter,calcResponseTime(temp[8],temp[9]));
					scoreCounter++;
				}
				else if(temp[3].matches("method=POST") && temp[4].matches("path=/api/users/\\d+")){
					postMethodsDyno.add(postCounter,dynoNum(temp[7]));
					postMethodsTime.add(postCounter,calcResponseTime(temp[8],temp[9]));
					postCounter++;
				}
				if(temp[3].matches("method=GET") && temp[4].matches("path=/api/users/\\d+/\\w+")){
					getMethodsDyno.add(getCounter,dynoNum(temp[7]));
					getMethodsTime.add(getCounter,calcResponseTime(temp[8],temp[9]));
					getCounter++;
				}
			}
			printEntry(cMsgCounter, countMsgTime,countMsgDyno, "GET /api/users/{user_id}/count_pending_messages");
			printEntry(gMsgCounter, getMsgTime,getMsgDyno, "GET /api/users/{user_id}/count_pending_messages");
			printEntry(progressCounter, progressTime,progressDyno, "GET /api/users/{user_id}/count_pending_messages");
			printEntry(scoreCounter, scoreTime,scoreDyno, "GET /api/users/{user_id}/count_pending_messages");
			printEntry(getCounter, getMethodsTime,getMethodsDyno, "GET /api/users/{user_id}/count_pending_messages");
			printEntry(postCounter, postMethodsTime,getMethodsDyno, "GET /api/users/{user_id}/count_pending_messages");
			
		} catch(FileNotFoundException ex) {
			System.out.println("File not Found");
			System.exit(0);
		}
	}

	public static int calcResponseTime(String a, String b){
		int stemp = 0;
		int stemp2 = 0;
		
		Pattern cPattern = Pattern.compile("connect=(.*?)ms");
		Pattern sPattern = Pattern.compile("service=(.*?)ms");
		
		Matcher cMatcher = cPattern.matcher(a);
		Matcher sMatcher = sPattern.matcher(b);
		if (cMatcher.find()){
		    stemp = Integer.parseInt((cMatcher.group(1)));
		}
		if (sMatcher.find()){
		    stemp2 = Integer.parseInt((sMatcher.group(1)));
		}
		return stemp+stemp2;
	}
	
	public static int dynoNum(String a){
		int stemp = 0;
		Pattern dPattern = Pattern.compile("dyno=web.(.*?)$");
		
		Matcher dMatcher = dPattern.matcher(a);
		if (dMatcher.find()){
		    stemp = Integer.parseInt((dMatcher.group(1)));
		}
		return stemp;
	}
	
	public static int meanCalc(ArrayList<Integer> a){
		double k=0;
		for(int j=0; j<a.size(); j++){
			k += a.get(j);
		}
		double mean=k/(a.size());
		int mean2 = (int)Math.round(mean);
		return mean2;
	}
	
	public static int medianCalc(ArrayList<Integer> a){
		Collections.sort(a);
		double l = a.size()/2;
		int l2 = (int)Math.ceil(l);
		double median = a.get(l2);
		int median2 = (int)Math.round(median);
		return median2;
	}

	public static int modeCalc(ArrayList<Integer> a){
		ArrayList<Integer> dupA = new ArrayList<>(a.size());
		dupA = a;
		Collections.sort(dupA);
		int trysize = dupA.get(dupA.size()-1);
		int[] tempArray = new int[trysize+1];
		int check=0;
		int mode=0;
	
		for (int i=0; i<a.size(); i++){
			tempArray[a.get(i)]++;
		}	
		
		for(int i=0; i<a.size(); i++){
			if(check<tempArray[a.get(i)]){
				check = tempArray[a.get(i)];
				mode = a.get(i);
			}
		}
		return mode;
	}

	public static void printEntry(int counter, ArrayList<Integer> a, ArrayList<Integer> b, String name){
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("URL endpoint: " + name);
		System.out.println("Number of times "+name+" called: "  + counter + " times");
		System.out.println("Mean: "+ meanCalc(a));
		System.out.println("Median: "+ medianCalc(a));
		System.out.println("Mode: "+ modeCalc(a));
		System.out.println("Number of Dynos: " + b.size());
		System.out.println("Most frequent Dyno used: " + modeCalc(b));
	}
}
