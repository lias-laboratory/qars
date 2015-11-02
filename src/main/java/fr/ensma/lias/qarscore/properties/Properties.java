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

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.sdb.store.DatabaseType;
import org.apache.jena.sdb.store.LayoutType;

/**
 * @author Geraud FOKOU
 */
public class Properties {

    private static LayoutType DEFAULT_DB_LAYOUT = LayoutType.LayoutTripleNodesHash;

    private static DatabaseType SDB_DB_SUPPORT_TYPE = DatabaseType.PostgreSQL;

    // "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3".
    private static String DEFAULT_ONTO_LANG = "RDF/XML";

    private static OntModelSpec DEFAULT_MODEL_SPECIFICATION = OntModelSpec.OWL_MEM;
    
    private static String RELAXATION_STRATEGY = "AUTO";
    
    private static int RELAXATION_ANSWERS_SIZE = 1;

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
     * @param lang
     *            the DEFAULT_ONTO_LANG to set
     */
    public static void setOntoLang(String lang) {

	switch (lang.toUpperCase()) {

	case "OWL":
	    DEFAULT_ONTO_LANG = "RDF/XML";
	    break;

	case "DAML":
	    DEFAULT_ONTO_LANG = "RDF/XML";
	    break;

	case "RDF":
	    DEFAULT_ONTO_LANG = "RDF/XML";
	    break;

	case "N3":
	    DEFAULT_ONTO_LANG = "N3";
	    break;

	case "NT":
	    DEFAULT_ONTO_LANG = "NT";
	    break;

	case "TURTLE":
	    DEFAULT_ONTO_LANG = "TTL";
	    break;

	default:
	    DEFAULT_ONTO_LANG = "RDF/XML";
	}
    }

    /**
     * @return the RELAXATION_STRATEGY
     */
    public static String getRELAXATION_STRATEGY() {
        return RELAXATION_STRATEGY;
    }


    public static void setlatticeStrategy(){
	RELAXATION_STRATEGY = "LBA";
    }
    
    public static void setlatticeOptimizeStrategy(){
	RELAXATION_STRATEGY = "LBA/OPT";
    }

    public static void setMatrixStrategy(){
	RELAXATION_STRATEGY = "MBA";
    }


    public static void setIshmaelStrategy(){
	RELAXATION_STRATEGY = "ISHMAEL";
    }

    public static void setSimilarityStrategy(){
	RELAXATION_STRATEGY = "SIM";
    }
    
    public static void setAutomaticStrategy(int size){
	RELAXATION_STRATEGY = "AUTO";
	RELAXATION_ANSWERS_SIZE = size;
    }


    /**
     * @return the rELAXATION_ANSWERS_SIZE
     */
    public static int getRELAXATION_ANSWERS_SIZE() {
        return RELAXATION_ANSWERS_SIZE;
    }

    /**
     * @return the DEFAULT_DB_LAYOUT
     */
    public static LayoutType getSdbLayout() {
	return DEFAULT_DB_LAYOUT;
    }

    /**
     * @param layout
     *            the DEFAULT_DB_LAYOUT to set
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
     * @param ontoSpec
     *            the DEFAULT_MODEL_SPECIFICATION to set
     */
    public static void setModelMemSpec(OntModelSpec ontoSpec) {
	DEFAULT_MODEL_SPECIFICATION = ontoSpec;
    }

}
