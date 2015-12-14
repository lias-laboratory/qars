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
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class MFSBaseOptimizedRelaxationStrategy extends
	MFSBaseRelaxationStrategy {

    protected List<List<int[]>> repaire_mfs_degree_by_index;

    /**
     * @param query
     * @param s
     */
    public MFSBaseOptimizedRelaxationStrategy(CQuery query, Session s) {
	super(query, s);

	repaire_mfs_degree_by_index = new ArrayList<List<int[]>>();
	for (int i = 0; i < mfs_elt_index.size(); i++) {
	    repaire_mfs_degree_by_index.add(i, new ArrayList<int[]>());
	}
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public MFSBaseOptimizedRelaxationStrategy(CQuery query, Session s,
	    boolean optimization) {
	super(query, s, optimization);
	repaire_mfs_degree_by_index = new ArrayList<List<int[]>>();
	for (int i = 0; i < mfs_elt_index.size(); i++) {
	    repaire_mfs_degree_by_index.add(i, new ArrayList<int[]>());
	}
    }

    public CQuery next() {

	while (true) {

	    if (this.relaxed_queries.isEmpty()) {
		return null;
	    }

	    GraphRelaxationIndex relax_graph_node = relaxed_queries.remove(0);

	    for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
		this.insert_relaxation_graph_node(relax_graph_node
			.getChild_elt()[j]);
	    }

	    boolean has_mfs = check_mfs(relax_graph_node);
	    current_relaxed_query = this.getQuery(relax_graph_node);

	    if (!has_mfs) {
		already_relaxed_queries.add(relax_graph_node);
		return current_relaxed_query;
	    }
	    
	    logger.info("Failed Query "+current_relaxed_query.toString());
	}
    }

    /**
     * Check if a relax query include or not an MFS
     * 
     * @param relax_graph_node
     * @return
     */
    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {

	int i = 0;
	boolean has_mfs = false;
	List<Integer> relaxed_mfs = new ArrayList<Integer>();
	List<int[]> degree_relaxed_mfs = new ArrayList<int[]>();

	int[] current_relax_query = relax_graph_node.getElement_index();

	while ((i < mfs_elt_index.size()) && (!has_mfs)) {

	    int[] current_mfs_degree = new int[mfs_elt_index.get(i)
		    .getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(i).getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index
			.get(i).select(j)];
	    }

	    boolean is_mfs = is_in_mfs_degree(mfs_degree_by_index.get(i),
		    current_mfs_degree);

	    if ((!is_mfs)
		    && (!is_in_mfs_degree(repaire_mfs_degree_by_index.get(i),
			    current_mfs_degree))) {
		relaxed_mfs.add(i);
		degree_relaxed_mfs.add(current_mfs_degree);
	    }
	    has_mfs = has_mfs || is_mfs;
	    i = i + 1;
	}

	if (has_mfs) {
	    failed_relaxed_queries.add(relax_graph_node);
	    return has_mfs;
	} else {
	    List<Integer> repaired_mfs = add_relaxed_mfs(relax_graph_node,
		    relaxed_mfs, degree_relaxed_mfs);
	    relaxed_mfs.removeAll(repaired_mfs);
	    return !relaxed_mfs.isEmpty();
	}
    }

    protected GraphRelaxationIndex getLeastRelaxedAncestor(
	    List<GraphRelaxationIndex> already_failed_relaxed_queries,
	    GraphRelaxationIndex relax_graph_node) {

	if (already_failed_relaxed_queries.size() == 0) {
	    return null;
	}
	int i = already_failed_relaxed_queries.size() - 1;
	boolean is_relaxation = false;
	while ((0 <= i) && (!is_relaxation)) {
	    is_relaxation = true;
	    int j = 0;
	    while ((j < relax_graph_node.getElement_index().length)
		    && (is_relaxation)) {
		CElement relax_elt = getRelaxedElement(j,
			relax_graph_node.getElement_index()[j]);
		CElement father_elt = getRelaxedElement(j,
			already_failed_relaxed_queries.get(i)
				.getElement_index()[j]);
		is_relaxation = is_relaxation
			&& TripleRelaxation.is_relaxation(relax_elt,
				father_elt, session);
		j = j + 1;
	    }
	    i = i - 1;
	}

	return already_failed_relaxed_queries.get(i + 1);
    }

    /**
     * Find a set of relaxation step in a list of relaxation step of an MFS
     * 
     * @param mfs_degrees
     * @param current_mfs_degree
     * @return
     */
    protected boolean is_in_mfs_degree(List<int[]> mfs_degrees,
	    int[] current_mfs_degree) {

	if (mfs_degrees.size() == 0) {
	    return false;
	}

	if (current_mfs_degree.length != mfs_degrees.get(0).length) {
	    return false;
	}
	int j = mfs_degrees.size() - 1;
	while (0 <= j) {
	    boolean is_in = true;
	    int k = 0;
	    while ((k < current_mfs_degree.length) && (is_in)) {
		is_in = is_in
			&& (mfs_degrees.get(j)[k] == current_mfs_degree[k]);
		k = k + 1;
	    }
	    if (is_in) {
		return true;
	    }
	    j = j - 1;
	}
	return false;
    }

    /**
     * Check if relaxed MFS are still MFS and add them into the list of current
     * MFS Return the MFS which are been repaired
     * 
     * @param relax_graph_node
     * @param current_mfs_relaxed
     * @param degree_mfs_relaxed
     * @return
     */
    protected List<Integer> add_relaxed_mfs(
	    GraphRelaxationIndex relax_graph_node,
	    List<Integer> current_mfs_relaxed, List<int[]> degree_mfs_relaxed) {

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<Integer> repaired_mfs = new ArrayList<Integer>();

	boolean has_mfs = false;

	for (int i = 0; i < current_mfs_relaxed.size(); i++) {
	    List<CElement> updated_mfs_query = get_element_list(
		    current_relax_query,
		    mfs_elt_index.get(current_mfs_relaxed.get(i)));

	    this.number_mfs_check_query_executed = this.number_mfs_check_query_executed + 1;
	    CQuery mfs_query = CQueryFactory.createCQuery(updated_mfs_query);
	    QueryStatement stm = session.createStatement(mfs_query.toString());
	    long begin = System.currentTimeMillis();
	    if (stm.getResultSetSize(1) == 0) {
		mfs_degree_by_index.get(current_mfs_relaxed.get(i)).add(
			degree_mfs_relaxed.get(i));
		has_mfs = true;
		logger.info("New MFS " + mfs_query.toString());
	    } else {
		repaired_mfs.add(current_mfs_relaxed.get(i));
		repaire_mfs_degree_by_index.get(current_mfs_relaxed.get(i))
			.add(degree_mfs_relaxed.get(i));
	    }
	    this.duration__mfs_check_query_executed = this.duration__mfs_check_query_executed
		    + (System.currentTimeMillis() - begin);
	}
	if (has_mfs) {
	    failed_relaxed_queries.add(relax_graph_node);
	}
	return repaired_mfs;
    }
}
