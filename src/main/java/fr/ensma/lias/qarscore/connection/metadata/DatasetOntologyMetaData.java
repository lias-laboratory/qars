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
package fr.ensma.lias.qarscore.connection.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.ensma.lias.qarscore.configuration.QueryConfig;
import fr.ensma.lias.qarscore.connection.Session;

/**
 *
 * @author Stéphane Jean
 * @author Geraud Fokou
 *
 */

public class DatasetOntologyMetaData {

    private final static String FOLDER_STAT = "Statistic/";

    // Constants for RDF prefixes
    public static String PREFIX_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static String PREFIX_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    public static String PREFIX_OWL = "http://www.w3.org/2002/07/owl#";
    public static String PREFIX_DATASET = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#";

    public static String RDFS_SUBCLASSOF = PREFIX_RDFS + "subClassOf";
    public static String RDFS_SUBPROPERTYOF = PREFIX_RDFS + "subPropertyOf";
    public static String RDF_TYPE = PREFIX_RDF + "type";
    private int nbInstances;
    private int nbTriples;

    // coded as a singleton
    private static DatasetOntologyMetaData instance;

    private Map<String, String> subClassOf;
    private Map<String, Integer> instances;

    private Map<String, String> subPropertyOf;
    private Map<String, Integer> triples;

    private int getNbInstances() {
	return nbInstances;
    }

    private int getNbTriples() {
	return nbTriples;
    }

    public void setNbInstances(int nbInstances) {
	this.nbInstances = nbInstances;
    }

    public void setNbTriples(int nbTriples) {
	this.nbTriples = nbTriples;
    }

