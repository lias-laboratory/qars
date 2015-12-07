package fr.ensma.lias.qarscore.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.benchmark.result.ResultStrategyExplain;
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

/**
 * @author Mickael BARON
 * @author Geraud FOKOU
 */
public class BenchmarkStrategiesTest extends InitTest {

    /**
     * set test parameter
     */
    private final int TOP_K = 10;
    private int time_multiple = 1;
    private String time_value;
    /**
     * set session and other tools
     */
    public MFSSearch relaxationStrategy;

    /**
     * looger tools
     */
    private Logger logger = Logger.getLogger(BenchmarkStrategiesTest.class);
    private PatternLayout layout;
    private FileAppender fileAppender;

    /**
     * Set queries files
     */
    private final static Map<String, String> QUERIES_TYPE_FILE;
    static {
	QUERIES_TYPE_FILE = new HashMap<String, String>();
	QUERIES_TYPE_FILE.put("star", "queries-star.test");
	QUERIES_TYPE_FILE.put("chain", "queries-chain.test");
	QUERIES_TYPE_FILE.put("composite", "queries-composite.test");
	QUERIES_TYPE_FILE.put("huang", "queries-huang.test");
	QUERIES_TYPE_FILE.put("mixed", "queries-mixed-II.test");
	QUERIES_TYPE_FILE.put("mixed_II", "queries-mixed.test");
	QUERIES_TYPE_FILE.put("one", "queries-mixed-one.test");
    }

    private String current_query_set = "one";

    /**
     * test tools
     */
    private LinkedHashMap<String, Double> solutions = new LinkedHashMap<String, Double>(TOP_K);
    private List<QueryExplain> newTestResultPairList = null;
    private ResultStrategyExplain newResultExplain = null;
    String fileCSV = "exp-relaxation-strategy-Jena-lubm-tdb_alias.csv";

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

    @Before
    public void setUp() {
	super.setUp();
	layout = new PatternLayout();
	String conversionPattern = "%-5p [%C{1}]: %m%n";
	// String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
	layout.setConversionPattern(conversionPattern);
	LocalDateTime time = LocalDateTime.now();
	time_value = "" + time.getDayOfMonth() + time.getMonthValue()
		+ time.getHour() + time.getMinute() + time.getSecond();
    }

