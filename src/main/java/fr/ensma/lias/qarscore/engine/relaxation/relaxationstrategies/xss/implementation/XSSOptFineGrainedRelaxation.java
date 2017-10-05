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
package fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.xss.implementation;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.implementation.JenaTDBSession;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class XSSOptFineGrainedRelaxation extends XSSFineGrainedRelaxation {

    /**
     * Relaxation session for each complement
     */
    protected Session[] comp_session;

    private Session current_Session;
    
    /**
     * @param query
     * @param s
     */
    public XSSOptFineGrainedRelaxation(CQuery query, Session s) {
	super(query, s);
	comp_session = new Session[xss_to_relax_queries.length];
	duration_computation_view = System.currentTimeMillis();
	for (int i = 0; i < xss_to_relax_queries.length; i++) {
//	    comp_session[i] = SessionFactory.getModelSession(session
//		    .executeConstructQuery(xss_to_relax_queries[i]
//			    .toConstructQuery()));
	    comp_session[i] = SessionFactory.getModelSession(((JenaTDBSession)session).executeModelConstructQuery(xss_to_relax_queries[i]
			    .toConstructQuery()));

	}
	duration_computation_view = System.currentTimeMillis() - duration_computation_view;
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public XSSOptFineGrainedRelaxation(CQuery query, Session s,
	    boolean optimization) {
	super(query, s, optimization);
	comp_session = new Session[xss_to_relax_queries.length];
	duration_computation_view = System.currentTimeMillis();
	for (int i = 0; i < xss_to_relax_queries.length; i++) {
	    comp_session[i] = SessionFactory
		    .getModelSession(session
			    .executeConstructQuery(xss_to_relax_queries[i].toConstructQuery()));
	}
	duration_computation_view = System.currentTimeMillis() - duration_computation_view;
    }

    @Override
    public CQuery next() {

	while (true) {
	    
	    int pos = getPositionRelaxGraph();
	    if (pos == -1) {
		return null;
	    }
	    if (!relaxed_comp_queries[pos].isEmpty()) {
		GraphRelaxationIndex relaxed_comp_node = relaxed_comp_queries[pos]
			.remove(0);
		for (int j = 0; j < relaxed_comp_node.getChild_elt().length; j++) {
		    this.insert_relaxation_comp_graph_node(
			    relaxed_comp_node.getChild_elt()[j], pos);
		}
		
		current_Session = comp_session[pos];
		already_relaxed_xss[pos].add(relaxed_comp_node);
		
		if(check_validity(relaxed_comp_node)){
		    current_relaxed_query = this.getCompQuery(relaxed_comp_node,
				pos);
		    return this.current_relaxed_query;
		}
		
	    } else {
		GraphRelaxationIndex relax_graph_node = relaxed_queries[pos]
			.remove(0);

		for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
		    this.insert_relaxation_graph_node(
			    relax_graph_node.getChild_elt()[j], pos);
		}
		current_Session = session;
		already_relaxed_xss[pos].add(relax_graph_node);
		current_relaxed_query = this.getQuery(relax_graph_node, pos);
		return this.current_relaxed_query;
	    }
	}
    }

    @Override
    public Session getCurrentView() {
	return current_Session;
    }

    /**
     * Check if a relax query relax all triple or not
     * 
     * @param relax_graph_node
     * @return
     */
    protected boolean check_validity(GraphRelaxationIndex relax_graph_node) {

	int i = 0;
	boolean has_zero = false;
	int[] current_relax_query = relax_graph_node.getElement_index();

	while ((i < current_relax_query.length) && (!has_zero)) {
	    has_zero = has_zero || current_relax_query[i] == 0;
	    i = i + 1;
	}
	return !has_zero;
    }

}
