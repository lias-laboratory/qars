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

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;

/**
 * @author Geraud FOKOU
 */
public class CQuery implements Comparable<CQuery> {

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
	    //tempvar.retainAll(varnodes);
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
	    //tempvarnames.retainAll(varnames);
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
     * Return true if the current query contains the element
     * @param element
     * @return
     */
    public boolean contain(CElement element){
	
	boolean found = false;
	int i = 0;
	while((!found)&&(i<this.elementList.size())) {
	    found = this.elementList.get(i).equals(element);
	    i++;
	}
	return found;
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
	    for(Node var:selectedQueryVar){
		tempQuery.addResultVar(var);
	    }
	   // tempQuery.addProjectVars(selectedQueryVar);
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
	if (this.getGroupList().size() == 0) {
	    return null;
	}

	tempQuery.setQueryPattern(this.getGroupList().get(0));
	tempQuery.setQuerySelectType();
	if (selectedQueryVar.size() == 0) {
	    tempQuery.setQueryResultStar(true);
	} else {
	    tempQuery.addProjectVars(selectedQueryVar);
	}
	return tempQuery;

    }

    /**
     * Says if the query is a star query or not
     * 
     * @return
     */
    public boolean isStarQuery() {

	boolean isFirst = true;
	Node central_var = null;

	for (CElement element : this.elementList) {

	    if (element.getElement() instanceof ElementPathBlock) {
		TriplePath currentClause = ((ElementPathBlock) element
			.getElement()).getPattern().getList().get(0);
		if (isFirst) {
		    if (currentClause.getSubject().isVariable()) {
			central_var = currentClause.getSubject();
			isFirst = false;
		    } else {
			return false;
		    }
		} else {
		    if (!currentClause.getSubject().sameValueAs(central_var)) {
			return false;
		    }
		}
	    }
	}
	return true;
    }

    /**
     * Says if the query is a chain query or not
     * 
     * @return
     */
    public boolean isChainQuery() {
	return false;
    }

    /**
     * Says if the query has a cartesian set
     * 
     * @return
     */
    public List<CQuery> getCartesianProduct() {

	List<CQuery> connexesCQueries = new ArrayList<CQuery>();
	
	List<List<CElement>> connexesComposantSet = new ArrayList<List<CElement>>();
	List<List<Node>> connexesComposantNodeSet = new ArrayList<List<Node>>();
	
	List<CElement> tempElementSet = new ArrayList<CElement>();
	tempElementSet.addAll(elementList);
	
//	List<CElement> cartesianSet = new ArrayList<CElement>();
//	List<Node> cartesianSetNode = new ArrayList<Node>();
	
//	connexesComposantSet.add(new ArrayList<CElement>());
//	connexesComposantNodeSet.add(new ArrayList<Node>());
	
//	connexesComposantSet.get(0).add(tempElementSet.get(0));
//	connexesComposantNodeSet.get(0).addAll(tempElementSet.get(0).getMentionnedVar());
//	
//	tempElementSet.remove(0);

	List<List<CElement>> linkedComposantSet = new ArrayList<List<CElement>>();
	List<List<Node>> linkedComposantNodeSet = new ArrayList<List<Node>>();
	List<Node> linkedNodeCandidates = new ArrayList<Node>();
	
	for(CElement currentElt:tempElementSet){
	    
	    linkedComposantSet.removeAll(linkedComposantSet);
	    linkedComposantNodeSet.removeAll(linkedComposantNodeSet);
	   
	    List<Node> currentNodeSet = currentElt.getMentionnedVar();
	    int k = 0;
	    
	    while(k<connexesComposantSet.size()){
		
		boolean isLinked = false;
		linkedNodeCandidates.removeAll(linkedNodeCandidates);
		linkedNodeCandidates.addAll(connexesComposantNodeSet.get(k));
		
		for(Node linkedNode:linkedNodeCandidates){
		    for(Node node:currentNodeSet){
			if(linkedNode.sameValueAs(node)){
			    linkedComposantSet.add(connexesComposantSet.get(k));
			    linkedComposantNodeSet.add(connexesComposantNodeSet.get(k));
			    connexesComposantNodeSet.remove(k);
			    connexesComposantSet.remove(k);
			    isLinked = true;
			    break;
			}
		    }
		    if(isLinked){
			break;
		    }
		}
		if(!isLinked){
		    k++;   
		}
	    }
	    
	    if(linkedComposantSet.size()==0){
		connexesComposantSet.add(new ArrayList<CElement>());
		connexesComposantNodeSet.add(new ArrayList<Node>());
		
		connexesComposantSet.get(connexesComposantSet.size()-1).add(currentElt);
		connexesComposantNodeSet.get(connexesComposantSet.size()-1).addAll(currentElt.getMentionnedVar());
	    }
	    else {		
		for (int index=1; index<linkedComposantSet.size(); index++){
		    linkedComposantNodeSet.get(0).removeAll(linkedComposantNodeSet.get(index));
		    linkedComposantNodeSet.get(0).addAll(linkedComposantNodeSet.get(index));
		    linkedComposantSet.get(0).addAll(linkedComposantSet.get(index));
		}
		linkedComposantNodeSet.get(0).removeAll(currentNodeSet);
		linkedComposantNodeSet.get(0).addAll(currentNodeSet);
		linkedComposantSet.get(0).add(currentElt);
		connexesComposantSet.add(linkedComposantSet.get(0));
		connexesComposantNodeSet.add(linkedComposantNodeSet.get(0));
	    }
	}
	
	for(List<CElement> connexeComposant:connexesComposantSet){
	    connexesCQueries.add(new CQuery(connexeComposant, null, null));
	}
	
	return connexesCQueries;
    }

