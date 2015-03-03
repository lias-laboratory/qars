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
package fr.ensma.lias.qarscore.engine.query;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;

/**
 * @author Geraud FOKOU
 */
public class CQueryTest {

    private Logger logger;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	logger = Logger.getRootLogger();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getElementList()}.
     */
    @Test
    public void testGetElementList() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	Assert.assertEquals(15, conjunctiveQuery.getElementList().size());
	for (CElement elt : conjunctiveQuery.getElementList()) {
	    Assert.assertTrue(elt.getElement() instanceof ElementPathBlock);
	    logger.info(((ElementPathBlock) elt.getElement()).getPattern()
		    .toString());
	}

	conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_18);
	Assert.assertEquals(9, conjunctiveQuery.getElementList().size());
	for (CElement elt : conjunctiveQuery.getElementList()) {
	    if (elt.getElement() instanceof ElementPathBlock) {
		logger.info(((ElementPathBlock) elt.getElement()).getPattern()
			.toString());
	    } else {
		logger.info(((ElementFilter) elt.getElement()).toString());
	    }
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getGroupList()}.
     */
    @Test
    public void testGetGroupList() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	Assert.assertEquals(1, conjunctiveQuery.getGroupList().size());

	conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_18);
	Assert.assertEquals(3, conjunctiveQuery.getGroupList().size());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getSelectedQueryVar()}.
     */
    @Test
    public void testGetSelectedQueryVar() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getSelectedQueryVarNames()}
     * .
     */
    @Test
    public void testGetSelectedQueryVarNames() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_18);
	Assert.assertEquals(2, conjunctiveQuery.getSelectedQueryVar().size());
	Assert.assertEquals(5, conjunctiveQuery.getMentionedQueryVar().size());
	logger.info(conjunctiveQuery.getSelectedQueryVarNames().toString());
	logger.info(conjunctiveQuery.getMentionedQueryVarNames().toString());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getMentionedQueryVar()}
     * .
     */
    @Test
    public void testGetMentionedQueryVar() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getMentionedQueryVarNames()}
     * .
     */
    @Test
    public void testGetMentionedQueryVarNames() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#isValidQuery()}.
     */
    @Test
    public void testIsValidQuery() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_18);
	Assert.assertEquals(9, conjunctiveQuery.getElementList().size());
	Assert.assertTrue(conjunctiveQuery.isValidQuery());
	conjunctiveQuery.getElementList().remove(2);
	Assert.assertFalse(conjunctiveQuery.isValidQuery());
	for (CElement elt : conjunctiveQuery.getElementList()) {
	    if (elt.getElement() instanceof ElementPathBlock) {
		logger.info(((ElementPathBlock) elt.getElement()).getPattern()
			.toString());
	    } else {
		logger.info(((ElementFilter) elt.getElement()).toString());
	    }
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getSPARQLQuery()}.
     */
    @Test
    public void testGetSPARQLQuery() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_18);
	Assert.assertEquals(9, conjunctiveQuery.getElementList().size());
	Assert.assertTrue(conjunctiveQuery.isValidQuery());
	logger.info(conjunctiveQuery.getSPARQLQuery());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#getNativeSPARQLQuery()}
     * .
     */
    @Test
    public void testGetNativeSPARQLQuery() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#isStarQuery()}.
     */
    @Test
    public void testIsStarQuery() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	Assert.assertTrue(conjunctiveQuery.isValidQuery());
	Assert.assertTrue(conjunctiveQuery.isStarQuery());
	for (CElement elt : conjunctiveQuery.getElementList()) {
	    if (elt.getElement() instanceof ElementPathBlock) {
		logger.info(((ElementPathBlock) elt.getElement()).getPattern()
			.toString());
	    } else {
		logger.info(((ElementFilter) elt.getElement()).toString());
	    }
	}

	conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	Assert.assertTrue(conjunctiveQuery.isValidQuery());
	Assert.assertTrue(!conjunctiveQuery.isStarQuery());
	for (CElement elt : conjunctiveQuery.getElementList()) {
	    if (elt.getElement() instanceof ElementPathBlock) {
		logger.info(((ElementPathBlock) elt.getElement()).getPattern()
			.toString());
	    } else {
		logger.info(((ElementFilter) elt.getElement()).toString());
	    }
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#isChainQuery()}.
     */
    @Test
    public void testIsChainQuery() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#isCartesianProduct()}.
     */
    @Test
    public void testIsCartesianProduct() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_2);
	Assert.assertTrue(conjunctiveQuery.isValidQuery());
	Assert.assertTrue(!conjunctiveQuery.isCartesianProduct());
	conjunctiveQuery.getElementList().remove(2);
	Assert.assertTrue(conjunctiveQuery.getElementList().size()==4);
	logger.info(conjunctiveQuery.getSPARQLQuery());
	logger.info(conjunctiveQuery.getNativeSPARQLQuery());
	Assert.assertTrue(conjunctiveQuery.isCartesianProduct());
	
	conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_4);
	Assert.assertTrue(conjunctiveQuery.isValidQuery());
	Assert.assertTrue(!conjunctiveQuery.isCartesianProduct());
	conjunctiveQuery.getElementList().remove(7);
	Assert.assertTrue(conjunctiveQuery.getElementList().size()==8);
	logger.info(conjunctiveQuery.getSPARQLQuery());
	logger.info(conjunctiveQuery.getNativeSPARQLQuery());
	Assert.assertTrue(conjunctiveQuery.isCartesianProduct());
	conjunctiveQuery.getElementList().remove(7);
	Assert.assertTrue(!conjunctiveQuery.isCartesianProduct());
	logger.info(conjunctiveQuery.getSPARQLQuery());
	logger.info(conjunctiveQuery.getNativeSPARQLQuery());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#isSubQueryOf(fr.ensma.lias.qarscore.engine.query.CQuery)}
     * .
     */
    @Test
    public void testIsSubQueryOf() {
	CQuery conjunctiveQuery21 = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_21);
	Assert.assertTrue(conjunctiveQuery21.isValidQuery());
	CQuery conjunctiveQuery19 = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_19);
	Assert.assertTrue(conjunctiveQuery19.isValidQuery());
	Assert.assertTrue(conjunctiveQuery21.isSuperQueryOf(conjunctiveQuery19));
	Assert.assertTrue(conjunctiveQuery19.isSubQueryOf(conjunctiveQuery21));
	CQuery conjunctiveQuery20 = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_20);
	Assert.assertTrue(conjunctiveQuery20.isValidQuery());
	Assert.assertTrue(conjunctiveQuery21.isSuperQueryOf(conjunctiveQuery20));
	Assert.assertTrue(conjunctiveQuery20.isSubQueryOf(conjunctiveQuery21));
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#replace(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testReplace() {
	
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#toString()}.
     */
    @Test
    public void testToString() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.query.CQuery#equals(java.lang.Object)}
     * .
     */
    @Test
    public void testEqualsObject() {
	fail("Not yet implemented"); // TODO
    }

}
