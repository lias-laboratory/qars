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
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class INCFULLMFSRelaxationStrategy extends MFSUpdateRelaxationStrategy {

    /**
     * 
     */
    public INCFULLMFSRelaxationStrategy(CQuery query, Session s) {
	super(query, s);

    }

    /**
     * 
     */
    public INCFULLMFSRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s, index);
    }

    protected boolean check_mfs(GraphRelaxationIndex relax_graph_node) {

	GraphRelaxationIndex least_relaxed_ancestor = getLeastRelaxedAncestor(
		failed_relaxed_queries, relax_graph_node);

	List<Integer> mfs_current_query = new ArrayList<Integer>();

	if (least_relaxed_ancestor != null) {
	    if (mfs_relaxed_queries.get(least_relaxed_ancestor) != null) {
		return check_on_mfs_relaxed(relax_graph_node,
			least_relaxed_ancestor, mfs_current_query);
	    } else {
		return check_on_allmfs(relax_graph_node, mfs_current_query);
	    }
	} else {
	    return check_on_allmfs(relax_graph_node, mfs_current_query);
	}
    }

    protected boolean check_on_allmfs(GraphRelaxationIndex relax_graph_node,
	    List<Integer> mfs_current_query) {

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<Integer> current_mfs_relaxed = new ArrayList<Integer>();
	List<int[]> degree_mfs_relaxed = new ArrayList<int[]>();

	int i = 0;
	while (i < mfs_elt_index.size()) {
	    int[] current_mfs_degree = new int[mfs_elt_index.get(i)
		    .getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(i).getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index
			.get(i).select(j)];
	    }
	    boolean is_mfs = is_in_mfs_degree(i, current_mfs_degree);
	    if (is_mfs) {
		mfs_current_query.add(i);
	    } else {
		current_mfs_relaxed.add(i);
		degree_mfs_relaxed.add(current_mfs_degree);
	    }
	    i = i + 1;
	}

	if (!mfs_current_query.isEmpty()) {
	    // mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    failed_relaxed_queries.add(relax_graph_node);
	    return true;
	}

	List<Integer> mfs_repaired_current_query = add_relaxed_mfs(
		relax_graph_node, current_mfs_relaxed, degree_mfs_relaxed);

	if (mfs_repaired_current_query.size() != current_mfs_relaxed.size()) {
	    add_new_mfs(relax_graph_node, mfs_current_query,
		    mfs_repaired_current_query);
	    return true;
	}
	return add_new_mfs(relax_graph_node, mfs_current_query,
		mfs_repaired_current_query);
    }

    protected boolean check_on_mfs_relaxed(
	    GraphRelaxationIndex relax_graph_node,
	    GraphRelaxationIndex least_relaxed_ancestor,
	    List<Integer> mfs_current_query) {

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<Integer> current_mfs_relaxed = new ArrayList<Integer>();
	List<int[]> degree_mfs_relaxed = new ArrayList<int[]>();

	List<Integer> potential_mfs = mfs_relaxed_queries
		.get(least_relaxed_ancestor);
	int i = 0;
	while (i < potential_mfs.size()) {
	    int[] current_mfs_degree = new int[mfs_elt_index.get(
		    potential_mfs.get(i)).getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(potential_mfs.get(i))
		    .getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index.get(
			potential_mfs.get(i)).select(j)];
	    }
	    boolean is_mfs = is_in_mfs_degree(potential_mfs.get(i),
		    current_mfs_degree);
	    if (is_mfs) {
		mfs_current_query.add(potential_mfs.get(i));
	    } else {
		current_mfs_relaxed.add(i);
		degree_mfs_relaxed.add(current_mfs_degree);
	    }
	    i = i + 1;
	}
	
	List<Integer> mfs_repaired_current_query = add_relaxed_mfs(relax_graph_node,
		current_mfs_relaxed, degree_mfs_relaxed);
	
	if (mfs_current_query.isEmpty()) {
	    if (mfs_repaired_current_query.size() != current_mfs_relaxed.size()){
		add_new_mfs(relax_graph_node, mfs_current_query,
			mfs_repaired_current_query);
		return true;
	    }
	    else {
		return add_new_mfs(relax_graph_node, mfs_current_query,
			mfs_repaired_current_query);
	    }
	} else {
	    failed_relaxed_queries.add(relax_graph_node);
	    if (mfs_current_query.size() == potential_mfs.size()) {
		mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    }
	    return true;
	}
    }

    protected boolean add_new_mfs(GraphRelaxationIndex relax_graph_node,
	    List<Integer> mfs_current_query,
	    List<Integer> mfs_repaired_current_query) {

	int[] current_relax_query = relax_graph_node.getElement_index();

	List<RoaringBitmap> new_mfs_founded = full_update_mfs(
		current_relax_query, mfs_current_query,
		mfs_repaired_current_query);

	for (int k = 0; k < new_mfs_founded.size(); k++) {
	    int index = mfs_elt_index.size();
	    mfs_elt_index.add(new_mfs_founded.get(k));
	    mfs_degree_by_index.add(new ArrayList<int[]>());
	    int[] current_mfs_degree = new int[mfs_elt_index.get(index)
		    .getCardinality()];
	    for (int j = 0; j < mfs_elt_index.get(index).getCardinality(); j++) {
		current_mfs_degree[j] = current_relax_query[mfs_elt_index.get(
			index).select(j)];
	    }
	    mfs_degree_by_index.get(index).add(current_mfs_degree);
	    mfs_current_query.add(index);
	}

	if (!mfs_current_query.isEmpty()) {
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    return true;
	}

	return false;
    }

    protected List<RoaringBitmap> full_update_mfs(int[] current_relax_query,
	    List<Integer> mfs_current_query, List<Integer> subqueries_of_mfs) {

	List<RoaringBitmap> new_mfs_founded = new ArrayList<RoaringBitmap>();
	if (subqueries_of_mfs.isEmpty()) {
	    return new_mfs_founded;
	}

	List<List<CElement>> mfs_subqueries = new ArrayList<List<CElement>>();
	List<List<CElement>> elt_mfs_subqueries = new ArrayList<List<CElement>>();
	for (int j = 0; j < mfs_current_query.size(); j++) {
	    RoaringBitmap current_mfs_index = mfs_elt_index
		    .get(mfs_current_query.get(j));
	    mfs_subqueries.add(get_element_list(current_relax_query,
		    current_mfs_index));
	}

	for (int j = 0; j < subqueries_of_mfs.size(); j++) {
	    RoaringBitmap current_sub_mfs_index = mfs_elt_index
		    .get(subqueries_of_mfs.get(j));
	    elt_mfs_subqueries.add(get_element_list(current_relax_query,
		    current_sub_mfs_index));
	}

	List<CElement> element_query = new ArrayList<CElement>();
	for (int i = 0; i < current_relax_query.length; i++) {
	    element_query.add(getRelaxedElement(i, current_relax_query[i]));
	}

	List<CQuery> maximal_factor = factorisation(
		CQueryFactory.createCQuery(element_query), mfs_subqueries);
	List<CQuery> mfs_list = new ArrayList<CQuery>();
	List<CQuery> pxss_to_process = new ArrayList<CQuery>();

	for (List<CElement> sub_mfs : elt_mfs_subqueries) {
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
	    return new_mfs_founded;
	}

	for (int j = 0; j < mfs_list.size(); j++) {
	    new_mfs_founded.add(j, new RoaringBitmap());
	}

	for (int i = 0; i < element_query.size(); i++) {
	    CElement elt = element_query.get(i);
	    for (int j = 0; j < mfs_list.size(); j++) {
		if (mfs_list.get(j).contain(elt)) {
		    new_mfs_founded.get(j).add(i);
		}
	    }
	}
	return new_mfs_founded;
    }

    private List<CElement> get_mfs_containing(CQuery current_pxss,
	    CQuery sub_mfs) {

	List<CElement> mfs_elt = new ArrayList<CElement>();
	CQuery query = CQueryFactory.cloneCQuery(current_pxss);
	QueryStatement stm = session.createStatement(query.toString());

	number_check_queries++;
	if (stm.getResultSetSize(1) != 0) {
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
		if (stm.getResultSetSize(1) != 0) {
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
	    if (stm.getResultSetSize(1) != 0) {
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

	if (current_pxss.getElementList().size() == sub_mfs.getElementList()
		.size()) {
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
