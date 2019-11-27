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

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class RelaxationTree {

	private CQuery current_query;
	private double similarity_to_parent;
	private RelaxationTree parent_query;
	private List<RelaxationTree> child_relaxed_query;

	/**
	 * 
	 */
	public RelaxationTree(CQuery q, RelaxationTree parent, double sim) {
		current_query = q;
		parent_query = parent;
		similarity_to_parent = sim;
		child_relaxed_query = new ArrayList<RelaxationTree>();
	}

	public RelaxationTree(CQuery q) {
		current_query = q;
		parent_query = null;
		similarity_to_parent = 1;
		child_relaxed_query = new ArrayList<RelaxationTree>();
	}

	/**
	 * @return the query
	 */
	public CQuery getQuery() {
		return current_query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(CQuery query) {
		this.current_query = query;
	}

	/**
	 * @return the similarity
	 */
	public double getSimilarity() {
		return similarity_to_parent;
	}

	/**
	 * @param similarity the similarity to set
	 */
	public void setSimilarity(double similarity) {
		this.similarity_to_parent = similarity;
	}

	/**
	 * @return the source_query
	 */
	public RelaxationTree getSource_query() {
		return parent_query;
	}

	/**
	 * @return the relaxed_query
	 */
	public List<RelaxationTree> getRelaxed_query() {
		return child_relaxed_query;
	}

	/**
	 * return all the relax queries without child
	 * 
	 * @return
	 */
	public List<RelaxationTree> getLeaf() {

		List<RelaxationTree> leaf = new ArrayList<RelaxationTree>();
		if (child_relaxed_query.size() == 0) {
			leaf.add(this);
			return leaf;
		}

		List<RelaxationTree> temp_leaf;
		for (int i = 0; i < child_relaxed_query.size(); i++) {
			temp_leaf = child_relaxed_query.get(i).getLeaf();

			for (int j = 0; j < temp_leaf.size(); j++) {

				int position = 0;
				boolean found = false;
				while (!found) {
					if (position == leaf.size()) {
						found = true;
					} else {
						if (leaf.get(position).getSimilarity() <= temp_leaf.get(j).getSimilarity()) {
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
			if (position == child_relaxed_query.size()) {
				found = true;
			} else {
				if (child_relaxed_query.get(position).getSimilarity() <= child.getSimilarity()) {
					found = true;
				} else {
					position++;
				}
			}
		}
		child_relaxed_query.add(position, child);
		return true;
	}

	/**
	 * Return the node of the relaxation Tree which has q as value
	 * 
	 * @param current_query
	 * @return
	 */
	public RelaxationTree isInGraph(CQuery q) {

		if (current_query.equals(q)) {
			return this;
		}
		for (int i = 0; i < child_relaxed_query.size(); i++) {
			RelaxationTree temp = child_relaxed_query.get(i).isInGraph(q);
			if (temp != null) {
				return temp;
			}
		}
		return null;
	}
}