    @After
    public void tearDown() throws Exception {
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void addResult(ResultSet results, double sim){
	
	if (results == null) {
	    return ;
	}
	
	try {
	    while ((results.hasNext())&&(solutions.size()<TOP_K)) {
		QuerySolution sol = results.nextSolution();
		solutions.put(sol.toString(), sim);
		logger.info(sol.toString());
	    }
	} finally {
	}

    }
    
    private void testRelaxationWithHuangStrategy() {

	long begin_query, begin, end_query, end;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration;
	double duration_mfs_search;

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    logger.info("**************************Begin QUERY "
		    + queryExplain.description
		    + "***********************************");
	    begin = System.currentTimeMillis();
	    HuangRelaxationStrategy relaxed_query = new HuangRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    hasTopk = false;
	    number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {

		begin_query = System.currentTimeMillis();
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = solutions.size();
		this.addResult((ResultSet) stm.executeQuery(), relaxed_query.getCurrent_similarity());
		query_answers_size = solutions.size() - query_answers_size;
		end_query = System.currentTimeMillis();
		
		hasTopk = solutions.size() >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ relaxed_query.getCurrent_level() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));
	    }
	    end = System.currentTimeMillis();
	    duration = ((float) (end - begin));
	    logger.info(number_relaxed_queries + " " + duration + " "
		    + solutions.size());
	    logger.info("**************************End QUERY "
		    + queryExplain.description
		    + "***********************************");
	    duration_mfs_search = 0.0;
	    number_check_queries = relaxed_query.number_check_queries;
	    number_queries_mfs = 0;
	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search, duration - duration_mfs_search,
		    number_check_queries + number_queries_mfs
			    + number_relaxed_queries, number_queries_mfs
			    + number_check_queries, number_relaxed_queries);

	}
    }

    private void testRelaxationWithGraphStrategy() {

	long begin_query, begin, end_query, end;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration;
	double duration_mfs_search;

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    logger.info("**************************Begin QUERY "
		    + queryExplain.description
		    + "***********************************");
	    begin = System.currentTimeMillis();
	    GraphRelaxationStrategy relaxed_query = new GraphRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    hasTopk = false;
	    number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {

		begin_query = System.currentTimeMillis();
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = solutions.size();
		this.addResult((ResultSet) stm.executeQuery(), relaxed_query.getCurrent_similarity());
		query_answers_size = solutions.size() - query_answers_size;
		end_query = System.currentTimeMillis();

		hasTopk = solutions.size() >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ relaxed_query.getCurrent_level() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));
	    }

	    end = System.currentTimeMillis();
	    duration = ((float) (end - begin));
	    logger.info(number_relaxed_queries + " " + duration + " "
		    + solutions.size());
	    logger.info("**************************End QUERY "
		    + queryExplain.description
		    + "***********************************");

	    duration_mfs_search = 0.0;
	    number_queries_mfs = 0;
	    number_check_queries = relaxed_query.number_check_queries;
	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search, duration - duration_mfs_search,
		    number_check_queries + number_queries_mfs
			    + number_relaxed_queries, number_queries_mfs
			    + number_check_queries, number_relaxed_queries);

	}
    }

    private void testRelaxationWithMFSStrategy() {

	long begin_query, begin, end_query, end;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration;
	double duration_mfs_search;

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    logger.info("**************************Begin QUERY "
		    + queryExplain.description
		    + "***********************************");
	    begin = System.currentTimeMillis();
	    MFSRelaxationStrategy relaxed_query = new MFSRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    hasTopk = false;
	    number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {

		begin_query = System.currentTimeMillis();
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = solutions.size();
		this.addResult((ResultSet) stm.executeQuery(), relaxed_query.getCurrent_similarity());
		query_answers_size = solutions.size() - query_answers_size;

		end_query = System.currentTimeMillis();

		hasTopk = solutions.size() >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ relaxed_query.getCurrent_level() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));
	    }

	    end = System.currentTimeMillis();
	    duration = ((float) (end - begin));
	    number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    logger.info(number_queries_mfs + " " + duration_mfs_search + " "
		    + number_relaxed_queries + " " + duration + " "
		    + solutions.size());
	    logger.info("**************************End QUERY "
		    + queryExplain.description
		    + "***********************************");

	    number_check_queries = relaxed_query.number_check_queries;
	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search, duration - duration_mfs_search,
		    number_check_queries + number_queries_mfs
			    + number_relaxed_queries, number_queries_mfs
			    + number_check_queries, number_relaxed_queries);
	}
    }

    private void testRelaxationWithUpdateMFSStrategy() {

	long begin_query, begin, end_query, end;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration;
	double duration_mfs_search;

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    logger.info("**************************Begin QUERY "
		    + queryExplain.description
		    + "***********************************");
	    begin = System.currentTimeMillis();
	    MFSUpdateRelaxationStrategy relaxed_query = new MFSUpdateRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    hasTopk = false;
	    number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {

		begin_query = System.currentTimeMillis();
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = solutions.size();
		this.addResult((ResultSet) stm.executeQuery(), relaxed_query.getCurrent_similarity());
		query_answers_size = solutions.size() - query_answers_size;
		end_query = System.currentTimeMillis();

		hasTopk = solutions.size() >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ relaxed_query.getCurrent_level() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));
	    }

	    end = System.currentTimeMillis();
	    duration = ((float) (end - begin));
	    number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    number_check_queries = relaxed_query.number_check_queries;
	    logger.info(number_check_queries + " " + number_queries_mfs + " "
		    + duration_mfs_search + " " + number_relaxed_queries + " "
		    + duration + " " + solutions.size());
	    logger.info("**************************End QUERY "
		    + queryExplain.description
		    + "***********************************");

	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search, duration - duration_mfs_search,
		    number_check_queries + number_queries_mfs
			    + number_relaxed_queries, number_queries_mfs
			    + number_check_queries, number_relaxed_queries);
	}
    }

    private void testRelaxationWithFullIncMFSStrategy() {

	long begin_query, begin, end_query, end;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration;
	double duration_mfs_search;

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    logger.info("**************************Begin QUERY "
		    + queryExplain.description
		    + "***********************************");
	    begin = System.currentTimeMillis();
	    INCFULLMFSRelaxationStrategy relaxed_query = new INCFULLMFSRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    hasTopk = false;
	    number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {

		begin_query = System.currentTimeMillis();
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());
		int query_answers_size = solutions.size();
		this.addResult((ResultSet) stm.executeQuery(), relaxed_query.getCurrent_similarity());
		query_answers_size = solutions.size() - query_answers_size;
		end_query = System.currentTimeMillis();

		hasTopk = solutions.size() >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));
	    }

	    end = System.currentTimeMillis();
	    duration = ((float) (end - begin));
	    number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    number_check_queries = relaxed_query.number_check_queries;
	    logger.info(number_check_queries + " " + number_queries_mfs + " "
		    + duration_mfs_search + " " + number_relaxed_queries + " "
		    + duration + " " + solutions.size());
	    logger.info("**************************End QUERY "
		    + queryExplain.description
		    + "***********************************");

	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search, duration - duration_mfs_search,
		    number_check_queries + number_queries_mfs
			    + number_relaxed_queries, number_queries_mfs
			    + number_check_queries, number_relaxed_queries);
	}

    }

    private void testRelaxationWithFullSysMFSStrategy() {

	long begin_query, begin, end_query, end;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration;
	double duration_mfs_search;

	for (QueryExplain queryExplain : newTestResultPairList) {
	    CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());

	    logger.info("**************************Begin QUERY "
		    + queryExplain.description
		    + "***********************************");
	    begin = System.currentTimeMillis();
	    SYSFULLMFSRelaxationStrategy relaxed_query = new SYSFULLMFSRelaxationStrategy(
		    conjunctiveQuery, sessionJena);
	    relaxed_query.begin_relax_process();
	    hasTopk = false;
	    number_relaxed_queries = 0;
	    while ((!hasTopk) && (relaxed_query.hasNext())) {

		begin_query = System.currentTimeMillis();
		QueryStatement stm = sessionJena.createStatement(relaxed_query
			.next().toString());		
		int query_answers_size = solutions.size();
		this.addResult((ResultSet) stm.executeQuery(), relaxed_query.getCurrent_similarity());
		query_answers_size = solutions.size() - query_answers_size;

		end_query = System.currentTimeMillis();

		hasTopk = solutions.size() >= TOP_K;

		number_relaxed_queries = number_relaxed_queries + 1;
		logger.info(relaxed_query.getCurrent_relaxed_query().toString()
			+ " " + relaxed_query.getCurrent_similarity() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));
	    }

	    end = System.currentTimeMillis();
	    duration = ((float) (end - begin));
	    number_queries_mfs = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).number_of_query_executed;
	    duration_mfs_search = ((AbstractLatticeStrategy) relaxed_query
		    .getMFSSearchEngine()).duration_of_execution;
	    number_check_queries = relaxed_query.number_check_queries;

	    logger.info(number_check_queries + " " + number_queries_mfs + " "
		    + duration_mfs_search + " " + number_relaxed_queries + " "
		    + duration + " " + solutions.size());
	    logger.info("**************************End QUERY "
		    + queryExplain.description
		    + "***********************************");

	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search, duration - duration_mfs_search,
		    number_check_queries + number_queries_mfs
			    + number_relaxed_queries, number_queries_mfs + number_check_queries,
		    number_relaxed_queries);
	}
    }

    /************************
     * Experiments for LUBM *
     ************************/

    @Test
    public void testLUBM_Huang() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_TYPE_FILE.get(current_query_set));

	String fileCSV = "exp-" + current_query_set
		+ "-Huang_relaxation-strategy-lubm-" + tdb_alias + "-"
		+ time_value + ".csv";
	newResultExplain = new ResultStrategyExplain(fileCSV, time_multiple);

	/**********************************
	 * Huang relaxation strategy test *
	 *********************************/

	String logfile = "exp-" + "Huang_relaxation" + "-" + "lubm" + tdb_alias
		+ "-" + time_value + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.removeAllAppenders();
	logger.addAppender(fileAppender);

	testRelaxationWithHuangStrategy();

	newResultExplain.generateReport();
    }

