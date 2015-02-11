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
package fr.ensma.lias.qarscore.statement;

import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;

/**
 * @author Geraud FOKOU
 */
public interface Statement {

    /**
     * Get the Session of the current statement
     * 
     * @return
     */
    Session getSession();

    /**
     * Get the Session of the current statement
     * 
     * @return
     */
    String getQuery();

    /**
     * Prepare the query for the using, it is an execution of the query
     * 
     * @param query
     */
    void preparedQuery(String query);

    /**
     * Prepare a relaxation following a particular strategy
     * @param strategy
     */
    void preparedRelaxation(int strategy);
    
    /**
     * execute a SPARQL query
     * 
     * @param query
     */
    ResultSet executeSPARQLQuery();

    /**
     * Return the failing cause of a SPARQL query if there exist
     * 
     * @param query
     */
    void getFailingCause();

    /**
     * Return the maximal subqueries of the query
     * 
     * @param query
     */
    void getMaxSuccessQuery();
    
    /**
     * return the most similar queries to the user query
     */
    void getSimilarityQuery();
}
