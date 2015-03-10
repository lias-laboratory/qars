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
import com.hp.hpl.jena.graph.NodeFactory;
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

    private Session session;
    private RelaxationTree relaxed_queries_graph;

    /**
     * 
     */
    public SimilarityStrategy(CQuery query, Session s) {

	session = s;
	relaxed_queries_graph = new RelaxationTree(query, null, 1);
    }

    private boolean relaxation_node(RelaxationTree graph_to_relax, Node node,
	    Session s) {

	RelaxationOperators operator_relax = OperatorsFactory.createOperator(s);
	boolean exist_relaxation = false;
	if (node.isURI()) {
	    if (session.getOntologyModel().getOntClass(node.getURI()) != null) {
		Map<CQuery, List<Double>> genqueries = operator_relax
			.generalize(graph_to_relax.getQuery(), node, 1);
		for (CQuery q : genqueries.keySet()) {
		    RelaxationTree temp_query = relaxed_queries_graph
			    .isInGraph(q);
		    double current_sim = 0;
		    if (temp_query != null) {
			current_sim = temp_query.getSimilarity();
		    } else {
			current_sim = genqueries.get(q).get(1).doubleValue()
				* graph_to_relax.getSimilarity();
		    }
		    RelaxationTree rtree = new RelaxationTree(q,
			    graph_to_relax, current_sim);

		    graph_to_relax.add_child(rtree);
		    exist_relaxation = true;
		}
		Map<CQuery, Double> sibqueries = operator_relax.sibling(
			graph_to_relax.getQuery(), node);
		for (CQuery q : sibqueries.keySet()) {
		    if (graph_to_relax.getSource_query() != null) {
			if (graph_to_relax.getSource_query().getQuery()
				.equals(q)) {
			    continue;
			}
		    }
		    RelaxationTree temp_query = relaxed_queries_graph
			    .isInGraph(q);
		    double current_sim = 0;
		    if (temp_query != null) {
			current_sim = temp_query.getSimilarity();
		    } else {
			current_sim = sibqueries.get(q).doubleValue()
				* graph_to_relax.getSimilarity();
		    }
		    RelaxationTree rtree = new RelaxationTree(q,
			    graph_to_relax, current_sim);

		    graph_to_relax.add_child(rtree);
		    exist_relaxation = true;
		}
		// suppress value ( change into variable)
		return exist_relaxation;
	    } else {
		// Todo (uri instance)
		// suppress value ( change into variable)
		return exist_relaxation;
	    }
	}

	if (node.isLiteral()) {
	    // Todo relax value (string, int, double)
	    return exist_relaxation;
	}

	if (node.isVariable()) {
	    // Todo join release
	    return exist_relaxation;
	}
	return exist_relaxation;
    }

    private boolean relaxation_element(RelaxationTree query_to_relax,
	    CElement elt_to_relax, Session s) {

	boolean has_relaxation = false;

	if (elt_to_relax.getElement() instanceof ElementPathBlock) {
	    TriplePath currentClause = ((ElementPathBlock) elt_to_relax
		    .getElement()).getPattern().getList().get(0);

	    boolean temp = relaxation_node(query_to_relax,
		    currentClause.getSubject(), s);
	    has_relaxation = has_relaxation || temp;

	    temp = relaxation_node(query_to_relax, currentClause.getObject(), s);
	    has_relaxation = has_relaxation || temp;

	    return has_relaxation;
	} else {
	    // Todo Filter element
	    return has_relaxation;
	}
    }

    public boolean next_step() {

	List<RelaxationTree> current_roots = relaxed_queries_graph.getLeaf();
	boolean hasRelaxation = false;

	for (int i = 0; i < current_roots.size(); i++) {
	    RelaxationTree current_root = current_roots.get(i);
	    for (int j = 0; j < current_root.getQuery().getElementList().size(); j++) {
		CElement element = current_root.getQuery().getElementList()
			.get(j);
		boolean temp = relaxation_element(current_root, element,
			session);
		hasRelaxation = hasRelaxation || temp;
	    }
	}
	return hasRelaxation;
    }

    /**
     * Gen relaxation on all the leaf of the current relaxation tree
     * 
     * @param uri
     * @return
     */
    public boolean next_gen_relax(String uri, int level) {

	RelaxationOperators operator_relax = OperatorsFactory
		.createOperator(session);
	List<RelaxationTree> current_roots = new ArrayList<RelaxationTree>();

	if (session.getOntologyModel().getOntClass(uri) != null) {
	    current_roots.addAll(relaxed_queries_graph.getLeaf());
	    for (RelaxationTree current_root : current_roots) {
		Map<CQuery, List<Double>> genqueries = operator_relax
			.generalize(current_root.getQuery(),
				NodeFactory.createURI(uri), level);

		for (CQuery q : genqueries.keySet()) {
		    RelaxationTree temp_query = relaxed_queries_graph
			    .isInGraph(q);
		    double current_sim = 0;
		    if (temp_query != null) {
			current_sim = temp_query.getSimilarity();
		    } else {
			current_sim = genqueries.get(q).get(1).doubleValue()
				* current_root.getSimilarity();
		    }
		    RelaxationTree rtree = new RelaxationTree(q, current_root,
			    current_sim);

		    current_root.add_child(rtree);
		}
	    }

	} else {
	    return false;
	}
	return true;
    }

    /**
     * Sibling relaxation on all the leaf of the current relaxation tree
     * 
     * @param uri
     * @return
     */
    public boolean next_sib_relax(String uri) {

	RelaxationOperators operator_relax = OperatorsFactory
		.createOperator(session);
	List<RelaxationTree> current_roots = new ArrayList<RelaxationTree>();

	if (session.getOntologyModel().getOntClass(uri) != null) {
	    current_roots.addAll(relaxed_queries_graph.getLeaf());
	    for (RelaxationTree current_root : current_roots) {
		Map<CQuery, Double> sibqueries = operator_relax.sibling(
			current_root.getQuery(), NodeFactory.createURI(uri));
		for (CQuery q : sibqueries.keySet()) {

		    if (current_root.getSource_query() != null) {
			if (current_root.getSource_query().getQuery().equals(q)) {
			    continue;
			}
		    }
		    RelaxationTree temp_query = relaxed_queries_graph
			    .isInGraph(q);
		    double current_sim = 0;
		    if (temp_query != null) {
			current_sim = temp_query.getSimilarity();
		    } else {
			current_sim = sibqueries.get(q).doubleValue()
				* current_root.getSimilarity();
		    }
		    RelaxationTree rtree = new RelaxationTree(q, current_root,
			    current_sim);

		    current_root.add_child(rtree);
		}
	    }
	} else {
	    return false;
	}
	return true;
    }

    /**
     * Return the last queries obtain by relaxation
     */
    public List<RelaxationTree> get_last_relaxed_queries() {
	return relaxed_queries_graph.getLeaf();
    }

    /**
     * @return the relaxed_queries
     */
    public RelaxationTree getRelaxed_queries_graph() {
	return relaxed_queries_graph;
    }

}