    private DatasetOntologyMetaData(Session session) {
	subClassOf = new HashMap<String, String>();
	instances = new HashMap<String, Integer>();
	subPropertyOf = new HashMap<String, String>();
	triples = new HashMap<String, Integer>();
	try {
	    if (!loadfiles(session.getNameSession())) {
		this.setOntologyMetaData(session);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    this.setOntologyMetaData(session);
	}
    }

    public void addSuperClass(String aClass, String superClass) {
	subClassOf.put(aClass, superClass);
    }

    public void addSuperProperty(String aProp, String superProp) {
	subPropertyOf.put(aProp, superProp);
    }

    public void addInstances(String aClass, int nbInstances) {
	instances.put(aClass, nbInstances);
    }

    public String getSuperClass(String aClass) {
	return subClassOf.get(aClass);
    }

    public void addTriples(String aProp, int nbTriples) {
	// System.out.println(aProp + " --> " + nbTriples);
	triples.put(aProp, nbTriples);
    }

    // information content of the class
    public double getIcClass(String aClass) {
	double nbInstanceClass = getNbInstances(aClass);
	// we change slightly the number of instances of the class
	// so that the similarity of a superclass is stronger than replacing it
	// with a variable
	if (nbInstanceClass == 0) {
	    nbInstanceClass = 0.5;
	}
	double res = -Math.log10((double) nbInstanceClass / getNbInstances());
	return res;
    }

    private int getNbInstances(String aClass) {
	Integer res = instances.get(aClass);
	if (res == null) {
	    System.out.println("The class : " + aClass
		    + " does not have any stat");
	    return -1;
	}
	return res;
    }

    public final static DatasetOntologyMetaData getInstance(Session session) {
	if (instance == null) {
	    instance = new DatasetOntologyMetaData(session);
	}
	return instance;
    }

    public String getSuperProperty(String aProp) {
	return subPropertyOf.get(aProp);
    }

    // information content of the property
    public double getIcProperty(String aProp) {
	double res = -Math.log10((double) getNbTriples(aProp) / getNbTriples());
	if (res == 0)
	    res = 0.0001;
	return res;
    }

    private int getNbTriples(String aProp) {
	Integer res = triples.get(aProp);
	if (res == null) {
	    System.out.println("The property: " + aProp
		    + " does not have any stat");
	    return -1;
	}
	return res;
    }

    /**
     * Compute statistic of the data session session
     * 
     * @param session
     */
    private void setOntologyMetaData(Session session) {

	File statfolder = new File(FOLDER_STAT + session.getNameSession());
	statfolder.mkdirs();

	JSONResultSet resultJson = session
		.executeSelectQuery(QueryConfig.LIST_SUPER_CLASSES);
	while (resultJson.next()) {
	    String classe = resultJson.getString("classe");
	    String directsuperclasses = resultJson
		    .getString("directsuperclasses");
	    this.addSuperClass(classe, directsuperclasses);
	}

	try {
	    FileWriter out = new FileWriter(FOLDER_STAT
		    + session.getNameSession()
		    + "/LIST_SUPER_CLASSES.properties");
	    out.write(resultJson.toString());
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	resultJson = session
		.executeSelectQuery(QueryConfig.NUMBER_INSTANCE_CLASS);
	while (resultJson.next()) {
	    String classe = resultJson.getString("classe");
	    this.addInstances(classe, resultJson.getInt("numberInstance"));
	}

	try {
	    FileWriter out = new FileWriter(FOLDER_STAT
		    + session.getNameSession()
		    + "/NUMBER_INSTANCE_CLASS.properties");
	    out.write(resultJson.toString());
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	resultJson = session
		.executeSelectQuery(QueryConfig.LIST_SUPER_PROPERTIES);
	while (resultJson.next()) {
	    String property = resultJson.getString("property");
	    String directsuperproperty = resultJson
		    .getString("directsuperproperty");
	    this.addSuperProperty(property, directsuperproperty);
	}

	try {
	    FileWriter out = new FileWriter(FOLDER_STAT
		    + session.getNameSession()
		    + "/LIST_SUPER_PROPERTIES.properties");
	    out.write(resultJson.toString());
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	resultJson = session
		.executeSelectQuery(QueryConfig.NUMBER_TRIPLET_PROPERTY);
	while (resultJson.next()) {
	    String property = resultJson.getString("property");
	    this.addTriples(property, resultJson.getInt("numberProperty"));
	}

	try {
	    FileWriter out = new FileWriter(FOLDER_STAT
		    + session.getNameSession()
		    + "/NUMBER_TRIPLET_PROPERTY.properties");
	    out.write(resultJson.toString());
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	resultJson = session.executeSelectQuery(QueryConfig.NUMBER_TRIPLET);
	while (resultJson.next()) {
	    this.setNbTriples(resultJson.getInt("numberTriplet"));
	}

	try {
	    FileWriter out = new FileWriter(FOLDER_STAT
		    + session.getNameSession() + "/NUMBER_TRIPLET.properties");
	    out.write(resultJson.toString());
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	resultJson = session.executeSelectQuery(QueryConfig.NUMBER_INSTANCE);
	while (resultJson.next()) {
	    this.setNbInstances(resultJson.getInt("numberInstance"));
	}

	try {
	    FileWriter out = new FileWriter(FOLDER_STAT
		    + session.getNameSession() + "/NUMBER_INSTANCE.properties");
	    out.write(resultJson.toString());
	    out.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Load statistic store in the folder foldername if it exists
     * 
     * @param foldername
     * @return
     * @throws IOException
     */
    private boolean loadfiles(String foldername) throws IOException {

	if (foldername == null) {
	    return false;
	}
	File statfolder = new File(FOLDER_STAT + foldername);
	if (!statfolder.exists()) {
	    return false;
	}
	if (!statfolder.isDirectory()) {
	    return false;
	}
//	File[] child = statfolder.listFiles();
//	boolean found = false;
//	int i = 0;
//	while ((i < child.length) && (!found)) {
//	    found = child[i].getName().equals(foldername);
//	    i = i + 1;
//	}
//	if (!found) {
//	    return false;
//	}
//	statfolder = child[i - 1];
//	if (!statfolder.isDirectory()) {
//	    return false;
//	}

	JSONResultSet resultJson = JSONResultSet.getJSONResultSet(this
		.readfile(statfolder.getCanonicalPath()
			+ "/LIST_SUPER_CLASSES.properties"));
	while (resultJson.next()) {
	    String classe = resultJson.getString("classe");
	    String directsuperclasses = resultJson
		    .getString("directsuperclasses");
	    this.addSuperClass(classe, directsuperclasses);
	}

	resultJson = JSONResultSet.getJSONResultSet(this.readfile(statfolder
		.getCanonicalPath() + "/NUMBER_INSTANCE_CLASS.properties"));
	while (resultJson.next()) {
	    String classe = resultJson.getString("classe");
	    this.addInstances(classe, resultJson.getInt("numberInstance"));
	}

	resultJson = JSONResultSet.getJSONResultSet(this.readfile(statfolder
		.getCanonicalPath() + "/LIST_SUPER_PROPERTIES.properties"));
	while (resultJson.next()) {
	    String property = resultJson.getString("property");
	    String directsuperproperty = resultJson
		    .getString("directsuperproperty");
	    this.addSuperProperty(property, directsuperproperty);
	}

	resultJson = JSONResultSet.getJSONResultSet(this.readfile(statfolder
		.getCanonicalPath() + "/NUMBER_TRIPLET_PROPERTY.properties"));
	while (resultJson.next()) {
	    String property = resultJson.getString("property");
	    this.addTriples(property, resultJson.getInt("numberProperty"));
	}

	resultJson = JSONResultSet.getJSONResultSet(this.readfile(statfolder
		.getCanonicalPath() + "/NUMBER_TRIPLET.properties"));
	while (resultJson.next()) {
	    this.setNbTriples(resultJson.getInt("numberTriplet"));
	}

	resultJson = JSONResultSet.getJSONResultSet(this.readfile(statfolder
		.getCanonicalPath() + "/NUMBER_INSTANCE.properties"));
	while (resultJson.next()) {
	    this.setNbInstances(resultJson.getInt("numberInstance"));
	}

	return true;
    }

    /**
     * Read a file
     * 
     * @param pathname
     * @return
     * @throws IOException
     */
    private String readfile(String pathname) throws IOException {

	BufferedReader br = new BufferedReader(new FileReader(pathname));
	String everything;

	try {
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
		sb.append(line);
		sb.append(System.lineSeparator());
		line = br.readLine();
	    }
	    everything = sb.toString();
	} finally {
	    br.close();
	}
	return everything;
    }
}