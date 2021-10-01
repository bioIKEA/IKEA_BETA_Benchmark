package benchmark.render.external;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java_cup.internal_error;

public class Test_clinicCT {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		XMLParse();
	}
	
	public static void XMLParse() throws ParserConfigurationException, SAXException, IOException {
		HashMap<String,Integer> counter =new HashMap<>();
		for(File dir:new File(dataDir+"/input/clinicCT/").listFiles()) {
			if(dir.isDirectory()) {
				for(File file:dir.listFiles()) {
					XMLParse(file.getAbsolutePath(), counter);
				}
			}
		}
		
		ArrayList<Map.Entry<String,Integer>> list=sortMap(counter);
		
		BufferedWriter bWriter =new BufferedWriter(new FileWriter(new File(dataDir+"/input/clinicCT_counter.tsv")));
		for (int i = 0; i < list.size(); i++) {
			bWriter.write(list.get(i).getKey()+"\t"+list.get(i).getValue()+"\n");
		}
		bWriter.flush();
		bWriter.close();
	}
	
	
	public static ArrayList<Map.Entry<String,Integer>>  sortMap( HashMap<String, Integer> map) {
		ArrayList<Map.Entry<String,Integer>> list_2 = new ArrayList<>(map.entrySet());
	       Collections.sort(list_2, new Comparator<Map.Entry<String,Integer>>() {
	           public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
	               return Double.valueOf(o2.getValue()).compareTo(Double.valueOf(o1.getValue()));//升序，前边加负号变为降序
	           }
	       });
	       
	     return list_2;
	}
	
	public static void XMLParse(String clinnicCT_file, HashMap<String,Integer> counter) throws ParserConfigurationException, SAXException, IOException{
		File fXmlFile = new File(clinnicCT_file);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("clinical_study");
		
		
		for (int i = 0; i < nList.getLength(); i++) {
			Node study = nList.item(i);
			NodeList study_list=study.getChildNodes();
			for (int j = 0; j < study_list.getLength(); j++) {
				
				Node sub_node = study_list.item(j);
				
				if(sub_node.getNodeName().equals("condition")) {
					String valueString= sub_node.getTextContent();
					System.out.println(clinnicCT_file+" -> "+valueString);
					if(counter.containsKey(valueString)) {
						int current=counter.get(valueString)+1;
						counter.remove(valueString);
						counter.put(valueString, current);
					}else {
						counter.put(valueString, 1);
					}
				}
			}
		}
 
	}
}
