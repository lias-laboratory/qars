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
package fr.ensma.lias.qarscore.engine.relaxation.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.roaringbitmap.RoaringBitmap;

import com.hp.hpl.jena.rdf.model.RDFNode;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.RelaxationStrategies;

/**
 * @author Geraud FOKOU
 */
public abstract class MatrixStrategy implements RelaxationStrategies {

    /**
     * minimal expected answers
     */
    protected final int NUMBER_OF_EXPECTED_ANSWERS;

    /**
     * Boolean value for a MappingResult of each triple pattern Column storage
     * of boolean
     */
    protected RoaringBitmap[] values;

    /**
     * Dictionary of answer for the current query
     */
    protected Map<RDFNode, Integer> dictionary;

    /**
     * Size of the dictionary
     */
    protected Integer dictionary_size;

    /**
     * The last query on which we found MFS
     */
    protected CQuery actualQuery = null;

    /**
     * List of CQuery Causes of query failure
     */
    protected List<CQuery> failingCauses = null;

    /**
     * List of CQuery maximal succeeding subqueries
     */
    protected List<CQuery> maximalSubqueries = null;

    /**
     * 
     * @param s
     * @param conjunctiveQuery
     * @param expected_answers_number
     */
    protected MatrixStrategy(Session s, CQuery conjunctiveQuery,
	    int expected_answers_number) {

	NUMBER_OF_EXPECTED_ANSWERS = expected_answers_number;
	dictionary = new HashMap<RDFNode, Integer>();
	dictionary_size = 0;
	actualQuery = conjunctiveQuery;

    }

    protected abstract RoaringBitmap getBitVector(int ti);

    protected abstract int getCardinality();

    /**
     * Says if the line l1 dominates or equals the line l2
     * 
     * @param l1
     * @param l2
     * @return
     */
    protected boolean domintatesOrEquals(int l1, int l2) {

	boolean res = true;
	RoaringBitmap bitmap;
	for (int i = 1; i <= actualQuery.getElementList().size(); i++) {
	    bitmap = getBitVector(i);
	    if (bitmap.contains(l2) && !bitmap.contains(l1))
		return false;
	}
	return res;
    }

    /**
     * Says if the line l1 dominates the line l2
     * 
     * @param l1
     * @param l2
     * @return
     */
    protected boolean domintates(int l1, int l2) {

	boolean res = false;
	RoaringBitmap bitmap;
	for (int i = 1; i <= actualQuery.getElementList().size(); i++) {
	    bitmap = getBitVector(i);
	    if (bitmap.contains(l2) && !bitmap.contains(l1)) {
		return false;
	    } else if (!bitmap.contains(l2) && bitmap.contains(l1)) {
		res = true;
	    }
	}
	return res;
    }

    /**
     * 
     * @return
     */
    protected List<Integer> getSkyline() {

	List<Integer> liste = new ArrayList<Integer>();
	int sizeMatrix = getCardinality();
	if (sizeMatrix > 0)
	    liste.add(1);
	for (int i = 2; i <= sizeMatrix; i++) {
	    // this point dominates a point in the list
	    boolean isInserted = false;
	    // this point is dominated by a point in the list
	    boolean isDominated = false;
	    List<Integer> listCopy = new ArrayList<Integer>(liste);
	    for (Integer l : liste) {
		if (domintatesOrEquals(l, i)) {
		    // else it is not part of the skyline
		    isDominated = true;
		    break;
		}
		if (domintates(i, l)) {
		    // it is part of the current skyline
		    if (!isInserted) {
			listCopy.add(i);
			isInserted = true;
		    }
		    listCopy.remove(l);
		    // the point of the skyline dominated by i is removed
		}
	    }
	    if (!isInserted && !isDominated) {
		// i is incomparable with all lines in the skyline
		listCopy.add(i);
	    }
	    liste = listCopy;
	}
	return liste;
    }

