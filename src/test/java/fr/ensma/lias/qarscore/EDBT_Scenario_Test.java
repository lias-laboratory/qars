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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.properties.Properties;
import fr.ensma.lias.qarscore.statement.Statement;
import fr.ensma.lias.qarscore.statement.StatementFactory;

/**
 * @author Geraud FOKOU
 */
public class EDBT_Scenario_Test extends SessionTDBTest {

    private Session session;
    private Statement queryStatement;

    /**
     * 
     */
    @Before
    public void setUp() {
	super.setUp();

	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
	queryStatement = StatementFactory.getStatement(session);

    }

    /**
     *
     */
    @After
    public void tearDown() {
	super.teardDown();
    }

    @Test
    public void testDesignAndExecuteQuery() {

	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.EDBT_QUERY_1);
	Assert.assertNotNull(result);
	int i = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(1)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(2)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(3)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(4)));

	    i++;
	}

	Logger.getRootLogger().info(i);
    }

    @Test
    public void testDesignAndExecuteRelaxedQuery() {

	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.EDBT_QUERY_2);
	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);

	OntClass class_to_relax = session
		.getOntologyModel()
		.getOntClass(
			"http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#UndergraduateStudent");
	Assert.assertNotNull(class_to_relax);
	Logger.getRootLogger().info(class_to_relax);
	Map<String, String> relax_operator = new HashMap<String, String>();
	relax_operator.put(class_to_relax.getURI(), "SIB");
	Map<ResultSet, Double> relaxed_result = queryStatement
		.executeRelaxedQuery(SPARQLQueriesSample.EDBT_QUERY_2,
			relax_operator);
	Assert.assertNotNull(relaxed_result);
	Assert.assertTrue(relaxed_result.entrySet().size() == 1);
	for (ResultSet one_result : relaxed_result.keySet()) {
	    number_answers = 0;
	    while (one_result.hasNext()) {
		QuerySolution solution = one_result.next();
		Logger.getRootLogger().info(
			solution.get(one_result.getResultVars().get(0)));
		number_answers++;
	    }
	    Logger.getRootLogger().info(relaxed_result.get(one_result));
	}
    }

    @Test
    public void testAutomaticRelaxationQueryWithResult() {

	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.EDBT_QUERY_2);
	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);

	Map<ResultSet, Double> relaxed_result = queryStatement
		.automaticRelaxation(SPARQLQueriesSample.EDBT_QUERY_2, 10);
	Assert.assertNotNull(relaxed_result);
	Assert.assertTrue(relaxed_result.entrySet().size() == 14);
	for (ResultSet one_result : relaxed_result.keySet()) {
	    number_answers = 0;
	    while (one_result.hasNext()) {
		QuerySolution solution = one_result.next();
		Logger.getRootLogger().info(
			solution.get(one_result.getResultVars().get(0)));
		number_answers++;
	    }
	    Logger.getRootLogger().info(relaxed_result.get(one_result));
	}
    }

    @Test
    public void testAutomaticRelaxationQueryWithoutResult() {
	
	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.EDBT_QUERY_3);
	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);

	Map<ResultSet, Double> relaxed_result = queryStatement
		.automaticRelaxation(SPARQLQueriesSample.EDBT_QUERY_3, 10);
	Assert.assertNotNull(relaxed_result);
	Assert.assertTrue(relaxed_result.entrySet().size() == 14);
	for (ResultSet one_result : relaxed_result.keySet()) {
	    number_answers = 0;
	    while (one_result.hasNext()) {
		QuerySolution solution = one_result.next();
		Logger.getRootLogger().info(
			solution.get(one_result.getResultVars().get(0)));
		number_answers++;
	    }
	    Logger.getRootLogger().info(relaxed_result.get(one_result));
	}
    }

    @Test
    public void testDesignAndExecuteFailQuery() {
	Logger.getRootLogger().info(SPARQLQueriesSample.EDBT_QUERY_3);
	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.EDBT_QUERY_3);
	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(1)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(2)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(3)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(4)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(5)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(6)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(7)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(8)));
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(9)));

	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);
	Logger.getRootLogger().info(number_answers);
	queryStatement.explainFailure(SPARQLQueriesSample.EDBT_QUERY_3, true);
	List<String> all_mfs = queryStatement.getFailingCause();
	for (String mfs : all_mfs) {
	    Logger.getRootLogger().info(mfs);
	}
	Logger.getRootLogger().info(all_mfs.size());
    }
}
