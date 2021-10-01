package data.process.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class KeggCompoundToProtein {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// new KeggCompoundToProtein().writeGene(
		// "data/input/kgg/kegg-disease.nq", "data/input/kgg/kegg-genes.nq",
		// "data/input/diseasome/diseasome_dump.nt", null,
		// new BufferedWriter(new FileWriter(new
		// File("data/input/drugCloud/test.nt"))));
		new KeggCompoundToProtein().writeCompoundMapping("data/input/drugbank_4/drugbank.nq", null);
		new KeggCompoundToProtein().writeCompoundToDisease("data/input/kgg/kegg-pathway.nq",
				new BufferedWriter(new FileWriter(new File("data/input/drugCloud/test.nt"))));
	}

	public void writeDrugToGene(String bio2rdfdrugbankfile, String keggDisease, String keggPathway,
			String keggGene, String diseasome, String drugbank, BufferedWriter bw) throws IOException {
		writeCompoundMapping(bio2rdfdrugbankfile, bw);
		writeCompoundToDisease(keggPathway, bw);
		writeGene(keggDisease, keggGene, diseasome, bw);
		writeProtein(keggGene, drugbank, bw);
	}

	/**
	 * 
	 * @param keggCompound
	 * @param bw
	 * @return
	 * @throws IOException
	 */
	public void writeCompoundMapping(String bio2rdfDrugBank, BufferedWriter bw) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(bio2rdfDrugBank)));
		String line = null;
		HashMap<String, HashSet<String>> compoundToDrugbank = new HashMap<>();
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();

				if (s.startsWith("<http://bio2rdf.org/drugbank:DB")
						& p.equals("<http://bio2rdf.org/drugbank_vocabulary:x-kegg>")
						& o.startsWith("<http://bio2rdf.org/kegg:")) {
					String news = "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/"
							+ s.substring(s.lastIndexOf(":") + 1);

					if (compoundToDrugbank.containsKey(o)) {
						compoundToDrugbank.get(o).add(news);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(news);
						compoundToDrugbank.put(o, set);
					}
				}
			}
		}

		System.out.println("kegg drug: " + compoundToDrugbank.size());

		for (Entry<String, HashSet<String>> entry : compoundToDrugbank.entrySet()) {
			for (String string : entry.getValue()) {
				bw.write(entry.getKey() + " <http://www.w3.org/2002/07/owl#sameAs> " + string + " .\n");
			}
		}
		bw.flush();
	}

	public void writeCompoundToDisease(String pathwayfile, BufferedWriter bw) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(pathwayfile)));
		String line = null;

		HashMap<String, HashSet<String>> compoundPathway = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> pathwayDisease = new HashMap<String, HashSet<String>>();

		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();

				if (s.startsWith("<http://bio2rdf.org/kegg:map") & o.startsWith("<http://bio2rdf.org/kegg:C")
						& p.startsWith("<http://bio2rdf.org/kegg_vocabulary:compound>")) {
					if (compoundPathway.containsKey(o)) {
						compoundPathway.get(o).add(s);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(s);
						compoundPathway.put(o, set);
					}
				}

				if (s.startsWith("<http://bio2rdf.org/kegg:map") & o.startsWith("<http://bio2rdf.org/kegg:H")
						& p.startsWith("<http://bio2rdf.org/kegg_vocabulary:disease>")) {
					if (pathwayDisease.containsKey(s)) {
						pathwayDisease.get(s).add(o);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(o);
						pathwayDisease.put(s, set);
					}
				}
			}
		}
		
		for (Entry<String, HashSet<String>> entry : compoundPathway.entrySet()) {
			for (String string : entry.getValue()) {
				bw.write(string + " <http://bio2rdf.org/kegg_vocabulary:keggCompound> " + entry.getKey() + " .\n"); 
			}
		}
		
		for (Entry<String, HashSet<String>> entry : pathwayDisease.entrySet()) {
			for(String string:entry.getValue()){
				bw.write(entry.getKey() + " <http://bio2rdf.org/kegg_vocabulary:keggDisease> " + string + " .\n"); 
			}
		}
		bw.flush();
	}

	public void writeGene(String keggDisease, String keggGene, String diseasome, 
			BufferedWriter bw) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(keggDisease)));
		String line = null;

		HashMap<String, HashSet<String>> diseaseGene = new HashMap<String, HashSet<String>>();
		HashSet<String> genes = new HashSet<>();
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();

				if (s.startsWith("<http://bio2rdf.org/kegg:H") & o.startsWith("<http://bio2rdf.org/kegg:")
						& p.startsWith("<http://bio2rdf.org/kegg_vocabulary:gene>")) {
					if (diseaseGene.containsKey(s)) {
						diseaseGene.get(s).add(o);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(o);
						diseaseGene.put(s, set);
					}
				}

			}
		}
		
		HashMap<String, String> hngcToDiseasome = new KeggGeneMapping().getDiseasomeGeneToHGNCMapping(diseasome);
		HashMap<String, String> geneTohngc = new KeggGeneMapping().getKeggGeneToHGNCMapping(keggGene);
		
		for(Entry<String,String> entry:geneTohngc.entrySet()){
			if(hngcToDiseasome.containsKey(entry.getValue())){
				bw.write(entry.getKey() + " <http://www.w3.org/2002/07/owl#sameAs> " + hngcToDiseasome.get(entry.getValue()) + " .\n");
			}
		}
		
		
		
		for (Entry<String, HashSet<String>> entry : diseaseGene.entrySet()) {
			for (String gene : entry.getValue()) {
				bw.write(entry.getKey() + " <http://bio2rdf.org/kegg_vocabulary:keggGene> " + gene + " .\n");
			}
		}
		bw.flush();
	}

	public void writeProtein(String keggGene, String drugBank, BufferedWriter bw)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(keggGene)));
		String line = null;

		HashMap<String, HashSet<String>> geneProtein = new HashMap<String, HashSet<String>>();
		HashSet<String> proteins = new HashSet<>();
		while ((line = br.readLine()) != null) {
			InputStream inputStream = new ByteArrayInputStream(line.getBytes());
			NxParser nxp = new NxParser();
			nxp.parse(inputStream);
			while (nxp.hasNext()) {
				Node[] quard = nxp.next();
				String s = quard[0].toString().trim();
				String p = quard[1].toString().trim();
				String o = quard[2].toString().trim();

				if (s.startsWith("<http://bio2rdf.org/kegg:") & o.startsWith("<http://bio2rdf.org/uniprot:")
						& p.startsWith("<http://bio2rdf.org/kegg_vocabulary:x-uniprot>")) {
					if (geneProtein.containsKey(s)) {
						geneProtein.get(s).add(o);
					} else {
						HashSet<String> set = new HashSet<>();
						set.add(o);
						geneProtein.put(s, set);
					}
				}

			}
		}

		HashMap<String, String> uniprots = new DrugBank().getDrugBankUniprotMapping(drugBank);
		
		for(Entry<String,String> entry:uniprots.entrySet()){
			bw.write( entry.getValue()+ " <http://www.w3.org/2002/07/owl#sameAs> " + entry.getKey()
					+ " .\n");
		}
		
		for (Entry<String, HashSet<String>> entry : geneProtein.entrySet()) {
			for (String uniprot : entry.getValue()) {
				bw.write(entry.getKey() + " <http://bio2rdf.org/kegg_vocabulary:associatedProtein> " + uniprot
						+ " .\n");
			}
		}
	}

}
