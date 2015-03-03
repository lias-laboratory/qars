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
package fr.ensma.lias.qarscore.engine.relaxation;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.StrategiesFactory;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyTest extends SessionTDBTest {

    private Session session;
    private RelaxationStrategies relaxationStrategy;
    private Logger logger;

    @Before
    public void setUp() {
	super.setUp();
	logger = Logger.getRootLogger();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	Assert.assertNotNull(session.getBaseModel());
    }

    @After
    public void tearDown() {
	super.teardDown();
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getOneMFS(fr.ensma.lias.qarscore.engine.query.CQuery)}
     * .
     */
    @Test
    public void testGetOneMFS() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_14);
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy
		.hasLeastKAnswers(conjunctiveQuery));
	CQuery oneCause = relaxationStrategy.getOneMFS();
	Assert.assertTrue(relaxationStrategy.isMFS(oneCause));
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(oneCause));

	conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_15);
	Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	oneCause = relaxationStrategy.getOneMFS(conjunctiveQuery);
	Assert.assertFalse(oneCause.isValidQuery());
	Assert.assertFalse(relaxationStrategy.isMFS(oneCause));
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getAllMFS(fr.ensma.lias.qarscore.engine.query.CQuery)}
     * .
     */
    @Test
    public void testGetAllMFS() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers());
	List<CQuery> allCauses = relaxationStrategy.getAllMFS();
	Assert.assertTrue(allCauses.size() == 14);
	Assert.assertTrue(relaxationStrategy.isMFS(allCauses.get(0)));
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(allCauses.get(0)));
	for (CQuery cause : allCauses) {
	    Assert.assertTrue(relaxationStrategy.isMFS(cause));
	    logger.info(cause.getSPARQLQuery());
	}
	List<CQuery> allSuccess = relaxationStrategy.getAllXSS();
	Assert.assertTrue(allSuccess.size() == 4);
	for (CQuery success : allSuccess) {
	    Assert.assertTrue(!relaxationStrategy.isMFS(success));
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(success));
	    logger.info(success.getSPARQLQuery());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getAllXSS()}
     * .
     */
    @Test
    public void testGetAllXSS() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_6);
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers());
	List<CQuery> allCauses = relaxationStrategy.getAllMFS();
	Assert.assertTrue(allCauses.size() == 6);
	for (CQuery cause : allCauses) {
	    Assert.assertTrue(relaxationStrategy.isMFS(cause));
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(cause));
	    logger.info(cause.getSPARQLQuery());
	}
	List<CQuery> allSuccess = relaxationStrategy.getAllXSS();
	Assert.assertTrue(allSuccess.size() == 5);
	for (CQuery success : allSuccess) {
	    Assert.assertTrue(!relaxationStrategy.isMFS(success));
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(success));
	    logger.info(success.getSPARQLQuery());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#hasLeastKAnswers(fr.ensma.lias.qarscore.engine.query.CQuery)}
     * .
     */
    @Test
    public void testHasLeastKAnswers() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_17);
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session,
		conjunctiveQuery);
	Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
    }
}
