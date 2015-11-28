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
	set_model();
	if(!load_stat_data(folderTDB)){
	    set_stat_data();
	}
	
	information_content = new HashMap<Resource, Double>();
	
	ExtendedIterator<OntClass> listClass = baseontology.listClasses();
	
	while (listClass.hasNext()) {
	    OntClass currentClass = listClass.next();
	    double classe_size = getSizeInstanceByClass(currentClass);
	    double icc_class = -1 * Math.log10(classe_size / stat_meta_data.getSize_instance());
	    information_content.put(currentClass, icc_class);
	}
	
	ExtendedIterator<OntProperty> listProperty = baseontology
		.listAllOntProperties();

	while (listProperty.hasNext()) {
	    OntProperty currentProperty = listProperty.next();
	    double property_size = getTripleSizeByProperty(currentProperty);
	    double icc_property = -1 * Math.log10(property_size / stat_meta_data.getSize_triple());
	    information_content.put(currentProperty, icc_property);
	}
    }
}
