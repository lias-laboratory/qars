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

import java.io.InputStream;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;



/**
 * @author Geraud FOKOU
 */
public class JSONResultSet {

    private JsonObject jsonObj;
    private JsonObject head;
    private JsonObject results;
    private JsonArray vars;
    private JsonArray bindings;
    private JsonObject current_result;
    private int result_index = 0;

    public static JSONResultSet getJSONResultSet(String resultset) {

	JSONResultSet currentResultSet = new JSONResultSet();
	JsonObject current_jsonObj = JSON.parse(resultset);
	currentResultSet.jsonObj = current_jsonObj;
	if (current_jsonObj.hasKey("head")) {
	    currentResultSet.head = current_jsonObj.get("head").getAsObject();
	} else {
	    return null;
	}
	if (current_jsonObj.hasKey("results")) {
	    currentResultSet.results = current_jsonObj.get("results").getAsObject();
	} else {
	    return null;
	}
	if (currentResultSet.head.hasKey("vars")) {
	    if (currentResultSet.head.get("vars").isArray()) {
		currentResultSet.vars = currentResultSet.head.get("vars").getAsArray();
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
	if (currentResultSet.results.hasKey("bindings")) {
	    if (currentResultSet.results.get("bindings").isArray()) {
		currentResultSet.bindings = currentResultSet.results.get("bindings").getAsArray();
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
	
	return currentResultSet;
    }

    public static JSONResultSet getJSONResultSet(InputStream resultset) {
	
	JSONResultSet currentResultSet = new JSONResultSet();
	JsonObject current_jsonObj = JSON.parse(resultset);
	currentResultSet.jsonObj = current_jsonObj;
	if (current_jsonObj.hasKey("head")) {
	    currentResultSet.head = current_jsonObj.get("head").getAsObject();
	} else {
	    return null;
	}
	if (current_jsonObj.hasKey("results")) {
	    currentResultSet.results = current_jsonObj.get("results").getAsObject();
	} else {
	    return null;
	}
	if (currentResultSet.head.hasKey("vars")) {
	    if (currentResultSet.head.get("vars").isArray()) {
		currentResultSet.vars = currentResultSet.head.get("vars").getAsArray();
	    } else {
		return null;
	    }
	} else {
	    return null;
	}
	if (currentResultSet.results.hasKey("bindings")) {
	    if (currentResultSet.results.get("bindings").isArray()) {
		currentResultSet.bindings = currentResultSet.results.get("bindings").getAsArray();
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
    public JsonObject getHead() {
	return head;
    }

    /**
     * @return the result
     */
    public JsonObject getResult() {
	return results;
    }

    /**
     * @return the vars
     */
    public JsonArray getVars() {
	return vars;
    }

    public String getVar(int i){
	
	if(i>= vars.size()){
	    return null;
	}
	return vars.get(i).getAsString().value();
    }
    
    /**
     * @return the bindings
     */
    public JsonArray getBindings() {
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
	if (current_result.hasKey(name)) {
	    return current_result.get(name).getAsObject().get("value").toString();
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
	JsonValue value = current_result.get(name).getAsObject().get("value");
	int length = value.toString().length();
	return Integer.parseInt(value.toString().substring(1, length-1));
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
	JsonValue value = current_result.get(name).getAsObject().get("value");
	int length = value.toString().length();
	return Boolean.parseBoolean(value.toString().substring(1, length-1));
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
	JsonValue value = current_result.get(name).getAsObject().get("value");
	int length = value.toString().length();
	return Double.parseDouble(value.toString().substring(1, length-1));
    }

    /**
     * Check if it is the last result
     * @return
     */
    public boolean hasNext(){
	return result_index<bindings.size() - 1;
    }
    
    /**
     * go to the next result set
     * @return
     */
    public boolean next(){
	
	result_index = result_index + 1;
	if(result_index<bindings.size()){
	    current_result = bindings.get(result_index).getAsObject();
	    return true;
	}
	return false;
    }

    /**
     * return size of the result set
     * @return
     */
    public int getSize(){
	return bindings.size();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return jsonObj.toString();
    }
    
    /**
     * 
     * @param bindings2
     * @param binding
     * @return
     */
    public static boolean bindingsInclude(JsonArray bindings2,
	    JsonObject binding) {
	
	int i = 0;
	boolean found = false;
	while((i<bindings2.size())&&(!found)){
	    found = bindingsInclude(bindings2.get(i).getAsObject(), binding);
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
    public static boolean bindingsInclude(JsonObject jsonObject,
	    JsonObject binding) {
	for(String key:jsonObject.keySet()){
	    if(binding.keySet().contains(key)){
		if(!jsonObject.get(key).getAsObject().get("value").toString().equals(binding.get(key).getAsObject().get("value").toString())){
		    return false;
		}
	    }
	}
	return true;
    }    
}
