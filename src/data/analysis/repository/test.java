package data.analysis.repository;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String o="\n               \n               \n               CLINICAL PHARMACOLOGY:\n               \n                  Although clindamycin phosphate is inactive in vitro, rapid in vivo hydrolysis converts this compound to the antibacterially active clindamycin.\n                  Clindamycin has been shown to have in vitro activity against isolates of Propionibacterium acnes. This may account for its usefulness in acne.\n                  Cross resistance has been demonstrated between clindamycin and lincomycin.\n                  Antagonism has been demonstrated between clindamycin and erythromycin.\n                  Following multiple topical applications of clindamycin phosphate at a concentration equivalent to 10 mg clindamycin per mL in an isopropyl alcohol and water solution, very low levels of clindamycin are present in the serum (0-3 ng/mL) and less than 0.2% of the dose is recovered in urine as clindamycin.\n                  Clindamycin activity has been demonstrated in comedones from acne patients. Clindamycin in vitro inhibits all Propionibacterium acnes cultures tested (MICs 0.4 mcg/mL). Free fatty acids on the skin surface have been decreased from approximately 14% to 2% following application of clindamycin.\n               \n               \n            \n         ";
		String new_o=o.replaceAll("\n"," ").trim();
		System.out.println(o);
		System.err.println(new_o);
	}

}
