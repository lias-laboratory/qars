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
package fr.ensma.lias.qarscore.engine;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.TriplePath;

/**
 * @author Geraud FOKOU
 */
@SuppressWarnings("unused")
public class CTriple {

    /**
     * give the number of Filter clause make.
     */
    private static int numberTripleClause = 1;

    /**
     * Label for represent clause
     */
    private final String label = "T";

    /**
     * Pattern of clause
     */
    private TriplePath triplet = null;

    /**
     * Filter clause of the Pattern
     */
    private List<CFilter> hisFilter = null;

    /**
     * list of variables in clause pattern
     */
    private List<Node> mentionnedVar = new ArrayList<Node>();

}
