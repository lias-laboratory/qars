/*********************************************************************************
 * This file is part of MariusQL Project.
 * Copyright (C) 2014  LIAS - ENSMA
 *   Teleport 2 - 1 avenue Clement Ader
 *   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
 * 
 * MariusQL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MariusQL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MariusQL.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package fr.ensma.lias.qarscore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.ensma.lias.qarscore.connection.AllConnectionTests;
import fr.ensma.lias.qarscore.engine.query.AllQueryTests;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.AllRelaxationTests;
import fr.ensma.lias.qarscore.loader.AllLoaderTests;
import fr.ensma.lias.qarscore.parser.AllParserTests;

/**
 * @author Mickael BARON
 */
@RunWith(Suite.class)
@SuiteClasses(value = { 
	AllConnectionTests.class, AllLoaderTests.class,
	AllQueryTests.class, AllParserTests.class, AllRelaxationTests.class})
public class AllTests {

}
