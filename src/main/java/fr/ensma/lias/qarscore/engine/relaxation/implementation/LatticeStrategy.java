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

    private final int NUMBER_OF_EXPECTED_ANSWERS;
    private final Session SESSION;
    private final CQuery CURRENT_CONJUNCTIVE_QUERY;
    private final List<CQuery> MFS_CURRENT_QUERY;
    private final List<CQuery> XSS_CURRENT_QUERY;
    private List<CQuery> failingCauses = null;
    private List<CQuery> maximalSubqueries = null;

    /**
     * Get a lattice strategy relaxation for a session s and a number answers of
     * wanted answers
     * 
     * @param s
     * @param answers
     * @return
     */
    public static LatticeStrategy getLatticeStrategy(Session s, CQuery query,
	    int answers) {
	return new LatticeStrategy(s, query, answers);
    }

    /**
     * private constructor
     */
    private LatticeStrategy(Session s, CQuery query, int answers) {
	NUMBER_OF_EXPECTED_ANSWERS = answers;
	SESSION = s;
	CURRENT_CONJUNCTIVE_QUERY = query;
	MFS_CURRENT_QUERY = getAllMFS(CURRENT_CONJUNCTIVE_QUERY);
	XSS_CURRENT_QUERY = maximalSubqueries;
    }

    @Override
    public CQuery getOneMFS() {

	return MFS_CURRENT_QUERY.get(0);
    }

    @Override
    public CQuery getOneMFS(CQuery query) {

	if (!query.isValidQuery()) {
	    return null;
	}

	if (hasLeastKAnswers(query)) {
	    return CQueryFactory.createCQuery(new ArrayList<CElement>());
	}

	if (query.getElementList().size() == 1) {
	    return CQueryFactory.cloneCQuery(query);
	}

	List<CElement> causes = new ArrayList<CElement>();
	CQuery tempQuery = CQueryFactory.cloneCQuery(query);

	for (CElement elt : query.getElementList()) {
	    tempQuery.getElementList().remove(elt);
	    CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	    temp.getElementList().addAll(causes);
	    if (temp.isValidQuery()) {
		if (hasLeastKAnswers(temp)) {
		    causes.add(elt);
		}
	    }
	}
	return CQueryFactory.createCQuery(causes);
    }

    @Override
    public boolean isMFS(CQuery query) {

	if (!query.isValidQuery()) {
	    return false;
	}

	if (hasLeastKAnswers(query)) {
	    return false;
	}

	for (CElement elt : query.getElementList()) {
	    CQuery tempQuery = CQueryFactory.cloneCQuery(query);
	    tempQuery.getElementList().remove(elt);
	    if (tempQuery.isValidQuery()) {
		if (!hasLeastKAnswers(tempQuery)) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public List<CQuery> getAllMFS(CQuery query) {

	failingCauses = new ArrayList<CQuery>();
	maximalSubqueries = new ArrayList<CQuery>();

	if (!query.isValidQuery()) {
	    return failingCauses;
	}

	if (hasLeastKAnswers(query)) {
	    maximalSubqueries.add(CQueryFactory.cloneCQuery(query));
	    return failingCauses;
	}

	failingCauses.add(getOneMFS(query));

	ArrayList<CQuery> potentialsMaximalSubqueries = new ArrayList<CQuery>();
	for (CElement elt : failingCauses.get(failingCauses.size() - 1)
		.getElementList()) {
	    CQuery tempquery = CQueryFactory.cloneCQuery(query);
	    tempquery.getElementList().remove(elt);
	    potentialsMaximalSubqueries.add(tempquery);
	}

	while (potentialsMaximalSubqueries.size() != 0) {

	    CQuery tempquery = potentialsMaximalSubqueries.get(0);

	    if (!tempquery.isValidQuery()) {
		potentialsMaximalSubqueries.remove(0);
		continue;
	    }

	    if (hasLeastKAnswers(tempquery)) {
		ArrayList<CQuery> oldMaximalSubqueries = potentialsMaximalSubqueries;
		potentialsMaximalSubqueries = new ArrayList<CQuery>();
		potentialsMaximalSubqueries.addAll(oldMaximalSubqueries);
		for (CQuery pxss : oldMaximalSubqueries) {
		    if (tempquery.getElementList().containsAll(
			    pxss.getElementList())) {
			potentialsMaximalSubqueries.remove(pxss);
		    }
		}
		boolean isContained = false;
		for (CQuery xss : maximalSubqueries) {
		    if (xss.getElementList().containsAll(
			    tempquery.getElementList())) {
			isContained = true;
			break;
		    }
		}
		if (!isContained) {
		    maximalSubqueries.add(CQueryFactory.cloneCQuery(tempquery));
		}
		continue;
	    }

	    failingCauses.add(getOneMFS(tempquery));
	    ArrayList<CQuery> newMaximalSubqueries = new ArrayList<CQuery>();
	    ArrayList<CQuery> oldMaximalSubqueries = new ArrayList<CQuery>();

	    for (CQuery pxss : potentialsMaximalSubqueries) {
		if (pxss.getElementList().containsAll(
			failingCauses.get(failingCauses.size() - 1)
				.getElementList())) {
		    for (CElement elt : failingCauses.get(
			    failingCauses.size() - 1).getElementList()) {
			CQuery temp = CQueryFactory.cloneCQuery(pxss);
			temp.getElementList().remove(elt);
			newMaximalSubqueries.add(temp);
		    }
		} else {
		    oldMaximalSubqueries.add(pxss);
		}
	    }
	    potentialsMaximalSubqueries = oldMaximalSubqueries;
	    potentialsMaximalSubqueries.addAll(newMaximalSubqueries);
	}
	return failingCauses;
    }

    @Override
    public List<CQuery> getAllXSS(CQuery query) {

	this.getAllMFS(query);
	return maximalSubqueries;
    }

    @Override
    public List<CQuery> getAllMFS() {

	return MFS_CURRENT_QUERY;
    }

    @Override
    public List<CQuery> getAllXSS() {

	return XSS_CURRENT_QUERY;
    }

    @Override
    public boolean hasLeastKAnswers(CQuery query) {

	if (!query.isValidQuery()) {
	    return false;
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
	return nbSolution >= NUMBER_OF_EXPECTED_ANSWERS;
    }
}
