package benchmark.render.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.netlib.util.booleanW;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.tukaani.xz.check.Check;

import benchmark.render.Benchmark_checker;
import java_cup.internal_error;

public class Internal_Testgenerator {
	public static String dataDir="data_sample";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

	}

	public static HashMap<String, HashSet<String>> getUniprotToDrugbank() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(new File(dataDir+"/input/done/drugbank.nq")));
		String line = null;
		HashMap<String, HashSet<String>> valueHashMap = new HashMap<>();
		while ((line = br.readLine()) != null) {
			if (!line.contains("\"")) {
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();

					String uniprotString = o.substring(o.lastIndexOf(":") + 1, o.lastIndexOf(">"));

					if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-uniprot>")) {
						if (s.startsWith("<http://bio2rdf.org/drugbank:be")) {
							if (valueHashMap.containsKey(uniprotString)) {
								valueHashMap.get(uniprotString).add(s);
							} else {
								HashSet<String> set = new HashSet<>();
								set.add(s);
								valueHashMap.put(uniprotString, set);
							}
						}
					}
				}
			}
		}
		return valueHashMap;
	}

	public static HashMap<String, HashSet<String>> getTripleFromObject() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/output/association_drugbank.nq")));
		String line = null;
		HashSet<String> drugs = getDrugs();
		HashSet<String> targets = getTargets();
		HashMap<String, HashSet<String>> valueHashMap = new HashMap<>();
		while ((line = br.readLine()) != null) {
			if (!line.contains("\"")) {
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();

					if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>") && drugs.contains(s)
							&& targets.contains(o)) {
						if (valueHashMap.containsKey(o)) {
							valueHashMap.get(o).add(line);
						} else {
							HashSet<String> set = new HashSet<>();
							set.add(line);
							valueHashMap.put(o, set);
						}
					}
				}
			}
		}
		return valueHashMap;
	}

	public static HashMap<String, HashSet<String>> convertToDrugBank(HashMap<String, HashSet<String>> x_uniprot,
			HashMap<String, HashSet<String>> uniprotToDrugbankHashMap,
			HashMap<String, HashSet<String>> objectToTripleHashMap) throws IOException {
		HashMap<String, HashSet<String>> map = new HashMap<>();
		HashSet<String> targets = getTargets();
		for (Entry<String, HashSet<String>> entry : x_uniprot.entrySet()) {
			HashSet<String> set = new HashSet<>();
			map.put(entry.getKey(), set);
			for (String uniprot : entry.getValue()) {
				if (uniprotToDrugbankHashMap.containsKey(uniprot)) {
					for (String drugbank_target : uniprotToDrugbankHashMap.get(uniprot)) {
						if (targets.contains(drugbank_target)) {
							if (objectToTripleHashMap.containsKey(drugbank_target)) {
								set.addAll(objectToTripleHashMap.get(drugbank_target));
							}
						}
					}
				}
			}
		}
		return map;
	}

	public static HashMap<String, HashSet<String>> convertToDrugBank_similars(
			HashMap<String, HashSet<String>> x_uniprot, HashMap<String, HashSet<String>> uniprotToDrugbankHashMap,
			HashMap<String, HashSet<String>> objectToTripleHashMap) throws IOException {
		HashMap<String, HashSet<String>> map = new HashMap<>();

		HashSet<String> targets = getTargets();

		for (Entry<String, HashSet<String>> entry : x_uniprot.entrySet()) {
			HashSet<String> set = new HashSet<>();
			map.put(entry.getKey(), set);
			for (String uniprot : entry.getValue()) {
				if (uniprotToDrugbankHashMap.containsKey(uniprot)) {
					HashSet<String> drugbank_targetSet = uniprotToDrugbankHashMap.get(uniprot);
					for (String drugbank_t : drugbank_targetSet) {
						if (targets.contains(drugbank_t)) {
							set.add(drugbank_t);
						}
					}
				}
			}
		}
		return map;
	}

	public static ArrayList<Map.Entry<String, HashSet<String>>> sortMap(HashMap<String, HashSet<String>> map,
			int topN) {
		ArrayList<Map.Entry<String, HashSet<String>>> list_2 = new ArrayList<>(map.entrySet());
		Collections.sort(list_2, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1, Map.Entry<String, HashSet<String>> o2) {
				return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));// 升序，前边加负号变为降序
			}
		});

		for (int i = 0; i < topN; i++) {
			System.out.println(list_2.get(i).getKey() + " -> " + list_2.get(i).getValue().size());
		}
		return list_2;
	}
	
	
	public static ArrayList<Map.Entry<String, HashSet<String>>> sortMap(HashMap<String, HashSet<String>> map) {
		ArrayList<Map.Entry<String, HashSet<String>>> list_2 = new ArrayList<>(map.entrySet());
		Collections.sort(list_2, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1, Map.Entry<String, HashSet<String>> o2) {
				return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));// 升序，前边加负号变为降序
			}
		});
		return list_2;
	}

	public static void generate_targetClass(HashSet<String> triples, String dir) throws IOException {

		HashMap<String, HashSet<String>> uniprotToDrugbankHashMap = getUniprotToDrugbank();
		HashMap<String, HashSet<String>> objectToTripleHashMap = getTripleFromObject();

		BufferedReader br = new BufferedReader(
				new FileReader(new File(dataDir+"/output/datasets/orignial/PTHR15.0_human")));
		String lineString = null;

		HashMap<String, HashSet<String>> pantherMolecular_uniprot = new HashMap<>();
		HashMap<String, HashSet<String>> pantherBiologicalProcess_uniprot = new HashMap<>();
		HashMap<String, HashSet<String>> pantherCellularComponents_uniprot = new HashMap<>();
		HashMap<String, HashSet<String>> pantherFamily_uniprot = new HashMap<>();
		HashMap<String, HashSet<String>> pantherSubFamily_uniprot = new HashMap<>();
		HashMap<String, HashSet<String>> pantherProteinClass_uniprot = new HashMap<>();

		while ((lineString = br.readLine()) != null) {
			String[] elementStrings = lineString.toLowerCase().split("\t");

			String Gene_Identifier = null;
			String PANTHER_SF_ID = null;

			String PANTHER_Family_Name = null;
			String PANTHER_Subfamily_Name = null;

			String PANTHER_Molecular_function = null;
			String PANTHER_Biological_process = null;
			String Cellular_components = null;

			String Protein_class = null;
			String Pathway = null;
			String gene_id = null;
			String pthr_familyid = null;
			String pthr_subfamilyid = null;

			if (elementStrings.length >= 1) {
				Gene_Identifier = elementStrings[0];
				gene_id = Gene_Identifier.substring(Gene_Identifier.lastIndexOf("=") + 1, Gene_Identifier.length())
						.trim();
			}

			if (elementStrings.length >= 3) {
				PANTHER_SF_ID = elementStrings[2];
				pthr_familyid = PANTHER_SF_ID.substring(0, PANTHER_SF_ID.lastIndexOf(":")).trim();
				pthr_subfamilyid = PANTHER_SF_ID.trim();
			}
			if (elementStrings.length >= 4) {
				PANTHER_Family_Name = elementStrings[3];
			}
			if (elementStrings.length >= 5) {
				PANTHER_Subfamily_Name = elementStrings[4];
			}
			if (elementStrings.length >= 6) {
				PANTHER_Molecular_function = elementStrings[5];
			}
			if (elementStrings.length >= 7) {
				PANTHER_Biological_process = elementStrings[6];
			}
			if (elementStrings.length >= 8) {
				Cellular_components = elementStrings[7];
			}
			if (elementStrings.length >= 9) {
				Protein_class = elementStrings[8];
			}
			if (elementStrings.length >= 10) {
				Pathway = elementStrings[9];
			}

			if (pthr_familyid != null) {
				if (pthr_familyid.length() > 1) {
					if (pantherFamily_uniprot.containsKey(pthr_familyid)) {
						pantherFamily_uniprot.get(pthr_familyid).add(gene_id);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(gene_id);
						pantherFamily_uniprot.put(pthr_familyid, set);
					}
				}
			}

			if (pthr_subfamilyid != null) {
				if (pthr_subfamilyid.length() > 1) {
					if (pantherSubFamily_uniprot.containsKey(pthr_subfamilyid)) {
						pantherSubFamily_uniprot.get(pthr_subfamilyid).add(gene_id);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(gene_id);
						pantherSubFamily_uniprot.put(pthr_subfamilyid, set);
					}
				}
			}

			if (PANTHER_Molecular_function != null) {
				for (String string : PANTHER_Molecular_function.split(";")) {
					String value = string.substring(string.lastIndexOf(":") + 1, string.length()).trim();
					if (value.length() > 1) {
						if (pantherMolecular_uniprot.containsKey(value)) {
							pantherMolecular_uniprot.get(value).add(gene_id);
						} else {
							HashSet<String> set = new HashSet<>();
							set.add(gene_id);
							pantherMolecular_uniprot.put(value, set);
						}
					}
				}
			}

			if (PANTHER_Biological_process != null) {
				for (String string : PANTHER_Biological_process.split(";")) {
					String value = string.substring(string.lastIndexOf(":") + 1, string.length()).trim();
					if (value.length() > 1) {
						if (pantherBiologicalProcess_uniprot.containsKey(value)) {
							pantherBiologicalProcess_uniprot.get(value).add(gene_id);
						} else {
							HashSet<String> set = new HashSet<>();
							set.add(gene_id);
							pantherBiologicalProcess_uniprot.put(value, set);
						}
					}
				}
			}

			if (Cellular_components != null) {
				for (String string : Cellular_components.split(";")) {
					String value = string.substring(string.lastIndexOf(":") + 1, string.length()).trim();
					if (value.length() > 1) {
						if (pantherCellularComponents_uniprot.containsKey(value)) {
							pantherCellularComponents_uniprot.get(value).add(gene_id);
						} else {
							HashSet<String> set = new HashSet<>();
							set.add(gene_id);
							pantherCellularComponents_uniprot.put(value, set);
						}
					}
				}
			}

			if (Protein_class != null) {
				if (Protein_class.length() > 1) {
					for (String string : Protein_class.split(";")) {
						String value = string.substring(string.lastIndexOf("#") + 1, string.length()).trim();
						if (value.length() > 1) {
							if (pantherProteinClass_uniprot.containsKey(value)) {
								pantherProteinClass_uniprot.get(value).add(gene_id);
							} else {
								HashSet<String> set = new HashSet<>();
								set.add(gene_id);
								pantherProteinClass_uniprot.put(value, set);
							}
						}
					}
				}
			}
		}
		br.close();

		HashMap<String, HashSet<String>> pantherMolecular_map = convertToDrugBank(pantherMolecular_uniprot,
				uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherBiologicalProcess_map = convertToDrugBank(
				pantherBiologicalProcess_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherCellularComponents_map = convertToDrugBank(
				pantherCellularComponents_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherFamily_map = convertToDrugBank(pantherFamily_uniprot,
				uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherSubFamily_map = convertToDrugBank(pantherSubFamily_uniprot,
				uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherProteinClass_map = convertToDrugBank(pantherProteinClass_uniprot,
				uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherMolecular_map_similar = convertToDrugBank_similars(
				pantherMolecular_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherBiologicalProcess_map_similar = convertToDrugBank_similars(
				pantherBiologicalProcess_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherCellularComponents_map_similar = convertToDrugBank_similars(
				pantherCellularComponents_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherFamily_map_similar = convertToDrugBank_similars(pantherFamily_uniprot,
				uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherSubFamily_map_similar = convertToDrugBank_similars(
				pantherSubFamily_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		HashMap<String, HashSet<String>> pantherProteinClass_map_similar = convertToDrugBank_similars(
				pantherProteinClass_uniprot, uniprotToDrugbankHashMap, objectToTripleHashMap);

		System.out.println("pantherMolecular_map: " + pantherMolecular_map.size());
		System.out.println("pantherBiologicalProcess_map: " + pantherBiologicalProcess_map.size());
		System.out.println("pantherCellularComponents_map: " + pantherCellularComponents_map.size());
		System.out.println("pantherFamily_map: " + pantherFamily_map.size());
		System.out.println("pantherSubFamily_map: " + pantherSubFamily_map.size());
		System.out.println("pantherProteinClass_map: " + pantherProteinClass_map.size());

		ArrayList<Map.Entry<String, HashSet<String>>> pantherMolecular_list = sortMap(pantherMolecular_map, 10);
		ArrayList<Map.Entry<String, HashSet<String>>> pantherBiologicalProcess_list = sortMap(
				pantherBiologicalProcess_map, 10);
		ArrayList<Map.Entry<String, HashSet<String>>> pantherCellularComponents_list = sortMap(
				pantherCellularComponents_map, 10);
		ArrayList<Map.Entry<String, HashSet<String>>> pantherFamily_list = sortMap(pantherFamily_map);
		ArrayList<Map.Entry<String, HashSet<String>>> pantherSubFamily_list = sortMap(pantherSubFamily_map);
		ArrayList<Map.Entry<String, HashSet<String>>> pantherProteinClass_list = sortMap(pantherProteinClass_map);

		new File(dir + "/family").mkdirs();
		new File(dir + "/subFamily").mkdirs();
		new File(dir + "/proteinClass").mkdirs();

		writeDataToFolders_targetClass(10, dir + "/family", triples, pantherFamily_map_similar, pantherFamily_list, 5);

		writeDataToFolders_targetClass(10, dir + "/subFamily", triples, pantherSubFamily_map_similar,
				pantherSubFamily_list, 5);

		writeDataToFolders_targetClass(10, dir + "/proteinClass", triples, pantherProteinClass_map_similar,
				pantherProteinClass_list, 5);
	}

	public static void generate_drugClass(HashSet<String> triples, String dir) throws IOException {
		HashMap<String, HashSet<String>> drugbankArrayList = readDrugBankClass(); // class, drugbank drugs

		HashMap<String, HashSet<String>> linkpl_typeArrayList = readLinkplClass(
				"<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-type>");
		HashMap<String, HashSet<String>> linkpl_classArrayList = readLinkplClass(
				"<http://dbmi-icode-01.dbmi.pitt.edu/linkedSPLs/vocab/resource/drug-pharmacologic_class>");

		ArrayList<Map.Entry<String, HashSet<String>>> drugbank_positives = generatePositiveAssociation(triples,
				drugbankArrayList); // class, drugbank triples
		ArrayList<Map.Entry<String, HashSet<String>>> linkpl_type_positives = generatePositiveAssociation(triples,
				linkpl_typeArrayList);
		ArrayList<Map.Entry<String, HashSet<String>>> linkpl_class_positives = generatePositiveAssociation(triples,
				linkpl_classArrayList);

		new File(dir + "/drugbankCategory").mkdirs();
		new File(dir + "/linkplType").mkdirs();
		new File(dir + "/linkplClass").mkdirs();

		writeDataToFolders_drugClass(10, dir + "/drugbankCategory", triples, drugbankArrayList, drugbank_positives, 5);

		writeDataToFolders_drugClass(10, dir + "/linkplType", triples, linkpl_typeArrayList, linkpl_type_positives, 5);

		writeDataToFolders_drugClass(10, dir + "/linkplClass", triples, linkpl_classArrayList, linkpl_class_positives,
				5);

//		for (int i = 0; i < 10; i++) {
//			System.out.println("drugbankArrayList: "+drugbankArrayList.get(drugbank_positives.get(i).getKey()).size()+" --> "+drugbank_positives.get(i).getValue().size());
//		}
//		for (int i = 0; i < 10; i++) {
//			System.out.println("linkpl_typeArrayList: "+linkpl_typeArrayList.get(linkpl_type_positives.get(i).getKey()).size()+" --> "+linkpl_type_positives.get(i).getValue().size());
//		}
//		for (int i = 0; i < 10; i++) {
//			System.out.println("linkpl_classArrayList: "+linkpl_classArrayList.get(linkpl_class_positives.get(i).getKey()).size()+" --> "+linkpl_class_positives.get(i).getValue().size());
//		}

	}

	public static void writeDataToFolders_targetClass(int foldernumber, String outDir, HashSet<String> triples,
			HashMap<String, HashSet<String>> class_drugbankdrugs,
			ArrayList<Map.Entry<String, HashSet<String>>> drugbank_positives, int topK) throws IOException {
		HashSet<String> nameSet=new HashSet<>();
				
		for (int top = 0; top < drugbank_positives.size(); top++) {
			if(nameSet.size()>topK-1) {
				break;
			}
			boolean repeat = false;
			String class_name = drugbank_positives.get(top).getKey();
			
			HashSet<String> similar_targets = class_drugbankdrugs.get(drugbank_positives.get(top).getKey());
			HashSet<String> positive_triples = drugbank_positives.get(top).getValue();

			HashSet<String> triples_1 = new HashSet<>();

			for (String triple : triples) {
				if (!positive_triples.contains(triple)) {
					triples_1.add(triple);
				}
			}

			ArrayList<String> list = new ArrayList<>();
			for (String string : positive_triples) {
				list.add(string);
			}

			Collections.shuffle(list, new Random(1024));

			int listCount = foldernumber;// 拆分数量
			int[] arr = new int[listCount];
			int avg = positive_triples.size() / listCount;// 平均数
			int addIndex = positive_triples.size() - avg * listCount;// 需要增加1个数量的最大下标
			for (int i = 0; i < listCount; ++i) {
				arr[i] = i < addIndex ? avg + 1 : avg;
			}
			for (int i = 0; i < arr.length; i++) {
				if (!repeat) {
					int start;
					System.out.println(outDir + "/multi_self_" + i + "_data.nt");
					if (i < addIndex) {
						start = i * (avg + 1);
					} else {
						start = (avg + 1) * addIndex + (i - addIndex) * avg;
					}
					HashSet<Integer> folder = new HashSet<>();
					for (int j = start; j < start + arr[i]; j++) {
						folder.add(j);
					}

					HashSet<String> positive_train = new HashSet<>();
					HashSet<String> positive_test = new HashSet<>();

					for (int j = 0; j < list.size(); j++) {
						if (folder.contains(j)) {
							positive_test.add(list.get(j));
						} else {
							positive_train.add(list.get(j));
						}
					}
					for (String triple : triples_1) {
						positive_train.add(triple);
					}

					repeat = generateNegative_targetClass(class_name, similar_targets, positive_train,
							positive_test, 1.0, outDir, i, topK);
				}
			}
			
			if(!repeat) {
				nameSet.add(class_name);
			}else {
				System.out.println("@@@@ ==> "+outDir + " / "+ class_name +" is removed as it is not qualified ...");
			}
		}
	}

	public static void writeDataToFolders_drugClass(int foldernumber, String outDir, HashSet<String> triples,
			HashMap<String, HashSet<String>> class_drugbankdrugs,
			ArrayList<Map.Entry<String, HashSet<String>>> drugbank_positives, int topK) throws IOException {

		for (int top = 0; top < topK; top++) {
			String class_name = drugbank_positives.get(top).getKey();
			HashSet<String> similar_drugs = class_drugbankdrugs.get(drugbank_positives.get(top).getKey());
			HashSet<String> positive_triples = drugbank_positives.get(top).getValue();

			HashSet<String> triples_1 = new HashSet<>();

			for (String triple : triples) {
				if (!positive_triples.contains(triple)) {
					triples_1.add(triple);
				}
			}

			ArrayList<String> list = new ArrayList<>();
			for (String string : positive_triples) {
				list.add(string);
			}

			Collections.shuffle(list, new Random(1024));

			int listCount = foldernumber;// 拆分数量
			int[] arr = new int[listCount];
			int avg = positive_triples.size() / listCount;// 平均数
			int addIndex = positive_triples.size() - avg * listCount;// 需要增加1个数量的最大下标
			for (int i = 0; i < listCount; ++i) {
				arr[i] = i < addIndex ? avg + 1 : avg;
			}
			for (int i = 0; i < arr.length; i++) {
				int start;
				System.out.println(outDir + "/multi_self_" + i + "_data.nt");
				if (i < addIndex) {
					start = i * (avg + 1);
				} else {
					start = (avg + 1) * addIndex + (i - addIndex) * avg;
				}
				HashSet<Integer> folder = new HashSet<>();
				for (int j = start; j < start + arr[i]; j++) {
					folder.add(j);
				}

				HashSet<String> positive_train = new HashSet<>();
				HashSet<String> positive_test = new HashSet<>();

				for (int j = 0; j < list.size(); j++) {
					if (folder.contains(j)) {
						positive_test.add(list.get(j));
					} else {
						positive_train.add(list.get(j));
					}
				}
				for (String triple : triples_1) {
					positive_train.add(triple);
				}
				System.out.println("class_name: " + class_name);
				System.out.println("similar_drugs: " + similar_drugs.size());
				System.out.println("positive_train: " + positive_train.size());
				System.out.println("positive_test: " + positive_test.size());
//				Check (positive_train,"positive_train");
//				Check (positive_test,"positive_test");
				generateNegative_drugClass(class_name, similar_drugs, positive_train, positive_test, 1.0, outDir, i);

			}
		}
	}

	public static void Check(HashSet<String> triples, String name) throws IOException {
		HashSet<String> targets = getTargets();
		HashSet<String> drugs = getDrugs();

		for (String line : triples) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")
						&& s.startsWith("<http://bio2rdf.org/drugbank:db")
						&& o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if (!drugs.contains(s) || !targets.contains(o)) {
						System.err.println(name + " check ==========> " + line);
					}
				}
			}
		}
	}

	public static Boolean generateNegative_targetClass(String class_name, HashSet<String> similar_targets,
			HashSet<String> positive_train, HashSet<String> positive_test, Double negative_ratio, String outDir, int i, int topK)
			throws IOException {

		NegativeGenerator_targetClass g = new NegativeGenerator_targetClass(positive_train, positive_test);
		NegativeSetBean bean_tt = g.generate(1024, negative_ratio, "tt", similar_targets, positive_train,
				positive_test);
		NegativeSetBean bean_trt = g.generate(1024, negative_ratio, "trt", similar_targets, positive_train,
				positive_test);
		NegativeSetBean bean_te = g.generate(1024, negative_ratio, "te", similar_targets, positive_train,
				positive_test);
		NegativeSetBean bean_tre = g.generate(1024, negative_ratio, "tre", similar_targets, positive_train,
				positive_test);

		if (class_name.contains(":")) {
			class_name = class_name.replaceAll(":", "_");
		}

		writeToFile(outDir, i, class_name + "_tt", positive_train, positive_test, bean_tt.getCandidateSet());
		writeToFile(outDir, i, class_name + "_trt", positive_train, positive_test, bean_trt.getCandidateSet());
		writeToFile(outDir, i, class_name + "_te", positive_train, positive_test, bean_te.getCandidateSet());
		writeToFile(outDir, i, class_name + "_tre", positive_train, positive_test, bean_tre.getCandidateSet());

		String type_tt = class_name + "_tt";
		String type_trt = class_name + "_trt";
		String type_te = class_name + "_te";
		String type_tre = class_name + "_tre";

		HashSet<String> drugs = getDrugs();
		HashSet<String> targets = getTargets();
		String file1 = outDir + "/train_" + i + "_" + type_tt + ".nt";
		String file2 = outDir + "/test_" + i + "_" + type_tt + ".nt";
		String file3 = outDir + "/train_" + i + "_" + type_trt + ".nt";
		String file4 = outDir + "/test_" + i + "_" + type_trt + ".nt";
		String file5 = outDir + "/train_" + i + "_" + type_te + ".nt";
		String file6 = outDir + "/test_" + i + "_" + type_te + ".nt";
		String file7 = outDir + "/train_" + i + "_" + type_tre + ".nt";
		String file8 = outDir + "/test_" + i + "_" + type_tre + ".nt";

		Boolean check_tt_train = Benchmark_checker.check(file1, drugs, targets);
		Boolean check_tt_test = Benchmark_checker.check(file2, drugs, targets);

		Boolean check_trt_train = Benchmark_checker.check(file3, drugs, targets);
		Boolean check_trt_test = Benchmark_checker.check(file4, drugs, targets);

		Boolean check_te_train = Benchmark_checker.check(file5, drugs, targets);
		Boolean check_te_test = Benchmark_checker.check(file6, drugs, targets);

		Boolean check_tre_train = Benchmark_checker.check(file7, drugs, targets);
		Boolean check_tre_test = Benchmark_checker.check(file8, drugs, targets);

		boolean repeat = false;
		if (check_tt_train || check_tt_test || check_trt_train || check_trt_test || check_te_train || check_te_test
				|| check_tre_train || check_tre_test) {
			
			for (int j = 0; j < topK; j++) {
				String file_tmp_1 = outDir + "/train_" + j + "_" + type_tt + ".nt";
				String file_tmp_2 = outDir + "/test_" + j + "_" + type_tt + ".nt";
				String file_tmp_3 = outDir + "/train_" + j + "_" + type_trt + ".nt";
				String file_tmp_4 = outDir + "/test_" + j + "_" + type_trt + ".nt";
				String file_tmp_5 = outDir + "/train_" + j + "_" + type_te + ".nt";
				String file_tmp_6 = outDir + "/test_" + j + "_" + type_te + ".nt";
				String file_tmp_7 = outDir + "/train_" + j + "_" + type_tre + ".nt";
				String file_tmp_8 = outDir + "/test_" + j + "_" + type_tre + ".nt";
				
				new File(file_tmp_1).deleteOnExit();
				new File(file_tmp_2).deleteOnExit();
				new File(file_tmp_3).deleteOnExit();
				new File(file_tmp_4).deleteOnExit();
				new File(file_tmp_5).deleteOnExit();
				new File(file_tmp_6).deleteOnExit();
				new File(file_tmp_7).deleteOnExit();
				new File(file_tmp_8).deleteOnExit();	
			}
			
			repeat = true;
		}
		System.err.println(repeat+" "+check_tt_train +" "+ check_tt_test +" "+ check_trt_train +" "+ check_trt_test +" "+
		check_te_train +" "+check_te_test +" "+check_tre_train +" "+ check_tre_test);
		return repeat;
	}

	public static void generateNegative_drugClass(String class_name, HashSet<String> similar_drugs,
			HashSet<String> positive_train, HashSet<String> positive_test, Double negative_ratio, String outDir, int i)
			throws IOException {
		NegativeGenerator_drugClass g = new NegativeGenerator_drugClass(positive_train, positive_test);

		System.out.println("similar_drugs: " + similar_drugs.size());
		NegativeSetBean bean_tt = g.generate(1024, negative_ratio, "tt", similar_drugs, positive_train, positive_test);
		NegativeSetBean bean_trt = g.generate(1024, negative_ratio, "trt", similar_drugs, positive_train,
				positive_test);
		NegativeSetBean bean_te = g.generate(1024, negative_ratio, "te", similar_drugs, positive_train, positive_test);
		NegativeSetBean bean_tre = g.generate(1024, negative_ratio, "tre", similar_drugs, positive_train,
				positive_test);

		String name = "";

		if (class_name.startsWith("<http://bio2rdf.org/drugbank_vocabulary:")) {
			name = class_name.substring(class_name.lastIndexOf(":") + 1, class_name.lastIndexOf(">"));
		}

		if (class_name.startsWith("<http://purl.bioontology.org/ontology/rxnorm/")) {
			name = class_name.substring(class_name.lastIndexOf("/") + 1, class_name.lastIndexOf(">"));
		}

		if (class_name.startsWith("<http://purl.bioontology.org/ontology/ndfrt/")) {
			name = class_name.substring(class_name.lastIndexOf("/") + 1, class_name.lastIndexOf(">"));
		}

		writeToFile(outDir, i, name + "_tt", positive_train, positive_test, bean_tt.getCandidateSet());
		writeToFile(outDir, i, name + "_trt", positive_train, positive_test, bean_trt.getCandidateSet());
		writeToFile(outDir, i, name + "_te", positive_train, positive_test, bean_te.getCandidateSet());
		writeToFile(outDir, i, name + "_tre", positive_train, positive_test, bean_tre.getCandidateSet());
	}

	public static ArrayList<Map.Entry<String, HashSet<String>>> generatePositiveAssociation(HashSet<String> triples,
			HashMap<String, HashSet<String>> class_drugs) throws IOException {

		HashMap<String, HashSet<String>> candidate_map = new HashMap<>();

		for (String triple : triples) {
			InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					if (candidate_map.containsKey(s)) {
						candidate_map.get(s).add(triple);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(triple);
						candidate_map.put(s, set);
					}
				}
			}
		}

		HashMap<String, HashSet<String>> map = new HashMap<>();

		for (Entry<String, HashSet<String>> entry : class_drugs.entrySet()) {
			HashSet<String> set = new HashSet<>();
			for (String drug : entry.getValue()) {
				if (candidate_map.containsKey(drug)) {
					for (String triple : candidate_map.get(drug)) {
						set.add(triple);
					}
				}
			}
			map.put(entry.getKey(), set);
		}

		ArrayList<Map.Entry<String, HashSet<String>>> list_1 = new ArrayList<>(map.entrySet());
		Collections.sort(list_1, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1, Map.Entry<String, HashSet<String>> o2) {
				return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));// 升序，前边加负号变为降序
			}
		});

		return list_1;
	}

	public static HashMap<String, HashSet<String>> readDrugBankClass() throws IOException {
		BufferedReader br = new BufferedReader(
				new FileReader(new File(dataDir+"/input/done/drugbank.nq")));
		HashSet<String> drugs = getDrugs();
		String line = null;
		HashMap<String, HashSet<String>> valueHashMap = new HashMap<>();
		while ((line = br.readLine()) != null) {
			if (!line.contains("\"")) {
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim().toLowerCase();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim().toLowerCase();

					if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:category>") && drugs.contains(s)) {
						if (valueHashMap.containsKey(o)) {
							valueHashMap.get(o).add(s);
						} else {
							HashSet<String> set = new HashSet<>();
							set.add(s);
							valueHashMap.put(o, set);
						}
					}
				}
			}
		}
		System.out.println("size： " + valueHashMap.size());
		int all_number = 0;
		HashMap<String, Integer> drugbankClass_counter = new HashMap<>();
		for (Entry<String, HashSet<String>> entry : valueHashMap.entrySet()) {
			all_number += entry.getValue().size();
			drugbankClass_counter.put(entry.getKey(), entry.getValue().size());
		}
		System.out.println("average： " + (double) all_number / valueHashMap.size());

		ArrayList<Map.Entry<String, HashSet<String>>> list_2 = new ArrayList<>(valueHashMap.entrySet());
		Collections.sort(list_2, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1, Map.Entry<String, HashSet<String>> o2) {
				return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));// 升序，前边加负号变为降序
			}
		});

		return valueHashMap;
	}

	public static HashMap<String, HashSet<String>> readLinkplClass(String perperty) throws IOException {
		HashSet<String> drugs = getDrugs();
		HashMap<String, HashSet<String>> linkplToDrugbank = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(
				dataDir+"/output/drug_linkspl_drugbank.nq")));
		String line = null;
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://www.w3.org/2002/07/owl#sameAs>")) {
					if (o.startsWith("<http://bio2rdf.org/drugbank:db")) {
						if (linkplToDrugbank.containsKey(s)) {
							linkplToDrugbank.get(s).add(o);
						} else {
							HashSet<String> set = new HashSet<>();
							set.add(o);
							linkplToDrugbank.put(s, set);
						}
					}
				}
			}
		}
		System.out.println("linkplToDrugbank: " + linkplToDrugbank.size());
		HashMap<String, HashSet<String>> drugbankClass_1 = new HashMap<>();
		br = new BufferedReader(new FileReader(new File(
				dataDir+"/output/association_linkspl.nq")));
		line = null;
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals(perperty)) {
					if (linkplToDrugbank.containsKey(s)) {
						for (String drug : linkplToDrugbank.get(s)) {
							if (drugs.contains(drug)) {
								if (drugbankClass_1.containsKey(o)) {
									drugbankClass_1.get(o).add(drug);
								} else {
									HashSet<String> set = new HashSet<>();
									set.add(drug);
									drugbankClass_1.put(o, set);
								}
							}
						}
					}
				}
			}
		}

		System.out.println("drugbankClass_1 size: " + drugbankClass_1.size());

		int all_number_1 = 0;

		HashMap<String, Integer> drugbankClass_1_counter = new HashMap<>();

		for (Entry<String, HashSet<String>> entry : drugbankClass_1.entrySet()) {
			all_number_1 += entry.getValue().size();
			drugbankClass_1_counter.put(entry.getKey(), entry.getValue().size());
		}
		System.out.println("average drugbankClass_1： " + (double) all_number_1 / drugbankClass_1.size());

		ArrayList<Map.Entry<String, HashSet<String>>> list_1 = new ArrayList<>(drugbankClass_1.entrySet());
		Collections.sort(list_1, new Comparator<Map.Entry<String, HashSet<String>>>() {
			public int compare(Map.Entry<String, HashSet<String>> o1, Map.Entry<String, HashSet<String>> o2) {
				return Double.valueOf(o2.getValue().size()).compareTo(Double.valueOf(o1.getValue().size()));// 升序，前边加负号变为降序
			}
		});

		return drugbankClass_1;
	}

	public static void generate_general(HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation_general(triples);

		System.out.println(" data selected ..."+ positiveSet.size()+" / "+ triples.size());

		writeDataToFolders_general(10, triples, dir, positiveSet);

	}
	
	public static void generate_clinicalCT_general(String annotation_file, HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation_general(triples);

		System.out.println(" data selected ...");
		
		writeData_general(annotation_file, triples, dir, positiveSet);

	}
	
	
	public static void generate_clinicalCT_nonisolate(String annotation_file, HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation(triples);

		System.out.println(" data selected ...");

		writeDataToFolders_clinicalCT(annotation_file, triples, dir, positiveSet);

	}

	public static void generate_clinicalCT_semiisolate(String annotation_file, HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation_semiisolated(triples);

		System.out.println(" data selected ...");

		writeDataToFolders_semiisolate_clinicalCT(annotation_file, triples, dir, positiveSet);

	}

	public static void generate_clinicalCT_allisolate(String annotation_file, HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation_allisolated(triples);

		System.out.println(" data selected ...");

		writeDataToFolders_allisolate_clinicalCT(annotation_file, triples, dir, positiveSet);

	}
	
	

	public static void generate_nonisolate(HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation(triples);

		System.out.println(" data selected ...");

		writeDataToFolders(10, triples, dir, positiveSet);

	}

	public static void generate_semiisolate(HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation_semiisolated(triples);

		System.out.println(" data selected ...");

		writeDataToFolders_semiisolate(10, triples, dir, positiveSet);

	}

	public static void generate_allisolate(HashSet<String> triples, String dir) throws IOException {

		HashSet<String> positiveSet = generateConnectedAssociation_allisolated(triples);

		System.out.println(" data selected ...");

		writeDataToFolders_allisolate(10, triples, dir, positiveSet);

	}

	public static HashSet<String> generateConnectedAssociation_allisolated(HashSet<String> triples) throws IOException {

		HashMap<String, HashSet<String>> drugTarget = new HashMap<>();
		HashMap<String, HashSet<String>> targetDrug = new HashMap<>();
		HashSet<String> candidates = new HashSet<>();
		for (String triple : triples) {
			InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					candidates.add(triple);
					if (drugTarget.containsKey(s)) {
						drugTarget.get(s).add(o);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(o);
						drugTarget.put(s, set);
					}

					if (targetDrug.containsKey(o)) {
						targetDrug.get(o).add(s);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(s);
						targetDrug.put(o, set);
					}
				}
			}
		}

		HashSet<String> slected = new HashSet<>();

		for (Entry<String, HashSet<String>> entry : drugTarget.entrySet()) {
			if (entry.getValue().size() == 1) {
				for (String target : entry.getValue()) {
					if (targetDrug.get(target).size() == 1) {
						if (!slected.contains(entry.getKey() + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> "
								+ target + " .")) {
							slected.add(entry.getKey() + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> "
									+ target + " .");
						}
					}
				}
			}
		}

		for (Entry<String, HashSet<String>> entry : targetDrug.entrySet()) {
			if (entry.getValue().size() == 1) {
				for (String drug : entry.getValue()) {
					if (drugTarget.get(drug).size() == 1) {
						if (!slected.contains(
								drug + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + entry.getKey())) {
							slected.add(drug + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + entry.getKey()
									+ " .");
						}
					}
				}
			}
		}

		System.out.println("select all size: " + candidates.size() + " of " + slected.size()
				+ " as the positive for training and testing ");
		return slected;
	}

	public static HashSet<String> generateConnectedAssociation_semiisolated(HashSet<String> triples)
			throws IOException {

		HashMap<String, HashSet<String>> drugTarget = new HashMap<>();
		HashMap<String, HashSet<String>> targetDrug = new HashMap<>();
		HashSet<String> candidates = new HashSet<>();
		for (String triple : triples) {
			InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					candidates.add(triple);
					if (drugTarget.containsKey(s)) {
						drugTarget.get(s).add(o);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(o);
						drugTarget.put(s, set);
					}

					if (targetDrug.containsKey(o)) {
						targetDrug.get(o).add(s);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(s);
						targetDrug.put(o, set);
					}
				}
			}
		}

		HashSet<String> slected = new HashSet<>();
		ArrayList<String> selectPool = new ArrayList<>();

		for (Entry<String, HashSet<String>> entry : drugTarget.entrySet()) {
			if (entry.getValue().size() == 1) {
				for (String target : entry.getValue()) {
					if (targetDrug.get(target).size() > 1) {
						if (!selectPool.contains(entry.getKey() + " " + target)) {
							selectPool.add(entry.getKey() + " " + target);
						}
					}
				}
			}
		}

		for (Entry<String, HashSet<String>> entry : targetDrug.entrySet()) {
			if (entry.getValue().size() == 1) {
				for (String drug : entry.getValue()) {
					if (drugTarget.get(drug).size() > 1) {
						if (!selectPool.contains(drug + " " + entry.getKey())) {
							selectPool.add(drug + " " + entry.getKey());
						}
					}
				}
			}
		}

		getOneRandom_semiisolate(drugTarget, targetDrug, selectPool, slected);
		System.out.println("select all size: " + candidates.size() + " of " + slected.size()
				+ " as the positive for training and testing ");
		return slected;
	}

	public static HashSet<String> generateConnectedAssociation_general(HashSet<String> triples) throws IOException {
		HashSet<String> slected = new HashSet<>();
		for (String triple : triples) {
			InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					slected.add(s + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + o + " .");
				}
			}
		}
		return slected;
	}

	public static HashSet<String> generateConnectedAssociation(HashSet<String> triples) throws IOException {

		HashMap<String, HashSet<String>> drugTarget = new HashMap<>();
		HashMap<String, HashSet<String>> targetDrug = new HashMap<>();
		HashSet<String> candidates = new HashSet<>();
		for (String triple : triples) {
			InputStream inputStream = new ByteArrayInputStream(triple.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					candidates.add(triple);
					if (drugTarget.containsKey(s)) {
						drugTarget.get(s).add(o);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(o);
						drugTarget.put(s, set);
					}

					if (targetDrug.containsKey(o)) {
						targetDrug.get(o).add(s);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(s);
						targetDrug.put(o, set);
					}
				}
			}
		}

		HashSet<String> slected = new HashSet<>();
		ArrayList<String> selectPool = new ArrayList<>();

		for (Entry<String, HashSet<String>> entry : drugTarget.entrySet()) {
			if (entry.getValue().size() > 1) {
				for (String target : entry.getValue()) {
					if (targetDrug.get(target).size() > 1) {
						if (!selectPool.contains(entry.getKey() + " " + target)) {
							selectPool.add(entry.getKey() + " " + target);
						}
					}
				}
			}
		}

		getOneRandom(drugTarget, targetDrug, selectPool, slected);
		System.out.println("select all size: " + candidates.size() + " of " + slected.size()
				+ " as the positive for training and testing ");
		return slected;
	}

	public static void getOneRandom_semiisolate(HashMap<String, HashSet<String>> drugTarget,
			HashMap<String, HashSet<String>> targetDrug, ArrayList<String> selectPool, HashSet<String> slected) {
		long seed = 1024;
		while (selectPool.size() > 0) {
			Random random = new Random(seed);
			int radomNumber = random.nextInt(selectPool.size());
			String triple = selectPool.get(radomNumber);
			String[] element = triple.split(" ");
			String s = element[0].toString().trim();
			String o = element[1].toString().trim();
			drugTarget.get(s).remove(o);
			targetDrug.get(o).remove(s);
			slected.add(s + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + o + " .");

			HashSet<String> tmp = new HashSet<>();

			for (Entry<String, HashSet<String>> entry : drugTarget.entrySet()) {
				if (entry.getValue().size() == 1) {
					for (String target : entry.getValue()) {
						if (targetDrug.get(target).size() > 1) {
							if (!tmp.contains(entry.getKey() + " " + target)) {
								tmp.add(entry.getKey() + " " + target);
							}
						}
					}
				}
			}

			for (Entry<String, HashSet<String>> entry : targetDrug.entrySet()) {
				if (entry.getValue().size() == 1) {
					for (String drug : entry.getValue()) {
						if (drugTarget.get(drug).size() > 1) {
							if (!tmp.contains(drug + " " + entry.getKey())) {
								tmp.add(drug + " " + entry.getKey());
							}
						}
					}
				}
			}
			selectPool = new ArrayList<>();
			selectPool.addAll(tmp);

//		System.out.println(" candidate size: " + selectPool.size() + " slected size: " + slected.size());
		}
	}

	public static void getOneRandom(HashMap<String, HashSet<String>> drugTarget,
			HashMap<String, HashSet<String>> targetDrug, ArrayList<String> selectPool, HashSet<String> slected) {
		long seed = 1024;
		while (selectPool.size() > 0) {
			Random random = new Random(seed);
			int radomNumber = random.nextInt(selectPool.size());
			String triple = selectPool.get(radomNumber);
			String[] element = triple.split(" ");
			String s = element[0].toString().trim();
			String o = element[1].toString().trim();
			drugTarget.get(s).remove(o);
			targetDrug.get(o).remove(s);
			slected.add(s + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + o + " .");

			HashSet<String> tmp = new HashSet<>();

			for (Entry<String, HashSet<String>> entry : drugTarget.entrySet()) {
				if (entry.getValue().size() > 1) {
					for (String target : entry.getValue()) {
						if (targetDrug.get(target).size() > 1) {
							tmp.add(entry.getKey() + " " + target);
						}
					}
				}
			}
			selectPool = new ArrayList<>();
			selectPool.addAll(tmp);

//		System.out.println(" candidate size: " + selectPool.size() + " slected size: " + slected.size());
		}
	}

	public static void writeDataToFolders_general(int foldernumber, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashSet<String> triples_1 = new HashSet<>();
		for (String triple : triples) {
			if (!slected.contains(triple)) {
				triples_1.add(triple);
			}
		}

		ArrayList<String> list = new ArrayList<>();
		for (String string : slected) {
			list.add(string);
		}

		Collections.shuffle(list, new Random(1024));

		int listCount = foldernumber;// 拆分数量
		int[] arr = new int[listCount];
		int avg = slected.size() / listCount;// 平均数
		int addIndex = slected.size() - avg * listCount;// 需要增加1个数量的最大下标
		for (int i = 0; i < listCount; ++i) {
			arr[i] = i < addIndex ? avg + 1 : avg;
		}
		for (int i = 0; i < arr.length; i++) {
			int start;
			System.out.println(outDir + "/multi_self_" + i + "_data.nt");
			if (i < addIndex) {
				start = i * (avg + 1);
			} else {
				start = (avg + 1) * addIndex + (i - addIndex) * avg;
			}
			HashSet<Integer> folder = new HashSet<>();
			for (int j = start; j < start + arr[i]; j++) {
				folder.add(j);
			}

			HashSet<String> positive_train = new HashSet<>();
			HashSet<String> positive_test = new HashSet<>();

			for (int j = 0; j < list.size(); j++) {
				if (folder.contains(j)) {
					positive_test.add(list.get(j));
				} else {
					positive_train.add(list.get(j));
				}
			}
			for (String triple : triples_1) {
				positive_train.add(triple);
			}
			generateNegative_general(positive_train, positive_test, 1.0, outDir, i);
		}
	}
	
	
	public static HashSet<String> getDrugTarget() throws IOException {
		String drugbank_file=dataDir+"/output/datasets/orignial/network/association_drugbank.nq";
		
		String line=null;
		HashSet<String> targets=getTargets() ;
		HashSet<String> drugs=getDrugs();
		BufferedReader br=new BufferedReader(new FileReader(new File(drugbank_file)));
		HashSet<String> pair=new HashSet<>();
		while((line=br.readLine())!=null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")&&
						s.startsWith("<http://bio2rdf.org/drugbank:db")&&
						o.startsWith("<http://bio2rdf.org/drugbank:be")) {
					if(drugs.contains(s)&&targets.contains(o)) {
						pair.add(s+" "+o);	
					}
				}
			}
		}
		return pair;
	}
	public static void updateDisease_target(String file, HashMap<String,HashSet<String>> disease_targets) throws IOException {
		String name=new File(file).getName();
		name=name.substring(0,name.indexOf("."));
		HashSet<String> set=new HashSet<>();
		BufferedReader br=new BufferedReader(new FileReader(new File(file)));
		String line=null;
		while((line=br.readLine())!=null) {
			String[] elements=line.split("\t");
			String dbpedia_name="<http://bio2rdf.org/drugbank:"+elements[0]+">";
			set.add(dbpedia_name);
		}
		disease_targets.put(name, set);
	}
	public static HashMap<String,HashSet<String>>  getTest_clinicalCT(String annotationFile) throws IOException {
          
          HashMap<String,HashSet<String>> disease_allTargets=new HashMap<>();
  		for(File file:new File(dataDir+"/input/disease_annotation/diseases").listFiles()){
  			updateDisease_target(file.getAbsolutePath(),  disease_allTargets);
  		}
  		System.out.println(disease_allTargets);
  		Reader reader = Files.newBufferedReader(Paths.get(annotationFile),StandardCharsets.ISO_8859_1);
          CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withIgnoreHeaderCase());
          
          HashMap<String,HashSet<String>> disease_drugs=new HashMap<>();
          HashMap<String,HashSet<String>> disease_targets=new HashMap<>();
          HashMap<String,HashSet<String>> disease_newPair=new HashMap<>();
          
          HashSet<String> drugs=getDrugs();
          HashSet<String> targets=getTargets();
          
          HashSet<String> exisited_drugTarget=getDrugTarget();
          
          HashMap<String,HashSet<String>>  test_positive=new HashMap<>();
          
          for (CSVRecord csvRecord : csvParser) {
        	  String disease=csvRecord.get(1).toLowerCase();
        	  String target_id=csvRecord.get(2).toLowerCase();
        	  String drug_id=csvRecord.get(4).toLowerCase();
        	  String target="<http://bio2rdf.org/drugbank:"+target_id+">";
        	  String drug="<http://bio2rdf.org/drugbank:"+drug_id+">";
        	  
        	  if(drugs.contains(drug)&&targets.contains(target)) {
        		  
        		  if(!exisited_drugTarget.contains(drug+" "+target)) {
        			  if(test_positive.containsKey(disease)) {
            			  test_positive.get(disease).add(drug + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + target + " .");
            		  }else {
            			  HashSet<String> set=new HashSet<>();
            			  set.add(drug + " <http://bio2rdf.org/drugbank_vocabulary:drug-target> " + target + " .");
            			  test_positive.put(disease, set);
            		  }	
        		  }
        	  }
          }
          return test_positive;
	}
	
	public static void writeData_general(String annotationFile,  HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {
		
		HashMap<String,HashSet<String>> test_positives= getTest_clinicalCT( annotationFile) ;
		
		HashSet<String> train_positive = new HashSet<>();
		
		for(Entry<String, HashSet<String>> entry:test_positives.entrySet()) {
			HashSet<String> test_positive=entry.getValue();
			String disease=entry.getKey();
			for (String triple : triples) {
				if (!test_positive.contains(triple)) {
					train_positive.add(triple);
				}
			}
			generateNegative_clincalCT_general(train_positive, test_positive, 1.0, outDir, disease);
		}
		
	}
	
	public static void writeDataToFolders(int foldernumber, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashSet<String> triples_1 = new HashSet<>();
		for (String triple : triples) {
			if (!slected.contains(triple)) {
				triples_1.add(triple);
			}
		}

		ArrayList<String> list = new ArrayList<>();
		for (String string : slected) {
			list.add(string);
		}

		Collections.shuffle(list, new Random(1024));

		int listCount = foldernumber;// 拆分数量
		int[] arr = new int[listCount];
		int avg = slected.size() / listCount;// 平均数
		int addIndex = slected.size() - avg * listCount;// 需要增加1个数量的最大下标
		for (int i = 0; i < listCount; ++i) {
			arr[i] = i < addIndex ? avg + 1 : avg;
		}
		for (int i = 0; i < arr.length; i++) {
			int start;
			System.out.println(outDir + "/multi_self_" + i + "_data.nt");
			if (i < addIndex) {
				start = i * (avg + 1);
			} else {
				start = (avg + 1) * addIndex + (i - addIndex) * avg;
			}
			HashSet<Integer> folder = new HashSet<>();
			for (int j = start; j < start + arr[i]; j++) {
				folder.add(j);
			}

			HashSet<String> positive_train = new HashSet<>();
			HashSet<String> positive_test = new HashSet<>();

			for (int j = 0; j < list.size(); j++) {
				if (folder.contains(j)) {
					positive_test.add(list.get(j));
				} else {
					positive_train.add(list.get(j));
				}
			}
			for (String triple : triples_1) {
				positive_train.add(triple);
			}

			checkPass(positive_train, positive_test);
			generateNegative(positive_train, positive_test, 1.0, outDir, i);
			if (!checkPass(positive_train, positive_test)) {
				System.err.println("@ warning, not pass: " + outDir + "/bi_folder-" + i + "_data.nt");
			}
		}

	}
	
	
	
	
	public static void writeDataToFolders_clinicalCT(String annotationFile, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashMap<String,HashSet<String>> test_positives= getTest_clinicalCT( annotationFile) ;
		
		HashSet<String> train_positive = new HashSet<>();
		
		for(Entry<String, HashSet<String>> entry:test_positives.entrySet()) {
			HashSet<String> test_positive=entry.getValue();
			String disease=entry.getKey();
			for (String triple : triples) {
				if (!test_positive.contains(triple)) {
					train_positive.add(triple);
				}
			}
			generateNegative_clinicalCT(train_positive, test_positive, 1.0, outDir, disease);
		}

	}
	
	
	public static void writeDataToFolders_semiisolate_clinicalCT(String annotationFile, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashMap<String,HashSet<String>> test_positives= getTest_clinicalCT( annotationFile) ;
		
		HashSet<String> train_positive = new HashSet<>();
		
		for(Entry<String, HashSet<String>> entry:test_positives.entrySet()) {
			HashSet<String> test_positive=entry.getValue();
			String disease=entry.getKey();
			for (String triple : triples) {
				if (!test_positive.contains(triple)) {
					train_positive.add(triple);
				}
			}
			generateNegative_semiisolate_clincalCT(train_positive, test_positive, 1, outDir, disease);
		}

	}

	public static void writeDataToFolders_allisolate_clinicalCT(String annotationFile, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashMap<String,HashSet<String>> test_positives= getTest_clinicalCT( annotationFile) ;
		
		HashSet<String> train_positive = new HashSet<>();
		
		for(Entry<String, HashSet<String>> entry:test_positives.entrySet()) {
			HashSet<String> test_positive=entry.getValue();
			String disease=entry.getKey();
			for (String triple : triples) {
				if (!test_positive.contains(triple)) {
					train_positive.add(triple);
				}
			}
			generateNegative_allisolate_clincalCT(train_positive, test_positive, 1, outDir, disease);
		}
			
	}

	public static void writeDataToFolders_semiisolate(int foldernumber, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashSet<String> triples_1 = new HashSet<>();
		for (String triple : triples) {
			if (!slected.contains(triple)) {
				triples_1.add(triple);
			}
		}

		ArrayList<String> list = new ArrayList<>();
		for (String string : slected) {
			list.add(string);
		}

		Collections.shuffle(list, new Random(1024));

		int listCount = foldernumber;// 拆分数量
		int[] arr = new int[listCount];
		int avg = slected.size() / listCount;// 平均数
		int addIndex = slected.size() - avg * listCount;// 需要增加1个数量的最大下标
		for (int i = 0; i < listCount; ++i) {
			arr[i] = i < addIndex ? avg + 1 : avg;
		}
		for (int i = 0; i < arr.length; i++) {
			int start;
			System.out.println(outDir + "/multi_self_" + i + "_data.nt");
			if (i < addIndex) {
				start = i * (avg + 1);
			} else {
				start = (avg + 1) * addIndex + (i - addIndex) * avg;
			}
			HashSet<Integer> folder = new HashSet<>();
			for (int j = start; j < start + arr[i]; j++) {
				folder.add(j);
			}

			HashSet<String> positive_train = new HashSet<>();
			HashSet<String> positive_test = new HashSet<>();

			for (int j = 0; j < list.size(); j++) {
				if (folder.contains(j)) {
					positive_test.add(list.get(j));
				} else {
					positive_train.add(list.get(j));
				}
			}
			for (String triple : triples_1) {
				positive_train.add(triple);
			}

			if (!checkPass_isolate(positive_train, positive_test)) {
				System.err.println("@ warning, not pass: " + outDir + "/bi_folder-" + i + "_data.nt");
				System.exit(0);
			}

			generateNegative_semiisolate(positive_train, positive_test, 1, outDir, i);

		}

	}

	public static void writeDataToFolders_allisolate(int foldernumber, HashSet<String> triples, String outDir,
			HashSet<String> slected) throws IOException {

		HashSet<String> triples_1 = new HashSet<>();
		for (String triple : triples) {
			if (!slected.contains(triple)) {
				triples_1.add(triple);
			}
		}

		ArrayList<String> list = new ArrayList<>();
		for (String string : slected) {
			list.add(string);
		}

		Collections.shuffle(list, new Random(1024));

		int listCount = foldernumber;// 拆分数量
		int[] arr = new int[listCount];
		int avg = slected.size() / listCount;// 平均数
		int addIndex = slected.size() - avg * listCount;// 需要增加1个数量的最大下标
		for (int i = 0; i < listCount; ++i) {
			arr[i] = i < addIndex ? avg + 1 : avg;
		}
		for (int i = 0; i < arr.length; i++) {
			int start;
			System.out.println(outDir + "/multi_self_" + i + "_data.nt");
			if (i < addIndex) {
				start = i * (avg + 1);
			} else {
				start = (avg + 1) * addIndex + (i - addIndex) * avg;
			}
			HashSet<Integer> folder = new HashSet<>();
			for (int j = start; j < start + arr[i]; j++) {
				folder.add(j);
			}

			HashSet<String> positive_train = new HashSet<>();
			HashSet<String> positive_test = new HashSet<>();

			for (int j = 0; j < list.size(); j++) {
				if (folder.contains(j)) {
					positive_test.add(list.get(j));
				} else {
					positive_train.add(list.get(j));
				}
			}
			for (String triple : triples_1) {
				positive_train.add(triple);
			}
			if (!checkPass_isolate(positive_train, positive_test)) {
				System.err.println("@ warning, not pass: " + outDir + "/bi_folder-" + i + "_data.nt");
				System.exit(0);
			}
			generateNegative_allisolate(positive_train, positive_test, 1, outDir, i);
		}
	}

	public static void generateNegative_allisolate(HashSet<String> positive_train, HashSet<String> positive_test,
			int negative_ratio, String outDir, int i) throws IOException {
		NegativeGenerator_isolate g = new NegativeGenerator_isolate(positive_train, positive_test);
		NegativeSetBean bean_di = g.generate_isolate_di(1024, negative_ratio);
		writeToFile(outDir, i, "di", positive_train, positive_test, bean_di.getCandidateSet());

	}

	public static void generateNegative_semiisolate(HashSet<String> positive_train, HashSet<String> positive_test,
			int negative_ratio, String outDir, int i) throws IOException {
		NegativeGenerator_isolate g = new NegativeGenerator_isolate(positive_train, positive_test);
		NegativeSetBean bean_ss = g.generate_isolate_ss(1024, negative_ratio);
		NegativeSetBean bean_su = g.generate_isolate_su(1024, negative_ratio);

		writeToFile(outDir, i, "ss", positive_train, positive_test, bean_ss.getCandidateSet());
		writeToFile(outDir, i, "su", positive_train, positive_test, bean_su.getCandidateSet());
	}
	
	
	public static void generateNegative_allisolate_clincalCT(HashSet<String> positive_train, HashSet<String> positive_test,
			int negative_ratio, String outDir, String disease) throws IOException {
		NegativeGenerator_isolate g = new NegativeGenerator_isolate(positive_train, positive_test);
		NegativeSetBean bean_di = g.generate_isolate_di(1024, negative_ratio);
		writeToFile(outDir, disease, "di", positive_train, positive_test, bean_di.getCandidateSet());
	}

	public static void generateNegative_semiisolate_clincalCT(HashSet<String> positive_train, HashSet<String> positive_test,
			int negative_ratio, String outDir, String disease) throws IOException {
		NegativeGenerator_isolate g = new NegativeGenerator_isolate(positive_train, positive_test);
		NegativeSetBean bean_ss = g.generate_isolate_ss(1024, negative_ratio);
		NegativeSetBean bean_su = g.generate_isolate_su(1024, negative_ratio);

		writeToFile(outDir, disease, "ss", positive_train, positive_test, bean_ss.getCandidateSet());
		writeToFile(outDir, disease, "su", positive_train, positive_test, bean_su.getCandidateSet());

	}

	public static void generateNegative_general(HashSet<String> positive_train, HashSet<String> positive_test,
			Double negative_ratio, String outDir, int i) throws IOException {
		NegativeGenerator g = new NegativeGenerator(positive_train, positive_test);

		NegativeSetBean bean = g.generate_general(1024, negative_ratio);

		writeToFile(outDir, i, "general", positive_train, positive_test, bean.getCandidateSet()); // test-test node
	}
	
	public static void generateNegative_clincalCT_general(HashSet<String> positive_train, HashSet<String> positive_test,
			Double negative_ratio, String outDir, String disease) throws IOException {
		NegativeGenerator g = new NegativeGenerator(positive_train, positive_test);

		NegativeSetBean bean = g.generate_general(1024, negative_ratio);

		writeToFile(outDir, disease, "general", positive_train, positive_test, bean.getCandidateSet()); // test-test node
	}

	public static void generateNegative(HashSet<String> positive_train, HashSet<String> positive_test,
			Double negative_ratio, String outDir, int i) throws IOException {
		NegativeGenerator g = new NegativeGenerator(positive_train, positive_test);
		NegativeSetBean bean_tt = g.generate(1024, negative_ratio, "tt");
		NegativeSetBean bean_tc = g.generate(1024, negative_ratio, "tc");
		NegativeSetBean bean_ta = g.generate(1024, negative_ratio, "ta");
		NegativeSetBean bean_cc = g.generate_bigdata(1024, negative_ratio, "cc");
		NegativeSetBean bean_ca = g.generate(1024, negative_ratio, "ca");
		NegativeSetBean bean_aa = g.generate(1024, negative_ratio, "aa");

		writeToFile(outDir, i, "tt", positive_train, positive_test, bean_tt.getCandidateSet()); // test-test node
		writeToFile(outDir, i, "tc", positive_train, positive_test, bean_tc.getCandidateSet()); // test-connect node
		writeToFile(outDir, i, "ta", positive_train, positive_test, bean_ta.getCandidateSet()); // test-all node
		writeToFile(outDir, i, "cc", positive_train, positive_test, bean_cc.getCandidateSet()); // connect-connect node
		writeToFile(outDir, i, "ca", positive_train, positive_test, bean_ca.getCandidateSet()); // connect-all node
		writeToFile(outDir, i, "aa", positive_train, positive_test, bean_aa.getCandidateSet()); // all-all node
	}
	
	
	
	public static void generateNegative_clinicalCT(HashSet<String> positive_train, HashSet<String> positive_test,
			Double negative_ratio, String outDir, String disease) throws IOException {
		NegativeGenerator g = new NegativeGenerator(positive_train, positive_test);
		NegativeSetBean bean_tt = g.generate(1024, negative_ratio, "tt");
		NegativeSetBean bean_tc = g.generate(1024, negative_ratio, "tc");
		NegativeSetBean bean_ta = g.generate(1024, negative_ratio, "ta");
		NegativeSetBean bean_cc = g.generate_bigdata(1024, negative_ratio, "cc");
		NegativeSetBean bean_ca = g.generate(1024, negative_ratio, "ca");
		NegativeSetBean bean_aa = g.generate(1024, negative_ratio, "aa");
		
		writeToFile(outDir, disease, "tt", positive_train, positive_test, bean_tt.getCandidateSet()); // test-test node
		writeToFile(outDir, disease, "tc", positive_train, positive_test, bean_tc.getCandidateSet()); // test-connect node
		writeToFile(outDir, disease, "ta", positive_train, positive_test, bean_ta.getCandidateSet()); // test-all node
		writeToFile(outDir, disease, "cc", positive_train, positive_test, bean_cc.getCandidateSet()); // connect-connect node
		writeToFile(outDir, disease, "ca", positive_train, positive_test, bean_ca.getCandidateSet()); // connect-all node
		writeToFile(outDir, disease, "aa", positive_train, positive_test, bean_aa.getCandidateSet()); // all-all node
	}

	public static HashSet<String> getTargets() throws IOException {
		HashSet<String> targetSet = new HashSet<>();
		BufferedReader bReader = new BufferedReader(new FileReader(
				new File(dataDir+"/output/datasets/orignial/sequence.txt")));
		String lineString = null;
		while ((lineString = bReader.readLine()) != null) {
			String[] elementStrings = lineString.split("\t");
			targetSet.add(elementStrings[0]);
		}
		return targetSet;
	}

	public static HashSet<String> getDrugs() throws IOException {
		HashSet<String> drugSet = new HashSet<>();
		BufferedReader bReader = new BufferedReader(new FileReader(
				new File(dataDir+"/output/datasets/orignial/smile.txt")));
		String lineString = null;
		while ((lineString = bReader.readLine()) != null) {
			String[] elementStrings = lineString.split("\t");
			drugSet.add(elementStrings[0]);
		}
		return drugSet;
	}

	public static void writeToFile(String outDir, int i, String type, HashSet<String> positive_train,
			HashSet<String> positive_test, HashSet<String> nateive_test) throws IOException {

		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(outDir + "/train_" + i + "_" + type + ".nt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(outDir + "/test_" + i + "_" + type + ".nt")));
		HashSet<String> drugs = getDrugs();
		HashSet<String> targets = getTargets();

		for (String string : positive_train) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				bw1.write(s + " " + o + " " + "true \n");
				if (!drugs.contains(s)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + s);
				}
				if (!targets.contains(o)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + o);
				}
			}
		}
		for (String string : positive_test) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				bw2.write(s + " " + o + " " + "true \n");
				if (!drugs.contains(s)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + s);
				}
				if (!targets.contains(o)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + o);
				}
			}
		}
		for (String string : nateive_test) {
			String[] elementStrings = string.split(" ");
			bw2.write(elementStrings[0] + " " + elementStrings[1] + " " + "false \n");

			if (!drugs.contains(elementStrings[0])) {
				System.err.println(
						type + " @ warning nateive_test, not SMILE or sequence for the pair " + elementStrings[0]);
			}
			if (!targets.contains(elementStrings[1])) {
				System.err.println(
						type + " @ warning nateive_test, not SMILE or sequence for the pair " + elementStrings[1]);
			}
		}
		bw1.flush();
		bw1.close();
		bw2.flush();
		bw2.close();
	}
	
	
	public static void writeToFile(String outDir, String disease, String type, HashSet<String> positive_train,
			HashSet<String> positive_test, HashSet<String> nateive_test) throws IOException {

		BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File(outDir + "/train_" + disease + "_" + type + ".nt")));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(outDir + "/test_" + disease + "_" + type + ".nt")));
		HashSet<String> drugs = getDrugs();
		HashSet<String> targets = getTargets();

		for (String string : positive_train) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				bw1.write(s + " " + o + " " + "true \n");
				if (!drugs.contains(s)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + s);
				}
				if (!targets.contains(o)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + o);
				}
			}
		}
		for (String string : positive_test) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				bw2.write(s + " " + o + " " + "true \n");
				if (!drugs.contains(s)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + s);
				}
				if (!targets.contains(o)) {
					System.err.println(type + " @ warning positive_test, not SMILE or sequence for the pair " + o);
				}
			}
		}
		for (String string : nateive_test) {
			String[] elementStrings = string.split(" ");
			bw2.write(elementStrings[0] + " " + elementStrings[1] + " " + "false \n");

			if (!drugs.contains(elementStrings[0])) {
				System.err.println(
						type + " @ warning nateive_test, not SMILE or sequence for the pair " + elementStrings[0]);
			}
			if (!targets.contains(elementStrings[1])) {
				System.err.println(
						type + " @ warning nateive_test, not SMILE or sequence for the pair " + elementStrings[1]);
			}
		}
		bw1.flush();
		bw1.close();
		bw2.flush();
		bw2.close();
	}
	
	
	
	public static boolean checkPass_isolate(HashSet<String> trian_positive, HashSet<String> test_positive)
			throws IOException {

		boolean pass = true;
		for (String string : trian_positive) {
			if (test_positive.contains(string)) {
				pass = false;
				break;
			}
		}
		return pass;
	}

	public static boolean checkPass(HashSet<String> trian_positive, HashSet<String> test_positive) throws IOException {

		HashSet<String> drugs1 = new HashSet<>();
		HashSet<String> targets1 = new HashSet<>();
		for (String sting : trian_positive) {
			InputStream inputStream = new ByteArrayInputStream(sting.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					drugs1.add(s);
					targets1.add(o);
				}
			}
		}

		boolean pass = true;
		HashSet<String> drugs2 = new HashSet<>();
		HashSet<String> targets2 = new HashSet<>();

		for (String string : test_positive) {
			InputStream inputStream = new ByteArrayInputStream(string.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();
				if (p.equals("<http://bio2rdf.org/drugbank_vocabulary:drug-target>")) {
					drugs2.add(s);
					targets2.add(o);
				}
			}
		}

		for (String string : drugs2) {
			if (!drugs1.contains(string)) {
				System.err.println(string);
				pass = false;
				break;
			}
		}
		for (String string : targets2) {
			if (!targets1.contains(string)) {
				System.err.println(string);
				pass = false;
				break;
			}
		}
		return pass;
	}

	public static boolean checkPass(String generatedfile, String removedfile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(generatedfile)));
		String line = null;

		HashSet<String> drugs1 = new HashSet<>();
		HashSet<String> targets1 = new HashSet<>();
		while ((line = br.readLine()) != null) {
			if (!line.contains("\"")) {
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					if (p.equals("<http://bio2rdf.org/MultiPartiteNetwork_vocabulary:Drug-Target>")) {
						drugs1.add(s);
						targets1.add(o);
					}
				}
			}
		}
		boolean pass = true;
		br = new BufferedReader(new FileReader(new File(removedfile)));
		line = null;
		HashSet<String> drugs2 = new HashSet<>();
		HashSet<String> targets2 = new HashSet<>();
		while ((line = br.readLine()) != null) {
			if (!line.contains("\"")) {
				InputStream inputStream = new ByteArrayInputStream(line.getBytes());
				NxParser nxp = new NxParser();
				nxp.parse(inputStream);
				while (nxp.hasNext()) {
					Node[] quard = nxp.next();
					String s = quard[0].toString().trim();
					String p = quard[1].toString().trim();
					String o = quard[2].toString().trim();
					if (p.equals("<http://bio2rdf.org/MultiPartiteNetwork_vocabulary:Drug-Target>")) {
						drugs2.add(s);
						targets2.add(o);
					}
				}
			}
		}
		for (String string : drugs2) {
			if (!drugs1.contains(string)) {
				pass = false;
				break;
			}
		}
		for (String string : targets2) {
			if (!targets1.contains(string)) {
				pass = false;
				break;
			}
		}
		return pass;
	}

}
