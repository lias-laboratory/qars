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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationStrategies;
import fr.ensma.lias.qarscore.engine.relaxation.SimilarityStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.StrategiesFactory;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree;
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
    private CQuery currentQuery = null;

    /**
     * Lattice Strategy for explanation query failure
     */
    private RelaxationStrategies relaxation_strategy_explanation = null;

    /**
     * engine for automatic relaxation
     */
    private SimilarityStrategy relax_engine;

    public StatementImpl(Session s) {
	session = s;
    }

    @Override
    public Session getSession() {
	return session;
    }

    @Override
    public String getQuery() {

	if (currentQuery != null) {
	    return currentQuery.toString();
	}
	return null;
    }

    @Override
    public ResultSet executeSPARQLQuery(String query) {
	currentQuery = CQueryFactory.createCQuery(query);
	if (!currentQuery.isValidQuery()) {
	    return null;
	}
	return QueryExecutionFactory.create(getQuery(), session.getDataset())
		.execSelect();
    }

    @Override
    public void explainFailure(String query, boolean strategy) {

	boolean isReady = false;
	if (currentQuery == null) {
	    currentQuery = CQueryFactory.createCQuery(query);
	} else {
	    CQuery tempquery = CQueryFactory.createCQuery(query);
	    if (currentQuery.equals(tempquery)) {
		if (relaxation_strategy_explanation != null) {
		    isReady = true;
		}
	    } else {
		currentQuery = tempquery;
	    }
	}

	if (isReady) {
	    return;
	}
	if (strategy) {
	    relaxation_strategy_explanation = StrategiesFactory
		    .getLatticeStrategy(session, currentQuery);
	} else {
	    relaxation_strategy_explanation = StrategiesFactory
		    .getMatrixStrategy(session, currentQuery);
	}
    }

    @Override
    public List<String> getFailingCause() {

	List<CQuery> cqueries_mfs = relaxation_strategy_explanation.getAllMFS();
	List<String> mfs_string_queries = new ArrayList<String>();
	for (CQuery query : cqueries_mfs) {
	    mfs_string_queries.add(query.toString());
	}
	return mfs_string_queries;
    }

    @Override
    public List<String> getMaxSuccessQuery() {

	List<CQuery> cqueries_xss = relaxation_strategy_explanation.getAllXSS();
	List<String> xss_string_queries = new ArrayList<String>();
	for (CQuery query : cqueries_xss) {
	    xss_string_queries.add(query.toString());
	}
	return xss_string_queries;
    }

    @Override
    public Map<ResultSet, Double> executeRelaxedQuery(String query,
	    Map<String, String> operator_param) {

	currentQuery = CQueryFactory.createCQuery(query);
	Map<ResultSet, Double> all_result = new HashMap<ResultSet, Double>();
	relax_engine = new SimilarityStrategy(currentQuery, session);
	for (String classe : operator_param.keySet()) {
	    String operator = operator_param.get(classe);
	    boolean has_operate = false;
	    switch (operator.toLowerCase()) {
	    case "gen":
		has_operate = relax_engine.next_gen_relax(classe, 1);
		break;
	    case "sib":
		has_operate = relax_engine.next_sib_relax(classe);
		break;
	    default:
		break;
	    }
	    if (has_operate) {
		for (RelaxationTree relaxed_tree : relax_engine
			.get_last_relaxed_queries()) {
		    ResultSet current_result = QueryExecutionFactory.create(
			    relaxed_tree.getQuery().toString(),
			    session.getDataset()).execSelect();
		    all_result
			    .put(current_result, relaxed_tree.getSimilarity());
		}
	    }
	}
	return all_result;
    }

    @Override
    public Map<ResultSet, Double> automaticRelaxation(String query,
	    int size_answers) {

	currentQuery = CQueryFactory.createCQuery(query);
	Map<ResultSet, Double> all_result = new HashMap<ResultSet, Double>();
	int size_all_result = 0;
	relax_engine = new SimilarityStrategy(currentQuery, session);

	for (RelaxationTree relaxed_tree : relax_engine.get_last_relaxed_queries()) {
	    ResultSet current_result = QueryExecutionFactory.create(
		    relaxed_tree.getQuery().toString(), session.getDataset())
		    .execSelect();
	    size_all_result = size_all_result + current_result.getRowNumber();
	    all_result.put(current_result, relaxed_tree.getSimilarity());
	}

	while ((relax_engine.next_step()) && (size_all_result < size_answers)) {
	    for (RelaxationTree relaxed_tree : relax_engine.get_last_relaxed_queries()) {
		ResultSet current_result = QueryExecutionFactory.create(
			relaxed_tree.getQuery().toString(),
			session.getDataset()).execSelect();
		size_all_result = size_all_result
			+ current_result.getRowNumber();
		all_result.put(current_result, relaxed_tree.getSimilarity());
	    }
	}
	return all_result;
    }

    @Override
    public RelaxationTree getRelaxationPlan() {
	return relax_engine.getRelaxed_queries_graph();
    }
}
