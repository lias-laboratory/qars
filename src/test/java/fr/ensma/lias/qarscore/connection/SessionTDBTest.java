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

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;
import fr.ensma.lias.qarscore.loader.BulkLoader;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class SessionTDBTest {

    /**
     * Method for deleting a directory after deleting all the files and folder
     * in this directory
     * 
     * @param folder
     * @return
     */
    private boolean deleteDirectory(File folder) {
	if (!folder.isDirectory()) {
	    return folder.delete();
	}

	for (File dataFile : folder.listFiles()) {
	    deleteDirectory(dataFile);
	}

	return folder.delete();
    }

    @Before
    public void setUp() {
	File folderTDB = new File("target/TDB/LUBM1");
	if (folderTDB.exists()) {
	    deleteDirectory(folderTDB);
	}
	folderTDB.mkdirs();

	String[] args = new String[4];
	args[0] = System.getProperty("user.dir")
		+ "/src/test/resources/DataSources/LUBM1";
	args[1] = "OWL";
	args[2] = "TDB";
	args[3] = "target/TDB/LUBM1";
	try {
	    BulkLoader.main(args);
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
    }

    @After
    public void teardDown() {
	File folderTDB = new File("target/TDB/LUBM1");
	if (folderTDB.exists()) {
	    deleteDirectory(folderTDB);
	}
    }

    @Test
    public void testSessionTDB() {
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	Session session = SessionFactory.getTDBSession("target/TDB/LUBM1");

	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	Assert.assertNotNull(session.getBaseModel());
	Assert.assertTrue(session.getOntologyTriple().size() != 0);

	String ontoJson = session.getOntoJSON();
	Assert.assertNotNull(ontoJson);
	String entity = "Professor";

	Assert.assertTrue(ontoJson.contains(entity));

    }

    @Test
    public void testSessionTDB1() {
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	Session session = SessionFactory.getTDBSession("target/TDB/LUBM1");

	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	Assert.assertNotNull(session.getBaseModel());
	Assert.assertTrue(session.getOntologyTriple().size() != 0);

	ExtendedIterator<OntClass> listRoot = session.getOntologyModel()
		.listHierarchyRootClasses();
	while (listRoot.hasNext()) {
	    OntClass currentClass = listRoot.next();
	    System.out.println(currentClass.getURI() + "-->"
		    + currentClass.getRDFType(false).getLocalName()
		    + "-----------------------------------------------");
	}

	ExtendedIterator<OntClass> allClass = session.getOntologyModel()
		.listClasses();
	while (allClass.hasNext()) {
	    OntClass currentClass = allClass.next();
	    System.out.println(currentClass.getURI() + "-->"
		    + currentClass.getRDFType(false).getLocalName()
		    + "-----------------------------------------------");
	    StmtIterator properties = currentClass.listProperties();
	    while (properties.hasNext()) {
		Statement currentProperty = properties.next();
		System.out.println(currentProperty.getSubject() + "-->"
			+ currentProperty.getPredicate() + "-->"
			+ currentProperty.getObject());
		System.out
			.println("----------------------------------------------------------------------------");
	    }

	    ExtendedIterator<OntProperty> allProperties = currentClass
		    .listDeclaredProperties(true);
	    while (allProperties.hasNext()) {
		OntProperty currentProperty = allProperties.next();
		System.out.println(currentProperty.getURI() + "-->"
			+ currentProperty.getRDFType(false).getLocalName()
			+ "-----------------------------------------------");
		System.out.println(currentProperty.getDomain() + "-->"
			+ currentProperty.getLocalName() + "-->"
			+ currentProperty.getRange());
		System.out
			.println("----------------------------------------------------------------------------");
	    }
	}

	// ExtendedIterator<OntProperty> allProperties =
	// session.getOntologyModel().listAllOntProperties();
	// while(allProperties.hasNext()){
	// OntProperty currentProperty = allProperties.next();
	// System.out.println(currentProperty.getURI()+"-->"+currentProperty.getRDFType(false).getLocalName()+"-----------------------------------------------");
	// System.out.println(currentProperty.getDomain()+"-->"+currentProperty.getLocalName()+"-->"+currentProperty.getRange());
	// System.out.println("----------------------------------------------------------------------------");
	// }
    }
}
