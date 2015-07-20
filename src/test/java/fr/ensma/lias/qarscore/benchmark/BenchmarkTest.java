package fr.ensma.lias.qarscore.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.RelaxationStrategies;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.implementation.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.implementation.StrategiesFactory;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Mickael BARON
 */
public class BenchmarkTest {

    private Session session;
    
    private RelaxationStrategies relaxationStrategy;

    private Logger logger;
    
    private static final String QUERIES_STAR_FILE = "queries-star.test";
    
    class QueryExplain {

	protected int index;

	protected String description;
	
	protected String query;

	protected List<String> mfs;

	protected List<String> xss;
	
	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public List<String> getMfs() {
	    return mfs;
	}

	public List<String> getXss() {
	    return xss;
	}

	public QueryExplain() {
	    this.mfs = new ArrayList<String>();
	    this.xss = new ArrayList<String>();
	}

	public String getQuery() {
	    return query;
	}

	public void setQuery(String pQuery) {
	    this.query = pQuery;
	}

	public void addMFS(String mfs) {
	    this.mfs.add(mfs);
	}

	public void addXSS(String xss) {
	    this.xss.add(xss);
	}

	public void setIndex(int pIndex) {
	    this.index = pIndex;
	}

	public int getIndex() {
	    return this.index;
	}
    }

    protected List<QueryExplain> newTestResultPairList(final String filename) throws IOException {
	final List<QueryExplain> queries = new ArrayList<QueryExplain>();
	final URL fileUrl = BenchmarkTest.class.getResource(filename);
	final FileReader file = new FileReader(fileUrl.getFile());
	BufferedReader in = null;
	try {
	    in = new BufferedReader(file);
	    StringBuffer test = null;
	    StringBuffer mfsresult = null;
	    StringBuffer xssresult = null;

	    final Pattern pTest = Pattern.compile("# Test (\\w+) \\((.*)\\)");
	    final Pattern pMFS = Pattern.compile("# MFS (\\w+)");
	    final Pattern pXSS = Pattern.compile("# XSS (\\w+)");

	    String line;
	    int lineNumber = 0;

	    String testNumber = null;
	    String testName = null;
	    StringBuffer curbuf = null;

	    while ((line = in.readLine()) != null) {
		lineNumber++;
		final Matcher mTest = pTest.matcher(line);
		final Matcher mMFS = pMFS.matcher(line);
		final Matcher mXSS = pXSS.matcher(line);

		if (mTest.matches()) { // # Test
		    addTestResultPair(queries, test, mfsresult, xssresult, testNumber, testName);
		    
		    testNumber = mTest.group(1);
		    testName = mTest.group(2);

		    test = new StringBuffer();
		    mfsresult = new StringBuffer();
		    xssresult = new StringBuffer();

		    curbuf = test;
		} else if (mMFS.matches()) { // # Result
		    if (testNumber == null) {
			throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
		    }
		    final String resultNumber = mMFS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException(
				"Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
		    }

		    curbuf = mfsresult;
		} else if (mXSS.matches()) {
		    if (testNumber == null) {
			throw new RuntimeException("Test file has result without a test (line " + lineNumber + ")");
		    }
		    final String resultNumber = mXSS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException(
				"Result " + resultNumber + " test " + testNumber + " (line " + lineNumber + ")");
		    }

		    curbuf = xssresult;
		} else {
		    line = line.trim();
		    if (!line.isEmpty()) {
			curbuf.append(line);
			curbuf.append("\n");			
		    }
		}
	    }

	    addTestResultPair(queries, test, mfsresult, xssresult, testNumber, testName);

	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (final IOException e) {
		}
	    }
	}
	
	return queries;
    }

    private void addTestResultPair(List<QueryExplain> queries, StringBuffer query, StringBuffer mfsResult,
	    StringBuffer xssResult, String number, String description) throws IOException {
	if (query == null || mfsResult == null || xssResult == null) {
	    return;
	}

	QueryExplain currentQuery = new QueryExplain();
	currentQuery.setQuery(query.toString().trim());
	currentQuery.setIndex(Integer.valueOf(number));
	currentQuery.setDescription(description.trim());
	
	BufferedReader bufReader = new BufferedReader(new StringReader(mfsResult.toString()));
	String line = null;
	while ((line = bufReader.readLine()) != null) {
	    currentQuery.addMFS(line.trim());
	}
	
	bufReader = new BufferedReader(new StringReader(xssResult.toString()));
	line = null;
	while ((line = bufReader.readLine()) != null) {
	    currentQuery.addXSS(line.trim());
	}
	
	queries.add(currentQuery);
    }
        
    @Before
    public void setUp() {
	logger = Logger.getRootLogger();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM100");
	Assert.assertNotNull(session.getDataset());
	Assert.assertNotNull(session.getModel());
	Assert.assertNotNull(session.getOntologyModel());
	Assert.assertNull(session.getDataStore());
	Assert.assertNotNull(session.getBaseModel());	
    }

    /**
     * test indicator
     */
    private void show_indicator(){
	logger.info("Time Duration of MFS Computation: "+((AbstractLatticeStrategy)relaxationStrategy).duration_of_execution);
	logger.info("Number of Executed queries: "+((AbstractLatticeStrategy)relaxationStrategy).number_of_query_executed);
	logger.info("Number of redundant queries: "+((AbstractLatticeStrategy)relaxationStrategy).number_of_query_reexecuted);
	logger.info("Number of Cartesian Product: "+((AbstractLatticeStrategy)relaxationStrategy).size_of_cartesian_product);
    }
    
    @Test
    public void latticeStrategyTest() throws IOException {
	List<QueryExplain> newTestResultPairList = this.newTestResultPairList("/" + QUERIES_STAR_FILE);
	
	relaxationStrategy = StrategiesFactory.getLatticeStrategy(session, false);
	
	for (QueryExplain queryExplain : newTestResultPairList) {
	    testTimePerformance(relaxationStrategy, queryExplain.getQuery());	    
	}
    }
    
    private void testTimePerformance(RelaxationStrategies relaxationStrategy, String query) {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(query);
	
	relaxationStrategy.getAllMFS(conjunctiveQuery);
	
	long entire_duration = 0;
	for (int i = 0; i < 5; i++) {
	    relaxationStrategy.getAllMFS(conjunctiveQuery);
	    entire_duration = entire_duration + ((AbstractLatticeStrategy)relaxationStrategy).duration_of_execution;
	}
	
	show_indicator();
	logger.info(entire_duration);
    }
}
