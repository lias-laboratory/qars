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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.RelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.AbstractMFSRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.relaxationstrategies.mfs.implementation.MFSBaseOptimizedRelaxationStrategy;
import fr.ensma.lias.qarscore.testqueries.SPARQLQueriesSample;

/**
 * @author Geraud FOKOU
 */
public class MFSBaseOptimizedRelaxationStrategyTest extends InitTest {

    /* (non-Javadoc)
     * @see fr.ensma.lias.qarscore.InitTest#setUp()
     */
    @Before
    public void setUp() {
	super.setUp();
    }

    /* (non-Javadoc)
     * @see fr.ensma.lias.qarscore.InitTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
	super.tearDown();
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testRelaxationWithOPTMFSStrategy(){
	
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.WWW_QUERY_7);
	
	long begin = System.currentTimeMillis();
	RelaxationStrategy relaxed_query = new MFSBaseOptimizedRelaxationStrategy(conjunctiveQuery, session);
	boolean hasTopk =false;
	int number_answers = 0;
	int number_relaxed_queries = 0;
	while ((!hasTopk)&&(relaxed_query.hasNext())){
	    JSONResultSet result = JSONResultSet.getJSONResultSet(session.executeSelectQuery(relaxed_query.next().toString()));
	    int query_answers_size = result.getBindings().length();
	    number_answers = number_answers + query_answers_size;
	    hasTopk = number_answers >= TOP_K;
	    
	    number_relaxed_queries = number_relaxed_queries + 1;
	    Logger.getRootLogger().info(relaxed_query.getCurrent_relaxed_query().toString()+" "+relaxed_query.getCurrent_similarity()+" "+relaxed_query.getCurrent_level().toString()+" "+query_answers_size);
	}
	
	long end = System.currentTimeMillis();
	long duration = end - begin ;
	int number_queries_mfs = ((AbstractMFSRelaxationStrategy)relaxed_query).number_mfs_query_executed;
	long duration_mfs_search = ((AbstractMFSRelaxationStrategy)relaxed_query).duration__mfs_query_executed;
	int number_check_queries = ((AbstractMFSRelaxationStrategy)relaxed_query).number_mfs_check_query_executed;
	long duration_mfs_check_search = ((AbstractMFSRelaxationStrategy)relaxed_query).duration__mfs_check_query_executed;
	Logger.getRootLogger().info(number_check_queries+" "+number_queries_mfs+" "+duration_mfs_search+" "+number_relaxed_queries+" "+duration+" "+number_answers);
    }

}
