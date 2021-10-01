package benchmark.render;

import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.apache.jena.sparql.function.library.leviathan.cartesian;

import com.clearspring.analytics.stream.Counter;

import java_cup.internal_error;
import java_cup.parse_action;

public class Benchmark_stat {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		BufferedWriter bw=new BufferedWriter(new FileWriter(dataDir+"/output/datasets/experiment/benchmar.stat")); 
		
		bw.write("dir"+"\t"+"# tasks"+"\t"+"train_number"+"\t"
				+"train_true"+"\t"
				+"train_false"+"\t"
				+"train_drug"+"\t"
				+"train_target"+"\t"
				+"test_number"+"\t"
				+"test_true"+"\t"
				+"test_false"+"\t"
				+"test_drug"+"\t"
				+"test_target"+"\t"
				+"overlap_drug"+"\t"
				+"overlap_target"+"\n");
		
		getStat(dataDir+"/output/datasets/experiment/internal", 10, bw);
		getStat(dataDir+"/output/datasets/experiment/internal_general", 10, bw);
		
		getStat(dataDir+"/output/datasets/experiment/drugClass/drugbankCategory", 10, bw);
		getStat(dataDir+"/output/datasets/experiment/drugClass/linkplClass", 10, bw);
		getStat(dataDir+"/output/datasets/experiment/drugClass/linkplType", 10, bw);
		
		getStat(dataDir+"/output/datasets/experiment/targetClass/family", 10, bw);
		getStat(dataDir+"/output/datasets/experiment/targetClass/subFamily", 10, bw);
		getStat(dataDir+"/output/datasets/experiment/targetClass/proteinClass", 10, bw);
		
