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

import java.util.HashMap;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

import fr.ensma.lias.qarscore.connection.Session;

/**
 * @author Geraud FOKOU
 */
public class SessionJenaTDB extends JenaSession {

    /**
     * Construct a TDB Session if there isn't existed
     */
    public static Session getSessionTDB(String folder) {

	if (session != null) {
	    return session;
	}
	session = new SessionJenaTDB(folder);
	return session;
    }

    private SessionJenaTDB(String folderTDB) {

	dataset = TDBFactory.createDataset(folderTDB);
	// model = dataset.getDefaultModel();
	// ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);

	set_model();

	int size_data = this.getOntology().listIndividuals().toList().size();
	int size_prop = this.getOntology().listStatements().toList().size();
	
	information_content = new HashMap<Resource, Double>();
	
	ExtendedIterator<OntClass> listClass = this.getOntology().listClasses();
	
	while (listClass.hasNext()) {
	    OntClass currentClass = listClass.next();
	    double classe_size = getInstanceNumber(currentClass);
	    // double classe_size =
	    // Double.valueOf((1+currentClass.listInstances(true).toList().size()));
	    double icc_class = -1 * Math.log10(classe_size / size_data);

	    information_content.put(currentClass, icc_class);
	}
	
	ExtendedIterator<OntProperty> listProperty = this.getOntology().listAllOntProperties();

	while (listProperty.hasNext()) {
	    OntProperty currentProperty = listProperty.next();
	    double property_size = getInstanceNumber(currentProperty);
	    // double classe_size =
	    // Double.valueOf((1+currentClass.listInstances(true).toList().size()));
	    double icc_property = -1 * Math.log10(property_size / size_prop);

	    information_content.put(currentProperty, icc_property);
	}
    }    
}
