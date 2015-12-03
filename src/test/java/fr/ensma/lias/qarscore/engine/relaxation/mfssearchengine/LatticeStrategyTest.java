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
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;

/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyTest extends InitTest{

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
     * test indicator
     */
    private void show_indicator(){
	logger.info("Time Duration of MFS Computation: "+((AbstractLatticeStrategy)relaxationStrategy).duration_of_execution);
	logger.info("Number of Executed queries: "+((AbstractLatticeStrategy)relaxationStrategy).number_of_query_executed);
	logger.info("Number of redundant queries: "+((AbstractLatticeStrategy)relaxationStrategy).number_of_query_reexecuted);
	logger.info("Number of Cartesian Product: "+((AbstractLatticeStrategy)relaxationStrategy).size_of_cartesian_product);
    }
    
    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.AbstractLatticeStrategy#getOneMFS(fr.ensma.lias.qarscore.engine.query.CQuery)}
     * .
     */
//    @Test
    public void testGetOneMFS() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_5);
	
	relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionSesame,
		conjunctiveQuery);

	show_indicator();
	
	Assert.assertTrue(!relaxationStrategy
		.hasLeastKAnswers(conjunctiveQuery));
	CQuery oneCause = relaxationStrategy.getOneMFS();
	Assert.assertTrue(relaxationStrategy.isMFS(oneCause));
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(oneCause));
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.AbstractLatticeStrategy#getAllMFS(fr.ensma.lias.qarscore.engine.query.CQuery)}
     * .
     */
    @Test
    public void testGetAllMFS() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);
	
	relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionJena,
		conjunctiveQuery);

	show_indicator();
	
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers());
	
	List<CQuery> allCauses = relaxationStrategy.getAllMFS();
	
	Assert.assertTrue(relaxationStrategy.isMFS(allCauses.get(0)));
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(allCauses.get(0)));
	for (CQuery cause : allCauses) {
	    Assert.assertTrue(relaxationStrategy.isMFS(cause));
	    logger.info(cause.getSPARQLQuery());
	}
	
	List<CQuery> allSuccess = relaxationStrategy.getAllXSS();
	for (CQuery success : allSuccess) {
	    Assert.assertTrue(!relaxationStrategy.isMFS(success));
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(success));
	    logger.info(success.getSPARQLQuery());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.AbstractLatticeStrategy#getAllXSS()}
     * .
     */
//    @Test
    public void testGetAllXSS() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_6);
	relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionJena,
		conjunctiveQuery);
	Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers());
	List<CQuery> allCauses = relaxationStrategy.getAllMFS();
	Assert.assertEquals(7, allCauses.size());
	for (CQuery cause : allCauses) {
	    Assert.assertTrue(relaxationStrategy.isMFS(cause));
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(cause));
	    logger.info(cause.getSPARQLQuery());
	}
	List<CQuery> allSuccess = relaxationStrategy.getAllXSS();
	Assert.assertEquals(12, allSuccess.size());
	for (CQuery success : allSuccess) {
	    Assert.assertTrue(!relaxationStrategy.isMFS(success));
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(success));
	    logger.info(success.getSPARQLQuery());
	}
    }
    
//    @Test
    public void testHasLeastKAnswers() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_17);
	relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionJena,
		conjunctiveQuery);
	Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
    }

 //   @Test
    public void testTraceParameter() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_6);
	relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionJena,
		conjunctiveQuery);
	
	show_indicator();
    }

 //   @Test
    public void testTimePerformance() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_4);
	
	relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionJena,
		conjunctiveQuery);
	
	long entire_duration = 0;
	for (int i = 0; i < 5; i++) {
	    conjunctiveQuery = CQueryFactory
		    .createCQuery(SPARQLQueriesSample.QUERY_4);
	
	    relaxationStrategy = StrategyFactory.getLatticeStrategy(sessionJena,
		    conjunctiveQuery);
	    entire_duration = entire_duration + ((AbstractLatticeStrategy)relaxationStrategy).duration_of_execution;
	}
	
	show_indicator();
	logger.info(entire_duration);
    }

}
