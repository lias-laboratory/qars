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
package fr.ensma.lias.qarscore.engine.relaxation.strategies.mfs.implementation;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.mfs.AbstractRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class BasicRelaxationStrategy extends AbstractRelaxationStrategy {

    /**
     * @param query
     * @param s
     */
    public BasicRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
    }

    @Override
    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}
	
	GraphRelaxationIndex relax_graph_node = relaxed_queries.remove(0);
	this.getQuery(relax_graph_node);

	for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
	    if (!alreadyRelaxed(relax_graph_node.getChild_elt()[j])) {
		this.insert_relaxation_graph_node(relax_graph_node.getChild_elt()[j]);
	    }
	}
	already_relaxed_queries.add(relax_graph_node);
	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query,
		query_to_relax.getSelectedQueryVar());
	return current_relaxed_query;

    }

}
