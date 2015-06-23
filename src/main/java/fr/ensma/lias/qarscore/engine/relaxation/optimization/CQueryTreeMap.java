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

import fr.ensma.lias.qarscore.engine.query.CQuery;


/**
 * @author Geraud FOKOU
 * This is a Binary Tree structure for indexing queries in LBA approach.
 * It is used like an optimization structure.
 */
@SuppressWarnings("hiding")
public class CQueryTreeMap <CQuery extends Comparable<CQuery> , Integer> {
    
        private Node root;             		// root of the Tertiary Search Tree Index (TSTI)

        private class Node {
            private CQuery key;           	// sorted by key , a CQuery
            private Integer val;         	// associated data, number of answer retrieving with this query 
            private Node sub, sup, incomparable;// subqueries tree, super queries tree and incomparable queries tree
            private int N;             		// number of nodes in subtree

            public Node(CQuery key, Integer val, int N) {
                this.key = key;
                this.val = val;
                this.N = N;
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
 	    this.root = new Node(queryRoot, value, 1);
 	}

        /**
 	 */
 	public CQueryTreeMap() {
 	    super();
 	    this.root = null;
 	}


        /***********************************************************************
         *  Size Properties of the TSTI
         ***********************************************************************/

        // return number of key-value pairs in TSTI rooted at x
        private int size(Node x) {
            if (x == null) return 0;
            else return x.N;
        }

        // return number of key-value pairs in TSTI
        public int size() {
            return size(root);
        }

        // is the symbol table empty?
        public boolean isEmpty() {
            return size() == 0;
        }

       /***********************************************************************
        *  Search TSTI for given key, and return associated value if found,
        *  return null if not found
        ***********************************************************************/
        public boolean contains(CQuery query) {
            return get(query) != null;
        }

        // return value associated with the given key, or null if no such key exists
        public Integer get(CQuery query) {
            return get(root, query);
        }

        private Integer get(Node x, CQuery key) {
            if (x == null) return null;
            int cmp = key.compareTo(x.key);
            if      (cmp == 0) return x.val;
            else if (cmp == 2) return get(x.incomparable, key);
            else if (cmp == 1) return get(x.sup, key);
            else if (cmp == -1) return get (x.sub, key);
            else              return null;
        }

    /***********************************************************************
        *  Insert key-value pair into TSTI
        *  If key already exists, update with new value
        ***********************************************************************/
        public void put(CQuery query, Integer numberAnswers) {
            if (numberAnswers == null) { 
        	//delete(key); 
        	return; 
            }
            root = put(root, query, numberAnswers);
            //assert check();
        }

        private Node put(Node x, CQuery key, Integer val) {
            if (x == null) return new Node(key, val, 1);
            int cmp = key.compareTo(x.key);
            if      (cmp == 0) x.val   = val;
            else if (cmp == 2) x.incomparable = put(x.incomparable, key, val);
            else if (cmp == 1) x.sup = put(x.sup, key, val);
            else if (cmp == -1)x.sub = put(x.sub, key, val);
            else              return x;
         
            //x.N = 1 + size(x.sub) + size(x.sup) +size(x.incomparable);
            x.N = x.N + 1;
            return x;
        }

       /***********************************************************************
        *  Delete Key in TSTI
        ***********************************************************************/
       /***********************************************************************
        *  Min, max, floor, and ceiling of a TSTI
        ***********************************************************************/
        public CQuery min() {
            if (isEmpty()) return null;
            return min(root).key;
        } 

        private Node min(Node x) { 
            if (x.sub == null) return x; 
            else               return min(x.sub); 
        } 

        public CQuery max() {
            if (isEmpty()) return null;
            return max(root).key;
        } 

        private Node max(Node x) { 
            if (x.sup == null) return x; 
            else               return max(x.sup); 
        } 

        public CQuery floor(CQuery key) {
            Node x = floor(root, key);
            if (x == null) return null;
            else return x.key;
        } 

        private Node floor(Node x, CQuery key) {
            if (x == null) return null;
            
            int cmp = key.compareTo(x.key);
            if      (cmp == 0) return x;
            else if (cmp == 2) return floor(x.incomparable, key);
            else if (cmp == 1) return floor(x.sup, key);
            else if (cmp == -1)return floor(x.sub, key);
            else              return null;
        } 

        public CQuery ceiling(CQuery key) {
            Node x = ceiling(root, key);
            if (x == null) return null;
            else return x.key;
        }

        private Node ceiling(Node x, CQuery key) {
            if (x == null) return null;
            int cmp = key.compareTo(x.key);
            if (cmp == 0) return x;
            if (cmp == 2) { 
                Node t = ceiling(x.incomparable, key); 
                if (t != null) return t;
                else return x; 
            } 
            if (cmp == 1) { 
                Node t = ceiling(x.sup, key); 
                if (t != null) return t;
                else return x; 
            } 
            if (cmp == -1) { 
                Node t = ceiling(x.sub, key); 
                if (t != null) return t;
                else return x; 
            } 
            return null; 
        } 

       /***********************************************************************
        *  Rank and selection
        ***********************************************************************/
       /***********************************************************************
        *  Range count and range search.
        ***********************************************************************/
      /*************************************************************************
        *  Check integrity of TSTI data structure
        *************************************************************************/
        public boolean check() {
            if (!isTSTI())            System.out.println("Not in symmetric order");
            if (!isSizeConsistent())  System.out.println("Subtree counts not consistent");
            return isTSTI() && isSizeConsistent() ;
        }

        // does this Tertiary tree satisfy query order?
        // Note: this test also ensures that data structure is a tertiary tree since order is strict
        private boolean isTSTI() {
            
            if(root==null) return true;
            return isTSTI(root);
        }

        private boolean isTSTI(Node x) {
            
            if(x==null) return true;
            
            if(x.sub!=null){
        	if(x.key.compareTo(x.sub.key)!=-1){
        	    return false;
        	}
            }
            if(x.sup!=null){
        	if(x.key.compareTo(x.sup.key)!= 1){
        	    return false;
        	}
            }
            if(x.incomparable!=null){
        	if(x.key.compareTo(x.incomparable.key)!= 2){
        	    return false;
        	}
            }
            
            return isTSTI(x.sub) && isTSTI(x.sup) && isTSTI(x.incomparable);
        } 

        // are the size fields correct?
        private boolean isSizeConsistent() { return isSizeConsistent(root); }
        private boolean isSizeConsistent(Node x) {
            if (x == null) return true;
            if (x.N != size(x.sub) + size(x.sup) +size(x.incomparable) + 1) return false;
            return isSizeConsistent(x.sub) && isSizeConsistent(x.sup) && isSizeConsistent(x.incomparable);
        }
}
