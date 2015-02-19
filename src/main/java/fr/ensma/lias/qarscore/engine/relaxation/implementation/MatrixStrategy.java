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
package fr.ensma.lias.qarscore.engine.relaxation.implementation;

import java.util.List;

import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationStrategies;

/**
 * @author Geraud FOKOU
 */
public class MatrixStrategy implements RelaxationStrategies {

    /**
     * 
     */
    private MatrixStrategy() {
	// TODO Auto-generated constructor stub
    }

    @Override
    public CQuery getAFailingCause(CQuery query) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean isAFailingCause(CQuery query) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public List<CQuery> getFailingCauses(CQuery query) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CQuery> getSuccessSubQueries() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean hasLeastKAnswers(CQuery query) {
	// TODO Auto-generated method stub
	return false;
    }

}
