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
package fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.implementation;

import java.util.ArrayList;
import java.util.List;

import org.roaringbitmap.RoaringBitmap;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.connection.implementation.JenaSession;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;

/**
 * @author Geraud FOKOU
 */
public class MatrixStrategyStarQuery extends MatrixStrategy {

    /**
     * Current data session
     */
    protected final Session SESSION;

    /**
     * Current final query
     */
    protected final CQuery CURRENT_CONJUNCTIVE_QUERY;

    /**
     * Final MFS
     */
    private final List<CQuery> MFS_CURRENT_QUERY;

    /**
     * Final XSS
     */
    private final List<CQuery> XSS_CURRENT_QUERY;

    /**
     * 
     * @param s
     * @param conjunctiveQuery
     * @param expected_answers_number
     */
    protected MatrixStrategyStarQuery(Session s, CQuery conjunctiveQuery,
	    int expected_answers_number) {

	super(s, conjunctiveQuery, expected_answers_number);

	SESSION = s;
	CURRENT_CONJUNCTIVE_QUERY = conjunctiveQuery;
	values = new RoaringBitmap[CURRENT_CONJUNCTIVE_QUERY.getElementList()
		.size() + 1];
	for (int i = 0; i < values.length; i++) {
	    values[i] = new RoaringBitmap();
	}

	this.initializeMatrix();

	this.MFS_CURRENT_QUERY = this.getAllMFS(CURRENT_CONJUNCTIVE_QUERY);
	this.XSS_CURRENT_QUERY = this.getAllXSS(CURRENT_CONJUNCTIVE_QUERY);
    }

    /**
     * Initialize the matrix of the query for finding MFS and XSS
     */
    private void initializeMatrix() {

	int number_Element = CURRENT_CONJUNCTIVE_QUERY.getElementList().size();

	for (int i = 0; i < number_Element; i++) {
	    List<CElement> elements = new ArrayList<CElement>();
	    elements.add(CURRENT_CONJUNCTIVE_QUERY.getElementList().get(i));
	    CQuery current_query = CQueryFactory.createCQuery(elements);

	    ResultSet result_set = QueryExecutionFactory.create(current_query.toString(), ((JenaSession)SESSION).getDataset())
		.execSelect();

	    while (result_set.hasNext()) {
		QuerySolution result = result_set.next();

		RDFNode val = result.get(result_set.getResultVars().get(0));
		Integer intVal = dictionary.get(val);

		if (intVal == null) {
		    dictionary_size++;
		    dictionary.put(val, dictionary_size);
		    intVal = dictionary_size;
		}

		if (i == 0) {
		    storeNewMapping(intVal, 1);
		} else {
		    if (contains(intVal)) {
			setTi(intVal, i + 1);
		    } else {
			storeNewMapping(intVal, i + 1);
		    }
		}
	    }
	}

    }

    /**
     * Check if a value of a central variable is already store in the matrix
     * 
     * @param mu
     * @return
     */
    private boolean contains(int mu) {
	return values[0].contains(mu);
    }

    /**
     * Set to 1 the value of the value line and ti column (ti starts at 1)
     * 
     * @param mu
     * @param ti
     */
    private void setTi(int mu, int ti) {
	values[ti].add(mu);
    }

    /**
     * Add a new value of the central variable in the matrix
     * 
     * @param val
     * @param ti
     */
    private void storeNewMapping(int val, int ti) {
	values[0].add(val);
	values[ti].add(val);
    }

    @Override
    public RoaringBitmap getBitVector(int ti) {
	return values[ti];
    }

    @Override
    public int getCardinality() {
	return values[0].getCardinality();
    }

    @Override
    public boolean hasLeastKAnswers() {

	List<Integer> listIndexElement = new ArrayList<Integer>();
	for (CElement element : CURRENT_CONJUNCTIVE_QUERY.getElementList()) {
	    int index = CURRENT_CONJUNCTIVE_QUERY.getElementList().indexOf(
		    element);
	    if (index != -1) {
		listIndexElement.add(index + 1);
	    }
	}

	RoaringBitmap conjunction = getBitVector(listIndexElement.get(0));
	for (int i = 0; i < listIndexElement.size(); i++) {
	    conjunction.and(getBitVector(listIndexElement.get(i)));
	}

	return conjunction.getCardinality() >= NUMBER_OF_EXPECTED_ANSWERS;
    }

    @Override
    public boolean isMFS() {

	if (hasLeastKAnswers(CURRENT_CONJUNCTIVE_QUERY)) {
	    return false;
	}

	for (CElement element : CURRENT_CONJUNCTIVE_QUERY.getElementList()) {

	    RoaringBitmap conjunction;

	    int i;
	    if (CURRENT_CONJUNCTIVE_QUERY.getElementList().get(0) == element) {
		conjunction = getBitVector(1);
		i = 1;
	    } else {
		conjunction = getBitVector(0);
		i = 0;
	    }
	    for (i = i + 1; i < CURRENT_CONJUNCTIVE_QUERY.getElementList()
		    .size(); i++) {
		if (CURRENT_CONJUNCTIVE_QUERY.getElementList().get(0) != element) {
		    conjunction.and(getBitVector(i));
		}
	    }

	    if (conjunction.getCardinality() < NUMBER_OF_EXPECTED_ANSWERS) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public CQuery getOneMFS() {
	return this.MFS_CURRENT_QUERY.get(0);
    }

    @Override
    public List<CQuery> getAllMFS() {
	return this.MFS_CURRENT_QUERY;
    }

    @Override
    public List<CQuery> getAllXSS() {
	return this.XSS_CURRENT_QUERY;
    }
}
