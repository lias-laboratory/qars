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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;

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
	args[0] = System.getProperty("user.dir") + "/src/test/resources/";
	args[1] = "OWL";
	args[2] = "TDB";
	args[3] = "target/TDB/LUBM1";
	BulkLoader.main(args);
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
	Logger.getRootLogger().info(ontoJson);
	Assert.assertTrue(ontoJson.contains(entity));

    }
}
