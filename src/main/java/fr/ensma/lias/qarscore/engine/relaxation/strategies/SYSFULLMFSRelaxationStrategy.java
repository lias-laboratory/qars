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
package fr.ensma.lias.qarscore.engine.relaxation.strategies;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.roaringbitmap.RoaringBitmap;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class SYSFULLMFSRelaxationStrategy extends INCFULLMFSRelaxationStrategy {

    protected Map<GraphRelaxationIndex, List<Integer>> mfs_repaired_queries;

    /**
     * 
     */
    public SYSFULLMFSRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
	mfs_repaired_queries = new LinkedHashMap<GraphRelaxationIndex, List<Integer>>();
    }

    /**
     * 
     */
    public SYSFULLMFSRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s, index);
	mfs_repaired_queries = new LinkedHashMap<GraphRelaxationIndex, List<Integer>>();
    }

    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {

	GraphRelaxationIndex least_relaxed_ancestor = getLeastRelaxedAncestor(
		failed_relaxed_queries, relax_graph_node);

	List<Integer> mfs_current_query = new ArrayList<Integer>();

	if (least_relaxed_ancestor != null) {
	    if (mfs_relaxed_queries.get(least_relaxed_ancestor) != null) {
		return check_on_mfs_relaxed(relax_graph_node,
			least_relaxed_ancestor, mfs_current_query);
	    } else {
		return check_on_allmfs(relax_graph_node,
			least_relaxed_ancestor, mfs_current_query);
	    }
	} else {
	    return check_on_allmfs(relax_graph_node, least_relaxed_ancestor,
		    mfs_current_query);
	}
    }

    protected boolean check_on_allmfs(GraphRelaxationIndex relax_graph_node,
	    GraphRelaxationIndex least_relaxed_ancestor,
	    List<Integer> mfs_current_query) {

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<Integer> current_mfs_relaxed = new ArrayList<Integer>();
	List<int[]> degree_mfs_relaxed = new ArrayList<int[]>();

	int i = 0;
	while (i < mfs_elt_index.size()) {
	    int[] current_mfs_degree = new int[mfs_elt_index.get(i)
		    .getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(i).getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index
			.get(i).select(j)];
	    }
	    boolean is_mfs = is_in_mfs_degree(i, current_mfs_degree);
	    if (is_mfs) {
		mfs_current_query.add(i);
	    } else {
		current_mfs_relaxed.add(i);
		degree_mfs_relaxed.add(current_mfs_degree);
	    }
	    i = i + 1;
	}

	if (!mfs_current_query.isEmpty()) {
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    failed_relaxed_queries.add(relax_graph_node);
	    return true;
	}

	List<Integer> mfs_repaired_current_query = add_relaxed_mfs(
		relax_graph_node, current_mfs_relaxed, degree_mfs_relaxed);

	if (mfs_repaired_current_query.size() != current_mfs_relaxed.size()) {
	    mfs_repaired_queries.put(relax_graph_node, mfs_repaired_current_query);
	    return true;
	}

	if (least_relaxed_ancestor != null) {
	    if (mfs_repaired_queries.get(least_relaxed_ancestor) != null) {
		mfs_repaired_current_query.addAll(mfs_repaired_queries
			.get(least_relaxed_ancestor));
	    }
	}

	return add_new_mfs(relax_graph_node, mfs_repaired_current_query);

    }

    protected boolean check_on_mfs_relaxed(
	    GraphRelaxationIndex relax_graph_node,
	    GraphRelaxationIndex least_relaxed_ancestor,
	    List<Integer> mfs_current_query) {

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<Integer> current_mfs_relaxed = new ArrayList<Integer>();
	List<int[]> degree_mfs_relaxed = new ArrayList<int[]>();
	List<Integer> potential_mfs = mfs_relaxed_queries
		.get(least_relaxed_ancestor);
	int i = 0;
	while (i < potential_mfs.size()) {
	    int[] current_mfs_degree = new int[mfs_elt_index.get(
		    potential_mfs.get(i)).getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(potential_mfs.get(i))
		    .getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index.get(
			potential_mfs.get(i)).select(j)];
	    }
	    boolean is_mfs = is_in_mfs_degree(potential_mfs.get(i),
		    current_mfs_degree);
	    if (is_mfs) {
		mfs_current_query.add(potential_mfs.get(i));
	    } else {
		current_mfs_relaxed.add(i);
		degree_mfs_relaxed.add(current_mfs_degree);
	    }
	    i = i + 1;
	}
	
	if (!mfs_current_query.isEmpty()) {
	    //mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    failed_relaxed_queries.add(relax_graph_node);
	    if (mfs_current_query.size() == potential_mfs.size()) {
		mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    }
	    return true;
	}

	List<Integer> mfs_repaired_current_query = add_relaxed_mfs(
		relax_graph_node, current_mfs_relaxed, degree_mfs_relaxed);

	if (mfs_repaired_current_query.size() != current_mfs_relaxed.size()) {
	    mfs_repaired_queries.put(relax_graph_node, mfs_repaired_current_query);
	    return true;
	}

	if (least_relaxed_ancestor != null) {
	    if (mfs_repaired_queries.get(least_relaxed_ancestor) != null) {
		mfs_repaired_current_query.addAll(mfs_repaired_queries
			.get(least_relaxed_ancestor));
	    }
	}

	return add_new_mfs(relax_graph_node, mfs_repaired_current_query);
    }

    protected boolean add_new_mfs(GraphRelaxationIndex relax_graph_node,
	    List<Integer> mfs_repaired_current_query) {

	// if (!mfs_current_query.isEmpty()) {
	// mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	// if (potential_repaired_mfs != null) {
	// mfs_repaired_current_query.addAll(potential_repaired_mfs);
	// }
	// if (!mfs_repaired_current_query.isEmpty()) {
	// mfs_repaired_queries.put(relax_graph_node,
	// mfs_repaired_current_query);
	// }
	//
	// return true;
	// }

	int[] current_relax_query = relax_graph_node.getElement_index();

	List<Integer> mfs_current_query = new ArrayList<Integer>();
	List<RoaringBitmap> new_mfs_founded = full_update_mfs(
		current_relax_query, mfs_current_query,
		mfs_repaired_current_query);

	for (int k = 0; k < new_mfs_founded.size(); k++) {
	    int index = mfs_elt_index.size();
	    mfs_elt_index.add(new_mfs_founded.get(k));
	    mfs_degree_by_index.add(new ArrayList<int[]>());
	    int[] current_mfs_degree = new int[mfs_elt_index.get(index)
		    .getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(index).getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index.get(
			index).select(j)];
	    }
	    mfs_degree_by_index.get(index).add(current_mfs_degree);
	    mfs_current_query.add(index);
	}

	if (!mfs_current_query.isEmpty()) {
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    return true;
	}
	return false;
    }
}
