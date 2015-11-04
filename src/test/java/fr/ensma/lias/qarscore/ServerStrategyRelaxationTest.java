/*********************************************************************************
 * This file is part of QARS Project.
 * Copyright (C) 2015  LIAS - ENSMA
 *   Teleport 2 - 1 avenue Clement Ader
 *   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
 * 
 * QARS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QARS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with QARS.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
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
import org.apache.jena.tdb.TDBLoader;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.GraphRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.HuangRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.MFSRelaxationGraph;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class ServerStrategyRelaxationTest extends InitTest {

    // final static String PATH = "c:/resources/UBA/Uni1.owl";
    final static String TDB_PATH = "/home/lias/tdb1repository";
    // final static String TDB_PATH = "C:/TDB/UBA";
    final static String LUBM_PREFIX = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
	    + "PREFIX ub:   <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";

    final static String QUERY = LUBM_PREFIX + "SELECT ?X WHERE { "
	    + "?X rdf:type ub:Professor . " + "}";

    private final int TOP_K = 10;

    /*
     * (non-Javadoc)
     * 
     * @see fr.ensma.lias.qarscore.InitTest#setUp()
     */
    @Before
    public void setUp() {
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(TDB_PATH);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ensma.lias.qarscore.InitTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

   // @Test
    public void testsaturationwithoutsession() {
	
	final String PATH = "/home/lias/Uni1.owl";
	final String TDB_PATH = "/home/lias/tdb1repository";

	Dataset dataset = TDBFactory.createDataset(TDB_PATH);
	Model dataModel = dataset.getDefaultModel();
	// TDBLoader.load(dataset, PATH);
	TDBLoader.loadModel(dataset.getDefaultModel(), PATH);
	dataModel.commit();
	// dataset.commit();

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

	query_exec = QueryExecutionFactory.create(QUERY, dataModel);

	result = query_exec.execSelect();
	Logger.getRootLogger().info("Result with saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}

    }

    @Test
    public void testsaturationwithsession() {

	QueryExecution query_exec = QueryExecutionFactory.create(QUERY,
		((JenaSession) sessionJena).getModel());

	ResultSet result = query_exec.execSelect();
	Logger.getRootLogger().info("Result with saturation");
	while (result.hasNext()) {
	    QuerySolution sol = result.next();
	    Logger.getRootLogger().info(sol.get("X"));
	}
    }

    // @Test
    public void testRelaxationWithHuangStrategy() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);

	HuangRelaxationStrategy relaxed_query = new HuangRelaxationStrategy(
		conjunctiveQuery, sessionJena);
	boolean hasTopk = false;
	int number_answers = 0;
	while ((!hasTopk) && (relaxed_query.hasNext())) {
	    QueryStatement stm = sessionJena.createStatement(relaxed_query
		    .next().toString());
	    number_answers = number_answers + stm.getResultSetSize();
	    Logger.getRootLogger().info(
		    relaxed_query.getCurrent_relaxed_query().toString() + " "
			    + relaxed_query.getCurrent_similarity() + " "
			    + relaxed_query.getCurrent_level() + " "
			    + number_answers);
	    hasTopk = number_answers >= TOP_K;
	}
    }

    // @Test
    public void testRelaxationWithGraphStrategy() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);

	GraphRelaxationStrategy relaxed_query = new GraphRelaxationStrategy(
		conjunctiveQuery, sessionJena);
	boolean hasTopk = false;
	int number_answers = 0;
	while ((!hasTopk) && (relaxed_query.hasNext())) {
	    QueryStatement stm = sessionJena.createStatement(relaxed_query
		    .next().toString());
	    number_answers = number_answers + stm.getResultSetSize();
	    Logger.getRootLogger().info(
		    relaxed_query.getCurrent_relaxed_query().toString() + " "
			    + relaxed_query.getCurrent_similarity() + " "
			    + relaxed_query.getCurrent_level() + " "
			    + number_answers);
	    hasTopk = number_answers >= TOP_K;
	}
    }

    // @Test
    public void testRelaxationWithMFSStrategy() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);

	MFSRelaxationGraph relaxed_query = new MFSRelaxationGraph(
		conjunctiveQuery, sessionJena);
	boolean hasTopk = false;
	int number_answers = 0;
	while ((!hasTopk) && (relaxed_query.hasNext())) {
	    QueryStatement stm = sessionJena.createStatement(relaxed_query
		    .next().toString());
	    number_answers = number_answers + stm.getResultSetSize();
	    Logger.getRootLogger().info(
		    relaxed_query.getCurrent_relaxed_query().toString() + " "
			    + relaxed_query.getCurrent_similarity() + " "
			    + relaxed_query.getCurrent_level() + " "
			    + number_answers);
	    hasTopk = number_answers >= TOP_K;
	}
    }

}
