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
package fr.ensma.lias.qarscore.connection;

import org.apache.jena.graph.Node;

import fr.ensma.lias.qarscore.connection.statement.QueryStatement;


/**
 * @author Geraud FOKOU
 */
public interface Session {
    
    void close() throws Exception;
    void open();
    boolean isopen();
    boolean isclose();
    QueryStatement createStatement(String query);
    double similarityMeasureClass(Node original_node, Node relaxed_node);
    double similarityMeasureProperty(Node original_node, Node relaxed_node);
}
