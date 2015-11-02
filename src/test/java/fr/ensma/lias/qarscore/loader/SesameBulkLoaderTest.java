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
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;

import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.SesameSession;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
@SuppressWarnings("deprecation")
public class SesameBulkLoaderTest {

    private SesameSession session;

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
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

	File folderTDB = new File("target/Sesame/NativeRepository/LUBM1");
	if (folderTDB.exists()) {
	    deleteDirectory(folderTDB);
	}
	folderTDB.mkdirs();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

	Assert.assertNotNull(session.getRepository());
	try {
	    Assert.assertNotNull(session.getModel());
	    Assert.assertNotNull(session.getRepositoryConnection());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.SesameBulkLoaderTest#loadRepositoryMemory(java.io.File[], java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testLoadRepositoryMemory() {
	
	String baseURI = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl";
	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/LUBM1/lubm1.owl");
	
	session = (SesameSession) SessionFactory.getInMemorySesameSession(
		datafiles, baseURI, Properties.getOntoLang(), true);
	
	Assert.assertNotNull(session.getRepository());
	try {
	    Assert.assertNotNull(session.getModel());
	    Assert.assertNotNull(session
		    .getRepositoryConnection());
	    for (URI classe : session
		    .getInformation_content().keySet()) {
		Logger.getRootLogger().info(
			classe.stringValue()
				+ " has information content "
				+ session
					.getInformation_content().get(classe));
	    }
	    
	 //   session.close();

	} catch (Exception e) {
	    Assert.fail();
	    e.printStackTrace();
	}
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.SesameBulkLoaderTest#loadRepositoryMemory(java.io.File[], java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testLoadRepositoryInMemory() {

	String baseURI = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl";
	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/LUBM1/lubm1.owl");
	session = (SesameSession) SessionFactory.getInMemorySesameSession(
		datafiles, baseURI, Properties.getOntoLang());
	Assert.assertNotNull(session.getRepository());
	try {
	    Assert.assertNotNull(session.getModel());
	    Assert.assertNotNull(session
		    .getRepositoryConnection());
	    for (URI classe : session
		    .getInformation_content().keySet()) {
		Logger.getRootLogger().info(
			classe.stringValue()
				+ " has information content "
				+ session
					.getInformation_content().get(classe));
	    }
	    
	 //   session.close();

	} catch (Exception e) {
	    Assert.fail();
	    e.printStackTrace();
	}	
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.SesameBulkLoaderTest#loadNativeRepository(java.lang.String, java.io.File[], java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testLoadNativeRepository() {

	String baseURI = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl";
	File[] datafiles = new File[1];
	datafiles[0] = new File(System.getProperty("user.dir")
		+ "/src/test/resources/LUBM1/lubm1.owl");

	SesameBulkLoader.loaderNativeStore("target/Sesame/NativeRepository/LUBM1",
		datafiles, baseURI, Properties.getOntoLang());
	session = (SesameSession) SessionFactory
		.getNativeSesameSession("target/Sesame/NativeRepository/LUBM1");
    }

    /**
     * Test method for
     * {@link fr.ensma.lias.qarscore.loader.SesameBulkLoaderTest#main(java.lang.String[])}
     * .
     */
    @Test
    public void testMain() {

	String[] args = new String[4];
	args[0] = System.getProperty("user.dir") + "/src/test/resources/LUBM1";
	args[1] = "OWL";
	args[2] = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl";
	args[3] = "target/Sesame/NativeRepository/LUBM1";
	SesameBulkLoader.main(args);
	session = (SesameSession) SessionFactory
		.getNativeSesameSession("target/Sesame/NativeRepository/LUBM1");
    }
}
