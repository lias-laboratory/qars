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
package fr.ensma.lias.qarscore.properties;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;

/**
 * @author Geraud FOKOU
 */
public class Properties {

    public static LayoutType DEFAULT_DB_LAYOUT = LayoutType.LayoutTripleNodesHash;
    
    public static DatabaseType SDB_DB_SUPPORT_TYPE = DatabaseType.PostgreSQL;
    
    public static String DEFAULT_ONTO_LANG = "OWL";
    
    public static OntModelSpec DEFAULT_MODEL_SPECIFICATION = OntModelSpec.OWL_MEM;
    
    public static String TDB_PATH = System.getProperty("user.dir")
	    + "\\src\\test\\ressources\\TDB\\";

    /**
     * return the appropriate jdbc class's name for the current SDB database
     * type
     * 
     * @return
     */
    public static String getSDBDriverJDBC() {

	if (SDB_DB_SUPPORT_TYPE == DatabaseType.PostgreSQL) {
	    return "org.postgresql.Driver";
	}
	return null;
    }
}
