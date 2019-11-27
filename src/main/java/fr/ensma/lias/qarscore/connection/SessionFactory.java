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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;

import fr.ensma.lias.qarscore.configuration.OutputFormat;
import fr.ensma.lias.qarscore.connection.implementation.EndPointSession;
import fr.ensma.lias.qarscore.connection.implementation.JenaSDBSession;
import fr.ensma.lias.qarscore.connection.implementation.JenaTDBSession;
import fr.ensma.lias.qarscore.connection.implementation.ModelSession;
import fr.ensma.lias.qarscore.connection.implementation.SesameSession;

/**
 * @author Geraud FOKOU
 */
public class SessionFactory {

	/**
	 * Get a session for a SDB RDF dataset
	 * 
	 * @param url
	 * @param login
	 * @param password
	 * @param nameDB
	 * @return
	 */
	public static Session getJenaSDBSession(String url, String login, String password, String nameDB) {
		Connection connect = null;

		try {
			Class.forName("org.postgresql.Driver");
			connect = DriverManager.getConnection(url + nameDB.toLowerCase(), login, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (connect == null)
			return null;

		return JenaSDBSession.getSDBSession(connect);
	}

	/**
	 * Get a Session for a TDB dataset
	 * 
	 * @param folder
	 * @return
	 */
	public static Session getJenaTDBSession(String folder) {
		return JenaTDBSession.getTDBSession(folder);
	}

	public static Session getNativeSesameSession(String folder) {
		return SesameSession.getNativeSesameSession(folder);
	}

	public static Session getInMemorySesameSession(File[] datafiles, String baseURI, String lang, OntModelSpec spec) {
		return SesameSession.getInMemorySesameSession(datafiles, baseURI, lang, spec);
	}

	public static Session getInMemorySesameSession(File[] datafiles, String baseURI, String lang, OntModelSpec spec,
			boolean persist) {
		return SesameSession.getInMemorySesameSession(datafiles, baseURI, lang, spec, persist);
	}

	public static Session getEndpointSession(String url) {
		return new EndPointSession.Builder().url(url).outputFormat(OutputFormat.JSON).build();
	}

	public static Session getModelSession(InputStream data) {
		return new ModelSession(data);
	}

	public static Session getModelSession(Model data) {
		return new ModelSession(data);
	}

}