    @Override
    public boolean hasLeastKAnswers(CQuery query) {

	if (query != actualQuery) {
	    return false;
	}

	List<Integer> listIndexElement = new ArrayList<Integer>();
	for (CElement element : actualQuery.getElementList()) {
	    int index = actualQuery.getElementList().indexOf(element);
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
    public boolean isMFS(CQuery query) {

	if (query != actualQuery) {
	    return false;
	}

	if (hasLeastKAnswers(actualQuery)) {
	    return false;
	}

	for (CElement element : actualQuery.getElementList()) {

	    RoaringBitmap conjunction;

	    int i;
	    if (actualQuery.getElementList().get(0) == element) {
		conjunction = getBitVector(1);
		i = 1;
	    } else {
		conjunction = getBitVector(0);
		i = 0;
	    }
	    for (i = i + 1; i < actualQuery.getElementList().size(); i++) {
		if (actualQuery.getElementList().get(0) != element) {
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
    public CQuery getOneMFS(CQuery query) {

	if (query != actualQuery) {
	    return null;
	}

	List<Integer> notEmptyCElement = new ArrayList<Integer>();

	for (int i = 0; i < actualQuery.getElementList().size(); i++) {
	    List<CElement> causes = new ArrayList<CElement>();
	    if (getBitVector(i + 1).isEmpty()) {
		causes.add(actualQuery.getElementList().get(i));
		return CQueryFactory.createCQuery(causes);
	    } else {
		notEmptyCElement.add(i + 1);
	    }
	}

	// MFS are the non empty combination not included in any found MFS
	Integer[] notEmptyCElmentArray = new Integer[notEmptyCElement.size()];

	ICombinatoricsVector<Integer> initialVector = Factory
		.createVector(notEmptyCElement.toArray(notEmptyCElmentArray));

	for (int i = 2; i <= actualQuery.getElementList().size(); i++) {

	    Generator<Integer> gen = Factory.createSimpleCombinationGenerator(
		    initialVector, i);

	    for (ICombinatoricsVector<Integer> combination : gen) {

		List<Integer> listeTi = combination.getVector();
		// assume that there is at least one CElement
		RoaringBitmap bitRes = getBitVector(listeTi.get(0));
		for (int j = 1; j < listeTi.size(); j++) {
		    bitRes = RoaringBitmap.and(bitRes,
			    getBitVector(listeTi.get(i)));
		}
		if (bitRes.isEmpty()) {
		    List<CElement> causes = new ArrayList<CElement>();
		    for (int j = 0; j < listeTi.size(); j++) {
			int index = listeTi.get(i);
			causes.add(actualQuery.getElementList().get(index));
		    }
		    return (CQueryFactory.createCQuery(causes));
		}
	    }
	}
	return null;
    }

    @Override
    public List<CQuery> getAllMFS(CQuery query) {

	if (query != actualQuery) {
	    return new ArrayList<CQuery>();
	}

	if (failingCauses != null) {
	    return failingCauses;
	}

	failingCauses = new ArrayList<CQuery>();
	List<Integer> notEmptyCElement = new ArrayList<Integer>();

	for (int i = 0; i < actualQuery.getElementList().size(); i++) {
	    List<CElement> causes = new ArrayList<CElement>();
	    if (getBitVector(i + 1).isEmpty()) {
		causes.add(actualQuery.getElementList().get(i));
		failingCauses.add(CQueryFactory.createCQuery(causes));
	    } else {
		notEmptyCElement.add(i + 1);
	    }
	}

	if (notEmptyCElement.size() > 1) {

	    List<ICombinatoricsVector<Integer>> mfsCombination = new ArrayList<ICombinatoricsVector<Integer>>();

	    // MFS are the non empty combination not included in any found MFS
	    Integer[] notEmptyCElmentArray = new Integer[notEmptyCElement
		    .size()];

	    ICombinatoricsVector<Integer> initialVector = Factory
		    .createVector(notEmptyCElement
			    .toArray(notEmptyCElmentArray));

	    for (int i = 2; i <= actualQuery.getElementList().size(); i++) {

		Generator<Integer> gen = Factory
			.createSimpleCombinationGenerator(initialVector, i);

		for (ICombinatoricsVector<Integer> combination : gen) {

		    List<Integer> listeTi = combination.getVector();
		    // assume that there is at least one CElement
		    RoaringBitmap bitRes = getBitVector(listeTi.get(0));
		    for (int j = 1; j < listeTi.size(); j++) {
			bitRes = RoaringBitmap.and(bitRes,
				getBitVector(listeTi.get(j)));
		    }
		    if (bitRes.isEmpty()) {
			boolean isSuper = false;
			int j = 0;
			while ((j < mfsCombination.size()) && (!isSuper)) {
			    isSuper = combination.getVector().containsAll(
				    mfsCombination.get(j).getVector());
			    j = j + 1;
			}
			if (!isSuper) {
			    mfsCombination.add(combination);
			}
		    }
		}
	    }
	    for (ICombinatoricsVector<Integer> mfs : mfsCombination) {
		List<Integer> listeTi = mfs.getVector();
		List<CElement> causes = new ArrayList<CElement>();
		for (int i = 0; i < listeTi.size(); i++) {
		    int index = listeTi.get(i);
		    causes.add(actualQuery.getElementList().get(index - 1));
		}
		failingCauses.add(CQueryFactory.createCQuery(causes));
	    }
	}
	return failingCauses;
    }

    @Override
    public List<CQuery> getAllXSS(CQuery query) {

	if (query != actualQuery) {
	    return new ArrayList<CQuery>();
	}

	if (maximalSubqueries != null) {
	    return maximalSubqueries;
	}

	maximalSubqueries = new ArrayList<CQuery>();
	List<ICombinatoricsVector<Integer>> xssCombination = new ArrayList<ICombinatoricsVector<Integer>>();

	Integer[] CElementArray = new Integer[actualQuery.getElementList()
		.size()];
	for (int i = 0; i < actualQuery.getElementList().size(); i++) {
	    CElementArray[i] = i + 1;
	}

	ICombinatoricsVector<Integer> initialVector = Factory
		.createVector(CElementArray);

	for (int i = actualQuery.getElementList().size() - 1; i >= 1; i--) {
	    Generator<Integer> gen = Factory.createSimpleCombinationGenerator(
		    initialVector, i);

	    for (ICombinatoricsVector<Integer> combination : gen) {

		List<Integer> listeTi = combination.getVector();
		// assume that there is at least one CElement
		RoaringBitmap bitRes = getBitVector(listeTi.get(0));
		for (int j = 1; j < listeTi.size(); j++) {
		    bitRes = RoaringBitmap.and(bitRes,
			    getBitVector(listeTi.get(j)));
		}
		if (!bitRes.isEmpty()) {
		    boolean isSubSet = false;
		    int j = 0;
		    while ((j < xssCombination.size()) && (!isSubSet)) {
			isSubSet = xssCombination.get(j).getVector()
				.containsAll(combination.getVector());
			j = j + 1;
		    }
		    if (!isSubSet) {
			xssCombination.add(combination);
		    }
		}
	    }
	}

	for (ICombinatoricsVector<Integer> mfs : xssCombination) {
	    List<Integer> listeTi = mfs.getVector();
	    List<CElement> causes = new ArrayList<CElement>();
	    for (int i = 0; i < listeTi.size(); i++) {
		int index = listeTi.get(i);
		causes.add(actualQuery.getElementList().get(index - 1));
	    }
	    maximalSubqueries.add(CQueryFactory.createCQuery(causes));
	}
	return maximalSubqueries;
    }
}
