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
import org.apache.jena.graph.Node_Variable;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class CElement {

    private final Long ELEMENT_INDEX;

    /**
     * give the number of triple clause make.
     */
    private static Long numberClause = (long) 1;

    /**
     * Label for represent clause
     */
    private final String label;

    /**
     * Pattern of clause
     */
    private Element element ;

    /**
     * list of variables in clause pattern
     */
    private List<Node> mentionnedVar = new ArrayList<Node>();

    /**
     * Private constructor
     */
    private CElement(Element currentElement) {

	ELEMENT_INDEX = CElement.numberClause++;

	if (currentElement instanceof ElementPathBlock) {

	    this.element = currentElement;

	    TriplePath currentClause = ((ElementPathBlock) element)
		    .getPattern().getList().get(0);

	    label = "t" + Long.toString(ELEMENT_INDEX);

	    if(currentClause.getSubject() instanceof Node_Variable){
		 mentionnedVar.add(currentClause.getSubject());
	    }
	    
//	    if (currentClause.getSubject().isVariable()) {
//		mentionnedVar.add(currentClause.getSubject());
//	    }
	    
	    if(currentClause.getPredicate() instanceof Node_Variable){
		 mentionnedVar.add(currentClause.getPredicate());
	    }

//	    if (currentClause.getPredicate() != null) {
//		if (currentClause.getPredicate().isVariable()) {
//		    mentionnedVar.add(currentClause.getPredicate());
//		}
//	    }
	    if(currentClause.getObject() instanceof Node_Variable){
		 mentionnedVar.add(currentClause.getObject());
	    }
//	    if (currentClause.getObject() != null) {
//		if (currentClause.getObject().isVariable()) {
//		    mentionnedVar.add(currentClause.getObject());
//		}
//	    }
	} else if (currentElement instanceof ElementFilter) {
	    label = "F" + Long.toString(CElement.numberClause++);
	    element = currentElement;

	    for (Var var : ((ElementFilter) currentElement).getExpr()
		    .getVarsMentioned()) {
		mentionnedVar.add(var);
	    }
	} else {
	    throw new NotYetImplementedException(
		    "This Element type don't support by the API");
	}
    }

    /**
     * Replace a node by an otherNode in a path block element
     * 
     * @param node
     * @param otherNode
     * @return
     */
    private TriplePath replaceInPathBlock(Node node, Node otherNode) {

	Node subjectNode;
	Node predicatNode;
	Node objectNode;
	boolean replacement = false;

	TriplePath currentClause = ((ElementPathBlock) element).getPattern()
		.getList().get(0);

	if (currentClause.getSubject().sameValueAs(node)) {
	    subjectNode = otherNode;
	    replacement = true;
	} else {
	    subjectNode = currentClause.getSubject();
	}
	if (currentClause.getPredicate() != null) {
	    if (currentClause.getPredicate().sameValueAs(node)) {
		predicatNode = otherNode;
		replacement = true;
	    } else {
		predicatNode = currentClause.getPredicate();
	    }
	} else {
	    predicatNode = null;
	}
	if (currentClause.getObject().sameValueAs(node)) {
	    objectNode = otherNode;
	    replacement = true;
	} else {
	    objectNode = currentClause.getSubject();
	}
	if (replacement) {
	    if (predicatNode == null) {
		return new TriplePath(subjectNode, currentClause.getPath(),
			objectNode);
	    } else {
		return new TriplePath(new Triple(subjectNode, predicatNode,
			objectNode));
	    }

	} else {
	    return currentClause;
	}
    }

    /**
     * Create a CTriple clause
     * 
     * @param triplet
     * @return
     */
    public static CElement createCTriple(Element currentElement) {
	return new CElement(currentElement);
    }

    /**
     * @return the element
     */
    public Element getElement() {
	return element;
    }

    /**
     * @return the label
     */
    public String getLabel() {
	return label;
    }

    /**
     * get all the variables mentioned in the Element
     * 
     * @return
     */
    public List<Node> getMentionnedVar() {

	List<Node> tempList = new ArrayList<Node>();
	for (int i = 0; i < mentionnedVar.size(); i++) {
	    tempList.add(mentionnedVar.get(i));
	}
	return tempList;
    }

    /**
     * Returns the variables names present in the element
     */
    public List<String> getMentionnedVarsNames() {

	List<String> tempList = new ArrayList<String>();
	for (int i = 0; i < mentionnedVar.size(); i++) {
	    tempList.add(mentionnedVar.get(i).getName());
	}
	return tempList;
    }

    /**
     * replace node by othernode in this element
     * 
     * @param node
     * @param otherNode
     * @return
     */
    public CElement replace(Node node, Node otherNode) {

	if (element instanceof ElementPathBlock) {

	    TriplePath newtriplePath = replaceInPathBlock(node, otherNode);
	    if (newtriplePath == ((ElementPathBlock) element).getPattern()
		    .getList().get(0)) {
		return this;
	    }
	    ElementPathBlock newPathBlock = new ElementPathBlock();
	    newPathBlock.addTriple(newtriplePath);

	    return new CElement(newPathBlock);
	}
	return null;
    }

    /**
     * replace the subject of an element by another node
     * 
     * @param otherNode
     * @return
     */
    public CElement replace_subject(Node otherNode) {

	TriplePath currentClause = ((ElementPathBlock) element).getPattern()
		.getList().get(0);

	TriplePath new_pattern;

	if (currentClause.getPredicate() != null) {
	    new_pattern = new TriplePath(new Triple(otherNode,
		    currentClause.getPredicate(), currentClause.getObject()));
	} else {
	    new_pattern = new TriplePath(otherNode, currentClause.getPath(),
		    currentClause.getObject());
	}

	ElementPathBlock newPathBlock = new ElementPathBlock();
	newPathBlock.addTriple(new_pattern);

	return new CElement(newPathBlock);
    }

    /**
     * replace the predicate of an element by another node
     * 
     * @param otherNode
     * @return
     */
    public CElement replace_predicat(Node otherNode) {

	TriplePath currentClause = ((ElementPathBlock) element).getPattern()
		.getList().get(0);

	TriplePath new_pattern;

	new_pattern = new TriplePath(new Triple(currentClause.getSubject(),
		otherNode, currentClause.getObject()));

	ElementPathBlock newPathBlock = new ElementPathBlock();
	newPathBlock.addTriple(new_pattern);

	return new CElement(newPathBlock);
    }

    /**
     * replace the object of an element by another node
     * 
     * @param otherNode
     * @return
     */
    public CElement replace_object(Node otherNode) {

	TriplePath currentClause = ((ElementPathBlock) element).getPattern()
		.getList().get(0);

	TriplePath new_pattern;

	if (currentClause.getPredicate() != null) {
	    new_pattern = new TriplePath(new Triple(currentClause.getSubject(),
		    currentClause.getPredicate(), otherNode));
	} else {
	    new_pattern = new TriplePath(currentClause.getSubject(),
		    currentClause.getPath(), otherNode);
	}

	ElementPathBlock newPathBlock = new ElementPathBlock();
	newPathBlock.addTriple(new_pattern);

	return new CElement(newPathBlock);
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

	CElement otherCElement = (CElement) obj;

	if (otherCElement.element == this.element)
	    return true;
	if(this.getLabel().equals(otherCElement.getLabel())){
	    return true;
	}
	if (this.element instanceof ElementPathBlock) {
	    List<TriplePath> currentTriple = ((ElementPathBlock) element)
		    .getPattern().getList();
	    if (otherCElement.element instanceof ElementPathBlock) {
		List<TriplePath> otherTriple = ((ElementPathBlock) otherCElement.element)
			.getPattern().getList();
		if (currentTriple.size() == otherTriple.size()) {
		    for (int i = 0; i < currentTriple.size(); i++) {
			int j = 0;
			boolean found = false;
			while ((!found) && (j < currentTriple.size())) {
			    found = currentTriple.get(i).equals(
				    otherTriple.get(j));
			    j++;
			}
			if (!found) {
			    return false;
			}
		    }
		    return true;
		}
		return false;
	    }
	    return false;
	}
	return false;
    }
    
    @Override
    public int hashCode() {
	return (int) (ELEMENT_INDEX * Long.hashCode(ELEMENT_INDEX));
    }
}
