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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.ensma.lias.qarscore.connection.implementation.SessionSDB;
import fr.ensma.lias.qarscore.connection.implementation.SessionTDB;
import fr.ensma.lias.qarscore.properties.Properties;

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
    public static Session getSDBSession(String url, String login,
	    String password, String nameDB) {
	Connection connect = null;

	try {
	    Class.forName(Properties.getSDBDriverJDBC());
	    connect = DriverManager
		    .getConnection(url + nameDB.toLowerCase(), login, password);
	} catch (SQLException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	if (connect == null)
	    return null;

	return SessionSDB.getSessionSDB(connect);
    }

    /**
     * Get a Session for a TDB dataset
     * 
     * @param folder
     * @return
     */
    public static Session getTDBSession(String folder) {
	return SessionTDB.getSessionTDB(folder);
    }
}
