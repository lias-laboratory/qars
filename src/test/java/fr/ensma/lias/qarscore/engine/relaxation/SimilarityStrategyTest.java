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

import static org.junit.Assert.*;

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
import fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class SimilarityStrategyTest extends SessionTDBTest{

    private Session session;
    private Logger logger;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp(){
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

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown(){
	super.teardDown();
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#SimilarityStrategy(fr.ensma.lias.qarscore.engine.query.CQuery, fr.ensma.lias.qarscore.connection.Session)}.
     */
    @Test
    public void testSimilarityStrategy() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(1==relax_strategy.getRelaxed_queries().getSimilarity());
	Assert.assertTrue(relax_strategy.getRelaxed_queries().getRelaxedQuery().isEmpty());
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#relaxation_tree(fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree, fr.ensma.lias.qarscore.connection.Session)}.
     */
    @Test
    public void testRelaxation_tree() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(1==relax_strategy.getRelaxed_queries().getSimilarity());
	Assert.assertTrue(relax_strategy.getRelaxed_queries().getRelaxedQuery().isEmpty());
	relax_strategy.next_step();
	Assert.assertTrue(!relax_strategy.getRelaxed_queries().getRelaxedQuery().isEmpty());
	for(RelaxationTree child:relax_strategy.getRelaxed_queries().getRelaxedQuery()){
	    Assert.assertTrue(child.getRelaxedQuery().isEmpty());
	    Assert.assertTrue(1>=relax_strategy.getRelaxed_queries().getSimilarity());
	    logger.info(child.getQuery());
	    logger.info(child.getSimilarity());
	}
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#next_step()}.
     */
    @Test
    public void testNext_step() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#get_current_relaxed_query()}.
     */
    @Test
    public void testGet_level_relaxed_query() {
	fail("Not yet implemented"); // TODO
    }

}
