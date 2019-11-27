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

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class RelaxationNodes {

	private CQuery relaxed_query;
	private double similarity_to_original;

	/**
	 * Constructor without parameter
	 */
	public RelaxationNodes() {
		relaxed_query = null;
		similarity_to_original = 0;
	}

	/**
	 * @param current_query
	 * @param parent_query
	 * @param similarity_to_parent
	 */
	public RelaxationNodes(CQuery relaxed_query, double similarity_to_source) {
		super();
		this.relaxed_query = relaxed_query;
		this.similarity_to_original = similarity_to_source;
	}

	/**
	 * @return the relaxed_query
	 */
	public CQuery getRelaxed_query() {
		return relaxed_query;
	}

	/**
	 * @return the similarity_to_original
	 */
	public double getSimilarity_to_original() {
		return similarity_to_original;
	}

}
