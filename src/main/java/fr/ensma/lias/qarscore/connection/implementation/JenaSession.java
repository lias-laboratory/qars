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
package fr.ensma.lias.qarscore.connection.implementation;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Dataset;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.LubmOntology;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.connection.statement.QueryStatementFactory;

/**
 * @author Geraud FOKOU
 */
public abstract class JenaSession implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    /**
     * Dataset use for querying
     */
    protected Dataset dataset;

    /**
     * LubmOntology Statistic
     */

    protected LubmOntology lubmStat;

    public LubmOntology getStat_Lubm_data() {
	return LubmOntology.getInstance();
    }

    public Dataset getDataset() {
	return dataset;
    }

    protected boolean load_stat_data(String folderTDB) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public QueryStatement createStatement(String query) {
	return QueryStatementFactory.createQueryStatement(query, session);
    }

    @Override
    public void close() {
	dataset.close();
    }

    @Override
    public boolean isclose() {
	return false;
    }

    @Override
    public void open() {
    }

    @Override
    public boolean isopen() {
	return true;
    }

    @Override
    public double similarityMeasureClass(Node original_node, Node relaxed_node) {

	if (original_node.equals(relaxed_node)) {
	    return 1.0;
	}

	String original_class = null;
	String relaxed_class = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_class = original_node.getURI();
	}

	if (original_class == null) {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_class = relaxed_node.getURI();
	}

	return conceptMeasure(original_class, relaxed_class);
    }

    @Override
    public double similarityMeasureProperty(Node original_node,
	    Node relaxed_node) {

	if (original_node.equals(relaxed_node)) {
	    return 1.0;
	}

	String original_property = null;
	String relaxed_property = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_property = original_node.getURI();
	}

	if (original_property == null) {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_property = relaxed_node.getURI();
	}

	return propertyMeasure(original_property, relaxed_property);
    }

    private double conceptMeasure(String original_class, String relaxed_class) {

	double ic_class1 = this.getStat_Lubm_data().getIcClass(original_class);
	double ic_class2 = this.getStat_Lubm_data().getIcClass(relaxed_class);
	if (ic_class1 == 0) {
	    return 0;
	}
	return ic_class2 / ic_class1;

    }

    private double propertyMeasure(String original_property,
	    String relaxed_property) {

	double ic_prop1 = this.getStat_Lubm_data().getIcProperty(
		original_property);
	double ic_prop2 = this.getStat_Lubm_data().getIcProperty(
		relaxed_property);
	if (ic_prop1 == 0) {
	    return 0;
	}
	return ic_prop2 / ic_prop1;

    }

    private double literalOrValueMeasure(Node original_node, Node relaxed_node) {
	// TODO Auto-generated method stub

	return 0.0;
    }

    protected void getStatsOnLubm() throws Exception {

	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "ClericalStaff",
		LubmOntology.PREFIX_UB + "AdministrativeStaff");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "SystemsStaff",
		LubmOntology.PREFIX_UB + "AdministrativeStaff");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "AdministrativeStaff",
		LubmOntology.PREFIX_UB + "Employee");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Faculty",
		LubmOntology.PREFIX_UB + "Employee");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "ConferencePaper",
		LubmOntology.PREFIX_UB + "Article");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "JournalArticle",
		LubmOntology.PREFIX_UB + "Article");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "TechnicalReport",
		LubmOntology.PREFIX_UB + "Article");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Article",
		LubmOntology.PREFIX_UB + "Publication");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Book",
		LubmOntology.PREFIX_UB + "Publication");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Manual",
		LubmOntology.PREFIX_UB + "Publication");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Software",
		LubmOntology.PREFIX_UB + "Publication");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Specification",
		LubmOntology.PREFIX_UB + "Publication");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "UnofficialPublication",
		LubmOntology.PREFIX_UB + "Publication");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "AssistantProfessor",
		LubmOntology.PREFIX_UB + "Professor");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "AssociateProfessor",
		LubmOntology.PREFIX_UB + "Professor");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Chair",
		LubmOntology.PREFIX_UB + "Professor");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Dean",
		LubmOntology.PREFIX_UB + "Professor");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "FullProfessor",
		LubmOntology.PREFIX_UB + "Professor");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "VisitingProfessor",
		LubmOntology.PREFIX_UB + "Professor");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "GraduateStudent",
		LubmOntology.PREFIX_UB + "Person");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "ResearchAssistant",
		LubmOntology.PREFIX_UB + "Person");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Department",
		LubmOntology.PREFIX_UB + "Organization");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "College",
		LubmOntology.PREFIX_UB + "Organization");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Program",
		LubmOntology.PREFIX_UB + "Organization");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Institute",
		LubmOntology.PREFIX_UB + "Organization");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "ResearchGroup",
		LubmOntology.PREFIX_UB + "Organization");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "University",
		LubmOntology.PREFIX_UB + "Organization");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "GraduateCourse",
		LubmOntology.PREFIX_UB + "Course");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Course",
		LubmOntology.PREFIX_UB + "Work");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Research",
		LubmOntology.PREFIX_UB + "Work");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Professor",
		LubmOntology.PREFIX_UB + "Faculty");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "Lecturer",
		LubmOntology.PREFIX_UB + "Faculty");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "PostDoc",
		LubmOntology.PREFIX_UB + "Faculty");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "GraduateStudent",
		LubmOntology.PREFIX_UB + "f7d3bc3ae45e3dd7aae1731beb113b34");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "ResearchAssistant",
		LubmOntology.PREFIX_UB + "5b651c4c09981f244d84a9dd6c97a1b9");
	LubmOntology.getInstance().addSuperClass(
		LubmOntology.PREFIX_UB + "UndergraduateStudent",
		LubmOntology.PREFIX_UB + "Student");

	// Number of instances
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "University", 1000);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Organization", 33043);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Department", 2007);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "FullProfessor", 17144);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Professor", 60268);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Faculty", 72302);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Employee", 72302);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Course", 217148);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Work", 217148);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Person", 1120834);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "AssociateProfessor", 24036);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "AssistantProfessor", 19088);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Lecturer", 12034);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "UndergraduateStudent", 795970);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Student", 795970);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "GraduateStudent", 252562);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "ResearchGroup", 30036);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Publication", 808741);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "GraduateCourse", 108514);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "TeachingAssistant", 55994);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "ResearchAssistant", 72927);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "AdministrativeStaff", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Article", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Book", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Chair", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "ClericalStaff", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "College", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "ConferencePaper", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Dean", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Director", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Program", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Institute", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "JournalArticle", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Manual", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "PostDoc", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Research", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Schedule", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Software", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "Specification", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "SystemsStaff", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "TechnicalReport", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "UnofficialPublication", 0);
	LubmOntology.getInstance().addInstances(
		LubmOntology.PREFIX_UB + "VisitingProfessor", 0);

	// Total number of instances
	LubmOntology.getInstance().setNbInstances(2179766);

	// Subproperties
	LubmOntology.getInstance().addSuperProperty(
		LubmOntology.PREFIX_UB + "undergraduateDegreeFrom",
		LubmOntology.PREFIX_UB + "degreeFrom");
	LubmOntology.getInstance().addSuperProperty(
		LubmOntology.PREFIX_UB + "mastersDegreeFrom",
		LubmOntology.PREFIX_UB + "degreeFrom");
	LubmOntology.getInstance().addSuperProperty(
		LubmOntology.PREFIX_UB + "doctoralDegreeFrom",
		LubmOntology.PREFIX_UB + "degreeFrom");
	LubmOntology.getInstance().addSuperProperty(
		LubmOntology.PREFIX_UB + "headOf",
		LubmOntology.PREFIX_UB + "worksFor");
	LubmOntology.getInstance().addSuperProperty(
		LubmOntology.PREFIX_UB + "worksFor",
		LubmOntology.PREFIX_UB + "memberOf");

	// Triples By Prop
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "degreeFrom", 469226);
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "doctoralDegreeFrom", 72302);
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "headOf", 2007);
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "mastersDegreeFrom", 72302);
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "memberOf", 1120834);
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "undergraduateDegreeFrom", 324864);
	LubmOntology.getInstance().addTriples(
		LubmOntology.PREFIX_UB + "worksFor", 72302);

	// Total number of triples
	LubmOntology.getInstance().setNbTriples(16757086);
    }
}
