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
package fr.ensma.lias.qarscore.engine.relaxation.implementation.utils;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class RelaxationTree {

    private CQuery query;
    private double similarity;
    private RelaxationTree source_query;
    private List<RelaxationTree> relaxed_query;

    /**
     * 
     */
    public RelaxationTree(CQuery q, RelaxationTree parent, double sim) {
	query = q;
	source_query = parent;
	similarity = sim;
	relaxed_query = new ArrayList<RelaxationTree>();
    }

    public RelaxationTree(CQuery q) {
	query = q;
	source_query = null;
	similarity = 1;
	relaxed_query = new ArrayList<RelaxationTree>();
    }

    /**
     * @return the query
     */
    public CQuery getQuery() {
	return query;
    }

    /**
     * @param query
     *            the query to set
     */
    public void setQuery(CQuery query) {
	this.query = query;
    }

    /**
     * @return the similarity
     */
    public double getSimilarity() {
	return similarity;
    }

    /**
     * @param similarity
     *            the similarity to set
     */
    public void setSimilarity(double similarity) {
	this.similarity = similarity;
    }

    /**
     * @return the source_query
     */
    public RelaxationTree getSource_query() {
	return source_query;
    }

    /**
     * @return the relaxed_query
     */
    public List<RelaxationTree> getRelaxed_query() {
	return relaxed_query;
    }

    /**
     * return all the relax queries without child
     * 
     * @return
     */
    public List<RelaxationTree> getLeaf() {

	List<RelaxationTree> leaf = new ArrayList<RelaxationTree>();
	if (relaxed_query.size() == 0) {
	    leaf.add(this);
	    return leaf;
	}

	List<RelaxationTree> temp_leaf;
	for (int i = 0; i < relaxed_query.size(); i++) {
	    temp_leaf = relaxed_query.get(i).getLeaf();

	    for (int j = 0; j < temp_leaf.size(); j++) {

		int position = 0;
		boolean found = false;
		while (!found) {
		    if (position == leaf.size()) {
			found = true;
		    } else {
			if (leaf.get(position).getSimilarity() <= temp_leaf
				.get(j).getSimilarity()) {
			    found = true;
			} else {
			    position++;
			}
		    }
		}

		leaf.add(position, temp_leaf.get(j));
	    }
	}
	return leaf;
    }

    /**
     * Add a new relaxed query in the list of relax queries of the current query
     * 
     * @param child
     * @return
     */
    public boolean add_child(RelaxationTree child) {

	if (this != child.getSource_query()) {
	    return false;
	}

	int position = 0;
	boolean found = false;
	while (!found) {
	    if (position == relaxed_query.size()) {
		found = true;
	    } else {
		if (relaxed_query.get(position).getSimilarity() <= child
			.getSimilarity()) {
		    found = true;
		} else {
		    position++;
		}
	    }
	}
	relaxed_query.add(position, child);
	return true;
    }

    /**
     * Return the node of the relaxation Tree which has q as value
     * 
     * @param query
     * @return
     */
    public RelaxationTree isInGraph(CQuery q) {

	if (query.equals(q)) {
	    return this;
	}
	for (int i = 0; i < relaxed_query.size(); i++) {
	    RelaxationTree temp = relaxed_query.get(i).isInGraph(q);
	    if (temp != null) {
		return temp;
	    }
	}
	return null;
    }
}
