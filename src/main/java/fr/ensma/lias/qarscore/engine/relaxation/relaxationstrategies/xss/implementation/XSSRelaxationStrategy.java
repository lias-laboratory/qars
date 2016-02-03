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

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.Similarity;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.xss.AbstractXSSRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class XSSRelaxationStrategy extends AbstractXSSRelaxationStrategy {

    /**
     * XSS Complement parameter
     */
    protected CQuery[] relaxed_comp_xss;
    protected double[] similarity_comp_xss;

    /**
     * @param query
     * @param s
     */
    public XSSRelaxationStrategy(CQuery query, Session s) {

	super(query, s);
	Similarity sim_measure = new Similarity(s);
	relaxed_comp_xss = new CQuery[xss_to_relax_queries.length];
	similarity_comp_xss = new double[xss_to_relax_queries.length];
	
//	if (xss_to_relax_queries.length == 1) {
//	    if (xss_to_relax_queries[0].getElementList().size() == query_to_relax
//		    .getElementList().size()) {
//		similarity_comp_xss[0] = 1.0;
//		relaxed_comp_xss[0] = null;
//		return;
//	    }
//	}
	for (int i = 0; i < xss_to_relax_queries.length; i++) {
	    List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	    CQuery comp_xss = query_to_relax
		    .difference(xss_to_relax_queries[i]);
	    similarity_comp_xss[i] = 1.0;
	    
	    if(comp_xss==null){
		relaxed_comp_xss[i] = null;
		continue;
	    }
	    for (int j = 0; j < comp_xss.getElementList().size(); j++) {
		elt_relaxed_query.add(comp_xss.getElementList().get(j)
			.supress_all_concrete());
		similarity_comp_xss[i] = similarity_comp_xss[i]
			* sim_measure.suppressTripleMeasure(comp_xss
				.getElementList().get(j));
	    }
	    relaxed_comp_xss[i] = CQueryFactory.createCQuery(elt_relaxed_query,
		    query_to_relax.getSelectedQueryVar());
	}
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public XSSRelaxationStrategy(CQuery query, Session s, boolean optimization) {

	super(query, s, optimization);
	Similarity sim_measure = new Similarity(s);
	relaxed_comp_xss = new CQuery[xss_to_relax_queries.length];
	similarity_comp_xss = new double[xss_to_relax_queries.length];

//	if (xss_to_relax_queries.length == 1) {
//	    if (xss_to_relax_queries[0].getElementList().size() == query_to_relax
//		    .getElementList().size()) {
//		similarity_comp_xss[0] = 1.0;
//		relaxed_comp_xss[0] = null;
//		return;
//	    }
//	}
	for (int i = 0; i < xss_to_relax_queries.length; i++) {
	    List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	    CQuery comp_xss = query_to_relax
		    .difference(xss_to_relax_queries[i]);
	    similarity_comp_xss[i] = 1.0;
	    
	    if(comp_xss==null){
		relaxed_comp_xss[i] = null;
		continue;
	    }
	    for (int j = 0; j < comp_xss.getElementList().size(); j++) {
		elt_relaxed_query.add(comp_xss.getElementList().get(j)
			.supress_all_concrete());
		similarity_comp_xss[i] = similarity_comp_xss[i]
			* sim_measure.suppressTripleMeasure(comp_xss
				.getElementList().get(j));
	    }
	    relaxed_comp_xss[i] = CQueryFactory.createCQuery(elt_relaxed_query,
		    query_to_relax.getSelectedQueryVar());
	}
    }

    @Override
    public CQuery next() {

	int pos = -1;
	double sim_pos = 0;
	for (int i = 0; i < relaxed_queries.length; i++) {
	    if (!relaxed_queries[i].isEmpty()) {
		double current_sim = this.similarity_graph_index(
			relaxed_queries[i].get(0), i);
		if (current_sim > sim_pos) {
		    pos = i;
		    sim_pos = current_sim;
		}
	    }
	}
	if (pos == -1) {
	    return null;
	}
	GraphRelaxationIndex relax_graph_node = relaxed_queries[pos].remove(0);

	for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
	    this.insert_relaxation_graph_node(
		    relax_graph_node.getChild_elt()[j], pos);
	}

	already_relaxed_xss[pos].add(relax_graph_node);
	current_relaxed_query = this.getQuery(relax_graph_node, pos);

	return current_relaxed_query;
    }

    @Override
    public Session getCurrentView() {
	return this.session;
    }

    /**
     * Convert a relaxation graph's node index into the corresponding query
     * 
     * @param index_query
     * @return
     */
    protected CQuery getQuery(GraphRelaxationIndex index_query, int xss_index) {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	elt_relaxed_query.addAll(relaxed_comp_xss[xss_index].getElementList());
	this.current_similarity = similarity_comp_xss[xss_index];
	this.current_satisfactory = 0.0;
	this.current_level = new ArrayList<int[]>();

	for (int i = 0; i < index_query.getElement_index().length; i++) {

	    int index_in_query = this.xss_query_index[xss_index][i];
	    elt_relaxed_query.add(getRelaxedElement(index_in_query,
		    index_query.getElement_index()[i]));

	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[index_in_query][index_query
			    .getElement_index()[i]].getSimilarity();

	    if (index_query.getElement_index()[i] == 0) {
		current_satisfactory = current_satisfactory + 1;
	    }

	    this.current_level
		    .add(relaxation_of_element[index_in_query][index_query
			    .getElement_index()[i]].getRelaxation_level());
	}

	this.current_satisfactory = current_satisfactory
		/ query_to_relax.getElementList().size();

	// logger.info(current_similarity+"      "+current_satisfactory);
	sim_sat.put(current_similarity, current_satisfactory);

	return CQueryFactory.createCQuery(elt_relaxed_query,
		query_to_relax.getSelectedQueryVar());
//	return CQueryFactory.createCQuery(elt_relaxed_query);
    }

    protected double similarity_graph_index(GraphRelaxationIndex child,
	    int xss_index) {

	double current_child_similarity = similarity_comp_xss[xss_index];

	for (int i = 0; i < child.getElement_index().length; i++) {

	    int relax_rank = child.getElement_index()[i];
	    int index_in_query = this.xss_query_index[xss_index][i];
	    current_child_similarity = current_child_similarity
		    * relaxation_of_element[index_in_query][relax_rank]
			    .getSimilarity();
	}

	return current_child_similarity;
    }
}
