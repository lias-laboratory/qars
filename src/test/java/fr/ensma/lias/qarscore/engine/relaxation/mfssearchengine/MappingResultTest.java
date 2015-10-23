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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import fr.ensma.lias.qarscore.InitTest;
import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.utils.MappingResult;

/**
 * @author Geraud FOKOU
 */
public class MappingResultTest extends InitTest {

    private Logger logger;

    @Before
    public void setUp() {

	super.setUp();
	logger = Logger.getRootLogger();
    }

    @After
    public void tearDown() throws Exception {
	super.tearDown();
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.utils.MappingResult#MappingResult(int[])}.
     */
    @Test
    public void testMappingResult() {


	HashMap<RDFNode, Integer> dictionary = new HashMap<RDFNode, Integer>();
	Integer dictionary_size = 0;

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_19);

	ResultSet result_set = (ResultSet) sessionJena.createStatement(conjunctiveQuery.toString()).executeQuery();

	MappingResult result_mapping = null;

	while (result_set.hasNext()) {

	    QuerySolution result = result_set.next();

	    int[] listMapping = new int[conjunctiveQuery
		    .getMentionedQueryVarNames().size()];

	    for (int j = 1; j <= conjunctiveQuery.getMentionedQueryVarNames()
		    .size(); j++) {
		RDFNode val = result.get(conjunctiveQuery
			.getMentionedQueryVarNames().get(j - 1));
		Integer intVal = null;
		if (val == null)
		    intVal = 0;
		else {
		    intVal = dictionary.get(val);
		    if (intVal == null) {
			dictionary_size++;
			dictionary.put(val, dictionary_size);
			intVal = dictionary_size;
		    }
		}
		listMapping[j - 1] = intVal;
	    }
	    result_mapping = new MappingResult(listMapping);
	    logger.info(result_mapping.toString());
	}
	Assert.assertTrue(dictionary_size == 8061);
	Assert.assertTrue(result_mapping.getVariables().length == 2);
    }

    @Test
    public void testMappingWithTwoPattern() {

	HashMap<RDFNode, Integer> dictionary = new HashMap<RDFNode, Integer>();
	Integer dictionary_size = 0;

	CQuery conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_21);

	for (int i = 1; i <= conjunctiveQuery.getElementList().size(); i++) {

	    List<CElement> elements = new ArrayList<CElement>();
	    elements.add(conjunctiveQuery.getElementList().get(i - 1));
	    CQuery current_query = CQueryFactory.createCQuery(elements);
	    
	    ResultSet result_set = (ResultSet) sessionJena.createStatement(current_query.toString()).executeQuery();

	    MappingResult result_mapping = null;

	    while (result_set.hasNext()) {

		QuerySolution result = result_set.next();

		int[] listMapping = new int[conjunctiveQuery
			.getMentionedQueryVarNames().size()];

		for (int j = 1; j <= conjunctiveQuery
			.getMentionedQueryVarNames().size(); j++) {
		    RDFNode val = result.get(conjunctiveQuery
			    .getMentionedQueryVarNames().get(j - 1));
		    Integer intVal = null;
		    if (val == null)
			intVal = 0;
		    else {
			intVal = dictionary.get(val);
			if (intVal == null) {
			    dictionary_size++;
			    dictionary.put(val, dictionary_size);
			    intVal = dictionary_size;
			}
		    }
		    listMapping[j - 1] = intVal;
		}
		result_mapping = new MappingResult(listMapping);
		logger.info(result_mapping.toString());
	    }
	    
	    if(i==1){
		Assert.assertTrue(dictionary_size == 8061);
	    }
	    else {
		Assert.assertTrue(dictionary_size == 8065);
	    }
	    
	    Assert.assertTrue(result_mapping.getVariables().length == 2);
	}
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.utils.MappingResult#getVariables()}.
     */
    @Test
    public void testGetVariables() {
	fail("Not yet implemented"); // TODO
    }
}
