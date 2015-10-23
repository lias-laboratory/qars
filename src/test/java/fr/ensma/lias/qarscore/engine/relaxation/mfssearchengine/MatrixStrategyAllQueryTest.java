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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;

/**
 * @author Geraud FOKOU
 */
public class MatrixStrategyAllQueryTest extends InitTest {

    private MFSSearch relaxationStrategy;
    private Logger logger;

    @Before
    public void setUp() {

	super.setUp();
	logger = Logger.getRootLogger();
   }

    @After
    public void tearDown() throws Exception {
	super.tearDown();
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.MatrixStrategyAllQuery#hasLeastKAnswers()}
     * .
     */
    @Test
    public void testHasLeastKAnswers() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_21);
	relaxationStrategy = StrategyFactory.getMatrixStrategy(sessionJena,
		conjunctiveQuery);
	Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));

	Assert.assertTrue(!relaxationStrategy.isMFS());

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.MatrixStrategyAllQuery#getOneMFS()}
     * .
     */
    @Test
    public void testGetOneMFS() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_14);
	relaxationStrategy = StrategyFactory.getMatrixStrategy(sessionJena,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy
		.hasLeastKAnswers(conjunctiveQuery));
	Assert.assertTrue(!relaxationStrategy.isMFS());
	CQuery cause = relaxationStrategy.getOneMFS();
	Assert.assertNotNull(cause);
	logger.info(cause.getSPARQLQuery().toString());
	
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.MatrixStrategyAllQuery#getAllMFS()}
     * .
     */
    @Test
    public void testGetAllMFS() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_14);
	relaxationStrategy = StrategyFactory.getMatrixStrategy(sessionJena,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers());
	List<CQuery> allCauses = relaxationStrategy.getAllMFS();
	Assert.assertTrue(allCauses.size() == 1);
	for (CQuery cause : allCauses) {
	    logger.info(cause.getSPARQLQuery());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.MatrixStrategyAllQuery#getAllXSS()}
     * .
     */
    @Test
    public void testGetAllXSS() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_14);
	relaxationStrategy = StrategyFactory.getMatrixStrategy(sessionJena,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers());
	List<CQuery> allSuccess = relaxationStrategy.getAllXSS();
	Assert.assertTrue(allSuccess.size() == 1);
	for (CQuery success : allSuccess) {
	    logger.info(success.getSPARQLQuery());
	}
    }
}
