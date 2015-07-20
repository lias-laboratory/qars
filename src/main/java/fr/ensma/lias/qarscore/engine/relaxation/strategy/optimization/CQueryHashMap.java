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
package fr.ensma.lias.qarscore.engine.relaxation.strategy.optimization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 */
public class CQueryHashMap implements CQueryIndexMap{

    protected Logger logger = Logger.getRootLogger();
    public Map<String, Integer> mapIndex;
    
    /**
     * 
     */
    public CQueryHashMap() {
	mapIndex = new HashMap<String, Integer>(20);
    }

    @Override
    public boolean contains(CQuery query){
	
	return this.mapIndex.containsKey(query.getQueryLabel());
    }
    
    @Override
    public Integer get(CQuery query){
	
	return this.mapIndex.get(query.getQueryLabel());
    }

    @Override
    public void put(CQuery query, Integer numberAnswers) {
	
	this.mapIndex.put(query.getQueryLabel(), numberAnswers);
    }
    
    public Set<String> getKeySet(){
	
	return this.mapIndex.keySet();
    }

    @Override
    public Integer indexEvaluationQuery(CQuery query) {
	
	
	Integer numberAnswer = this.mapIndex.get(query.getQueryLabel());
	
	if(numberAnswer!=null){
	    logger.info("Execution of : "+query.getQueryLabel()+"                use equality relation");
	}
	
	return numberAnswer;
    }

    @Override
    public int size() {
	
	return this.mapIndex.size();
    }
}
