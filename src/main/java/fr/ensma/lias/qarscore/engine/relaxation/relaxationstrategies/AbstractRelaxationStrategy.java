/*********************************************************************************
 * This file is part of QARS Project.
 * Copyright (C) 2015  LIAS - ENSMA
 *   Teleport 2 - 1 avenue Clement Ader
 *   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
 * 
 * QARS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QARS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with QARS.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Geraud FOKOU
 */
public abstract class AbstractRelaxationStrategy implements RelaxationStrategy {

    protected Logger logger = Logger.getLogger(AbstractRelaxationStrategy.class);
    
    public int number_mfs_query_executed =0;
    public int number_mfs_check_query_executed =0;
    public int number_fine_grained_query_executed =0;
    public long duration_mfs_query_executed =0;
    public long duration_mfs_check_query_executed =0;
    public long duration_computation_view =0;
    public Map<Double, Double> sim_sat =  new LinkedHashMap<Double, Double>();

    protected void logger_init() {

//	LocalDateTime time = LocalDateTime.now();
//	String time_value = "" + time.getDayOfMonth() + time.getMonthValue()
//		+ time.getHour() + time.getMinute() + time.getSecond();

	String logfile = this.getClass().getSimpleName() + "-Process" + ".log";

	PatternLayout layout = new PatternLayout();
	String conversionPattern = "%-5p [%C{1}]: %m%n";
	layout.setConversionPattern(conversionPattern);

	FileAppender fileAppender = new FileAppender();
	fileAppender.setFile(logfile);
	fileAppender.setLayout(layout);
	fileAppender.activateOptions();
	logger.removeAllAppenders();
	logger.addAppender(fileAppender);
    }
}
