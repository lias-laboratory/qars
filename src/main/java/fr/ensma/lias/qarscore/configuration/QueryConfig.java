/**
 * 
 */
package fr.ensma.lias.qarscore.configuration;

/**
 * @author Geraud FOKOU
 *
 */
public class QueryConfig {

	public static String RDF_PREFIX = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>";

	// List of super class
	public static String LIST_SUPER_CLASSES = RDF_PREFIX + "SELECT ?directsuperclasses ?classe "
			+ "WHERE { ?classe rdfs:subClassOf ?directsuperclasses "
			+ "FILTER(?directsuperclasses != ?classe && isIRI(?classe) && isIRI(?directsuperclasses)) " + "OPTIONAL { "
			+ "?classe rdfs:subClassOf ?othersuperclasse ." + "?othersuperclasse rdfs:subClassOf ?directsuperclasses ."
			+ "FILTER( ?othersuperclasse != ?classe && ?othersuperclasse != ?directsuperclasses )" + "} "
			+ "FILTER (!bound(?othersuperclasse) ) " + "}";

	// Return the number of instance by Class
	public static String NUMBER_INSTANCE_CLASS = RDF_PREFIX + "SELECT ?classe (COUNT(?instance) AS ?numberInstance)  "
			+ "WHERE {?instance rdf:type ?classe . }" + "GROUP BY ?classe ";

	// List of super property
	public static String LIST_SUPER_PROPERTIES = RDF_PREFIX + "SELECT ?directsuperproperty ?property "
			+ "WHERE { ?property rdfs:subPropertyOf ?directsuperproperty "
			+ "FILTER(?directsuperproperty != ?property && isIRI(?property) && isIRI(?directsuperproperty)) "
			+ "OPTIONAL { " + "?property rdfs:subPropertyOf ?othersuperproperty ."
			+ "?othersuperproperty rdfs:subPropertyOf ?directsuperproperty ."
			+ "FILTER( ?othersuperproperty != ?property && ?othersuperproperty != ?directsuperproperty )" + "} "
			+ "FILTER (!bound(?othersuperproperty) ) " + "}";

	// Return the number of Triplet for each property
	public static String NUMBER_TRIPLET_PROPERTY = RDF_PREFIX
			+ "SELECT ?property (COUNT(?property) AS ?numberProperty)  " + "WHERE { ?s ?property ?o  .} "
			+ "GROUP BY ?property ";

	// Return the number of triplet in the dataset
	public static String NUMBER_TRIPLET = RDF_PREFIX + "SELECT (COUNT(*) AS ?numberTriplet) " + "WHERE { ?s ?p ?o "
			+ " }";

	// Return the number of instance in the dataset
	public static String NUMBER_INSTANCE = RDF_PREFIX + "SELECT (COUNT(?instance) AS ?numberInstance) "
			+ "WHERE { ?instance rdf:type ?classe " + "}";

	// Return the number of Class in the dataset
	public static String NUMBER_CLASS = RDF_PREFIX + "SELECT (COUNT(DISTINCT ?classe) AS ?numberClasse) "
			+ "WHERE { ?instance rdf:type ?classe " + "}";

	// Return the number of property in the dataset
	public static String NUMBER_PROPERTY = RDF_PREFIX + "SELECT (COUNT(DISTINCT ?property) AS ?numberProperty) "
			+ "WHERE { ?s ?property ?o " + "}";

	// List of Class in dataset
	public static String LIST_CLASS = RDF_PREFIX + "SELECT DISTINCT ?classe " + "WHERE { ?instance rdf:type ?classe "
			+ "}";

	// List of Property
	public static String LIST_PROPERTY = RDF_PREFIX + "SELECT DISTINCT ?property " + "WHERE { ?s ?property ?o " + "}";
}
