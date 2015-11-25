package fr.ensma.lias.qarscore;

import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.properties.Properties;

public class SaturationTest extends InitTest {

    final static String PATH = "c:/resources/UBA/Uni1.owl";
    final static String TDB_PATH = "c:/TDB/UBA";
    final static String LUBM_PREFIX = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
	    + "PREFIX ub:   <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testwithoutsession() {
	Dataset dataset = TDBFactory.createDataset(TDB_PATH);
	Model dataModel = dataset.getDefaultModel();
	//TDBLoader.load(dataset, PATH);
	TDBLoader.loadModel(dataset.getDefaultModel(), PATH);
	dataModel.commit();
	// dataset.commit();

	QueryExecution query_exec = QueryExecutionFactory.create(SPARQLQueriesSample.WWW_QUERY_1,
		dataModel);

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info("Result without saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}

	dataModel = ModelFactory.createInfModel(
		ReasonerRegistry.getRDFSReasoner(), dataset.getDefaultModel());

	query_exec = QueryExecutionFactory.create(SPARQLQueriesSample.WWW_QUERY_3, dataModel);

	result = query_exec.execSelect();
	Logger.getRootLogger().info("Result with saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}
    }

    @Test
    public void testwithsessionwithoutsaturation() {

	Properties.setModelMemSpec(OntModelSpec.OWL_DL_MEM);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(TDB_PATH);

	QueryExecution query_exec = QueryExecutionFactory.create(SPARQLQueriesSample.WWW_QUERY_3,
		((JenaSession) sessionJena).getModel());

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info("Result without saturation");
	int num_answers = 0;
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	    num_answers = num_answers + 1;
	}
	 Logger.getRootLogger().info(num_answers);
    }

    @Test
    public void testsaturationwithsession() {

	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(TDB_PATH);

	QueryExecution query_exec = QueryExecutionFactory.create(SPARQLQueriesSample.WWW_QUERY_3,
		((JenaSession) sessionJena).getModel());

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info(query_exec.getQuery());
	int num_answers = 0;
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	    num_answers = num_answers + 1;
	}
	 Logger.getRootLogger().info(num_answers);
    }

    @Test
    public void executeStatisticQueryTest() {

	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(TDB_PATH);

	Map<String, String> allQueries = StatisticDataSetQueryTest
		.getAllQueries();
	for (String key : allQueries.keySet()) {
	    try {
		Logger.getRootLogger().info(key);
		Query query = QueryFactory.create(allQueries.get(key));
		List<String> varNames = query.getResultVars();
		QueryExecution qexec = QueryExecutionFactory.create(query,
			((JenaSession) sessionJena).getModel());
		try {
		    ResultSet results = qexec.execSelect();
		    while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			String solution = "";
			for (int i = 0; i < varNames.size() - 1; i++) {
			    solution = solution + varNames.get(i) + ":"
				    + soln.get(varNames.get(i)) + "; ";
			}
			solution = solution + varNames.get(varNames.size() - 1)
				+ ":"
				+ soln.get(varNames.get(varNames.size() - 1))
				+ "; ";
			Logger.getRootLogger().info(solution);
		    }
		} finally {
		}
	    } finally {
	    }
	}
    }
}
