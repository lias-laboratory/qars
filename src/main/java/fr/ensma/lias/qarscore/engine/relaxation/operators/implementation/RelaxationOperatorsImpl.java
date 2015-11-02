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
package fr.ensma.lias.qarscore.engine.relaxation.operators.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntClass;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.operators.RelaxationOperators;
import fr.ensma.lias.qarscore.engine.similaritymeasure.SimilarityMeasureConcept;

/**
 * @author Geraud FOKOU
 */
public class RelaxationOperatorsImpl extends PrimitiveRelaxationOperators implements RelaxationOperators {
    
    protected RelaxationOperatorsImpl(Session s) {
	super();
    }

    private Map<CElement, Double> generalize(CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	relaxedPatterns.putAll(this.generalize_Object(element_to_relax, session));
	
	relaxedPatterns.putAll(this.generalize_Subject(element_to_relax, session));
	
	relaxedPatterns.putAll(this.generalize_Predicate(element_to_relax, session));
	
	return relaxedPatterns;
 
    }    
    
    private Map<CElement, Double> releaseValue(CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	relaxedPatterns.putAll(this.release_Literal(element_to_relax, session));
	
	relaxedPatterns.putAll(this.release_URI(element_to_relax, session));
	
	relaxedPatterns.putAll(this.release_Blank(element_to_relax, session));
	
	return relaxedPatterns;
 
    }    
    
    @Override
    public Map<CQuery, Double> generalize(CQuery query, Double sim_query, Session session) {
	
	Map<CQuery, Double> relaxedQueries = new HashMap<CQuery, Double>();;
	Map<CElement, Double> tempRelaxedPatterns;
	
	for(CElement elt_to_relax:query.getElementList()){
	    
	    tempRelaxedPatterns = this.generalize(elt_to_relax, session);	
	    
	    for(CElement relaxed_elt:tempRelaxedPatterns.keySet()){
		
		CQuery tempQuery = CQueryFactory.cloneCQuery(query);
		tempQuery.replace(elt_to_relax, relaxed_elt);
		double new_sim_query = sim_query * tempRelaxedPatterns.get(relaxed_elt);
		relaxedQueries.put(tempQuery, new_sim_query);
	    }
	}
	
	return relaxedQueries;
    }
    
    @Override
    public Map<CQuery, Double> releaseValue(CQuery query, Double sim_query, Session session) {
	
	Map<CQuery, Double> relaxedQueries = new HashMap<CQuery, Double>();;
	Map<CElement, Double> tempRelaxedPatterns;
	
	for(CElement elt_to_relax:query.getElementList()){
	    
	    tempRelaxedPatterns = this.releaseValue(elt_to_relax, session);	
	    
	    for(CElement relaxed_elt:tempRelaxedPatterns.keySet()){
		
		CQuery tempQuery = CQueryFactory.cloneCQuery(query);
		tempQuery.replace(elt_to_relax, relaxed_elt);
		double new_sim_query = sim_query * tempRelaxedPatterns.get(relaxed_elt);
		relaxedQueries.put(tempQuery, new_sim_query);
	    }
	}
	
	return relaxedQueries;
    }

    @Override
    public Map<CQuery, Double> releaseValue(CQuery query, Node value, Session session) {
	// TODO Auto-generated method stub
	return null;
    }

    
    public Map<CQuery, List<Double>> specialization(CQuery query, Node classe,
	    int level, Session session) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = ((JenaSession)session).getOntology().getOntClass(
		classe.getURI());
	if (currentClass == null) {
	    return relaxedQueries;
	}
	List<OntClass> directSubClass = currentClass.listSubClasses(true)
		.toList();
	depht = depht - 1;

	while ((level <= depht) && (directSubClass.size() != 0)) {

	    List<OntClass> tempSubClass = directSubClass;
	    directSubClass = new ArrayList<OntClass>();

	    for (OntClass subClass : tempSubClass) {
		if (subClass.isURIResource()) {
		    CQuery tempQuery = CQueryFactory.cloneCQuery(query);
		    if (tempQuery.replace(classe,
			    NodeFactory.createURI(subClass.getURI()))) {
			List<Double> value = new ArrayList<Double>();
			value.add((double) depht);
			value.add(SimilarityMeasureConcept.get_concept_measure(session).similarity(
				currentClass, subClass));
			relaxedQueries.put(tempQuery, value);
		    }
		    directSubClass.addAll(subClass.listSubClasses(true)
			    .toList());
		}
	    }
	    depht = depht - 1;
	}

