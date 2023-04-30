# QaRS

QaRS (Query-and-Relax System (QaRS) is designed to facilitate the exploitation of large knowledge bases. QaRS implements two main functionnalities: 

* It gives explanation for the failure of a SPARQL query;
* It performs relaxation of a SPARQL query in order to return top-k alternative answers.

The explanation functionnality of QaRS returns the following subqueries:
* Minimal Failing Subqueries (MFSs). Each MFS is a minimal part of a SPARQL query that fails;
* MaXimal Succeeding Subqueries (XSSs). Each XSS is a maximal (in terms of triple patterns) non-failing subquery viewed as a relaxed query.

These MFSs and XSSs can be computed with two algorithms (for details, please see our publications):
* Lattice-Based Approach (LBA);
* Matrice-Based Approach (MBA).

The following four algorithms are implemented to obtain top-k alternative answers. They use different levels of information  about the MFSs to speed up the relaxation process (again, please see our publications for details).
* Best first Search (BFS) Algorithm (A related work algorithm).
* MFS-Based Search (MBS) Algorithm.
* Optimized MFS-Based Search (O-MBS) Algorithm.
* Full MFS-Based Search (F-MBS) Algorithm.

## Software requirements

* Java version >= 8.
* An integrated development environment (i.e. Eclipse: https://eclipse.org/downloads/).
* All operating systems that support at least the Java 8 version.
* Maven.

## Compilation

* Compile the project and deploy the artifcats to the local Maven repository.

```console
$ mvn clean install
```

## Usage

### Step 0: Data Initialization instructions (Mandatory before any experiment)

* Generate LUBM (Lehigh University Benchmark) data set and copy the content into a directory named "lubm" (i.e. _c:/lubm/lubm1.owl_).

* Download and copy [univ-bench.owl](http://swat.cse.lehigh.edu/onto/univ-bench.owl) into the previous directory where is stored the data set (i.e. _c:/lubm/univ-bench.owl_).

* Create a sample Maven project

* Add QaRS Maven dependency

```xml
<groupId>fr.ensma.lias</groupId>
<artifactId>qarscore</artifactId>
<version>0.1-SNAPSHOT</version>
```

* Create a snippet class (i.e. _QARSInitializationSample_) with a Java main method and fill it with the following code.

```java
public class QARSInitializationSample {
    public static void main(String[] args) {
        String[] params = new String[5];
        params[0] = "c:/lubm"; // Data folder
        params[1] = "OWL";
        params[2] = "TDB";
        params[3] = "c:/tdbrepository-saturated"; // TDB repository path
        params[4] = "true"; // Enable RDFS entailment
        JenaBulkLoader.main(params);
    }
}
```

* Execute the previous code to initialize Jena TDB.

### Step 1: Using the LBA and MBA Algorithms

* Create a snippet class (i.e. _QARSMFSComputeSample_) with a Java main method and fill it with the following code.

* Initialize *QaRS* session and the query.

```java
public class QARSMFSComputeSample {
    public static void main(String[] args) {
        Session session = SessionFactory.getTDBSession("c:/tdbrepository-saturated");

        String QUERY_2 = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
	    + "PREFIX ub:   <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> "
	    + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 "
	    + "WHERE { ?X rdf:type ub:UndergraduateStudent . "
	    + "?X ub:memberOf ?Y1 . "
	    + "?X ub:mastersDegreeFrom <http://www.University822.edu>  . "
	    + "?X ub:emailAddress ?Y2 . "
	    + "?X ub:advisor <http://www.Department0.University0.edu/FullProfessor0> . "
	    + "?X ub:takesCourse ?Y4. "
	    + "?X ub:name ?Y5  . "
	    + " }";
```

* Instructions to compute the MFSs and XSSs with the _Lattice-Based Approach_ (LBA).

```java
        CQuery conjunctiveQuery = CQueryFactory.createCQuery(QUERY_2);
        MFSSearch relaxationStrategy = StrategyFactory.getLatticeStrategy(session, conjunctiveQuery);
        List<CQuery> allMFS = relaxationStrategy.getAllMFS();
        List<CQuery> allXSS = relaxationStrategy.getAllXSS();
```

* Instructions to compute the MFSs and XSSs with the _Matrice-Based Approach_ (MBA). Use only this approach for star-shaped queries.

```java
        CQuery conjunctiveQuery = CQueryFactory.createCQuery(QUERY_2);
        MFSSearch relaxationStrategy = StrategyFactory.getMatrixStrategy(session, conjunctiveQuery);
        List<CQuery> allMFS = relaxationStrategy.getAllMFS();
        List<CQuery> allXSS = relaxationStrategy.getAllXSS();
    }
}
```

### Step 2: Using the BFS, MBS, O-MBS and F-MBS Algorithms

* Create a snippet class (i.e. _QARSRelaxationSample_) with a Java main method and fill it with the following code.

```java
public class QARSRelaxationSample {
    public static void main(String[] args) {
        Session session = SessionFactory.getTDBSession("c:/tdbrepository-saturated");

        String QUERY_2 = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
	    + "PREFIX ub:   <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> "
	    + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 "
	    + "WHERE { ?X rdf:type ub:UndergraduateStudent . "
	    + "?X ub:memberOf ?Y1 . "
	    + "?X ub:mastersDegreeFrom <http://www.University822.edu>  . "
	    + "?X ub:emailAddress ?Y2 . "
	    + "?X ub:advisor <http://www.Department0.University0.edu/FullProfessor0> . "
	    + "?X ub:takesCourse ?Y4. "
	    + "?X ub:name ?Y5  . "
	    + " }";

        final int TOP_K = 50; 
```

* Initialize the list for retrieving relaxed solutions. 

```java
        final LinkedHashMap<String, Double> solutions = new LinkedHashMap<String, Double>(TOP_K);
```

*  Create @CQuery@ object with @QUERY_2@ and a @RelaxationStrategy@ object. This relaxation strategy is MBS (MFS-based Search).

```java
        CQuery conjunctiveQuery = CQueryFactory.createCQuery(QUERY_2);
        RelaxationStrategy relaxedQuery = new MFSBaseRelaxationStrategy(conjunctiveQuery, session);
```

* Use the following code to get a maximum of _k_ alternative answers

```java
        while ((!hasTopk)&&(relaxedQuery.hasNext())){
        QueryStatement stm = session.createStatement(relaxedQuery.next().toString());
	        
        ResultSet results = (ResultSet) stm.executeQuery();
        double sim = relaxedQuery.getCurrentSimilarity();
        while ((results.hasNext()) && (solutions.size() < TOP_K)) {
            QuerySolution sol = results.nextSolution();
            if(!solutions.keySet().contains(sol.toString())){
                solutions.put(sol.toString(), sim);
            }
        }
	        
        hasTopk = solutions.size() >= TOP_K;
    }
}
```

#### Remarks

* As relaxation strategy, Qars implements four differents algorithms with different performance. For using these algorithms you have to change the following instruction.

```java
RelaxationStrategy relaxedQuery = new MFSBaseRelaxationStrategy(conjunctiveQuery, session);
```

* by one of these other instructions 

```java
1- RelaxationStrategy relaxedQuery = new BasicRelaxationStrategy(conjunctiveQuery, session); // BFS
2- RelaxationStrategy relaxedQuery = new MFSBaseOptimizedRelaxationStrategy(conjunctiveQuery, session); // O-MBS
3- RelaxationStrategy relaxedQuery = new IncrementalMFSBaseRelaxationStrategy(conjunctiveQuery, session); // F-MBS
```

### Results

* With all the previous relaxation strategies, we obtain the following results for Query_2 (all the results are associated with a score)

```
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = "GraduateStudent93@Department10.University0.edu" ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.5914630829395665
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse39> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = "GraduateStudent93@Department10.University0.edu" ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.5914630829395665
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse12> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = "GraduateStudent93@Department10.University0.edu" ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.5914630829395665
( ?Y4 = <http://www.Department0.University0.edu/GraduateCourse59> ) ( ?Y5 = "GraduateStudent48" ) ( ?Y2 = "GraduateStudent48@Department0.University0.edu" ) ( ?X = <http://www.Department0.University0.edu/GraduateStudent48> ) ( ?Y1 = <http://www.Department0.University0.edu> )---->0.5613006264956873
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = rdfs:Resource ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.394308721959711
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = <http://swat.cse.lehigh.edu/onto/univ-bench.owl#Person> ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.394308721959711
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = <http://swat.cse.lehigh.edu/onto/univ-bench.owl#Student> ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.394308721959711
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = _:b0 ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.394308721959711
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = <http://swat.cse.lehigh.edu/onto/univ-bench.owl#GraduateStudent> ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.394308721959711
( ?Y4 = <http://www.Department10.University0.edu/GraduateCourse24> ) ( ?Y5 = "GraduateStudent93" ) ( ?Y2 = _:b1 ) ( ?X = <http://www.Department10.University0.edu/GraduateStudent93> ) ( ?Y1 = <http://www.Department10.University0.edu> )---->0.394308721959711
```

## Publications

* Géraud FOKOU, Stéphane JEAN, Allel HADJALI, Mickael BARON, _RDF Query Relaxation Strategies Based on Failure Causes_, In Proceedings of the 13th Extended Semantic Web Conference (ESWC 2016).
* Géraud FOKOU, Stéphane JEAN, Allel HADJALI, Mickael BARON, _Handling failing RDF queries: from diagnosis to relaxation_, In Knowledge and Information Systems (KAIS),2016. [[http://link.springer.com/article/10.1007/s10115-016-0941-0]] 
* Géraud FOKOU, Stéphane JEAN, Allel HADJALI, Mickael BARON, _Cooperative Techniques for SPARQL Query Relaxation in RDF Databases_, In Proceedings of the 12th Extended Semantic Web Conference (ESWC 2015).
* Géraud FOKOU, Stéphane JEAN, Allel HADJALI, Mickael BARON, _Cooperative Techniques for SPARQL Query Relaxation in RDF Databases_, Technical Report available at "Report_MFS_XSS.pdf":http://www.lias-lab.fr/publications/16873/Report_MFS_XSS.pdf

## Software licence agreement

Details the license agreement of QaRS4UKB: [LICENSE](LICENSE)

## Historic Contributors (core developers first followed by alphabetical order)

* [Géraud FOKOU (core developer)](https://www.lias-lab.fr/members/geraudfokou/)
* [Mickael BARON](https://www.lias-lab.fr/members/mickaelbaron/)
* [Allel HADJALI](https://www.lias-lab.fr/members/allelhadjali/)
* [Stéphane JEAN](https://www.lias-lab.fr/members/stephanejean/)

## Code analysis

* Lines of Code: 7 253
* Programming Language: Java
