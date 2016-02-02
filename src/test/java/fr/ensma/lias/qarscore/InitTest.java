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

import org.junit.After;
import org.junit.Before;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;

/**
 * @author Geraud FOKOU
 */
public class InitTest {

    public final String LUBM_FUSEKI = "http://localhost:3030/lubm1/sparql";
    public final String LUBM_SATURATED_FUSEKI = "http://localhost:3030/lubm1saturated/sparql";
    public final static String PATH = "c:/resources/UBA/Uni1.owl";
    public final static String PATH_ONTO = "c:/resources/UBA/univ-bench.owl";
    public final static String PATH_SATURATED = "c:/resources/UBA/univ1_saturated.nt";
    public final static String TDB_PATH = "c:/TDB/UBA";
    public final static String TDB_PATH_SAT= "c:/TDB/UBA_saturated";
    public final static String TDB_ALIAS = "tdb500";
    public final static int TOP_K = 50;
    
//    final static String TDB_PATH = "/home/lias/tdb500repository";
//    final static String TDB_PATH_SAT= "/home/lias/tdb500repository-saturated";
    
    public final static String LUBM_PREFIX = "PREFIX base: <http://swat.cse.lehigh.edu/onto/univ-bench.owl> "
	    + "PREFIX ub:   <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> "
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	    + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	    + "PREFIX owl:  <http://www.w3.org/2002/07/owl#> "
	    + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";

    public Session session;    

    /**
     * @throws java.lang.Exception
     * For sesame: sessionSesame = SessionFactory.getNativeSesameSession(repository_path);
     */
    @Before
    public void setUp(){
//	session = SessionFactory.getJenaTDBSession(TDB_PATH_SAT);
//	session = SessionFactory.getJenaTDBSession(TDB_PATH);
//	session = SessionFactory.getEndpointSession(LUBM_FUSEKI);
	session = SessionFactory.getEndpointSession(LUBM_SATURATED_FUSEKI);
    }

   
    /**
     * @throws java.lang.Exception
     * For Sesame: sessionSesame.close();
     */
    @After
    public void tearDown() throws Exception {
    }
}
