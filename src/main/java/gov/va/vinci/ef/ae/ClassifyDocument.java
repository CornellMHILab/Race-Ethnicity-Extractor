package gov.va.vinci.ef.ae;

/*
 * #%L
 * Echo Concept Extractor
 * %%
 * Copyright (C) 2017 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.ef.types.*;
import gov.va.vinci.ef.types.Relation;
import gov.va.vinci.ef.types.Classify;
// import gov.va.vinci.ef.types.Relation;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.AnnotationLibrarian;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;

/**
 * Classify document for AA race and hispanic ethnicity
 * Created by prakash on 01/11/18.
 */
public class ClassifyDocument extends LeoBaseAnnotator {

	private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.vinci.leo.ae.LeoBaseAnnotator#initialize(org.apache.uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		// initialize digit patterns
		String regex = "";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.vinci.leo.ae.LeoBaseAnnotator#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		// process each relation annotation to identify an overall classification for the document
		FSIterator<Annotation> relations = this.getAnnotationListForType(aJCas, Relation.class.getCanonicalName());
		boolean relFound = false;
		String docRaceScore = "";
		String docEthnicityScore = "";
		List<String> raceScoreList = new ArrayList<String>();
		List<String> ethnicityScoreList = new ArrayList<String>();

		while (relations.hasNext()) {
			relFound = true;
			try {
				Relation relation = (Relation) relations.next();

				String thisRaceScore = "0";
				if (relation.getRace() != null && !relation.getRace().isEmpty()) {
					thisRaceScore = getRaceScore(aJCas, relation);
				}
				raceScoreList.add(thisRaceScore);

				String thisEthnicityScore = "0";
				if (relation.getEthnicity() != null && !relation.getEthnicity().isEmpty()) {
					thisEthnicityScore = getEthnicityScore(aJCas, relation);
				}
				ethnicityScoreList.add(thisEthnicityScore);

			} catch (Exception e) {
				log.warn("Failed processing classify document.");
				e.printStackTrace();
			}
		}

		if (relFound == true) {
			if (raceScoreList.size() > 0) {
				// docRaceScore = findPopular(raceScoreList);
				if (scoreExist(raceScoreList, "1")) {
					docRaceScore = "1";
				}else {
					docRaceScore = "0";
				}

			}
			if (ethnicityScoreList.size() > 0) {
				// docEthnicityScore = findPopular(ethnicityScoreList);
				if (scoreExist(ethnicityScoreList, "1")) {
					docEthnicityScore = "1";
				}else {
					docEthnicityScore = "0";
				}
			}

			// System.out.println("Doc score: " + docRaceScore);

			Classify outClassify = new Classify(aJCas);
			if (docRaceScore != null && !docRaceScore.isEmpty()) {
				// set race
				outClassify.setAfricanAmerican(docRaceScore);
			}
			if (docEthnicityScore != null && !docEthnicityScore.isEmpty()) {
				// set ethnicity
				outClassify.setHispanic(docEthnicityScore);
			}
			// add to index
			outClassify.addToIndexes();
		}

	}

	/**
	 * get race score
	 */
	private String getRaceScore(JCas aJCas, Relation rel) {
		String score = "0";
		String[] aaRace = { "black", "african_american" };
		String race = "";
		race = rel.getRace();
		String chunks[] = race.split("\\|");
		for (int i = 0; i < chunks.length; i++) {
			String thisChunk = chunks[i];
			if (thisChunk != null && !thisChunk.isEmpty()) {
				for (int j = 0; j < aaRace.length; j++) {
					if (thisChunk.equals(aaRace[j])) {
						score = "1";
						break;
					}
				}
			}
		}

		return score;
	}

	/**
	* get ethnicity score
	*/
	private String getEthnicityScore(JCas aJCas, Relation rel) {
		String score = "0";
		String[] hispanicEthnicity = { "hispanic", "latino" };
		String ethnicity = "";
		ethnicity = rel.getEthnicity();
		String chunks[] = ethnicity.split("\\|");
		for (int i = 0; i < chunks.length; i++) {
			String thisChunk = chunks[i];
			if (thisChunk != null && !thisChunk.isEmpty()) {
				for (int j = 0; j < hispanicEthnicity.length; j++) {
					if (thisChunk.equals(hispanicEthnicity[j])) {
						score = "1";
						break;
					}
				}
			}
		}
		return score;
	}

	// check if item exist in array
	public boolean scoreExist(List<String> scoreList, String score) {
		boolean found = false;
		for (String item : scoreList) {
			if (item.length() > 0) {
				if (item.equals(score))
					found = true;
			}
		}
		return found;
	}

	// find popular item in an array
	public String findPopular(List<String> typeList) {

		Map<String, Integer> stringsCount = new HashMap<String, Integer>();
		for (String string : typeList) {
			if (string.length() > 0) {
				string = string.toUpperCase();
				Integer count = stringsCount.get(string);
				if (count == null)
					count = new Integer(0);
				count++;
				stringsCount.put(string, count);
			}
		}
		Map.Entry<String, Integer> mostRepeated = null;
		for (Map.Entry<String, Integer> e : stringsCount.entrySet()) {
			if (mostRepeated == null || mostRepeated.getValue() < e.getValue())
				mostRepeated = e;
		}
		try {
			return mostRepeated.getKey();
		} catch (NullPointerException e) {
			System.out.println("Cannot find most popular value at the List. Maybe all strings are empty");
			return "";
		}

	}

	/***/
	/**
	 * Normalization removes [,][;][=][:][multiple -][|][*] , replaces multiple whitespaces with one and sets to lower case
	 *
	 * @param concept
	 * @return
	 */

	private String normalize(String concept, boolean removeIfNumeric) {
		if (StringUtils.isNotBlank(concept)) {
			String str = concept.toLowerCase().replaceAll(",", " ").replaceAll(";", " ").replaceAll("==+", " ")
					// .replaceAll(":", "")
					.replaceAll("--+", " ").replaceAll("\\|", "").replaceAll("\\*", " ").replaceAll("\\s+", " ").trim();
			if (removeIfNumeric) {
				try {
					Double.parseDouble(str);
					return "";
				} catch (Exception e) {
					return str;
				}
			} else {
				return str;
			}
		} else {
			return "";
		}
	}

	@Override
	public LeoTypeSystemDescription getLeoTypeSystemDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
