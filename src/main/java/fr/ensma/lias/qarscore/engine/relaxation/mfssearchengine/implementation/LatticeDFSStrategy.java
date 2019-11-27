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

import org.apache.jena.query.Query;

import fr.ensma.lias.qarscore.connection.Session;
import fr.ensma.lias.qarscore.engine.query.CElement;
import fr.ensma.lias.qarscore.engine.query.CQuery;
import fr.ensma.lias.qarscore.engine.query.CQueryFactory;
import fr.ensma.lias.qarscore.engine.relaxation.mfssearchengine.AbstractLatticeStrategy;

/**
 * @author Geraud FOKOU
 */
public class LatticeDFSStrategy extends AbstractLatticeStrategy {

	/**
	 * Get a lattice strategy relaxation for a session s and a number answers of
	 * wanted answers
	 * 
	 * @param s
	 * @param answers
	 * @return
	 */
	protected static LatticeDFSStrategy getLatticeDFSStrategy(Session s, CQuery query, int answers) {
		return new LatticeDFSStrategy(s, query, answers);
	}

	/**
	 * private constructor
	 */
	protected LatticeDFSStrategy(Session s, CQuery query, int answers) {
		super(s, query, answers);

		duration_of_execution = System.currentTimeMillis();
		this.computeMFS(actualQuery);
		duration_of_execution = System.currentTimeMillis() - duration_of_execution;
	}

	/**
	 * Computes all the MFS and XSS of a CQuery query
	 * 
	 * @param query
	 */
	protected void computeMFS(CQuery query) {

		/**
		 * log the current query
		 */
		for (int i = 0; i < query.getElementList().size(); i++) {
			logger.info(query.getElementList().get(i).getElement().toString() + "-->"
					+ query.getElementList().get(i).getLabel());
		}

		failingCauses = new ArrayList<CQuery>();
		maximalSubqueries = new ArrayList<CQuery>();

		if (!query.isValidQuery()) {
			return;
		}

		if (hasLeastKAnswers(query)) {
			maximalSubqueries.add(CQueryFactory.cloneCQuery(query));
			return;
		}

		if (query.getElementList().size() == 1) {
			failingCauses.add(CQueryFactory.cloneCQuery(query));
			return;
		}

		boolean isMfs = true;

		for (int i = 0; i < query.getElementList().size() - 1; i++) {

			CElement element = query.getElementList().get(i);
			CQuery tempQuery = CQueryFactory.cloneCQuery(query);
			tempQuery.getElementList().remove(element);

			LatticeDFSStrategy tempQueryCauses = new LatticeDFSStrategy(SESSION, tempQuery, NUMBER_OF_EXPECTED_ANSWERS);
			List<CQuery> currentAllMfs = tempQueryCauses.getAllMFS();
			List<CQuery> currentAllXss = tempQueryCauses.getAllXSS();
			List<CQuery> toremove = new ArrayList<CQuery>();

			if (currentAllMfs.size() != 0) {
				isMfs = false;

				toremove.clear();
				for (CQuery oneMfs : currentAllMfs) {
					boolean isOld = false;
					for (CQuery oldMfs : failingCauses) {
						if (oneMfs.equals(oldMfs)) {
							isOld = true;
						} else {
							if (oneMfs.isSuperQueryOf(oldMfs)) {
								isOld = true;
							}
							if (oldMfs.isSuperQueryOf(oneMfs)) {
								toremove.add(oldMfs);
							}
						}
					}
					failingCauses.removeAll(toremove);
					if (!isOld) {
						failingCauses.add(oneMfs);
					}
				}

				toremove.clear();
				for (CQuery oneXSS : currentAllXss) {
					boolean isOld = false;
					for (CQuery oldXSS : maximalSubqueries) {
						if (oldXSS.equals(oneXSS)) {
							isOld = true;
						} else {
							if (oldXSS.isSuperQueryOf(oneXSS)) {
								isOld = true;
							}
							if (oneXSS.isSuperQueryOf(oldXSS)) {
								toremove.add(oldXSS);
							}
						}
					}
					maximalSubqueries.removeAll(toremove);
					if (!isOld) {
						maximalSubqueries.add(oneXSS);
					}
				}
			} else {
				if (tempQuery.isValidQuery()) {

					toremove.clear();
					CQuery oneXSS = CQueryFactory.cloneCQuery(tempQuery);
					boolean isOld = false;
					for (CQuery oldXSS : maximalSubqueries) {
						if (oldXSS.equals(oneXSS)) {
							isOld = true;
						} else {
							if (oldXSS.isSuperQueryOf(oneXSS)) {
								isOld = true;
							}
							if (oneXSS.isSuperQueryOf(oldXSS)) {
								toremove.add(oldXSS);
							}
						}
					}
					maximalSubqueries.removeAll(toremove);
					if (!isOld) {
						maximalSubqueries.add(oneXSS);
					}
				}
			}
		}

		if (isMfs) {
			failingCauses.add(CQueryFactory.cloneCQuery(query));
		}
		return;
	}

	@Override
	public boolean hasLeastKAnswers(CQuery query) {

		number_of_query_executed++;
		Query temp_query = query.getSPARQLQuery();
		temp_query.setLimit(NUMBER_OF_EXPECTED_ANSWERS);

		int nbSolution = SESSION.getResultSize(temp_query.toString());

		if (nbSolution >= NUMBER_OF_EXPECTED_ANSWERS) {
			logger.info("Execution of : " + query.getQueryLabel() + "                           Succes " + nbSolution);
		} else {
			logger.info("Execution of : " + query.getQueryLabel() + "                           Echec " + nbSolution);
			return false;
		}
		return true;
	}
}
