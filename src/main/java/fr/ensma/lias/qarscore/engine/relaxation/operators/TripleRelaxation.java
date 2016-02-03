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
package fr.ensma.lias.qarscore.engine.relaxation.operators;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.relaxation.utils.HelperRelax;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;
import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class TripleRelaxation {

    public static final int LEVEL_ORDER = 0;

    public static int SIM_ORDER = 1;

    public static int HYBRID_ORDER = 2;

    public static int SUPRESS_NODE_LEVEL = -1;

    private int relaxation_order;

    /**
     * Current data session
     */
    private Session session = null;

    /**
     * Original subject if it is a variable
     */
    private Node subject_var = null;

    /**
     * Original object if it is a variable
     */
    private Node object_var = null;

    /**
     * Original predicat if it is a variable
     */
    private Node predicat_var = null;

    /**
     * Subject's relaxation
     */
    private List<NodeRelaxed> relaxed_subject = null;

    /**
     * Predicate's relaxation
     */
    private List<NodeRelaxed> relaxed_predicat = null;

    /**
     * Object's relaxation
     */
    private List<NodeRelaxed> relaxed_object = null;

    /**
     * triple relaxation
     */
    private List<NodeRelaxed> relaxed_triple = null;

    /**
     * Element to relax
     */
    private CElement current_clause = null;

    /**
     * Relaxation operators
     */
    private RelaxationOperators model_operators ;
    
    /**
     * Similarity measure for compute distance between semantics concepts
     */
    private Similarity sim_measure;
    
    /**
     * index of the current triple relaxed
     */
    private int current_elt = -1;

    /**
     * Relaxation of one node
     * 
     * @param original_node
     * @return
     */
    private Map<Node, Integer> relax_node(Node original_node) {
	
	Map<Node, Integer> relaxed_node = new LinkedHashMap<Node, Integer>();

	if (original_node.isURI()) {
	    relaxed_node.put(original_node, 0);
	    relaxed_node.putAll(model_operators.getSuperClasses(original_node));
	    Node var_node = NodeFactory.createVariable(HelperRelax.getNewResource());
	    relaxed_node.put(var_node, SUPRESS_NODE_LEVEL);
	} else if (original_node.isLiteral()) {
	    relaxed_node.put(original_node, 0);
	    // predicat relaxation levensteing or Hausdorff
	    Node var_node = NodeFactory.createVariable(HelperRelax.getNewResource());
	    relaxed_node.put(var_node, SUPRESS_NODE_LEVEL);
	}

	else if (original_node.isConcrete()) {
	    relaxed_node.put(original_node, 0);
	    // release relaxation
	    Node var_node = NodeFactory.createVariable(HelperRelax.getNewResource());
	    relaxed_node.put(var_node, SUPRESS_NODE_LEVEL);

	} else {
	    // variables relaxation (join)
	}

	return relaxed_node;
    }

    /**
     * 
     * @param original_node
     * @return
     */
    private Map<Node, Integer> relax_predicat(Node original_node) {

	Map<Node, Integer> relaxed_node = new LinkedHashMap<Node, Integer>();

	if (original_node.isURI()) {
	    relaxed_node.put(original_node, 0);
	    relaxed_node
		    .putAll(model_operators.getSuperProperty(original_node));
	    Node var_node = NodeFactory
		    .createVariable(HelperRelax.getNewPredicat());
	    relaxed_node.put(var_node, SUPRESS_NODE_LEVEL);
	} else if (original_node.isLiteral()) {
	    relaxed_node.put(original_node, 0);
	    // predicat relaxation
	    Node var_node = NodeFactory
		    .createVariable(HelperRelax.getNewPredicat());
	    relaxed_node.put(var_node, SUPRESS_NODE_LEVEL);
	}

	else if (original_node.isConcrete()) {
	    relaxed_node.put(original_node, 0);
	    // release relaxation
	    Node var_node = NodeFactory
		    .createVariable(HelperRelax.getNewPredicat());
	    relaxed_node.put(var_node, SUPRESS_NODE_LEVEL);
	} else {
	    // variables relaxation (join)
	}

	return relaxed_node;
    }

    private void set_relaxation_order(int order) {

	if ((0 <= order) && (order <= 2)) {
	    this.relaxation_order = order;
	} else {
	    throw new NotYetImplementedException(
		    "This order not yet implemented");
	}
    }

    private void filterRelaxation(Expr expr) {
	// TODO Auto-generated method stub
    }

    /**
     * relaxation of one triple path
     */
    private void tripleRelaxation(TriplePath clause) {

	if (clause.getSubject().isConcrete()) {
	    relaxed_subject = new ArrayList<NodeRelaxed>();

	    Map<Node, Integer> subject_relaxation = this.relax_node(clause
		    .getSubject());
	    for (Node relaxed_node : subject_relaxation.keySet()) {
		double sim = sim_measure.similarityMeasureClass(
			clause.getSubject(), relaxed_node) / 3.0;
		relaxed_subject.add(new NodeRelaxed(relaxed_node, null, null,
			sim, new int[] { subject_relaxation.get(relaxed_node),
				-2, -2 }));
	    }
	} else {
	    // join relaxation
	    subject_var = clause.getSubject();
	}

	if (clause.getObject().isConcrete()) {
	    relaxed_object = new ArrayList<NodeRelaxed>();

	    Map<Node, Integer> object_relaxation = this.relax_node(clause
		    .getObject());
	    for (Node relaxed_node : object_relaxation.keySet()) {
		double sim = sim_measure.similarityMeasureClass(clause.getObject(),
			relaxed_node) / 3.0;
		relaxed_object.add(new NodeRelaxed(null, null, relaxed_node,
			sim, new int[] { -2, -2,
				object_relaxation.get(relaxed_node) }));
	    }
	} else {
	    // join relaxation
	    object_var = clause.getObject();
	}

	if (clause.getPredicate() != null) {
	    if (clause.getPredicate().isConcrete()) {
		relaxed_predicat = new ArrayList<NodeRelaxed>();

		Map<Node, Integer> predicat_relaxation = this
			.relax_predicat(clause.getPredicate());
		for (Node relaxed_node : predicat_relaxation.keySet()) {
		    double sim = sim_measure.similarityMeasureProperty(
			    clause.getPredicate(), relaxed_node) / 3.0;
		    relaxed_predicat
			    .add(new NodeRelaxed(null, relaxed_node, null, sim,
				    new int[] {
					    -2,
					    predicat_relaxation
						    .get(relaxed_node), -2 }));
		}
	    } else {
		// join relaxation
		predicat_var = clause.getPredicate();
	    }
	}

	if (clause.getPath() != null) {
	    // path relaxation
	}

	this.allTripleRelaxation();
    }

    /**
     * Generation of all possible triple relaxation
     */
    private void allTripleRelaxation() {

	relaxed_triple = new ArrayList<NodeRelaxed>();

	if (this.relaxed_subject != null) {
	    if (this.relaxed_predicat != null) {
		NodeRelaxed object = new NodeRelaxed(null, null, object_var,
			1.0 / 3, new int[] { -2, -2, 0 });
		for (NodeRelaxed relax_s : relaxed_subject) {
		    for (NodeRelaxed relax_p : relaxed_predicat) {
			this.add_node_relaxation(NodeRelaxed.merge(relax_s,
				relax_p, object));
			// relaxed_triple.add(NodeRelaxed.merge(relax_s,
			// relax_p,
			// object));
		    }
		}
	    } else {
		if (this.relaxed_object != null) {
		    NodeRelaxed predicat = new NodeRelaxed(null, predicat_var,
			    null, 1.0 / 3, new int[] { -2, 0, -2 });
		    for (NodeRelaxed relax_s : relaxed_subject) {
			for (NodeRelaxed relax_o : relaxed_object) {
			    this.add_node_relaxation(NodeRelaxed.merge(relax_s,
				    predicat, relax_o));
			    // relaxed_triple.add(NodeRelaxed.merge(relax_s,
			    // predicat, relax_o));
			}
		    }
		} else {
		    NodeRelaxed object = new NodeRelaxed(null, null,
			    object_var, 1.0 / 3, new int[] { -2, -2, 0 });
		    NodeRelaxed predicat = new NodeRelaxed(null, predicat_var,
			    null, 1.0 / 3, new int[] { -2, 0, -2 });
		    for (NodeRelaxed relax_s : relaxed_subject) {
			this.add_node_relaxation(NodeRelaxed.merge(relax_s,
				predicat, object));
			// relaxed_triple.add(NodeRelaxed.merge(relax_s,
			// predicat,
			// object));
		    }
		}
	    }
	} else {
	    NodeRelaxed subject = new NodeRelaxed(subject_var, null, null,
		    1.0 / 3, new int[] { 0, -2, -2 });
	    if (this.relaxed_predicat != null) {
		if (this.relaxed_object != null) {
		    for (NodeRelaxed relax_p : relaxed_predicat) {
			for (NodeRelaxed relax_o : relaxed_object) {
			    this.add_node_relaxation(NodeRelaxed.merge(subject,
				    relax_p, relax_o));
			    // relaxed_triple.add(NodeRelaxed.merge(subject,
			    // relax_p, relax_o));
			}
		    }
		} else {
		    NodeRelaxed object = new NodeRelaxed(null, null,
			    object_var, 1.0 / 3, new int[] { -2, -2, 0 });
		    for (NodeRelaxed relax_p : relaxed_predicat) {
			this.add_node_relaxation(NodeRelaxed.merge(subject,
				relax_p, object));
			// relaxed_triple.add(NodeRelaxed.merge(subject,
			// relax_p,
			// object));
		    }
		}
	    } else {
		NodeRelaxed predicat = new NodeRelaxed(null, predicat_var,
			null, 1.0 / 3, new int[] { -2, 0, -2 });
		if (this.relaxed_object != null) {
		    for (NodeRelaxed relax_o : relaxed_object) {
			this.add_node_relaxation(NodeRelaxed.merge(subject,
				predicat, relax_o));
			// relaxed_triple.add(NodeRelaxed.merge(subject,
			// predicat,
			// relax_o));
		    }
		} else {
		    throw new IllegalArgumentException("Unrelaxable triple");
		}
	    }
	}
	this.current_elt = -1;
    }

    /**
     * Constructor of TripleRelaxation with one query element path
     */
    public TripleRelaxation(CElement clause, Session s) {

	session = s;
	this.current_clause = clause;
	this.model_operators = new RelaxationOperators(session);
	sim_measure =  new Similarity(session);
	this.set_relaxation_order(1);

	if (clause.getLabel().startsWith("t")) {
	    TriplePath currentClause = ((ElementPathBlock) clause.getElement())
		    .getPattern().getList().get(0);
	    this.tripleRelaxation(currentClause);
	} else {

	    filterRelaxation(((ElementFilter) clause.getElement()).getExpr());
	}
    }

    /**
     * Constructor of TripleRelaxation with one query element path
     */
    public TripleRelaxation(CElement clause, Session s, int order) {

	session = s;
	this.current_clause = clause;
	this.model_operators = new RelaxationOperators(session);
	sim_measure =  new Similarity(session);
	this.set_relaxation_order(order);

	if (clause.getLabel().startsWith("t")) {
	    TriplePath currentClause = ((ElementPathBlock) clause.getElement())
		    .getPattern().getList().get(0);
	    this.tripleRelaxation(currentClause);
	} else {

	    filterRelaxation(((ElementFilter) clause.getElement()).getExpr());
	}
    }

    private void add_node_relaxation(NodeRelaxed node) {

	int i = 0;
	boolean inserted = false;

	if (this.relaxation_order == TripleRelaxation.LEVEL_ORDER) {
	    while ((i < this.relaxed_triple.size()) && (!inserted)) {
		// if (relaxed_triple.get(i).getRelaxation_level() <= node
		// .getRelaxation_level()) {
		// i = i + 1;
		// } else {
		// relaxed_triple.add(i, node);
		// inserted = true;
		// }
	    }
	    if (!inserted) {
		relaxed_triple.add(node);
	    }
	} else if (this.relaxation_order == TripleRelaxation.SIM_ORDER) {
	    i = this.relaxed_triple.size() - 1;
	    while ((0 <= i) && (!inserted)) {
		if (relaxed_triple.get(i).getSimilarity() < node
			.getSimilarity()) {
		    i = i - 1;
		} else {
		    relaxed_triple.add(i + 1, node);
		    inserted = true;
		}
	    }
	    if (!inserted) {
		relaxed_triple.add(0, node);
	    }
	} else if (this.relaxation_order == TripleRelaxation.HYBRID_ORDER) {

	    while ((i < this.relaxed_triple.size()) && (!inserted)) {
		/*
		 * if (relaxed_triple.get(i).getRelaxation_level() < node
		 * .getRelaxation_level()) { i = i + 1; } else
		 */
		if (relaxed_triple.get(i).getRelaxation_level() == node
			.getRelaxation_level()) {
		    while ((i < this.relaxed_triple.size()) && (!inserted)) {
			if (relaxed_triple.get(i).getSimilarity() >= node
				.getSimilarity()) {
			    i = i + 1;
			} else {
			    relaxed_triple.add(i, node);
			    inserted = true;
			}
		    }
		    if (!inserted) {
			relaxed_triple.add(node);
		    }

		} else {
		    relaxed_triple.add(i, node);
		    inserted = true;
		}
	    }
	    if (!inserted) {
		relaxed_triple.add(node);
	    }

	} else {

	}
    }

    /**
     * @return the relaxed_triple
     */
    public List<NodeRelaxed> getRelaxed_triple() {
	return relaxed_triple;
    }

    /**
     * @return the subject_var
     */
    public Node getSubject_var() {
	return subject_var;
    }

    /**
     * @return the object_var
     */
    public Node getObject_var() {
	return object_var;
    }

    /**
     * @return the predicat_var
     */
    public Node getPredicat_var() {
	return predicat_var;
    }

    /**
     * @return the relaxed_subject
     */
    public List<NodeRelaxed> getRelaxed_subject() {
	return relaxed_subject;
    }

    /**
     * @return the relaxed_predicat
     */
    public List<NodeRelaxed> getRelaxed_predicat() {
	return relaxed_predicat;
    }

    /**
     * @return the relaxed_object
     */
    public List<NodeRelaxed> getRelaxed_object() {
	return relaxed_object;
    }

    public NodeRelaxed next_relaxed_triple() {

	this.current_elt = this.current_elt + 1;

	if (this.current_elt >= this.relaxed_triple.size()) {
	    return null;
	}
	return this.relaxed_triple.get(current_elt);
    }

    public CElement next_relaxed_element_triple() {

	this.current_elt = this.current_elt + 1;

	if (this.current_elt >= this.relaxed_triple.size()) {
	    return null;
	}
	CElement relax_element = CElement.createCTriple(this.current_clause
		.getElement());
	relax_element = relax_element.replace_subject(relaxed_triple.get(
		current_elt).getNode_1());
	relax_element = relax_element.replace_predicat(relaxed_triple.get(
		current_elt).getNode_2());
	relax_element = relax_element.replace_object(relaxed_triple.get(
		current_elt).getNode_3());

	return relax_element;
    }

    public CElement getCurrentRelaxedCElement() {

	if (this.current_elt >= this.relaxed_triple.size()) {
	    return null;
	}
	CElement relax_element = CElement.createCTriple(this.current_clause
		.getElement());
	relax_element = relax_element.replace_subject(relaxed_triple.get(
		current_elt).getNode_1());
	relax_element = relax_element.replace_predicat(relaxed_triple.get(
		current_elt).getNode_2());
	relax_element = relax_element.replace_object(relaxed_triple.get(
		current_elt).getNode_3());

	return relax_element;
    }

    public NodeRelaxed getCurrentRelaxedTriple() {

	if (this.current_elt >= this.relaxed_triple.size()) {
	    return null;
	}
	return this.relaxed_triple.get(current_elt);
    }

    public boolean hasNext() {

	return this.current_elt + 1 < this.relaxed_triple.size();
    }

    public void moveToFirst() {

	this.current_elt = -1;
    }

    public void moveToLast() {

	this.current_elt = this.relaxed_triple.size() - 2;
    }

    public void moveToIndex(int index) {

	if (index >= relaxed_triple.size()) {
	    this.moveToLast();
	}
	current_elt = index - 1;
    }

    public NodeRelaxed[] get_relaxed_node_list() {

	NodeRelaxed[] current_elt = new NodeRelaxed[this.relaxed_triple.size()];

	for (int i = 0; i < this.relaxed_triple.size(); i++) {
	    current_elt[i] = relaxed_triple.get(i);
	}
	return current_elt;
    }

    public CElement[] get_relaxed_elt_list() {

	CElement[] current_elt = new CElement[this.relaxed_triple.size()];

	for (int i = 0; i < this.relaxed_triple.size(); i++) {

	    CElement relax_element = CElement.createCTriple(this.current_clause
		    .getElement());
	    relax_element = relax_element.replace_subject(relaxed_triple.get(i)
		    .getNode_1());
	    relax_element = relax_element.replace_predicat(relaxed_triple
		    .get(i).getNode_2());
	    relax_element = relax_element.replace_object(relaxed_triple.get(i)
		    .getNode_3());

	    current_elt[i] = relax_element;
	}
	return current_elt;
    }

    public static boolean is_relaxation(CElement relax_elt,
	    CElement father_elt, Session s) {

	TriplePath relax_clause = ((ElementPathBlock) relax_elt.getElement())
		.getPattern().getList().get(0);

	TriplePath father_clause = ((ElementPathBlock) father_elt.getElement())
		.getPattern().getList().get(0);

	if (!is_relaxation(relax_clause.getSubject(),
		father_clause.getSubject(), s, 1)) {
	    return false;
	}
	if ((father_clause.getPredicate() != null)
		&& (relax_clause.getPredicate() != null)) {
	    if (!is_relaxation(relax_clause.getPredicate(),
		    father_clause.getPredicate(), s, 2)) {
		return false;
	    }
	    return is_relaxation(relax_clause.getObject(),
		    father_clause.getObject(), s, 1);
	}

	if (father_clause.getPath() != null) {
	    if (father_clause.getPath().equals(relax_clause.getPath())) {
		return is_relaxation(relax_clause.getObject(),
			father_clause.getObject(), s, 1);
	    } else {
		// Relaxation of SPARQL Path queries
		return false;
	    }
	}

	return false;
    }

    public static boolean is_relaxation(Node subject, Node subject2, Session s,
	    int type) {

	if (subject2.isVariable()) {
	    if (subject.isVariable()) {
		return true;
	    } else {
		return false;
	    }
	}

	if (!subject.isURI()) {
	    return false;
	}

	RelaxationOperators temp_model_operator = new RelaxationOperators(s);

	// Classe type
	if (type == 1) {
	    Map<String, Integer> relaxed_node = temp_model_operator
		    .getURISuperClasses(subject2);
	    if (relaxed_node.get(subject.getURI()) != null) {
		return true;
	    } else {
		if (subject.isVariable()) {
		    return true;
		} else {
		    return false;
		}
	    }
	}
	// Property type
	if (type == 2) {
	    Map<String, Integer> relaxed_node = temp_model_operator
		    .getURISuperProperty(subject2);
	    if (relaxed_node.get(subject.getURI()) != null) {
		return true;
	    } else {
		if (subject.isVariable()) {
		    return true;
		} else {
		    return false;
		}
	    }
	}
	return false;
    }

    /**
     * Get Node Relaxed corresponding to the CElement only and without super classes
     * @param elt
     * @return
     */
    public static NodeRelaxed[] getNodeRelaxed(CElement elt){
	
	if (elt.getLabel().startsWith("t")) {
	    TriplePath currentClause = ((ElementPathBlock) elt.getElement())
		    .getPattern().getList().get(0);
	    NodeRelaxed[] tripleRelaxed = new NodeRelaxed[1];
	    tripleRelaxed[0] = new NodeRelaxed(currentClause.getSubject(), currentClause.getPredicate(), currentClause.getObject(),
			1.0, new int[] { 0, 0, 0 });
	   return tripleRelaxed;
	} else {
	    return null;
	}

    }
}
