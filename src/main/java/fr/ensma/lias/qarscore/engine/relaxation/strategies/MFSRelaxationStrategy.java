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
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class MFSRelaxationStrategy extends GraphRelaxationStrategy {

    protected MFSSearch mfs_finders;
    protected Map<GraphRelaxationIndex, List<Integer>> mfs_relaxed_queries;
    protected List<GraphRelaxationIndex> failed_relaxed_queries;
    protected List<List<int[]>> mfs_degree_by_index;
    protected List<RoaringBitmap> mfs_elt_index;
    protected final RoaringBitmap QUERY;

    /**
     * 
     */
    public MFSRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
	QUERY = new RoaringBitmap();
	QUERY.add(0, query.getElementList().size() - 1);
	mfs_relaxed_queries = new LinkedHashMap<GraphRelaxationIndex, List<Integer>>();
	failed_relaxed_queries = new ArrayList<GraphRelaxationIndex>();

	mfs_finders = StrategyFactory.getLatticeStrategy(s, query, 1);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	mfs_elt_index = new ArrayList<RoaringBitmap>();
	mfs_degree_by_index = new ArrayList<List<int[]>>();
	for (int i = 0; i < all_mfs.size(); i++) {
	    mfs_elt_index.add(i, new RoaringBitmap());
	    mfs_degree_by_index.add(i, new ArrayList<int[]>());
	    int [] relax_degree = new int[all_mfs.get(i).getElementList().size()];
	    for( int j = 0; j<relax_degree.length; j++){
		relax_degree[j] = 0;
	    }
	    mfs_degree_by_index.get(i).add(relax_degree);
	}
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    for (int j = 0; j < all_mfs.size(); j++) {
		if (all_mfs.get(j).contain(
			query_to_relax.getElementList().get(i))) {
		    mfs_elt_index.get(j).add(i);
		}
	    }
	}
    }

    /**
     * 
     */
    public MFSRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s);
	QUERY = new RoaringBitmap();
	QUERY.add(0, query.getElementList().size() - 1);
	mfs_relaxed_queries = new LinkedHashMap<GraphRelaxationIndex, List<Integer>>();
	failed_relaxed_queries = new ArrayList<GraphRelaxationIndex>();

	MFSSearch mfs_finders = StrategyFactory.getLatticeStrategy(s, query, 1,
		index);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	mfs_elt_index = new ArrayList<RoaringBitmap>();
	for (int i = 0; i < all_mfs.size(); i++) {
	    mfs_elt_index.add(i,  new RoaringBitmap());
	    mfs_degree_by_index.add(i, new ArrayList<int[]>());
	    int [] relax_degree = new int[all_mfs.get(i).getElementList().size()];
	    for( int j = 0; j<relax_degree.length; j++){
		relax_degree[j] = 0;
	    }
	    mfs_degree_by_index.get(i).add(relax_degree);
	}
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    for (int j = 0; j < all_mfs.size(); j++) {
		if (all_mfs.get(j).contain(
			query_to_relax.getElementList().get(i))) {
		    mfs_elt_index.get(j).add(i);
		}
	    }
	}
    }

    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}

	GraphRelaxationIndex relax_graph_node = relaxed_queries.remove(0);

	this.current_similarity = 1.0;
	this.current_level = new ArrayList<int[]>();
	for (int i = 0; i < relax_graph_node.getElement_index().length; i++) {

	    elt_relaxed_query.add(getRelaxedElement(i,
		    relax_graph_node.getElement_index()[i]));

	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][relax_graph_node
			    .getElement_index()[i]].getSimilarity();
	    this.current_level.add(relaxation_of_element[i][relax_graph_node
		    .getElement_index()[i]].getRelaxation_level());
	}

	for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
	    this.insert(relax_graph_node.getChild_elt()[j]);
	}

	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query,
		query_to_relax.getSelectedQueryVar());
	boolean has_mfs = check_mfs(relax_graph_node);

	if (!has_mfs) {
	    already_relaxed_queries.add(relax_graph_node);
	    return current_relaxed_query;
	} else {
	    return this.next();
	}
    }

    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {

	int i = 0;
	boolean has_mfs = false;
	List<Integer> mfs_current_query = new ArrayList<Integer>();
	int[] current_relax_query = relax_graph_node.getElement_index();

	while ((i < mfs_elt_index.size()) && (!has_mfs)) {
	    has_mfs = true;
	    for (int j = 0; j < mfs_elt_index.get(i).getCardinality(); j++) {
		has_mfs = has_mfs
			&& current_relax_query[mfs_elt_index.get(i).select(j)] == 0;
	    }
	    i = i + 1;
	}

	if (has_mfs) {
	    mfs_current_query.add(i-1);
	    failed_relaxed_queries.add(relax_graph_node);
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	}
	return has_mfs;
    }

    public MFSSearch getMFSSearchEngine() {
	return mfs_finders;
    }
}
