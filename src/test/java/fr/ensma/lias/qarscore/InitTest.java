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

import org.apache.jena.ontology.OntModelSpec;
import org.junit.After;
import org.junit.Before;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.SessionFactory;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
public class InitTest {

    public Session sessionJena;
    public Session sessionSesame;
//    public static String tdb_path = "C:/TDB/UBA";
    public String tdb_path = "/home/lias/tdb100repository-saturated";
    public String tdb_alias = "tdb100";
    
    public final int TOP_K = 10;
    public String repository_path = "target/Sesame/NativeRepository/LUBM1";  
    

    /**
     * @throws java.lang.Exception
     * For sesame: sessionSesame = SessionFactory.getNativeSesameSession(repository_path);
     */
    @Before
    public void setUp(){
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);		
//	Properties.setModelMemSpec(OntModelSpec.OWL_MEM_RDFS_INF);
	Properties.setOntoLang("OWL");
	sessionJena = SessionFactory.getTDBSession(tdb_path);
    }

   
    /**
     * @throws java.lang.Exception
     * For Sesame: sessionSesame.close();
     */
    @After
    public void tearDown() throws Exception {
	try {
	    sessionJena.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