		getStat(dataDir+"/output/datasets/experiment/disease", 0, bw);
		getStat(dataDir+"/output/datasets/experiment/clinicalCT", 0, bw);
		bw.flush();
		bw.close();
	}
	
	
	
	public static void getStat(String dir, int K_folder, BufferedWriter bw) throws IOException {
		
		HashSet<String> set=new HashSet<>();
		
		int train_number_all=0;
		int train_true_number_all=0;
		int train_false_number_all=0;
		int train_drug_size_all=0;
		int train_target_size_all=0;
		
		int test_number_all=0;
		int test_true_number_all=0;
		int test_false_number_all=0;
		int test_drug_size_all=0;
		int test_target_size_all=0;

		int overlap_drug_size_all=0;
		int overlap_target_size_all=0;
		
		for(File file: new File(dir).listFiles()) {
			String file_name=file.getName();
			if(K_folder==0) {
				if(file_name.contains("train_")) {
					String genral_name=file_name.substring(6);	
					System.out.println(genral_name);
					set.add(genral_name);
				}
				if(file_name.contains("test_")) {
					String genral_name=file_name.substring(5);	
					System.out.println(genral_name);
					set.add(genral_name);
				}
			}else {
				if(!dir.contains("internal")) {
					if(file_name.contains("test_")) {
						String genral_name=file_name.substring(7);	
						System.err.println(file.getAbsolutePath()+" -> "+genral_name);
						set.add(genral_name);
					}	
				}
					
				if(file_name.contains("train_")) {
					String genral_name=file_name.substring(8);	
					System.err.println(file.getAbsolutePath()+" -> "+genral_name);
					set.add(genral_name);
				}
						
			}
		}
		
		int counter=0;
		for(String name:set) {
			if(K_folder==0) {
					counter++;
					String train_file=dir+"/"+"train_"+name;
					String test_file=dir+"/"+"test_"+name;	
					
					Benchmark_Bean train_bean=parse(train_file);
					Benchmark_Bean test_bean=parse(test_file);
					
					HashSet<String> train_drugs=train_bean.getDrugs();
					HashSet<String> train_targets=train_bean.getTargets();
					
					HashSet<String> test_drugs=test_bean.getDrugs();
					HashSet<String> test_targets=test_bean.getTargets();
					
					int train_number=train_bean.getPair_num();
					int train_true_number=train_bean.getTrue_num();
					int train_false_number=train_bean.getFalse_num();
					int train_drug_size=train_drugs.size();
					int train_target_size=train_targets.size();
					
					int test_number=test_bean.getPair_num();
					int test_true_number=test_bean.getTrue_num();
					int test_false_number=test_bean.getFalse_num();
					int test_drug_size=test_drugs.size();
					int test_target_size=test_targets.size();
					train_drugs.retainAll(test_drugs);
					train_targets.retainAll(test_targets);

					int overlap_drug_size=train_drugs.size();
					int overlap_target_size=train_targets.size();
					
					 train_number_all+=train_number;
					 train_true_number_all+=train_true_number;
					 train_false_number_all+=train_false_number;
					 train_drug_size_all+=train_drug_size;
					 train_target_size_all+=train_target_size;
					
					 test_number_all+=test_number;
					 test_true_number_all+=test_true_number;
					 test_false_number_all+=test_false_number;
					 test_drug_size_all+=test_drug_size;
					 test_target_size_all+=test_target_size;
					 overlap_drug_size_all+=overlap_drug_size;
					 overlap_target_size_all+=overlap_target_size;
			}else {
				for (int i = 0; i < K_folder; i++) {
					counter++;
					String train_file=dir+"/"+"train_"+i+"_"+name;
					String test_file=dir+"/"+"test_"+i+"_"+name;
					if(dir.contains("general")) {
						 test_file=dir+"/"+"test_"+i+"_general_isolateFree.nt";	
					}
					
					Benchmark_Bean train_bean=parse(train_file);
					Benchmark_Bean test_bean=parse(test_file);
					
					HashSet<String> train_drugs=train_bean.getDrugs();
					HashSet<String> train_targets=train_bean.getTargets();
					
					HashSet<String> test_drugs=test_bean.getDrugs();
					HashSet<String> test_targets=test_bean.getTargets();
					
					int train_number=train_bean.getPair_num();
					int train_true_number=train_bean.getTrue_num();
					int train_false_number=train_bean.getFalse_num();
					int train_drug_size=train_drugs.size();
					int train_target_size=train_targets.size();
					
					int test_number=test_bean.getPair_num();
					int test_true_number=test_bean.getTrue_num();
					int test_false_number=test_bean.getFalse_num();
					int test_drug_size=test_drugs.size();
					int test_target_size=test_targets.size();
					train_drugs.retainAll(test_drugs);
					train_targets.retainAll(test_targets);

					int overlap_drug_size=train_drugs.size();
					int overlap_target_size=train_targets.size();
					
					 train_number_all+=train_number;
					 train_true_number_all+=train_true_number;
					 train_false_number_all+=train_false_number;
					 train_drug_size_all+=train_drug_size;
					 train_target_size_all+=train_target_size;
					
					 test_number_all+=test_number;
					 test_true_number_all+=test_true_number;
					 test_false_number_all+=test_false_number;
					 test_drug_size_all+=test_drug_size;
					 test_target_size_all+=test_target_size;
					 overlap_drug_size_all+=overlap_drug_size;
					 overlap_target_size_all+=overlap_target_size;
				}
			}
			
		}
		
		System.out.println(dir+"\t"+counter+"\t"+(double)train_number_all/counter+"\t"
				+(double)train_true_number_all/counter+"\t"
				+(double)train_false_number_all/counter+"\t"
				+(double)train_drug_size_all/counter+"\t"
				+(double)train_target_size_all/counter+"\t"
				+(double)test_number_all/counter+"\t"
				+(double)test_true_number_all/counter+"\t"
				+(double)test_false_number_all/counter+"\t"
				+(double)test_drug_size_all/counter+"\t"
				+(double)test_target_size_all/counter+"\t"
				+(double)overlap_drug_size_all/counter+"\t"
				+(double)overlap_target_size_all/counter+"\n");
		bw.write(dir+"\t"+counter+"\t"+(double)train_number_all/counter+"\t"
				+(double)train_true_number_all/counter+"\t"
				+(double)train_false_number_all/counter+"\t"
				+(double)train_drug_size_all/counter+"\t"
				+(double)train_target_size_all/counter+"\t"
				+(double)test_number_all/counter+"\t"
				+(double)test_true_number_all/counter+"\t"
				+(double)test_false_number_all/counter+"\t"
				+(double)test_drug_size_all/counter+"\t"
				+(double)test_target_size_all/counter+"\t"
				+(double)overlap_drug_size_all/counter+"\t"
				+(double)overlap_target_size_all/counter+"\n");
		bw.flush();
	}
	
	public static Benchmark_Bean parse(String file) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		String line=null;
		int true_num=0;
		int false_num=0;
		int pair_num=0;
		HashSet<String> drugs=new HashSet<>();
		HashSet<String> targets=new HashSet<>();
		Benchmark_Bean bean=new Benchmark_Bean();
		while((line=br.readLine())!=null) {
			String[] elements=line.split(" ");
			drugs.add(elements[0]);
			targets.add(elements[1]);
			pair_num++;
			if(elements[2].equals("true")) {
				true_num++;
			}else if(elements[2].equals("false")){
				false_num++;
			}
		}
		bean.setDrugs(drugs);
		bean.setTargets(targets);
		bean.setPair_num(pair_num);
		bean.setFalse_num(false_num);
		bean.setTrue_num(true_num);
		br.close();
		return bean;
	}

}
