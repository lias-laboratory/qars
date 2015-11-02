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
package fr.ensma.lias.qarscore.connection.statement.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;

import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.connection.statement.ModelStatement;

/**
 * @author Geraud FOKOU
 */
public class JenaModelStatement implements ModelStatement {

    /**
     * Session of the current model statement
     */
    JenaSession session = null;

    /**
     * Constructor
     */
    public JenaModelStatement(JenaSession s) {
	session = s;
    }

    @Override
    public Map<Node, Integer> getSubClasses(Node classeNode) {

	Map<Node, Integer> subNodes = new HashMap<Node, Integer>();
	List<Node> toFindSubClass = new ArrayList<Node>();
	toFindSubClass.add(classeNode);
	int level = 0;

	while (!toFindSubClass.isEmpty()) {
	    Node currentNode = toFindSubClass.remove(0);
	    if (currentNode.isURI()) {
		OntClass currentClass = session.getOntology().getOntClass(
			currentNode.getURI());
		if (currentClass != null) {
		    List<OntClass> subClasses = currentClass.listSubClasses(
			    true).toList();
		    level = level - 1;
		    for (OntClass subClass : subClasses) {
			if (subClass.isURIResource()) {
			    Node relax_node = NodeFactory.createURI(subClass
				    .getURI());
			    subNodes.put(relax_node, level);
			    toFindSubClass.add(relax_node);
			}
		    }
		}
	    }
	}
	return subNodes;
    }

    @Override
    public Map<Node, Integer> getSubProperies(Node property) {

	Map<Node, Integer> subPropertiesNodes = new HashMap<Node, Integer>();
	List<Node> toFindSubProperties = new ArrayList<Node>();
	toFindSubProperties.add(property);
	int level = 0;

	while (!toFindSubProperties.isEmpty()) {
	    Node currentPropertyNode = toFindSubProperties.remove(0);
	    if (currentPropertyNode.isURI()) {
		OntProperty curentProperty = session.getOntology()
			.getOntProperty(currentPropertyNode.getURI());

		if (curentProperty != null) {
		    List<? extends OntProperty> subProperties = curentProperty
			    .listSubProperties().toList();
		    level = level - 1;
		    for (OntProperty subProperty : subProperties) {
			if (subProperty.isURIResource()) {
			    Node relax_property = NodeFactory
				    .createURI(subProperty.getURI());
			    subPropertiesNodes.put(relax_property, level);
			    toFindSubProperties.add(relax_property);
			}
		    }
		}
	    }
	}
	return subPropertiesNodes;
    }

    @Override
    public Map<Node, Integer> getSuperClasses(Node classeNode) {

	Map<Node, Integer> superNodes = new HashMap<Node, Integer>();
	List<Node> toFindSuperClass = new ArrayList<Node>();
	toFindSuperClass.add(classeNode);
	int level = 0;

	while (!toFindSuperClass.isEmpty()) {
	    Node currentNode = toFindSuperClass.remove(0);
	    if (currentNode.isURI()) {
		OntClass currentClass = session.getOntology().getOntClass(
			currentNode.getURI());
		if (currentClass != null) {
		    List<OntClass> subClasses = currentClass.listSuperClasses(true).toList();
		    level = level + 1;
		    for (OntClass superClass : subClasses) {
			if (superClass.isURIResource()) {
			    Node relax_node = NodeFactory.createURI(superClass
				    .getURI());
			    superNodes.put(relax_node, level);
			    toFindSuperClass.add(relax_node);
			}
		    }
		}
	    }
	}
	return superNodes;
    }

    @Override
    public Map<Node, Integer> getSuperProperty(Node property) {

	Map<Node, Integer> superPropertiesNodes = new HashMap<Node, Integer>();
	List<Node> toFindSuperProperties = new ArrayList<Node>();
	toFindSuperProperties.add(property);
	int level = 0;

	while (!toFindSuperProperties.isEmpty()) {
	    Node currentPropertyNode = toFindSuperProperties.remove(0);
	    if (currentPropertyNode.isURI()) {
		OntProperty curentProperty = session.getOntology()
			.getOntProperty(currentPropertyNode.getURI());

		if (curentProperty != null) {
		    List<? extends OntProperty> superProperties = curentProperty.listSuperProperties(true).toList();
		    level = level + 1;
		    for (OntProperty superProperty : superProperties) {
			if (superProperty.isURIResource()) {
			    Node relax_property = NodeFactory
				    .createURI(superProperty.getURI());
			    superPropertiesNodes.put(relax_property, level);
			    toFindSuperProperties.add(relax_property);
			}
		    }
		}
	    }
	}
	return superPropertiesNodes;
    }
}
