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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.statement.Statement;
import fr.ensma.lias.qarscore.statement.StatementFactory;

/**
 * @author Geraud FOKOU
 */
public abstract class JenaSession implements Session {

    /**
     * Only one session is allowed for an instance of the program
     */
    protected static Session session;

    /**
     * Dataset use for querying
     */
    protected Dataset dataset;

    /**
     * model of semantic data
     */
    protected Model model;

    /**
     * Ontology model of semantic data
     */
    protected OntModel ontologyModel;

    @Override
    public Dataset getDataset() {
	return dataset;
    }

    @Override
    public Model getModel() {
	return model;
    }

    @Override
    public OntModel getOntologyModel() {
	return ontologyModel;
    }

    /**
     * Return the base model of the data set in the SDB Triple store
     */
    @Override
    public Model getBaseModel() {
	return ontologyModel.getBaseModel();
    }

    /**
     * Return all the triple of the ontology for the data set in the SDB Triple
     * store
     */
    @Override
    public List<Triple> getOntologyTriple() {

	Model baseModel = ontologyModel.getBaseModel();
	List<Triple> allTriple = new ArrayList<Triple>();

	StmtIterator tripleIterator = baseModel.listStatements();
	while (tripleIterator.hasNext()) {
	    allTriple.add(tripleIterator.next().asTriple());
	}
	return allTriple;
    }

    /**
     * Return the ontology and its instance in a JSON format
     */
    @Override
    public String getOntoJSON() {

	JenaJSONLD.init();
	StringWriter writer = new StringWriter();
	getBaseModel().write(writer, "JSON-LD");

	return writer.toString();
    }

    /**
     * Create a statement for execute query
     */
    @Override
    public Statement createStatement() {
	return StatementFactory.getStatement(session);
    }

    /**
     * Create a statement for execute query
     */
    @Override
    public Statement createStatement(String query) {
	return StatementFactory.getStatement(session, query);
    }
}
