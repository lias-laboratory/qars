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
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class CElement {

    /**
     * give the number of triple clause make.
     */
    private static int numberClause = 1;

    /**
     * Label for represent clause
     */
    private final String label;

    /**
     * Pattern of clause
     */
    private Element element = null;

    /**
     * list of variables in clause pattern
     */
    private List<Node> mentionnedVar = new ArrayList<Node>();

    /**
     * Private constructor
     * 
     * @throws NotYetImplementedException
     */
    private CElement(Element currentElement) throws NotYetImplementedException {

	super();
	TriplePath currentClause;

	if (currentElement instanceof ElementPathBlock) {

	    this.element = currentElement;

	    currentClause = ((ElementPathBlock) element).getPattern().getList()
		    .get(0);
	    label = "T" + Integer.toString(CElement.numberClause++);

	    if (currentClause.getSubject().isVariable()) {
		mentionnedVar.add(currentClause.getSubject());
	    }
	    if (currentClause.getPredicate() != null) {
		if (currentClause.getPredicate().isVariable()) {
		    mentionnedVar.add(currentClause.getPredicate());
		}
	    }
	    if (currentClause.getObject().isVariable()) {
		mentionnedVar.add(currentClause.getObject());
	    }
	} else if (currentElement instanceof ElementFilter) {
	    label = "F" + Integer.toString(CElement.numberClause++);
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
     * Create a CTriple clause
     * 
     * @param triplet
     * @return
     * @throws NotYetImplementedException
     */
    protected static CElement createCTriple(Element currentElement)
	    throws NotYetImplementedException {
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
}
