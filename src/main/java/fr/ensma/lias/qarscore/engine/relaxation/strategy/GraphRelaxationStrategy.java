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
package fr.ensma.lias.qarscore.engine.relaxation.strategy;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;

/**
 * @author Geraud FOKOU
 */
public class GraphRelaxationStrategy extends HuangRelaxationStrategy{

    public GraphRelaxationStrategy(CQuery query, Session s) {
	query_to_relax = query;
	session = s;
	relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	start_relaxation();
    }

    protected void start_relaxation() {

	int[] relexation_limit_index = new int[query_to_relax.getElementList()
		.size()];
	int[] relaxation_index = new int[query_to_relax.getElementList().size()];

	relaxation_of_element = new NodeRelaxed[query_to_relax.getElementList()
		.size()][];
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    CElement element = query_to_relax.getElementList().get(i);
	    relaxation_of_element[i] = (new TripleRelaxation(element, session,
		    TripleRelaxation.SIM_ORDER)).get_relaxed_node_list();
	    relexation_limit_index[i] = relaxation_of_element[i].length;
	    relaxation_index[i] = 0;
	}

	relaxed_graph = new GraphRelaxationIndex(relaxation_index,
		relexation_limit_index);
	relaxed_queries.add(relaxed_graph);
    }

    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}
	GraphRelaxationIndex current_graph = relaxed_queries.remove(0);
	this.current_similarity = 1.0;
	this.current_level = 0;

	for (int i = 0; i < current_graph.getElement_index().length; i++) {

	    CElement relax_element = CElement.createCTriple(query_to_relax
		    .getElementList().get(0).getElement());
	    relax_element = relax_element
		    .replace_subject(relaxation_of_element[i][current_graph
			    .getElement_index()[i]].getNode_1());
	    relax_element = relax_element
		    .replace_predicat(relaxation_of_element[i][current_graph
			    .getElement_index()[i]].getNode_2());
	    relax_element = relax_element
		    .replace_object(relaxation_of_element[i][current_graph
			    .getElement_index()[i]].getNode_3());

	    elt_relaxed_query.add(relax_element);
	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getSimilarity();
	    this.current_level = this.current_level
		    + relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getRelaxation_level();
	}

	for (int j = 0; j < current_graph.getChild_elt().length; j++) {
	    this.insert(current_graph.getChild_elt()[j]);
	}

	return CQueryFactory.createCQuery(elt_relaxed_query);
    }
}
