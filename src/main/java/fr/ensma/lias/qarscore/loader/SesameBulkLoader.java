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
package fr.ensma.lias.qarscore.loader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import org.apache.jena.ontology.OntModelSpec;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.sail.NotifyingSail;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.DirectTypeHierarchyInferencer;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

import fr.ensma.lias.qarscore.exception.NotYetImplementedException;

/**
 * @author Geraud FOKOU
 */
public class SesameBulkLoader {

	private static final String MEMORY_PATH = "target/Sesame/MemoryRepository/";

	/**
	 * Apply inference on data base on the user's property
	 * 
	 * @param store
	 * @return
	 */
	public static Sail inferred_data(NotifyingSail store, OntModelSpec spec) {

		/**
		 * Return a prebuilt standard configuration for the default RDFS reasoner
		 */
		if (spec.equals(OntModelSpec.OWL_MEM_RDFS_INF)) {
			return new ForwardChainingRDFSInferencer(store);
		}

		/**
		 * Return a prebuilt standard configuration for the default subclass/subproperty
		 * transitive closure reasoner.
		 */
		else if (spec.equals(OntModelSpec.OWL_MEM_TRANS_INF)) {
			return new DirectTypeHierarchyInferencer(store);
		}

		/**
		 * Default model without inferred triple
		 */
		else if (spec.equals(OntModelSpec.OWL_MEM)) {
			return store;
		}
		/**
		 * Default model without inferred triple
		 */
		else if (spec.equals(OntModelSpec.OWL_DL_MEM)) {
			return store;
		}

		else if (spec.equals(OntModelSpec.OWL_DL_MEM_RDFS_INF)) {
			return new ForwardChainingRDFSInferencer(store);
		}

		/**
		 * Prebuilt standard configuration for the default OWL reasoner.
		 */
		else if (spec.equals(OntModelSpec.OWL_DL_MEM_RULE_INF)) {
			return new ForwardChainingRDFSInferencer(store);
		}

		else if (spec.equals(OntModelSpec.RDFS_MEM)) {
			return store;
		}
		/**
		 * Return a prebuilt standard configuration for the default RDFS reasoner
		 */
		else if (spec.equals(OntModelSpec.RDFS_MEM_RDFS_INF)) {
			return new ForwardChainingRDFSInferencer(store);
		}

		/**
		 * Return a prebuilt standard configuration for the default subclass/subproperty
		 * transitive closure reasoner.
		 */
		else if (spec.equals(OntModelSpec.RDFS_MEM_TRANS_INF)) {
			return new DirectTypeHierarchyInferencer(store);
		}

		else {
			throw new NotYetImplementedException("unknow ontology specification");
		}
	}

	/**
	 * Load data on disk using a native store class with B-tree indexing
	 * 
	 * @return
	 */
	public static Repository loaderNativeStore(String dataDirPath, File[] dataFiles, String baseURI, String lang,
			OntModelSpec spec) {

		File dataDir = new File(dataDirPath);
		if (!dataDir.isDirectory()) {
			throw new IllegalArgumentException("illegal parameter: Directory path expected");
		}

		Repository repo = new SailRepository(inferred_data(new NativeStore(dataDir), spec));
		loaderRepository(repo, dataFiles, baseURI, lang, spec, true);

		return repo;
	}

	/**
	 * Load data in memory using a memory store class can use file on disk for
	 * persitant storage
	 * 
	 * @return
	 */
	public static Repository loaderMemoryStore(File[] dataFiles, String baseURI, String lang, OntModelSpec spec,
			boolean persist) {

		MemoryStore mem_store;
		Repository repo = null;
		if (persist) {
			File dataDir = new File(MEMORY_PATH + "LUBM1");
			mem_store = new MemoryStore(dataDir);
			mem_store.setSyncDelay(Long.MAX_VALUE);
		} else {
			mem_store = new MemoryStore();
		}
		repo = new SailRepository(inferred_data(mem_store, spec));
		loaderRepository(repo, dataFiles, baseURI, lang, spec, false);

		return repo;
	}

	// /**
	// * Load data in memory using a memory store class can use file on disk for
	// * persitant storage
	// *
	// * @return
	// */
	// public static Repository loaderMemoryStore(File[] dataFiles,
	// String baseURI, String lang) {
	//
	// MemoryStore mem_store;
	// mem_store = new MemoryStore();
	// mem_store.setSyncDelay(Long.MAX_VALUE);
	// Repository repo = new SailRepository(inferred_data(mem_store));
	// loaderRepository(repo, dataFiles, baseURI, lang, true);
	//
	// return repo;
	// }

