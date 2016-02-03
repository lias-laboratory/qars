package fr.ensma.lias.qarscore.benchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.benchmark.result.ResultStrategyExplain;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.AbstractRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.RelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.BasicOptimizedRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.BasicRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.DiscretionalMFSBasedRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.IncrementalMFSBaseRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.MFSBaseOptimizedRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.MFSBaseRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.xss.implementation.XSSFineGrainedRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.xss.implementation.XSSOptFineGrainedRelaxation;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.xss.implementation.XSSRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.utils.RelaxedResultTools;

/**
 * @author Mickael BARON
 * @author Geraud FOKOU
 */
@RunWith(Parameterized.class)
public class BenchmarkStrategiesTest extends InitTest {

    /**
     * Algorithm
     */
    private final static String ALGO = "xss-relax";

    /**
     * Set queries files
     */
    public final static Map<String, String> QUERIES_TYPE_FILE;
    static {
	QUERIES_TYPE_FILE = new HashMap<String, String>();
	QUERIES_TYPE_FILE.put("huang", "queries-huang.test");
	QUERIES_TYPE_FILE.put("mixed_III", "queries-mixed-III.test");
	QUERIES_TYPE_FILE.put("mixed_II", "queries-mixed-II.test");
	QUERIES_TYPE_FILE.put("mixed_I", "queries-mixed-I.test");
	QUERIES_TYPE_FILE.put("one", "queries-one.test");
    }

    public static String current_query_set = "one";

