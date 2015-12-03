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

/**
 * @author Geraud FOKOU
 */
public class SYSFULLMFSRelaxationStrategy extends INCFULLMFSRelaxationStrategy {

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public SYSFULLMFSRelaxationStrategy(CQuery query, Session s) {
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
	mfs_evolution = new ArrayList<RoaringBitmap>();
	mfs_evolution_relaxation_degree = new ArrayList<List<int[]>>();
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public SYSFULLMFSRelaxationStrategy(CQuery query, Session s, boolean index) {
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

    protected boolean update_mfs(int[] current_relax_query) {

	List<List<CElement>> mfs_subqueries = new ArrayList<List<CElement>>();
	List<List<CElement>> subqueries_of_mfs = new ArrayList<List<CElement>>();
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
	    number_check_queries = number_check_queries + 1;

	    if (stm.getResultSetSize(1) == 0) {
		mfs_subqueries.add(updated_mfs_query);
		mfs_relaxation_degree[i].add(relax_degree);
	    } else {
		subqueries_of_mfs.add(updated_mfs_query);
	    }
	}

	for (int i = 0; i < mfs_evolution.size(); i++) {
	    List<CElement> updated_mfs_query = new ArrayList<CElement>();
	    int[] relax_degree = new int[mfs_evolution.get(i).getCardinality()];
	    for (int j = 0; j < relax_degree.length; j++) {
		relax_degree[j] = current_relax_query[mfs_evolution.get(i)
			.select(j)];
		updated_mfs_query.add(this.getRelaxedElement(
			mfs_evolution.get(i).select(j), relax_degree[j]));
	    }

	    QueryStatement stm = session.createStatement((CQueryFactory
		    .createCQuery(updated_mfs_query)).toString());
	    number_check_queries = number_check_queries + 1;

	    if (stm.getResultSetSize(1) == 0) {
		mfs_subqueries.add(updated_mfs_query);
		mfs_evolution_relaxation_degree.get(i).add(relax_degree);
	    } else {
		subqueries_of_mfs.add(updated_mfs_query);
	    }
	}

	if(!mfs_subqueries.isEmpty()){
	    return true;
	}
	else {
	    return full_update_mfs(current_relax_query, mfs_subqueries,
				subqueries_of_mfs);
	}
    }
}
