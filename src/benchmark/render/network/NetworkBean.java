package benchmark.render.network;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.collections.map.HashedMap;


public class NetworkBean {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	HashSet<String> drugSet;
	HashSet<String> targetSet;
	HashMap<String,HashSet<String>> drug_target_map;
	
	
	public HashSet<String> getDrugSet() {
		return drugSet;
	}
	public void setDrugSet(HashSet<String> drugSet) {
		this.drugSet = drugSet;
	}
	public HashSet<String> getTargetSet() {
		return targetSet;
	}
	public void setTargetSet(HashSet<String> targetSet) {
		this.targetSet = targetSet;
	}
	public HashMap<String, HashSet<String>> getDrug_target_map() {
		return drug_target_map;
	}
	public void setDrug_target_map(HashMap<String, HashSet<String>> drug_target_map) {
		this.drug_target_map = drug_target_map;
	}

}
