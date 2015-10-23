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

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.util.URIUtil;
import org.openrdf.model.vocabulary.RDFS;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;

import fr.ensma.lias.qarscore.connection.implementation.SesameSession;
import fr.ensma.lias.qarscore.connection.statement.ModelStatement;

/**
 * @author Geraud FOKOU
 */
@SuppressWarnings("deprecation")
public class SesameModelStatement implements ModelStatement {

    SesameSession session = null;

    /**
     * Constructor
     */
    public SesameModelStatement(SesameSession s) {
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
		Model subModel;
		try {
		    subModel = session.getModel().filter(null, RDFS.SUBCLASSOF,
			    new URIImpl(currentNode.getURI()));
		    level = level - 1;
		    for (Resource subClass : subModel.subjects()) {
			Node relax_node = NodeFactory.createURI(subClass
				.stringValue());
			subNodes.put(relax_node, level);
			toFindSubClass.add(relax_node);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return subNodes;
    }

    @Override
    public Map<Node, Integer> getSubProperies(Node property) {

	Map<Node, Integer> subProperties = new HashMap<Node, Integer>();
	List<Node> toFindSubProperties = new ArrayList<Node>();
	toFindSubProperties.add(property);
	int level = 0;

	while (!toFindSubProperties.isEmpty()) {
	    Node currentPropertyNode = toFindSubProperties.remove(0);
	    if (property.isURI()) {
		Model subModel;
		try {
		    subModel = session.getModel().filter(null,
			    RDFS.SUBPROPERTYOF,
			    new URIImpl(currentPropertyNode.getURI()));
		    level = level - 1;
		    for (Resource subProperty : subModel.subjects()) {
			Node relax_property = NodeFactory.createURI(subProperty
				.stringValue());
			subProperties.put(relax_property, level);
			toFindSubProperties.add(relax_property);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return subProperties;

    }

    @Override
    public Map<Node, Integer> getSuperClasses(Node classe) {

	Map<Node, Integer> superClasses = new HashMap<Node, Integer>();
	List<Node> toFindSuperClass = new ArrayList<Node>();
	toFindSuperClass.add(classe);
	int level = 0;

	while (!toFindSuperClass.isEmpty()) {
	    Node currentNode = toFindSuperClass.remove(0);
	    if (classe.isURI()) {
		Model superModel;
		try {
		    superModel = session.getModel().filter(
			    new URIImpl(currentNode.getURI()), RDFS.SUBCLASSOF,
			    null);
		    level = level + 1;
		    for (Value superClass : superModel.objects()) {
			if (URIUtil.isValidURIReference(superClass
				.stringValue())) {
			    Node relax_node = NodeFactory.createURI(superClass
				    .stringValue());

			    superClasses.put(relax_node, level);
			    toFindSuperClass.add(relax_node);
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return superClasses;
    }

    @Override
    public Map<Node, Integer> getSuperProperty(Node property) {

	Map<Node, Integer> superProperties = new HashMap<Node, Integer>();
	List<Node> toFindSuperProperties = new ArrayList<Node>();
	toFindSuperProperties.add(property);
	int level = 0;

	while (!toFindSuperProperties.isEmpty()) {
	    Node currentPropertyNode = toFindSuperProperties.remove(0);
	    if (property.isURI()) {
		Model superModel;
		try {
		    superModel = session.getModel().filter(
			    new URIImpl(currentPropertyNode.getURI()),
			    RDFS.SUBPROPERTYOF, null);
		    level = level + 1;
		    for (Value superProperty : superModel.objects()) {
			Node relax_property = NodeFactory
				.createURI(superProperty.stringValue());
			superProperties.put(relax_property, level);
			toFindSuperProperties.add(relax_property);
		    }

		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	return superProperties;

    }

}
