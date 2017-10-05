package fr.ensma.lias.qarscore;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SaturationTest extends InitTest {


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testwithoutsession() {
	
	List<String> path_entry = new ArrayList<String>();
	path_entry.add(PATH_ONTO);
	path_entry.add(PATH_SATURATED);
	Dataset dataset = TDBFactory.createDataset(TDB_PATH_SAT);
	TDBLoader.loadModel(dataset.getDefaultModel(), path_entry, false);
    }

    @Test
    public void testsaturationwithoutsession() {
	
	List<String> path_entry = new ArrayList<String>();
	path_entry.add(PATH_ONTO);
	path_entry.add(PATH_SATURATED);
	Dataset dataset = TDBFactory.createDataset(TDB_PATH_SAT);
	InfModel dataModel = ModelFactory.createInfModel(
		ReasonerRegistry.getRDFSReasoner(), dataset.getDefaultModel());

//	TDBLoader.loadModel(dataModel, path_entry, false);
	FileWriter out = null;
	try {
	    out = new FileWriter( PATH_SATURATED );
	    dataModel.write( out, "N-TRIPLE" );
	} catch (IOException e) {
	    e.printStackTrace();
	}
	finally {
	   try {
	       out.close();
	   }
	   catch (IOException closeException) {
	   }
	}
    }

    @Test
    public void testwithsessionwithoutsaturation() {
    }

    @Test
    public void testsaturationwithsession() {
    }

    @Test
    public void executeStatisticQueryTest() {
    }
}
