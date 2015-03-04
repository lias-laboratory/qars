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
package fr.ensma.lias.qarscore.engine.relaxation;

import java.util.List;

import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree;

/**
 * @author Geraud FOKOU
 */
public class SimialrityStrategy {

    private CQuery root_query;
    private Session session;
    private RelaxationTree relaxed_queries;

    /**
     * 
     */
    public SimialrityStrategy(CQuery query, Session s) {
	root_query = query;
	session = s;
	relaxed_queries = new RelaxationTree(root_query, null, 1);
    }

    private List<CQuery> relaxation_Element(CElement element) {
	
	if(element.getElement() instanceof ElementPathBlock){
	    
	}
	return null;
    }

    private void initializeTree() {

	List<CQuery> relaxed_queries;
	for (CElement element : root_query.getElementList()) {
	    if(element.getElement() instanceof ElementPathBlock){
		    
		}
	    relaxed_queries = relaxation_Element(element);
	}
    }

}
