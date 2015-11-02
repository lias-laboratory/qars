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
package fr.ensma.lias.qarscore.loader;

import java.io.File;

import org.apache.jena.ontology.OntModelSpec;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class JenaBulkLoaderTest {

    private JenaSession session;

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

    /**
     * Delete the TDB folder if it exist and creates a new
     */
    @Before
    public void setUp() {

	File folderTDB = new File(System.getProperty("user.dir")
		+ "/target/TDB/LUBM1");
	if (folderTDB.exists()) {
	    deleteDirectory(folderTDB);
	}
	folderTDB.mkdirs();

    }

    /**
     * Delete a TDB folder
     */
    @After
    public void tearDown() {

	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getModel());
	Assert.assertNotNull(session.getOntology());

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.JenaBulkLoader#loadTDBDataset(java.io.File[], java.lang.String, java.lang.String)}
     */
    @Test
    public void testLoadTDBDataset() {

	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/LUBM1/lubm1.owl");

	JenaBulkLoader.loadTDBDataset(datafiles, "OWL",
		System.getProperty("user.dir") + "/target/TDB/LUBM1");

	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	session = (JenaSession) SessionFactory
		.getTDBSession("target/TDB/LUBM1");

    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.JenaBulkLoader#main(java.lang.String[])}
     */
    @Test
    public void testMain() {

	String[] args = new String[4];
	args[0] = System.getProperty("user.dir") + "/src/test/resources/LUBM1";
	args[1] = "OWL";
	args[2] = "TDB";
	args[3] = System.getProperty("user.dir") + "/target/TDB/LUBM1";
	JenaBulkLoader.main(args);
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	session = (JenaSession) SessionFactory
		.getTDBSession("target/TDB/LUBM1");
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.JenaBulkLoader#loadPostgresSBDDataset(java.io.File[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testLoadPostgresSBDDataset() {

	String[] args = new String[6];
	args[0] = System.getProperty("user.dir") + "/src/test/resources/LUBM1";
	args[1] = "OWL";
	args[2] = "POSTGRES";
	args[3] = "jdbc:postgresql://localhost:5432";
	args[4] = "postgres";
	args[5] = "plsql";
	args[6] = "test";
	JenaBulkLoader.main(args);
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	Properties.setPostgresSdbSupport();
	session = (JenaSession) SessionFactory
		.getSDBSession("jdbc:postgresql://localhost:5432", "postgres",
			"plsql", "test");
    }

    /**
     * 
     */
    @Test
    public void testLoadSDBDataset() {

	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/LUBM1/lubm1.owl");

	JenaBulkLoader.loadPostgresSBDDataset(datafiles, "OWL",
		"jdbc:postgresql://localhost:5432", "postgres", "plsql",
		"lubm1");
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	Properties.setPostgresSdbSupport();
	session = (JenaSession) SessionFactory
		.getSDBSession("jdbc:postgresql://localhost:5432", "postgres",
			"plsql", "lubm1");

    }

}
