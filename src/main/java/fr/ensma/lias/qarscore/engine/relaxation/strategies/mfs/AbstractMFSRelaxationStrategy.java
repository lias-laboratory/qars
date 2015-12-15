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
package fr.ensma.lias.qarscore.engine.relaxation.strategies.mfs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.RelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;

/**
 * @author Geraud FOKOU
 */
public abstract class AbstractMFSRelaxationStrategy implements RelaxationStrategy {

    public int number_mfs_query_executed =0;
    public int number_mfs_check_query_executed =0;
    public long duration__mfs_query_executed =0;
    public long duration__mfs_check_query_executed =0;
    
    protected Logger logger = Logger.getLogger(AbstractMFSRelaxationStrategy.class);
    
    /**
     * Field for initialization of the relaxation process
     */
    protected List<GraphRelaxationIndex> relaxed_queries;
    protected List<GraphRelaxationIndex> already_relaxed_queries;
    protected GraphRelaxationIndex relaxed_graph;
    protected NodeRelaxed[][] relaxation_of_element;
    protected CQuery query_to_relax;
    protected Session session;

    /**
     * Result of each relaxation
     */
    protected CQuery current_relaxed_query;
    protected double current_similarity;
    protected List<int[]> current_level;

    /**
     * Abstract constructor
     */
    public AbstractMFSRelaxationStrategy(CQuery query, Session s) {
	
	this.logger_init();
	query_to_relax = query;
	session = s;
	relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	already_relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	int[] relaxation_limit_index = new int[query_to_relax.getElementList()
		.size()];
	int[] relaxation_index = new int[query_to_relax.getElementList().size()];
	relaxation_of_element = new NodeRelaxed[query_to_relax.getElementList()
		.size()][];
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    CElement element = query_to_relax.getElementList().get(i);
	    relaxation_of_element[i] = (new TripleRelaxation(element, session,
		    TripleRelaxation.SIM_ORDER)).get_relaxed_node_list();
	    relaxation_limit_index[i] = relaxation_of_element[i].length;
	    relaxation_index[i] = 0;
	}
	relaxed_graph = new GraphRelaxationIndex(relaxation_index,
		relaxation_limit_index);
	relaxed_queries.add(relaxed_graph);

    }
    /**
     * Abstract constructor
     */
    protected AbstractMFSRelaxationStrategy(CQuery query, Session s, boolean optimization) {
	
	this.logger_init();
	query_to_relax = query;
	session = s;
	relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	already_relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	int[] relaxation_limit_index = new int[query_to_relax.getElementList()
		.size()];
	int[] relaxation_index = new int[query_to_relax.getElementList().size()];
	relaxation_of_element = new NodeRelaxed[query_to_relax.getElementList()
		.size()][];
	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    CElement element = query_to_relax.getElementList().get(i);
	    relaxation_of_element[i] = (new TripleRelaxation(element, session,
		    TripleRelaxation.SIM_ORDER)).get_relaxed_node_list();
	    relaxation_limit_index[i] = relaxation_of_element[i].length;
	    relaxation_index[i] = 0;
	}
	relaxed_graph = new GraphRelaxationIndex(relaxation_index,
		relaxation_limit_index, !optimization);
	relaxed_queries.add(relaxed_graph);
    }

    /**
     * Are there any other relaxation for the current query?
     */
    @Override
    public boolean hasNext() {
	return (!this.relaxed_queries.isEmpty());
    }

    /**
     * Similarity between the current relaxation and the original query
     */
    @Override
    public Double getCurrent_similarity() {
	return current_similarity;
    }

    /**
     * List of relaxation level of all the triple of the current relaxation query
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

    /**
     * Graph root of the relaxation process
     */
    @Override
    public GraphRelaxationIndex getRelaxed_graph() {
	return relaxed_graph;
    }

    @Override
    public CElement getRelaxedElement(CElement triple, int relaxation_rank) {
	
	int index = query_to_relax.getElementList().indexOf(triple);
	if(index!=-1){
	    return getRelaxedElement(index, relaxation_rank);
	}
	else {
	    return null;
	}
    }

    /**
     * The next relaxed query
     */
    @Override
    public abstract CQuery next();

    @Override
    public CQuery[] getAllRelaxedQueries() {
	
	CQuery[] relaxed_queries = new CQuery[already_relaxed_queries.size()];
	
	for (int i = 0; i<already_relaxed_queries.size(); i++){
	    
	}
	
	return relaxed_queries;
    }

    /**
     * Get the relaxation_rank th best relaxation of the triple with index num_triple in the original query
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
     * Convert a relaxation graph's node index into the corresponding query
     * @param index_query
     * @return
     */
    protected CQuery getQuery(GraphRelaxationIndex index_query){
	
	List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	this.current_similarity = 1.0;
	this.current_level = new ArrayList<int[]>();
	
	for (int i = 0; i < index_query.getElement_index().length; i++) {

	    elt_relaxed_query.add(getRelaxedElement(i,
		    index_query.getElement_index()[i]));
	    this.current_similarity = this.current_similarity
		    * relaxation_of_element[i][index_query.getElement_index()[i]]
			    .getSimilarity();
	    
	    this.current_level.add(relaxation_of_element[i][index_query.getElement_index()[i]]
		    .getRelaxation_level());
	}
	
	return CQueryFactory.createCQuery(elt_relaxed_query, query_to_relax.getSelectedQueryVar());
    }
    
    /**
     * Check if a node of the graph node is already in the list of relaxed queries.
     * @param graphRelaxationIndex
     * @return
     */
    protected boolean alreadyRelaxed(GraphRelaxationIndex graphRelaxationIndex) {

	boolean found = false;
	int i = 0;
	while ((!found) && (i < this.already_relaxed_queries.size())) {
	    found = hasSameIndex(graphRelaxationIndex.getElement_index(),
		    already_relaxed_queries.get(i).getElement_index());
	    i = i + 1;
	}
	if (found) {
	    return found;
	}
	i = 0;
	while ((!found) && (i < this.relaxed_queries.size())) {
	    found = hasSameIndex(graphRelaxationIndex.getElement_index(),
		    relaxed_queries.get(i).getElement_index());
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

    protected void insert_relaxation_graph_node(GraphRelaxationIndex child) {

	double current_child_similarity = 1.0;
	// int current_child_level = 0;
	for (int i = 0; i < child.getElement_index().length; i++) {

	    current_child_similarity = current_child_similarity
		    * relaxation_of_element[i][child.getElement_index()[i]]
			    .getSimilarity();
	    // current_child_level = current_child_level +
	    // relaxation_of_element[i][child.getElement_index()[i]].getRelaxation_level();
	}
	int pos = 0;
	int index = this.relaxed_queries.size()-1;
	boolean found_pos = false;
	while ((!found_pos) && (0 <= index)) {
	    double index_sim = 1.0;
	    GraphRelaxationIndex elt_index = relaxed_queries.get(index);
	    for (int i = 0; i < elt_index.getElement_index().length; i++) {
		index_sim = index_sim
			* relaxation_of_element[i][elt_index.getElement_index()[i]]
				.getSimilarity();
	    }
	    found_pos = current_child_similarity  <= index_sim ;
	    pos = index + 1;
	    index = index - 1;
	}
	if (found_pos) {
	    relaxed_queries.add(pos, child);
	} else {
	    relaxed_queries.add(0, child);
	}
    }

    protected void logger_init() {

	LocalDateTime time = LocalDateTime.now();
	String time_value = "" + time.getDayOfMonth() + time.getMonthValue()
		+ time.getHour() + time.getMinute() + time.getSecond();

	String logfile = this.getClass().getSimpleName()+"-Process" + "-" + time_value + ".log";

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
