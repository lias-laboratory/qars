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
package fr.ensma.lias.qarscore.connection.statement.implementation;

import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;

/**
 * @author Geraud FOKOU
 */
public class JenaQueryStatement implements QueryStatement {

    private JenaSession session;

    private String sparqlQuery;

    private ResultSet results;
    
    private  QueryExecution qexec;

    /**
     * 
     */
    public JenaQueryStatement(String query, JenaSession s) {
	sparqlQuery = query;
	session = s;
    }

    @Override
    public int getResultSetSize() {

	int size =  0;
	if (results == null) {
	    this.executeQuery();
	}
	
	try {
	    while (results.hasNext()) {
		results.nextSolution();
		size++;
	    }
	} finally {
	    results = null;
	}
	return size ;
    }
    
    @Override
    public int getResultSetSize(int limit) {
	
	int size =  0;

	if (results == null) {
	    this.executeQuery();
	}
	
	try {
	    while ((results.hasNext())&&(size<limit)) {
		results.nextSolution();
		size++;
	    }
	} finally {
	    results = null;
	}
	return size ;
    }

    @Override
    public ResultSet executeQuery() {

	try {
	    qexec = QueryExecutionFactory.create(sparqlQuery,
		    session.getDataset());
	    try {
		results = qexec.execSelect();
	    } finally {
	    }
	} finally {
	}
	
	return results;
    }

    @Override
    public Map<ResultSet, Double> relaxedQuery(
	    Map<String, String> relax_operator) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<ResultSet, Double> relaxedQuery() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void close() {
	
	if(qexec!=null){
	    qexec.close();
	    qexec = null;
	}
	if(results!=null){
	    results = null;
	}
    }

    @Override
    public String getQuery() {
	return sparqlQuery;
    }
}