	/**
	 * Load data in folder'file into the Repository repo and the Model model
	 * 
	 * @param repo
	 * @param model
	 * @param nameFolder
	 * @param baseURI
	 * @param lang
	 * @param spec
	 * @param close
	 */
	private static void loaderRepositoryandModel(Repository repo, Model model, File[] dataFiles, String baseURI,
			String lang, OntModelSpec spec, boolean close) {

		RDFParser rdfParser;

		switch (lang.toUpperCase()) {

		case "OWL":
			rdfParser = Rio.createParser(RDFFormat.RDFXML);
			break;

		case "RDF/XML":
			rdfParser = Rio.createParser(RDFFormat.RDFXML);
			break;

		case "N3":
			rdfParser = Rio.createParser(RDFFormat.N3);
			break;

		case "RDF":
			rdfParser = Rio.createParser(RDFFormat.RDFXML);
			break;

		case "NT":
			rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
			break;

		case "DAML":
			rdfParser = Rio.createParser(RDFFormat.RDFXML);
			break;

		case "TURTLE":
			rdfParser = Rio.createParser(RDFFormat.TURTLE);
			break;

		default:
			throw new IllegalArgumentException("wrong ontology language");
		}

		Resource context = null;
		if (model != null) {
			rdfParser.setRDFHandler(new StatementCollector(model));
		}

		repo.initialize();

		for (File dataFile : dataFiles) {
			URI currentUri = dataFile.toURI();
			String currentUrl = null;
			try {
				currentUrl = currentUri.toURL().toString();
				if (model != null) {
					rdfParser.parse(currentUri.toURL().openStream(), currentUrl);
					context = model.contexts().iterator().next();
				}
				repo.getConnection().add(currentUri.toURL(), baseURI, rdfParser.getRDFFormat(), context);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RDFParseException e) {
				e.printStackTrace();
			} catch (RDFHandlerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		repo.getConnection().commit();

		if (close) {
			repo.getConnection().close();
			repo.shutDown();

		}

		return;
	}

	private static void loaderRepository(Repository repo, File[] dataFiles, String baseURI, String lang,
			OntModelSpec spec, boolean close) {
		loaderRepositoryandModel(repo, null, dataFiles, baseURI, lang, spec, close);
	}

	/**
	 * main of BulkLoader, use for loading a specific data set in Sesame triple
	 * store BulkLoader Folder/File name, Onto_Lang, base_URI "Repository path"
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		int argsLenth = args.length;
		if (argsLenth < 3) {
			throw new IllegalArgumentException("illegal number of parameter");
		}

		String nameFolder = args[0];
		File dataFolder = new File(nameFolder);
		File[] dataFiles;

		if (!dataFolder.exists()) {
			throw new IllegalArgumentException("File doesn't exist");
		}

		FilenameFilter fileExt;
		switch (args[1].toUpperCase()) {

		case "OWL":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".owl");
				}
			};
			break;

		case "N3":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".n3");
				}
			};
			break;

		case "RDF":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".rdf");
				}
			};
			break;

		case "NT":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".nt");
				}
			};
			break;

		case "DAML":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".daml");
				}
			};
			break;

		case "TURTLE":
			fileExt = new FilenameFilter() {
				@Override
				public boolean accept(File sourceFolder, String name) {
					return name.toLowerCase().endsWith(".ttl");
				}
			};
			break;

		default:
			throw new IllegalArgumentException("wrong ontology language");
		}

		if (dataFolder.isDirectory()) {
			dataFiles = dataFolder.listFiles(fileExt);
		} else {
			if (fileExt.accept(dataFolder.getParentFile(), dataFolder.getName())) {
				dataFiles = new File[1];
				dataFiles[0] = dataFolder;
			} else {
				throw new IllegalArgumentException("Incompatible File and language");
			}
		}
		String base_uri = args[2];
		if (argsLenth == 3) {
			loaderMemoryStore(dataFiles, base_uri, args[1], OntModelSpec.OWL_DL_MEM, true);
		} else {
			loaderNativeStore(args[3], dataFiles, base_uri, args[1], OntModelSpec.OWL_DL_MEM);
		}
	}
}