//    @Test
    public void testLUBM_Graph() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_TYPE_FILE.get(current_query_set));

	String fileCSV = "exp-" + current_query_set
		+ "-Graph_relaxation-strategy-Jena-lubm-" + tdb_alias + "-"
		+ time_value + ".csv";
	newResultExplain = new ResultStrategyExplain(fileCSV, time_multiple);

	/**********************************
	 * Graph relaxation strategy test *
	 **********************************/

	String logfile = "exp-" + "Graph_relaxation" + "-" + "lubm" + tdb_alias
		+ "-" + time_value + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.removeAllAppenders();
	logger.addAppender(fileAppender);

	testRelaxationWithGraphStrategy();

	newResultExplain.generateReport();
    }

//    @Test
    public void testLUBM_MFS() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_TYPE_FILE.get(current_query_set));

	String fileCSV = "exp-" + current_query_set
		+ "-MFS_relaxation-strategy-Jena-lubm-" + tdb_alias + "-"
		+ time_value + ".csv";
	newResultExplain = new ResultStrategyExplain(fileCSV, time_multiple);

	/********************************
	 * MFS relaxation strategy test *
	 ********************************/
	String logfile = "exp-" + "MFS_relaxation" + "-" + "lubm" + tdb_alias
		+ "-" + time_value + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithMFSStrategy();

	newResultExplain.generateReport();
    }

