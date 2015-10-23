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
package fr.ensma.lias.qarscore.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class EDBT_Scenario_Test {

    private Session session;
    private QueryStatement queryStatement;

    /**
     * 
     */
    @Before
    public void setUp() {

	Properties.setOntoLang("OWL");
	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testDesignAndExecuteQuery() {

	queryStatement = session.createStatement(SPARQLQueriesSample.EDBT_QUERY_1);	
	ResultSet result = (ResultSet) queryStatement.executeQuery();
	Assert.assertNotNull(result);
	int i = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    for(String var:result.getResultVars()){
		 Logger.getRootLogger().info(
			    solution.get(var));
	    }
	    i++;
	}
	Logger.getRootLogger().info(i);
    }

    @Test
    public void testDesignAndExecuteRelaxedQuery() {

	queryStatement = session.createStatement(SPARQLQueriesSample.EDBT_QUERY_2);	
	ResultSet result = (ResultSet) queryStatement.executeQuery();
	
	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);

	OntClass class_to_relax = ((JenaSession)session).getOntology()
		.getOntClass(
			"http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#UndergraduateStudent");
	Assert.assertNotNull(class_to_relax);
	Logger.getRootLogger().info(class_to_relax);
	Map<String, String> relax_operator = new HashMap<String, String>();
	relax_operator.put(class_to_relax.getURI(), "SIB");
	
	Properties.setSimilarityStrategy();
	Map<ResultSet, Double> relaxed_result = queryStatement.relaxedQuery(relax_operator);
	
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

	queryStatement = session.createStatement(SPARQLQueriesSample.EDBT_QUERY_2);	
	ResultSet result = (ResultSet) queryStatement.executeQuery();

	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);

	Properties.setAutomaticStrategy(10);
	Map<ResultSet, Double> relaxed_result = queryStatement.relaxedQuery();
	
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
	
	queryStatement = session.createStatement(SPARQLQueriesSample.EDBT_QUERY_3);	
	ResultSet result = (ResultSet) queryStatement.executeQuery();
	
	Assert.assertNotNull(result);
	int number_answers = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
	    number_answers++;
	}
	Assert.assertTrue(number_answers == 0);

	Properties.setAutomaticStrategy(10);
	Map<ResultSet, Double> relaxed_result = queryStatement.relaxedQuery();
	
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
	
	queryStatement = session.createStatement(SPARQLQueriesSample.EDBT_QUERY_3);	
	ResultSet result = (ResultSet) queryStatement.executeQuery();
	
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

	 MFSSearch relaxationStrategy = StrategyFactory.getLatticeDFSStrategy(session);
	List<CQuery> all_mfs = relaxationStrategy.getAllMFS();
	for (CQuery mfs : all_mfs) {
	    Logger.getRootLogger().info(mfs.getSPARQLQuery().toString());
	}
	Logger.getRootLogger().info(all_mfs.size());
    }
}
