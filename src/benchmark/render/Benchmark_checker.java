package benchmark.render;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.netlib.util.booleanW;
import org.tukaani.xz.check.Check;

public class Benchmark_checker {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashSet<String> targets=getTargets();
		HashSet<String> drugs=getDrugs();
		
		HashSet<String> twolayers=new HashSet<>();
		twolayers.add("targetClass");
		twolayers.add("drugClass");
		
		HashSet<String> onelayers=new HashSet<>();
		onelayers.add("disease");
		onelayers.add("clinicalCT");
		
		BufferedWriter beWriter =new BufferedWriter(new FileWriter(new File(dataDir+"/output/datasets/experiment/debug.txt")));
		for(String twolayer:twolayers) {
			for(File dir: new File(dataDir+"/output/datasets/experiment/"+twolayer).listFiles()) {
				for(File file: dir.listFiles()) {
					check( file.getAbsolutePath(),  drugs,  targets, beWriter) ;
				}
			}
		}
		
		for(String onelayer:onelayers) {
			for(File file: new File(dataDir+"/output/datasets/experiment/"+onelayer).listFiles()) {
				check( file.getAbsolutePath(),  drugs,  targets, beWriter) ;
			}
		}
		beWriter.flush();
		beWriter.close();
	}

	public static void check(String file, HashSet<String> drugs, HashSet<String> targets , BufferedWriter beWriter) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashSet<String> positive=new HashSet<>();
		HashSet<String> negative=new HashSet<>();
		Boolean not_contained=false;
		while((line=br.readLine())!=null) {
			String[] elements=line.split(" ");
			if(!drugs.contains(elements[0])) {
				not_contained=true;
			}
			if(!targets.contains(elements[1])) {
				not_contained=true;
			}
			if(elements[2].equals("true")) {
				positive.add(line);
			}
			if(elements[2].equals("false")) {
				negative.add(line);
			}
		}
		
		if(not_contained) {
			System.out.println(file +"\t not_contained");
			beWriter.write(file +"\t not_contained \n");
		}
		if(file.contains("test")){
			if((positive.size()==0||negative.size()==0)) {
				System.out.println(file +"\t test misses true and false pair");
				beWriter.write(file +"\t test misses true and false pair \n");
			}
		}
		
		
		if(file.contains("train")) {
			if((positive.size()==0||negative.size()>0)) {
				System.out.println(file +"\t train has wrong true and false pair");	
				beWriter.write(file +"\t train has wrong true and false pair \n");
			}
			
		}
		beWriter.flush();
	}
	
	
	public static Boolean check(String file, HashSet<String> drugs, HashSet<String> targets) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		String line=null;
		HashSet<String> positive=new HashSet<>();
		HashSet<String> negative=new HashSet<>();
		Boolean remove=false;
		while((line=br.readLine())!=null) {
			String[] elements=line.split(" ");
			if(!drugs.contains(elements[0])) {
				System.out.println("@@@ -> "+ elements[0]+" is not covered");
				remove=true;
			}
			if(!targets.contains(elements[1])) {
				System.out.println("@@@ -> "+ elements[1]+" is not covered");
				remove=true;
			}
			
			if(elements[2].equals("true")) {
				positive.add(line);
			}
			if(elements[2].equals("false")) {
				negative.add(line);
			}
		}
		br.close();
		
		if(file.contains("test")){
			if((positive.size()==0||negative.size()==0)) {
				System.out.println(file+ "@@@ test->  positive.size()==0||negative.size()==0 is not covered, "+positive.size()+" "+negative.size());
				remove=true;
			}
		}
		
		if(file.contains("train")) {
			if((positive.size()==0||negative.size()>0)) {
				System.out.println(file+ "@@@ train-> positive.size()==0||negative.size()>0 is not covered, "+positive.size()+" "+negative.size());
				remove=true;
			}
		}
		
		return remove;
	}
	
	
	public static HashSet<String> getTargets() throws IOException{
		HashSet<String> targetSet=new HashSet<>();
		BufferedReader bReader =new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/sequence.txt")));
		String lineString=null;
		while((lineString=bReader.readLine())!=null) {
			String[] elementStrings=lineString.split("\t");
			targetSet.add(elementStrings[0]);
		}
		return targetSet;
	}
	
	public static HashSet<String> getDrugs() throws IOException{
		HashSet<String> drugSet=new HashSet<>();
		BufferedReader bReader =new BufferedReader(new FileReader(new File(dataDir+"/output/datasets/orignial/smile.txt")));
		String lineString=null;
		while((lineString=bReader.readLine())!=null) {
			String[] elementStrings=lineString.split("\t");
			drugSet.add(elementStrings[0]);
		}
		return drugSet;
	}
	
}
