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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.roaringbitmap.RoaringBitmap;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;

/**
 * @author Geraud FOKOU
 */
public class MFSUpdateRelaxationStrategy extends MFSRelaxationStrategy {

    public int number_check_queries = 0;
    protected Logger logger = Logger.getLogger(MFSRelaxationStrategy.class);

    /**
     * 
     */
    public MFSUpdateRelaxationStrategy(CQuery query, Session s) {
	super(query, s);
    }

    /**
     * 
     */
    public MFSUpdateRelaxationStrategy(CQuery query, Session s, boolean index) {
	super(query, s, index);
    }

    public CQuery next() {

	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	if (this.relaxed_queries.isEmpty()) {
	    return null;
	}

	GraphRelaxationIndex current_graph = relaxed_queries.remove(0);

	this.current_similarity = 1.0;
	this.current_level = new ArrayList<int[]>();
	for (int i = 0; i < current_graph.getElement_index().length; i++) {

	    elt_relaxed_query.add(getRelaxedElement(i,
		    current_graph.getElement_index()[i]));

	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][current_graph.getElement_index()[i]]
			    .getSimilarity();
	    this.current_level.add(relaxation_of_element[i][current_graph
		    .getElement_index()[i]].getRelaxation_level());
	}

	for (int j = 0; j < current_graph.getChild_elt().length; j++) {
	    this.insert(current_graph.getChild_elt()[j]);
	}

	current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query,
		query_to_relax.getSelectedQueryVar());
	boolean has_mfs = check_mfs(current_graph);

	if (!has_mfs) {
	    already_relaxed_queries.add(current_graph);
	    return current_relaxed_query;
	} else {
	    return this.next();
	}
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
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    failed_relaxed_queries.add(relax_graph_node);
	    return true;
	}

	List<Integer> repair_mfs = add_relaxed_mfs(relax_graph_node,
		current_mfs_relaxed, degree_mfs_relaxed);

	return repair_mfs.size() != current_mfs_relaxed.size();
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
	if (!mfs_current_query.isEmpty()) {
	    // mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    failed_relaxed_queries.add(relax_graph_node);
	    if (mfs_current_query.size() == potential_mfs.size()) {
		mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    }
	    return true;
	}

	List<Integer> repair_mfs = add_relaxed_mfs(relax_graph_node,
		current_mfs_relaxed, degree_mfs_relaxed);
	
	 return repair_mfs.size() != current_mfs_relaxed.size();
    }

    protected List<Integer> add_relaxed_mfs(
	    GraphRelaxationIndex relax_graph_node,
	    List<Integer> current_mfs_relaxed, List<int[]> degree_mfs_relaxed) {

	int[] current_relax_query = relax_graph_node.getElement_index();
	List<Integer> mfs_current_query = new ArrayList<Integer>();
	List<Integer> repair_current_query = new ArrayList<Integer>();

	for (int i = 0; i < current_mfs_relaxed.size(); i++) {
	    List<CElement> updated_mfs_query = get_element_list(
		    current_relax_query,
		    mfs_elt_index.get(current_mfs_relaxed.get(i)));

	    number_check_queries = number_check_queries + 1;
	    QueryStatement stm = session.createStatement((CQueryFactory
		    .createCQuery(updated_mfs_query)).toString());

	    if (stm.getResultSetSize(1) == 0) {
		mfs_current_query.add(current_mfs_relaxed.get(i));
		mfs_degree_by_index.get(current_mfs_relaxed.get(i)).add(
			degree_mfs_relaxed.get(i));
	    } else {
		repair_current_query.add(current_mfs_relaxed.get(i));
	    }
	}
	if (!mfs_current_query.isEmpty()) {
	    mfs_relaxed_queries.put(relax_graph_node, mfs_current_query);
	    failed_relaxed_queries.add(relax_graph_node);
	}
	return repair_current_query;
    }

    protected boolean is_in_mfs_degree(int i, int[] mfs_degree) {

	if (mfs_degree.length != mfs_degree_by_index.get(i).get(0).length) {
	    return false;
	}
	int j = mfs_degree_by_index.get(i).size() - 1;
	while (0 <= j) {
	    boolean is_in = true;
	    int k = 0;
	    while ((k < mfs_degree.length) && (is_in)) {
		is_in = is_in
			&& (mfs_degree_by_index.get(i).get(j)[k] == mfs_degree[k]);
		k = k + 1;
	    }
	    if (is_in) {
		return true;
	    }
	    j = j - 1;
	}
	return false;
    }

    protected List<CElement> get_element_list(int[] current_relax_query,
	    RoaringBitmap current_mfs_index) {

	List<CElement> elt_form = new ArrayList<CElement>();
	for (int i = 0; i < current_mfs_index.getCardinality(); i++) {
	    elt_form.add(this.getRelaxedElement(current_mfs_index.select(i),
		    current_relax_query[current_mfs_index.select(i)]));
	}
	return elt_form;
    }

    protected GraphRelaxationIndex getLeastRelaxedAncestor(
	    List<GraphRelaxationIndex> already_failed_relaxed_queries,
	    GraphRelaxationIndex relax_graph_node) {

	if (already_failed_relaxed_queries.size() == 0) {
	    return null;
	}
	int i = already_failed_relaxed_queries.size() - 1;
	boolean is_relaxation = false;
	while ((0 <= i) && (!is_relaxation)) {
	    is_relaxation = true;
	    int j = 0;
	    while ((j < relax_graph_node.getElement_index().length)
		    && (is_relaxation)) {
		CElement relax_elt = getRelaxedElement(j,
			relax_graph_node.getElement_index()[j]);
		CElement father_elt = getRelaxedElement(j,
			already_failed_relaxed_queries.get(i)
				.getElement_index()[j]);
		is_relaxation = is_relaxation
			&& TripleRelaxation.is_relaxation(relax_elt,
				father_elt, session);
		j = j + 1;
	    }
	    i = i - 1;
	}

	return already_failed_relaxed_queries.get(i + 1);
    }

    protected void logger_init() {

	LocalDateTime time = LocalDateTime.now();
	String time_value = "" + time.getDayOfMonth() + time.getMonthValue()
		+ time.getHour() + time.getMinute() + time.getSecond();

	String logfile = "UpdateMFS-Process" + "-" + time_value + ".log";

	PatternLayout layout = new PatternLayout();
	String conversionPattern = "%-5p [%C{1}]: %m%n";
	layout.setConversionPattern(conversionPattern);

	FileAppender fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.removeAllAppenders();
	logger.addAppender(fileAppender);
    }

}
