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

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationOperators;

/**
 * @author Geraud FOKOU
 */
public class RelaxationOperatorsImpl implements RelaxationOperators {

    private Session session;
    

    @Override
    public List<CQuery> generalize(CQuery query, Node classe, int level) {
	
	List<CQuery> relaxedQueries = new ArrayList<CQuery>();
	
	return relaxedQueries;
    }

    @Override
    public List<CQuery> generalize(CQuery query, Node classe) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CQuery> sibling(CQuery query, Node classe) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CQuery> relaxValue(CQuery query, Node value) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CQuery> releaseValue(CQuery query, Node value) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<CQuery> releaseJoin(CQuery query, Node variable) {
	// TODO Auto-generated method stub
	return null;
    }

}
