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
public class XSSFineGrainedRelaxation extends AbstractXSSRelaxationStrategy {

    /**
     * XSS Similarity and XSS complement index
     */
    protected int[][] comp_xss_query_index;
    protected double[] similarity_comp_xss;

    /**
     * Relaxation for each complement
     */
    protected List<GraphRelaxationIndex>[] relaxed_comp_queries;

    /**
     * XSS complement query
     */
    protected CQuery[] xss_complement_queries;
    protected CQuery[] relaxed_comp_xss;

    /**
     * Initilization of data structures for complement of XSS
     * 
     * @param query
     * @param s
     */
    @SuppressWarnings("unchecked")
    protected void initilizationXSSComp(CQuery query, Session s,
	    boolean optimization) {

	Similarity sim_measure = new Similarity(s);
	comp_xss_query_index = new int[xss_to_relax_queries.length][];
	xss_complement_queries = new CQuery[xss_to_relax_queries.length];
	relaxed_comp_xss = new CQuery[xss_to_relax_queries.length];
	similarity_comp_xss = new double[xss_to_relax_queries.length];
	relaxed_comp_queries = new List[xss_to_relax_queries.length];

//	if (xss_to_relax_queries.length == 1) {
//	    if (xss_to_relax_queries[0].getElementList().size() == query_to_relax
//		    .getElementList().size()) {
//		xss_complement_queries[0] = query
//			.difference(xss_to_relax_queries[0]);
//		relaxed_comp_xss[0] = null;
//		comp_xss_query_index[0] = null;
//		relaxed_comp_queries[0] = new ArrayList<GraphRelaxationIndex>();
//		similarity_comp_xss[0] = 1.0;
//		return;
//	    }
//	}
	for (int i = 0; i < xss_to_relax_queries.length; i++) {
	    xss_complement_queries[i] = query
		    .difference(xss_to_relax_queries[i]);
	    similarity_comp_xss[i] = 1.0;
	    relaxed_comp_queries[i] = new ArrayList<GraphRelaxationIndex>();
	    if(xss_complement_queries[i]==null){
		relaxed_comp_xss[i] = null;
		continue;
	    }
	    comp_xss_query_index[i] = new int[xss_complement_queries[i]
		    .getElementList().size()];
	    int[] relaxation_index_comp_xss = new int[xss_complement_queries[i]
		    .getElementList().size()];
	    int[] relaxation_limit_comp_xss = new int[xss_complement_queries[i]
		    .getElementList().size()];
	    List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	    for (int j = 0; j < query_to_relax.getElementList().size(); j++) {
		CElement element = query_to_relax.getElementList().get(j);
		int index = xss_complement_queries[i].getElementList().indexOf(
			element);
		if (index != -1) {
		    elt_relaxed_query.add(element.supress_all_concrete());
		    comp_xss_query_index[i][index] = j;
		    relaxation_index_comp_xss[index] = 0;
		    relaxation_limit_comp_xss[index] = relaxation_of_element[j].length;
		    similarity_comp_xss[i] = similarity_comp_xss[i]
			    * sim_measure.suppressTripleMeasure(query
				    .getElementList().get(j));
		}
	    }
	    relaxed_comp_xss[i] = CQueryFactory.createCQuery(elt_relaxed_query,
		    query_to_relax.getSelectedQueryVar());
	    relaxed_comp_queries[i].add(new GraphRelaxationIndex(
		    relaxation_index_comp_xss, relaxation_limit_comp_xss,
		    !optimization));
	    GraphRelaxationIndex relax_graph_node = relaxed_queries[i]
		    .remove(0);
	    for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
		this.insert_relaxation_graph_node(
			relax_graph_node.getChild_elt()[j], i);
	    }
	}
    }

    /**
     * @param query
     * @param s
     */
    public XSSFineGrainedRelaxation(CQuery query, Session s) {
	super(query, s);
	initilizationXSSComp(query, s, true);
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public XSSFineGrainedRelaxation(CQuery query, Session s,
	    boolean optimization) {
	super(query, s, optimization);
	initilizationXSSComp(query, s, true);
    }

    @Override
    public CQuery next() {

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
	    already_relaxed_xss[pos].add(relaxed_comp_node);
	    current_relaxed_query = this.getCompQuery(relaxed_comp_node, pos);
	    
	} else {
	    GraphRelaxationIndex relax_graph_node = relaxed_queries[pos]
		    .remove(0);

	    for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
		this.insert_relaxation_graph_node(
			relax_graph_node.getChild_elt()[j], pos);
	    }

	    already_relaxed_xss[pos].add(relax_graph_node);
	    current_relaxed_query = this.getQuery(relax_graph_node, pos);
	}

	return this.current_relaxed_query;
    }

    @Override
    public Session getCurrentView() {
	return this.session;
    }

    @Override
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

    protected CQuery getCompQuery(GraphRelaxationIndex relaxed_comp_node,
	    int comp_xss_index) {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	elt_relaxed_query.addAll(xss_to_relax_queries[comp_xss_index]
		.getElementList());
	this.current_similarity = 1.0;
	this.current_satisfactory = xss_to_relax_queries[comp_xss_index]
		.getElementList().size();
	this.current_level = new ArrayList<int[]>();

	for (int i = 0; i < relaxed_comp_node.getElement_index().length; i++) {
	    int index_in_query = comp_xss_query_index[comp_xss_index][i];
	    elt_relaxed_query.add(getRelaxedElement(index_in_query,
		    relaxed_comp_node.getElement_index()[i]));

	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[index_in_query][relaxed_comp_node
			    .getElement_index()[i]].getSimilarity();

	    if (relaxed_comp_node.getElement_index()[i] == 0) {
		current_satisfactory = current_satisfactory + 1;
	    }

	    this.current_level
		    .add(relaxation_of_element[index_in_query][relaxed_comp_node
			    .getElement_index()[i]].getRelaxation_level());
	}

	this.current_satisfactory = current_satisfactory
		/ query_to_relax.getElementList().size();

	// logger.info(current_similarity+"      "+current_satisfactory);
	sim_sat.put(current_similarity, current_satisfactory);
	number_fine_grained_query_executed ++;
	
	return CQueryFactory.createCQuery(elt_relaxed_query,
		query_to_relax.getSelectedQueryVar());
    }

    protected int getPositionRelaxGraph() {

	int pos = -1;
	double sim_pos = 0;
	for (int i = 0; i < relaxed_comp_queries.length; i++) {
	    if (!relaxed_comp_queries[i].isEmpty()) {
		double current_sim = this.similarity_comp_graph_index(
			relaxed_comp_queries[i].get(0), i);
		if (current_sim > sim_pos) {
		    pos = i;
		    sim_pos = current_sim;
		}
	    } else {
		if (!relaxed_queries[i].isEmpty()) {
		    double current_sim = this.similarity_graph_index(
			    relaxed_queries[i].get(0), i);
		    if (current_sim > sim_pos) {
			pos = i;
			sim_pos = current_sim;
		    }
		}
	    }
	}

	return pos;
    }

    protected double similarity_comp_graph_index(
	    GraphRelaxationIndex relax_comp, int comp_xss_index) {

	double current_comp_similarity = 1.0;

	for (int i = 0; i < relax_comp.getElement_index().length; i++) {

	    int index_in_query = this.comp_xss_query_index[comp_xss_index][i];
	    current_comp_similarity = current_comp_similarity
		    * relaxation_of_element[index_in_query][relax_comp
			    .getElement_index()[i]].getSimilarity();
	}
	return current_comp_similarity;
    }

    protected void insert_relaxation_comp_graph_node(
	    GraphRelaxationIndex child, int comp_xss_index) {

	double current_child_similarity = this.similarity_comp_graph_index(
		child, comp_xss_index);
	int pos = 0;
	int index = relaxed_comp_queries[comp_xss_index].size() - 1;
	boolean found_pos = false;
	while ((!found_pos) && (0 <= index)) {
	    double index_sim = 1.0;
	    GraphRelaxationIndex elt_index = relaxed_comp_queries[comp_xss_index]
		    .get(index);
	    index_sim = this.similarity_comp_graph_index(
		    elt_index, comp_xss_index);
	    
//	    for (int i = 0; i < elt_index.getElement_index().length; i++) {
//		int index_in_query = this.comp_xss_query_index[comp_xss_index][i];
//		index_sim = index_sim
//			* relaxation_of_element[index_in_query][elt_index
//				.getElement_index()[i]].getSimilarity();
//	    }
	    found_pos = current_child_similarity <= index_sim;
	    pos = index + 1;
	    index = index - 1;
	}
	if (found_pos) {
	    relaxed_comp_queries[comp_xss_index].add(pos, child);
	} else {
	    relaxed_comp_queries[comp_xss_index].add(0, child);
	}
    }

    protected double similarity_graph_index(GraphRelaxationIndex child,
	    int xss_index) {

	double current_child_similarity = similarity_comp_xss[xss_index];

	for (int i = 0; i < child.getElement_index().length; i++) {

	    int index_in_query = this.xss_query_index[xss_index][i];
	    current_child_similarity = current_child_similarity
		    * relaxation_of_element[index_in_query][child
			    .getElement_index()[i]].getSimilarity();
	}
	return current_child_similarity;
    }
}
