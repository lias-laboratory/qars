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

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree;

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
     * execute a SPARQL query
     * 
     * @param query
     */
    ResultSet executeSPARQLQuery(String query);

    /**
     * Prepare a relaxation following a particular strategy
     * 
     * @param strategy
     */
    void explainFailure(String query, boolean strategy);

    /**
     * Return the failing cause of a SPARQL query if there exist
     * 
     * @param query
     */
    List<String> getFailingCause();

    /**
     * Return the maximal subqueries of the query
     * 
     * @param query
     */
    List<String> getMaxSuccessQuery();

    /**
     * Execute a query with relaxation operator
     * 
     * @param query
     * @return
     */
    Map<ResultSet, Double> executeRelaxedQuery(String query);

    /**
     * return the most similar queries to the user query
     */
    Map<ResultSet, Double> automaticRelaxation(String query, int size_answers);

    /**
     * Get the relaxatiion plan of a relaxation
     * 
     * @return
     */
    RelaxationTree getRelaxationPlan();

}
