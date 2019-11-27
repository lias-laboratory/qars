package fr.ensma.lias.qarscore.benchmark.result;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultStrategyExplain {

	private String logger_stat;

	protected Map<String, QueryReport> result = new LinkedHashMap<String, QueryReport>();

	private int multiple = 1000;

	class QueryReport {

		String name;

		List<Double> all_process_time = new ArrayList<Double>();

		List<Double> relaxation_process_time = new ArrayList<Double>();

		List<Double> mfs_process_time = new ArrayList<Double>();

		List<Double> view_process_time = new ArrayList<Double>();

		List<Integer> size_all_executed_queries = new ArrayList<Integer>();

		List<Integer> size_mfs_executed_queries = new ArrayList<Integer>();

		List<Integer> size_relaxation_executed_queries = new ArrayList<Integer>();

		private double getQueryTimeAverage(List<Double> contents) {
			double sum = 0.0;

			if (!contents.isEmpty()) {
				for (double current : contents) {
					sum += current;
				}
			}

			return sum / contents.size();
		}

		private int getQueryNumberAverage(List<Integer> contents) {
			int sum = 0;

			if (!contents.isEmpty()) {
				for (int current : contents) {
					sum += current;
				}
			}

			return sum / contents.size();
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the all_process_time
		 */
		public double getAll_process_time() {
			return getQueryTimeAverage(all_process_time);
		}

		/**
		 * @return the relaxation_process_time
		 */
		public double getRelaxation_process_time() {
			return getQueryTimeAverage(relaxation_process_time);
		}

		/**
		 * @return the mFS_process_time
		 */
		public double getMFS_process_time() {
			return getQueryTimeAverage(mfs_process_time);
		}

		/**
		 * @return the view_process_time
		 */
		public double getView_process_time() {
			return getQueryTimeAverage(view_process_time);
		}

		/**
		 * @return the size_all_executed_queries
		 */
		public int getSize_all_executed_queries() {
			return getQueryNumberAverage(size_all_executed_queries);
		}

		/**
		 * @return the size_mfs_executed_queries
		 */
		public int getSize_mfs_executed_queries() {
			return getQueryNumberAverage(size_mfs_executed_queries);
		}

		/**
		 * @return the size_relaxation_executed_queries
		 */
		public int getSize_relaxation_executed_queries() {
			return getQueryNumberAverage(size_relaxation_executed_queries);
		}
	}

	public ResultStrategyExplain(String loggerName) {
		logger_stat = loggerName;
	}

	public ResultStrategyExplain(String loggerName, int mul) {
		logger_stat = loggerName;
		multiple = mul;
	}

	protected static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * @param name
	 * @param value          ms
	 * @param queryExecution
	 */
	public void add(String name, double all_process, double mfs_process, double relaxation_process, double view_process,
			int all_queries, int mfs_queries, int relaxation_queries) {

		// String substring = name.substring(6, name.length() - 4);
		String substring = name.substring(6, name.length());

		QueryReport newQueryReport = null;
		if (!result.containsKey(substring)) {
			newQueryReport = new QueryReport();
			result.put(substring, newQueryReport);
		} else {
			newQueryReport = result.get(substring);
		}

		newQueryReport.name = substring;
		newQueryReport.all_process_time.add(all_process);
		newQueryReport.mfs_process_time.add(mfs_process);
		newQueryReport.relaxation_process_time.add(relaxation_process);
		newQueryReport.view_process_time.add(view_process);
		newQueryReport.size_all_executed_queries.add(all_queries);
		newQueryReport.size_mfs_executed_queries.add(mfs_queries);
		newQueryReport.size_relaxation_executed_queries.add(relaxation_queries);
	}

	public void generateReport() throws IOException {
		StringBuffer buffer = new StringBuffer();

		for (Map.Entry<String, QueryReport> entry : result.entrySet()) {
			Double queryAllProcessTimeAverage = (entry.getValue().getAll_process_time() / multiple);
			Double queryMFSProcessTimeAverage = (entry.getValue().getMFS_process_time() / multiple);

			Double queryRelaxationProcessTimeAverage = (entry.getValue().getRelaxation_process_time() / multiple);

			Double queryViewProcessTimeAverage = (entry.getValue().getView_process_time() / multiple);

			Integer sizeAllQuery = entry.getValue().getSize_all_executed_queries();
			Integer sizeRelaxationQuery = entry.getValue().getSize_relaxation_executed_queries();
			Integer sizeMFSQuery = entry.getValue().getSize_mfs_executed_queries();

			buffer.append(entry.getKey() + "\t" + queryAllProcessTimeAverage.toString().replace('.', ',') + "\t"
					+ queryMFSProcessTimeAverage.toString().replace('.', ',') + "\t"
					+ queryRelaxationProcessTimeAverage.toString().replace('.', ',') + "\t"
					+ queryViewProcessTimeAverage.toString().replace('.', ',') + "\t" + sizeAllQuery.toString() + "\t"
					+ sizeMFSQuery.toString() + "\t" + sizeRelaxationQuery.toString() + "\n");
		}

		BufferedWriter fichier = new BufferedWriter(new FileWriter(logger_stat.toString()));
		fichier.write(buffer.toString());
		fichier.close();
	}
}
