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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.similaritymeasure.SimilarityMeasureConcept;

/**
 * @author Geraud FOKOU
 */
public abstract class PrimitiveRelaxationOperators {

    protected static int num_relax_var = 0;
    
    private int get_pertinent_node_size(TriplePath currentClause) {

	int pertinent_node_size = 0;

	if (currentClause.getSubject().isConcrete()) {
	    pertinent_node_size++;
	} else {
	    if (!currentClause.getSubject().getName().startsWith("R")) {
		pertinent_node_size++;
	    }
	}

	if (currentClause.getObject().isConcrete()) {
	    pertinent_node_size++;
	} else {
	    if (!currentClause.getObject().getName().startsWith("R")) {
		pertinent_node_size++;
	    }
	}

	if(currentClause.getPredicate()==null){
	    return pertinent_node_size ++;
	}
	
	if (currentClause.getPredicate().isConcrete()) {
	    pertinent_node_size++;
	} else {
	    if (!currentClause.getObject().getName().startsWith("R")) {
		pertinent_node_size++;
	    }
	}

	return pertinent_node_size;
    }

    public Map<CElement, Double> generalize_Object (CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	if (element_to_relax.getElement() instanceof ElementPathBlock) {
	    
	    TriplePath currentClause = ((ElementPathBlock) element_to_relax
		    .getElement()).getPattern().getList().get(0);
	    
	    Node current_node = currentClause.getObject();
	    
	    if(current_node.isURI()){
		
		OntClass currentClass = session.getOntologyModel().getOntClass(
			current_node.getURI());
		
		if(currentClass!=null){
		    List<OntClass> directSuperClass = currentClass.listSuperClasses(true)
				.toList();
		    for (OntClass superClass : directSuperClass) {
			if (superClass.isURIResource()) {
			    CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createURI(superClass.getURI()));
			    if (!element_relaxed.equals(element_to_relax)) {
				double current_similarity = SimilarityMeasureConcept.get_concept_measure(session).similarity(
					currentClass, superClass);
				relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/3));
			    }
			}
		    }
		}
	    }

	}
	
	return relaxedPatterns;
    }
    
    public Map<CElement, Double> generalize_Subject (CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	if (element_to_relax.getElement() instanceof ElementPathBlock) {
	    
	    TriplePath currentClause = ((ElementPathBlock) element_to_relax
		    .getElement()).getPattern().getList().get(0);
	    
	    Node current_node = currentClause.getSubject();
	    
	    if(current_node.isURI()){
		
		OntClass currentClass = session.getOntologyModel().getOntClass(
			current_node.getURI());
		
		if(currentClass!=null){
		    List<OntClass> directSuperClass = currentClass.listSuperClasses(true)
				.toList();
		    for (OntClass superClass : directSuperClass) {
			if (superClass.isURIResource()) {
			    CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createURI(superClass.getURI()));
			    if (!element_relaxed.equals(element_to_relax)) {
				double current_similarity = SimilarityMeasureConcept.get_concept_measure(session).similarity(
					currentClass, superClass);
				relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/3));
			    }
			}
		    }
		}
	    }

	}
	
	return relaxedPatterns;
    }

    public Map<CElement, Double> generalize_Predicate (CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	if (element_to_relax.getElement() instanceof ElementPathBlock) {
	    
	    TriplePath currentClause = ((ElementPathBlock) element_to_relax
		    .getElement()).getPattern().getList().get(0);
	    
	    Node current_node = currentClause.getPredicate();
	    if(current_node!=null){
		if(current_node.isURI()){
		    OntProperty cuurentProperty = session.getOntologyModel().getOntProperty(current_node.getURI());
		    if(cuurentProperty!=null){
			    List<? extends OntProperty> directSuperProperty = cuurentProperty.listSuperProperties(true).toList();
			    for ( OntProperty superProperty : directSuperProperty) {
				if (superProperty.isURIResource()) {
				    CElement element_relaxed = element_to_relax.replace_subject(NodeFactory.createURI(superProperty.getURI()));
				    if (!element_relaxed.equals(element_to_relax)) {
					double current_similarity = SimilarityMeasureConcept.get_concept_measure(session).similarity(
						cuurentProperty, superProperty);
					relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/3));
				    }
				}
			    }
			}
		}
	    }

	}
	
	return relaxedPatterns;
    }

    public Map<CElement, Double> release_Literal (CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	if (element_to_relax.getElement() instanceof ElementPathBlock) {
	    
	    TriplePath currentClause = ((ElementPathBlock) element_to_relax
		    .getElement()).getPattern().getList().get(0);
	    
	    int pertinent_node_size = get_pertinent_node_size(currentClause);
	    
	    Node current_node = currentClause.getObject();
	    if(current_node.isLiteral()){
		CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createVariable("R"+num_relax_var++));
		double current_similarity = 0;
		relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
	    }
	    
	    current_node = currentClause.getSubject();
	    if(current_node.isLiteral()){
		CElement element_relaxed = element_to_relax.replace_subject(NodeFactory.createVariable("R"+num_relax_var++));
		double current_similarity = 0;
		relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
	    }

	    current_node = currentClause.getPredicate();
	    if(current_node!=null){
		if(current_node.isLiteral()){
		    CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createVariable("R"+num_relax_var++));
		    double current_similarity = 0;
		    relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
		}
	    }
	}
	
	return relaxedPatterns;
    }
    
    public Map<CElement, Double> release_URI (CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	if (element_to_relax.getElement() instanceof ElementPathBlock) {
	    
	    TriplePath currentClause = ((ElementPathBlock) element_to_relax
		    .getElement()).getPattern().getList().get(0);

	    int pertinent_node_size = get_pertinent_node_size(currentClause);
	    
	    Node current_node = currentClause.getObject();
	    if(current_node.isURI()){
		CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createVariable("R"+num_relax_var++));
		double current_similarity = 0;
		relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
	    }
	    
	    current_node = currentClause.getSubject();
	    if(current_node.isURI()){
		CElement element_relaxed = element_to_relax.replace_subject(NodeFactory.createVariable("R"+num_relax_var++));
		double current_similarity = 0;
		relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
	    }

	    current_node = currentClause.getPredicate();
	    if(current_node!=null){
		if(current_node.isURI()){
		    CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createVariable("R"+num_relax_var++));
		    double current_similarity = 0;
		    relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
		}
	    }
	}
	
	return relaxedPatterns;
    }

    public Map<CElement, Double> release_Blank (CElement element_to_relax, Session session) {
	
	Map<CElement, Double> relaxedPatterns = new HashMap<CElement, Double>();
	
	if (element_to_relax.getElement() instanceof ElementPathBlock) {
	    
	    TriplePath currentClause = ((ElementPathBlock) element_to_relax
		    .getElement()).getPattern().getList().get(0);

	    int pertinent_node_size = get_pertinent_node_size(currentClause);
	    
	    Node current_node = currentClause.getObject();
	    if(current_node.isBlank()){
		CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createVariable("R"+num_relax_var++));
		double current_similarity = 0;
		relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
	    }
	    
	    current_node = currentClause.getSubject();
	    if(current_node.isBlank()){
		CElement element_relaxed = element_to_relax.replace_subject(NodeFactory.createVariable("R"+num_relax_var++));
		double current_similarity = 0;
		relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
	    }

	    current_node = currentClause.getPredicate();
	    if(current_node!=null){
		if(current_node.isBlank()){
		    CElement element_relaxed = element_to_relax.replace_object(NodeFactory.createVariable("R"+num_relax_var++));
		    double current_similarity = 0;
		    relaxedPatterns.put(element_relaxed, (2/3)+(current_similarity*1/pertinent_node_size));
		}
	    }
	}
	
	return relaxedPatterns;
    }

}
