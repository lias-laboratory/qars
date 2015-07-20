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
package fr.ensma.lias.qarscore.engine.relaxation.strategy;

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
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class RelaxationGraphStrategyTest extends SessionTDBTest {

    private Session session;
    private Logger logger;
    private RelaxationGraphStrategy  relaxation_auto;
    
    @Before
    public void setUp() {
	super.setUp();
	logger = Logger.getRootLogger();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	
	session = SessionFactory.getTDBSession("target/TDB/LUBM100");
	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	Assert.assertNotNull(session.getBaseModel());
	
	relaxation_auto = new RelaxationGraphStrategy(session);
    }

    @After
    public void tearDown() {
	super.teardDown();
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.strategy.RelaxationGraphStrategy#getRelaxed_queries_graph()}.
     */
    @Test
    public void testGetRelaxed_queries_graph() {
	
	logger.info("Relaxation Visiting Professor");
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);
	
	Assert.assertTrue(relaxation_auto.relax_query(conjunctiveQuery));
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.strategy.RelaxationGraphStrategy#relax_query(fr.ensma.lias.qarscore.engine.query.CQuery)}.
     */
    @Test
    public void testRelax_query() {
	fail("Not yet implemented"); // TODO
    }

}
