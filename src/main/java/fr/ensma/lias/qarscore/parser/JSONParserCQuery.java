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
package fr.ensma.lias.qarscore.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class JSONParserCQuery {

    private List<NodeJSON> listNodeJs;

    private List<EdgesJSON> listEdgesProperties;

    private final CQuery query;

    public JSONParserCQuery(CQuery q) {
	super();
	query = q;
	listNodeJs = new ArrayList<NodeJSON>();
	listEdgesProperties = new ArrayList<EdgesJSON>();
	this.parse_query_node();
	this.parse_query_path();
    }

    private void parse_query_node() {

	for (CElement element : query.getElementList()) {
	    if (element.getElement() instanceof ElementPathBlock) {
		TriplePath current_element = ((ElementPathBlock) element
			.getElement()).getPattern().getList().get(0);
		addNodeJs(current_element.getSubject());
		addNodeJs(current_element.getObject());
	    }
	    // else todo
	}
    }

    private void addNodeJs(Node node) {

	if (node.isVariable()) {
	    if (getNodeJSON(node.getName()) == null) {
		NodeJSON nodejs = new NodeJSON(node.getName(), "", "",
			node.getName(), node.getName());
		listNodeJs.add(nodejs);
	    }
	} else {
	    if (node.isURI()) {
		if (getNodeJSON(node.getLocalName()) == null) {
		    NodeJSON nodejs = new NodeJSON(node.getLocalName(),
			    node.getNameSpace(), "", node.getURI(),
			    node.getLocalName());
		    listNodeJs.add(nodejs);
		}
	    } else {
		if (node.isLiteral()) {
		    if (getNodeJSON(node.getLiteral().toString()) == null) {
			NodeJSON nodejs = new NodeJSON(node.getLiteral()
				.toString(), "", "", node.getLiteral()
				.toString(), node.getLiteral().toString());
			listNodeJs.add(nodejs);
		    }
		} else {
		    if (node.isBlank()) {
			if (getNodeJSON(node.getBlankNodeLabel()) == null) {
			    NodeJSON nodejs = new NodeJSON(
				    node.getBlankNodeLabel(), "", "",
				    node.getBlankNodeLabel(),
				    node.getBlankNodeLabel());
			    listNodeJs.add(nodejs);
			}
		    }
		    // else todo
		}
	    }

	}
    }

    private void parse_query_path() {

	for (CElement element : query.getElementList()) {
	    if (element.getElement() instanceof ElementPathBlock) {
		TriplePath current_element = ((ElementPathBlock) element
			.getElement()).getPattern().getList().get(0);
		if (current_element.getPredicate() != null) {
		    EdgesJSON edge = new EdgesJSON(current_element
			    .getPredicate().getLocalName(), current_element
			    .getPredicate().getNameSpace(), "", current_element
			    .getPredicate().getURI(), current_element
			    .getPredicate().getLocalName(), "ObjectProperty");
		    NodeJSON source = this.getNodeJSON(current_element
			    .getSubject());
		    NodeJSON destination = this.getNodeJSON(current_element
			    .getObject());
		    edge.setEdgeSource(source);
		    edge.setEdgeDestination(destination);
		    listEdgesProperties.add(edge);
		} else {
		    EdgesJSON edge = new EdgesJSON(current_element.getPath()
			    .toString(), "", "", current_element.getPath()
			    .toString(), current_element.getPath().toString(),
			    "ObjectProperty");
		    NodeJSON source = this.getNodeJSON(current_element
			    .getSubject());
		    NodeJSON destination = this.getNodeJSON(current_element
			    .getObject());
		    edge.setEdgeSource(source);
		    edge.setEdgeDestination(destination);
		    listEdgesProperties.add(edge);
		}
	    }
	    // else todo
	}

	return;
    }

    /**
     * find a node JSon in the list with a specific URI uri
     * 
     * @param uri
     * @return
     */
    private NodeJSON getNodeJSON(String uri) {

	boolean found = false;
	int i = 0;

	while ((i < listNodeJs.size()) && (!found)) {
	    found = listNodeJs.get(i).getNodeIRI().equalsIgnoreCase(uri);
	    i = i + 1;
	}
	if (found) {
	    return listNodeJs.get(i - 1);
	}
	return null;
    }

    private NodeJSON getNodeJSON(Node node) {

	String name = "";
	if (node.isVariable()) {
	    name = node.getName();
	} else {
	    if (node.isURI()) {
		name = node.getURI();
	    } else {
		if (node.isLiteral()) {
		    name = node.getLiteral().toString();
		} else {
		    if (node.isBlank()) {
			name = node.getBlankNodeLabel();
		    }
		    // else todo
		}
	    }
	}
	return this.getNodeJSON(name);
    }

    /**
     * @return the listNodeJs
     */
    public List<NodeJSON> getListNodeJs() {
	return listNodeJs;
    }

    /**
     * @return the listEdgesProperties
     */
    public List<EdgesJSON> getListEdgesProperties() {
	return listEdgesProperties;
    }

    public String getParser() {

	String resulJson = "{\"nodes\" : [";

	if (listNodeJs.size() == 1) {
	    resulJson = resulJson + ", " + listNodeJs.get(0).toString() + "]";
	} else {
	    if (listNodeJs.size() > 1) {
		resulJson = resulJson + listNodeJs.get(0).toString();
		for (int i = 1; i < listNodeJs.size() - 1; i++) {
		    resulJson = resulJson + ", " + listNodeJs.get(i).toString();
		}
		resulJson = resulJson + ", "
			+ listNodeJs.get(listNodeJs.size() - 1).toString()
			+ "]";
	    } else {
		resulJson = resulJson + "]";
	    }
	}

	resulJson = resulJson + ", \"edges\" : [";

	if (listEdgesProperties.size() == 1) {
	    resulJson = resulJson + listEdgesProperties.get(0).toString();
	} else {
	    if (listEdgesProperties.size() > 1) {
		resulJson = resulJson + listEdgesProperties.get(0).toString();
		for (int i = 1; i < listEdgesProperties.size() - 1; i++) {
		    resulJson = resulJson + ", "
			    + listEdgesProperties.get(i).toString();
		}
		resulJson = resulJson
			+ ", "
			+ listEdgesProperties.get(
				listEdgesProperties.size() - 1).toString();
	    }
	}
	resulJson = resulJson + "]";
	resulJson = resulJson + "} ";

	return resulJson;
    }
}
