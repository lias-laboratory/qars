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

import org.apache.jena.ontology.OntModelSpec;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;

import fr.ensma.lias.qarscore.connection.implementation.SesameSession;
import fr.ensma.lias.qarscore.properties.Properties;

/**
 * @author Geraud FOKOU
 */
@SuppressWarnings("deprecation")
public class SessionSesameTest {

    public Session session;
    public String repository_path = "target/Sesame/NativeRepository/LUBM1";

    @Before
    public void setUp() {
	
	Properties.setModelMemSpec(OntModelSpec.OWL_MEM);
	Properties.setOntoLang("OWL");
	session = SessionFactory.getNativeSesameSession(repository_path);
    }

    @After
    public void teardDown() {
	try {
	    session.close();
	} catch (Exception e) {
	    Assert.fail();
	    e.printStackTrace();
	}
    }

    @Test
    public void testSessionSesame() {

	Assert.assertNotNull(((SesameSession) session).getRepository());
	try {
	    Assert.assertNotNull(((SesameSession) session).getModel());
	    Assert.assertNotNull(((SesameSession) session)
		    .getRepositoryConnection());
	    for (URI classe : ((SesameSession) session)
		    .getInformation_content().keySet()) {
		Logger.getRootLogger().info(
			classe.stringValue()
				+ " has information content "
				+ ((SesameSession) session)
					.getInformation_content().get(classe));
	    }

	} catch (Exception e) {
	    Assert.fail();
	    e.printStackTrace();
	}
    }
}
