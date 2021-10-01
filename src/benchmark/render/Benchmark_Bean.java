package benchmark.render;

import java.util.HashSet;

public class Benchmark_Bean {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	
	public int getTrue_num() {
		return true_num;
	}
	public void setTrue_num(int true_num) {
		this.true_num = true_num;
	}
	public int getFalse_num() {
		return false_num;
	}
	public void setFalse_num(int false_num) {
		this.false_num = false_num;
	}
	public int getPair_num() {
		return pair_num;
	}
	public void setPair_num(int pair_num) {
		this.pair_num = pair_num;
	}
	public HashSet<String> getDrugs() {
		return drugs;
	}
	public void setDrugs(HashSet<String> drugs) {
		this.drugs = drugs;
	}
	public HashSet<String> getTargets() {
		return targets;
	}
	public void setTargets(HashSet<String> targets) {
		this.targets = targets;
	}

	int true_num=0;
	int false_num=0;
	int pair_num=0;
	HashSet<String> drugs=new HashSet<>();
	HashSet<String> targets=new HashSet<>();
	

}
