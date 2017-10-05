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
package fr.ensma.lias.qarscore.connection.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.apache.jena.tdb.TDBFactory;
import org.apache.log4j.Logger;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.DatasetOntologyMetaData;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;

/**
 * @author Geraud FOKOU
 */
public class JenaTDBSession implements Session {

    protected Logger logger = Logger.getLogger(JenaTDBSession.class);

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    /**
     * Url of the dataset
     */
    protected String url;

    /**
     * Dataset use for querying
     */
    protected Dataset dataset;

    /**
     * Statistic of the ontology
     */
    protected DatasetOntologyMetaData ontologyStat;

    /**
     * Construct a TDB Session if there isn't existed
     */
    public static Session getTDBSession(String folder) {

	if (session != null) {
	    return session;
	}
	JenaTDBSession sessionJena = new JenaTDBSession();
	sessionJena.dataset = TDBFactory.createDataset(folder);
	sessionJena.url = folder;
	sessionJena.ontologyStat = DatasetOntologyMetaData
		.getInstance(sessionJena);
	session = sessionJena;
	return sessionJena;
    }

    private JenaTDBSession() {
    }

    @Override
    public String getNameSession() {
	return url.substring(url.indexOf("/") + 1);
    }

    @Override
    public JSONResultSet executeSelectQuery(String query) {

	QueryExecution qexec = QueryExecutionFactory
		.create(query, this.dataset);

	// ResultSet results = qexec.execSelect();
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	ResultSetFormatter.output(outputStream, qexec.execSelect(),
		ResultsFormat.FMT_RS_JSON);
	ByteArrayInputStream input = new ByteArrayInputStream(
		outputStream.toByteArray());

	return JSONResultSet.getJSONResultSet(input);
    }

    @Override
    public int getResultSize(String query) {

	JSONResultSet result = this.executeSelectQuery(query);
	return result.getBindings().size();
    }

    @Override
    public DatasetOntologyMetaData getOntology() {
	return ontologyStat;
    }

    @Override
    public InputStream executeConstructQuery(String query) {

	QueryExecution qexec = QueryExecutionFactory
		.create(query, this.dataset);
	// Model results = qexec.execConstruct();
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	RDFDataMgr.write(out, qexec.execConstruct(), Lang.NTRIPLES);

	// String syntax = "N-TRIPLE"; // also try "RDF/XML-ABBREV" , "N-TRIPLE"
	// and "TURTLE"
	// results.write(out, syntax);

	return new ByteArrayInputStream(out.toByteArray());
    }

    public ResultSet execute(String query) {

	QueryExecution qexec = QueryExecutionFactory
		.create(query, this.dataset);

	return qexec.execSelect();
    }

    public int getResultSetSize(String query, int limit) {

	int size = 0;

	ResultSet results = execute(query);
	while ((results.hasNext()) && (size < limit)) {
	    results.nextSolution();
	    size++;
	}
	return size;
    }
    
    public Model executeModelConstructQuery(String query) {

	QueryExecution qexec = QueryExecutionFactory
		.create(query, this.dataset);
	
	return qexec.execConstruct();
    }
}
