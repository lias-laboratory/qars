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

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;
import fr.ensma.lias.qarscore.testqueries.SPARQLQueriesSample;

/**
 * @author Geraud FOKOU
 */
public class TripleRelaxationTest extends InitTest {

	@Before
	public void setUp() {
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation#TripleRelaxation(com.hp.hpl.jena.sparql.core.TriplePath, fr.ensma.lias.qarscore.connection.Session)}
	 * .
	 */
	@Test
	public void testTripleRelaxation() {
		CQuery conjunctiveQuery = CQueryFactory.createCQuery(SPARQLQueriesSample.QUERY_1);

		TripleRelaxation relax_triple = new TripleRelaxation(conjunctiveQuery.getElementList().get(0), session);

		Assert.assertNotNull(relax_triple);
		Assert.assertNotNull(relax_triple.getSubject_var());
		Assert.assertTrue(relax_triple.getSubject_var().getName().equals("X"));
		Assert.assertNull(relax_triple.getObject_var());
		Assert.assertNull(relax_triple.getPredicat_var());
		Assert.assertNull(relax_triple.getRelaxed_subject());
		Assert.assertEquals(5, relax_triple.getRelaxed_object().size());
		Assert.assertEquals(2, relax_triple.getRelaxed_predicat().size());
		Assert.assertEquals(10, relax_triple.getRelaxed_triple().size());
		while (relax_triple.hasNext()) {
			NodeRelaxed triple = relax_triple.next_relaxed_triple();
			Logger.getRootLogger().info(triple.getNode_1() + " " + triple.getNode_2() + " " + triple.getNode_3() + " :"
					+ triple.getSimilarity() + " :" + triple.getRelaxation_level());
		}
	}

	@Test
	public void testTripleRelaxationWithOrder() {
		CQuery conjunctiveQuery = CQueryFactory.createCQuery(SPARQLQueriesSample.QUERY_1);

		TripleRelaxation relax_triple = new TripleRelaxation(conjunctiveQuery.getElementList().get(0), session, 1);

		Assert.assertNotNull(relax_triple);
		Assert.assertNotNull(relax_triple.getSubject_var());
		Assert.assertTrue(relax_triple.getSubject_var().getName().equals("X"));
		Assert.assertNull(relax_triple.getObject_var());
		Assert.assertNull(relax_triple.getPredicat_var());
		Assert.assertNull(relax_triple.getRelaxed_subject());
		Assert.assertEquals(5, relax_triple.getRelaxed_object().size());
		Assert.assertEquals(2, relax_triple.getRelaxed_predicat().size());
		Assert.assertEquals(10, relax_triple.getRelaxed_triple().size());
		for (NodeRelaxed triple : relax_triple.getRelaxed_triple()) {
			Logger.getRootLogger().info(triple.getNode_1() + " " + triple.getNode_2() + " " + triple.getNode_3() + " :"
					+ triple.getSimilarity() + " :" + triple.getRelaxation_level());
		}
	}

}