    @Parameters
    public static Object[][] initialize_queries() {

	List<QueryExplain> queryList = new ArrayList<QueryExplain>();
	try {
	    queryList = QueryExplain.newTestResultPairList("/"
		    + QUERIES_TYPE_FILE.get(current_query_set));
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
	Object[][] parameter = new Object[queryList.size()][2];
	for (int i = 0; i < queryList.size(); i++) {
	    parameter[i][0] = ALGO;
	    parameter[i][1] = queryList.get(i);
	}
	return parameter;
    }

    @Parameter(0)
    public String algorithm;

    @Parameter(1)
    public QueryExplain current_query;

    /**
     * looger tools
     */
    private Logger logger = Logger.getLogger(BenchmarkStrategiesTest.class);
    private PatternLayout layout;
    private FileAppender fileAppender;

    /**
     * set test parameter
     */
    private static final int NB_EXEC = 5;
    private int time_multiple = 1000;
    // private String time_value;
    private String timeEvaluationCSV;
    private String answersEvaluationCSV;
    private String simEvaluationCSV;
    private String logfile;

    /**
     * set session and other tools
     */
    private RelaxationStrategy relaxed_query;

    /**
     * Algorithm execution parameter
     */
    // private List<QueryExplain> newTestResultPairList = null;
    private LinkedHashMap<String, Double> solutions;
    private LinkedHashMap<String, Double> solutions_rsat;
    private ResultStrategyExplain newResultExplain = null;
    private BufferedWriter fichierAnswers = null;
    private BufferedWriter fichierSimilarity = null;

    /**
     * Set algorithm
     */
    private void set_relaxation(CQuery conjunctiveQuery) {

	switch (algorithm) {

	case "huang":
	    relaxed_query = new BasicRelaxationStrategy(conjunctiveQuery,
		    session);
	    break;
	case "huang-opt":
	    relaxed_query = new BasicOptimizedRelaxation(conjunctiveQuery,
		    session);
	    break;
	case "mfs":
	    relaxed_query = new MFSBaseRelaxationStrategy(conjunctiveQuery,
		    session);
	    break;
	case "mfs-opt":
	    relaxed_query = new MFSBaseOptimizedRelaxationStrategy(
		    conjunctiveQuery, session);
	    break;
	case "full-mfs-inc":
	    relaxed_query = new IncrementalMFSBaseRelaxationStrategy(
		    conjunctiveQuery, session);
	    break;
	case "full-mfs-disc":
	    relaxed_query = new DiscretionalMFSBasedRelaxationStrategy(
		    conjunctiveQuery, session);
	    break;
	case "xss-relax":
	    relaxed_query = new XSSRelaxationStrategy(conjunctiveQuery, session);
	    break;
	case "xss-relax-comp":
	    relaxed_query = new XSSFineGrainedRelaxation(conjunctiveQuery,
		    session);
	    break;
	case "xss-relax-opt":
	    relaxed_query = new XSSOptFineGrainedRelaxation(conjunctiveQuery,
		    session);
	    break;
	default:
	    Assert.fail();
	    break;
	}
    }

    @Before
    public void setUp() {

	super.setUp();
	layout = new PatternLayout();
	// LocalDateTime time = LocalDateTime.now();
	String conversionPattern = "%-5p [%C{1}]: %m%n";
	// String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
	layout.setConversionPattern(conversionPattern);

	// time_value = "" + time.getDayOfMonth() + time.getMonthValue()
	// + time.getHour() + time.getMinute() + time.getSecond();

	logfile = "exp-" + algorithm + "-" + "lubm" + TDB_ALIAS + "-"
		+ current_query.description + ".log";

	fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.addAppender(fileAppender);

	timeEvaluationCSV = "exp-time" + current_query.description + "-"
		+ algorithm + "-strategy-Jena-lubm-" + TDB_ALIAS + ".csv";

	answersEvaluationCSV = "exp-answers" + current_query.description + "-"
		+ algorithm + "-strategy-Jena-lubm-" + TDB_ALIAS + ".csv";

	simEvaluationCSV = "exp-sim" + current_query.description + "-"
		+ algorithm + "-strategy-Jena-lubm-" + TDB_ALIAS + ".csv";

	try {
	    // newTestResultPairList = this.newTestResultPairList("/"
	    // + QUERIES_TYPE_FILE.get(current_query_set));
	    fichierAnswers = new BufferedWriter(new FileWriter(
		    answersEvaluationCSV.toString()));
	    fichierSimilarity = new BufferedWriter(new FileWriter(
		    simEvaluationCSV.toString()));
	} catch (IOException e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	newResultExplain = new ResultStrategyExplain(timeEvaluationCSV,
		time_multiple);
	solutions = new LinkedHashMap<String, Double>(TOP_K);
	solutions_rsat = new LinkedHashMap<String, Double>(TOP_K);
    }

    @After
    public void tearDown() throws Exception {
	newResultExplain.generateReport();
	fichierAnswers.close();
	fichierSimilarity.close();
    }

    @Test
    public void testRelaxationStrategy() {

	long begin_query, begin, end_query;
	boolean hasTopk;
	int number_relaxed_queries, number_queries_mfs, number_check_queries;
	float duration, duration_mfs_search, duration_mfs_check_search, view_computation_duration;
	Map<Double, Double> all_sim;

	QueryExplain queryExplain = current_query;

	// for (QueryExplain queryExplain : newTestResultPairList) {

	logger.info("**************************Begin QUERY "
		+ queryExplain.description
		+ "***********************************");

	solutions.clear();
	solutions_rsat.clear();
	hasTopk = solutions.size() >= TOP_K;
	number_relaxed_queries = 0;

	CQuery conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		.getQuery());

	begin = System.currentTimeMillis();
	set_relaxation(conjunctiveQuery);
	duration = (float) (System.currentTimeMillis() - begin);

	while ((!hasTopk) && (relaxed_query.hasNext())) {
	    int query_answers_size = solutions.size();

	    begin_query = System.currentTimeMillis();
	    CQuery next_query = relaxed_query.next();

	    Query temp_query = next_query.getSPARQLQuery();
	    temp_query.setLimit(TOP_K);
	    
	    logger.info(temp_query.toString());
	    
	    Session session = relaxed_query.getCurrentView();
	    JSONResultSet result = session.executeSelectQuery(temp_query
		    .toString());

	    RelaxedResultTools.addResult(solutions, result,
		    relaxed_query.getCurrent_similarity(), TOP_K);

	    end_query = System.currentTimeMillis();
	    duration = duration + (float) (end_query - begin_query);

	    number_relaxed_queries = number_relaxed_queries + 1;
	    query_answers_size = solutions.size() - query_answers_size;

	    logger.info(relaxed_query.getCurrent_similarity() + " "
		    + query_answers_size + " "
		    + ((float) (end_query - begin_query)));

	    RelaxedResultTools.addResult(solutions_rsat, result,
		    relaxed_query.getRelativeSatisfactory(), TOP_K);

	    hasTopk = solutions.size() >= TOP_K;
	}

	duration_mfs_search = ((AbstractRelaxationStrategy) relaxed_query).duration_mfs_query_executed;
	duration_mfs_check_search = ((AbstractRelaxationStrategy) relaxed_query).duration_mfs_check_query_executed;

	number_queries_mfs = ((AbstractRelaxationStrategy) relaxed_query).number_mfs_query_executed;
	// number_check_queries = ((AbstractRelaxationStrategy)
	// relaxed_query).number_mfs_check_query_executed;
	number_check_queries = ((AbstractRelaxationStrategy) relaxed_query).number_fine_grained_query_executed;

	all_sim = ((AbstractRelaxationStrategy) relaxed_query).sim_sat;

	logger.info(duration + " " + duration_mfs_search + " "
		+ duration_mfs_check_search + " " + number_relaxed_queries
		+ " " + number_queries_mfs + " " + number_check_queries + " "
		+ solutions.size());
	logger.info("**************************End First iteration "
		+ "***********************************");

	for (int i = 0; i < NB_EXEC; i++) {

	    duration = 0;
	    duration_mfs_search = 0;
	    duration_mfs_check_search = 0;
	    number_queries_mfs = 0;
	    number_check_queries = 0;
	    number_relaxed_queries = 0;
	    solutions.clear();
	    solutions_rsat.clear();

	    hasTopk = solutions.size() >= TOP_K;

	    conjunctiveQuery = CQueryFactory.createCQuery(queryExplain
		    .getQuery());
	    begin = System.currentTimeMillis();
	    set_relaxation(conjunctiveQuery);
	    duration = (float) (System.currentTimeMillis() - begin);
	    while ((!hasTopk) && (relaxed_query.hasNext())) {
		int query_answers_size = solutions.size();

		begin_query = System.currentTimeMillis();

		CQuery next_query = relaxed_query.next();
		Query temp_query = next_query.getSPARQLQuery();
		temp_query.setLimit(TOP_K);
		Session session = relaxed_query.getCurrentView();
		JSONResultSet result = session.executeSelectQuery(temp_query
			.toString());

		RelaxedResultTools.addResult(solutions, result,
			relaxed_query.getCurrent_similarity(), TOP_K);

		end_query = System.currentTimeMillis();
		duration = duration + (float) (end_query - begin_query);

		query_answers_size = solutions.size() - query_answers_size;
		number_relaxed_queries = number_relaxed_queries + 1;

		logger.info("*****" + (int) (i + 2) + "******"
			+ relaxed_query.getCurrent_similarity() + " "
			+ query_answers_size + " "
			+ ((float) (end_query - begin_query)));

		RelaxedResultTools.addResult(solutions_rsat, result,
			relaxed_query.getRelativeSatisfactory(), TOP_K);

		hasTopk = solutions.size() >= TOP_K;
	    }

	    number_queries_mfs = ((AbstractRelaxationStrategy) relaxed_query).number_mfs_query_executed;
	    duration_mfs_search = ((AbstractRelaxationStrategy) relaxed_query).duration_mfs_query_executed;
	    // number_check_queries = ((AbstractRelaxationStrategy)
	    // relaxed_query).number_mfs_check_query_executed;
	    number_check_queries = ((AbstractRelaxationStrategy) relaxed_query).number_fine_grained_query_executed;
	    duration_mfs_check_search = ((AbstractRelaxationStrategy) relaxed_query).duration_mfs_check_query_executed;
	    all_sim = ((AbstractRelaxationStrategy) relaxed_query).sim_sat;

	    view_computation_duration = ((AbstractRelaxationStrategy) relaxed_query).duration_computation_view;

	    newResultExplain.add(queryExplain.getDescription(), duration,
		    duration_mfs_search + duration_mfs_check_search, duration
			    - duration_mfs_search - duration_mfs_check_search
			    - view_computation_duration,
		    view_computation_duration, number_relaxed_queries,
		    number_queries_mfs, number_check_queries);

	    logger.info("*****" + (int) (i + 2) + "******" + duration + " "
		    + duration_mfs_search + " " + duration_mfs_check_search
		    + " " + number_relaxed_queries + " " + number_queries_mfs
		    + " " + number_check_queries + " " + solutions.size());
	}
	logger.info("**************************End QUERY "
		+ queryExplain.description
		+ "***********************************");
	try {
	    load_similarity(queryExplain.getDescription(), all_sim);
	    load_answersQuality(queryExplain.getDescription());
	} catch (IOException e) {
	    e.printStackTrace();
	}
	// }
    }

    private void load_answersQuality(String description) throws IOException {

	StringBuffer buffer = new StringBuffer();
	Map<Double, Map<Double, Integer>> sim_size = new HashMap<Double, Map<Double, Integer>>();

	buffer.append(description + "\n");

	for (String answer : solutions.keySet()) {
	    Double sim = solutions.get(answer);
	    Double sat = solutions_rsat.get(answer);
	    if (sim_size.containsKey(sim)) {
		Map<Double, Integer> value = sim_size.get(sim);
		if (value.containsKey(sat)) {
		    int n_size = value.get(sat) + 1;
		    value.replace(sat, n_size);
		} else {
		    value.put(sat, 1);
		}
	    } else {
		HashMap<Double, Integer> n_value = new HashMap<Double, Integer>();
		n_value.put(sat, 1);
		sim_size.put(sim, n_value);
	    }
	}
	for (Double similarity : sim_size.keySet()) {

	    for (Double relative_sat : sim_size.get(similarity).keySet()) {
		Double satisfiability = similarity * relative_sat;
		Integer size = sim_size.get(similarity).get(relative_sat)
			.intValue();

		buffer.append("" + "\t"
			+ similarity.toString().replace('.', ',') + "\t"
			+ relative_sat.toString().replace('.', ',') + "\t"
			+ satisfiability.toString().replace('.', ',') + "\t"
			+ (size).toString() + "\n");

	    }
	}
	fichierAnswers.write(buffer.toString());
    }

    private void load_similarity(String description, Map<Double, Double> all_sim)
	    throws IOException {

	StringBuffer buffer = new StringBuffer();

	buffer.append(description + "\n");
	for (Map.Entry<Double, Double> entry : all_sim.entrySet()) {
	    Double similarity = entry.getKey();
	    Double relative_sat = entry.getValue();
	    Double satisfiability = similarity * relative_sat;

	    buffer.append("" + "\t" + similarity.toString().replace('.', ',')
		    + "\t" + relative_sat.toString().replace('.', ',') + "\t"
		    + satisfiability.toString().replace('.', ',') + "\n");

	}
	fichierSimilarity.write(buffer.toString());
    }
}
