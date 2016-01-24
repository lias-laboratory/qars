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
package fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.xss;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.AbstractRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;

/**
 * @author Geraud FOKOU
 */
public abstract class AbstractXSSRelaxationStrategy extends
	AbstractRelaxationStrategy {

    protected Logger logger = Logger
	    .getLogger(AbstractXSSRelaxationStrategy.class);

    public static final int MFS_LIMIT_ANSWERS = 1;

    /**
     * Initialization
     */
    protected CQuery query_to_relax;
    protected Session session;

    /**
     * xss computing variables
     */
    protected List<GraphRelaxationIndex>[] already_relaxed_xss;
    protected List<GraphRelaxationIndex>[] relaxed_queries;
    protected NodeRelaxed[][] relaxation_of_element;
    protected int[][] xss_query_index;
    protected MFSSearch xss_finders;

    /**
     * xss variables
     */
    protected CQuery[] xss_to_relax_queries;

    /**
     * Result of each relaxation
     */
    protected CQuery current_relaxed_query;
    protected double current_similarity;
    protected double current_satisfactory;
    protected List<int[]> current_level;

    /**
     * Initialize all the data structure of the relaxation object
     */
    @SuppressWarnings("unchecked")
    protected void initilizationXSS(CQuery query, Session s, boolean optimization) {

	session = s;
	this.logger_init();
	query_to_relax = query;

	relaxation_of_element = new NodeRelaxed[query_to_relax.getElementList()
		.size()][];
	int[] relaxation_limit_index = new int[query_to_relax.getElementList()
		.size()];
	int[] relaxation_index = new int[query_to_relax.getElementList().size()];

	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    CElement element = query_to_relax.getElementList().get(i);
	    relaxation_of_element[i] = (new TripleRelaxation(element, session,
		    TripleRelaxation.SIM_ORDER)).get_relaxed_node_list();
	    relaxation_limit_index[i] = relaxation_of_element[i].length;
	    relaxation_index[i] = 0;
	}

	List<CQuery> xss_queries = xss_finders.getAllXSS();
	already_relaxed_xss = new List[xss_queries.size()];
	relaxed_queries = new List[xss_queries.size()];
	xss_query_index = new int[xss_queries.size()][];
	xss_to_relax_queries = new CQuery[xss_queries.size()];

	if (xss_queries.get(0).getElementList().size() == query
		.getElementList().size()) {
	    xss_to_relax_queries[0] = xss_queries.get(0);
	    already_relaxed_xss[0] = new ArrayList<GraphRelaxationIndex>();
	    relaxed_queries[0] = new ArrayList<GraphRelaxationIndex>();
	    xss_query_index[0] = new int[xss_queries.get(0).getElementList()
		    .size()];
	    int[] relaxation_index_xss = new int[xss_queries.get(0)
		    .getElementList().size()];
	    int[] relaxation_limit_xss = new int[xss_queries.get(0)
		    .getElementList().size()];
	    for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
		CElement element = query_to_relax.getElementList().get(i);
		int j = xss_to_relax_queries[0].getElementList().indexOf(
			element);
		xss_query_index[0][j] = i;
		relaxation_index_xss[j] = relaxation_index[i];
		relaxation_limit_xss[j] = relaxation_limit_xss[i];
	    }
	    relaxed_queries[0].add(new GraphRelaxationIndex(
		    relaxation_index_xss, relaxation_limit_xss));
	} else {
	    for (int i = 0; i < xss_queries.size(); i++) {
		xss_to_relax_queries[i] = xss_queries.get(i);
		already_relaxed_xss[i] = new ArrayList<GraphRelaxationIndex>();
		relaxed_queries[i] = new ArrayList<GraphRelaxationIndex>();
		xss_query_index[i] = new int[xss_queries.get(i)
			.getElementList().size()];
		int[] relaxation_index_xss = new int[xss_queries.get(i)
			.getElementList().size()];
		int[] relaxation_limit_xss = new int[xss_queries.get(i)
			.getElementList().size()];
		
		for (int j = 0; j < query_to_relax.getElementList().size(); j++) {
		    CElement element = query_to_relax.getElementList().get(j);
		    int index = xss_to_relax_queries[i].getElementList()
			    .indexOf(element);
		    if (index != -1) {
			xss_query_index[i][index] = j;
			relaxation_index_xss[index] = relaxation_index[j];
			relaxation_limit_xss[index] = relaxation_limit_index[j];
		    }
		}
		relaxed_queries[i].add(new GraphRelaxationIndex(
			relaxation_index_xss, relaxation_limit_xss, !optimization));
	    }
	}

	this.number_mfs_query_executed = ((AbstractLatticeStrategy) xss_finders).number_of_query_executed;
	this.duration__mfs_query_executed = ((AbstractLatticeStrategy) xss_finders).duration_of_execution;
    }

    /**
     * Abstract constructor
     */
    public AbstractXSSRelaxationStrategy(CQuery query, Session s) {

	xss_finders = StrategyFactory.getLatticeStrategy(s, query,
		MFS_LIMIT_ANSWERS);
	this.initilizationXSS(query, s, true);
    }

    /**
     * Abstract constructor
     */
    protected AbstractXSSRelaxationStrategy(CQuery query, Session s,
	    boolean optimization) {

	xss_finders = StrategyFactory.getLatticeStrategy(s, query,
		MFS_LIMIT_ANSWERS, optimization);
	this.initilizationXSS(query, s, true);
    }

    /**
     * Similarity between the current relaxation and the original query
     */
    @Override
    public Double getCurrent_similarity() {
	return current_similarity;
    }

    @Override
    public double getRelativeSatisfactory() {
	return current_satisfactory;
    }

    /**
     * List of relaxation level of all the triple of the current relaxation
     * query
     */
    @Override
    public List<int[]> getCurrent_level() {
	return current_level;
    }

    @Override
    public CQuery getQuery_to_relax() {
	return query_to_relax;
    }

    /**
     * Current relaxation query
     */
    @Override
    public CQuery getCurrent_relaxed_query() {
	return current_relaxed_query;
    }

    @Override
    public CElement getRelaxedElement(CElement triple, int relaxation_rank) {

	int index = query_to_relax.getElementList().indexOf(triple);
	if (index != -1) {
	    return getRelaxedElement(index, relaxation_rank);
	} else {
	    return null;
	}
    }

    /**
     * Are there any other relaxation for the current query?
     */
    @Override
    public boolean hasNext() {

	for (int i = 0; i < relaxed_queries.length; i++) {
	    if (!relaxed_queries[i].isEmpty()) {
		return true;
	    }
	}
	return false;
    }

    /**
     * The next relaxed query
     */
    @Override
    public abstract CQuery next();

    protected abstract CQuery getQuery(GraphRelaxationIndex graphRelaxationIndex, int j);

    /**
     * Get the relaxation_rank th best relaxation of the triple with index
     * num_triple in the original query
     * 
     * @param num_triple
     * @param relaxation_rank
     * @return
     */
    protected CElement getRelaxedElement(int num_triple, int relaxation_rank) {

	CElement relax_element = CElement.createCTriple(query_to_relax
		.getElementList().get(0).getElement());
	relax_element = relax_element
		.replace_subject(relaxation_of_element[num_triple][relaxation_rank]
			.getNode_1());
	relax_element = relax_element
		.replace_predicat(relaxation_of_element[num_triple][relaxation_rank]
			.getNode_2());
	relax_element = relax_element
		.replace_object(relaxation_of_element[num_triple][relaxation_rank]
			.getNode_3());

	return relax_element;
    }

    /**
     * Check if a node of the graph node is already in the list of relaxed
     * queries.
     * 
     * @param graphRelaxationIndex
     * @return
     */
    protected boolean alreadyRelaxed(GraphRelaxationIndex graphRelaxationIndex,
	    int xss_index) {

	boolean found = false;
	int i = 0;
	while ((!found) && (i < already_relaxed_xss[xss_index].size())) {
	    found = hasSameIndex(graphRelaxationIndex.getElement_index(),
		    already_relaxed_xss[xss_index].get(i).getElement_index());
	    i = i + 1;
	}
	if (found) {
	    return found;
	}
	i = 0;
	while ((!found) && (i < relaxed_queries[xss_index].size())) {
	    found = hasSameIndex(graphRelaxationIndex.getElement_index(),
		    relaxed_queries[xss_index].get(i).getElement_index());
	    i = i + 1;
	}
	return found;
    }

    private boolean hasSameIndex(int[] element_index, int[] other_element_index) {

	if (element_index == other_element_index) {
	    return true;
	}

	if (element_index.length != other_element_index.length) {
	    return false;
	}

	boolean is_same = true;
	int i = 0;
	while ((is_same) && (i < other_element_index.length)) {
	    is_same = is_same && other_element_index[i] == element_index[i];
	    i = i + 1;
	}
	return is_same;
    }

    protected abstract double similarity_graph_index(GraphRelaxationIndex child,
	    int xss_index) ;

    protected void insert_relaxation_graph_node(GraphRelaxationIndex child,
	    int xss_index) {

	double current_child_similarity = this.similarity_graph_index(child, xss_index);
	int pos = 0;
	int index = relaxed_queries[xss_index].size() - 1;
	boolean found_pos = false;
	while ((!found_pos) && (0 <= index)) {
	    double index_sim = 1.0;
	    GraphRelaxationIndex elt_index = relaxed_queries[xss_index]
		    .get(index);
	    for (int i = 0; i < elt_index.getElement_index().length; i++) {
		int index_in_query = this.xss_query_index[xss_index][i];
		index_sim = index_sim
			* relaxation_of_element[index_in_query][elt_index
				.getElement_index()[i]].getSimilarity();
	    }
	    found_pos = current_child_similarity <= index_sim;
	    pos = index + 1;
	    index = index - 1;
	}
	if (found_pos) {
	    relaxed_queries[xss_index].add(pos, child);
	} else {
	    relaxed_queries[xss_index].add(0, child);
	}
    }

    @Override
    public CQuery[] getAllRelaxedQueries() {

	int size = 0;
	for (int i = 0; i < already_relaxed_xss.length; i++) {
	    size = size + already_relaxed_xss[i].size();
	}
	CQuery[] relaxed_queries = new CQuery[size];

	for (int j = 0; j < already_relaxed_xss.length; j++) {
	    for (int i = 0; i < already_relaxed_xss[j].size(); i++) {
		relaxed_queries[i] = this.getQuery(
			already_relaxed_xss[j].get(i), j);
	    }
	}

	return relaxed_queries;
    }

    protected void logger_init() {

	LocalDateTime time = LocalDateTime.now();
	String time_value = "" + time.getDayOfMonth() + time.getMonthValue()
		+ time.getHour() + time.getMinute() + time.getSecond();

	String logfile = this.getClass().getSimpleName() + "-Process" + "-"
		+ time_value + ".log";

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
