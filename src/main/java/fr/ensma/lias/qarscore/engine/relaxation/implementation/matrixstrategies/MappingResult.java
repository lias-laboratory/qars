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
package fr.ensma.lias.qarscore.engine.relaxation.implementation.matrixstrategies;

import java.util.Arrays;

/**
 * @author Geraud FOKOU
 */
public class MappingResult {

    /**
     * Entry of the result value in dictionary for each variable 0 if variable
     * has a null value for the specify mapping
     */
    private int[] variables;

    /**
     * current size of the mapping
     */
    private int size;

    /**
     * @param variables
     */
    public MappingResult(int[] variables) {
	super();
	this.variables = variables;
	this.size = variables.length;
    }

    /**
     * @return the variables
     */
    public int[] getVariables() {
	return variables;
    }

    /**
     * @param variables
     *            the variables to set
     */
    public void setVariables(int[] variables) {
	this.variables = variables;
	size = variables.length;
    }

    /**
     * Two value are compatible if there are same value or one is 0
     * 
     * @param e1
     * @param e2
     * @return
     */
    private boolean isCompatible(int e1, int e2) {

	boolean res = false;
	if ((e1 == 0) || (e2 == 0)) {
	    res = true;
	}
	if (e1 == e2) {
	    res = true;
	}
	return res;
    }

    /**
     * Check if two mapping are compatibles
     * 
     * @param otherMapping
     * @return
     */
    public boolean isCompatible(MappingResult otherMapping) {

	if (otherMapping.getVariables().length != size) {
	    return false;
	}

	for (int i = 0; i < size; i++) {
	    if (!isCompatible(variables[i], otherMapping.variables[i]))
		return false;
	}
	return true;
    }

    /**
     * Creates a new mapping which is the merging of the current mapping and a
     * second mapping otherMapping. If the two mappings are'nt compatibles
     * return null.
     * 
     * @param otherMapping
     * @return
     */
    public MappingResult union(MappingResult otherMapping) {

	if (!this.isCompatible(otherMapping)) {
	    return null;
	}

	int[] res = new int[size];
	for (int i = 0; i < size; i++) {
	    res[i] = variables[i];
	    int val = otherMapping.variables[i];
	    if (val != 0) {
		res[i] = val;
	    }
	}
	return new MappingResult(res);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	MappingResult other = (MappingResult) obj;
	if (size != other.size)
	    return false;
	if (!Arrays.equals(variables, other.variables))
	    return false;
	return true;
    }
    
    @Override
    public String toString(){
	
	String result="";
	
	for(int i=0; i<size; i++){
	    result = result + variables[i]+" ";
	}
	return result;
    }
}
