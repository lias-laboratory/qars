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

import static org.junit.Assert.fail;

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
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
	super.setUp();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("LUBM1");
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getAFailingCause(fr.ensma.lias.qarscore.engine.query.CQuery)}.
     */
    @Test
    public void testGetAFailingCause() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getFailingCauses(fr.ensma.lias.qarscore.engine.query.CQuery)}.
     */
    @Test
    public void testGetFailingCauses() {
	fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.implementation.LatticeStrategy#getSuccessSubQueries()}.
     */
    @Test
    public void testGetSuccessSubQueries() {
	fail("Not yet implemented"); // TODO
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
