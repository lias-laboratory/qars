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
package fr.ensma.lias.qarscore.testqueries;

/**
 * @author Geraud FOKOU
 */
public class SPARQLQueriesSample {

	/**
	 * Prefixes of Queries
	 */
	public static String LUBM_PREFIX = "PREFIX base: <http://swat.cse.lehigh.edu/onto/univ-bench.owl> "
			+ "PREFIX ub:   <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX owl:  <http://www.w3.org/2002/07/owl#> " + "PREFIX xdt:  <http://www.w3.org/2001/XMLSchema#> ";

	// 1 triple pattern Chain ou Star
	public static String QUERY_1 = LUBM_PREFIX + "SELECT ?X WHERE { " + "?X rdf:type ub:Professor . " + "}";

	// 5 triple patterns Chain(la plus longue possible)
	public static String QUERY_2 = LUBM_PREFIX + "SELECT ?P ?Y1 ?Y2 ?Y3 ?Y4 " + "WHERE {?X ub:publicationAuthor ?Y1 . "
			+ "?Y1 ub:advisor ?Y2 . " + "?Y2 ub:worksFor ?Y3 . " + "?Y3 ub:subOrganizationOf ?Y4 . "
			+ "?Y4 ub:teacherOf <http://www.Department0.University0.edu/Course1> . " + "}";

	// 7 triple patterns Star
	public static String QUERY_3 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 "
			+ "WHERE { ?X ub:undergraduateDegreeFrom <http://www.University303.edu> . "
			+ "?X rdf:type ub:GraduateStudent ."
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>.  " + "?X ub:name ?Y1 . "
			+ "?X ub:telephone ?Y2 . " + "?X ub:memberOf ?Y3 . " + "?X ub:advisor ?Y4 " + "}";

	// 9 triple patterns Composite
	public static String QUERY_4 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 "
			+ "WHERE { ?X rdf:type ub:FullProfessor . " + "?X ub:doctoralDegreeFrom <http://www.University8.edu> . "
			+ "?X ub:researchInterest 'Research23' . " + "?X ub:name ?Y1 . " + "?X ub:teacherOf ?Y4 . "
			+ "?Y5 ub:takesCourse ?Y4 . " + "?Y5 ub:name ?Y6 . " + "?Y5 ub:advisor ?Y7 . "
			+ "?Y7 ub:worksFor <http://www.Department0.University0.edu> . " + "}";

	// 11 triple patterns Star
	public static String QUERY_5 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 ?Y8 ?Y9 "
			+ "WHERE { ?X rdf:type ub:VisitingProfessor . "
			+ "?X ub:memberOf <http://www.Department1.University1.edu> . " + "?X ub:name ?Y1 . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . " + "?X ub:undergraduateDegreeFrom ?Y4 . "
			+ "?X ub:mastersDegreeFrom ?Y5 . " + "?X ub:doctoralDegreeFrom ?Y6 . " + "?X ub:worksFor ?Y7 . "
			+ "?X ub:researchInterest ?Y8 . " + "?X ub:headOf ?Y9 . " + " }";

	// 13 triple patterns Composite
	public static String QUERY_6 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 ?Y8 ?Y9 "
			+ "WHERE { ?X rdf:type ub:Lecturer . " + "?X ub:researchInterest 'Research28' . " + "?X ub:name ?Y1 . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . " + "?X ub:undergraduateDegreeFrom ?Y4 . "
			+ "?X ub:mastersDegreeFrom ?Y5 . " + "?X ub:doctoralDegreeFrom ?Y6 . " + "?X ub:headOf ?Y7 . "
			+ "?X ub:worksFor ?Y8 . " + "?Y8 ub:subOrganizationOf <http://www.University8.edu> . "
			+ "?Y9 ub:publicationAuthor ?X . "
			+ "?Y9 ub:publicationAuthor <http://www.Department0.University0.edu/FullProfessor0> . " + "}";

	// 15 triple patterns Star
	public static String QUERY_7 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y7 ?Y8 ?Y9 ?Y10 ?Y11 "
			+ "WHERE { ?X rdf:type ub:Student . "
			+ "?X ub:undergraduateDegreeFrom <http://www.Department0.University0.edu> . "
			+ "?X ub:mastersDegreeFrom <http://www.Department0.University0.edu> . "
			+ "?X ub:memberOf <http://www.Department1.University1.edu> . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse65> . " + "?X ub:name ?Y1 . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . " + "?X ub:title ?Y4 . " + "?X ub:age ?Y5 . "
			+ "?X ub:advisor ?Y6 ." + "?X ub:teachingAssistantOf <http://www.Department0.University0.edu/Course1> . "
			+ "?X ub:teachingAssistantOf ?Y7 . " + "?X ub:researchInterest ?Y8 . "
			+ "?X ub:researchInterest 'Research2' . " + " }";

