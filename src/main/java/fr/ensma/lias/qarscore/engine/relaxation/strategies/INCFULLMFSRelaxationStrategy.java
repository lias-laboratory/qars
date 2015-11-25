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
public class INCFULLMFSRelaxationStrategy extends MFSUpdateRelaxationStrategy {

    protected List<RoaringBitmap> mfs_evolution;
    List<List<int[]>> mfs_evolution_relaxation_degree;
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public INCFULLMFSRelaxationStrategy(CQuery query, Session s) {
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
    public INCFULLMFSRelaxationStrategy(CQuery query, Session s, boolean index) {
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
	i = 0;
	while ((i < mfs_evolution.size()) && (!has_mfs)) {
	    int k = mfs_evolution_relaxation_degree.get(i).size() - 1;
	    while ((k >= 0) && (!has_mfs)) {
		has_mfs = true;
		for (int j = 0; j < mfs_evolution.get(i).getCardinality(); j++) {
		    has_mfs = has_mfs
			    && current_relax_query[mfs_evolution.get(i).select(
				    j)] <= mfs_evolution_relaxation_degree.get(
				    i).get(k)[j];
		}
		k = k - 1;
	    }
	    i = i + 1;

	}
	return has_mfs;
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

	    if (stm.getResultSetSize() == 0) {
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

	    if (stm.getResultSetSize() == 0) {
		mfs_subqueries.add(updated_mfs_query);
		mfs_evolution_relaxation_degree.get(i).add(relax_degree);
	    } else {
		subqueries_of_mfs.add(updated_mfs_query);
	    }
	}

	return (!mfs_subqueries.isEmpty())
		|| full_update_mfs(current_relax_query, mfs_subqueries,
			subqueries_of_mfs);
    }

    protected boolean full_update_mfs(int[] current_relax_query,
	    List<List<CElement>> mfs_subqueries,
	    List<List<CElement>> subqueries_of_mfs) {

	if (subqueries_of_mfs.isEmpty()) {
	    return false;
	}
	List<CElement> element_query = new ArrayList<CElement>();
	for (int i = 0; i < current_relax_query.length; i++) {
	    element_query.add(getRelaxedElement(i, current_relax_query[i]));
	}
	List<CQuery> maximal_factor = factorisation(
		CQueryFactory.createCQuery(element_query), mfs_subqueries);
	List<CQuery> mfs_list = new ArrayList<CQuery>();
	List<CQuery> pxss_to_process = new ArrayList<CQuery>();

	for (List<CElement> sub_mfs : subqueries_of_mfs) {
	    while (!maximal_factor.isEmpty()) {
		CQuery pxss = maximal_factor.get(0);
		maximal_factor.remove(0);
		if (pxss.getElementList().containsAll(sub_mfs)) {
		    List<CQuery> current_mfs = new ArrayList<CQuery>();
		    List<CQuery> current_pxss = this.get_all_mfs_containing(
			    pxss, CQueryFactory.createCQuery(sub_mfs),
			    current_mfs);
		    for (CQuery mfs : current_mfs) {
			if (!mfs_list.contains(mfs)) {
			    mfs_list.add(mfs);
			}
		    }
		    List<CQuery> to_remove = new ArrayList<CQuery>();
		    for (CQuery xss_query : current_pxss) {
			for (CQuery old_xss_query : pxss_to_process) {
			    if (old_xss_query.isSubQueryOf(xss_query)) {
				to_remove.add(old_xss_query);
				pxss_to_process.add(xss_query);
			    } else {
				if (!old_xss_query.isSuperQueryOf(xss_query)) {
				    pxss_to_process.add(xss_query);
				}
			    }
			}
		    }
		    pxss_to_process.removeAll(to_remove);
		} else {
		    pxss_to_process.add(pxss);
		}
	    }
	    maximal_factor.addAll(pxss_to_process);
	    pxss_to_process.clear();
	}

	if (mfs_list.size() == 0) {
	    return false;
	}

	List<RoaringBitmap> mfs_new_evolution = new ArrayList<RoaringBitmap>();
	List<int[]> mfs_new_evolution_degree = new ArrayList<int[]>();
	for (int j = 0; j < mfs_list.size(); j++) {
	    mfs_new_evolution.add(j, new RoaringBitmap());
	    mfs_new_evolution_degree.add(new int[mfs_list.get(j)
		    .getElementList().size()]);
	}

	int i = 0;
	int[] index_of_degree = new int[mfs_list.size()];
	while (i < element_query.size()) {
	    CElement elt = element_query.get(i);
	    for (int j = 0; j < mfs_list.size(); j++) {
		if (mfs_list.get(j).contain(elt)) {
		    mfs_new_evolution.get(j).add(i);
		    mfs_new_evolution_degree.get(j)[index_of_degree[j]] = current_relax_query[i];
		    index_of_degree[j] = index_of_degree[j] + 1;
		}
	    }
	    i = i + 1;
	}

	for (int j = 0; i < mfs_new_evolution.size(); i++) {
	    mfs_evolution.add(mfs_new_evolution.get(j));
	    List<int[]> first_degree = new ArrayList<int[]>();
	    first_degree.add(mfs_new_evolution_degree.get(j));
	    mfs_evolution_relaxation_degree.add(first_degree);
	}
	return true;
    }

    private List<CElement> get_mfs_containing(CQuery current_pxss,
	    CQuery sub_mfs) {

	List<CElement> mfs_elt = new ArrayList<CElement>();
	CQuery query = CQueryFactory.cloneCQuery(current_pxss);
	QueryStatement stm = session.createStatement(query.toString());

	number_check_queries++;
	if (stm.getResultSetSize() != 0) {
	    return mfs_elt;
	}
	
	if (query.getElementList().size() == sub_mfs.getElementList().size() + 1) {
	    mfs_elt.addAll(query.getElementList());
	    return mfs_elt;
	}

	for (CElement elt : sub_mfs.getElementList()) {
	    query.getElementList().remove(elt);
	}

	List<CElement> causes = new ArrayList<CElement>();
	causes.addAll(sub_mfs.getElementList());
	CQuery tempQuery = CQueryFactory.cloneCQuery(query);

	for (int i = 0; i < query.getElementList().size() - 1; i++) {
	    CElement elt = query.getElementList().get(i);
	    tempQuery.getElementList().remove(elt);
	    CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	    temp.getElementList().addAll(causes);
	    if (temp.isValidQuery()) {
		number_check_queries++;
		stm = session.createStatement(temp.toString());
		if (stm.getResultSetSize() != 0) {
		    causes.add(elt);
		}
	    }
	}

	CElement elt = query.getElementList().get(
		query.getElementList().size() - 1);

	tempQuery.getElementList().remove(elt);
	CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	temp.getElementList().addAll(causes);
	if (temp.isValidQuery()) {
	    number_check_queries++;
	    stm = session.createStatement(temp.toString());
	    if (stm.getResultSetSize() != 0) {
		causes.add(elt);
	    }
	}

	return causes;
    }

    private List<CQuery> factorisation(CQuery query,
	    List<List<CElement>> mfs_subqueries) {

	List<CQuery> pxss = new ArrayList<CQuery>();
	pxss.add(query);

	for (List<CElement> mfs : mfs_subqueries) {
	    List<CQuery> new_pxss = new ArrayList<CQuery>();
	    for (CQuery xss : pxss) {
		for (CElement elt : mfs) {
		    CQuery new_xss = CQueryFactory.cloneCQuery(xss);
		    new_xss.getElementList().remove(elt);
		    new_pxss.add(new_xss);
		}
	    }
	    pxss.clear();
	    pxss.addAll(new_pxss);
	}

	return pxss;
    }

    private List<CQuery> get_all_mfs_containing(CQuery current_pxss,
	    CQuery sub_mfs, List<CQuery> all_mfs) {

	if (current_pxss.getElementList().size() == sub_mfs.getElementList().size()) {
	    ArrayList<CQuery> list_pxss = new ArrayList<CQuery>();
	    list_pxss.add(current_pxss);
	    return list_pxss;
	}
	
	List<CElement> first = this.get_mfs_containing(current_pxss, sub_mfs);
	if (first.isEmpty()) {
	    return new ArrayList<CQuery>();
	}
	List<List<CElement>> factor = new ArrayList<List<CElement>>();
	factor.add(first);
	all_mfs.add(CQueryFactory.createCQuery(first));
	List<CQuery> list_pxss_containing = factorisation(current_pxss, factor);
	ArrayList<CQuery> list_pxss__not_containing = new ArrayList<CQuery>();
	while (!list_pxss_containing.isEmpty()) {

	    CQuery tempquery = list_pxss_containing.get(0);
	    list_pxss_containing.remove(tempquery);

	    if (!tempquery.isSuperQueryOf(sub_mfs)) {
		list_pxss__not_containing.add(tempquery);
		continue;
	    }

	    List<CElement> anCause = this
		    .get_mfs_containing(tempquery, sub_mfs);
	    if (anCause.isEmpty()) {
		continue;
	    }
	    if (anCause.size() == sub_mfs.getElementList().size()) {
		list_pxss__not_containing.add(tempquery);
		continue;
	    }

	    all_mfs.add(CQueryFactory.createCQuery(anCause));
	    List<CQuery> new_pxss = new ArrayList<CQuery>();
	    for (int i = 0; i < list_pxss_containing.size(); i++) {

		if (!list_pxss_containing.get(i).isSuperQueryOf(
			all_mfs.get(all_mfs.size() - 1))) {
		    if (!list_pxss_containing.get(i).isSuperQueryOf(sub_mfs)) {
			list_pxss__not_containing.add(list_pxss_containing
				.get(i));
		    } else {
			new_pxss.add(list_pxss_containing.get(i));
		    }
		} else {
		    factor.clear();
		    factor.add(anCause);
		    List<CQuery> current_pxss_list = factorisation(
			    list_pxss_containing.get(i), factor);
		    for (CQuery npxss : current_pxss_list) {
			boolean include = false;
			int j = 0;
			while ((j < new_pxss.size() && (!include))) {
			    include = new_pxss.get(j).equals(npxss);
			}
			if (!include) {
			    new_pxss.add(npxss);
			}
		    }
		}
	    }

	    list_pxss_containing.clear();
	    list_pxss_containing.addAll(new_pxss);
	}
	return list_pxss__not_containing;
    }
}
