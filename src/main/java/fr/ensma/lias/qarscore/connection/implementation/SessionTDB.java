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
package fr.ensma.lias.qarscore.connection.implementation;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.tdb.TDBFactory;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class SessionTDB implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    private static Session session;

    /**
     * Dataset use for querying
     */
    protected Dataset dataset;

    /**
     * DataModel of semantic data
     */
    protected Model dataModel;

    /**
     * Ontology model for data
     */
    protected OntModel ontoModel;

    /**
     * Construct a TDB Session if there isn't existed
     */
    public static Session getSessionTDB(String folder) {

	if (session != null) {
	    return session;
	}
	session = new SessionTDB(folder);
	return session;
    }

    private SessionTDB(String folder) {

	dataset = TDBFactory.createDataset(Properties.getTDB_PATH() + folder);
	dataModel = dataset.getDefaultModel();
	ontoModel = ModelFactory.createOntologyModel(
		Properties.getModelMemSpec(), dataModel);
    }

    @Override
    public Dataset getDataset() {
	return dataset;
    }

    @Override
    public Model getDataModel() {
	return dataModel;
    }

    @Override
    public OntModel getOntologyModel() {
	return ontoModel;
    }

    /**
     * For TDB session DataStore is null
     */
    @Override
    public Store getDataStore() {
	return null;
    }
}
