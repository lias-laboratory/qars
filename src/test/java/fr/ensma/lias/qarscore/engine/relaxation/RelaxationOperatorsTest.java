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
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.OperatorsFactory;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class RelaxationOperatorsTest extends SessionTDBTest {

    private Session session;
    private Logger logger;
    private RelaxationOperators relaxOperator;

    @Before
    public void setUp() {
	super.setUp();
	logger = Logger.getRootLogger();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
	relaxOperator = OperatorsFactory.createOperator(session);
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
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#specialization(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node, int)}
     * .
     */
    @Test
    public void testSpecializationCQueryNodeInt() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_7);

	ElementPathBlock element = (ElementPathBlock) conjunctiveQuery
		.getElementList().get(0).getElement();
	Map<CQuery, Integer> relaxQueries = relaxOperator.generalize(
		conjunctiveQuery, element.getPattern().getList().get(0)
			.getObject(), -2);
	Assert.assertNotNull(relaxQueries);
	Assert.assertEquals(2, relaxQueries.size());
	for (CQuery query : relaxQueries.keySet()) {
	    logger.info(query.getSPARQLQuery());
	    logger.info(relaxQueries.get(query));
	    Assert.assertEquals(-1, relaxQueries.get(query).intValue());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#specialization(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testSpecializationCQueryNode() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_17);

	ElementPathBlock element = (ElementPathBlock) conjunctiveQuery
		.getElementList().get(0).getElement();
	Map<CQuery, Integer> relaxQueries = ((RelaxationOperatorsImpl) relaxOperator)
		.specialization(conjunctiveQuery, element.getPattern()
			.getList().get(0).getObject());
	Assert.assertNotNull(relaxQueries);
	Assert.assertEquals(9, relaxQueries.size());
	for (CQuery query : relaxQueries.keySet()) {
	    logger.info(query.getSPARQLQuery());
	    logger.info(relaxQueries.get(query));
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#generalization(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node, int)}
     * .
     */
    @Test
    public void testGeneralizationCQueryNodeInt() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);

	ElementPathBlock element = (ElementPathBlock) conjunctiveQuery
		.getElementList().get(0).getElement();
	Map<CQuery, Integer> relaxQueries = relaxOperator.generalize(
		conjunctiveQuery, element.getPattern().getList().get(0)
			.getObject(), 2);
	Assert.assertNotNull(relaxQueries);
	Assert.assertEquals(2, relaxQueries.size());
	for (CQuery query : relaxQueries.keySet()) {
	    logger.info(query.getSPARQLQuery());
	    logger.info(relaxQueries.get(query));
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#generalization(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testGeneralizationCQueryNode() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);

	ElementPathBlock element = (ElementPathBlock) conjunctiveQuery
		.getElementList().get(0).getElement();
	Map<CQuery, Integer> relaxQueries = relaxOperator.generalize(
		conjunctiveQuery, element.getPattern().getList().get(0)
			.getObject());
	Assert.assertNotNull(relaxQueries);
	Assert.assertEquals(3, relaxQueries.size());
	for (CQuery query : relaxQueries.keySet()) {
	    logger.info(query.getSPARQLQuery());
	    logger.info(relaxQueries.get(query));
	}

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#generalize(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node, int)}
     * .
     */
    @Test
    public void testGeneralizeCQueryNodeInt() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_7);

	ElementPathBlock element = (ElementPathBlock) conjunctiveQuery
		.getElementList().get(0).getElement();
	Map<CQuery, Integer> relaxQueries = relaxOperator.generalize(
		conjunctiveQuery, element.getPattern().getList().get(0)
			.getObject(), 2);
	Assert.assertNotNull(relaxQueries);
	Assert.assertEquals(1, relaxQueries.size());
	for (CQuery query : relaxQueries.keySet()) {
	    logger.info(query.getSPARQLQuery());
	    logger.info(relaxQueries.get(query));
	}

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#sibling(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testSibling() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_8);

	ElementPathBlock element = (ElementPathBlock) conjunctiveQuery
		.getElementList().get(0).getElement();
	List<CQuery> relaxQueries = relaxOperator.sibling(conjunctiveQuery, element.getPattern().getList().get(0).getObject());
	Assert.assertNotNull(relaxQueries);
	Assert.assertEquals(6, relaxQueries.size());
	for (CQuery query : relaxQueries) {
	    logger.info(query.getSPARQLQuery());
	}

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#relaxValue(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testRelaxValue() {
	// TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#releaseValue(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testReleaseValue() {
	 // TODO
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.RelaxationOperatorsImpl#releaseJoin(fr.ensma.lias.qarscore.engine.query.CQuery, com.hp.hpl.jena.graph.Node)}
     * .
     */
    @Test
    public void testReleaseJoin() {
	// TODO
    }

}
