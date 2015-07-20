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
package fr.ensma.lias.qarscore.statement;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.RelaxationTree;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class StatementTest extends SessionTDBTest {

    private Session session;
    private Statement queryStatement;

    /*
     * (non-Javadoc)
     * 
     * @see fr.ensma.lias.qarscore.connection.SessionTDBTest#setUp()
     */
    @Before
    public void setUp() {
	super.setUp();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
	queryStatement = StatementFactory.getStatement(session);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
	super.teardDown();
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#StatementImpl(fr.ensma.lias.qarscore.connection.Session)}
     * .
     */
    @Test
    public void testStatementImpl() {
	Assert.assertNotNull(queryStatement);
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#getSession()}
     * .
     */
    @Test
    public void testGetSession() {
	Statement queryStatement1 = StatementFactory.getStatement(session);
	Assert.assertNotNull(queryStatement1);
	Assert.assertEquals(session, queryStatement1.getSession());
	Assert.assertEquals(session, queryStatement.getSession());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#getQuery()}
     * .
     */
    @Test
    public void testGetQuery() {
	Assert.assertNull(queryStatement.getQuery());
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	queryStatement.executeSPARQLQuery(conjunctiveQuery.toString());
	Assert.assertTrue(conjunctiveQuery.toString().equalsIgnoreCase(
		queryStatement.getQuery()));
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#executeSPARQLQuery(java.lang.String)}
     * .
     */
    @Test
    public void testExecuteSPARQLQuery() {

	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.QUERY_16);
	Assert.assertNotNull(result);
	int i = 0;
	while (result.hasNext()) {
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(
		    solution.get(result.getResultVars().get(0)));
//	    Logger.getRootLogger().info(
//		    solution.get(result.getResultVars().get(1)));
	    i++;
	}

	Assert.assertTrue(!result.hasNext());
	Assert.assertTrue(i == result.getRowNumber());
	Logger.getRootLogger().info(result.getRowNumber());

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#explainFailure(java.lang.String, boolean)}
     * .
     */
    @Test
    public void testExplainFailure() {

	queryStatement.explainFailure(SPARQLQueriesSample.QUERY_4, true);
	List<String> all_mfs = queryStatement.getFailingCause();
	Assert.assertEquals(2, all_mfs.size());
	for (String mfs : all_mfs) {
	    Logger.getRootLogger().info(mfs);
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#getFailingCause()}
     * .
     */
    @Test
    public void testGetFailingCause() {

	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.QUERY_22);
	Assert.assertTrue(result.getRowNumber() == 0);
	queryStatement.explainFailure(SPARQLQueriesSample.QUERY_22, false);
	List<String> all_mfs = queryStatement.getFailingCause();
	Assert.assertEquals(1, all_mfs.size());
	for (String mfs : all_mfs) {
	    Logger.getRootLogger().info(mfs);
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#getMaxSuccessQuery()}
     * .
     */
    @Test
    public void testGetMaxSuccessQuery() {

	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.QUERY_22);
	Assert.assertTrue(result.getRowNumber() == 0);
	queryStatement.explainFailure(SPARQLQueriesSample.QUERY_5, true);
	List<String> all_xss = queryStatement.getMaxSuccessQuery();
	Assert.assertEquals(1, all_xss.size());
	for (String xss : all_xss) {
	    Logger.getRootLogger().info(xss);
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#executeRelaxedQuery(java.lang.String)}
     * .
     */
    @Test
    public void testExecuteRelaxedQuery() {
	// TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#automaticRelaxation(java.lang.String, int)}
     * .
     */
    @Test
    public void testAutomaticRelaxation() {
	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.QUERY_23);
	Assert.assertTrue(result.getRowNumber() == 0);
	Map<ResultSet, Double> all_result = queryStatement.automaticRelaxation(
		SPARQLQueriesSample.QUERY_23, 10);
	//Assert.assertEquals(1, all_result.keySet().size());
	for (ResultSet res : all_result.keySet()) {
	    //Assert.assertEquals(1, all_result.get(res).doubleValue(), 0.001);
	    Logger.getRootLogger().info(all_result.get(res).doubleValue());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.statement.implementation.StatementImpl#getRelaxationPlan()}
     * .
     */
    @Test
    public void testGetRelaxationPlan() {
	ResultSet result = queryStatement
		.executeSPARQLQuery(SPARQLQueriesSample.QUERY_3);
	Assert.assertTrue(result.getRowNumber() == 0);
	Map<ResultSet, Double> all_result = queryStatement.automaticRelaxation(
		SPARQLQueriesSample.QUERY_3, 20);

	for(ResultSet res:all_result.keySet()){
	    Logger.getRootLogger().info(all_result.get(res));
	}
	
	RelaxationTree execution_plan = queryStatement.getRelaxationPlan();
	Logger.getRootLogger().info(execution_plan.getQuery().toString());
	Logger.getRootLogger().info(execution_plan.getSimilarity());
	
    }
}
