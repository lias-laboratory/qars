package fr.ensma.lias.qarscore.connection.implementation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.util.URIUtil;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.connection.statement.QueryStatementFactory;
import fr.ensma.lias.qarscore.loader.SesameBulkLoader;

/**
 * @author Mickael BARON
 * @author Geraud FOKOU
 */
@SuppressWarnings("deprecation")
public class SesameSession implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    private Repository repository;

    private RepositoryConnection repositoryConnection;

    private Model model;

    protected HashMap<URI, Double> information_content;

    /**
     * @return the information_content
     */
    public HashMap<URI, Double> getInformation_content() {
	return information_content;
    }

    /**
     * Construct a Sesame Session if there isn't existed
     */
    public static Session getNativeSesameSession(String repositoryPath) {

	if (session != null) {
	    return session;
	}
	File dataDir = new File(repositoryPath);
	if (!dataDir.isDirectory()) {
	    throw new IllegalArgumentException(
		    "illegal parameter: Directory path expected");
	}
	session = new SesameSession(
		new SailRepository(new NativeStore(dataDir)));
	return session;
    }

    public static Session getInMemorySesameSession(File[] datafiles,
	    String baseURI, String lang, boolean persist) {

	if (session != null) {
	    return session;
	}
	session = new SesameSession(SesameBulkLoader.loaderMemoryStore(
		datafiles, baseURI, lang, persist));
	return session;
    }

    /**
     * Construct a Sesame Session if there isn't existed
     */
    public static Session getInMemorySesameSession(File[] dataFiles,
	    String baseURI, String lang) {

	return getInMemorySesameSession(dataFiles, baseURI, lang, false);
    }

    private SesameSession(Repository sailRepository) {

	this.repository = sailRepository;
	this.open();
	GraphQueryResult graphResult = repositoryConnection.prepareGraphQuery(
		QueryLanguage.SPARQL,
		"CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }").evaluate();
	model = QueryResults.asModel(graphResult);

	int size_data = 0;
	int size_prop = 0;
	try {
	    size_data = this.getModel().filter(null, RDF.TYPE, null).subjects()
		    .size();
	    size_prop =  this.getModel().size();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	information_content = new HashMap<URI, Double>();
	Set<Resource> list_class = null;
	Set<IRI> list_property = null;
	try {
	    list_class = this.getModel().filter(null, RDF.TYPE, OWL.CLASS)
		    .subjects();
	    
	    list_property = this.getModel().predicates();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	for (Resource classe : list_class) {

	    if (!(classe instanceof URI)) {
		continue;
	    }

	    double classe_size = 0;
	    try {
		classe_size = getClassInstanceSize((URI) classe);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    // double classe_size =
	    // Double.valueOf((1+currentClass.listInstances(true).toList().size()));
	    double icc_class = -1 * Math.log10(classe_size / size_data);

	    information_content.put((URI) classe, icc_class);
	}

	for (IRI property : list_property) {

	    double property_size = 0;
	    try {
		property_size = getPropertyInstanceSize(property);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    // double classe_size =
	    // Double.valueOf((1+currentClass.listInstances(true).toList().size()));
	    double icc_class = -1 * Math.log10(property_size / size_prop);

	    information_content.put(property, icc_class);
	}

	// try {
	// this.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
    }

    private double getClassInstanceSize(URI classe) throws Exception {

	int number = this.getModel().filter(null, RDF.TYPE, classe)
		.subjects().size();
	List<Resource> subclasses = new ArrayList<Resource>();
	subclasses.addAll(this.getModel().filter(null, RDFS.SUBCLASSOF, classe)
		.subjects());

	while (!subclasses.isEmpty()) {

	    Resource currentClass = subclasses.get(0);
	    subclasses.remove(currentClass);
	    if (!(currentClass instanceof URI)) {
		continue;
	    }

	    number = number
		    + this.getModel().filter(null, RDF.TYPE, currentClass)
			    .subjects().size();
	    subclasses.addAll(this.getModel()
		    .filter(null, RDFS.SUBCLASSOF, currentClass).subjects());
	}

	return number;
    }

    private double getPropertyInstanceSize(URI property) throws Exception {

	int number = this.getModel().filter(null, property, null)
		.subjects().size();
	List<Resource> sub_properties = new ArrayList<Resource>();
	sub_properties.addAll(this.getModel().filter(null, RDFS.SUBPROPERTYOF, property)
		.subjects());

	while (!sub_properties.isEmpty()) {

	    Resource current_property = sub_properties.get(0);
	    sub_properties.remove(current_property);
	    if (!(current_property instanceof URI)) {
		continue;
	    }

	    number = number
		    + this.getModel().filter(null, property, null)
			    .subjects().size();
	    sub_properties.addAll(this.getModel()
		    .filter(null, RDFS.SUBPROPERTYOF, current_property).subjects());
	}

	return number;
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
	return repository;
    }

    public RepositoryConnection getRepositoryConnection() throws Exception {

	if (repositoryConnection == null)
	    repositoryConnection = repository.getConnection();
	return repositoryConnection;
    }

    @Override
    public void close() throws Exception {

	if (repository != null) {
	    if (repositoryConnection != null) {
		repositoryConnection.close();
	    }

	    repository.shutDown();
	}
    }

    @Override
    public void open() {
	if (!this.repository.isInitialized()) {
	    this.repository.initialize();
	}
	this.repositoryConnection = this.repository.getConnection();

    }

    public Model getModel() throws Exception {
	return model;
    }

    @Override
    public QueryStatement createStatement(String query) {
	return QueryStatementFactory.createQueryStatement(query, session);
    }

    @Override
    public double similarityMeasureClass(Node original_node, Node relaxed_node) {

	if(original_node.equals(relaxed_node)){
	    return 1.0;
	}
	
	URI original_class = null;
	URI relaxed_class = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_class = new URIImpl(original_node.getURI());
	} else {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_class = new URIImpl(relaxed_node.getURI());
	}

	try {
	    return conceptMeasure(original_class, relaxed_class);
	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    @Override
    public double similarityMeasureProperty(Node original_node, Node relaxed_node) {
	
	if(original_node.equals(relaxed_node)){
	    return 1.0;
	}

	URI original_property = null;
	URI relaxed_property = null;

	if (relaxed_node.isVariable()) {
	    return 0.0;
	}

	if (original_node.isURI()) {
	    original_property = new URIImpl(original_node.getURI());
	} else {
	    return literalOrValueMeasure(original_node, relaxed_node);
	}

	if (relaxed_node.isURI()) {
	    relaxed_property = new URIImpl(relaxed_node.getURI());
	}

	try {
	    return propertyMeasure(original_property, relaxed_property);
	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    private double conceptMeasure(URI original_class, URI relaxed_class)
	    throws Exception {

	URI least_common_class = getLeastCommonClassAncestor(original_class,
		relaxed_class);

	if (least_common_class == null) {
	    return 0;
	}

	double ic_lcc = 0;
	ic_lcc = information_content.get(least_common_class);

	double ic_class1 = 0;
	double ic_class2 = 0;
	ic_class1 = information_content.get(original_class);
	ic_class2 = information_content.get(relaxed_class);

	return ic_lcc / (ic_class1 + ic_class2 - ic_lcc);

    }

    private double propertyMeasure(URI original_property, URI relaxed_property) throws Exception {
	
	URI least_common_property = getLeastCommonPropertyAncestor(original_property,
		relaxed_property);

	if (least_common_property == null) {
	    return 0;
	}

	double ic_lcp = 0;
	ic_lcp = information_content.get(least_common_property);

	double ic_prop1 = 0;
	double ic_prop2 = 0;
	ic_prop1 = information_content.get(original_property);
	ic_prop2 = information_content.get(relaxed_property);

	return ic_lcp / (ic_prop1 + ic_prop2 - ic_lcp);

    }

    private URI getLeastCommonClassAncestor(URI original_class, URI relaxed_class)
	    throws Exception {

	List<Value> super_class_1 = new ArrayList<Value>();

	super_class_1.addAll(this.getModel()
		.filter(original_class, RDFS.SUBCLASSOF, null).objects());

	List<Value> super_class_2 = new ArrayList<Value>();

	super_class_2.addAll(this.getModel()
		.filter(relaxed_class, RDFS.SUBCLASSOF, null).objects());

	List<Value> new_super_class_1 = new ArrayList<Value>();
	List<Value> new_super_class_2 = new ArrayList<Value>();

	for (Value class_of_super_class_1 : super_class_1) {
	    for (Value class_of_super_class_2 : super_class_2) {
		/*
		 * if ((classe1_super_class.equals(relaxed_class)) ||
		 * (classe2_super_class.equals(original_class))) { continue; }
		 */
		if (class_of_super_class_1.equals(class_of_super_class_2)) {
		    return (URI) class_of_super_class_1;
		}

		if (URIUtil.isValidURIReference(class_of_super_class_2
			.stringValue())) {
		    new_super_class_2.addAll(this
			    .getModel()
			    .filter((URI) class_of_super_class_2, RDFS.SUBCLASSOF,
				    null).objects());
		}
	    }
	    if (URIUtil.isValidURIReference(class_of_super_class_1.stringValue())) {
		new_super_class_1.addAll(this
			.getModel()
			.filter((URI) class_of_super_class_1, RDFS.SUBCLASSOF,
				null).objects());
	    }
	}

	while ((!new_super_class_1.isEmpty()) || (!new_super_class_2.isEmpty())) {

	    for (Value class_of_new_super_class_1 : new_super_class_1) {
		for (Value class_of_super_class_2 : super_class_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_new_super_class_1.equals(class_of_super_class_2)
			    && (URIUtil.isValidURIReference(class_of_new_super_class_1
				    .stringValue()))) {
			return (URI) class_of_new_super_class_1;
		    } else {
			return null;
		    }

		}
	    }

	    for (Value class_of_super_class_1 : super_class_1) {
		for (Value class_of_new_super_class_2 : new_super_class_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (class_of_super_class_1.equals(class_of_new_super_class_2)
			    && (URIUtil.isValidURIReference(class_of_super_class_1
				    .stringValue()))) {
			return (URI) class_of_super_class_1;
		    } else {
			return null;

		    }
		}
	    }

	    List<Value> temp_super_class1 = new ArrayList<Value>();
	    List<Value> temp_super_class2 = new ArrayList<Value>();

	    for (Value class_of_new_super_class_1 : new_super_class_1) {
		for (Value class_of_new_super_class_2 : new_super_class_2) {
		    // if ((classe1_super_class.equals(relaxed_class))
		    // || (classe2_super_class.equals(original_class))) {
		    // continue;
		    // }
		    if (class_of_new_super_class_1.equals(class_of_new_super_class_2)) {
			return (URI) class_of_new_super_class_1;
		    }
		    if (URIUtil.isValidURIReference(class_of_new_super_class_2
			    .stringValue())) {
			temp_super_class2.addAll(this
				.getModel()
				.filter((URI) class_of_new_super_class_2,
					RDFS.SUBCLASSOF, null).objects());
		    }
		}
		if (URIUtil.isValidURIReference(class_of_new_super_class_1
			.stringValue())) {
		    temp_super_class1.addAll(this
			    .getModel()
			    .filter((URI) class_of_new_super_class_1, RDFS.SUBCLASSOF,
				    null).objects());
		}
	    }

	    super_class_1.addAll(new_super_class_1);
	    super_class_2.addAll(new_super_class_2);
	    new_super_class_1.clear();
	    new_super_class_2.clear();
	    new_super_class_1.addAll(temp_super_class1);
	    new_super_class_2.addAll(temp_super_class2);
	}
	return null;
    }

    private URI getLeastCommonPropertyAncestor(URI original_property,
	    URI relaxed_property) throws Exception {
	
	List<Value> super_property_1 = new ArrayList<Value>();

	super_property_1.addAll(this.getModel()
		.filter(original_property, RDFS.SUBPROPERTYOF, null).objects());

	List<Value> super_property_2 = new ArrayList<Value>();

	super_property_2.addAll(this.getModel()
		.filter(relaxed_property, RDFS.SUBPROPERTYOF, null).objects());

	List<Value> new_super_property_1 = new ArrayList<Value>();
	List<Value> new_super_property_2 = new ArrayList<Value>();

	for (Value property_of_super_property_1 : super_property_1) {
	    for (Value property_of_super_property_2 : super_property_2) {
		/*
		 * if ((classe1_super_class.equals(relaxed_class)) ||
		 * (classe2_super_class.equals(original_class))) { continue; }
		 */
		if (property_of_super_property_1.equals(property_of_super_property_2)) {
		    return (URI) property_of_super_property_1;
		}

		if (URIUtil.isValidURIReference(property_of_super_property_2
			.stringValue())) {
		    new_super_property_2.addAll(this
			    .getModel()
			    .filter((URI) property_of_super_property_2, RDFS.SUBCLASSOF,
				    null).objects());
		}
	    }
	    if (URIUtil.isValidURIReference(property_of_super_property_1.stringValue())) {
		new_super_property_1.addAll(this
			.getModel()
			.filter((URI) property_of_super_property_1, RDFS.SUBCLASSOF,
				null).objects());
	    }
	}

	while ((!new_super_property_1.isEmpty()) || (!new_super_property_2.isEmpty())) {

	    for (Value property_of_new_super_property_1 : new_super_property_1) {
		for (Value property_of_super_property_2 : super_property_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_new_super_property_1.equals(property_of_super_property_2)
			    && (URIUtil.isValidURIReference(property_of_new_super_property_1
				    .stringValue()))) {
			return (URI) property_of_new_super_property_1;
		    } else {
			return null;
		    }

		}
	    }

	    for (Value property_of_super_property_1 : super_property_1) {
		for (Value property_of_new_super_property_2 : new_super_property_2) {
		    /*
		     * if ((classe1_super_class.equals(relaxed_class)) ||
		     * (classe2_super_class.equals(original_class))) { continue;
		     * }
		     */
		    if (property_of_super_property_1.equals(property_of_new_super_property_2)
			    && (URIUtil.isValidURIReference(property_of_super_property_1
				    .stringValue()))) {
			return (URI) property_of_super_property_1;
		    } else {
			return null;

		    }
		}
	    }

	    List<Value> temp_super_class1 = new ArrayList<Value>();
	    List<Value> temp_super_class2 = new ArrayList<Value>();

	    for (Value property_of_new_super_property_1 : new_super_property_1) {
		for (Value property_of_new_super_property_2 : new_super_property_2) {
		    // if ((classe1_super_class.equals(relaxed_class))
		    // || (classe2_super_class.equals(original_class))) {
		    // continue;
		    // }
		    if (property_of_new_super_property_1.equals(property_of_new_super_property_2)) {
			return (URI) property_of_new_super_property_1;
		    }
		    if (URIUtil.isValidURIReference(property_of_new_super_property_2
			    .stringValue())) {
			temp_super_class2.addAll(this
				.getModel()
				.filter((URI) property_of_new_super_property_2,
					RDFS.SUBPROPERTYOF, null).objects());
		    }
		}
		if (URIUtil.isValidURIReference(property_of_new_super_property_1
			.stringValue())) {
		    temp_super_class1.addAll(this
			    .getModel()
			    .filter((URI) property_of_new_super_property_1, RDFS.SUBPROPERTYOF,
				    null).objects());
		}
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
	return 0;
    }

    @Override
    public boolean isopen() {

	return repository.isInitialized();
    }

    @Override
    public boolean isclose() {

	return !repository.isInitialized();
    }

}
