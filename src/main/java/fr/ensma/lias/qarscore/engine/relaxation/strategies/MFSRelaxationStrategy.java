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
    protected Map<GraphRelaxationIndex, List<RoaringBitmap>> mfs_relaxed_queries;
    protected final RoaringBitmap[] MFS_QUERY;
    protected final RoaringBitmap QUERY;

    /**
     * 
     */
    public MFSRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
	QUERY = new RoaringBitmap();
	QUERY.add(0, query.getElementList().size() - 1);
	mfs_relaxed_queries = new LinkedHashMap<GraphRelaxationIndex, List<RoaringBitmap>>();
	
	mfs_finders = StrategyFactory.getLatticeStrategy(s, query, 1);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	MFS_QUERY = new RoaringBitmap[all_mfs.size()];
	for (int i = 0; i < all_mfs.size(); i++) {
	    MFS_QUERY[i] = new RoaringBitmap();
	}
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    for (int j = 0; j < all_mfs.size(); j++) {
		if (all_mfs.get(j).contain(
			query_to_relax.getElementList().get(i))) {
		    MFS_QUERY[j].add(i);
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
	mfs_relaxed_queries = new LinkedHashMap<GraphRelaxationIndex, List<RoaringBitmap>>();
	
	MFSSearch mfs_finders = StrategyFactory.getLatticeStrategy(s, query, 1,
		index);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	MFS_QUERY = new RoaringBitmap[all_mfs.size()];
	for (int i = 0; i < all_mfs.size(); i++) {
	    MFS_QUERY[i] = new RoaringBitmap();
	}
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    for (int j = 0; j < all_mfs.size(); j++) {
		if (all_mfs.get(j).contain(
			query_to_relax.getElementList().get(i))) {
		    MFS_QUERY[j].add(i);
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
		    * relaxation_of_element[i][relax_graph_node.getElement_index()[i]]
			    .getSimilarity();
	    this.current_level.add(relaxation_of_element[i][relax_graph_node.getElement_index()[i]]
		    .getRelaxation_level());
	}

	for (int j = 0; j < relax_graph_node.getChild_elt().length; j++) {
	    this.insert(relax_graph_node.getChild_elt()[j]);
	}
	already_relaxed_queries.add(relax_graph_node);
	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query, query_to_relax.getSelectedQueryVar());
	boolean has_mfs = check_mfs(relax_graph_node);

	if (!has_mfs) {
	    return current_relaxed_query;
	} else {
	    return this.next();
	}
    }

    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {
	
	int i = 0;
	boolean has_mfs = false;
	List<RoaringBitmap> mfs_current_query = new ArrayList<RoaringBitmap>();
	int[] current_relax_query = relax_graph_node.getElement_index();
	
	while ((i < MFS_QUERY.length) && (!has_mfs)) {
	    has_mfs = true;
	    for (int j = 0; j < MFS_QUERY[i].getCardinality(); j++) {
		has_mfs = has_mfs
			&& current_relax_query[MFS_QUERY[i].select(j)] == 0;
	    }
	    i = i + 1;
	}
	
	if(has_mfs){
	    mfs_current_query.add(MFS_QUERY[i-1]);
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	}
	return has_mfs;
    }

    public MFSSearch getMFSSearchEngine() {
	return mfs_finders;
    }
}
