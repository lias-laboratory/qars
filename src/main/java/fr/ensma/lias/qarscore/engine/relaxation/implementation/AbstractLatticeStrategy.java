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

import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationStrategies;

/**
 * @author Geraud FOKOU
 */
public abstract class AbstractLatticeStrategy implements RelaxationStrategies {

    protected CQuery CURRENT_CONJUNCTIVE_QUERY ;
    protected List<CQuery> MFS_CURRENT_QUERY ;
    protected List<CQuery> XSS_CURRENT_QUERY ;
    protected CQuery actualQuery ;
    protected List<CQuery> failingCauses ;
    protected List<CQuery> maximalSubqueries ;
    
   /**
     * Computes all the MFS and XSS of a CQuery query
     * 
     * @param query
     */
   protected void computeMFS(CQuery query) {
       
	failingCauses = new ArrayList<CQuery>();
	maximalSubqueries = new ArrayList<CQuery>();

	if (!query.isValidQuery()) {
	    return;
	}

	if (hasLeastKAnswers(query)) {
	    maximalSubqueries.add(CQueryFactory.cloneCQuery(query));
	    return;
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
		    if (pxss.isSubQueryOf(tempquery)) {
			potentialsMaximalSubqueries.remove(pxss);
		    }
		}
		boolean isContained = false;
		for (CQuery xss : maximalSubqueries) {
		    if (tempquery.isSubQueryOf(xss)) {
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
    }

    @Override
    public boolean hasLeastKAnswers() {

	return this.hasLeastKAnswers(CURRENT_CONJUNCTIVE_QUERY);
    }

    @Override
    public boolean isMFS() {

	return this.isMFS(CURRENT_CONJUNCTIVE_QUERY);
    }

    @Override
    public CQuery getOneMFS() {

	return MFS_CURRENT_QUERY.get(0);
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
    abstract public boolean hasLeastKAnswers(CQuery query) ;

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

	for (int i = 0; i < query.getElementList().size() - 1; i++) {
	    CElement elt = query.getElementList().get(i);
	    tempQuery.getElementList().remove(elt);
	    CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	    temp.getElementList().addAll(causes);
	    if (temp.isValidQuery()) {
		if (hasLeastKAnswers(temp)) {
		    causes.add(elt);
		}
	    }
	}

	CElement elt = query.getElementList().get(
		query.getElementList().size() - 1);

	if (causes.size() == 0) {
	    causes.add(elt);
	    return CQueryFactory.createCQuery(causes);
	}

	tempQuery.getElementList().remove(elt);
	CQuery temp = CQueryFactory.cloneCQuery(tempQuery);
	temp.getElementList().addAll(causes);
	if (temp.isValidQuery()) {
	    if (hasLeastKAnswers(temp)) {
		causes.add(elt);
	    }
	}
	return CQueryFactory.createCQuery(causes);
    }

    @Override
    public List<CQuery> getAllMFS(CQuery query) {

	if (this.actualQuery == query) {
	    return failingCauses;
	}
	this.actualQuery = query;
	this.computeMFS(query);
	return failingCauses;
    }

    @Override
    public List<CQuery> getAllXSS(CQuery query) {

	if (this.actualQuery == query) {
	    return maximalSubqueries;
	} else {
	    this.actualQuery = query;
	    this.computeMFS(query);
	    return maximalSubqueries;
	}
    }
}