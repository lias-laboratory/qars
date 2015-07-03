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
package fr.ensma.lias.qarscore.engine.relaxation.implementation;

import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.optimization.*;


/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyWithIndex extends AbstractLatticeStrategy {

    public static int number_of_executed_query = 0;
    public static int number_of_reexecuted_query = 0;
    public static int size_of_cartesian_product = 0;
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

    /**
     * private constructor
     */
    protected LatticeStrategyWithIndex(Session s, CQuery query, int answers) {
	NUMBER_OF_EXPECTED_ANSWERS = answers;
	SESSION = s;
	CURRENT_CONJUNCTIVE_QUERY = query;
	indexCQuery = new CQueryTreeMap();
	this.computeMFS(CURRENT_CONJUNCTIVE_QUERY);
	actualQuery = CURRENT_CONJUNCTIVE_QUERY;
	MFS_CURRENT_QUERY = failingCauses;
	XSS_CURRENT_QUERY = maximalSubqueries;
    }
  
    @Override
    public boolean hasLeastKAnswers(CQuery query) {

//	if (!query.isValidQuery()) {
//	    return false;
//	}
//	List<CQuery> queries = new ArrayList<CQuery>();
//	queries.add(query);
	List<CQuery> queries = query.getCartesianProduct();
	if(queries.size()!=1){
	    size_of_cartesian_product++;
	    System.out.println("*******************Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
	}
	
	for(CQuery a_connex_query:queries){
	    
	    Integer numberAnswer = indexCQuery.indexEvaluationQuery(a_connex_query);
	    
	    if(numberAnswer!=null){
		number_of_reexecuted_query ++;
		if(numberAnswer < NUMBER_OF_EXPECTED_ANSWERS){
		    if(queries.size()!=1){
			System.out.println("******************* End Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
		    }
		    return false;
		}
		continue;
	    }

	    int nbSolution = 0;
	    try {
		QueryExecution qexec = QueryExecutionFactory.create(a_connex_query.getSPARQLQuery(), SESSION.getDataset());
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
	    number_of_executed_query ++;

	    if(nbSolution >= NUMBER_OF_EXPECTED_ANSWERS){
		System.out.println("Execution of : "+a_connex_query.getQueryLabel()+"                           Once Succes");
	    }
	    else {
		System.out.println("Execution of : "+a_connex_query.getQueryLabel()+"                           Once Echec");
		if(queries.size()!=1){
		    System.out.println("*******************End Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
		}
		return false;
	    }
	}
	
	if(queries.size()!=1){
	    System.out.println("*******************End Execution of query with cartesian product: "+query.getQueryLabel()+"**********************************");
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