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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import fr.ensma.lias.qarscore.engine.relaxation.utils.MappingResult;

/**
 * @author Geraud FOKOU
 */
public class MatrixStrategyAllQuery extends MatrixStrategy {

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
     * Maximal number of mappings for one matrix
     */
    private static final int NB_MAPPING = 1000000;

    /**
     * Set of Mapping result with their position
     */
    private Map<MappingResult, Integer> mappings;

    /**
     * Position of the last MappingResult inserted in the matrix
     */
    private int lastPosition = 0;

    /**
     * Constructor
     * 
     * @param s
     * @param conjunctiveQuery
     * @param expected_answers_number
     */
    protected MatrixStrategyAllQuery(Session s, CQuery conjunctiveQuery,
	    int expected_answers_number) {

	super(s, conjunctiveQuery, expected_answers_number);
	SESSION = s;
	CURRENT_CONJUNCTIVE_QUERY = conjunctiveQuery;

	mappings = new HashMap<MappingResult, Integer>(NB_MAPPING);
	values = new RoaringBitmap[CURRENT_CONJUNCTIVE_QUERY.getElementList()
		.size()];
	for (int i = 0; i < CURRENT_CONJUNCTIVE_QUERY.getElementList().size(); i++) {
	    values[i] = new RoaringBitmap();
	}

	initializeMatrix();

	this.MFS_CURRENT_QUERY = this.getAllMFS(CURRENT_CONJUNCTIVE_QUERY);
	this.XSS_CURRENT_QUERY = this.getAllXSS(CURRENT_CONJUNCTIVE_QUERY);

    }

    /**
     * Initialize the matrix for the computation of xss and mfs
     */
    private void initializeMatrix() {

	int number_Element = CURRENT_CONJUNCTIVE_QUERY.getElementList().size();
	boolean isInserted = false;

	for (int i = 1; i <= number_Element; i++) {

	    List<CElement> elements = new ArrayList<CElement>();
	    elements.add(CURRENT_CONJUNCTIVE_QUERY.getElementList().get(i - 1));
	    CQuery current_query = CQueryFactory.createCQuery(elements);

	    ResultSet result_set = QueryExecutionFactory.create(current_query.toString(), ((JenaSession)SESSION).getDataset())
			.execSelect();

	    while (result_set.hasNext()) {

		QuerySolution result = result_set.next();

		int[] listMapping = new int[CURRENT_CONJUNCTIVE_QUERY
			.getMentionedQueryVarNames().size()];

		for (int j = 1; j <= CURRENT_CONJUNCTIVE_QUERY
			.getMentionedQueryVarNames().size(); j++) {
		    RDFNode val = result.get(CURRENT_CONJUNCTIVE_QUERY
			    .getMentionedQueryVarNames().get(j - 1));
		    Integer intVal = null;
		    if (val == null)
			intVal = 0;
		    else {
			intVal = dictionary.get(val);
			if (intVal == null) {
			    dictionary_size++;
			    dictionary.put(val, dictionary_size);
			    intVal = dictionary_size;
			}
		    }
		    listMapping[j - 1] = intVal;
		}
		MappingResult result_mapping = new MappingResult(listMapping);
		isInserted = false;

		Map<MappingResult, Integer> actualMappings = new HashMap<MappingResult, Integer>(
			mappings.size());
		actualMappings.putAll(mappings);

		for (MappingResult store_mapping : actualMappings.keySet()) {

		    if (result_mapping.isCompatible(store_mapping)) {
			MappingResult unionMapping = result_mapping
				.union(store_mapping);
			Integer posUnionMapping = add(unionMapping);
			for (int j = 1; j <= number_Element; j++) {
			    if (j == i) {
				setTi(posUnionMapping, j);
			    } else {
				if (!getTi(posUnionMapping, j)) {
				    int posMup = getPostMapping(store_mapping);
				    if (getTi(posMup, j)) {
					setTi(posUnionMapping, j);
				    }
				}
			    }
			}
			if (result_mapping.equals(unionMapping)) {
			    isInserted = true;
			}
		    }
		}
		if (!isInserted) {
		    int posNewLine = storeNewMapping(result_mapping);
		    setTi(posNewLine, i);
		}
	    }
	}
    }

    /**
     * A a MappingResult m in the set of MappingResult mappings
     * 
     * @param m
     * @return
     */
    private Integer add(MappingResult m) {

	Integer res = mappings.get(m);
	if (res == null) {
	    lastPosition++;
	    mappings.put(m, lastPosition);
	    res = lastPosition;
	}
	return res;
    }

    /**
     * Set to 1 the value of the posMapping line and ti column (starts with 1)
     * 
     * @param posMapping
     * @param ti
     */
    private void setTi(int posMapping, int ti) {
	values[ti - 1].add(posMapping);
    }

    /**
     * Is the value of the posMapping line and ti column set to 1 ?
     * 
     * @param posMapping
     * @param ti
     * @return
     */
    private boolean getTi(int posMapping, int ti) {
	return values[ti - 1].contains(posMapping);
    }

    /**
     * return the position of a mapping in the set of stored mappings
     * 
     * @param m
     * @return
     */
    private Integer getPostMapping(MappingResult m) {
	return mappings.get(m);
    }

    /**
     * Store a new MappingResult in the set of mapping result mappings
     * 
     * @param m
     * @return
     */
    private Integer storeNewMapping(MappingResult m) {
	lastPosition++;
	mappings.put(m, lastPosition);
	return lastPosition;
    }

    @Override
    public RoaringBitmap getBitVector(int ti) {
	return values[ti - 1];
    }

    @Override
    public int getCardinality() {
	return mappings.size();
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
