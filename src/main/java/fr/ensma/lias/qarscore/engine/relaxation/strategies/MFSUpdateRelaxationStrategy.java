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
import java.util.List;

import org.roaringbitmap.RoaringBitmap;

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
public class MFSUpdateRelaxationStrategy extends MFSRelaxationStrategy {

    public int number_check_queries = 0;
    
    /**
     * 
     */
    public MFSUpdateRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
    }

    /**
     * 
     */
    public MFSUpdateRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s, index);
    }

    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}

	GraphRelaxationIndex current_graph = relaxed_queries.remove(0);

	this.current_similarity = 1.0;
	this.current_level = new ArrayList<int[]>();
	for (int i = 0; i < current_graph.getElement_index().length; i++) {

	    elt_relaxed_query.add(getRelaxedElement(i,
		    current_graph.getElement_index()[i]));

	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getSimilarity();
	    this.current_level.add(relaxation_of_element[i][current_graph
		    .getElement_index()[i]].getRelaxation_level());
	}

	for (int j = 0; j < current_graph.getChild_elt().length; j++) {
	    this.insert(current_graph.getChild_elt()[j]);
	}

	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query,
		query_to_relax.getSelectedQueryVar());
	boolean has_mfs = check_mfs(current_graph);

	if (!has_mfs) {
	    already_relaxed_queries.add(current_graph);
	    return current_relaxed_query;
	} else {
	    return this.next();
	}
    }

    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {

	GraphRelaxationIndex least_relaxed_ancestor = getLeastRelaxedAncestor(
		failed_relaxed_queries, relax_graph_node);
	List<RoaringBitmap> potential_mfs = null;
	if (least_relaxed_ancestor != null) {
	    potential_mfs = mfs_relaxed_queries.get(least_relaxed_ancestor);
	}

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<RoaringBitmap> mfs_current_query = new ArrayList<RoaringBitmap>();

	if (potential_mfs == null) {
	    int i = 0;
	    while (i < MFS_QUERY.length) {
		boolean is_mfs = true;
		for (int j = 0; j < MFS_QUERY[i].getCardinality(); j++) {
		    is_mfs = is_mfs
			    && current_relax_query[MFS_QUERY[i].select(j)] == 0;
		}
		if (is_mfs) {
		    mfs_current_query.add(MFS_QUERY[i]);
		} else {
		    List<CElement> updated_mfs_query = get_element_list(
			    current_relax_query, MFS_QUERY[i]);

		    number_check_queries = number_check_queries + 1;
		    QueryStatement stm = session.createStatement((CQueryFactory
			    .createCQuery(updated_mfs_query)).toString());
		    
		    if (stm.getResultSetSize(1) == 0) {
			mfs_current_query.add(MFS_QUERY[i]);
			is_mfs = true;
		    }
		}
		i = i + 1;
	    }
	    if (!mfs_current_query.isEmpty()) {
		mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
		failed_relaxed_queries.add(relax_graph_node);
	    }
	    return !mfs_current_query.isEmpty();
	}

	int i = 0;
	while (i < potential_mfs.size()) {
	    boolean is_mfs = true;
	    for (int j = 0; j < potential_mfs.get(i).getCardinality(); j++) {
		is_mfs = is_mfs
			&& current_relax_query[potential_mfs.get(i).select(j)] == least_relaxed_ancestor
				.getElement_index()[potential_mfs.get(i)
				.select(j)];
	    }
	    if (is_mfs) {
		mfs_current_query.add(potential_mfs.get(i));
	    } else {
		List<CElement> updated_mfs_query = get_element_list(
			current_relax_query, potential_mfs.get(i));

		number_check_queries = number_check_queries + 1;
		QueryStatement stm = session.createStatement((CQueryFactory
			.createCQuery(updated_mfs_query)).toString());
		
		if (stm.getResultSetSize(1) == 0) {
		    mfs_current_query.add(potential_mfs.get(i));
		    is_mfs = true;
		}
	    }
	    i = i + 1;
	}
	if (!mfs_current_query.isEmpty()) {
	    failed_relaxed_queries.add(relax_graph_node);
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	}
	return !mfs_current_query.isEmpty();
    }

    protected List<CElement> get_element_list(int[] current_relax_query,
	    RoaringBitmap current_mfs_index) {

	List<CElement> elt_form = new ArrayList<CElement>();
	for (int i = 0; i < current_mfs_index.getCardinality(); i++) {
	    elt_form.add(this.getRelaxedElement(current_mfs_index.select(i),
		    current_relax_query[current_mfs_index.select(i)]));
	}
	return elt_form;
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
			already_failed_relaxed_queries.get(i).getElement_index()[j]);
		is_relaxation = is_relaxation
			&& TripleRelaxation.is_relaxation(relax_elt,
				father_elt, session);
		j = j + 1;
	    }
	    i = i - 1;
	}

	return already_failed_relaxed_queries.get(i + 1);
    }
}
