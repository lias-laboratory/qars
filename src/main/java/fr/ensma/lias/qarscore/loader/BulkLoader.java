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
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.StoreFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class BulkLoader {

    /**
     * Load a set of Graph data present in a list of data files into a jena sdb
     * dataset with Postgres Database.
     * 
     * @param dataFiles
     * @param lang
     * @param url
     * @param login
     * @param password
     * @param nameDB
     */
    public static void loadPostgresSBDDataset(List<File> dataFiles,
	    String lang, String url, String login, String password,
	    String nameDB) {

	Connection connect = null;

	try {
	    Class.forName(Properties.getSDBDriverJDBC());
	    connect = DriverManager.getConnection(url, login, password);
	} catch (SQLException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	try {
	    Statement stmt = connect.createStatement();
	    stmt.executeUpdate("DROP DATABASE " + nameDB);
	    stmt.executeUpdate("CREATE DATABASE " + nameDB);
	    stmt.close();
	    connect.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	try {
	    Class.forName(Properties.getSDBDriverJDBC());
	    connect = DriverManager
		    .getConnection(url + nameDB, login, password);
	} catch (SQLException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	SDBConnection connectSDB = SDBFactory.createConnection(connect);
	StoreDesc storeDesc = new StoreDesc(Properties.DEFAULT_DB_LAYOUT,
		Properties.SDB_DB_SUPPORT_TYPE);
	Store store = StoreFactory.create(storeDesc, connectSDB);
	store.getTableFormatter().create();
	store.getTableFormatter().truncate();
	Model dataModel = SDBFactory.connectDefaultModel(store);

	OntModel ontoModel = ModelFactory.createOntologyModel(
		Properties.DEFAULT_MODEL_SPECIFICATION, dataModel);

	for (File dataFile : dataFiles) {

	    URI currentUri = dataFile.toURI();
	    String currentUrl = null;
	    try {
		currentUrl = currentUri.toURL().toString();
		ontoModel.read(currentUrl, lang);
	    } catch (MalformedURLException e) {
		e.printStackTrace();
	    }
	}
	dataModel.commit();
    }

    /**
     * Load a set of graph data present in a set of data files into a jena tdb
     * dataset.
     * 
     * @param dataFiles
     * @param lang
     * @param folder
     */
    public static void loadTDBDataset(List<File> dataFiles, String lang,
	    String folder) {

	Dataset dataset = TDBFactory
		.createDataset(Properties.TDB_PATH + folder);
	Model dataModel = dataset.getDefaultModel();

	OntModel ontoModel = ModelFactory.createOntologyModel(
		Properties.DEFAULT_MODEL_SPECIFICATION, dataModel);

	for (File dataFile : dataFiles) {

	    URI currentUri = dataFile.toURI();
	    String currentUrl = null;
	    try {
		currentUrl = currentUri.toURL().toString();
		ontoModel.read(currentUrl, lang);
	    } catch (MalformedURLException e) {
		e.printStackTrace();
	    }
	}
	dataModel.commit();
    }
}
