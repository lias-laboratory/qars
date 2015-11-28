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

import java.util.Map;

import org.apache.jena.rdf.model.Resource;

/**
 * @author Geraud FOKOU
 */
public class JenaMetaDataSet {

    /**
     * Number of instance by class
     */
    protected Map<Resource, Integer> instance_by_class;

    /**
     * Number of triple by property
     */
    protected Map<Resource, Integer> triple_by_property;

    /**
     * Number of instance in the dataset
     */
    protected int size_instance;

    /**
     * Number of triple in the dataset
     */
    protected int size_triple;

    
    /**
     * @return the instance_by_class
     */
    public Map<Resource, Integer> getInstance_by_class() {
        return instance_by_class;
    }


    /**
     * @param instance_by_class the instance_by_class to set
     */
    public void setInstance_by_class(Map<Resource, Integer> instance_by_class) {
        this.instance_by_class = instance_by_class;
    }


    /**
     * @return the triple_by_property
     */
    public Map<Resource, Integer> getTriple_by_property() {
        return triple_by_property;
    }


    /**
     * @param triple_by_property the triple_by_property to set
     */
    public void setTriple_by_property(Map<Resource, Integer> triple_by_property) {
        this.triple_by_property = triple_by_property;
    }


    /**
     * @return the size_instance
     */
    public int getSize_instance() {
        return size_instance;
    }


    /**
     * @param size_instance the size_instance to set
     */
    public void setSize_instance(int size_instance) {
        this.size_instance = size_instance;
    }


    /**
     * @return the size_triple
     */
    public int getSize_triple() {
        return size_triple;
    }


    /**
     * @param size_triple the size_triple to set
     */
    public void setSize_triple(int size_triple) {
        this.size_triple = size_triple;
    }


    /**
     * @param instance_by_class2
     * @param triple_by_property2
     * @param size_instance
     * @param size_triple
     */
    public JenaMetaDataSet(Map<Resource, Integer> instance_by_class2,
	    Map<Resource, Integer> triple_by_property2, int size_instance,
	    int size_triple) {
	super();
	this.instance_by_class = instance_by_class2;
	this.triple_by_property = triple_by_property2;
	this.size_instance = size_instance;
    }

    
}
