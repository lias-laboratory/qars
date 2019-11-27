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
package fr.ensma.lias.qarscore.engine.relaxation.operators;

import org.apache.jena.graph.Node;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;

/**
 * @author Geraud FOKOU
 */
public class Similarity {

	private Session session;

	/**
	 * 
	 */
	public Similarity(Session s) {
		session = s;
	}

	public double similarityMeasureClass(Node original_node, Node relaxed_node) {

		if (original_node.equals(relaxed_node)) {
			return 1.0;
		}

		String original_class = null;
		String relaxed_class = null;

		if (relaxed_node.isVariable()) {
			return 0.0;
		}

		if (original_node.isURI()) {
			original_class = original_node.getURI();
		}

		if (original_class == null) {
			return literalOrValueMeasure(original_node, relaxed_node);
		}

		if (relaxed_node.isURI()) {
			relaxed_class = relaxed_node.getURI();
		}

		return conceptMeasure(original_class, relaxed_class);
	}

	public double similarityMeasureProperty(Node original_node, Node relaxed_node) {

		if (original_node.equals(relaxed_node)) {
			return 1.0;
		}

		String original_property = null;
		String relaxed_property = null;

		if (relaxed_node.isVariable()) {
			return 0.0;
		}

		if (original_node.isURI()) {
			original_property = original_node.getURI();
		}

		if (original_property == null) {
			return literalOrValueMeasure(original_node, relaxed_node);
		}

		if (relaxed_node.isURI()) {
			relaxed_property = relaxed_node.getURI();
		}

		return propertyMeasure(original_property, relaxed_property);
	}

	public double conceptMeasure(String original_class, String relaxed_class) {

		double ic_class1 = session.getOntology().getIcClass(original_class);
		double ic_class2 = session.getOntology().getIcClass(relaxed_class);
		if (ic_class1 == 0) {
			return 0;
		}
		return ic_class2 / ic_class1;

	}

	public double propertyMeasure(String original_property, String relaxed_property) {

		double ic_prop1 = session.getOntology().getIcProperty(original_property);
		double ic_prop2 = session.getOntology().getIcProperty(relaxed_property);
		if (ic_prop1 == 0) {
			return 0;
		}
		return ic_prop2 / ic_prop1;

	}

	public double suppressValueMeasure() {

		return 0.0;
	}

	public double suppressTripleMeasure(CElement elt) {
		return elt.getMentionnedVar().size() / 3.0;
	}

	public double literalOrValueMeasure(Node original_node, Node relaxed_node) {
		return 0.0;
	}

}