//    @Test
    public void testLUBM_MFSUpdate() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_TYPE_FILE.get(current_query_set));

	String fileCSV = "exp-" + current_query_set
		+ "-MFSUPDATE_relaxation-strategy-Jena-lubm-" + tdb_alias + "-"
		+ time_value + ".csv";
	newResultExplain = new ResultStrategyExplain(fileCSV, time_multiple);

	/***************************************
	 * MFS UPDATE relaxation strategy test *
	 ***************************************/
	String logfile = "exp-" + "MFSUPDATE_relaxation" + "-" + "lubm"
		+ tdb_alias + "-" + time_value + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithUpdateMFSStrategy();

	newResultExplain.generateReport();
    }

//    @Test
    public void testLUBM_MFSFULLINC() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_TYPE_FILE.get(current_query_set));

	String fileCSV = "exp-" + current_query_set
		+ "-MFSFULLINC_relaxation-strategy-Jena-lubm-" + tdb_alias
		+ "-" + time_value + ".csv";
	newResultExplain = new ResultStrategyExplain(fileCSV, time_multiple);

	/*****************************************
	 * MFS FULL INC relaxation strategy test *
	 *****************************************/
	String logfile = "exp-" + "MFSFullINC_relaxation" + "-" + "lubm"
		+ tdb_alias + "-" + time_value + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithFullIncMFSStrategy();

	newResultExplain.generateReport();
    }

//    @Test
    public void testLUBM_MFSFULLSYS() throws Exception {

	newTestResultPairList = this.newTestResultPairList("/"
		+ QUERIES_TYPE_FILE.get(current_query_set));

	String fileCSV = "exp-" + current_query_set
		+ "-MFSFULLSYS_relaxation-strategy-Jena-lubm-" + tdb_alias
		+ "-" + time_value + ".csv";
	newResultExplain = new ResultStrategyExplain(fileCSV, time_multiple);

	/*****************************************
	 * MFS FULL SYS relaxation strategy test *
	 ****************************************/
	String logfile = "exp-" + "MFSFullSys_relaxation" + "-" + "lubm"
		+ tdb_alias + "-" + time_value + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	testRelaxationWithFullSysMFSStrategy();

	newResultExplain.generateReport();
    }
}
