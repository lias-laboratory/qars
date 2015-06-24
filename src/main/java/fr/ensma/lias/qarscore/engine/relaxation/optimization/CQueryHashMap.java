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
package fr.ensma.lias.qarscore.engine.relaxation.optimization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class CQueryHashMap implements CQueryIndexMap{

    public static int number_of_included_query = 0;
    public static int number_of_unincluded_query = 0;

    private Map<CQuery, Integer> hashMapIndex;
    
    /**
     * 
     */
    public CQueryHashMap() {
	hashMapIndex = new HashMap<CQuery, Integer>();
    }

    @Override
    public boolean contains(CQuery query){
	
	return this.hashMapIndex.containsKey(query);
    }
    
    @Override
    public Integer get(CQuery query){
	
	return this.hashMapIndex.get(query);
    }

    @Override
    public void put(CQuery query, Integer numberAnswers) {
	number_of_included_query ++;
	this.hashMapIndex.put(query, numberAnswers);
    }
    
    public Set<CQuery> getKeySet(){
	
	return this.hashMapIndex.keySet();
    }

    @Override
    public Integer indexEvaluationQuery(CQuery query) {
	
	Integer numberAnswer = this.hashMapIndex.get(query);
	if(numberAnswer!=null){
	    number_of_unincluded_query ++;
	}
	return numberAnswer;
    }

    @Override
    public int size() {
	
	return this.hashMapIndex.size();
    }
}
