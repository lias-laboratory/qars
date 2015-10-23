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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.optimization;

import org.apache.log4j.Logger;

import fr.ensma.lias.qarscore.engine.query.CQuery;

/**
 * @author Geraud FOKOU
 * This is a Tertiary Tree structure for indexing queries in LBA approach.
 * It is used like an optimization structure.
 */
public class CQueryTreeMap implements CQueryIndexMap {
    	
    	protected Logger logger = Logger.getRootLogger();
        private Node root;             		// root of the Tertiary Search Tree Index (TSTI)
      
        private class Node {
            private CQuery key;           	// sorted by key , a CQuery
            private Integer val;         	// associated data, number of answer retrieving with this query 
            private Node sub, sup, incomparable;// subqueries tree, super queries tree and incomparable queries tree

            public Node(CQuery key, Integer val) {
                this.key = key;
                this.val = val;
            }

	    /**
	     * @return the key
	     */
	    public CQuery getKey() {
	        return key;
	    }

	    /**
	     * @return the val
	     */
	    public Integer getVal() {
	        return val;
	    }

	    /**
	     * @param val the val to set
	     */
	    public void setVal(Integer val) {
	        this.val = val;
	    }

	    /**
	     * @return the sub
	     */
	    public Node getSub() {
	        return sub;
	    }

	    /**
	     * @return the sup
	     */
	    public Node getSup() {
	        return sup;
	    }

	    /**
	     * @return the incomparable
	     */
	    public Node getIncomparable() {
	        return incomparable;
	    }

	    /**
	     * @param sub the sub to set
	     */
	    public void setSub(Node sub) {
	        this.sub = sub;
	    }

	    /**
	     * @param sup the sup to set
	     */
	    public void setSup(Node sup) {
	        this.sup = sup;
	    }

	    /**
	     * @param incomparable the incomparable to set
	     */
	    public void setIncomparable(Node incomparable) {
	        this.incomparable = incomparable;
	    }            
        }

        /***********************************************************************
         *  Constructors of a TSTI
         ***********************************************************************/

        /**
 	 * @param queryRoot, value
 	 */
 	public CQueryTreeMap(CQuery queryRoot, Integer value) {
 	    super();
 	    this.root = new Node(queryRoot, value);
 	}

        /**
 	 */
 	public CQueryTreeMap() {
 	    super();
 	    this.root = null;
 	}

 	/**
 	 * Chechk if one query is in the tree
 	 * @param query
 	 * @return
 	 */
	@Override
	public boolean contains(CQuery query) {
	    
	    return contains(root, query);
	}

	/**
	 * Recursive methode for checking contains in the tree
	 * @param node
	 * @param query
	 * @return
	 */
	private boolean contains(Node node, CQuery query){
	    
	    if(node == null){
		return false;
	    }
	    
	    switch (query.compareTo(node.getKey())){
	    	case 0:  return true;
	    	case -1: return contains(node.getSub(), query);
	    	case 1: return contains(node.getSup(), query);
	    	case 2: return contains(node.getIncomparable(), query);
	    }
	    
	    return false;
	}
	
	/**
	 * Get the value (number of Answers) of a query 
	 * @param query
	 * @return
	 */
	@Override
	public Integer get(CQuery query) {
	    
	    return  get(root, query);
	}

	/**
	 * Recursive Get
	 * @param node
	 * @param query
	 * @return
	 */
	private Integer get(Node node, CQuery query) {
	  
	    if(node == null){
		return null;
	    }
	    
	    switch (query.compareTo(node.getKey())){
	    	case 0:  return node.getVal();
	    	case -1: return get(node.getSub(), query);
	    	case 1: return  get(node.getSup(), query);
	    	case 2: return  get(node.getIncomparable(), query);
	    }
	  
	    return null;
	}

	/**
	 * Put a new query in the tree
	 * @param query
	 * @param numberAnswers
	 */
	@Override
	public void put(CQuery query, Integer numberAnswers) {
	    
	    if(root==null){
		root = new Node(query, numberAnswers);
		return ;
	    }
	    
	    switch (query.compareTo(root.getKey())){
	    
	    	case 0:{  
	    	    root.setVal(numberAnswers);
	    	    return;
	    	}
	    	case -1:{
	    	    if(root.getSub()==null){
	    		root.setSub(new Node(query, numberAnswers));
	    	    }
	    	    else {
	    		put(root.getSub(), query, numberAnswers);
	    	    }
	    	    return ;
	    	}
	    	case 1: {
	    	    if(root.getSub()==null){
	    		root.setSup(new Node(query, numberAnswers));
	    	    }
	    	    else {
	    		put(root.getSup(), query, numberAnswers);
	    	    }
	    	    return;
	    	}
	    	case 2:{
	    	    if(root.getIncomparable()==null){
	    		root.setIncomparable(new Node(query, numberAnswers));
	    	    }
	    	    else {
	    		put (root.getIncomparable(), query, numberAnswers);
	    	    }
	    	    return ;
	    	}
	    }
	    
	    return;
	}

	private void put(Node node, CQuery query, Integer numberAnswers) {
	    
	    
	    switch (query.compareTo(node.getKey())){
	    
	    	case 0:{  
	    	    node.setVal(numberAnswers);
	    	    return;
	    	}
	    	case -1:{
	    	    if(node.getSub()==null){
	    		node.setSub(new Node(query, numberAnswers));
	    	    }
	    	    else {
	    		put(node.getSub(), query, numberAnswers);
	    	    }
	    	    return ;
	    	}
	    	case 1: {
	    	    if(node.getSub()==null){
	    		node.setSup(new Node(query, numberAnswers));
	    	    }
	    	    else {
	    		put(node.getSup(), query, numberAnswers);
	    	    }
	    	    return;
	    	}
	    	case 2:{
	    	    if(node.getIncomparable()==null){
	    		node.setIncomparable(new Node(query, numberAnswers));
	    	    }
	    	    else {
	    		put (node.getIncomparable(), query, numberAnswers);
	    	    }
	    	    return ;
	    	}
	    }
	    
	    return;

	}

	/**
	 * Direct evaluation of the query based on the index
	 * @param query
	 * @return
	 */
	@Override
	public Integer indexEvaluationQuery(CQuery query) {
	    
	    return indexEvaluationQuery(root, query);
	}

	private Integer indexEvaluationQuery(Node node, CQuery query) {
	   
	    if(node == null){
		return null;
	    }
	    
	    switch (query.compareTo(node.getKey())){
	    
	    	case 0:  {
	    	    logger.info("Execution of : "+query.getQueryLabel()+"                use equality relation");
	    	    return node.getVal();
	    	}
	    	case -1: {
	    	    if(node.getVal().intValue()!=0){
	    		logger.info("Execution of : "+query.getQueryLabel()+"                use subquery relation");
	    		return node.getVal();
	    	    }
	    	    return indexEvaluationQuery(node.getSub(), query);
	    	}
	    	case 1: {
	    	    if(node.getVal().intValue()==0){
	    		logger.info("Execution of : "+query.getQueryLabel()+"                use superquery relation");
	    		return node.getVal();
	    	    }
	    	    return  indexEvaluationQuery(node.getSup(), query);
	    	}
	    	case 2: return  indexEvaluationQuery(node.getIncomparable(), query);
	    }
	  
	    return null;

	}
	
	@Override
	public int size (){
	    return size(root);
	}

	private int size(Node node) {
	   
	    if(node==null){
		return 0;
	    }
	    return 1 + size(node.getSub())+size(node.sup)+size(node.incomparable);
	}
}
