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
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class SYSFULLMFSRelaxationStrategy extends INCFULLMFSRelaxationStrategy {

    protected Map<GraphRelaxationIndex, List<RoaringBitmap>> mfs_repaired_queries;

    /**
     * 
     */
    public SYSFULLMFSRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
	mfs_repaired_queries = new LinkedHashMap<GraphRelaxationIndex, List<RoaringBitmap>>();
    }

    /**
     * 
     */
    public SYSFULLMFSRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s, index);
	mfs_repaired_queries = new LinkedHashMap<GraphRelaxationIndex, List<RoaringBitmap>>();
    }

    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {

	GraphRelaxationIndex least_relaxed_ancestor = getLeastRelaxedAncestor(
		already_relaxed_queries, relax_graph_node);
	List<RoaringBitmap> potential_mfs = mfs_relaxed_queries
		.get(least_relaxed_ancestor);
	List<RoaringBitmap> potential_repaired_mfs = mfs_repaired_queries
		.get(least_relaxed_ancestor);

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<RoaringBitmap> mfs_current_query = new ArrayList<RoaringBitmap>();
	List<RoaringBitmap> mfs_repaired_current_query = new ArrayList<RoaringBitmap>();

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
		    } else {
			mfs_repaired_current_query.add(MFS_QUERY[i]);
		    }
		}
		i = i + 1;
	    }
	    if (!mfs_current_query.isEmpty()) {
		mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
		mfs_repaired_current_query.addAll(potential_repaired_mfs);
		mfs_repaired_queries.put(relax_graph_node,
			mfs_repaired_current_query);
		return true;
	    }
	    mfs_repaired_current_query.addAll(potential_repaired_mfs);

	    List<RoaringBitmap> new_mfs_founded = full_update_mfs(
		    current_relax_query, mfs_current_query,
		    mfs_repaired_current_query);
	    mfs_current_query.addAll(new_mfs_founded);

	    if (!mfs_current_query.isEmpty()) {
		mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
		mfs_repaired_queries.put(relax_graph_node,
			mfs_repaired_current_query);
		return true;
	    }
	    return false;
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
		} else {
		    mfs_repaired_current_query.add(potential_mfs.get(i));
		}
	    }
	    i = i + 1;
	}
	if (!mfs_current_query.isEmpty()) {
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    mfs_repaired_current_query.addAll(potential_repaired_mfs);
	    mfs_repaired_queries.put(relax_graph_node,
		    mfs_repaired_current_query);
	    return true;
	}

	mfs_repaired_current_query.addAll(potential_repaired_mfs);

	List<RoaringBitmap> new_mfs_founded = full_update_mfs(
		current_relax_query, mfs_current_query,
		mfs_repaired_current_query);
	mfs_current_query.addAll(new_mfs_founded);

	if (!mfs_current_query.isEmpty()) {
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    mfs_repaired_queries.put(relax_graph_node,
		    mfs_repaired_current_query);
	    return true;
	}
	return false;
    }

}
