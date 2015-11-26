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

import org.apache.jena.ontology.OntModelSpec;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.MFSSearch;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation.AbstractLatticeStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.GraphRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.HuangRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.INCFULLMFSRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.MFSRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.MFSUpdateRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategies.SYSFULLMFSRelaxationStrategy;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Mickael BARON
 * @author Geraud FOKOU
 */
public class BenchmarkStrategiesTest extends InitTest{

    /**
     * set test parameter
     */
    private static final int NB_EXEC = 1;
    private final int TOP_K = 10;
    /**
     * set session and other tools
     */
    public MFSSearch relaxationStrategy;

    /**
     * looger tools
     */
    private Logger logger = Logger.getLogger(BenchmarkStrategiesTest.class);
    private static PatternLayout layout;
    private FileAppender fileAppender;

    /**
     * Set queries files
     */
    private final String QUERIES_STAR_FILE = "queries-star.test";
    private final String QUERIES_CHAIN_FILE = "queries-chain.test";
    private final String QUERIES_COMPOSITE_FILE = "queries-composite.test";
    private final String QUERIES_HUANG_FILE = "queries-huang.test";

    // private final String QUERIES_SCALE_STAR_FILE =
    // "queries-star-scalability.test";
    // private final String QUERIES_SCALE_CHAIN_FILE =
    // "queries-chain-scalability.test";
    // private final String QUERIES_SCALE_COMPOSITE_FILE =
    // "queries-composite-scalability.test";

    /**
     * test tools
     */
    private List<QueryExplain> newTestResultPairList = null;
    private ResultExplain newResultExplain = null;

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

    protected List<QueryExplain> newTestResultPairList(final String filename)
	    throws IOException {
	final List<QueryExplain> queries = new ArrayList<QueryExplain>();
	final URL fileUrl = BenchmarkStrategiesTest.class.getResource(filename);
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
		    addTestResultPair(queries, test, mfsresult, xssresult,
			    testNumber, testName);

		    testNumber = mTest.group(1);
		    testName = mTest.group(2);

		    test = new StringBuffer();
		    mfsresult = new StringBuffer();
		    xssresult = new StringBuffer();

		    curbuf = test;
		} else if (mMFS.matches()) { // # Result
		    if (testNumber == null) {
			throw new RuntimeException(
				"Test file has result without a test (line "
					+ lineNumber + ")");
		    }
		    final String resultNumber = mMFS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException("Result " + resultNumber
				+ " test " + testNumber + " (line "
				+ lineNumber + ")");
		    }

