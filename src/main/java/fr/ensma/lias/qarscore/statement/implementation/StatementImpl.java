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
package fr.ensma.lias.qarscore.statement.implementation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.statement.Statement;

/**
 * @author Geraud FOKOU
 */
public class StatementImpl implements Statement {

    /**
     * Session for this statement
     */
    private Session session = null;

    /**
     * The current query of the statement
     */
    private Query currentQuery = null;

    public StatementImpl(Session s) {
	session = s;
    }

    public StatementImpl(Session s, String query) {
	session = s;
	currentQuery = QueryFactory.create(query);
    }

    @Override
    public Session getSession() {
	return session;
    }

    @Override
    public String getQuery() {

	if (currentQuery != null) {
	    return currentQuery.serialize();
	}
	return null;
    }

    @Override
    public void preparedQuery(String query) {
	currentQuery = QueryFactory.create(query);
    }

    @Override
    public void preparedRelaxation(int strategy) {

	if ((currentQuery == null) || (session == null)) {
	    return;
	}

	switch (strategy) {
	case 0:

	    break;
	default:
	    throw new IllegalArgumentException("wrong strategy number");
	}
    }

    @Override
    public ResultSet executeSPARQLQuery() {
	return QueryExecutionFactory.create(getQuery(), session.getDataset())
		.execSelect();
    }

    @Override
    public void getFailingCause() {
    }

    @Override
    public void getMaxSuccessQuery() {
    }

    @Override
    public void getSimilarityQuery() {
	// TODO Auto-generated method stub

    }
}
