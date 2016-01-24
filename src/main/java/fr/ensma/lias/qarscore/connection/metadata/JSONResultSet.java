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
package fr.ensma.lias.qarscore.connection.metadata;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Geraud FOKOU
 */
public class JSONResultSet {

    private String resultset ;
    private JSONObject head;
    private JSONObject results;
    private JSONArray vars;
    private JSONArray bindings;
    private JSONObject current_result;
    private int result_index = 0;

    public static JSONResultSet getJSONResultSet(String resultset) {

	JSONResultSet currentResultSet = new JSONResultSet();
	currentResultSet.resultset = resultset;
	JSONObject jsonObj = new JSONObject(resultset);
	if (jsonObj.has("head")) {
	    currentResultSet.head = jsonObj.getJSONObject("head");
	} else {
	    return null;
	}
	if (jsonObj.has("results")) {
	    currentResultSet.results = jsonObj.getJSONObject("results");
	} else {
	    return null;
	}
	if (currentResultSet.head.has("vars")) {
	    if (currentResultSet.head.get("vars") instanceof JSONArray) {
		currentResultSet.vars = currentResultSet.head
			.getJSONArray("vars");
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
	if (currentResultSet.results.has("bindings")) {
	    if (currentResultSet.results.get("bindings") instanceof JSONArray) {
		currentResultSet.bindings = currentResultSet.results
			.getJSONArray("bindings");
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
	
	return currentResultSet;
    }

    /**
     * 
     */
    private JSONResultSet() {
	result_index = -1;
	current_result = null;
    }

    /**
     * @return the head
     */
    public JSONObject getHead() {
	return head;
    }

    /**
     * @return the result
     */
    public JSONObject getResult() {
	return results;
    }

    /**
     * @return the vars
     */
    public JSONArray getVars() {
	return vars;
    }

    public String getVar(int i){
	
	if(i>= vars.length()){
	    return null;
	}
	return vars.getString(i);
    }
    
    /**
     * @return the bindings
     */
    public JSONArray getBindings() {
	return bindings;
    }

    /**
     * return current result as string
     * 
     * @param name
     * @return
     */
    public String getString(String name) {

	if (current_result == null) {
	    throw new NullPointerException("End Result set");
	}
	if (current_result.has(name)) {
	    return current_result.getJSONObject(name).getString("value");
	}

	return null;
    }

    /**
     * return current result as int
     * @param name
     * @return
     */
    public int getInt(String name) {

	if (current_result == null) {
	    throw new NullPointerException("End Result set");
	}
	return current_result.getJSONObject(name).getInt("value");
    }
    
    /**
     * return current result as boolean
     * @param name
     * @return
     */
    public boolean getBoolean(String name) {

	if (current_result == null) {
	    throw new NullPointerException("End Result set");
	}
	return current_result.getJSONObject(name).getBoolean("value");
    }
    
    /**
     * return current result as Double
     * @param name
     * @return
     */
    public double getDouble(String name) {

	if (current_result == null) {
	    throw new NullPointerException("End Result set");
	}
	return current_result.getJSONObject(name).getDouble("value");
    }

    /**
     * Check if it is the last result
     * @return
     */
    public boolean hasNext(){
	return result_index<bindings.length() - 1;
    }
    
    /**
     * go to the next result set
     * @return
     */
    public boolean next(){
	
	result_index = result_index + 1;
	if(result_index<bindings.length()){
	    current_result = bindings.getJSONObject(result_index);
	    return true;
	}
	return false;
    }

    /**
     * return size of the result set
     * @return
     */
    public int getSize(){
	return bindings.length();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return this.resultset;
    }
    
    /**
     * 
     * @param bindings2
     * @param binding
     * @return
     */
    public static boolean bindingsInclude(JSONArray bindings2,
	    JSONObject binding) {
	
	int i = 0;
	boolean found = false;
	while((i<bindings2.length())&&(!found)){
	    found = bindingsInclude(bindings2.getJSONObject(i), binding);
	    i = i +1;
	}
	return found;
    }

    /**
     * 
     * @param jsonObject
     * @param binding
     * @return
     */
    public static boolean bindingsInclude(JSONObject jsonObject,
	    JSONObject binding) {
	for(String key:jsonObject.keySet()){
	    if(binding.keySet().contains(key)){
		if(!jsonObject.getJSONObject(key).getString("value").equals(binding.getJSONObject(key).getString("value"))){
		    return false;
		}
	    }
	}
	return true;
    }    
}
