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
public class EdgesJSONTest extends InitTest {

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

//    @Test
//    public void testEdgesConstruction() {
//
//	ExtendedIterator<OntClass> listRoot = ((JenaSession)sessionJena).getOntology()
//		.listHierarchyRootClasses();
//	while (listRoot.hasNext()) {
//	    OntClass currentRoot = listRoot.next();
//	    if (currentRoot.getURI() != null) {
//		NodeJSON nodejs = new NodeJSON(currentRoot.getLocalName(),
//			currentRoot.getNameSpace(), ((JenaSession)sessionJena).getOntology().getNsURIPrefix(currentRoot.getNameSpace()), currentRoot.getURI(),
//			currentRoot.getLocalName());
//		Assert.assertNotNull(nodejs);
//		Assert.assertTrue(nodejs.getNodeIRI().contains(
//			nodejs.getNodeName()));
//		Assert.assertTrue(nodejs.getNodeIRI().contains(
//			nodejs.getNodeNameSpace()));
//
//		logger.info(nodejs.toString());
//
//		ExtendedIterator<OntProperty> allProperties = currentRoot
//			.listDeclaredProperties(true);
//		while (allProperties.hasNext()) {
//		    OntProperty currentProperty = allProperties.next();
//		    if (currentProperty.isObjectProperty()) {
//			EdgesJSON edge = new EdgesJSON(
//				currentProperty.getLocalName(),
//				currentProperty.getNameSpace(), ((JenaSession)sessionJena).getOntology().getNsURIPrefix(currentProperty.getNameSpace()),
//				currentProperty.getURI(),
//				currentProperty.getLocalName(),
//				"ObjectProperty");
//			logger.info(edge.getEdgeName());
//			if (currentProperty.getRange() != null) {
//			    OntClass range = currentProperty.getRange()
//				    .asClass();
//			    Assert.assertTrue(range.isClass());
//			    Assert.assertNotNull(edge);
//			    NodeJSON nodejs1 = new NodeJSON(
//				    range.getLocalName(), range.getNameSpace(), ((JenaSession)sessionJena).getOntology().getNsURIPrefix(range.getNameSpace()),
//				    range.getURI(), range.getLocalName());
//			    edge.setEdgeSource(nodejs);
//			    edge.setEdgeDestination(nodejs1);
//
//			}
//			logger.info(edge.toString());
//		    }
//		}
//	    }
//	}
//    }
}