	// 15 triple patterns Composite
	public static String QUERY_8 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 ?Y8 ?Y9 ?Y10 ?Y11 "
			+ "WHERE { ?X rdf:type ub:FullProfessor . " + "?X ub:doctoralDegreeFrom <http://www.University8.edu> . "
			+ "?X ub:researchInterest 'Research23' . " + "?X ub:name ?Y1 . " + "?X ub:emailAddress ?Y2 . "
			+ "?X ub:telephone ?Y3 . " + "?X ub:undergraduateDegreeFrom ?Y4 . " + "?X ub:mastersDegreeFrom ?Y5 . "
			+ "?X ub:teacherOf ?Y6 . " + "?Y7 ub:takesCourse ?Y6 . " + "?Y7 ub:name ?Y8 . " + "?Y7 ub:telephone ?Y9 . "
			+ "?Y7 ub:memberOf ?Y10 . " + "?Y7 ub:advisor ?Y11 . "
			+ "?Y11 ub:worksFor <http://www.Department0.University0.edu> . " + "}";

	// 12 triple patterns Star
	public static String QUERY_9 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 ?Y8 ?Y9 "
			+ "WHERE { ?X rdf:type ub:VisitingProfessor . "
			+ "?X ub:memberOf <http://www.Department1.University1.edu> . "
			+ "?X ub:teacherOf <http://www.Department0.University0.edu/Course1> . " + "?X ub:name ?Y1 . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . "
			+ "?X ub:undergraduateDegreeFrom <http://www.Department0.University0.edu> . "
			+ "?X ub:mastersDegreeFrom <http://www.Department0.University0.edu> . "
			+ "?X ub:doctoralDegreeFrom <http://www.Department0.University0.edu> . " + "?X ub:worksFor ?Y7 . "
			+ "?X ub:researchInterest ?Y8 . " + "?X ub:headOf ?Y9 . " + " }";

	// 15 triple patterns Composite
	public static String QUERY_10 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 ?Y8 ?Y9 "
			+ "WHERE { ?X rdf:type ub:FullProfessor . " + "?X ub:doctoralDegreeFrom <http://www.University8.edu> . "
			+ "?X ub:undergraduateDegreeFrom <http://www.University8.edu>  . "
			+ "?X ub:mastersDegreeFrom <http://www.University8.edu>  . " + "?X ub:researchInterest 'Research23' . "
			+ "?X ub:name ?Y1 . " + "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . " + "?X ub:teacherOf ?Y4 . "
			+ "?Y5 ub:takesCourse ?Y4 . " + "?Y5 ub:name ?Y6 . " + "?Y5 ub:telephone ?Y7 . " + "?Y5 ub:memberOf ?Y8 . "
			+ "?Y5 ub:advisor ?Y9 . " + "?Y9 ub:worksFor <http://www.Department0.University0.edu> . " + "}";

	// 15 triple patterns Star
	public static String QUERY_13 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y7 ?Y8 ?Y9 ?Y10 ?Y11 "
			+ "WHERE { ?X rdf:type ub:VisitingProfessor . "
			+ "?X ub:memberOf <http://www.Department1.University1.edu> . "
			+ "?X ub:teacherOf <http://www.Department0.University0.edu/Course1> . " + "?X ub:name ?Y1 . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . "
			+ "?X ub:undergraduateDegreeFrom <http://www.Department0.University0.edu> . "
			+ "?X ub:mastersDegreeFrom <http://www.Department0.University0.edu> . "
			+ "?X ub:doctoralDegreeFrom <http://www.Department0.University0.edu> . " + "?X ub:worksFor ?Y7 . "
			+ "?X ub:researchInterest ?Y8 . " + "?X ub:researchInterest 'Research2' . " + "?X ub:headOf ?Y9 . "
			+ "?X ub:teacherOf ?Y10. " + "?X ub:advisor ?Y11 ." + " }";

	// Empty query
	public static String QUERY_14 = LUBM_PREFIX + "SELECT ?X ?Y1 " + "WHERE { "
//	    + "?X rdf:type ub:FullProfessor . "
//	    + "?X ub:doctoralDegreeFrom <http://www.University703.edu> . "
//	    + "?X ub:teacherOf <http://www.Department5.University0.edu/GraduateCourse25> . " 
			+ "?X ub:researchInterest  'Research10' . " + "?Y1 ub:advisor ?X . " + "?X  ?P0  ?R0 . "
//	    + "?X ?P1 ?R1 . "
//	    + "?X ?P2 ?R2 . " 
			+ "} ";

