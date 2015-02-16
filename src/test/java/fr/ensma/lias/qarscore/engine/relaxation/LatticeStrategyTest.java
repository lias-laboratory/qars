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
import fr.ensma.lias.qarscore.exception.NotYetImplementedException;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyTest extends SessionTDBTest {

    private Session session;
    RelaxationStrategies relaxationStrategy;
    Logger logger;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
	super.setUp();
	logger = Logger.getRootLogger();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
	super.teardDown();
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getAFailingCause(fr.ensma.lias.qarscore.engine.query.CQuery)}.
     */
    @Test
    public void testGetAFailingCause() {
	
	try {
	    CQuery conjunctiveQuery = CQueryFactory
	    	    .createCQuery(SPARQLQueriesSample.QUERY_14);
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	    CQuery oneCause = relaxationStrategy.getAFailingCause(conjunctiveQuery);
	    Assert.assertTrue(relaxationStrategy.isAFailingCause(oneCause));
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(oneCause));
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	
	try {
	    CQuery conjunctiveQuery = CQueryFactory
	        	    .createCQuery(SPARQLQueriesSample.QUERY_15);
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	    CQuery oneCause = relaxationStrategy.getAFailingCause(conjunctiveQuery);
	    Assert.assertFalse(oneCause.isValidQuery());
	    Assert.assertFalse(relaxationStrategy.isAFailingCause(oneCause));
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getFailingCauses(fr.ensma.lias.qarscore.engine.query.CQuery)}.
     */
    @Test
    public void testGetFailingCauses() {
	
	try {
	    CQuery conjunctiveQuery = CQueryFactory
	    	    .createCQuery(SPARQLQueriesSample.QUERY_14);
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	    List<CQuery> allCauses = relaxationStrategy.getFailingCauses(conjunctiveQuery);
	    Assert.assertTrue(allCauses.size()==1);
	    Assert.assertTrue(relaxationStrategy.isAFailingCause(allCauses.get(0)));
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(allCauses.get(0)));
	    for( CQuery cause: allCauses){
		System.out.println(cause.getSPARQLQuery());
	    }
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
	
	try {
	    CQuery conjunctiveQuery = CQueryFactory
	        	    .createCQuery(SPARQLQueriesSample.QUERY_15);
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	    List<CQuery> allCauses = relaxationStrategy.getFailingCauses(conjunctiveQuery);
	    Assert.assertNull(allCauses);
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}


    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getSuccessSubQueries()}.
     */
    @Test
    public void testGetSuccessSubQueries() {
	
	try {
	    CQuery conjunctiveQuery = CQueryFactory
	    	    .createCQuery(SPARQLQueriesSample.QUERY_6);
	    Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	    List<CQuery> allCauses = relaxationStrategy.getFailingCauses(conjunctiveQuery);
	    Assert.assertTrue(allCauses.size()==6);
	    for( CQuery cause: allCauses){
		Assert.assertTrue(relaxationStrategy.isAFailingCause(cause));
		Assert.assertTrue(!relaxationStrategy.hasLeastKAnswers(cause));
		System.out.println(cause.getSPARQLQuery());
	    }
	    List<CQuery> allSuccess = relaxationStrategy.getSuccessSubQueries();
	    Assert.assertTrue(allSuccess.size()==5);
	    for( CQuery success: allSuccess){
		Assert.assertTrue(!relaxationStrategy.isAFailingCause(success));
		Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(success));
		System.out.println(success.getSPARQLQuery());
	    }
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}

    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#hasLeastKAnswers(fr.ensma.lias.qarscore.engine.query.CQuery)}.
     */
    @Test
    public void testHasLeastKAnswers() {
	
	try {
	    CQuery conjunctiveQuery = CQueryFactory
	    	    .createCQuery(SPARQLQueriesSample.QUERY_17);
	    Assert.assertTrue(relaxationStrategy.hasLeastKAnswers(conjunctiveQuery));
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
    }
}
