<table border='1' align="center">
<tr>
<img src="img/log_1.png" height="500" width="2500" >
</tr>
</tabe>


# Objective:   
We provide a large-scale benchmark that enables a comprehensive evaluation of the drug-target predictive models to facilitate a better selection of computational strategies for pre-screening. This benchmark functions are: 

* 1) an extensive multiple-partite network (e.g., 0.95 million biomedical concepts including 59 thousand drugs and 75 thousand targets, and 2.5 million associations including 817 thousand drug-target associations) as well as drug-drug and protein-protein similarities based on drug chemical structures and gene sequences
* 2) a way of comprehensively evaluating strategies that reflect diverse scenarios (a total 1300 tasks across two types of training/testing sampling strategies based on drug-target space as well as five types of validation strategies).  

# Data:
<table border='1' align="center">
<tr>
We constructed a multiple-partite network based on an integration of the existing 9 biomedical repositories (Diseasome, Drugbank, Gene Ontology Annotation (GOA), Interaction Reference Index (iRefindex), Kyoto Encyclopedia of Genes and Genomes (KEGG), Linked Structured Product Label (Linkedspl), Online Mendelian Inheritance in Man (OMIM), Pharmacogenomics Knowledge Base (Pharmgkb), and SIDER) incorporating 952,489 entities and 2,560,787 associations in total (see Table 1) 
</tr>
<tr>
<img src="img/data.png" height="500" width="2500" >
</tr>
<tr>
<img src="img/table_1.png" height="500" width="800" >
</tr>
</tabe>

# Benchmark    
<table border='1' align="center">
<tr>
There were five types of evaluation tasks designed based on the different combinations of validation and drug-target space for training and testing (see Table 2).
</tr>
<tr>
<img src="img/table_2.png" height="500" width="2500" >
</tr>
</tabe>
  
# How to use

## Downlaod the latest benchmark
We have provided three sources for you, 1) benchmark-raw (raw data directly downloaded from each source),  2) benchmark-data (networks and mappings), 3) benchmark-tasks (a total of 1300 tasks). Please follow the path to download the archived benchmark and data. Please note, you don't need licenses to obtain all the evaluation tasks. However, the archives of raw data and benchmark data are encrypted due to the copy-right policy regulated by the license in each database used. You need to get the licenses and contact the author (Zong.nansu@mayo.edu) for decryption.

- benchmark/row/ ** These are the raw data, please prepare the licenses for decryption **
- benchmark/data/ ** These are the data used in the benchmark, please prepare the licenses for decryption **
- benchmark/tasks/ ** These are the evaluation tasks in the benchmark, you don't need licenses **


## Generate your own version of benchmark
While we will regularly generate a new version of the benchmark based on the newly released raw data, we also provide the codes to enable the generation of your own benchmark. You need to download the data from each database with a valid license.   

- Data sources: 
   - [DrugBank](https://go.drugbank.com/)
   - [bio2rdf](https://bio2rdf.org/)
   - [DiseaseNetwork](http://networkrepository.com/bio-diseasome.php)
   
- Data download ** (you can reuse the data in the latest benchmark if you do not need to replace them with the preferred version) **:
   - https://bio2rdf.org/ --> RDF forms of DrugBank, GOA, Irefindex, KEGG, Linkedspl, OMIM, Pharmgkb, Pharmgkb-offside, Sider
   - http://networkrepository.com/bio-diseasome.php --> RDF form of Disease network from diseasome
   - https://go.drugbank.com/ --> latest drug-target from DrugBank
   - replace the sample files at data_sample/input/done with  RDF files downloaded from bio2rdf and diseasome  data_sample/input/done
   - put latest drug-target from DrugBank (xml) to data_sample/output/datasets/orinigal
   
- Sample data: ** @ Warning: The sample datasets are used for demonstration of the data format, you need downloading full datasets to run. Running sample dataset will result in failure of the program**
   - data_sample/input/done ## **Those are the datasets with a reduced size, please go the data sources to download the file with the corresponding names**
   - data_sample/output/\*.nq ## ** Those are the example networks used in the benchmark, including associations and mappings **
   - data_sample/output/datasets/experiment ## **Those are the example evaluation tasks for the benchmark, to generate the tasks needed a generation of the network first**
   - data_sample/output/datasets/orinigal ## **Those are the files needed to generate the network and evaluation tasks. @ Warning: modification could result failure of the program**
   
- Environment:
  - Java 1.8
  - Weka 3.8
  - nxparser 2.2
- Maven install:
  - [Maven in eclipse](https://www.vogella.com/tutorials/EclipseMaven/article.html)
- Network generation:
  - Java src/main/Render_main.java ## **running all the data needs approximately 1 hour**
- Benchmark generation:
  - Java src/main/Benchmark_main.java **running all the data needs approximately 3 hours**
# Contact
For help or questions of using the application, please contact Zong.nansu@mayo.edu