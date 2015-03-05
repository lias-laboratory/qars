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
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;

import fr.ensma.lias.qarscore.connection.Session;

/**
 * @author Geraud FOKOU
 */
public class SimilarityMeasureConcept {

    private static double getInstanceNumber(OntClass classe) {

	int number = classe.listInstances(true).toList().size();
	for (OntClass sub_class : classe.listSubClasses().toList()) {
	    number = number + sub_class.listInstances(true).toList().size();
	}
	return number;
    }

    private static int getLevel(OntClass child, OntClass parent) {

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

    private static OntClass getLeastCommonAncestor(OntClass classe1,
	    OntClass classe2) {

	List<OntClass> superClass1 = classe1.listSuperClasses().toList();
	superClass1.add(classe1);
	List<OntClass> superClass2 = classe2.listSuperClasses().toList();
	superClass2.add(classe2);
	List<OntClass> intersection = new ArrayList<OntClass>();
	for (OntClass classe1_super_class : superClass1) {
	    for (OntClass classe2_super_class : superClass2) {
		if (classe1_super_class.equals(classe2_super_class)) {
		    intersection.add(classe1_super_class);
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

    public static double similarity(Session session, OntClass classe1,
	    OntClass classe2) {

	if ((!classe1.isClass()) || (!classe1.isURIResource())) {
	    return -1;
	}
	if ((!classe2.isClass()) || (!classe2.isURIResource())) {
	    return -1;
	}

	OntClass least_common_class = getLeastCommonAncestor(classe1, classe2);
	double ic_lcc = -1
		* Math.log10(getInstanceNumber(least_common_class)
			/ session.getOntologyModel().listIndividuals().toList()
				.size());
	double ic_class1 = -1
		* Math.log10(getInstanceNumber(classe1)
			/ session.getOntologyModel().listIndividuals().toList()
				.size());
	double ic_class2 = -1
		* Math.log10(getInstanceNumber(classe2)
			/ session.getOntologyModel().listIndividuals().toList()
				.size());

	return ic_lcc / (ic_class1 + ic_class2 - ic_lcc);
    }
}
