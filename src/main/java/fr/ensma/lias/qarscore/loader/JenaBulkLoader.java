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
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sdb.SDBFactory;
import org.apache.jena.sdb.Store;
import org.apache.jena.sdb.StoreDesc;
import org.apache.jena.sdb.sql.SDBConnection;
import org.apache.jena.sdb.store.DatabaseType;
import org.apache.jena.sdb.store.LayoutType;
import org.apache.jena.sdb.store.StoreFactory;
import org.apache.jena.tdb.TDBFactory;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class JenaBulkLoader {

	private static void dropDatabase(String url, String login, String password, String nameDB)
			throws SQLException, ClassNotFoundException {

		Connection connect = null;

		Class.forName("org.postgresql.Driver");
		connect = DriverManager.getConnection(url, login, password);

		Statement stmt = connect.createStatement();
		stmt.executeUpdate("DROP DATABASE IF EXISTS " + nameDB);
		stmt.executeUpdate("CREATE DATABASE " + nameDB);
		stmt.close();

		if (connect != null) {
			connect.close();
		}

	}

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
	public static void loadPostgresSBDDataset(File[] dataFiles, String lang, String url, String login, String password,
			String nameDB) {

		Connection connect = null;

		nameDB = nameDB.toLowerCase();
		try {
			dropDatabase(url, login, password, nameDB.toLowerCase());
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}

		try {
			Class.forName("org.postgresql.Driver");
			connect = DriverManager.getConnection(url + nameDB.toLowerCase(), login, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		SDBConnection connectSDB = SDBFactory.createConnection(connect);
		StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.PostgreSQL);
		Store store = StoreFactory.create(storeDesc, connectSDB);
		store.getTableFormatter().create();
		store.getTableFormatter().truncate();
		Model dataModel = SDBFactory.connectDefaultModel(store);

		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, dataModel);

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
	public static void loadTDBDataset(File[] dataFiles, String lang, String folderTDB) {

		Dataset dataset = TDBFactory.createDataset(folderTDB);
		Model dataModel = dataset.getDefaultModel();

		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, dataModel);

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
	 * main of BulkLoader, use for loading a specific data set in a TDB or SDB Jena
	 * triple store BulkLoader Folder/File name, Onto_Lang, "TDB" BulkLoader
	 * Folder/File name, Onto_Lang, "SDB", login, password, dbname
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		int argsLenth = args.length;
		if (argsLenth < 4) {
			throw new IllegalArgumentException("illegal number of parameter");
		}

		String nameFolder = args[0];
		File dataFolder = new File(nameFolder);
		File[] dataFiles;

		if (!dataFolder.exists()) {
			throw new IllegalArgumentException("File doesn't exist");
		}

		FilenameFilter fileExt;
		switch (args[1].toUpperCase()) {

		case "OWL":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".owl");
				}
			};
			break;

		case "N3":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".n3");
				}
			};
			break;

		case "RDF":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".rdf");
				}
			};
			break;

		case "NT":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".nt");
				}
			};
			break;

		case "DAML":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".daml");
				}
			};
			break;

		case "TURTLE":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".ttl");
				}
			};
			break;

		default:
			throw new IllegalArgumentException("wrong ontology language");
		}

		if (dataFolder.isDirectory()) {
			dataFiles = dataFolder.listFiles(fileExt);
		} else {
			if (fileExt.accept(dataFolder.getParentFile(), dataFolder.getName())) {
				dataFiles = new File[1];
				dataFiles[0] = dataFolder;
			} else {
				throw new IllegalArgumentException("Incompatible File and language");
			}
		}

		String dbname;
		switch (args[2].toUpperCase()) {
		case "TDB":
			dbname = args[3];
			loadTDBDataset(dataFiles, args[1], dbname);
			break;

		case "POSTGRES":

			if ((argsLenth < 6) || (argsLenth > 7)) {
				throw new IllegalArgumentException("illegal number of parameter");
			}
			if (argsLenth == 7) {
				dbname = args[6];
			} else {
				dbname = dataFolder.getName();
				if (!dataFolder.isDirectory()) {
					dbname = dbname.substring(0, dbname.lastIndexOf('.'));
				}
			}
			loadPostgresSBDDataset(dataFiles, args[1], args[3], args[4], args[5], dbname);
			break;

		default:
			throw new NotYetImplementedException("SDB Support not yet implemented");
		}
	}
}
