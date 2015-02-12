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
    private final Session session;
    private List<CQuery> failingCauses;
    private List<CQuery> maximalSubqueries;

    /**
     * Get a lattice strategy relaxation for a session s and a number answers of
     * wanted answers
     * 
     * @param s
     * @param answers
     * @return
     */
    public static LatticeStrategy getLatticeStrategy(Session s, int answers) {
	return new LatticeStrategy(s, answers);
    }

    /**
     * private constructor
     */
    private LatticeStrategy(Session s, int answers) {
	K_ANSWERS = answers;
	session = s;
    }

    @Override
    public CQuery getAFailingCause(CQuery query) {

	if (!query.isValidQuery()) {
	    return null;
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
		if (!hasLeastKAnswers(temp)) {
		    causes.add(elt);
		}
	    }
	}
	return CQueryFactory.createCQuery(causes);
    }

    @Override
    public boolean isAFailingCause(CQuery query) {

	if (!query.isValidQuery()) {
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
    public List<CQuery> getFailingCauses(CQuery query) {

	if (!query.isValidQuery()) {
	    return failingCauses;
	}

	maximalSubqueries = new ArrayList<CQuery>();

	if (hasLeastKAnswers(query)) {
	    maximalSubqueries.add(CQueryFactory.cloneCQuery(query));
	    return failingCauses;
	}

	failingCauses = new ArrayList<CQuery>();
	failingCauses.add(getAFailingCause(query));

	ArrayList<CQuery> potentialsMaximalSubqueries = new ArrayList<CQuery>();
	for (CElement elt : failingCauses.get(failingCauses.size() - 1)
		.getElementList()) {
	    CQuery tempquery = CQueryFactory.cloneCQuery(query);
	    tempquery.getElementList().remove(elt);
	    potentialsMaximalSubqueries.add(tempquery);
	}

	while (potentialsMaximalSubqueries.size() != 0) {
	    CQuery tempquery = potentialsMaximalSubqueries.get(0);

	    if (hasLeastKAnswers(tempquery)) {
		maximalSubqueries.add(CQueryFactory.cloneCQuery(tempquery));
		potentialsMaximalSubqueries.remove(0);
		continue;
	    }

	    failingCauses.add(getAFailingCause(tempquery));
	    ArrayList<CQuery> templist = potentialsMaximalSubqueries;
	    potentialsMaximalSubqueries = new ArrayList<CQuery>();

	    for (CQuery potentialElt : templist) {
		if (potentialElt.getElementList().containsAll(
			failingCauses.get(failingCauses.size() - 1)
				.getElementList())) {
		    for (CElement elt : failingCauses.get(
			    failingCauses.size() - 1).getElementList()) {
			CQuery temp = CQueryFactory.cloneCQuery(tempquery);
			temp.getElementList().remove(elt);
			potentialsMaximalSubqueries.add(temp);
		    }
		} else {
		    potentialsMaximalSubqueries.add(potentialElt);
		}
	    }
	}
	return failingCauses;
    }

    @Override
    public List<CQuery> getSuccessSubQueries() {

	return maximalSubqueries;
    }

    @Override
    public boolean hasLeastKAnswers(CQuery query) {

	if (!query.isValidQuery()) {
	    return false;
	}

	int nbSolution = 0;
	try {
	    QueryExecution qexec = QueryExecutionFactory.create(
		    query.getSPARQLQuery(), session.getDataset());
	    try {
		ResultSet results = qexec.execSelect();
		while (results.hasNext() && (nbSolution < K_ANSWERS)) {
		    nbSolution++;
		}
	    } finally {
		qexec.close();
	    }
	} finally {
	}
	return nbSolution >= K_ANSWERS;
    }
}
