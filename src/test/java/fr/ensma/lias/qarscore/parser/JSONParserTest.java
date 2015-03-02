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
package fr.ensma.lias.qarscore.parser;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class JSONParserTest extends SessionTDBTest{

    private Logger logger;
    private Session session;

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

    @Test
    public void TestGetListNodeJs(){
	JSONParser parser = new JSONParser(session.getOntologyModel());
	Assert.assertNotNull(parser);
	List<NodeJSON> nodes = parser.getListNodeJs();
	Assert.assertNotNull(nodes);
	Assert.assertTrue(!nodes.isEmpty());
	for(NodeJSON node:nodes){
	    logger.info(node);
	}
    }
    
    @Test
    public void testGetParser() {
	
	JSONParser parser = new JSONParser(session.getOntologyModel());
	Assert.assertNotNull(parser.getListNodeJs());
	Assert.assertTrue(!parser.getListEdgesProperties().isEmpty());
	Assert.assertTrue(!parser.getListEdgesSubclass().isEmpty());
	logger.info(parser.getParser());
    }

}
