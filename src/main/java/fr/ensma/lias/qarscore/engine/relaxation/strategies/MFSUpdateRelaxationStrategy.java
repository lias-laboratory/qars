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

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class MFSUpdateRelaxationStrategy extends MFSRelaxationStrategy {

    protected List<int[]>[] mfs_relaxation_degree;

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public MFSUpdateRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
	mfs_relaxation_degree = new List[MFS_QUERY.length];
	for (int i = 0; i < mfs_relaxation_degree.length; i++) {
	    mfs_relaxation_degree[i] = new ArrayList<int[]>();
	    int[] relax_degree = new int[MFS_QUERY[i].getCardinality()];
	    for (int j = 0; j < relax_degree.length; j++) {
		relax_degree[j] = 0;
	    }
	    mfs_relaxation_degree[i].add(relax_degree);
	}
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public MFSUpdateRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s, index);
	mfs_relaxation_degree = new List[MFS_QUERY.length];
	for (int i = 0; i < mfs_relaxation_degree.length; i++) {
	    mfs_relaxation_degree[i] = new ArrayList<int[]>();
	    int[] relax_degree = new int[MFS_QUERY[i].getCardinality()];
	    for (int j = 0; j < relax_degree.length; j++) {
		relax_degree[j] = 0;
	    }
	    mfs_relaxation_degree[i].add(relax_degree);
	}
    }

    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}

	GraphRelaxationIndex current_graph = relaxed_queries.remove(0);
	int[] current_relax_query = current_graph.getElement_index();

	this.current_similarity = 1.0;
	this.current_level = 0;
	for (int i = 0; i < current_graph.getElement_index().length; i++) {

	    elt_relaxed_query.add(getRelaxedElement(i,
		    current_graph.getElement_index()[i]));

	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getSimilarity();
	    this.current_level = this.current_level
		    + relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getRelaxation_level();
	}

	for (int j = 0; j < current_graph.getChild_elt().length; j++) {
	    this.insert(current_graph.getChild_elt()[j]);
	}
	
	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query);
	boolean has_mfs = check_mfs(current_relax_query);
	
	if (!has_mfs) {
	    if (!update_mfs(current_relax_query)) {
		return current_relaxed_query;
	    }
	    // Some mfs hasn't been repaired
	    else {
		return this.next();
	    }
	} else {
	    return this.next();
	}
    }

    protected boolean check_mfs(int[] current_relax_query) {
	int i = 0;
	boolean has_mfs = false;
	while ((i < MFS_QUERY.length) && (!has_mfs)) {
	    int k = mfs_relaxation_degree[i].size() - 1;
	    while ((k >= 0) && (!has_mfs)) {
		has_mfs = true;
		for (int j = 0; j < MFS_QUERY[i].getCardinality(); j++) {
		    has_mfs = has_mfs
			    && current_relax_query[MFS_QUERY[i].select(j)] <= mfs_relaxation_degree[i]
				    .get(k)[j];
		}
		k = k - 1;
	    }
	    i = i + 1;
	}
	return has_mfs;
    }

    protected boolean update_mfs(int[] current_relax_query) {

	boolean has_new_mfs = false;
	for (int i = 0; i < MFS_QUERY.length; i++) {
	    List<CElement> updated_mfs_query = new ArrayList<CElement>();
	    int[] relax_degree = new int[MFS_QUERY[i].getCardinality()];
	    for (int j = 0; j < relax_degree.length; j++) {
		relax_degree[j] = current_relax_query[MFS_QUERY[i].select(j)];
		updated_mfs_query.add(this.getRelaxedElement(
			MFS_QUERY[i].select(j), relax_degree[j]));
	    }

	    QueryStatement stm = session.createStatement((CQueryFactory
		    .createCQuery(updated_mfs_query)).toString());

	    if (stm.getResultSetSize() == 0) {
		number_check_queries = number_check_queries + 1;
		mfs_relaxation_degree[i].add(relax_degree);
		has_new_mfs = true;
	    }
	}
	return has_new_mfs;
    }
}
