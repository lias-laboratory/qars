package fr.ensma.lias.qarscore.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    private Logger logger = Logger.getLogger(BenchmarkTest.class);

//    private static final String folder = "/home/lias/jena/tdbrepository";

//    private List<String> fullQueries = Arrays.asList("queries-chain.test", "queries-composite.test", "queries-star.test");
    
//    private List<String> oneQueries = Arrays.asList("queries-chain-scalability.test", "queries-composite-scalability.test", "queries-star-scalability.test");    

    private List<String> fullQueries = Arrays.asList("queries-star.test");
    
    private List<String> oneQueries = Arrays.asList("queries-star-scalability.test");    
    
    private List<String> repository = Arrays.asList("100", "250", "500", "750");
    
    private static final String folder = "/Users/baronm/Public/tdbrepository/tdbrepository";
    
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
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");
    }

    private void testTimePerformance(RelaxationStrategies relaxationStrategy, String queriesFilename, String repo) throws IOException {
	List<QueryExplain> newTestResultPairList = this.newTestResultPairList("/" + queriesFilename);
	ResultExplain newResultExplain = new ResultExplain(queriesFilename + repo + ".csv");
	
	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain.getQuery());

	    relaxationStrategy.getAllMFS(conjunctiveQuery);

	    int numberQueryExecuted = 0;
	    long entire_duration = 0;
	    for (int i = 0; i < 5; i++) {
		relaxationStrategy.getAllMFS(conjunctiveQuery);
		entire_duration = entire_duration
			+ ((AbstractLatticeStrategy) relaxationStrategy).duration_of_execution;
		numberQueryExecuted += ((AbstractLatticeStrategy) relaxationStrategy).number_of_query_executed;
	    }

	    logger.info(queryExplain.getDescription() + " " + (entire_duration / 5.0) + " " + (numberQueryExecuted / 5.0));
	    newResultExplain.add(queryExplain.getDescription(), entire_duration / 5.0, numberQueryExecuted / 5.0);
	}
	
	newResultExplain.generateReport();
    }
    
    private void latticeStrategy(String fullQuery, String oneQuery) throws IOException {	
	session = SessionFactory.getTDBSession(folder + repository.get(0));
	Assert.assertNotNull(session.getDataset());

	relaxationStrategy = StrategiesFactory.getLatticeDFSStrategy(session);
	testTimePerformance(relaxationStrategy, fullQuery, repository.get(0));
	session.close();
	
	for (int i = 1; i <= repository.size(); i++) {
	    session = SessionFactory.getTDBSession(folder + repository.get(i));
	    Assert.assertNotNull(session.getDataset());

	    relaxationStrategy = StrategiesFactory.getLatticeDFSStrategy(session);
	    testTimePerformance(relaxationStrategy, oneQuery, repository.get(i));	
	    
	    session.close();
	}
    }
    
    @Test
    public void startLatticeStrategyTest() throws IOException {
	for(int i = 0 ; i < fullQueries.size(); i++) {
	    latticeStrategy(fullQueries.get(i), oneQueries.get(i));
	}
    }
}
