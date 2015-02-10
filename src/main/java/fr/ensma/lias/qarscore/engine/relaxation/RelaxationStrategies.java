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
package fr.ensma.lias.qarscore.engine.relaxation;

import java.util.List;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public interface RelaxationStrategies {
    
    /**
     * return a query failure cause
     * @return
     */
    public CQuery getAFailingCause(CQuery query);
    
    /**
     * Says if a CQuery is a MFS or not
     * @param query
     * @return
     */
    public boolean isAFailingCause(CQuery query);

    /**
     * Return the list of all the MFS of the CQuery query
     * @param query
     * @return
     */
    public List<CQuery> getFailingCauses(CQuery query);
    
    /**
     * Return all the maximal success subqueries of a CQuery query
     * @param query
     * @return
     */
    public List<CQuery> getSuccessSubQueries(CQuery query);
    
    /**
     * Says if the CQuery query has at least K answers in the dataset
     * @param query
     * @param k
     * @return
     */
    public boolean hasLeastKAnswers(CQuery query);
}
