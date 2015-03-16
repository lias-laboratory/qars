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
package fr.ensma.lias.qarscore.engine.relaxation.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntClass;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationOperators;
import fr.ensma.lias.qarscore.engine.similaritymeasure.SimilarityMeasureConcept;

/**
 * @author Geraud FOKOU
 */
public class RelaxationOperatorsImpl implements RelaxationOperators {

    private Session session;

    protected RelaxationOperatorsImpl(Session s) {
	session = s;
    }

    public Map<CQuery, List<Double>> specialization(CQuery query, Node classe,
	    int level) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = session.getOntologyModel().getOntClass(
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

    public Map<CQuery, List<Double>> specialization(CQuery query, Node classe) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = session.getOntologyModel().getOntClass(
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
	    int level) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = session.getOntologyModel().getOntClass(
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

    public Map<CQuery, List<Double>> generalization(CQuery query, Node classe) {

	int depht = 0;
	Map<CQuery, List<Double>> relaxedQueries = new HashMap<CQuery, List<Double>>();
	OntClass currentClass = session.getOntologyModel().getOntClass(
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
	    int level) {

	if (level < 0) {
	    return this.specialization(query, classe, level);
	} else {
	    return this.generalization(query, classe, level);
	}
    }

    @Override
    public Map<CQuery, List<Double>> generalize(CQuery query, Node classe) {

	return this.generalization(query, classe);
    }

    @Override
    public Map<CQuery, Double> sibling(CQuery query, Node classe) {

	Map<CQuery, Double> relaxedQueries = new HashMap<CQuery, Double>();
	OntClass currentClass = session.getOntologyModel().getOntClass(
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
    public Map<CQuery, Double> relaxValue(CQuery query, Node value) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<CQuery, Double> releaseValue(CQuery query, Node value) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Map<CQuery, Double> releaseJoin(CQuery query, Node variable) {
	// TODO Auto-generated method stub
	return null;
    }
}
