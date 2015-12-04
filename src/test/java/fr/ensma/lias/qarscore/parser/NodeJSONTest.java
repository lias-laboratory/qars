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
import org.junit.Before;

import fr.ensma.lias.qarscore.InitTest;

/**
 * @author Geraud FOKOU
 */
public class NodeJSONTest extends InitTest {

    @SuppressWarnings("unused")
    private Logger logger;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
	super.setUp();
	logger = Logger.getRootLogger();
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
     * {@link fr.ensma.lias.qarscore.parser.NodeJSON#NodeJSON(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
//    @Test
//    public void testNodeJSON() {
//
//	ExtendedIterator<OntClass> listRoot = ((JenaSession)sessionJena).getOntology()
//		.listHierarchyRootClasses();
//	while (listRoot.hasNext()) {
//	    OntClass currentClass = listRoot.next();
//	    if (currentClass.getURI() != null) {
//		NodeJSON nodejs = new NodeJSON(currentClass.getLocalName(),
//			currentClass.getNameSpace(), ((JenaSession)sessionJena).getOntology()
//				.getNsURIPrefix(currentClass.getNameSpace()),
//			currentClass.getURI(), currentClass.getLocalName());
//		Assert.assertNotNull(nodejs);
//		Assert.assertTrue(nodejs.getNodeIRI().contains(
//			nodejs.getNodeName()));
//		Assert.assertTrue(nodejs.getNodeIRI().contains(
//			nodejs.getNodeNameSpace()));
//		logger.info(nodejs.toString());
//	    }
//	}
//    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.parser.NodeJSON#getAttributesNames()}.
     */
//    @Test
//    public void testGetAttributesNames() {
//	ExtendedIterator<OntClass> listRoot = ((JenaSession)sessionJena).getOntology()
//		.listHierarchyRootClasses();
//	OntClass currentClass = listRoot.next();
//	if (currentClass.getURI() != null) {
//	    NodeJSON nodejs = new NodeJSON(currentClass.getLocalName(),
//		    currentClass.getNameSpace(), ((JenaSession)sessionJena).getOntology()
//			    .getNsURIPrefix(currentClass.getNameSpace()),
//		    currentClass.getURI(), currentClass.getLocalName());
//	    Assert.assertNotNull(nodejs);
//	    Assert.assertTrue(nodejs.getNodeIRI()
//		    .contains(nodejs.getNodeName()));
//	    Assert.assertTrue(nodejs.getNodeIRI().contains(
//		    nodejs.getNodeNameSpace()));
//
//	    ExtendedIterator<OntProperty> allProperties = currentClass
//		    .listDeclaredProperties(true);
//	    while (allProperties.hasNext()) {
//		OntProperty currentProperty = allProperties.next();
//		if (currentProperty.isDatatypeProperty()) {
//		    nodejs.add(currentProperty.getLocalName(), currentProperty
//			    .getRDFType().getLocalName(), currentProperty.getURI());
//		}
//	    }
//	    logger.info(nodejs.toString());
//	}
//    }
}
