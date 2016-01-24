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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation;

import java.util.List;

import org.apache.jena.query.Query;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.optimization.CQueryIndexMap;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.optimization.CQueryTreeMap;


/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyWithIndex extends AbstractLatticeStrategy {
    
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
	super(s, null, 1);
	indexCQuery = new CQueryTreeMap(); // Tree index we can also use HaspMap Index
    }    
    
    /**
     * private constructor
     */
    protected LatticeStrategyWithIndex(Session s, CQuery query, int answers) {
	super(s, query, answers);	
	indexCQuery = new CQueryTreeMap(); // Tree index we can also use HaspMap Index
	
	duration_of_execution = System.currentTimeMillis();
	this.computeMFS(actualQuery);
	duration_of_execution = System.currentTimeMillis() - duration_of_execution;
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
	    Query temp_query = a_connex_query.getSPARQLQuery();
	    temp_query.setLimit(NUMBER_OF_EXPECTED_ANSWERS);
	    int nbSolution = SESSION.getResultSize(temp_query.toString());   
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