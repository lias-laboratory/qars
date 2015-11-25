/**
 * 
 */
package fr.ensma.lias.qarscore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Geraud FOKOU
 *
 */
public class StatisticDataSetQueryTest {
	
	public static String LUBM_PREFIX = "PREFIX base: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl> "
									 + "PREFIX ub:   <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#> "
									 + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
									 + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
									 + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
									 + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";
	
	
	//Return the number of triplet in the dataset
	public static String NUMBER_TRIPLET = LUBM_PREFIX 
									+ "SELECT (COUNT(*) AS ?numberTriplet) "
									+ "WHERE { ?s ?p ?o "
									+ " }";

	//Return the number of Class in the dataset
	public static String NUMBER_CLASS = LUBM_PREFIX
									+ "SELECT (COUNT(DISTINCT ?classe) AS ?numberClasse) "
									+ "WHERE { ?instance rdf:type ?classe "
									+ "}";
	
	//Return the number of instance in the dataset
	public static String NUMBER_INSTANCE = LUBM_PREFIX
									+ "SELECT (COUNT(?instance) AS ?numberInstance) "
									+ "WHERE { ?instance rdf:type ?classe "
									+ "}";
	
	//Return the number of property in the dataset
	public static String NUMBER_PROPERTY = LUBM_PREFIX
									+ "SELECT (COUNT(DISTINCT ?property) AS ?numberProperty) "
									+ "WHERE { ?s ?property ?o "
									+ "}";
	
	//Return the number of instance by Class
	public static String NUMBER_INSTANCE_CLASS = LUBM_PREFIX
									+ "SELECT ?classe (COUNT(?instance) AS ?numberInstance)  "
									+ "WHERE {?instance rdf:type ?classe . }"
									+ "GROUP BY ?classe "
									+ "ORDER BY ?numberInstance ";
	
	//Return the number of Triplet for each property
	public static String NUMBER_TRIPLET_PROPERTY = LUBM_PREFIX
									+ "SELECT ?property (COUNT(?property) AS ?numberProperty)  "
									+ "WHERE { ?s ?property ?o  .} "
									+ "GROUP BY ?property "
									+ "ORDER BY ?numberProperty ";

	//List of Class in dataset
	public static String LIST_CLASS = LUBM_PREFIX
									+ "SELECT DISTINCT ?classe "
									+ "WHERE { ?instance rdf:type ?classe "
									+ "}";
	//List of Property
	public static String LIST_PROPERTY = LUBM_PREFIX
									+ "SELECT DISTINCT ?property "
									+ "WHERE { ?s ?property ?o "
									+ "}";
	
	public static Map<String, String> getAllQueries (){
		
		Map<String, String> allQueries = new HashMap<String, String>();
		
		allQueries.put("Number of triplet", NUMBER_TRIPLET);
		allQueries.put("Number of Class", NUMBER_CLASS);
		allQueries.put("Number of Instance", NUMBER_INSTANCE);
		allQueries.put("Number of Property", NUMBER_PROPERTY);
		allQueries.put("Number of Instance by class", NUMBER_INSTANCE_CLASS);
		allQueries.put("Number of Triplet by Porperty", NUMBER_TRIPLET_PROPERTY);
		allQueries.put("List of Class", LIST_CLASS);
		allQueries.put("List of Properties", LIST_PROPERTY);
		
		return allQueries;
	}
}
