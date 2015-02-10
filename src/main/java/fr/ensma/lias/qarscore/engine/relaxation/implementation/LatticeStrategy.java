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

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationStrategies;

/**
 * @author Geraud FOKOU
 */
public class LatticeStrategy implements RelaxationStrategies {

    private final int K_ANSWERS;
    private Session session;
    
    /**
     * 
     */
    public LatticeStrategy() {
	K_ANSWERS = 1;
	// TODO Auto-generated constructor stub
    }

    @Override
    public CQuery getAFailingCause(CQuery query) {
	
	if(!query.isValidQuery()){
	    return null;
	}
	
	if(query.getElementList().size()==1){
	    return CQueryFactory.cloneCQuery(query);
	}
	
	List<CElement> causes = new ArrayList<CElement>();
	CQuery tempQuery = CQueryFactory.cloneCQuery(query);
	for( CElement elt:query.getElementList()){
	    tempQuery.getElementList().remove(elt);
	    CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	    temp.getElementList().addAll(causes);
	    if(temp.isValidQuery()){
		if(!hasLeastKAnswers(temp, K_ANSWERS)){
		    causes.add(elt);
		}
	    }
	}
	
	return CQueryFactory.createCQuery(causes);
    }

    @Override
    public boolean isAFailingCause(CQuery query) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public List<CQuery> getFailingCauses(CQuery query) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CQuery> getSuccessSubQueries(CQuery query) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean hasLeastKAnswers(CQuery query) {
	
	if(!query.isValidQuery()){
	    return false;
	}
			
	int nbSolution = 0;
	try {
	    QueryExecution qexec = QueryExecutionFactory.create(, dataset);
	    try {
	    	ResultSet results = qexec.execSelect();
	    	while (results.hasNext() && (nbSolution <= K_ANSWERS)) {
	    		results.nextSolution();
	    		nbSolution++;
	    	}
	    } finally {
		qexec.close();
	    }
	} finally {
	}
	return nbSolution >= K_ANSWERS;

	return false;
    }

}
