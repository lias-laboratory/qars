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
package fr.ensma.lias.qarscore.engine.relaxation.optimization;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lias.qarscore.SPARQLQueriesSample;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;

/**
 * @author Geraud FOKOU
 */
public class CQueryTreeMapTest {

    private CQuery conjunctiveQuery;
    private CQueryTreeMap indexOfQuery;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	
	conjunctiveQuery = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	
	Assert.assertEquals(conjunctiveQuery.getElementList().size(), 15);
	conjunctiveQuery.getElementList().remove(2);
	conjunctiveQuery.getElementList().remove(2);
	conjunctiveQuery.getElementList().remove(3);
	conjunctiveQuery.getElementList().remove(5);
	conjunctiveQuery.getElementList().remove(3);
	
	Assert.assertEquals(conjunctiveQuery.getElementList().size(), 10);
	indexOfQuery = new CQueryTreeMap(conjunctiveQuery, 1);
	
	
	CQuery firstSub = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	
	Assert.assertEquals(firstSub.getElementList().size(), 15);
	firstSub.getElementList().remove(2);
	firstSub.getElementList().remove(2);
	firstSub.getElementList().remove(3);
	firstSub.getElementList().remove(3);
	firstSub.getElementList().remove(3);
	firstSub.getElementList().remove(5);
	Assert.assertEquals(firstSub.getElementList().size(), 9);
	
	indexOfQuery.put(firstSub, 0);
	
	CQuery firstSup = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	
	firstSup.getElementList().remove(2);
	firstSup.getElementList().remove(3);
	firstSup.getElementList().remove(5);
	
	indexOfQuery.put(firstSup, 0);
	
	CQuery firstIncomp = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	
	firstIncomp.getElementList().remove(2);
	firstIncomp.getElementList().remove(2);
	firstIncomp.getElementList().remove(3);
	firstIncomp.getElementList().remove(3);
	firstIncomp.getElementList().remove(7);
	
	indexOfQuery.put(firstIncomp, 0);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }


    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.optimization.CQueryTreeMap#contains(java.lang.Comparable)}.
     */
    @Test
    public void testContains() {
	
	CQuery firstIncomp = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	
	firstIncomp.getElementList().remove(2);
	firstIncomp.getElementList().remove(2);
	firstIncomp.getElementList().remove(3);
	firstIncomp.getElementList().remove(3);
	firstIncomp.getElementList().remove(7);
	
	Assert.assertTrue(indexOfQuery.contains(firstIncomp));
	Assert.assertTrue(indexOfQuery.contains(conjunctiveQuery));
	
	firstIncomp.getElementList().remove(0);
	Assert.assertFalse(indexOfQuery.contains(firstIncomp));
	
    }

    /**
     * Test method for {@link fr.ensma.lias.qarscore.engine.relaxation.optimization.CQueryTreeMap#get(java.lang.Comparable)}.
     */
    @Test
    public void testGet() {
	Assert.assertEquals(1, indexOfQuery.get(conjunctiveQuery).intValue());
	
	CQuery firstSup = CQueryFactory
		.createCQuery(SPARQLQueriesSample.QUERY_10);
	
	firstSup.getElementList().remove(2);
	firstSup.getElementList().remove(3);
	firstSup.getElementList().remove(5);

	Assert.assertEquals(0, indexOfQuery.get(firstSup).intValue());
	
	firstSup.getElementList().remove(0);
	
	Assert.assertEquals(null, indexOfQuery.get(firstSup));
	
    }
}
