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
package fr.ensma.lias.qarscore.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;

/**
 * @author Geraud FOKOU
 */
public class SessionTDBTest extends InitTest {

    public Map<OntClass, Integer> class_instance;
    public Map<OntProperty, Integer> property_Triplet;
    public Logger logger = Logger.getLogger(SessionTDBTest.class);
    
    @Before
    public void setUp() {
	
	super.setUp();
//	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
//	Properties.setOntoLang("OWL");
	class_instance = new HashMap<OntClass, Integer>();
	property_Triplet = new HashMap<OntProperty, Integer>();
//	sessionJena = SessionFactory.getTDBSession(tdb_path);
    }

    @After
    public void tearDown() {
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

//    @Test
    public void testSessionTDB() {

	Assert.assertNotNull(((JenaSession) sessionJena).getDataset());
	Assert.assertNotNull(((JenaSession) sessionJena).getModel());
	Assert.assertNotNull(((JenaSession) sessionJena).getOntology());
	Assert.assertTrue(((JenaSession) sessionJena).getTripleList().size() != 0);
	logger.info(
		((JenaSession) sessionJena).getDataset().toString());

	for (Resource classe : ((JenaSession) sessionJena).getInformation_content()
		.keySet()) {
	    logger.info(
		    classe.getURI()
			    + " has information content "
			    + ((JenaSession) sessionJena).getInformation_content()
				    .get(classe));
	}
    }
    
    @Test
    public void testOntologyTDB(){
	Assert.assertNotNull(((JenaSession) sessionJena).getDataset());
	Assert.assertNotNull(((JenaSession) sessionJena).getModel());
	OntModel ontology = ((JenaSession) sessionJena).getOntology();
	Assert.assertNotNull(ontology);
	
	ExtendedIterator<OntClass> listClass = ontology.listClasses();
	logger.info("SubClassOf");
	while (listClass.hasNext()) {
	    OntClass currentClass = listClass.next();
	    class_instance.put(currentClass, currentClass.listInstances(false).toList().size());
	    List<OntClass> subclasses = currentClass.listSubClasses(true).toList();
	    while (!subclasses.isEmpty()) {
		OntClass currentSubClass = subclasses.get(0);
		subclasses.remove(currentSubClass);
		logger.info(currentSubClass.getLocalName()+"-->"+currentClass.getLocalName());
	    }
	}
	
	logger.info("Instance by class");
	for(OntClass key:class_instance.keySet()){
	    logger.info(key.getLocalName()+":"+class_instance.get(key).intValue());
	}
	
	logger.info("All Instance Number");
	logger.info(ontology.listIndividuals().toList().size());
	
	logger.info("SubpropertyOf");
	ExtendedIterator<OntProperty> listProperty = ontology.listAllOntProperties();

	while (listProperty.hasNext()) {
	    OntProperty currentProperty = listProperty.next();
	    int number = ontology.listResourcesWithProperty(currentProperty, null).toList().size();
	    List<OntProperty> subproperties = new ArrayList<OntProperty>();
	    subproperties.addAll(currentProperty.listSubProperties().toList());
	    while (!subproperties.isEmpty()) {
		OntProperty currentSubProperty = subproperties.get(0);
		subproperties.remove(currentSubProperty);
		logger.info(currentSubProperty.getLocalName()+"-->"+currentProperty.getLocalName());
		number = number + ontology.listResourcesWithProperty(currentSubProperty, null).toList().size();
	    }
	    property_Triplet.put(currentProperty, number) ;  
	}
	
	logger.info("Triple by Property");
	for( OntProperty key:property_Triplet.keySet()){
	    logger.info(key.getLocalName()+":"+property_Triplet.get(key).intValue());
	}
	
	logger.info("All Triple Number");
	logger.info(ontology.listStatements().toList().size());
    }
}
