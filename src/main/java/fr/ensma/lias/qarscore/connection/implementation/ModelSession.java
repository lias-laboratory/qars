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
import java.io.StringWriter;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.resultset.ResultsFormat;

import fr.ensma.lias.qarscore.configuration.Properties;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.DatasetOntologyMetaData;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;

/**
 * @author Geraud FOKOU
 */
public class ModelSession implements Session {

    private final Model MODEL;

    /**
     * 
     */
    public ModelSession(String data) {
	Model model = ModelFactory.createDefaultModel();
	// String syntax = "N-TRIPLE";
	// MODEL.read(new ByteArrayInputStream(data.getBytes()), syntax);
	RDFDataMgr.read(model, Properties.DATA_SCHEMA_FILE,
		Lang.NTRIPLES);
	RDFDataMgr.read(model, new ByteArrayInputStream(data.getBytes()),
		Lang.NTRIPLES);
	MODEL = ModelFactory.createRDFSModel(model);
    }

    @Override
    public String getNameSession() {
	return null;
    }

    @Override
    public String executeSelectQuery(String query) {
	QueryExecution qexec = QueryExecutionFactory.create(query, MODEL);

	ResultSet results = qexec.execSelect();
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	ResultSetFormatter.output(outputStream, results,
		ResultsFormat.FMT_RS_JSON);

	return outputStream.toString();
    }

    @Override
    public int getResultSize(String query) {
	JSONResultSet result = JSONResultSet.getJSONResultSet(this
		.executeSelectQuery(query));
	return result.getBindings().length();
    }

    @Override
    public DatasetOntologyMetaData getOntology() {
	return null;
    }

    @Override
    public String executeConstructQuery(String query) {

	QueryExecution qexec = QueryExecutionFactory.create(query, MODEL);
	Model results = qexec.execConstruct();
	StringWriter out = new StringWriter();

	RDFDataMgr.write(out, results, Lang.NTRIPLES);

	// String syntax = "N-TRIPLE"; // also try "RDF/XML-ABBREV" , "N-TRIPLE"
	// and "TURTLE"
	// results.write(out, syntax);

	return out.toString();

    }

}