		    curbuf = mfsresult;
		} else if (mXSS.matches()) {
		    if (testNumber == null) {
			throw new RuntimeException(
				"Test file has result without a test (line "
					+ lineNumber + ")");
		    }
		    final String resultNumber = mXSS.group(1);
		    if (!testNumber.equals(resultNumber)) {
			throw new RuntimeException("Result " + resultNumber
				+ " test " + testNumber + " (line "
				+ lineNumber + ")");
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

	    addTestResultPair(queries, test, mfsresult, xssresult, testNumber,
		    testName);

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

    private void addTestResultPair(List<QueryExplain> queries,
	    StringBuffer query, StringBuffer mfsResult, StringBuffer xssResult,
	    String number, String description) throws IOException {
	if (query == null || mfsResult == null || xssResult == null) {
	    return;
	}

	QueryExplain currentQuery = new QueryExplain();
	currentQuery.setQuery(query.toString().trim());
	currentQuery.setIndex(Integer.valueOf(number));
	currentQuery.setDescription(description.trim());

	BufferedReader bufReader = new BufferedReader(new StringReader(
		mfsResult.toString()));
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

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(tdb_path);
	layout = new PatternLayout();
	String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
	layout.setConversionPattern(conversionPattern);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void testRelaxationWithHuangStrategy() {

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    long begin = System.currentTimeMillis();
	    HuangRelaxationStrategy relaxed_query = new HuangRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    boolean hasTopk = false;
	    int number_answers = 0;
	    int number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = stm.getResultSetSize();
		number_answers = number_answers + query_answers_size;
		hasTopk = number_answers >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ relaxed_query.getCurrent_level() + " "
			+ query_answers_size);

	    }
	    long end = System.currentTimeMillis();
	    float duration = ((float) (end - begin));
	    logger.info(number_relaxed_queries + " " + duration + " "
		    + number_answers);
	    /*
	     * newResultExplain.add(queryExplain.getDescription(), duration /
	     * NB_EXEC, number_relaxed_queries / NB_EXEC);
	     */
	}

	try {
	    newResultExplain.generateReport();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void testRelaxationWithGraphStrategy() {

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    long begin = System.currentTimeMillis();
	    GraphRelaxationStrategy relaxed_query = new GraphRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    boolean hasTopk = false;
	    int number_answers = 0;
	    int number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = stm.getResultSetSize();
		number_answers = number_answers + query_answers_size;
		hasTopk = number_answers >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ relaxed_query.getCurrent_level() + " "
			+ query_answers_size);
	    }

	    long end = System.currentTimeMillis();
	    long duration = end - begin;
	    logger.info(number_relaxed_queries + " " + duration + " "
		    + number_answers);
	}

	try {
	    newResultExplain.generateReport();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void testRelaxationWithMFSStrategy() {

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    long begin = System.currentTimeMillis();
	    MFSRelaxationStrategy relaxed_query = new MFSRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    boolean hasTopk = false;
	    int number_answers = 0;
	    int number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = stm.getResultSetSize();
		number_answers = number_answers + query_answers_size;
		hasTopk = number_answers >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(
			relaxed_query.getCurrent_relaxed_query().toString()
				+ " " + relaxed_query.getCurrent_similarity()
				+ " " + relaxed_query.getCurrent_level() + " "
				+ query_answers_size);
	    }

	    long end = System.currentTimeMillis();
	    float duration = ((float) (end - begin));
	    int number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    long duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    logger.info(
		    number_queries_mfs + " " + duration_mfs_search + " "
			    + number_relaxed_queries + " " + duration + " "
			    + number_answers);

	    newResultExplain.add(queryExplain.getDescription(), duration
		    / NB_EXEC, number_relaxed_queries / NB_EXEC);
	}

	try {
	    newResultExplain.generateReport();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void testRelaxationWithUpdateMFSStrategy() {

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    long begin = System.currentTimeMillis();
	    MFSUpdateRelaxationStrategy relaxed_query = new MFSUpdateRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    boolean hasTopk = false;
	    int number_answers = 0;
	    int number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = stm.getResultSetSize();
		number_answers = number_answers + query_answers_size;
		hasTopk = number_answers >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(
			relaxed_query.getCurrent_relaxed_query().toString()
				+ " " + relaxed_query.getCurrent_similarity()
				+ " " + relaxed_query.getCurrent_level() + " "
				+ query_answers_size);
	    }

	    long end = System.currentTimeMillis();
	    float duration = ((float) (end - begin));
	    int number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    long duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    int number_check_queries = relaxed_query.number_check_queries;
	    logger.info(
		    number_check_queries + " " + number_queries_mfs + " "
			    + duration_mfs_search + " "
			    + number_relaxed_queries + " " + duration + " "
			    + number_answers);

	    newResultExplain.add(queryExplain.getDescription(), duration
		    / NB_EXEC, number_relaxed_queries / NB_EXEC);
	}

	try {
	    newResultExplain.generateReport();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void testRelaxationWithFullIncMFSStrategy() {

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    long begin = System.currentTimeMillis();
	    INCFULLMFSRelaxationStrategy relaxed_query = new INCFULLMFSRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    boolean hasTopk = false;
	    int number_answers = 0;
	    int number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = stm.getResultSetSize();
		number_answers = number_answers + query_answers_size;
		hasTopk = number_answers >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(
			relaxed_query.getCurrent_relaxed_query().toString()
				+ " " + relaxed_query.getCurrent_similarity()
				+ " " + relaxed_query.getCurrent_level() + " "
				+ query_answers_size);
	    }

	    long end = System.currentTimeMillis();
	    float duration = ((float) (end - begin));
	    int number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    long duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    int number_check_queries = relaxed_query.number_check_queries;
	    logger.info(
		    number_check_queries + " " + number_queries_mfs + " "
			    + duration_mfs_search + " "
			    + number_relaxed_queries + " " + duration + " "
			    + number_answers);

	    newResultExplain.add(queryExplain.getDescription(), duration
		    / NB_EXEC, number_relaxed_queries / NB_EXEC);
	}

	try {
	    newResultExplain.generateReport();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void testRelaxationWithFullSysMFSStrategy() {

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    long begin = System.currentTimeMillis();
	    SYSFULLMFSRelaxationStrategy relaxed_query = new SYSFULLMFSRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    boolean hasTopk = false;
	    int number_answers = 0;
	    int number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = stm.getResultSetSize();
		number_answers = number_answers + query_answers_size;
		hasTopk = number_answers >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(
			relaxed_query.getCurrent_relaxed_query().toString()
				+ " " + relaxed_query.getCurrent_similarity()
				+ " " + relaxed_query.getCurrent_level() + " "
				+ query_answers_size);
	    }

	    long end = System.currentTimeMillis();
	    float duration = ((float) (end - begin));
	    int number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    long duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    int number_check_queries = relaxed_query.number_check_queries;
	    logger.info(
		    number_check_queries + " " + number_queries_mfs + " "
			    + duration_mfs_search + " "
			    + number_relaxed_queries + " " + duration + " "
			    + number_answers);

	    newResultExplain.add(queryExplain.getDescription(), duration
		    / NB_EXEC, number_relaxed_queries / NB_EXEC);
	}

	try {
	    newResultExplain.generateReport();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /*******************************
     * Experiments for LUBM
     ********************************/

    @SuppressWarnings("deprecation")
    @Test
    public void testStarLUBM() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_STAR_FILE);

	/***********************************
	 * Huang relaxation strategy test
	 ********************************/

	String logfile = "exp-" + "star" + "-" + "Huang_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);


	testRelaxationWithHuangStrategy();

	/***********************************
	 * Graph relaxation strategy test
	 ********************************/

	logfile = "exp-" + "star" + "-" + "Graph_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);


	testRelaxationWithGraphStrategy();

	/***********************************
	 * MFS relaxation strategy test
	 ********************************/

	logfile = "exp-" + "star" + "-" + "MFS_relaxation" + "-" + "Jena" + "-"
		+ "lubm" + tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithMFSStrategy();
	
	/***********************************
	 * UPDATE MFS relaxation strategy test
	 ********************************/

	logfile = "exp-" + "star" + "-" + "UPDATE_MFS_relaxation" + "-" + "Jena" + "-"
		+ "lubm" + tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithUpdateMFSStrategy();

	/***********************************
	 * FULL INC MFS relaxation strategy test
	 ********************************/

	logfile = "exp-" + "star" + "-" + "FULL_INC_MFS_relaxation" + "-" + "Jena" + "-"
		+ "lubm" + tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithFullIncMFSStrategy();

	/***********************************
	 * FULL SYS MFS relaxation strategy test
	 ********************************/

	logfile = "exp-" + "star" + "-" + "FULL_SYS_MFS_relaxation" + "-" + "Jena" + "-"
		+ "lubm" + tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithFullIncMFSStrategy();

    }

    @SuppressWarnings("deprecation")
    @Test
    public void testChainLUBM() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_CHAIN_FILE);

	/***********************************
	 * Huang relaxation strategy test
	 ********************************/

	String fileCSV = "exp-" + "chain" + "-" + "Huang_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".csv";

	String logfile = "exp-" + "chain" + "-" + "Huang_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	newResultExplain = new ResultExplain(fileCSV);

	testRelaxationWithHuangStrategy();

	/***********************************
	 * Graph relaxation strategy test
	 ********************************/

	fileCSV = "exp-" + "chain" + "-" + "Graph_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".csv";

	logfile = "exp-" + "chain" + "-" + "Graph_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	newResultExplain = new ResultExplain(fileCSV);

	testRelaxationWithGraphStrategy();

	/***********************************
	 * MFS relaxation strategy test
	 ********************************/

	fileCSV = "exp-" + "chain" + "-" + "MFS_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".csv";

	logfile = "exp-" + "chain" + "-" + "MFS_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	newResultExplain = new ResultExplain(fileCSV);

	testRelaxationWithMFSStrategy();

    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCompositeLUBM() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_COMPOSITE_FILE);

	/***********************************
	 * Huang relaxation strategy test
	 ********************************/

	String fileCSV = "exp-" + "composite" + "-" + "Huang_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".csv";

	String logfile = "exp-" + "composite" + "-" + "Huang_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	newResultExplain = new ResultExplain(fileCSV);

	testRelaxationWithHuangStrategy();

	/***********************************
	 * Graph relaxation strategy test
	 ********************************/

	fileCSV = "exp-" + "composite" + "-" + "Graph_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".csv";

	logfile = "exp-" + "composite" + "-" + "Graph_relaxation" + "-"
		+ "Jena" + "-" + "lubm"
		+ tdb_alias + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	newResultExplain = new ResultExplain(fileCSV);

	testRelaxationWithGraphStrategy();

	/***********************************
	 * MFS relaxation strategy test
	 ********************************/

	fileCSV = "exp-" + "composite" + "-" + "MFS_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".csv";

	logfile = "exp-" + "composite" + "-" + "MFS_relaxation" + "-" + "Jena"
		+ "-" + "lubm" + tdb_alias
		+ ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setImmediateFlush(false);
	fileAppender.setThreshold(Priority.DEBUG);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	newResultExplain = new ResultExplain(fileCSV);

	testRelaxationWithMFSStrategy();
    }

    public void testTimePerformance(MFSSearch relaxationStrategy,
	    String queriesFilename, String repo) throws IOException {

	List<QueryExplain> newTestResultPairList = this
		.newTestResultPairList("/" + queriesFilename);
	ResultExplain newResultExplain = new ResultExplain(queriesFilename
		+ repo + ".csv");

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    relaxationStrategy.getAllMFS(conjunctiveQuery);

	    int numberQueryExecuted = 0;
	    long entire_duration = 0;
	    for (int i = 0; i < NB_EXEC; i++) {
		relaxationStrategy.getAllMFS(conjunctiveQuery);
		entire_duration = entire_duration
			+ ((AbstractLatticeStrategy) relaxationStrategy).duration_of_execution;
		numberQueryExecuted += ((AbstractLatticeStrategy) relaxationStrategy).number_of_query_executed;
	    }

	    logger.info(queryExplain.getDescription() + " "
		    + (entire_duration / NB_EXEC) + " "
		    + (numberQueryExecuted / NB_EXEC));
	    newResultExplain.add(queryExplain.getDescription(), entire_duration
		    / NB_EXEC, numberQueryExecuted / NB_EXEC);
	}

	newResultExplain.generateReport();
    }
}
