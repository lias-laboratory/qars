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
package fr.ensma.lias.qarscore;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.statement.QueryStatement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.GraphRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.HuangRelaxationStrategy;
import fr.ensma.lias.qarscore.engine.relaxation.strategy.MFSRelaxationGraph;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class ServerStrategyRelaxationTest {

    public String repository_path = "target/Sesame/NativeRepository/LUBM1";
    public String tdb_path = "home/lias/tdb100repository";
    public Session sessionJena, sessionSesame;

    private final int TOP_K = 10;
    /* (non-Javadoc)
     * @see fr.ensma.lias.qarscore.InitTest#setUp()
     */
    @Before
    public void setUp() {
	//Properties.setModelMemSpec(OntModelSpec.OWL_DL_MEM);
	Properties.setModelMemSpec(OntModelSpec.OWL_DL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(tdb_path);
	sessionSesame = SessionFactory.getNativeSesameSession(repository_path);
    }

    /* (non-Javadoc)
     * @see fr.ensma.lias.qarscore.InitTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
	try {
	    sessionJena.close();
	    sessionSesame.close();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    public void testRelaxationWithHuangStrategy(){
	
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);
	
	HuangRelaxationStrategy relaxed_query = new HuangRelaxationStrategy(conjunctiveQuery, sessionJena);
	boolean hasTopk =false;
	int number_answers = 0;
	while ((!hasTopk)&&(relaxed_query.hasNext())){
	    QueryStatement stm = sessionJena.createStatement(relaxed_query.next().toString());
	    number_answers = number_answers + stm.getResultSetSize();
	    Logger.getRootLogger().info(relaxed_query.getCurrent_relaxed_query().toString()+" "+relaxed_query.getCurrent_similarity()+" "+relaxed_query.getCurrent_level()+" "+number_answers);
	    hasTopk = number_answers >= TOP_K;
	}
    }
    
    public void testRelaxationWithGraphStrategy(){
	
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);
	
	GraphRelaxationStrategy relaxed_query = new GraphRelaxationStrategy(conjunctiveQuery, sessionJena);
	boolean hasTopk =false;
	int number_answers = 0;
	while ((!hasTopk)&&(relaxed_query.hasNext())){
	    QueryStatement stm = sessionJena.createStatement(relaxed_query.next().toString());
	    number_answers = number_answers + stm.getResultSetSize();
	    Logger.getRootLogger().info(relaxed_query.getCurrent_relaxed_query().toString()+" "+relaxed_query.getCurrent_similarity()+" "+relaxed_query.getCurrent_level()+" "+number_answers);
	    hasTopk = number_answers >= TOP_K;
	}
    }

    public void testRelaxationWithMFSStrategy(){
	
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_1);
	
	MFSRelaxationGraph relaxed_query = new MFSRelaxationGraph(conjunctiveQuery, sessionJena);
	boolean hasTopk =false;
	int number_answers = 0;
	while ((!hasTopk)&&(relaxed_query.hasNext())){
	    QueryStatement stm = sessionJena.createStatement(relaxed_query.next().toString());
	    number_answers = number_answers + stm.getResultSetSize();
	    Logger.getRootLogger().info(relaxed_query.getCurrent_relaxed_query().toString()+" "+relaxed_query.getCurrent_similarity()+" "+relaxed_query.getCurrent_level()+" "+number_answers);
	    hasTopk = number_answers >= TOP_K;
	}
    }

}
