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

/**
 * @author Geraud FOKOU
 */
public class NodeJSON {

    /**
     * The node's name
     */
    private String nodeName;

    /**
     * The URI of the current Node
     */
    private String nodeNameSpace;

    /**
     * the ID of the current Node
     */
    private String nodeIRI;

    /**
     * The label of the current Node
     */
    private String nodeLabel;

    /**
     * List attributes of the current Nodes
     */
    private List<String> attributesNames;

    /**
     * List attributes's types in the same order of attribute's name
     */
    private List<String> attributesType;

    public NodeJSON(String name, String namespace, String iri, String label) {
	nodeName = name;
	nodeNameSpace = namespace;
	nodeIRI = iri;
	nodeLabel = label;
	attributesNames = new ArrayList<String>();
	attributesType = new ArrayList<String>();
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
	return nodeName;
    }

    /**
     * @return the nodeNameSpace
     */
    public String getNodeNameSpace() {
	return nodeNameSpace;
    }

    /**
     * @param nodeNameSpace
     *            the nodeNameSpace to set
     */
    public void setNodeNameSpace(String nodeNameSpace) {
	this.nodeNameSpace = nodeNameSpace;
    }

    /**
     * @return the nodeIRI
     */
    public String getNodeIRI() {
	return nodeIRI;
    }

    /**
     * @param nodeIRI
     *            the nodeIRI to set
     */
    public void setNodeIRI(String nodeIRI) {
	this.nodeIRI = nodeIRI;
    }

    /**
     * @return the nodeLabel
     */
    public String getNodeLabel() {
	return nodeLabel;
    }

    /**
     * @return the attributesNames
     */
    public List<String> getAttributesNames() {
	return attributesNames;
    }

    /**
     * @return the attributesType
     */
    public List<String> getAttributesType() {
	return attributesType;
    }

    /**
     * Add attribute name of type "type" as attribute of the current node
     * 
     * @param name
     * @param type
     */
    public void add(String name, String type) {
	attributesNames.add(name);
	attributesType.add(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

	String node = "{\"label\" : \"" + this.nodeLabel + "\",";
	node = node + "\"attributes\" : " + attributeToString();
	node = node + "}";

	return node;
    }

    private String attributeToString() {

	if (this.attributesNames.size() == 0) {
	    return "[]";
	}

	String attrib = "[{\"name\" : \"" + attributesNames.get(0)
		+ "\", \"type\" : \"" + attributesType.get(0) + "\"}";

	for (int i = 1; i < attributesNames.size() - 1; i++) {
	    attrib = attrib + ", {\"name\" : \"" + attributesNames.get(i)
		    + "\", \"type\" : \"" + attributesType.get(i) + "\"}";
	}

	attrib = attrib + ", {\"name\" : \""
		+ attributesNames.get(attributesNames.size() - 1)
		+ "\", \"type\" : \""
		+ attributesType.get(attributesNames.size() - 1) + "\"}]";

	return attrib;
    }
}