	// Not empty query : one answers
	public static String QUERY_15 = LUBM_PREFIX + "SELECT ?X " + "WHERE { "
			+ "?X ub:undergraduateDegreeFrom <http://www.University303.edu> . " + "?X rdf:type ub:GraduateStudent. "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse65> . " + "}";

	// Not empty query: three answers
	public static String QUERY_16 = LUBM_PREFIX + "SELECT ?X " + "WHERE {  ?X rdf:type ub:Student . "
	// + "WHERE { ?X rdf:type ub:GraduateStudent . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>. " + "}";

	// Not Empty query, six answers
	public static String QUERY_17 = LUBM_PREFIX + "SELECT ?X  " + "WHERE { ?X rdf:type ub:Publication . "
			+ "?X ub:publicationAuthor <http://www.Department0.University0.edu/AssistantProfessor0> . " + "}";

	// Empty query with Filter clause
	public static String QUERY_18 = LUBM_PREFIX + "SELECT ?X ?Z " + "WHERE { " + "{?X rdf:type ub:FullProfessor . "
			+ "?X ub:publicationAuthor ?Y . " + "?X ub:age ?age . " + "Filter ((?age<60) && (?age>50))}"
			+ "{ ?Z rdf:type ub:AssociateProfessor . " + "?Z ub:publicationAuthor ?Y . " + "?Z ub:age ?age1 . "
			+ "Filter (?age1<40)}" + "}";

	// Not Empty query, 10634 answers
	public static String QUERY_19 = LUBM_PREFIX + "SELECT ?X ?Y " + "WHERE { ?X ub:publicationAuthor ?Y ." + "}";

	// Not Empty query, 41 answers
	public static String QUERY_20 = LUBM_PREFIX + "SELECT ?X ?Y"
			+ "WHERE { ?Y ub:worksFor <http://www.Department0.University0.edu> . " + "}";

	// Not Empty query, 460 answers
	public static String QUERY_21 = LUBM_PREFIX + "SELECT ?X ?Y " + "WHERE {  ?X ub:publicationAuthor ?Y. "
			+ " ?Y ub:worksFor <http://www.Department0.University0.edu> ." + "}";

	public static String QUERY_22 = LUBM_PREFIX + "" + " SELECT ?Course1 ?Person1 "
			+ "WHERE {?Course1 rdf:type ub:Course . " + "?Person1 ub:takesCourse ?Course1 . "
			+ "?Person1 rdf:type ub:Person" + "}";

	// Not empty query: four answers
	public static String QUERY_23 = LUBM_PREFIX + "SELECT ?X " + "WHERE {  ?X rdf:type ub:GraduateStudent . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>. " + "}";

	// Requête pour avoir tout les graduates students qui suivent le cour
	// GraduateCourse0
	// 6 triples
	public static String EDBT_QUERY_1 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y3 ?Y4 ?Y5 "
			+ "WHERE { ?X rdf:type ub:GraduateStudent . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>. "
			+ "?X ub:undergraduateDegreeFrom ?Y1 . " + "?X ub:telephone ?Y3. " + "?X ub:advisor ?Y4 ."
			+ "?Y5 ub:publicationAuthor ?X . " + " }";

	// Rechercher tout les etudiants qui suivent un cours particulier
	public static String EDBT_QUERY_2 = LUBM_PREFIX + "SELECT ?X " + "WHERE {  ?X rdf:type ub:UndergraduateStudent . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>. " + "}";

	// Requête pour avoir tout les étudiants ayant obtenu leurs diplômes
	// supérieurs à l'université 8, qui suivent GraduateCourse 65
	// au département 0 de l'université 0
	// et qui sont supervisé par un professeur ayant eu son doctorat à
	// l'université 0
	public static String EDBT_QUERY_3 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y7 ?Y8 ?Y9 "
			+ "WHERE { ?X rdf:type ub:GraduateStudent . "
			+ "?X ub:undergraduateDegreeFrom <http://www.University8.edu> . "
			+ "?X ub:mastersDegreeFrom <http://www.University8.edu> . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse65> . "
			+ "?X ub:emailAddress ?Y1 . " + "?X ub:telephone ?Y2 . " + "?X ub:teachingAssistantOf ?Y3 . "
			+ "?X ub:advisor ?Y4 ." + "?Y4 ub:doctoralDegreeFrom <http://www.University0.edu> . "
			+ "?Y5 ub:publicationAuthor ?X . " + " }";

	// Requête pour avoir toute les FullProfessor, chef de département ayant
	// obtenu leurs diplômes supérieurs à l'université Universty0
	// 12 triples
	public static String EDBT_QUERY_4 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 ?Y5 ?Y6 ?Y7 ?Y8 ?Y9 "
			+ "WHERE { ?X rdf:type ub:FullProfessor . " + "?X ub:headOf ?Y1 . "
			+ "?X ub:doctoralDegreeFrom <http://www.University0.edu> . "
			+ "?X ub:undergraduateDegreeFrom <http://www.University0.edu>  . "
			+ "?X ub:mastersDegreeFrom <http://www.University0.edu>  . " + "?X ub:researchInterest 'Research23' . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . " + "?X ub:teacherOf ?Y4 . " + "?Y5 ub:advisor ?X . "
			+ "?Y5 ub:telephone ?Y7 . " + "?Y5 ub:memberOf ?Y8 . " + "}";

	/**
	 * Queries uses on WWW paper of Huang
	 */
	// Empty query visitingProfessor is empty
	public static String WWW_QUERY_1 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 ?Y3 ?Y4 "
			+ "WHERE { ?X rdf:type ub:VisitingProfessor . "
			+ "?X ub:memberOf <http://www.Department1.University1.edu> . " + "?X ub:name ?Y1 . "
			+ "?X ub:emailAddress ?Y2 . " + "?X ub:telephone ?Y3 . " + "?X ub:undergraduateDegreeFrom ?Y4 . " + " }";

	// Empty query Professor is empty
	public static String WWW_QUERY_2 = LUBM_PREFIX + "SELECT ?X ?Y " + "WHERE { ?X rdf:type ub:Professor . "
			+ "?X ub:worksFor <http://www.Department0.University0.edu> . " + "?X ub:researchInterest 'Research2' . "
			+ "?X ub:doctoralDegreeFrom ?Y . " + "}";

	// Not empty query : one answers
	public static String WWW_QUERY_3 = LUBM_PREFIX + "SELECT ?X "
			+ "WHERE { ?X ub:undergraduateDegreeFrom <http://www.University303.edu> . "
			+ "?X rdf:type ub:GraduateStudent. "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse65> . " + "}";

	// Empty query: Not headOf for University
	public static String WWW_QUERY_4 = LUBM_PREFIX + "SELECT ?X ?Y " + "WHERE { ?X ub:advisor ?Y . "
			+ "?Y ub:headOf <http://www.University476.edu> . " + "}";

	// Empty query: triplet 2
	public static String WWW_QUERY_5 = LUBM_PREFIX + "SELECT ?X ?Y "
			+ "WHERE { <http://www.Department0.University0.edu/GraduateStudent73> ub:advisor ?Y . "
			+ "?Y ub:doctoralDegreeFrom <http://www.University0.edu> . " + "}";

	// Empty query triplet 1
	public static String WWW_QUERY_6 = LUBM_PREFIX + "SELECT ?X ?Y1 " + "WHERE { ?X ub:researchInterest 'Research28' . "
			+ "?Y1 ub:subOrganizationOf <http://www.University8.edu> . " + "?X rdf:type ub:Lecturer . "
			+ "?X ub:worksFor ?Y1 . " + "}";

	// Empty query T1/\T2, T1/\T3
	public static String WWW_QUERY_7 = LUBM_PREFIX + "SELECT ?X ?Y1 ?Y2 " + "WHERE { ?X rdf:type ub:FullProfessor . "
			+ "?X ub:doctoralDegreeFrom <http://www.University8.edu> . " + "?X ub:researchInterest 'Research23' . "
			+ "?X ub:teacherOf ?Y1 . " + "?Y2 ub:takesCourse ?Y1 . " + "}";

	public static String CONSTRUCT_QUERY_1 = LUBM_PREFIX
			+ "CONSTRUCT  {?X <http://www.Department0.University0.edu/GraduateCourse0> <http://www.Department0.University0.edu/GraduateCourse0> . ?X ?Y4 ?Y5 }"
			+ "WHERE { ?X rdf:type ub:GraduateStudent . "
			+ "?X ub:takesCourse <http://www.Department0.University0.edu/GraduateCourse0>. "
			+ "?X ub:undergraduateDegreeFrom ?Y1 . " + "?X ub:telephone ?Y3. " + "?X ub:advisor ?Y4 ."
			+ "?Y5 ub:publicationAuthor ?X . " + " }";

	public static String CONSTRUCT_QUERY_2 = LUBM_PREFIX + "CONSTRUCT  {}" + "WHERE {}";

}
