package fr.ensma.lias.qarscore;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.tdb.TDBFactory;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.properties.Properties;

public class SaturationTest extends InitTest{

    final static String PATH = "src/main/resources/Uni1.owl";
    final static String TDB_PATH = "src/test/resources/TDB/Uni1";
    final static String LUBM_PREFIX = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
	    + "PREFIX ub:   <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";

    final static String QUERY = LUBM_PREFIX + "SELECT ?X WHERE { "
	    + "?X rdf:type ub:Professor . " + "}";
 
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
	//TDBLoader.loadModel(dataset.getDefaultModel(), PATH);
	//dataModel.commit();
	//dataset.commit();

	QueryExecution query_exec = QueryExecutionFactory.create(QUERY,
		dataModel);

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info("Result without saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}

	dataModel = ModelFactory.createInfModel(
		ReasonerRegistry.getRDFSReasoner(), dataset.getDefaultModel());

	query_exec = QueryExecutionFactory.create(QUERY,
		dataModel);

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

	QueryExecution query_exec = QueryExecutionFactory.create(QUERY,
		((JenaSession)sessionJena).getModel());

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info("Result without saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}
    }

    @Test
    public void testsaturationwithsession() {
	
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(TDB_PATH);

	QueryExecution query_exec = QueryExecutionFactory.create(QUERY,
		((JenaSession)sessionJena).getModel());

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info("Result with saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}
    }

}
