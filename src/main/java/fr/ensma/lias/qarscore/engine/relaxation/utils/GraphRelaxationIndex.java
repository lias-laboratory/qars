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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Geraud FOKOU
 */
public class GraphRelaxationIndex {

    private int[] element_index_limit;
    private int[] element_index;
    private int start_index = 0;
    private boolean with_redundance;

    public GraphRelaxationIndex(int[] index, int[] index_limit) {

	element_index = index;
	element_index_limit = index_limit;
	start_index = 0;
	with_redundance = false;
    }

    public GraphRelaxationIndex(int[] index, int[] index_limit,
	    boolean redundant) {

	element_index = index;
	element_index_limit = index_limit;
	start_index = 0;
	with_redundance = redundant;
    }

    public GraphRelaxationIndex(int[] index, int[] index_limit, int s_index) {

	element_index = index;
	element_index_limit = index_limit;
	start_index = s_index;
	with_redundance = false;
    }

    private GraphRelaxationIndex[] generate_distinct_child() {

	List<GraphRelaxationIndex> child_list = new ArrayList<GraphRelaxationIndex>();

	for (int i = start_index; i < element_index.length; i++) {
	    if (element_index[i] + 1 < element_index_limit[i]) {
		int[] child = new int[element_index.length];
		for (int j = 0; j < element_index.length; j++) {
		    child[j] = element_index[j];
		}
		child[i] = element_index[i] + 1;
		child_list.add(new GraphRelaxationIndex(child,
			element_index_limit, i));
	    }
	}

	GraphRelaxationIndex[] child_elt = new GraphRelaxationIndex[child_list
		.size()];
	for (int i = 0; i < child_list.size(); i++) {
	    child_elt[i] = child_list.get(i);
	}
	return child_elt;
    }

    private GraphRelaxationIndex[] generate_redundant_child() {

	List<GraphRelaxationIndex> child_list = new ArrayList<GraphRelaxationIndex>();
	for (int i = 0; i < element_index.length; i++) {
	    if (element_index[i] + 1 < element_index_limit[i]) {
		int[] child = new int[element_index.length];
		for (int j = 0; j < element_index.length; j++) {
		    child[j] = element_index[j];
		}
		child[i] = element_index[i] + 1;
		child_list.add(new GraphRelaxationIndex(child,
			element_index_limit, true));
	    }
	}

	GraphRelaxationIndex[] child_elt = new GraphRelaxationIndex[child_list
		.size()];
	for (int i = 0; i < child_list.size(); i++) {
	    child_elt[i] = child_list.get(i);
	}
	return child_elt;

    }

    /**
     * @return the element_index
     */
    public int[] getElement_index() {
	return element_index;
    }

    /**
     * @return the child_elt
     */
    public GraphRelaxationIndex[] getChild_elt() {

	if (this.with_redundance) {
	    return generate_redundant_child();
	} else {
	    return generate_distinct_child();
	}
    }
}
