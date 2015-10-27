package fr.ensma.lias.qarscore;

import com.hp.hpl.jena.ontology.OntModelSpec;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.properties.Properties;

public class Test {
    
    public String tdbPath = "/Users/baronm/Public/tdb100repository";
    
    public Session sessionJena;
    
//    private final int TOP_K = 10;
    
    public static String LUBM_PREFIX = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
	    + "PREFIX ub:   <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";
    
    public static String QUERY_1 = LUBM_PREFIX + "SELECT ?X WHERE { "
	    + "?X rdf:type ub:VisitingProfessor . " + "}";
    
    public static String queryTest = "SELECT * WHERE { ?s ?p ?o } LIMIT 100";
    
    public Test() {
	Properties.setModelMemSpec(OntModelSpec.OWL_DL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(tdbPath);
	
//	CQuery conjunctiveQuery = CQueryFactory
//		.createCQuery(QUERY_1);
		
	QueryStatement stm = sessionJena.createStatement(queryTest);
	stm.executeQuery();
	
//	HuangRelaxationStrategy relaxed_query = new HuangRelaxationStrategy(conjunctiveQuery, sessionJena);
//	boolean hasTopk =false;
//	int number_answers = 0;
//	while ((!hasTopk)&&(relaxed_query.hasNext())){
//	    QueryStatement stm = sessionJena.createStatement(relaxed_query.next().toString());
//	    number_answers = number_answers + stm.getResultSetSize();
//	    Logger.getRootLogger().info(relaxed_query.getCurrent_relaxed_query().toString()+" "+relaxed_query.getCurrent_similarity()+" "+relaxed_query.getCurrent_level()+" "+number_answers);
//	    hasTopk = number_answers >= TOP_K;
//	}
	
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    public static void main(String[] args) {
	new Test();
    }
}
