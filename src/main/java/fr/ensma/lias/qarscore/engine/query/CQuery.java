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
package fr.ensma.lias.qarscore.engine.query;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

/**
 * @author Geraud FOKOU
 */
public class CQuery {

    /**
     * List of Simple literal create
     */
    private List<CElement> elementList;

    /**
     * List of group element
     */
    private List<ElementGroup> groupList;

    /**
     * Result variable of the current query
     */
    private List<Node> selectedQueryVar;

    /**
     * Constructor of Conjunctive query CQuery
     */
    private CQuery(List<CElement> elt, List<ElementGroup> gr,
	    List<Node> selectedVar) {
	elementList = elt;
	groupList = gr;
	selectedQueryVar = selectedVar;
	if (selectedQueryVar == null) {
	    selectedQueryVar = getMentionedQueryVar();
	}
    }

    /**
     * Create a CQuery with a set of CElement
     * 
     * @param elt
     * @param gr
     * @param selectedVar
     * @return
     */
    protected static CQuery createCQuery(List<CElement> elt,
	    List<ElementGroup> gr, List<Node> selectedVar) {

	if (elt == null) {
	    return null;
	}
	if (gr == null) {
	    gr = new ArrayList<ElementGroup>();
	}
	if (selectedVar == null) {
	    selectedVar = new ArrayList<Node>();
	}

	return new CQuery(elt, gr, selectedVar);
    }

    /**
     * @return the elementList
     */
    public List<CElement> getElementList() {
	return elementList;
    }

    /**
     * @return the groupList
     */
    public List<ElementGroup> getGroupList() {
	return groupList;
    }

    /**
     * @return the selectedQueryVar
     */
    public List<Node> getSelectedQueryVar() {
	return selectedQueryVar;
    }

    /**
     * @return the selectedQueryVarNames
     */
    public List<String> getSelectedQueryVarNames() {

	List<String> tempvarnames = new ArrayList<String>();

	for (int i = 0; i < this.getSelectedQueryVar().size(); i++) {
	    tempvarnames.add(getSelectedQueryVar().get(i).getName());
	}
	return tempvarnames;
    }

    /**
     * Return the list of the variables mentioned in the query
     * 
     * @return
     */
    public List<Node> getMentionedQueryVar() {

	List<Node> varnodes = new ArrayList<Node>();
	List<Node> tempvar = new ArrayList<Node>();

	for (int i = 0; i < this.getElementList().size(); i++) {
	    tempvar.addAll(this.getElementList().get(i).getMentionnedVar());
	    tempvar.retainAll(varnodes);
	    varnodes.removeAll(tempvar);
	    varnodes.addAll(this.getElementList().get(i).getMentionnedVar());
	    tempvar.clear();
	}

	return varnodes;
    }

    /**
     * Gives all the variables names use in the query
     * 
     * @return
     */
    public List<String> getMentionedQueryVarNames() {

	List<String> varnames = new ArrayList<String>();
	List<String> tempvarnames = new ArrayList<String>();

	for (int i = 0; i < this.getElementList().size(); i++) {
	    tempvarnames.addAll(this.getElementList().get(i)
		    .getMentionnedVarsNames());
	    tempvarnames.retainAll(varnames);
	    varnames.removeAll(tempvarnames);
	    varnames.addAll(this.getElementList().get(i)
		    .getMentionnedVarsNames());
	    tempvarnames.clear();
	}
	return varnames;
    }

    /**
     * true if the query is valid i.e there are at least one triple element and
     * all the filter element use variable present in at least one triplet
     * 
     * @return
     */
    public boolean isValidQuery() {

	boolean hasTriplePattern = false;
	List<Node> triplePattern = new ArrayList<Node>();
	List<Node> tempVarnames = new ArrayList<Node>();

	for (int i = 0; i < this.getElementList().size(); i++) {

	    if (this.getElementList().get(i).getElement() instanceof ElementPathBlock) {
		hasTriplePattern = true;
		tempVarnames.addAll(this.getElementList().get(i)
			.getMentionnedVar());

		tempVarnames.retainAll(triplePattern);
		triplePattern.removeAll(tempVarnames);
		triplePattern.addAll(this.getElementList().get(i)
			.getMentionnedVar());
		tempVarnames.clear();
	    }
	}

	if (!hasTriplePattern) {
	    return false;
	}

	boolean isValid = true;
	int i = 0;
	while ((i < this.getElementList().size()) && (isValid)) {

	    if (this.getElementList().get(i).getElement() instanceof ElementFilter) {
		isValid = isValid
			&& triplePattern.containsAll(this.getElementList()
				.get(i).getMentionnedVar());
	    }
	    i = i + 1;
	}

	return isValid;
    }

    /**
     * Return the corresponding SPARQL Query
     * 
     * @return
     */
    public Query getSPARQLQuery() {

	if (!isValidQuery()) {
	    return null;
	}

	Query tempQuery = new Query();
	ElementGroup elementGroup = new ElementGroup();

	for (CElement elt : elementList) {
	    elementGroup.addElement(elt.getElement());
	}

	tempQuery.setQueryPattern(elementGroup);
	tempQuery.setQuerySelectType();
	if (selectedQueryVar.size() == 0) {
	    tempQuery.setQueryResultStar(true);
	} else {
	    tempQuery.addProjectVars(selectedQueryVar);
	}
	return tempQuery;

    }

    /**
     * Return the corresponding SPARQL Query
     * 
     * @return
     */
    public Query getNativeSPARQLQuery() {

	if (!isValidQuery()) {
	    return null;
	}

	Query tempQuery = new Query();
	ElementGroup elementGroup = new ElementGroup();

	elementGroup.addElement(this.getGroupList().get(0));

	tempQuery.setQueryPattern(elementGroup);
	tempQuery.setQuerySelectType();
	if (selectedQueryVar.size() == 0) {
	    tempQuery.setQueryResultStar(true);
	} else {
	    tempQuery.addProjectVars(selectedQueryVar);
	}
	return tempQuery;

    }

}
