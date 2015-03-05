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
package fr.ensma.lias.qarscore.engine.relaxation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.OperatorsFactory;
import fr.ensma.lias.qarscore.engine.relaxation.implementation.utils.RelaxationTree;

/**
 * @author Geraud FOKOU
 */
public class SimilarityStrategy {

    private CQuery root_query;
    private Session session;
    private RelaxationTree relaxed_queries;
    private List<RelaxationTree> leaf_queries;

    /**
     * 
     */
    public SimilarityStrategy(CQuery query, Session s) {
	root_query = query;
	session = s;
	relaxed_queries = new RelaxationTree(root_query, null, 1);
	leaf_queries = new ArrayList<RelaxationTree>();
	leaf_queries.add(relaxed_queries);
    }

    private int get_position(List<RelaxationTree> tree_list,
	    RelaxationTree current_tree) {

	int position = 0;
	boolean found = false;
	while (!found) {
	    if (position == tree_list.size()) {
		found = true;
		break;
	    }
	    if (tree_list.get(position).getSimilarity() > current_tree
		    .getSimilarity()) {
		position++;
		break;
	    }
	    found = true;
	}

	return position;
    }

    private List<RelaxationTree> relaxation_node(RelaxationTree current_root,
	    Node node, Session s) {

	RelaxationOperators operator_relax = OperatorsFactory.createOperator(s);
	List<RelaxationTree> child = new ArrayList<RelaxationTree>();

	if (node.isURI()) {
	    if (session.getOntologyModel().getOntClass(node.getURI()) != null) {
		Map<CQuery, List<Double>> genqueries = operator_relax
			.generalize(current_root.getQuery(), node, 1);
		for (CQuery q : genqueries.keySet()) {
		    RelaxationTree rtree = new RelaxationTree(q, current_root,
			    genqueries.get(q).get(1).doubleValue()
				    * current_root.getSimilarity());
		    current_root.getRelaxedQuery().add(
			    this.get_position(current_root.getRelaxedQuery(),
				    rtree), rtree);
		    child.add(this.get_position(child, rtree), rtree);
		}
		Map<CQuery, Double> sibqueries = operator_relax.sibling(
			current_root.getQuery(), node);
		for (CQuery q : sibqueries.keySet()) {
		    RelaxationTree rtree = new RelaxationTree(q, current_root,
			    sibqueries.get(q).doubleValue()
				    * current_root.getSimilarity());
		    current_root.getRelaxedQuery().add(
			    this.get_position(current_root.getRelaxedQuery(),
				    rtree), rtree);
		    child.add(this.get_position(child, rtree), rtree);
		}
		// suppress value ( change into variable)
		return child;
	    } else {
		// Todo (uri instance)
		// suppress value ( change into variable)
		return child;
	    }
	}

	if (node.isLiteral()) {
	    // Todo relax value (string, int, double)
	    return child;
	}

	if (node.isVariable()) {
	    // Todo join release
	    return child;
	}
	return child;
    }

    private List<RelaxationTree> relaxation_element(
	    RelaxationTree current_root, CElement element, Session s) {

	List<RelaxationTree> child = new ArrayList<RelaxationTree>();

	if (element.getElement() instanceof ElementPathBlock) {
	    TriplePath currentClause = ((ElementPathBlock) element.getElement())
		    .getPattern().getList().get(0);
	    List<RelaxationTree> temp_leaf_tree = relaxation_node(current_root,
		    currentClause.getSubject(), s);
	    temp_leaf_tree.addAll(relaxation_node(current_root,
		    currentClause.getObject(), s));
	    for (RelaxationTree one_tree : temp_leaf_tree) {
		child.add(this.get_position(child, one_tree), one_tree);
	    }
	    return child;
	} else {
	    // Todo Filter element
	    return child;
	}
    }

    public List<RelaxationTree> relaxation_tree(RelaxationTree current_root,
	    Session s) {

	List<RelaxationTree> child = new ArrayList<RelaxationTree>();
	for (CElement element : current_root.getQuery().getElementList()) {
	    child.addAll(relaxation_element(current_root, element, s));
	}

	List<RelaxationTree> old_child = new ArrayList<RelaxationTree>();
	old_child.addAll(child);
	while (!old_child.isEmpty()) {
	    child = new ArrayList<RelaxationTree>();
	    for (RelaxationTree relative_root : old_child) {
		for (CElement element : relative_root.getQuery()
			.getElementList()) {
		    child.addAll(relaxation_element(relative_root, element, s));
		}
	    }
	    old_child.clear();
	    for (RelaxationTree one_tree : child) {
		old_child.add(this.get_position(old_child, one_tree), one_tree);
	    }
	}
	return child;
    }

    public void next_step() {

	List<RelaxationTree> current_roots = new ArrayList<RelaxationTree>();
	List<RelaxationTree> temp_leaf_tree = new ArrayList<RelaxationTree>();

	current_roots.addAll(leaf_queries);
	leaf_queries.clear();
	for (RelaxationTree current_root : current_roots) {
	    for (CElement element : current_root.getQuery().getElementList()) {
		temp_leaf_tree.addAll(relaxation_element(current_root, element,
			session));
	    }
	}
	for (RelaxationTree one_tree : temp_leaf_tree) {
	    leaf_queries.add(this.get_position(leaf_queries, one_tree),
		    one_tree);
	}
    }

    public CQuery get_level_relaxed_query(int level) {

	CQuery current_query = null;

	return current_query;
    }

    /**
     * @return the root_query
     */
    public CQuery getRoot_query() {
	return root_query;
    }

    /**
     * @param root_query
     *            the root_query to set
     */
    public void setRoot_query(CQuery root_query) {
	this.root_query = root_query;
	relaxed_queries = new RelaxationTree(root_query, null, 1);
	List<RelaxationTree> leaf_queries = new ArrayList<RelaxationTree>();
	leaf_queries.add(relaxed_queries);
    }

    /**
     * @return the relaxed_queries
     */
    public RelaxationTree getRelaxed_queries() {
	return relaxed_queries;
    }
}
