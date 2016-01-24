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
package fr.ensma.lias.qarscore.connection;

import java.io.IOException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.syntax.Template;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.configuration.OutputFormat;
import fr.ensma.lias.qarscore.configuration.QueryConfig;
import fr.ensma.lias.qarscore.connection.implementation.EndPointSession;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.testqueries.SPARQLQueriesSample;

/**
 * @author Geraud FOKOU
 */
public class EndPointSessionTest {


    private EndPointSession session;

    @Before
    public void setUp() {
	session = new EndPointSession.Builder().url("http://localhost:3030/lubm1saturated/sparql").outputFormat(OutputFormat.JSON).build();
    }

    @After
    public void teardDown() {
   }

    @Test
    public void testEndPointSession() {
	try {
	    String result = session.query(QueryConfig.LIST_SUPER_CLASSES);
	    JSONObject jsonObj = new JSONObject(result);
	    for(String key: jsonObj.keySet()){
		JSONObject newJson = jsonObj.getJSONObject(key);
		for(String key2: newJson.keySet()){
		    System.out.println(key2+"--->"+newJson.get(key2)+"--->"+newJson.getJSONArray(key2));
		}
		System.out.println(key+"--->"+jsonObj.get(key));
	    }
	    System.out.println(jsonObj.toString());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
   
    @Test
    public void testEndPointSessionQuery() {
	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_14);
//	conjunctiveQuery.getElementList().remove(0);
	System.out.println(conjunctiveQuery.toString());
//	JSONResultSet result = JSONResultSet.getJSONResultSet(session.executeSelectQuery(conjunctiveQuery.toString()));
	JSONResultSet result = JSONResultSet.getJSONResultSet(session.executeSelectQuery(SPARQLQueriesSample.QUERY_14));
	int j = 0;
	while(result.next()){
	    j++;
	    System.out.println("*********************************************************************");
	    for (int i=0; i<result.getVars().length(); i++){
		System.out.println(result.getString(result.getVar(i)));
	    }
	    System.out.println("*********************************************************************");
	}
	System.out.println("size "+j);
    }

    @Test
    public void testConstructQueryExecution() {
	Query query = QueryFactory.create(SPARQLQueriesSample.EDBT_QUERY_1);
	CQuery conjunctiveQuery14 = CQueryFactory
		.createCQuery(SPARQLQueriesSample.EDBT_QUERY_1);
	Assert.assertTrue(query.isSelectType());
	query.setQueryConstructType();
	query.setConstructTemplate(new Template(new BasicPattern()));
	System.out.println(query.getConstructTemplate().getBGP());
	query.getConstructTemplate().getBGP().add(conjunctiveQuery14.getElementList().get(0).getTriple());
	query.getConstructTemplate().getBGP().add(conjunctiveQuery14.getElementList().get(1).getTriple());
	System.out.println(query.getConstructTemplate().getBGP());
	System.out.println(query.getQueryPattern().toString());

	String result = session.executeConstructQuery(query.toString());
	System.out.println(result);
    }
}
