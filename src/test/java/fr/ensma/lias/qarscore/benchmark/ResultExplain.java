package fr.ensma.lias.qarscore.benchmark;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultExplain {

    private String logger_stat;

    protected Map<String, QueryReport> result = new LinkedHashMap<String, QueryReport>();
    
    class QueryReport {
	
	String name;

	List<Double> queryTime = new ArrayList<Double>();

	List<Double> queryExecution = new ArrayList<Double>();
	
	private double getQueryAverage(List<Double> contents) {
	    double sum = 0.0;
	    
	    if (!contents.isEmpty()) {
		for(double current : contents) {
		    sum += current;
		}
	    }
	    
	    return sum / contents.size();
	}
	
	public double getQueryTimeAverage() {
	    return getQueryAverage(queryTime);
	}
	
	public double getQueryExecutionAverage() {
	    return getQueryAverage(queryExecution);
	}
    }

    public ResultExplain(String loggerName) {
	logger_stat = loggerName;
    }

    private static double round(double d, int decimalPlace) {
	BigDecimal bd = new BigDecimal(Double.toString(d));
	bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	return bd.doubleValue();
    }

    /**
     * @param name
     * @param value ms
     * @param queryExecution
     */
    public void add(String name, double value, double queryExecution) {
	String substring = name.substring(6, name.length() - 4);

	QueryReport newQueryReport = null;
	if (!result.containsKey(substring)) {
	    newQueryReport = new QueryReport();
	    result.put(substring, newQueryReport);
	} else {
	    newQueryReport = result.get(substring);
	}

	newQueryReport.name = substring;
	newQueryReport.queryTime.add(value);
	newQueryReport.queryExecution.add(queryExecution);
    }
    
    public void generateReport() throws IOException {
	StringBuffer buffer = new StringBuffer();
	
	for (Map.Entry<String, QueryReport> entry : result.entrySet()) {
	    Double queryTimeAverage = (entry.getValue().getQueryTimeAverage() / 1000);
	    queryTimeAverage = ResultExplain.round(queryTimeAverage, 2);
	    buffer.append(entry.getKey() + "\t" + queryTimeAverage.toString().replace('.', ',') + "\t" + Math.round(entry.getValue().getQueryExecutionAverage()) + "\n");
	}
	
	BufferedWriter fichier = new BufferedWriter(new FileWriter(
 		logger_stat.toString()));
 	fichier.write(buffer.toString());
 	fichier.close();
    }
}
