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
package fr.ensma.lias.qarscore.engine.relaxation.utils;

import com.hp.hpl.jena.graph.Node;

/**
 * @author Geraud FOKOU
 */
public class NodeRelaxed {

    /**
     * The first relaxed node
     */
    private Node node_1;

    /**
     * The second relaxed node
     */
    private Node node_2;

    /**
     * The third relaxed node
     */
    private Node node_3;

    /**
     * The similarity of the relaxed node
     */
    private double similarity;

    /**
     * the level of the relaxation for the node
     */
    private int relaxation_level;

    /**
     * 
     * @param node_s
     * @param n_2
     * @param n_3
     * @param similarity
     * @param relaxation_level
     */
    public NodeRelaxed(Node node_s, Node node_p, Node node_o,
	    double similarity, int relaxation_level) {

	this.node_1 = node_s;
	this.node_2 = node_p;
	this.node_3 = node_o;
	this.similarity = similarity;
	this.relaxation_level = relaxation_level;
    }

    public NodeRelaxed(NodeRelaxed n_1) {

	this.node_1 = n_1.getNode_1();
	this.node_2 = n_1.getNode_2();
	this.node_3 = n_1.getNode_3();
	this.similarity = n_1.getSimilarity();
	this.relaxation_level = n_1.getRelaxation_level();
    }

    /**
     * 
     * @param n_1
     * @param n_2
     * @return
     */
    public static NodeRelaxed merge(NodeRelaxed n_1, NodeRelaxed n_2) {

	Node node_s;
	Node node_p;
	Node node_o;

	if (n_1.getNode_1() != null) {
	    if (n_2.getNode_1() != null) {
		throw new IllegalArgumentException("Incompatibles nodes");
	    } else {
		node_s = n_1.getNode_1();
	    }
	} else {
	    node_s = n_2.getNode_1();
	}

	if (n_1.getNode_2() != null) {
	    if (n_2.getNode_2() != null) {
		throw new IllegalArgumentException("Incompatibles nodes");
	    } else {
		node_p = n_1.getNode_2();
	    }
	} else {
	    node_p = n_2.getNode_2();
	}

	if (n_1.getNode_3() != null) {
	    if (n_2.getNode_3() != null) {
		throw new IllegalArgumentException("Incompatibles nodes");
	    } else {
		node_o = n_1.getNode_3();
	    }
	} else {
	    node_o = n_2.getNode_3();
	}

	return new NodeRelaxed(node_s, node_p, node_o, n_1.getSimilarity()
		+ n_2.getSimilarity(), n_1.getRelaxation_level()
		+ n_2.getRelaxation_level());
    }

    public static NodeRelaxed merge(NodeRelaxed n_1, NodeRelaxed n_2,
	    NodeRelaxed n_3) {

	Node node_s;
	Node node_p;
	Node node_o;

	if (n_1.getNode_1() != null) {
	    if ((n_2.getNode_1() != null) || (n_3.getNode_1() != null)) {
		throw new IllegalArgumentException("Incompatibles nodes");
	    } else {
		node_s = n_1.getNode_1();
	    }
	} else {
	    if (n_2.getNode_1() != null) {
		if (n_3.getNode_1() != null) {
		    throw new IllegalArgumentException("Incompatibles nodes");
		} else {
		    node_s = n_2.getNode_1();
		}
	    } else {
		node_s = n_3.getNode_1();
	    }
	}

	if (n_1.getNode_2() != null) {
	    if ((n_2.getNode_2() != null) || (n_3.getNode_2() != null)) {
		throw new IllegalArgumentException("Incompatibles nodes");
	    } else {
		node_p = n_1.getNode_2();
	    }
	} else {
	    if (n_2.getNode_2() != null) {
		if (n_3.getNode_2() != null) {
		    throw new IllegalArgumentException("Incompatibles nodes");
		} else {
		    node_p = n_2.getNode_2();
		}
	    } else {
		node_p = n_3.getNode_2();
	    }
	}

	if (n_1.getNode_3() != null) {
	    if ((n_2.getNode_3() != null) || (n_3.getNode_3() != null)) {
		throw new IllegalArgumentException("Incompatibles nodes");
	    } else {
		node_o = n_1.getNode_3();
	    }
	} else {
	    if (n_2.getNode_3() != null) {
		if (n_3.getNode_3() != null) {
		    throw new IllegalArgumentException("Incompatibles nodes");
		} else {
		    node_o = n_2.getNode_3();
		}
	    } else {
		node_o = n_3.getNode_3();
	    }
	}

	return new NodeRelaxed(node_s, node_p, node_o, n_1.getSimilarity()
		+ n_2.getSimilarity() + n_3.getSimilarity(),
		n_1.getRelaxation_level() + n_2.getRelaxation_level()
			+ n_3.getRelaxation_level());

    }

    /**
     * @return the node_1
     */
    public Node getNode_1() {
	return node_1;
    }

    /**
     * @return the node_2
     */
    public Node getNode_2() {
	return node_2;
    }

    /**
     * @return the node_3
     */
    public Node getNode_3() {
	return node_3;
    }

    /**
     * @return the similarity
     */
    public double getSimilarity() {
	return similarity;
    }

    /**
     * @return the relaxation_level
     */
    public int getRelaxation_level() {
	return relaxation_level;
    }

}
