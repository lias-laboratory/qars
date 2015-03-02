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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class NodeJSONTest extends SessionTDBTest {

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

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.parser.NodeJSON#NodeJSON(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testNodeJSON() {

	ExtendedIterator<OntClass> listRoot = session.getOntologyModel()
		.listHierarchyRootClasses();
	while (listRoot.hasNext()) {
	    OntClass currentClass = listRoot.next();
	    if (currentClass.getURI() != null) {
		NodeJSON nodejs = new NodeJSON(currentClass.getLocalName(),
			currentClass.getNameSpace(), session.getOntologyModel()
				.getNsURIPrefix(currentClass.getNameSpace()),
			currentClass.getURI(), currentClass.getLocalName());
		Assert.assertNotNull(nodejs);
		Assert.assertTrue(nodejs.getNodeIRI().contains(
			nodejs.getNodeName()));
		Assert.assertTrue(nodejs.getNodeIRI().contains(
			nodejs.getNodeNameSpace()));
		logger.info(nodejs.toString());
	    }
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.parser.NodeJSON#getAttributesNames()}.
     */
    @Test
    public void testGetAttributesNames() {
	ExtendedIterator<OntClass> listRoot = session.getOntologyModel()
		.listHierarchyRootClasses();
	OntClass currentClass = listRoot.next();
	if (currentClass.getURI() != null) {
	    NodeJSON nodejs = new NodeJSON(currentClass.getLocalName(),
		    currentClass.getNameSpace(), session.getOntologyModel()
			    .getNsURIPrefix(currentClass.getNameSpace()),
		    currentClass.getURI(), currentClass.getLocalName());
	    Assert.assertNotNull(nodejs);
	    Assert.assertTrue(nodejs.getNodeIRI()
		    .contains(nodejs.getNodeName()));
	    Assert.assertTrue(nodejs.getNodeIRI().contains(
		    nodejs.getNodeNameSpace()));

	    ExtendedIterator<OntProperty> allProperties = currentClass
		    .listDeclaredProperties(true);
	    while (allProperties.hasNext()) {
		OntProperty currentProperty = allProperties.next();
		if (currentProperty.isDatatypeProperty()) {
		    nodejs.add(currentProperty.getLocalName(), currentProperty
			    .getRDFType().getLocalName(), currentProperty.getURI());
		}
	    }
	    logger.info(nodejs.toString());
	}
    }
}