    /**
     * Says if the query is a subquery of the query
     * 
     * @param query
     * @return
     */
    public boolean isSubQueryOf(CQuery query) {

	for (CElement element : this.getElementList()) {
	    boolean hasEquiv = false;

	    for (CElement equivElement : query.getElementList()) {
		if (equivElement.equals(element)) {
		    hasEquiv = true;
		    break;
		}
	    }

	    if (!hasEquiv) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Says if query is a subquery of the current query
     * 
     * @param query
     * @return
     */
    public boolean isSuperQueryOf(CQuery query) {

	return query.isSubQueryOf(this);
    }

    /**
     * Replace if exist the CElement elt by the otherElt
     * @param elt
     * @param otherElt
     * @return
     */
    public boolean replace(CElement elt, CElement otherElt){
	
	int index = 0;
	boolean found = false;
	while((index<this.getElementList().size())&&(!found)){
	    found = this.getElementList().get(index).equals(elt);
	    index++;
	}
	if(!found){
	    return false;
	}
	index --;
	this.getElementList().remove(index);
	this.getElementList().add(index, otherElt);
	
	return true;
    }
    
    /**
     * replace all the Node with value node by othernode
     * 
     * @param classe
     * @param otherNode
     * @return
     */
    public boolean replace(Node node, Node otherNode) {

	if (node.sameValueAs(otherNode)) {
	    return false;
	}

	boolean hasReplacement = false;
	List<CElement> tempListCElement = new ArrayList<CElement>();
	tempListCElement.addAll(elementList);
	elementList = new ArrayList<CElement>();

	for (CElement element : tempListCElement) {
	    CElement newElement = element.replace(node, otherNode);
	    if (newElement.equals(element)) {
		elementList.add(element);
	    } else {
		elementList.add(newElement);
		hasReplacement = true;
	    }
	}

	if (node.isVariable()) {
	    List<Node> tempListVarNode = new ArrayList<Node>();
	    tempListVarNode.addAll(this.selectedQueryVar);
	    selectedQueryVar = new ArrayList<Node>();

	    boolean found = false;
	    for (Node currentNode : tempListVarNode) {
		if (!currentNode.sameValueAs(node)) {
		    selectedQueryVar.add(currentNode);
		} else {
		    if (!found) {
			found = true;
			if (otherNode.isVariable()) {
			    selectedQueryVar.add(otherNode);
			}
		    }
		}
	    }
	} else {
	    if (otherNode.isVariable()) {
		selectedQueryVar.add(otherNode);
	    }
	}
	return hasReplacement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

	return getSPARQLQuery().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;

	CQuery otherQuery = (CQuery) obj;
	if (elementList.size() != otherQuery.getElementList().size()) {
	    return false;
	}
	for (CElement element : this.elementList) {
	    int j = 0;
	    boolean found = false;
	    while ((!found) && (j < otherQuery.elementList.size())) {
		found = element.equals(otherQuery.elementList.get(j));
		j++;
	    }
	    if (!found) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public int compareTo(CQuery otherQuery) {
	
	if (otherQuery == null)
	    return -2;

	if (this == otherQuery)
	    return 0;
	
	if(this.equals(otherQuery))
	    return 0;
		
	if(this.isSubQueryOf(otherQuery))
	    return -1;
	
	if(this.isSuperQueryOf(otherQuery))
	    return 1;

	return 2;

    }
    
    /*
     * 
     */
    public String getQueryLabel(){
	
	String label="";
	for(CElement elt:this.elementList){
	    label = label + elt.getLabel()+" ^ ";
	}
	label = label.substring(0, label.length()-3);
	
	return label;
    }
    
    @Override
    public int hashCode() {

	int code = 0;
	for(CElement elt:this.elementList){
	    code = code*elt.hashCode();
	}		
	return code;
    }
}
