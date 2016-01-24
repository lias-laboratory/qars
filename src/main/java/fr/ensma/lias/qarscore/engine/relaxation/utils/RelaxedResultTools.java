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

import java.util.LinkedHashMap;

import org.json.JSONObject;

import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;

/**
 * @author Geraud FOKOU
 */
public class RelaxedResultTools {

    /**
     * Add a set of result in a map with their similarity without redundancy
     * @param result
     * @param newresult
     * @param sim
     * @param limit
     */
    public static void addResult(LinkedHashMap<String, Double> result,
	    JSONResultSet newresult, double sim, int limit) {
	if (newresult == null) {
	    return;
	}

	try {
	    int i = 0;
	    while ((i < newresult.getBindings().length())
		    && (result.size() < limit)) {
		JSONObject sol = newresult.getBindings().getJSONObject(i);
		String newsol = sol.toString();
		if(!result.containsKey(newsol)){
		    result.put(sol.toString(), Double.valueOf(sim));
		}
		i = i +1;
	    }
	} finally {
	}
    }
}
