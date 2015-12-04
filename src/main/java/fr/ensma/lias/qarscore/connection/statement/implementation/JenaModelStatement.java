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

	return null;
    }

    @Override
    public Map<Node, Integer> getSubProperies(Node property) {

	return null;
    }

    @Override
    public Map<Node, Integer> getSuperClasses(Node classeNode) {

	Map<Node, Integer> superNodes = new HashMap<Node, Integer>();
	List<String> toFindSuperClass = new ArrayList<String>();
	List<String> alreadyInserted = new ArrayList<String>();
	if (classeNode.isURI()) {
	    toFindSuperClass.add(classeNode.getURI());
	}
	int level = 0;

	while (!toFindSuperClass.isEmpty()) {
	    String currentURI = toFindSuperClass.remove(0);
	    String supClasses = session.getStat_Lubm_data().getSuperClass(
		    currentURI);
	    level = level + 1;
	    if (supClasses != null) {
		if (!alreadyInserted.contains(supClasses)) {
		    Node relax_node = NodeFactory.createURI(supClasses);
		    superNodes.put(relax_node, level);
		    alreadyInserted.add(relax_node.getURI());
		    toFindSuperClass.add(relax_node.getURI());
		}
	    }
	}
	return superNodes;
    }

    @Override
    public Map<Node, Integer> getSuperProperty(Node property) {

	Map<Node, Integer> superPropertiesNodes = new HashMap<Node, Integer>();
	List<String> toFindSuperProperties = new ArrayList<String>();
	List<String> alreadyInserted = new ArrayList<String>();
	if (property.isURI()) {
	    toFindSuperProperties.add(property.getURI());
	    alreadyInserted.add(property.getURI());
	}

	int level = 0;

	while (!toFindSuperProperties.isEmpty()) {
	    String currentPropertyURI = toFindSuperProperties.remove(0);
	    String superProperties = session.getStat_Lubm_data()
		    .getSuperProperty(currentPropertyURI);

	    level = level + 1;
	    if (superProperties != null) {
		if (!alreadyInserted.contains(superProperties)) {
		    Node relax_property = NodeFactory
			    .createURI(superProperties);
		    superPropertiesNodes.put(relax_property, level);
		    alreadyInserted.add(relax_property.getURI());
		    toFindSuperProperties.add(relax_property.getURI());
		}
	    }
	}
	return superPropertiesNodes;
    }
}
