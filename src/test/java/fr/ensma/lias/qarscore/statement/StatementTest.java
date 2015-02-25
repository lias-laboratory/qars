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
package fr.ensma.lias.qarscore.statement;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.connection.SessionTDBTest;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class StatementTest extends SessionTDBTest {

    private Session session;

    private Statement queryStatement;

    /**
     */
    @Before
    public void setUp() {
	super.setUp();
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");

	session = SessionFactory.getTDBSession("target/TDB/LUBM1");
	queryStatement = StatementFactory.getStatement(session);
    }

    @After
    public void teardDown() {
	super.teardDown();
    }

    @Test
    public void testPrepareQuery() {
	queryStatement.preparedQuery(SPARQLQueriesSample.QUERY_17);
	Assert.assertNotNull(queryStatement.getQuery());
    }

    @Test
    public void testExecuteQuery() {
	
	queryStatement.preparedQuery(SPARQLQueriesSample.QUERY_21);
	Assert.assertNotNull(queryStatement.getQuery());

	ResultSet result = queryStatement.executeSPARQLQuery();
	Assert.assertNotNull(result);
	int i =0;
	while(result.hasNext()){
	    QuerySolution solution = result.next();
	    Logger.getRootLogger().info(solution.get(result.getResultVars().get(0)));
	    i++;
	}
	
	Assert.assertTrue(!result.hasNext());
	Logger.getRootLogger().info(i);
    }
}
