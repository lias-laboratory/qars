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
package fr.ensma.lias.qarscore.configuration;


/**
 * @author Geraud FOKOU
 */
public class Properties {
    
    public static String DATA_SCHEMA_FILE = "src/test/resources/univ-bench.nt";
    
    private static String RELAXATION_STRATEGY = "AUTO";
    
    private static int RELAXATION_ANSWERS_SIZE = 1;
    
    /**
     * @return the RELAXATION_STRATEGY
     */
    public static String getRELAXATION_STRATEGY() {
        return RELAXATION_STRATEGY;
    }


    public static void setlatticeStrategy(){
	RELAXATION_STRATEGY = "LBA";
    }
    
    public static void setlatticeOptimizeStrategy(){
	RELAXATION_STRATEGY = "LBA/OPT";
    }

    public static void setMatrixStrategy(){
	RELAXATION_STRATEGY = "MBA";
    }


    public static void setIshmaelStrategy(){
	RELAXATION_STRATEGY = "ISHMAEL";
    }

    public static void setSimilarityStrategy(){
	RELAXATION_STRATEGY = "SIM";
    }
    
    public static void setAutomaticStrategy(int size){
	RELAXATION_STRATEGY = "AUTO";
	RELAXATION_ANSWERS_SIZE = size;
    }

    /**
     * @return the rELAXATION_ANSWERS_SIZE
     */
    public static int getRELAXATION_ANSWERS_SIZE() {
        return RELAXATION_ANSWERS_SIZE;
    }

}
