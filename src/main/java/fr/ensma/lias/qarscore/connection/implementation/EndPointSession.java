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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import fr.ensma.lias.qarscore.configuration.OutputFormat;
import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.metadata.DatasetOntologyMetaData;
import fr.ensma.lias.qarscore.connection.metadata.JSONResultSet;

/**
 * @author Mickael BARON
 * @author Geraud FOKOU
 */
public class EndPointSession implements Session {

    protected Logger logger = Logger.getLogger(EndPointSession.class);

    protected String url;

    protected String defaultGraphURI;

    protected OutputFormat outputFormat;

    protected URL baseURL;

    protected Integer softLimit = Integer.MIN_VALUE;

    protected DatasetOntologyMetaData ontologyStat;

    /**
     * 
     */
    public EndPointSession() {
    }

    public static class Builder {

	private String url;

	private String defaultGraphURI;

	private OutputFormat outputFormat;

	private Integer softLimit = Integer.MIN_VALUE;

	public Builder url(String url) {
	    this.url = url;
	    return this;
	}

	public Builder defaultGraphURI(String pDefaultGraphURI) {
	    this.defaultGraphURI = pDefaultGraphURI;
	    return this;
	}

	public Builder outputFormat(OutputFormat pOutputFormat) {
	    this.outputFormat = pOutputFormat;
	    return this;
	}

	public Builder softLimit(Integer pSoftLimit) {
	    this.softLimit = pSoftLimit;
	    return this;
	}

	public EndPointSession build() {
	    EndPointSession sparqlEndpointConfig = new EndPointSession();
	    sparqlEndpointConfig.url = this.url;
	    sparqlEndpointConfig.defaultGraphURI = this.defaultGraphURI;
	    sparqlEndpointConfig.outputFormat = this.outputFormat;
	    sparqlEndpointConfig.softLimit = this.softLimit;
	    sparqlEndpointConfig.ontologyStat = DatasetOntologyMetaData
		    .getInstance(sparqlEndpointConfig);

	    return sparqlEndpointConfig;
	}
    }

    public void setOutputFormat(OutputFormat outputFormat) {
	this.outputFormat = outputFormat;
    }

    public URL getBaseURL() throws MalformedURLException {
	if (baseURL == null) {
	    baseURL = new URL(url);
	}
	return baseURL;
    }

    private String readResponse(HttpURLConnection connection)
	    throws MalformedURLException, ProtocolException, IOException {
	BufferedReader in = new BufferedReader(new InputStreamReader(
		connection.getInputStream()));

	StringBuilder responseBuilder = new StringBuilder();
	String str;
	while (null != ((str = in.readLine()))) {
	    responseBuilder.append(str + System.getProperty("line.separator"));
	}
	in.close();
	return responseBuilder.toString();
    }

    /**
     * Queries the repository and returns the result in the requested format
     * 
     * @param sparql
     * @param format
     * @param softLimit
     * @return the result in the requested format
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     */
    public String query(String sparql) throws MalformedURLException,
	    ProtocolException, IOException {
	OutputFormat refOutputFormat = null;

	if (outputFormat == null) {
	    refOutputFormat = OutputFormat.JSON;
	} else {
	    refOutputFormat = outputFormat;
	}

	HttpURLConnection connection = (HttpURLConnection) this.getBaseURL()
		.openConnection();

	connection.setDoOutput(true);
	connection.setDoInput(true);
	connection.setRequestMethod("POST");
	connection.setRequestProperty("Content-Type",
		"application/x-www-form-urlencoded");
	connection.setRequestProperty("Accept", refOutputFormat.getMimeType());

	StringBuffer queryString = new StringBuffer();
	queryString.append("&query=" + URLEncoder.encode(sparql, "UTF-8"));

	if (softLimit != Integer.MIN_VALUE) {
	    queryString.append("&soft-limit=" + softLimit);
	}

	if (defaultGraphURI != null) {
	    queryString.append("&default-graph-uri="
		    + URLEncoder.encode(defaultGraphURI, "UTF-8"));
	}

	DataOutputStream ps = new DataOutputStream(connection.getOutputStream());
	ps.writeBytes(queryString.toString());

	ps.flush();
	ps.close();

	return readResponse(connection);
    }

    @Override
    public String getNameSession() {

	String tempUrl = url.substring(0, url.lastIndexOf("/"));
	tempUrl = tempUrl.substring(tempUrl.lastIndexOf("/") + 1);
	return tempUrl;
    }

    @Override
    public String executeSelectQuery(String query) {

	try {
	    return this.query(query);
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    @Override
    public int getResultSize(String query) {

	JSONResultSet result = JSONResultSet.getJSONResultSet(this
		.executeSelectQuery(query));
	return result.getBindings().length();
    }

    @Override
    public DatasetOntologyMetaData getOntology() {
	return ontologyStat;
    }

    @Override
    public String executeConstructQuery(String query) {

	OutputFormat refOutputFormat = OutputFormat.N_TRIPLES;
	HttpURLConnection connection;
	
	try {
	    connection = (HttpURLConnection) this.getBaseURL().openConnection();
	    connection.setDoOutput(true);
	    connection.setDoInput(true);
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type",
		    "application/x-www-form-urlencoded");
	    connection.setRequestProperty("Accept",
		    refOutputFormat.getMimeType());

	    StringBuffer queryString = new StringBuffer();
	    queryString.append("&query=" + URLEncoder.encode(query, "UTF-8"));

	    if (defaultGraphURI != null) {
		queryString.append("&default-graph-uri="
			+ URLEncoder.encode(defaultGraphURI, "UTF-8"));
	    }

	    DataOutputStream ps = new DataOutputStream(
		    connection.getOutputStream());
	    ps.writeBytes(queryString.toString());

	    ps.flush();
	    ps.close();

	    return readResponse(connection);

	} catch (IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
