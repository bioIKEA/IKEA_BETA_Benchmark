package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bytedeco.javacpp.FlyCapture2.StrobeControl;
import org.nd4j.linalg.api.iter.FirstAxisIterator;

import java_cup.internal_error;

public class Copy_data {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		cp("D:/data/drug-taget-network/Databases/data/release_4/input/done", "D:/data/drug-taget-network/Databases/data/github_data");
//		cp("D:/data/drug-taget-network/Databases/data/release_4/output", "D:/data/drug-taget-network/Databases/data/github_data/output");
		
		cp("D:/data/drug-taget-network/Databases/data/github_data_upload_replication/output", "data_sample/output");
	}
	
	
	public static void cp(String source, String target) throws IOException {
		
		for(File f: new File(source).listFiles()) {
			
			if(f.isDirectory()) {
				cp( source+"/"+f.getName(),  target+"/"+f.getName());
				
			}else{
				
				System.out.println(source +" --> "+ target+"/"+f.getName());
				new File(target).mkdirs();
				BufferedWriter bw=new BufferedWriter(new FileWriter(new File(target+"/"+f.getName())));
				BufferedReader br=new BufferedReader(new FileReader(new File(source+"/"+f.getName())));
				
				String line=null;
				int counter=0;
				while((line=br.readLine())!=null) {
					counter++;
					bw.write(line+"\n");
					if(counter>100) {
						break;
					}
				}
				bw.flush();
				bw.close();
			}
		
			
			
		}
	}
}
