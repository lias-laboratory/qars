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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.JenaMetaDataSet;
import fr.ensma.lias.qarscore.connection.metadata.LubmOntology;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.connection.statement.QueryStatementFactory;

/**
 * @author Geraud FOKOU
 */
public abstract class JenaSession_Old implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    /**
     * Dataset use for querying
     */
    protected Dataset dataset;

    /**
     * model of semantic data
     */
//    protected Model model;

    /**
     * Ontology of the data
     */
//    protected OntModel ontology;

    /**
     * Ontology of the data without reasoning
     */
//    protected OntModel baseontology;

    /**
     * Statistics data
     */
    protected JenaMetaDataSet stat_meta_data;
    
    /**
     * LubmOntology Statistic
     */
 
    protected LubmOntology lubmStat ;
    
    /**
     * @return the stat_meta_data
     */
    public JenaMetaDataSet getStat_meta_data() {
        return stat_meta_data;
    }
    
    public LubmOntology getStat_Lubm_data() {
        return LubmOntology.getInstance();
    }


    /**
     * 
     */
//    protected void set_model() {
//
//	/**
//	 * Return a prebuilt standard configuration for the default RDFS
//	 * reasoner
//	 */
//	if (Properties.getModelMemSpec().equals(OntModelSpec.OWL_MEM_RDFS_INF)) {
//	    model = ModelFactory.createInfModel(
//		    ReasonerRegistry.getRDFSReasoner(),
//		    dataset.getDefaultModel());
//	    ontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_MEM_RDFS_INF, dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_MEM, ontology.getBaseModel());
//	}
//
//	/**
//	 * Return a prebuilt standard configuration for the default
//	 * subclass/subproperty transitive closure reasoner.
//	 */
//	else if (Properties.getModelMemSpec().equals(
//		OntModelSpec.OWL_MEM_TRANS_INF)) {
//	    model = ModelFactory.createInfModel(
//		    ReasonerRegistry.getTransitiveReasoner(),
//		    dataset.getDefaultModel());
//	    ontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_MEM_TRANS_INF, dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_MEM, ontology.getBaseModel());
//	}
//
//	/**
//	 * Default model without inferred triple
//	 */
//	else if (Properties.getModelMemSpec().equals(OntModelSpec.OWL_MEM)) {
//	    model = dataset.getDefaultModel();
//	    ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM,
//		    dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_MEM, ontology.getBaseModel());
//	}
//	/**
//	 * Default model without inferred triple
//	 */
//	else if (Properties.getModelMemSpec().equals(OntModelSpec.OWL_DL_MEM)) {
//	    model = dataset.getDefaultModel();
//	    ontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_DL_MEM, dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_DL_MEM, ontology.getBaseModel());
//	}
//
//	/**
//	 * Prebuilt standard configuration for the default OWL reasoner.
//	 */
//	else if (Properties.getModelMemSpec().equals(
//		OntModelSpec.OWL_DL_MEM_RULE_INF)) {
//	    model = ModelFactory.createInfModel(
//		    ReasonerRegistry.getOWLReasoner(),
//		    dataset.getDefaultModel());
//	    ontology = ModelFactory
//		    .createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF,
//			    dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_DL_MEM, ontology.getBaseModel());
//	}
//
//	/**
//	 * Prebuilt standard configuration for the default OWL reasoner.
//	 */
//	else if (Properties.getModelMemSpec().equals(
//		OntModelSpec.OWL_DL_MEM_RDFS_INF)) {
//	    model = ModelFactory.createInfModel(
//		    ReasonerRegistry.getRDFSReasoner(),
//		    dataset.getDefaultModel());
//	    ontology = ModelFactory
//		    .createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF,
//			    dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.OWL_DL_MEM, ontology.getBaseModel());
//	}
//
//	else if (Properties.getModelMemSpec().equals(OntModelSpec.RDFS_MEM)) {
//	    model = dataset.getDefaultModel();
//	    ontology = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM,
//		    dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.RDFS_MEM, ontology.getBaseModel());
//	}
//	/**
//	 * Return a prebuilt standard configuration for the default RDFS
//	 * reasoner
//	 */
//	else if (Properties.getModelMemSpec().equals(
//		OntModelSpec.RDFS_MEM_RDFS_INF)) {
//	    model = ModelFactory.createInfModel(
//		    ReasonerRegistry.getRDFSReasoner(),
//		    dataset.getDefaultModel());
//	    ontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.RDFS_MEM_RDFS_INF, dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.RDFS_MEM, ontology.getBaseModel());
//	}
//
//	/**
//	 * Return a prebuilt standard configuration for the default
//	 * subclass/subproperty transitive closure reasoner.
//	 */
//	else if (Properties.getModelMemSpec().equals(
//		OntModelSpec.RDFS_MEM_TRANS_INF)) {
//	    model = ModelFactory.createInfModel(
//		    ReasonerRegistry.getTransitiveReasoner(),
//		    dataset.getDefaultModel());
//	    ontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.RDFS_MEM_TRANS_INF, dataset.getDefaultModel());
//	    baseontology = ModelFactory.createOntologyModel(
//		    OntModelSpec.RDFS_MEM, ontology.getBaseModel());
//	}
//
//	else {
//	    throw new NotYetImplementedException(
//		    "unknow ontology specification");
//	}
//
//    }

    /**
     * 
     */
    protected void set_stat_data() {

	Map<String, Integer> instance_by_class = new HashMap<String, Integer>();
	Map<String, Integer> triple_by_property = new HashMap<String, Integer>();
	String rdf_prefix = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
	int size_instance = 0;
	
	String instance_by_class_query = rdf_prefix
		+ " SELECT ?classe (COUNT(?instance) AS ?numberInstance)  "
		+ "WHERE {?instance rdf:type ?classe . }" + "GROUP BY ?classe "
		+ "ORDER BY ?numberInstance ";

	String triple_by_property_query = rdf_prefix
		+ " SELECT ?property (COUNT(?property) AS ?numberProperty)  "
		+ "WHERE { ?s ?property ?o  .} " + "GROUP BY ?property "
		+ "ORDER BY ?numberProperty ";

	QueryExecution qexec = QueryExecutionFactory.create(
		instance_by_class_query, dataset);
	ResultSet result = qexec.execSelect();
	while(result.hasNext()){
	    QuerySolution sol = result.next();
	    instance_by_class.put(sol.getResource("classe").getURI(), sol.getLiteral("numberInstance").getInt());
	    size_instance = size_instance +  sol.getLiteral("numberInstance").getInt();
	}
  
//	int size_instance = baseontology.listIndividuals().toList().size();

	qexec = QueryExecutionFactory.create(
		triple_by_property_query, dataset);
	result = qexec.execSelect();
	while(result.hasNext()){
	    QuerySolution sol = result.next();
	    triple_by_property.put(sol.getResource("property").getURI(), sol.getLiteral("numberProperty").getInt());
	}

	int size_triple = (int) dataset.asDatasetGraph().size();
//	int size_triple = ontology.listStatements().toList().size();
	stat_meta_data = new JenaMetaDataSet(instance_by_class,
		triple_by_property, size_instance, size_triple);
    }

    protected boolean load_stat_data(String folderTDB) {
	// TODO Auto-generated method stub
	return false;
    }

    protected boolean save_stat_data(String folderTDB) {
	// TODO Auto-generated method stub
	return false;
    }

    public Dataset getDataset() {
	return dataset;
    }

    public Model getModel() {
	return null;
//	return model;
    }

    public OntModel getOntology() {
	return null;
//	return ontology;
    }

    /**
     * @return the baseontology
     */
    public OntModel getBaseontology() {
	return null;
//      return baseontology;
    }

    /**
     * Return all the triple of the ontology for the data set in the SDB Triple
     * store
     */
    public List<Triple> getTripleList() {

	List<Triple> allTriple = new ArrayList<Triple>();

	StmtIterator tripleIterator = this.dataset.getDefaultModel().listStatements();
	while (tripleIterator.hasNext()) {
	    allTriple.add(tripleIterator.next().asTriple());
	}
	return allTriple;
    }

    /**
     * Return the ontology and its instance in a JSON format
     */
    public String getOntoJSON() {

	// JenaJSONLD.init();
	StringWriter writer = new StringWriter();
	getModel().write(writer, "JSON-LD");

	return writer.toString();
    }

    @Override
    public QueryStatement createStatement(String query) {
	return QueryStatementFactory.createQueryStatement(query, session);
    }

    @Override
    public void close() {
	dataset.close();
//	model.close();
//	ontology.close();
    }

    @Override
    public boolean isclose() {
	
	return true;
//	return model.isClosed();
    }

    @Override
    public void open() {
//	model.begin();
//	ontology.begin();
    }

    @Override
    public boolean isopen() {
	return true;
//	return !model.isClosed();
    }

    @Override
    public double similarityMeasureClass(Node original_node, Node relaxed_node) {

	if (original_node.equals(relaxed_node)) {
	    return 1.0;
	}

	OntClass original_class = null;
	OntClass relaxed_class = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_class = this.getOntology().getOntClass(
		    original_node.getURI());
	}

	if (original_class == null) {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_class = this.getOntology().getOntClass(
		    relaxed_node.getURI());
	}

	return conceptMeasure(original_class, relaxed_class);
    }

    @Override
    public double similarityMeasureProperty(Node original_node,
	    Node relaxed_node) {

	if (original_node.equals(relaxed_node)) {
	    return 1.0;
	}

	OntProperty original_property = null;
	OntProperty relaxed_property = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_property = this.getOntology().getOntProperty(
		    original_node.getURI());
	}

	if (original_property == null) {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_property = this.getOntology().getOntProperty(
		    relaxed_node.getURI());
	}

	return propertyMeasure(original_property, relaxed_property);
    }

    private double conceptMeasure(OntClass original_class,
	    OntClass relaxed_class) {

	if ((!original_class.isClass()) || (!original_class.isURIResource())) {
	    return -1;
	}
	if ((!relaxed_class.isClass()) || (!relaxed_class.isURIResource())) {
	    return -1;
	}

/*	OntClass least_common_class = getLeastCommonClassAncestor(
		original_class, relaxed_class);

	if (least_common_class == null) {
	    return 0;
	}

	double ic_lcc = 0;
	try {
	    ic_lcc = information_content.get(least_common_class);
	} catch (NullPointerException e) {
	    Logger.getRootLogger().debug(e.getMessage());
	    return 0;
	}
*/
	double ic_class1 = this.stat_meta_data.getInformationContent(original_class.getURI());
	double ic_class2 = this.stat_meta_data.getInformationContent(relaxed_class.getURI());
	if(ic_class1 == 0){
	    return 0;
	}
	return ic_class2 / ic_class1;

    }

    private double propertyMeasure(OntProperty original_property,
	    OntProperty relaxed_property) {

	if (!original_property.isProperty()) {
	    return -1;
	}
	if (!relaxed_property.isProperty()) {
	    return -1;
	}

/*	OntProperty least_common_Property = getLeastCommonPropertyAncestor(
		original_property, relaxed_property);

	if (least_common_Property == null) {
	    return 0;
	}

	double ic_lcp = 0;

	ic_lcp = information_content.get(least_common_Property);
*/
	double ic_prop1 = this.stat_meta_data.getInformationContent(original_property.getURI());
	double ic_prop2 = this.stat_meta_data.getInformationContent(relaxed_property.getURI());
	if(ic_prop1 == 0){
	    return 0;
	}
	return ic_prop2 / ic_prop1;

    }

    /**
     * 
     * @param original_class
     * @param relaxed_class
     * @return
     */
    @SuppressWarnings("unused")
    private OntClass getLeastCommonClassAncestor(OntClass original_class,
	    OntClass relaxed_class) {

	List<OntClass> super_class_1 = original_class.listSuperClasses(true)
		.toList();
	List<OntClass> super_class_2 = relaxed_class.listSuperClasses(true)
		.toList();
	List<OntClass> new_super_class1 = new ArrayList<OntClass>();
	List<OntClass> new_super_class2 = new ArrayList<OntClass>();

	for (OntClass class_of_super_class_1 : super_class_1) {
	    for (OntClass class_of_super_class_2 : super_class_2) {
		new_super_class2.addAll(class_of_super_class_2
			.listSuperClasses(true).toList());
		/*
		 * if ((classe1_super_class.equals(relaxed_class)) ||
		 * (classe2_super_class.equals(original_class))) { continue; }
		 */
		if (class_of_super_class_1.equals(class_of_super_class_2)) {
		    return class_of_super_class_1;
		}
	    }
	    new_super_class1.addAll(class_of_super_class_1.listSuperClasses(
		    true).toList());
	}

	while ((!new_super_class1.isEmpty()) || (!new_super_class2.isEmpty())) {

	    for (OntClass class_of_new_super_class_1 : new_super_class1) {
		for (OntClass class_of_super_class_2 : super_class_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_new_super_class_1
			    .equals(class_of_super_class_2)) {
			return class_of_new_super_class_1;
		    }
		}
	    }

	    for (OntClass class_of_new_super_class_2 : new_super_class2) {
		for (OntClass class_of_super_class_1 : super_class_1) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_super_class_1
			    .equals(class_of_new_super_class_2)) {
			return class_of_super_class_1;
		    }
		}
	    }

	    List<OntClass> temp_super_class1 = new ArrayList<OntClass>();
	    List<OntClass> temp_super_class2 = new ArrayList<OntClass>();

	    for (OntClass class_of_new_super_class_1 : new_super_class1) {
		for (OntClass class_of_new_super_class_2 : new_super_class2) {
		    temp_super_class2.addAll(class_of_new_super_class_2
			    .listSuperClasses(true).toList());
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_new_super_class_1
			    .equals(class_of_new_super_class_2)) {
			return class_of_new_super_class_1;
		    }
		}
		temp_super_class1.addAll(class_of_new_super_class_1
			.listSuperClasses(true).toList());
	    }

	    super_class_1.addAll(new_super_class1);
	    super_class_2.addAll(new_super_class2);
	    new_super_class1.clear();
	    new_super_class2.clear();
	    new_super_class1.addAll(temp_super_class1);
	    new_super_class2.addAll(temp_super_class2);
	}

	return null;
    }

    /**
     * 
     * @param original_property
     * @param relaxed_property
     * @return
     */
    @SuppressWarnings("unused")
    private OntProperty getLeastCommonPropertyAncestor(
	    OntProperty original_property, OntProperty relaxed_property) {

	List<OntProperty> super_property_1 = new ArrayList<OntProperty>();
	List<OntProperty> super_property_2 = new ArrayList<OntProperty>();

	super_property_1.addAll(original_property.listSuperProperties(true)
		.toList());
	super_property_2.addAll(relaxed_property.listSuperProperties(true)
		.toList());

	List<OntProperty> new_super_property_1 = new ArrayList<OntProperty>();
	List<OntProperty> new_super_property_2 = new ArrayList<OntProperty>();

	for (OntProperty property_of_super_property_1 : super_property_1) {
	    for (OntProperty property_of_super_property_2 : super_property_2) {
		new_super_property_2.addAll(property_of_super_property_2
			.listSuperProperties(true).toList());
		/*
		 * if ((classe1_super_class.equals(relaxed_class)) ||
		 * (classe2_super_class.equals(original_class))) { continue; }
		 */
		if (property_of_super_property_1
			.equals(property_of_super_property_2)) {
		    return property_of_super_property_1;
		}
	    }
	    new_super_property_1.addAll(property_of_super_property_1
		    .listSuperProperties(true).toList());
	}

	while ((!new_super_property_1.isEmpty())
		|| (!new_super_property_2.isEmpty())) {

	    for (OntProperty property_of_new_super_property_1 : new_super_property_1) {
		for (OntProperty property_of_super_property_2 : super_property_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_new_super_property_1
			    .equals(property_of_super_property_2)) {
			return property_of_new_super_property_1;
		    }
		}
	    }

	    for (OntProperty property_of_new_super_property_2 : new_super_property_2) {
		for (OntProperty property_of_super_property_1 : super_property_1) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_super_property_1
			    .equals(property_of_new_super_property_2)) {
			return property_of_super_property_1;
		    }
		}
	    }

	    List<OntProperty> temp_super_class1 = new ArrayList<OntProperty>();
	    List<OntProperty> temp_super_class2 = new ArrayList<OntProperty>();

	    for (OntProperty property_of_new_super_property_1 : new_super_property_1) {
		for (OntProperty property_of_new_super_property_2 : new_super_property_2) {
		    temp_super_class2.addAll(property_of_new_super_property_2
			    .listSuperProperties(true).toList());
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_new_super_property_1
			    .equals(property_of_new_super_property_2)) {
			return property_of_new_super_property_1;
		    }
		}
		temp_super_class1.addAll(property_of_new_super_property_1
			.listSuperProperties(true).toList());
	    }

	    super_property_1.addAll(new_super_property_1);
	    super_property_2.addAll(new_super_property_2);
	    new_super_property_1.clear();
	    new_super_property_2.clear();
	    new_super_property_1.addAll(temp_super_class1);
	    new_super_property_2.addAll(temp_super_class2);
	}

	return null;
    }

    private double literalOrValueMeasure(Node original_node, Node relaxed_node) {
	// TODO Auto-generated method stub

	return 0.0;
    }

    protected void getStatsOnLubm() throws Exception{
	
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
        LubmOntology.PREFIX_UB + "undergraduateDegreeFrom", LubmOntology.PREFIX_UB + "degreeFrom");
    LubmOntology.getInstance().addSuperProperty(
        LubmOntology.PREFIX_UB + "mastersDegreeFrom", LubmOntology.PREFIX_UB + "degreeFrom");
    LubmOntology.getInstance().addSuperProperty(
        LubmOntology.PREFIX_UB + "doctoralDegreeFrom", LubmOntology.PREFIX_UB + "degreeFrom");
    LubmOntology.getInstance().addSuperProperty(
        LubmOntology.PREFIX_UB + "headOf", LubmOntology.PREFIX_UB + "worksFor");
    LubmOntology.getInstance().addSuperProperty(
        LubmOntology.PREFIX_UB + "worksFor", LubmOntology.PREFIX_UB + "memberOf");

    // Triples By Prop
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "degreeFrom", 469226);
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "doctoralDegreeFrom", 72302);
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "headOf", 2007);
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "mastersDegreeFrom", 72302);
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "memberOf", 1120834);
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "undergraduateDegreeFrom", 324864);
    LubmOntology.getInstance().addTriples(LubmOntology.PREFIX_UB + "worksFor", 72302);

    // Total number of triples
    LubmOntology.getInstance().setNbTriples(16757086);
    }
}