	return relaxedQueries;
    }

    public Map<CQuery, List<Double>> specialization(CQuery query, Node classe, Session session) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = ((JenaSession)session).getOntology().getOntClass(
		classe.getURI());

	if (currentClass == null) {
	    return relaxedQueries;
	}

	List<OntClass> directSubClass = currentClass.listSubClasses(true)
		.toList();
	depht = depht - 1;

	while (directSubClass.size() != 0) {

	    List<OntClass> tempSubClass = directSubClass;
	    directSubClass = new ArrayList<OntClass>();

	    for (OntClass subClass : tempSubClass) {
		if (subClass.isURIResource()) {
		    CQuery tempQuery = CQueryFactory.cloneCQuery(query);
		    if (tempQuery.replace(classe,
			    NodeFactory.createURI(subClass.getURI()))) {
			List<Double> value = new ArrayList<Double>();
			value.add((double) depht);
			value.add(SimilarityMeasureConcept.get_concept_measure(session).similarity(
				currentClass, subClass));
			relaxedQueries.put(tempQuery, value);
		    }
		    directSubClass.addAll(subClass.listSubClasses(true)
			    .toList());
		}
	    }
	    depht = depht - 1;
	}
	return relaxedQueries;
    }

    public Map<CQuery, List<Double>> generalization(CQuery query, Node classe,
	    int level, Session session) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = ((JenaSession)session).getOntology().getOntClass(
		classe.getURI());

	if (currentClass == null) {
	    return relaxedQueries;
	}

	List<OntClass> directSuperClass = currentClass.listSuperClasses(true)
		.toList();
	depht = depht + 1;

	while ((depht <= level) && (directSuperClass.size() != 0)) {

	    List<OntClass> tempSuperClass = directSuperClass;
	    directSuperClass = new ArrayList<OntClass>();

	    for (OntClass superClass : tempSuperClass) {
		if (superClass.isURIResource()) {
		    CQuery tempQuery = CQueryFactory.cloneCQuery(query);
		    if (tempQuery.replace(classe,
			    NodeFactory.createURI(superClass.getURI()))) {
			List<Double> value = new ArrayList<Double>();
			value.add((double) depht);
			value.add(SimilarityMeasureConcept.get_concept_measure(session).similarity(
				currentClass, superClass));
			relaxedQueries.put(tempQuery, value);
		    }
		    directSuperClass.addAll(superClass.listSuperClasses(true)
			    .toList());
		}
	    }
	    depht = depht + 1;
	}
	return relaxedQueries;
    }

    public Map<CQuery, List<Double>> generalization(CQuery query, Node classe, Session session) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = ((JenaSession)session).getOntology().getOntClass(
		classe.getURI());
	if (currentClass == null) {
	    return relaxedQueries;
	}

	List<OntClass> directSuperClass = currentClass.listSuperClasses(true)
		.toList();
	depht = depht + 1;

	while (directSuperClass.size() != 0) {

	    List<OntClass> tempSuperClass = directSuperClass;
	    directSuperClass = new ArrayList<OntClass>();

	    for (OntClass superClass : tempSuperClass) {
		if (superClass.isURIResource()) {
		    CQuery tempQuery = CQueryFactory.cloneCQuery(query);
		    if (tempQuery.replace(classe,
			    NodeFactory.createURI(superClass.getURI()))) {
			List<Double> value = new ArrayList<Double>();
			value.add((double) depht);
			value.add(SimilarityMeasureConcept.get_concept_measure(session).similarity(
				currentClass, superClass));
			relaxedQueries.put(tempQuery, value);
		    }
		    directSuperClass.addAll(superClass.listSuperClasses(true)
			    .toList());
		}
	    }
	    depht = depht + 1;
	}
	return relaxedQueries;
    }

    @Override
    public Map<CQuery, List<Double>> generalize(CQuery query, Node classe,
	    int level, Session session) {

	if (level < 0) {
	    return this.specialization(query, classe, level, session);
	} else {
	    return this.generalization(query, classe, level, session);
	}
    }

    @Override
    public Map<CQuery, List<Double>> generalize(CQuery query, Node classe, Session session) {

	return this.generalization(query, classe, session);
    }

    @Override
    public Map<CQuery, Double> sibling(CQuery query, Node classe, Session session) {

	Map<CQuery, Double> relaxedQueries = new HashMap<CQuery, Double>();
	OntClass currentClass = ((JenaSession)session).getOntology().getOntClass(
		classe.getURI());
	if (currentClass == null) {
	    return relaxedQueries;
	}

	List<OntClass> listSuperClass = currentClass.listSuperClasses(true)
		.toList();
	List<OntClass> subClassesFound = new ArrayList<>();
	for (OntClass superClasse : listSuperClass) {
	    List<OntClass> listSubClass = superClasse.listSubClasses(true)
		    .toList();
	    for (OntClass subclass : listSubClass) {
		if (subclass.isURIResource()) {
		    if ((!subclass.equals(currentClass))
			    && (!subClassesFound.contains(subclass))) {
			CQuery tempQuery = CQueryFactory.cloneCQuery(query);
			if (tempQuery.replace(classe,
				NodeFactory.createURI(subclass.getURI()))) {
			    double sim = SimilarityMeasureConcept.get_concept_measure(session).similarity(currentClass, subclass);
			    relaxedQueries.put(tempQuery, sim);
			}
			subClassesFound.add(subclass);
		    }
		}
	    }
	}
	return relaxedQueries;
    }

    @Override
    public Map<CQuery, Double> relaxValue(CQuery query, Node value, Session session) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<CQuery, Double> releaseJoin(CQuery query, Node variable, Session session) {
	// TODO Auto-generated method stub
	return null;
    }    
}
