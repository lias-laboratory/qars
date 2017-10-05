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
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.AbstractMFSRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class MFSBaseRelaxationStrategy extends AbstractMFSRelaxationStrategy {

    protected List<GraphRelaxationIndex> failed_relaxed_queries;
    protected List<List<int[]>> mfs_degree_by_index;
    protected List<int[]> mfs_elt_index;
    protected MFSSearch mfs_finders;
    protected int[] query_index;

    /**
     * @param query
     * @param s
     */
    public MFSBaseRelaxationStrategy(CQuery query, Session s) {
	super(query, s, true);

	query_index = new int[query.getElementList().size()];
	for(int i=0; i<query.getElementList().size(); i++){
	    query_index[i] = i;
	}
	failed_relaxed_queries = new ArrayList<GraphRelaxationIndex>();

	mfs_finders = StrategyFactory.getLatticeStrategy(s, query, MFS_LIMIT_ANSWERS);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	this.number_mfs_query_executed = ((AbstractLatticeStrategy)mfs_finders).number_of_query_executed;
	this.duration_mfs_query_executed = ((AbstractLatticeStrategy)mfs_finders).duration_of_execution;
	
	mfs_elt_index = new ArrayList<int[]>();
	mfs_degree_by_index = new ArrayList<List<int[]>>();
	for (int i = 0; i < all_mfs.size(); i++) {
	    mfs_elt_index.add(i,  new int[all_mfs.get(i).getElementList().size()]);
	    mfs_degree_by_index.add(i, new ArrayList<int[]>());
	    int[] relax_degree = new int[all_mfs.get(i).getElementList().size()];
	    for (int j = 0; j < relax_degree.length; j++) {
		relax_degree[j] = 0;
	    }
	    mfs_degree_by_index.get(i).add(relax_degree);
	}

	 for (int j = 0; j < all_mfs.size(); j++) {
	     int k = 0;
	     for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
		 if (all_mfs.get(j).contain(
				query_to_relax.getElementList().get(i))) {
		     mfs_elt_index.get(j)[k] = i;
		     k = k + 1;
		 }
	     }
	 }
    }

    /**
     * @param query
     * @param s
     * @param optimization
     */
    public MFSBaseRelaxationStrategy(CQuery query, Session s,
	    boolean optimization) {
	
	super(query, s, true);

	query_index = new int[query.getElementList().size()];
	for(int i=0; i<query.getElementList().size(); i++){
	    query_index[i] = i;
	}
	failed_relaxed_queries = new ArrayList<GraphRelaxationIndex>();

	mfs_finders = StrategyFactory.getLatticeStrategy(s, query, 1,
		optimization);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	this.number_mfs_query_executed = ((AbstractLatticeStrategy)mfs_finders).number_of_query_executed;
	this.duration_mfs_query_executed = ((AbstractLatticeStrategy)mfs_finders).duration_of_execution;
	
	mfs_elt_index = new ArrayList<int[]>();
	mfs_degree_by_index = new ArrayList<List<int[]>>();
	for (int i = 0; i < all_mfs.size(); i++) {
	    mfs_elt_index.add(i,  new int[all_mfs.get(i).getElementList().size()]);
	    mfs_degree_by_index.add(i, new ArrayList<int[]>());
	    int[] relax_degree = new int[all_mfs.get(i).getElementList().size()];
	    for (int j = 0; j < relax_degree.length; j++) {
		relax_degree[j] = 0;
	    }
	    mfs_degree_by_index.get(i).add(relax_degree);
	}

	 for (int j = 0; j < all_mfs.size(); j++) {
	     int k = 0;
	     for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
		 if (all_mfs.get(j).contain(
				query_to_relax.getElementList().get(i))) {
		     mfs_elt_index.get(j)[k]=i;
		 }
	     }
	 }
    }

    @Override
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
	int[] current_relax_query = relax_graph_node.getElement_index();

	while ((i < mfs_elt_index.size()) && (!has_mfs)) {
	    has_mfs = true;
	    for (int j = 0; j < mfs_elt_index.get(i).length; j++) {
		has_mfs = has_mfs
			&& current_relax_query[mfs_elt_index.get(i)[j]] == 0;
	    }
	    i = i + 1;
	}

	if (has_mfs) {
	    failed_relaxed_queries.add(relax_graph_node);
	}
	return has_mfs;
    }

    /**
     * Get the MFS search engine uses for finding MFS
     * 
     * @return
     */
    public MFSSearch getMFSSearchEngine() {
	return mfs_finders;
    }

    /**
     * Get the relaxed element of the MFS of a relaxed query
     * @param current_relax_query
     * @param current_mfs_index
     * @return
     */
    protected List<CElement> get_element_list(int[] current_relax_query,
	    int[] current_mfs_index) {

	List<CElement> elt_form = new ArrayList<CElement>();
	for (int i = 0; i < current_mfs_index.length; i++) {
	    elt_form.add(this.getRelaxedElement(current_mfs_index[i],
		    current_relax_query[current_mfs_index[i]]));
	}
	return elt_form;
    }
}
