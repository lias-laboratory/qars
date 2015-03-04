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
    private RelaxationTree originalQuery;
    private List<RelaxationTree> relaxedQuery;

    /**
     * 
     */
    public RelaxationTree(CQuery q, RelaxationTree parent, int sim) {
	query = q;
	originalQuery = parent;
	similarity = sim;
	relaxedQuery = new ArrayList<RelaxationTree>();
    }

    public RelaxationTree(CQuery q) {
	query = q;
	originalQuery = null;
	similarity = 0;
	relaxedQuery = new ArrayList<RelaxationTree>();
    }

    /**
     * @return the query
     */
    public CQuery getQuery() {
        return query;
    }

    /**
     * @param query the query to set
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
     * @param similarity the similarity to set
     */
    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    /**
     * @return the originalQuery
     */
    public RelaxationTree getOriginalQuery() {
        return originalQuery;
    }

    /**
     * @param originalQuery the originalQuery to set
     */
    public void setOriginalQuery(RelaxationTree originalQuery) {
        this.originalQuery = originalQuery;
    }

    /**
     * @return the relaxedQuery
     */
    public List<RelaxationTree> getRelaxedQuery() {
        return relaxedQuery;
    }

    /**
     * @param relaxedQuery the relaxedQuery to set
     */
    public void setRelaxedQuery(List<RelaxationTree> relaxedQuery) {
        this.relaxedQuery = relaxedQuery;
    }
}
