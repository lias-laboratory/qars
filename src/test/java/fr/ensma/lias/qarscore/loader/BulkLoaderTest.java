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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class BulkLoaderTest {

    /** URL of SDB database on postgres **/
    private final String POSTGRES_DB_URL = "jdbc:postgresql://localhost:5432/";

    /** User credentials */
    private final String POSTGRES_DB_USER = "postgres";
    private final String POSTGRES_DB_PASSWORD = "psql";

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
	File folderTDB = new File(System.getProperty("user.dir")
		+ "/target/TDB/LUBM1");
	deleteDirectory(folderTDB);
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.BulkLoader#loadPostgresSBDDataset(java.io.File[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    // @Test
    public void testLoadPostgresSBDDataset() {
	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/DataSources/LUBM1/Uni1.owl");

	BulkLoader.loadPostgresSBDDataset(datafiles, "OWL", POSTGRES_DB_URL,
		POSTGRES_DB_USER, POSTGRES_DB_PASSWORD, "LUBM1");
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.BulkLoader#loadTDBDataset(java.io.File[], java.lang.String, java.lang.String)}
     */
    @Test
    public void testLoadTDBDataset() {
	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/DataSources/LUBM1/Uni1.owl");

	BulkLoader.loadTDBDataset(datafiles, "OWL",
		System.getProperty("user.dir") + "/target/TDB/LUBM1");
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.BulkLoader#main(java.lang.String[])}
     */
    @Test
    public void testMain() {
	String[] args = new String[4];
	args[0] = System.getProperty("user.dir")
		+ "/src/test/resources/DataSources/LUBM1";
	args[1] = "OWL";
	args[2] = "TDB";
	args[3] = System.getProperty("user.dir") + "/target/TDB/LUBM1";
	try {
	    BulkLoader.main(args);
	} catch (NotYetImplementedException e) {
	    e.printStackTrace();
	    Assert.fail();
	}
    }

}
