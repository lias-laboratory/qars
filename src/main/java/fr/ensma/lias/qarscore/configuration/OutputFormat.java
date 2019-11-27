package fr.ensma.lias.qarscore.configuration;

/**
 * @author Mickael BARON
 */
public enum OutputFormat {
	HTML("text/html"), HTML_BASIC_BROWSING_LINKS("text/x-html+tr"), SPARQL_XML("application/sparql-results+xml"),
	JSON("application/sparql-results+json"), JAVASCRIPT("application/javascript"), TURTLE("text/turtle"),
	RDF_XML("application/rdf+xml"), N_TRIPLES("text/plain"), CSV("text/csv"),
	TAB_SEPARATED("text/tab-separated-values");

	private final String mimeType;

	OutputFormat(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	};
};