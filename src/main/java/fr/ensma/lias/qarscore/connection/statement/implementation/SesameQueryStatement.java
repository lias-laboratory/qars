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

import org.apache.jena.query.ResultSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import fr.ensma.lias.qarscore.connection.implementation.SesameSession;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;

/**
 * @author Geraud FOKOU
 */
public class SesameQueryStatement implements QueryStatement {

    private SesameSession session;

    private String sparqlQuery;

    private TupleQueryResult results;

    /**
     * 
     */
    public SesameQueryStatement(String query, SesameSession s) {
	sparqlQuery = query;
	session = s;
    }

    @Override
    public TupleQueryResult executeQuery() {

	TupleQuery tupleQuery;

	try {
	    tupleQuery = session.getRepositoryConnection().prepareTupleQuery(
		    QueryLanguage.SPARQL, sparqlQuery);
	    results = tupleQuery.evaluate();
	} catch (RepositoryException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (MalformedQueryException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return results;
    }

    @Override
    public int getResultSetSize() {

	int size = 0;

	if (results == null) {
	    this.executeQuery();
	}
	try {
	    while (results.hasNext()) {
		results.next();
		size++;
	    }
	} catch (QueryEvaluationException e) {
	    e.printStackTrace();
	} finally {
	}
	return size;
    }

    @Override
    public int getResultSetSize(int limit) {
	
	int size = 0;

	if (results == null) {
	    this.executeQuery();
	}
	try {
	    while ((results.hasNext())&&(size<limit)) {
		results.next();
		size++;
	    }
	} catch (QueryEvaluationException e) {
	    e.printStackTrace();
	} finally {
	}
	return size;

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

	if (results != null) {
	    results.close();
	    results = null;
	}
    }
}
