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
package fr.ensma.lias.qarscore.parser;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import fr.ensma.lias.qarscore.InitTest;

/**
 * @author Geraud FOKOU
 */
public class JSONParserModelTest extends InitTest {

	@SuppressWarnings("unused")
	private Logger logger;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() {
		super.setUp();
		logger = Logger.getRootLogger();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

//    @Test
//    public void TestGetListNodeJs(){
//	JSONParserModel parser = new JSONParserModel(((JenaSession)sessionJena).getOntology());
//	Assert.assertNotNull(parser);
//	List<NodeJSON> nodes = parser.getListNodeJs();
//	Assert.assertNotNull(nodes);
//	Assert.assertTrue(!nodes.isEmpty());
//	for(NodeJSON node:nodes){
//	    logger.info(node);
//	}
//    }
//    
//    @Test
//    public void testGetParser() {
//	
//	JSONParserModel parser = new JSONParserModel(((JenaSession)sessionJena).getOntology());
//	Assert.assertNotNull(parser.getListNodeJs());
//	Assert.assertTrue(!parser.getListEdgesProperties().isEmpty());
//	Assert.assertTrue(!parser.getListEdgesSubclass().isEmpty());
//	logger.info(parser.getParser());
//    }
//
//    @Test
//    public void testGetParserExcludeClass() {
//	
//	List<String> exclude = new ArrayList<String>();
//	exclude.add("Director");
//	exclude.add("TeachingAssistant");
//	JSONParserModel parser = new JSONParserModel(((JenaSession)sessionJena).getOntology(), exclude);
//	Assert.assertNotNull(parser.getListNodeJs());
//	Assert.assertTrue(!parser.getListEdgesProperties().isEmpty());
//	Assert.assertTrue(!parser.getListEdgesSubclass().isEmpty());
//	logger.info(parser.getParser());
//    }

}
