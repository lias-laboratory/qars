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

import org.roaringbitmap.RoaringBitmap;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class IncrementalMFSBaseRelaxationStrategy extends
	MFSBaseOptimizedRelaxationStrategy {

    /**
     * @param query
     * @param s
     */
    public IncrementalMFSBaseRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public IncrementalMFSBaseRelaxationStrategy(CQuery query, Session s,
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

	    return (!relaxed_mfs.isEmpty())
		    || (!add_new_mfs(relax_graph_node, relaxed_mfs,
			    repaired_mfs).isEmpty());
	}
    }

    protected List<RoaringBitmap> add_new_mfs(
	    GraphRelaxationIndex relax_graph_node,
	    List<Integer> mfs_current_query,
	    List<Integer> mfs_repaired_current_query) {

	List<RoaringBitmap> new_mfs_founded = new ArrayList<RoaringBitmap>();
	int[] current_relax_query = relax_graph_node.getElement_index();

	if (mfs_repaired_current_query.isEmpty()) {
	    return new_mfs_founded;
	}

	List<CQuery> mfs_subqueries = new ArrayList<CQuery>();
	List<CQuery> elt_mfs_subqueries = new ArrayList<CQuery>();
	for (int j = 0; j < mfs_current_query.size(); j++) {
	    RoaringBitmap current_mfs_index = mfs_elt_index
		    .get(mfs_current_query.get(j));
	    mfs_subqueries.add(CQueryFactory.createCQuery(get_element_list(
		    current_relax_query, current_mfs_index)));
	}

	for (int j = 0; j < mfs_repaired_current_query.size(); j++) {
	    RoaringBitmap current_sub_mfs_index = mfs_elt_index
		    .get(mfs_repaired_current_query.get(j));
	    elt_mfs_subqueries.add(CQueryFactory.createCQuery(get_element_list(
		    current_relax_query, current_sub_mfs_index)));
	}

	CQuery current_relaxed_query = CQueryFactory
		.createCQuery(get_element_list(current_relax_query,
			this.query_index));

	long begin = System.currentTimeMillis();
	List<CQuery> mfs_list = this.mfs_finders.getOtherMFS(
		current_relaxed_query, mfs_subqueries, elt_mfs_subqueries);
	this.duration__mfs_check_query_executed = this.duration__mfs_check_query_executed + (System.currentTimeMillis()-begin);
	this.number_mfs_check_query_executed = this.number_mfs_check_query_executed +  ((AbstractLatticeStrategy)this.mfs_finders).number_of_query_executed;
	
	for (int j = 0; j < mfs_list.size(); j++) {
	    logger.info("New MFS "+mfs_list.get(j).toString());
	    
	    new_mfs_founded.add(j, new RoaringBitmap());
	    List<int[]> degree_new_mfs_founded = new ArrayList<int[]>();
	    degree_new_mfs_founded.add(new int[mfs_list.get(j).getElementList()
		    .size()]);
	    for(int i=0; i<current_relax_query.length; i++){
		CElement elt = this.getRelaxedElement(i, current_relax_query[i]);
		int k = 0;
		if (mfs_list.get(j).contain(elt)) {
		    new_mfs_founded.get(j).add(i);
		    degree_new_mfs_founded.get(0)[k] = current_relax_query[i];
		}
	    }
	    mfs_elt_index.add(new_mfs_founded.get(j));
	    mfs_degree_by_index.add(degree_new_mfs_founded);
	}
	
	return new_mfs_founded;
    }
}
