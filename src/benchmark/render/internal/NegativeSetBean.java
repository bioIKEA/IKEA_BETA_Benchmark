package benchmark.render.internal;

import java.util.HashMap;
import java.util.HashSet;

public class NegativeSetBean {
	HashMap<String,HashSet<String>> candidateDrugTargets;
	public HashMap<String, HashSet<String>> getCandidateDrugTargets() {
		return candidateDrugTargets;
	}
	public void setCandidateDrugTargets(HashMap<String, HashSet<String>> candidateDrugTargets) {
		this.candidateDrugTargets = candidateDrugTargets;
	}
	public HashMap<String, HashSet<String>> getCandidateTargetDrugs() {
		return candidateTargetDrugs;
	}
	public void setCandidateTargetDrugs(HashMap<String, HashSet<String>> candidateTargetDrugs) {
		this.candidateTargetDrugs = candidateTargetDrugs;
	}
	public HashSet<String> getCandidateSet() {
		return candidateSet;
	}
	public void setCandidateSet(HashSet<String> candidateSet) {
		this.candidateSet = candidateSet;
	}
	HashMap<String,HashSet<String>> candidateTargetDrugs;
	HashSet<String> candidateSet;
	
	
	
}
