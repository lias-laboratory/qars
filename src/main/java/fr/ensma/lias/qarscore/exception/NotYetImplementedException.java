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
package fr.ensma.lias.qarscore.exception;

/**
 * @author Geraud FOKOU
 */
public class NotYetImplementedException extends RuntimeException {

    private static final long serialVersionUID = 1346560659118448325L;

    public NotYetImplementedException() {
    }

    /**
     * @param arg0
     */
    public NotYetImplementedException(String arg0) {
	super(arg0);
    }

    /**
     * @param arg0
     */
    public NotYetImplementedException(Throwable arg0) {
	super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public NotYetImplementedException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }
}
