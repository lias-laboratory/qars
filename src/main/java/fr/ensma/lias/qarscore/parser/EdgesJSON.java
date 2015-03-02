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

/**
 * @author Geraud FOKOU
 */
public class EdgesJSON {

    /**
     * The node's name
     */
    private String edgeName;

    /**
     * The URI of the current Node
     */
    private String edgeNameSpace;

    /**
     * the ID of the current Node
     */
    private String edgeIRI;

    /**
     * The label of the current Node
     */
    private String edgeLabel;

    /**
     * Type of this Edge (Datatype or Object Type)
     */
    private String edgeType;

    /**
     * Source node of the edge (Domain)
     */
    private NodeJSON edgeSource;

    /**
     * Destination node of the edge (Range)
     */
    private NodeJSON edgeDestination;

    /**
     * @param edgeName
     * @param edgeNameSpace
     * @param edgeIRI
     * @param edgeLabel
     * @param edgeType
     */
    public EdgesJSON(String edgeName, String edgeNameSpace, String edgeIRI,
	    String edgeLabel, String edgeType) {
	super();
	this.edgeName = edgeName;
	this.edgeNameSpace = edgeNameSpace;
	this.edgeIRI = edgeIRI;
	this.edgeLabel = edgeLabel;
	this.edgeType = edgeType;
    }

    /**
     * @param edgeName
     * @param edgeNameSpace
     * @param edgeIRI
     * @param edgeLabel
     * @param edgeType
     * @param edgeSource
     * @param edgeDestination
     */
    public EdgesJSON(String edgeName, String edgeNameSpace, String edgeIRI,
	    String edgeLabel, String edgeType, NodeJSON edgeSource,
	    NodeJSON edgeDestination) {
	super();
	this.edgeName = edgeName;
	this.edgeNameSpace = edgeNameSpace;
	this.edgeIRI = edgeIRI;
	this.edgeLabel = edgeLabel;
	this.edgeType = edgeType;
	this.edgeSource = edgeSource;
	this.edgeDestination = edgeDestination;
    }

    /**
     * @return the edgeName
     */
    public String getEdgeName() {
	return edgeName;
    }

    /**
     * @param edgeName
     *            the edgeName to set
     */
    public void setEdgeName(String edgeName) {
	this.edgeName = edgeName;
    }

    /**
     * @return the edgeNameSpace
     */
    public String getEdgeNameSpace() {
	return edgeNameSpace;
    }

    /**
     * @param edgeNameSpace
     *            the edgeNameSpace to set
     */
    public void setEdgeNameSpace(String edgeNameSpace) {
	this.edgeNameSpace = edgeNameSpace;
    }

    /**
     * @return the edgeIRI
     */
    public String getEdgeIRI() {
	return edgeIRI;
    }

    /**
     * @param edgeIRI
     *            the edgeIRI to set
     */
    public void setEdgeIRI(String edgeIRI) {
	this.edgeIRI = edgeIRI;
    }

    /**
     * @return the edgeLabel
     */
    public String getEdgeLabel() {
	return edgeLabel;
    }

    /**
     * @param edgeLabel
     *            the edgeLabel to set
     */
    public void setEdgeLabel(String edgeLabel) {
	this.edgeLabel = edgeLabel;
    }

    /**
     * @return the edgeType
     */
    public String getEdgeType() {
	return edgeType;
    }

    /**
     * @param edgeType
     *            the edgeType to set
     */
    public void setEdgeType(String edgeType) {
	this.edgeType = edgeType;
    }

    /**
     * @return the edgeSource
     */
    public NodeJSON getEdgeSource() {
	return edgeSource;
    }

    /**
     * @param edgeSource
     *            the edgeSource to set
     */
    public void setEdgeSource(NodeJSON edgeSource) {
	this.edgeSource = edgeSource;
    }

    /**
     * @return the edgeDestination
     */
    public NodeJSON getEdgeDestination() {
	return edgeDestination;
    }

    /**
     * @param edgeDestination
     *            the edgeDestination to set
     */
    public void setEdgeDestination(NodeJSON edgeDestination) {
	this.edgeDestination = edgeDestination;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

	String edge = "{";
	if ((edgeDestination == null) || (edgeSource == null)) {
	    edge = edge + "}";
	    return edge;
	}
	edge = edge + "\"name\" : \"" + edgeLabel + "\",";
	edge = edge + "\"from\" : \"" + edgeSource.getNodeLabel() + "\",";
	edge = edge + "\"to\" : \"" + edgeDestination.getNodeLabel() + "\",";

	if (edgeType.equalsIgnoreCase("ObjectProperty")) {
	    edge = edge + "\"type\" : \"relation\"}";
	} else {
	    if (edgeType.equalsIgnoreCase("SubClassOf")) {
		edge = edge + "\"type\" : \"inheritance\"}";
	    } else {
		edge = edge + "\"type\" : " + edgeType + "}";
	    }
	}
	return edge;
    }
}
