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

    private static LayoutType DEFAULT_DB_LAYOUT = LayoutType.LayoutTripleNodesHash;
    
    private static DatabaseType SDB_DB_SUPPORT_TYPE = DatabaseType.PostgreSQL;
    
    private static String DEFAULT_ONTO_LANG = "OWL";
    
    private static OntModelSpec DEFAULT_MODEL_SPECIFICATION = OntModelSpec.OWL_MEM;
    
    private static String TDB_PATH = System.getProperty("user.dir")
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

    /**
     * @return the SDB_DB_SUPPORT_TYPE
     */
    public static DatabaseType getSdbSupportType() {
        return SDB_DB_SUPPORT_TYPE;
    }

    /**
     * set Postgres as support of SDB jena tripleStore
     */
    public static void setPostgresSdbSupport() {
        SDB_DB_SUPPORT_TYPE = DatabaseType.PostgreSQL;
    }

    /**
     * @return the DEFAULT_ONTO_LANG
     */
    public static String getOntoLang() {
        return DEFAULT_ONTO_LANG;
    }

    /**
     * @param lang the DEFAULT_ONTO_LANG to set
     */
    public static void setOntoLang(String lang) {
        DEFAULT_ONTO_LANG = lang;
    }

    /**
     * @return the DEFAULT_DB_LAYOUT
     */
    public static LayoutType getSdbLayout() {
        return DEFAULT_DB_LAYOUT;
    }

    /**
     * @param layout the DEFAULT_DB_LAYOUT to set
     */
    public static void setSdbLayout(LayoutType layout) {
        DEFAULT_DB_LAYOUT = layout;
    }

    /**
     * @return the DEFAULT_MODEL_SPECIFICATION
     */
    public static OntModelSpec getModelMemSpec() {
        return DEFAULT_MODEL_SPECIFICATION;
    }

    /**
     * @param ontoSpec the DEFAULT_MODEL_SPECIFICATION to set
     */
    public static void setModelMemSpec(OntModelSpec ontoSpec) {
        DEFAULT_MODEL_SPECIFICATION = ontoSpec;
    }

    /**
     * @return the tDB_PATH
     */
    public static String getTDB_PATH() {
        return TDB_PATH;
    }
    
}
