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
public class SimilarityStrategyTest extends SessionTDBTest {

    private Session session;
    private Logger logger;

    /*
     * (non-Javadoc)
     * 
     * @see fr.ensma.lias.qarscore.connection.SessionTDBTest#setUp()
     */
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

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

	super.teardDown();
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#SimilarityStrategy(fr.ensma.lias.qarscore.engine.query.CQuery, fr.ensma.lias.qarscore.connection.Session)}
     * .
     */
    @Test
    public void testSimilarityStrategy() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(1 == relax_strategy.getRelaxed_queries_graph()
		.getSimilarity());
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#relaxation_tree(fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree, fr.ensma.lias.qarscore.connection.Session)}
     * .
     */
    @Test
    public void testRelaxation_tree() {

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_13);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(1 == relax_strategy.getRelaxed_queries_graph()
		.getSimilarity());
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());
	relax_strategy.next_step();
	Assert.assertTrue(!relax_strategy.getRelaxed_queries_graph()
		.getRelaxed_query().isEmpty());
	
	for (int i = 0; i< relax_strategy.getRelaxed_queries_graph()
		.getRelaxed_query().size(); i++) {
	    
	    RelaxationTree child = relax_strategy.getRelaxed_queries_graph().getRelaxed_query().get(i);
	    Assert.assertTrue(child.getRelaxed_query().isEmpty());
	    Assert.assertTrue(1 >= relax_strategy.getRelaxed_queries_graph()
		    .getSimilarity());
	    logger.info(child.getQuery());
	    logger.info(child.getSimilarity());
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#next_step()}
     * .
     */
    @Test
    public void testNext_step() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_16);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.size() == 2);
	
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 2);
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 3);
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 4);
	Assert.assertTrue(relax_strategy.next_step());
	
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 4);
	logger.info(relax_strategy.get_last_relaxed_queries().size());
	Assert.assertTrue(!relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 4);
	
	show_tree(relax_strategy.getRelaxed_queries_graph());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#next_gen_relax(java.lang.String)}
     * .
     */
    @Test
    public void testNext_gen_relax() {

	String uri = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#VisitingProfessor";
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_5);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());

	Assert.assertTrue(relax_strategy.next_gen_relax(uri, 1));
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.size() == 1);
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 1);

	relax_strategy = new SimilarityStrategy(conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());

	Assert.assertTrue(relax_strategy.next_gen_relax(uri, 3));
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 3);
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 3);
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#next_sib_relax(java.lang.String)}
     * .
     */
    @Test
    public void testNext_sib_relax() {

	String uri = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#VisitingProfessor";
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_5);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());

	Assert.assertTrue(relax_strategy.next_sib_relax(uri));
	logger.info(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.size());
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.size() == 5);
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size() == 5);

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#get_root_query()}
     * .
     */
    @Test
    public void testGet_root_query() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_16);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query().size()==2);
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size()==2);
	Assert.assertNull(relax_strategy.getRelaxed_queries_graph().getSource_query());
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#get_last_relaxed_queries()}
     * .
     */
    @Test
    public void testGet_leaf_queries() {
    }

    private void show_tree(RelaxationTree temp_tree) {

	if (temp_tree == null) {
	    return;
	}
	logger.info(temp_tree.getQuery());
	logger.info(temp_tree.getSimilarity());
	for(int i=0; i<temp_tree.getRelaxed_query().size(); i++){
	    show_tree(temp_tree.getRelaxed_query().get(i));
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy#getRelaxed_queries_graph()}
     * .
     */
    @Test
    public void testGetRelaxed_queries() {
	
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_16);
	SimilarityStrategy relax_strategy = new SimilarityStrategy(
		conjunctiveQuery, session);
	Assert.assertNotNull(relax_strategy);
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query()
		.isEmpty());
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.getRelaxed_queries_graph().getRelaxed_query().size()==2);
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size()==2);
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size()==3);
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size()==4);
	Assert.assertTrue(relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size()==4);
	Assert.assertTrue(!relax_strategy.next_step());
	Assert.assertTrue(relax_strategy.get_last_relaxed_queries().size()==4);

	show_tree(relax_strategy.getRelaxed_queries_graph());
    }
}
