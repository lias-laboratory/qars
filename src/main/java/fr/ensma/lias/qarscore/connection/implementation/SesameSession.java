package fr.ensma.lias.qarscore.connection.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.jena.ontology.OntModelSpec;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.nativerdf.NativeStore;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.DatasetOntologyMetaData;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;
import fr.ensma.lias.qarscore.loader.SesameBulkLoader;

/**
 * @author Mickael BARON
 * @author Geraud FOKOU
 */
public class SesameSession implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    /**
     * Url of the dataset
     */
    protected String url;

    /**
     * Data repository for sesame
     */
    private Repository repository;

    /**
     * Statistic of the ontology
     */
    protected DatasetOntologyMetaData ontologyStat;

    /**
     * Construct a Sesame Session if there isn't existed
     */
    public static Session getNativeSesameSession(String repositoryPath) {

	if (session != null) {
	    return session;
	}
	SesameSession sesameSession = new SesameSession();
	File dataDir = new File(repositoryPath);
	if (!dataDir.isDirectory()) {
	    throw new IllegalArgumentException(
		    "illegal parameter: Directory path expected");
	}
	sesameSession.repository = new SailRepository(new NativeStore(dataDir));
	sesameSession.url = repositoryPath;
	sesameSession.ontologyStat = DatasetOntologyMetaData
		.getInstance(sesameSession);
	session = sesameSession;
	return session;
    }

    public static Session getInMemorySesameSession(File[] datafiles,
	    String baseURI, String lang, OntModelSpec spec, boolean persist) {

	if (session != null) {
	    return session;
	}
	SesameSession sesameSession = new SesameSession();
	sesameSession.repository = SesameBulkLoader.loaderMemoryStore(
		datafiles, baseURI, lang, spec, persist);
	sesameSession.url = null;
	sesameSession.ontologyStat = DatasetOntologyMetaData
		.getInstance(sesameSession);
	session = sesameSession;
	return session;

    }

    /**
     * 
     */
    public SesameSession() {
    }

    /**
     * Construct a Sesame Session if there isn't existed
     */
    public static Session getInMemorySesameSession(File[] dataFiles,
	    String baseURI, String lang, OntModelSpec spec) {

	return getInMemorySesameSession(dataFiles, baseURI, lang, spec, false);
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
	return repository;
    }

    @Override
    public String getNameSession() {
	if (url != null) {
	    return url.substring(url.indexOf("/") + 1);
	}
	return null;
    }

    @Override
    public JSONResultSet executeSelectQuery(String query) {

	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	TupleQueryResultHandler writer = new SPARQLResultsJSONWriter(
		outputStream);
	TupleQuery sesameQuery = this.repository.getConnection()
		.prepareTupleQuery(query);
	sesameQuery.evaluate(writer);

	ByteArrayInputStream input = new ByteArrayInputStream(
		outputStream.toByteArray());

	return JSONResultSet.getJSONResultSet(input);
    }

    @Override
    public int getResultSize(String query) {

	JSONResultSet result = this.executeSelectQuery(query);
	return result.getBindings().size();
    }

    @Override
    public DatasetOntologyMetaData getOntology() {
	return ontologyStat;
    }

    @Override
    public InputStream executeConstructQuery(String query) {

	GraphQuery sesamequery = this.repository.getConnection()
		.prepareGraphQuery(query);
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, outputStream);
	sesamequery.evaluate(writer);

	return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
