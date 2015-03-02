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

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * @author Geraud FOKOU
 */
public class JSONParser {

    /**
     * List of JSon nodes of the model parsed
     */
    private List<NodeJSON> listNodeJs;

    /**
     * List of JSon edges for properties of the model parsed
     */
    private List<EdgesJSON> listEdgesProperties;

    /**
     * List of Json edges for subclasses relation of the model parsed
     */
    private List<EdgesJSON> listEdgesSubclass;

    /**
     * Model to parse
     */
    private final OntModel MODEL_TO_PARSE;

    /**
     * @param model
     */
    public JSONParser(OntModel model) {
	super();
	MODEL_TO_PARSE = model;
	listNodeJs = new ArrayList<NodeJSON>();
	listEdgesProperties = new ArrayList<EdgesJSON>();
	listEdgesSubclass = new ArrayList<EdgesJSON>();
	this.parseOntClass();
	this.parseOntProperty();
	this.parseSubClassRelation();
    }

    /**
     * Parse all the class of the current ontology to node of JSON
     */
    private void parseOntClass() {

	ExtendedIterator<OntClass> listClass = MODEL_TO_PARSE
		.listNamedClasses();

	while (listClass.hasNext()) {
	    OntClass currentClass = listClass.next();

	    if (currentClass.getURI() != null) {
		NodeJSON nodejs = new NodeJSON(currentClass.getLocalName(),
			currentClass.getNameSpace(), currentClass.getURI(),
			currentClass.getLocalName());

		ExtendedIterator<DatatypeProperty> allProperties = MODEL_TO_PARSE
			.listDatatypeProperties();

		while (allProperties.hasNext()) {
		    DatatypeProperty currentProperty = allProperties.next();
		    if (currentProperty.getDomain() != null) {
			if (currentProperty.getDomain().getURI()
				.equalsIgnoreCase(currentClass.getURI())) {
			    if (currentProperty.getRange() == null) {
				nodejs.add(currentProperty.getLocalName(),
					"string");
			    } else {
				nodejs.add(currentProperty.getLocalName(),
					currentProperty.getRange()
						.getLocalName());
			    }
			}
		    }
		}
		listNodeJs.add(nodejs);
	    }
	}
	return;
    }

    /**
     * Parse all the object properties of the current ontology to Edge of JSON
     */
    private void parseOntProperty() {

	ExtendedIterator<ObjectProperty> allObjectProperties = MODEL_TO_PARSE
		.listObjectProperties();

	while (allObjectProperties.hasNext()) {

	    ObjectProperty currentProperty = allObjectProperties.next();

	    if ((currentProperty.getDomain() != null)
		    && (currentProperty.getRange() != null)) {
		NodeJSON sourceEdge = getNodeJSON(currentProperty.getDomain()
			.getURI());
		NodeJSON destinationEdge = getNodeJSON(currentProperty
			.getRange().getURI());
		if ((sourceEdge != null) && (destinationEdge != null)) {
		    EdgesJSON edge = new EdgesJSON(
			    currentProperty.getLocalName(),
			    currentProperty.getNameSpace(),
			    currentProperty.getURI(),
			    currentProperty.getLocalName(), "ObjectProperty",
			    sourceEdge, destinationEdge);
		    listEdgesProperties.add(edge);
		}
	    }
	}
	return;
    }

    /**
     * Parse all the subclasses relation to edges json relation
     */
    private void parseSubClassRelation() {

	for (NodeJSON currentNode : listNodeJs) {
	    OntClass currentClass = MODEL_TO_PARSE.getOntClass(currentNode
		    .getNodeIRI());
	    ExtendedIterator<OntClass> childClasses = currentClass
		    .listSubClasses(true);
	    while (childClasses.hasNext()) {
		OntClass currentChildClass = childClasses.next();
		NodeJSON currentChildNodeJson = getNodeJSON(currentChildClass
			.getURI());
		if (currentChildNodeJson != null) {
		    EdgesJSON edge = new EdgesJSON("SubClassOf",
			    "http://www.w3.org/2000/01/rdf-schema#",
			    "http://www.w3.org/2000/01/rdf-schema#subClassOf",
			    "SubClassOf", "SubClassOf", currentChildNodeJson,
			    currentNode);
		    listEdgesSubclass.add(edge);
		}
	    }
	}
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

    /**
     * @return the listEdgesSubclass
     */
    public List<EdgesJSON> getListEdgesSubclass() {
	return listEdgesSubclass;
    }

    /**
     * return the String representation of the JSON model for the specified
     * model
     * 
     * @return
     */
    public String getParser() {

	String resulJson = "{\"node\" : [";

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

	if (listEdgesSubclass.size() == 1) {
	    resulJson = resulJson + ", " + listEdgesSubclass.get(0).toString()
		    + "]";
	} else {
	    if (listEdgesSubclass.size() > 1) {
		resulJson = resulJson + ", "
			+ listEdgesSubclass.get(0).toString();
		for (int i = 1; i < listEdgesSubclass.size() - 1; i++) {
		    resulJson = resulJson + ", "
			    + listEdgesSubclass.get(i).toString();
		}
		resulJson = resulJson
			+ ", "
			+ listEdgesSubclass.get(listEdgesSubclass.size() - 1)
				.toString() + "]";
	    } else {
		resulJson = resulJson + "]";
	    }
	}

	resulJson = resulJson + "} ";

	return resulJson;
    }
}
