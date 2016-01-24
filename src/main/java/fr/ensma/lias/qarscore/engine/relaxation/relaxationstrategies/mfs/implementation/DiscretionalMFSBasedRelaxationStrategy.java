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
package fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class DiscretionalMFSBasedRelaxationStrategy extends
	IncrementalMFSBaseRelaxationStrategy {

    /**
     * @param query
     * @param s
     */
    public DiscretionalMFSBasedRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public DiscretionalMFSBasedRelaxationStrategy(CQuery query, Session s,
	    boolean optimization) {
	super(query, s, optimization);
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

	    int[] current_mfs_degree = new int[mfs_elt_index.get(i).length];
	    for (int j = 0; j < mfs_elt_index.get(i).length; j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index
			.get(i)[j]];
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

	    if(!relaxed_mfs.isEmpty()){
		return false;
	    }
	    return !add_new_mfs(relax_graph_node, relaxed_mfs,
			    repaired_mfs).isEmpty();
	}
    }
}
