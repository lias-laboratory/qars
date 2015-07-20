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
package fr.ensma.lias.qarscore.engine.relaxation.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.RelaxationOperators;
import fr.ensma.lias.qarscore.engine.relaxation.operators.implementation.OperatorsFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.RelaxationNodes;

/**
 * @author Geraud FOKOU
 */
public class RelaxationGraphStrategy {
    
    private Logger logger;
    private final int TOP_K = 1;
    private Session session;
    private RelaxationOperators relax_operator;
    private List<CQuery> executed_queries ;
    private List<ResultSet> relaxed_answers;
    private TreeMap<RelaxationNodes, CQuery> relaxed_queries_graph;
    
    /**
     * @param session 
     * @param session
     * @param relaxed_queries_graph
     */
    public RelaxationGraphStrategy(Session s) {
	
	session =s;
	executed_queries = new ArrayList<CQuery>();
	relaxed_answers = new ArrayList<ResultSet>();
	relax_operator = OperatorsFactory.createOperator(session);
	relaxed_queries_graph = new TreeMap<RelaxationNodes, CQuery>();
    }

    /**
     * return the number of answers for the CQuery query
     * @param query
     * @return
     */
    private int get_number_answers(CQuery query) {

	int nbSolution = 0;
	executed_queries.add(query);
	try {
	    QueryExecution qexec = QueryExecutionFactory.create(
		    query.getSPARQLQuery(), session.getModel());
	    try {
		ResultSet results = qexec.execSelect();
		relaxed_answers.add(results);
		nbSolution = results.getRowNumber();
	    } finally {
		qexec.close();
	    }
	} finally {
	}

	return nbSolution;
    }

    /**
     * @return the relaxed_queries_graph
     */
    public TreeMap<RelaxationNodes, CQuery> getRelaxed_queries_graph() {
        return relaxed_queries_graph;
    }

    /**
     * relax a SPARQL query using the BFS-relaxation algorithm of Huang (www'12)
     * @param query
     * @param session
     * @return
     */
    public boolean relax_query(CQuery query){
	
	CQuery current_query_to_relax = CQueryFactory.cloneCQuery(query);
	Double sim_query_to_relax = 1.0;
	int number_answers = 0;
	
	Map<CQuery, Double> relaxed_queries = relax_operator.generalize(current_query_to_relax, sim_query_to_relax, session);
	logger.info(relaxed_queries.size());
	if(relaxed_queries.isEmpty()){
	    relaxed_queries = relax_operator.releaseValue(current_query_to_relax, sim_query_to_relax, session);
	}

	for(CQuery relaxed_query:relaxed_queries.keySet()){
	    relaxed_queries_graph.put(new RelaxationNodes(relaxed_query, relaxed_queries.get(relaxed_query)), current_query_to_relax);
	}
	
	while((!relaxed_queries_graph.isEmpty())&&(number_answers< TOP_K)){
	    
	    RelaxationNodes most_similar_node = relaxed_queries_graph.lastKey();
	    current_query_to_relax = most_similar_node.getRelaxed_query();
	    sim_query_to_relax = most_similar_node.getSimilarity_to_original();
	    
	    relaxed_queries_graph.remove(most_similar_node);
	    
	    number_answers = number_answers + this.get_number_answers(current_query_to_relax);
	    
	    relaxed_queries = relax_operator.generalize(current_query_to_relax, sim_query_to_relax, session);
		
	    if(relaxed_queries.isEmpty()){
		relaxed_queries = relax_operator.releaseValue(current_query_to_relax, sim_query_to_relax, session);
	    }

	    for(CQuery relaxed_query:relaxed_queries.keySet()){
		relaxed_queries_graph.put(new RelaxationNodes(relaxed_query, relaxed_queries.get(relaxed_query)), current_query_to_relax);
	    }	
	}
	
	return number_answers>=TOP_K;
    }

    /**
     * @return the tOP_K
     */
    public int getTOP_K() {
        return TOP_K;
    }

    /**
     * @return the executed_queries
     */
    public List<CQuery> getExecuted_queries() {
        return executed_queries;
    }

    /**
     * @return the relaxed_answers
     */
    public List<ResultSet> getRelaxed_answers() {
        return relaxed_answers;
    }
    
    
}
