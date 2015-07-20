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
package fr.ensma.lias.qarscore.engine.relaxation.strategy.implementation;

import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.optimization.CQueryIndexMap;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.optimization.CQueryTreeMap;


/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyWithIndex extends AbstractLatticeStrategy {
    
    private final int NUMBER_OF_EXPECTED_ANSWERS ;
    private final Session SESSION;

    public CQueryIndexMap indexCQuery ;

    /**
     * Get a lattice strategy relaxation for a session s and a number answers of
     * wanted answers
     * 
     * @param s
     * @param answers
     * @return
     */
    protected static LatticeStrategyWithIndex getLatticeStrategyWithIndex(Session s,
	    CQuery query, int answers) {
	return new LatticeStrategyWithIndex(s, query, answers);
    }

    protected static LatticeStrategyWithIndex getLatticeStrategyWithIndex(Session s) {
	return new LatticeStrategyWithIndex(s);
    }
    
    protected LatticeStrategyWithIndex(Session s) {
	number_of_query_executed = 0;
	number_of_query_reexecuted = 0;
	size_of_cartesian_product = 0;
	duration_of_execution = 0;
	
	NUMBER_OF_EXPECTED_ANSWERS = 1;
	SESSION = s;	
	indexCQuery = new CQueryTreeMap(); // Tree index we can also use HaspMap Index
    }    
    
    /**
     * private constructor
     */
    protected LatticeStrategyWithIndex(Session s, CQuery query, int answers) {
	number_of_query_executed = 0;
	number_of_query_reexecuted = 0;
	size_of_cartesian_product = 0;
	duration_of_execution = 0;

	NUMBER_OF_EXPECTED_ANSWERS = answers;
	SESSION = s;
	CURRENT_CONJUNCTIVE_QUERY = query;
	
	indexCQuery = new CQueryTreeMap(); // Tree index we can also use HaspMap Index
	
	duration_of_execution = System.currentTimeMillis();
	this.computeMFS(CURRENT_CONJUNCTIVE_QUERY);
	duration_of_execution = System.currentTimeMillis() - duration_of_execution;
	
	actualQuery = CURRENT_CONJUNCTIVE_QUERY;
	MFS_CURRENT_QUERY = failingCauses;
	XSS_CURRENT_QUERY = maximalSubqueries;
    }
  
    @Override
    public boolean hasLeastKAnswers(CQuery query) {


/*	//use the following code for execution without cartesian product
	 
	List<CQuery> queries = new ArrayList<CQuery>();
	queries.add(query);
*/	
	List<CQuery> queries = query.getCartesianProduct();
	if(queries.size()!=1){
	    size_of_cartesian_product++;
	    logger.info("*******************Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
	}
	
	for(CQuery a_connex_query:queries){
	    
	    Integer numberAnswer = indexCQuery.indexEvaluationQuery(a_connex_query);
	    
	    if(numberAnswer!=null){
		number_of_query_reexecuted ++;
		if(numberAnswer < NUMBER_OF_EXPECTED_ANSWERS){
		    if(queries.size()!=1){
			logger.info("******************* End Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
		    }
		    return false;
		}
		continue;
	    }

	    int nbSolution = 0;
	    try {
		QueryExecution qexec = QueryExecutionFactory.create(a_connex_query.getSPARQLQuery(), SESSION.getModel());
		try {
		    ResultSet results = qexec.execSelect();
		    while (results.hasNext() && (nbSolution < NUMBER_OF_EXPECTED_ANSWERS)) {
			results.nextSolution();
			nbSolution++;
			}
		    } finally {
			qexec.close();
		    }
		} finally {
		}
	    
	    indexCQuery.put(a_connex_query, nbSolution);
	    number_of_query_executed ++;

	    /*
	     * Think to put the right log if you don't want to execute with cartesian product
	     */
	    if(nbSolution >= NUMBER_OF_EXPECTED_ANSWERS){
		logger.info("Execution of : "+a_connex_query.getQueryLabel()+"                           Once Succes");
	    }
	    else {
		logger.info("Execution of : "+a_connex_query.getQueryLabel()+"                           Once Echec");
		if(queries.size()!=1){
		    logger.info("*******************End Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
		}
		return false;
	    }
	}
	
	if(queries.size()!=1){
	    logger.info("*******************End Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
	}
	return true;
    }
   
    /**
     * @return the indexCQuery
     */
    public CQueryIndexMap getIndexCQuery() {
        return indexCQuery;
    }
}