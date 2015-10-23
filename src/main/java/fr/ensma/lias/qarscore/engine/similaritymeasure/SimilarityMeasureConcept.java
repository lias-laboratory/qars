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
package fr.ensma.lias.qarscore.engine.similaritymeasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;

/**
 * @author Geraud FOKOU
 */
public class SimilarityMeasureConcept {

    private static SimilarityMeasureConcept measure_concept;

    private Map<OntClass, Double> information_content;
    
    private Session session;

    public static SimilarityMeasureConcept get_concept_measure(Session s) {
	if (measure_concept == null) {
	    measure_concept = new SimilarityMeasureConcept(s);
	}
	return measure_concept;
    }

    public SimilarityMeasureConcept(Session s) {

	session = s;
	int size_data = ((JenaSession)session).getOntology().listIndividuals().toList()
		.size();
	information_content = new HashMap<OntClass, Double>();
	ExtendedIterator<OntClass> listClass = ((JenaSession)session).getOntology()
		.listNamedClasses();

	while (listClass.hasNext()) {
	    OntClass currentClass = listClass.next();

	    if (currentClass.getURI() == null) {
		continue;
	    }
	    if (currentClass.isIntersectionClass()) {
		continue;
	    }
	    if (currentClass.isRestriction()) {
		continue;
	    }

	    double classe_size = getInstanceNumber(currentClass);
	    // double classe_size = Double.valueOf((1+currentClass.listInstances(true).toList().size()));
	    double icc_class = -1 * Math.log10(classe_size / size_data);

	    information_content.put(currentClass, icc_class);
	}
    }

    public static double getInstanceNumber(OntClass classe) {

	int number = 1 + classe.listInstances(true).toList().size();
	List<OntClass> subclasses = classe.listSubClasses(true).toList();
	
	while(!subclasses.isEmpty()){
	    
	    OntClass currentClass = subclasses.get(0);
	    subclasses.remove(currentClass);
	    if (currentClass.isIntersectionClass()) {
		continue;
	    }
	    if (currentClass.isRestriction()) {
		continue;
	    }
	    
	    number = number + currentClass.listInstances(true).toList().size();
	    subclasses.addAll(currentClass.listSubClasses(true).toList());
	}
	return number;
    }

    private int getLevel(OntClass child, OntClass parent) {

	int level = 0;
	if (child.equals(parent)) {
	    return level;
	}
	List<OntClass> super_parent = child.listSuperClasses(true).toList();
	while (!super_parent.isEmpty()) {
	    level = level + 1;
	    ArrayList<OntClass> temp_parent = new ArrayList<OntClass>();
	    temp_parent.addAll(super_parent);
	    super_parent = new ArrayList<OntClass>();
	    for (OntClass one_parent : temp_parent) {
		if (one_parent.equals(parent)) {
		    return level;
		}
		super_parent.addAll(one_parent.listSuperClasses(true).toList());
	    }
	}
	return level;
    }

    private OntClass getLeastCommonAncestor(OntClass classe1, OntClass classe2) {

	List<OntClass> superClass1 = classe1.listSuperClasses().toList();
	List<OntClass> superClass2 = classe2.listSuperClasses().toList();
	List<OntClass> intersection = new ArrayList<OntClass>();
	for (OntClass classe1_super_class : superClass1) {
	    for (OntClass classe2_super_class : superClass2) {
		if ((classe1_super_class.equals(classe2))
			|| (classe2_super_class.equals(classe1))) {
		    continue;
		}
		if (classe1_super_class.equals(classe2_super_class)) {
		    intersection.add(classe1_super_class);
		}
	    }
	}

	if (intersection.size() == 0) {
	    if (classe1.listSuperClasses().toList().contains(classe2)) {
		return classe2;
	    } else {
		if (classe2.listSuperClasses().toList().contains(classe1)) {
		    return classe1;
		} else {
		    return null;
		}
	    }
	}
	OntClass super_class = intersection.get(0);
	int level = getLevel(classe2, super_class);
	for (int i = 1; i < intersection.size(); i++) {
	    int currentlevel = getLevel(classe2, intersection.get(i));
	    if (currentlevel < level) {
		level = currentlevel;
		super_class = intersection.get(i);
	    }
	}

	return super_class;
    }

    public double similarity(OntClass classe1, OntClass classe2) {

	if ((!classe1.isClass()) || (!classe1.isURIResource())) {
	    return -1;
	}
	if ((!classe2.isClass()) || (!classe2.isURIResource())) {
	    return -1;
	}

	OntClass least_common_class = getLeastCommonAncestor(classe1, classe2);

	if (least_common_class == null) {
	    return 0;
	}

	double ic_lcc = 0;
	ic_lcc = information_content.get(least_common_class);

	double ic_class1 = 0;
	double ic_class2 = 0;
	ic_class1 = information_content.get(classe1);
	ic_class2 = information_content.get(classe2);

	return ic_lcc / (ic_class1 + ic_class2 - ic_lcc);
    }

    public double similarity(OntProperty cuurentProperty,
	    OntProperty superProperty) {
	// TODO Auto-generated method stub
	return 0;
    }
    
    public double concept_similarity(Node original_node, Node relaxed_node){
	
	return 0;
    }
}
