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
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.E_LogicalAnd;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class CQueryFactory {

	/**
	 * List of Simple literal create
	 */
	private static List<CElement> elementList;

	/**
	 * List of group element
	 */
	private static List<ElementGroup> groupList;

	/**
	 * extract clause in an element of SPARQL query
	 * 
	 * @param element
	 */
	private static void getClause(Element element) {

		if (element instanceof ElementPathBlock) {
			List<TriplePath> triplePathList = ((ElementPathBlock) element).getPattern().getList();

			for (TriplePath triplePath : triplePathList) {
				ElementPathBlock currentTripleElt = new ElementPathBlock();
				currentTripleElt.addTriplePath(triplePath);
				elementList.add(CElement.createCTriple(currentTripleElt));
			}
		}

		else if (element instanceof ElementFilter) {

			Expr expression = ((ElementFilter) element).getExpr();
			getClause(expression);
		}

		else if (element instanceof ElementGroup) {

			for (Element elementInGroup : ((ElementGroup) element).getElements()) {
				if (elementInGroup instanceof ElementGroup) {
					groupList.add((ElementGroup) elementInGroup);
				} else {
					getClause(elementInGroup);
				}
			}
		}

		else {
			throw new NotYetImplementedException("This Element type don't support by the API");
		}
	}

	/**
	 * Extract clause in an expression of SPARQL query
	 * 
	 * @param expression
	 */
	private static void getClause(Expr expression) {
		if (expression instanceof E_LogicalAnd) {
			ElementFilter currentFilterElt = new ElementFilter(((E_LogicalAnd) expression).getArg1());
			elementList.add(CElement.createCTriple(currentFilterElt));

			currentFilterElt = new ElementFilter(((E_LogicalAnd) expression).getArg2());
			elementList.add(CElement.createCTriple(currentFilterElt));
		} else {
			ElementFilter currentFilterElt = new ElementFilter(expression);
			elementList.add(CElement.createCTriple(currentFilterElt));
		}
	}

	/**
	 * For a SPARQL Query query creates the corresponding CQuery
	 * 
	 * @param query
	 * @return
	 */
	public static CQuery createCQuery(Query query) {
		groupList = new ArrayList<ElementGroup>();
		elementList = new ArrayList<CElement>();
		groupList.add((ElementGroup) query.getQueryPattern());

		for (int i = 0; i < groupList.size(); i++) {
			getClause(groupList.get(i));
		}

		List<Node> selectedQueryVar = new ArrayList<Node>();
		selectedQueryVar.addAll(query.getProjectVars());

		return CQuery.createCQuery(elementList, groupList, selectedQueryVar);
	}

	/**
	 * Create a copy a CQuery
	 * 
	 * @param query
	 * @return
	 */
	public static CQuery cloneCQuery(CQuery query) {

		groupList = new ArrayList<ElementGroup>();
		elementList = new ArrayList<CElement>();

		for (CElement elt : query.getElementList()) {
			elementList.add(elt);
		}

		for (ElementGroup groupe : query.getGroupList()) {
			groupList.add(groupe);
		}

		List<Node> selectedQueryVar = new ArrayList<Node>();

		for (Node varNode : query.getSelectedQueryVar()) {
			selectedQueryVar.add(varNode);
		}

		return CQuery.createCQuery(elementList, groupList, selectedQueryVar);
	}

	/**
	 * Create a conjunctive query with a SPARQL string query
	 * 
	 * @param sparqlQuery
	 * @return
	 */
	public static CQuery createCQuery(String sparqlQuery) {
		return createCQuery(QueryFactory.create(sparqlQuery));
	}

	/**
	 * creates a start query with a list of elements
	 * 
	 * @param elements
	 * @return
	 */
	public static CQuery createCQuery(List<CElement> elements) {

		return CQuery.createCQuery(elements, null, null);
	}

	/**
	 * creates a start query with a list of elements with a precise list of selected
	 * var
	 * 
	 * @param elements
	 * @return
	 */
	public static CQuery createCQuery(List<CElement> elements, List<Node> selectedQueryVars) {

		return CQuery.createCQuery(elements, null, selectedQueryVars);
	}

}
