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

import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.tdb.TDBFactory;

import fr.ensma.lias.qarscore.connection.Session;

/**
 * @author Geraud FOKOU
 */
public class SessionTDB extends JenaSession {

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

    private SessionTDB(String folderTDB) {	
	dataset = TDBFactory.createDataset(folderTDB);
	
//	model = dataset.getDefaultModel();
//	
//	if (Properties.getModelMemSpec().equals(OntModelSpec.OWL_MEM_RDFS_INF)) {
//	    model = ModelFactory.createInfModel(ReasonerRegistry.getRDFSReasoner(), model);
//	}
//
//	if (Properties.getModelMemSpec().equals(OntModelSpec.OWL_MEM_TRANS_INF)) {
//	    model = ModelFactory.createInfModel(ReasonerRegistry.getTransitiveReasoner(), model);
//	}
//
//	if (Properties.getModelMemSpec().equals(OntModelSpec.RDFS_MEM_RDFS_INF)) {
//	    model = ModelFactory.createInfModel(ReasonerRegistry.getRDFSReasoner(), model);
//	}
//
//	if (Properties.getModelMemSpec()
//		.equals(OntModelSpec.RDFS_MEM_TRANS_INF)) {
//	    model = ModelFactory.createInfModel(ReasonerRegistry.getTransitiveReasoner(), model);
//	}
//
//	//model = ModelFactory.createInfModel(Properties.getModelMemSpec().getReasoner(), dataset.getDefaultModel());
//	ontologyModel = ModelFactory.createOntologyModel(
//		Properties.getModelMemSpec(), model);
//	SimilarityMeasureConcept.get_concept_measure(this);
    }

    @Override
    public Store getDataStore() {
	return null;
    }
}
