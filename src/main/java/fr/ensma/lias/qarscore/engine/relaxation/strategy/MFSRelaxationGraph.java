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
package fr.ensma.lias.qarscore.engine.relaxation.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.operators.TripleRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.StrategyFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.GraphRelaxationIndex;
import fr.ensma.lias.qarscore.engine.relaxation.utils.NodeRelaxed;

/**
 * @author Geraud FOKOU
 */
public class MFSRelaxationGraph extends HuangRelaxationStrategy{

    protected CQuery[][] mfs_by_triple;
    protected CQuery[] queries_mfs;

    /**
     * 
     */
    public MFSRelaxationGraph(CQuery query, Session s) {
	super();
	query_to_relax = query;
	session = s;
	relaxed_queries = new ArrayList<GraphRelaxationIndex>();
	MFSSearch mfs_finders = StrategyFactory.getLatticeStrategy(s, query, 1);
	List<CQuery> all_mfs = mfs_finders.getAllMFS();
	queries_mfs = new CQuery[all_mfs.size()];
	for (int i = 0; i < all_mfs.size(); i++) {
	    queries_mfs[i] = all_mfs.get(i);
	}
	start_relaxation();
    }

    protected void start_relaxation() {

	int[] relexation_limit_index = new int[query_to_relax.getElementList()
		.size()];
	int[] relaxation_index = new int[query_to_relax.getElementList().size()];

	relaxation_of_element = new NodeRelaxed[query_to_relax.getElementList()
		.size()][];
	mfs_by_triple = new CQuery[query_to_relax.getElementList().size()][];

	for (int i = 0; i < query_to_relax.getElementList().size(); i++) {
	    CElement element = query_to_relax.getElementList().get(i);
	    relaxation_of_element[i] = (new TripleRelaxation(element, session,
		    TripleRelaxation.SIM_ORDER)).get_relaxed_node_list();
	    relexation_limit_index[i] = relaxation_of_element[i].length;
	    relaxation_index[i] = 0;

	    List<CQuery> mfs_current_triple = new ArrayList<CQuery>();
	    for (CQuery mfs : queries_mfs) {
		if (mfs.contain(element)) {
		    mfs_current_triple.add(mfs);
		}
	    }

	    mfs_by_triple[i] = new CQuery[mfs_current_triple.size()];
	    for (int j = 0; j < mfs_current_triple.size(); j++) {
		mfs_by_triple[i][j] = mfs_current_triple.get(j);
	    }
	}

	relaxed_graph = new GraphRelaxationIndex(relaxation_index,
		relexation_limit_index);
	relaxed_queries.add(relaxed_graph);
    }

    public CQuery next() {

	while(true){
	    
	    List<CElement> elt_relaxed_query = new ArrayList<CElement>();
	    if (this.relaxed_queries.isEmpty()) {
		return null;
	    }
	    Map<CQuery, Boolean> relaxe_mfs = new HashMap<CQuery, Boolean>();
	    for (CQuery mfs : queries_mfs) {
		relaxe_mfs.put(mfs, false);
	    }
	    GraphRelaxationIndex current_graph = relaxed_queries.remove(0);
	    this.current_similarity = 1.0;
	    this.current_level = 0;
	    for (int i = 0; i < current_graph.getElement_index().length; i++) {
		CElement relax_element = CElement.createCTriple(query_to_relax
			.getElementList().get(0).getElement());
		relax_element = relax_element
			.replace_subject(relaxation_of_element[i][current_graph
				.getElement_index()[i]].getNode_1());
		relax_element = relax_element
			.replace_predicat(relaxation_of_element[i][current_graph
				.getElement_index()[i]].getNode_2());
		relax_element = relax_element
			.replace_object(relaxation_of_element[i][current_graph
				.getElement_index()[i]].getNode_3());
		elt_relaxed_query.add(relax_element);
		this.current_similarity = this.current_similarity
			* relaxation_of_element[i][current_graph
				.getElement_index()[i]].getSimilarity();
		this.current_level = this.current_level
			+ relaxation_of_element[i][current_graph
				.getElement_index()[i]].getRelaxation_level();

		for (int j = 0; j < mfs_by_triple[i].length; j++) {
		    relaxe_mfs.put(mfs_by_triple[i][j], true);
		}
	    }

	    for (int j = 0; j < current_graph.getChild_elt().length; j++) {
		this.insert(current_graph.getChild_elt()[j]);
	    }
	    if(!relaxe_mfs.containsValue(false)){
		current_relaxed_query = CQueryFactory.createCQuery(elt_relaxed_query);
		return current_relaxed_query;
	    }
	}
    }
}
