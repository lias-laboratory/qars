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

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.optimization.CQueryIndexMap;
import fr.ensma.lias.qarscore.engine.relaxation.optimization.CQueryTreeMap;

/**
 * @author Geraud FOKOU
 */
public class LatticeStrategyWithIndex extends AbstractLatticeStrategy {

    public static int number_of_executed_query = 0;
    public static int number_of_reexecuted_query = 0;
    private final int NUMBER_OF_EXPECTED_ANSWERS ;
    private final Session SESSION;

    private CQueryIndexMap indexCQuery ;

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

	if (!query.isValidQuery()) {
	    return false;
	}

	Integer numberAnswer = indexCQuery.indexEvaluationQuery(query);
	if(numberAnswer!=null){
	    number_of_reexecuted_query ++;
	    return numberAnswer >= NUMBER_OF_EXPECTED_ANSWERS;
	}
	
	int nbSolution = 0;
	try {
	    QueryExecution qexec = QueryExecutionFactory.create(
		    query.getSPARQLQuery(), SESSION.getDataset());
	    try {
		ResultSet results = qexec.execSelect();
		while (results.hasNext()
			&& (nbSolution < NUMBER_OF_EXPECTED_ANSWERS)) {
		    results.nextSolution();
		    nbSolution++;
		}
	    } finally {
		qexec.close();
	    }
	} finally {
	}

	indexCQuery.put(query, nbSolution);
	number_of_executed_query ++;
	if(nbSolution >= NUMBER_OF_EXPECTED_ANSWERS){
	    System.out.println("Execution of : "+query.getQueryLabel()+"                          Once Succes");
	}
	else {
	    System.out.println("Execution of : "+query.getQueryLabel()+"                          Once Echec");
	}

	return nbSolution >= NUMBER_OF_EXPECTED_ANSWERS;
    }
   
    /**
     * @return the indexCQuery
     */
    public CQueryIndexMap getIndexCQuery() {
        return indexCQuery;
    }
}