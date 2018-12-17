package gov.va.vinci.ef.ae;

/*
 * #%L
 * Echo concept exctractor
 * %%
 * Copyright (C) 2010 - 2016 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or conclied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import gov.va.vinci.ef.types.*;
import gov.va.vinci.leo.AnnotationLibrarian;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.types.TypeLibrarian;
import gov.va.vinci.leo.window.WindowService;
import gov.va.vinci.leo.window.types.Window;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Flatten relationships for easier output.
 * <p>
 * Created by vhaslcpatteo on 9/16/2015. Edited by Thomas Ginter on 09/18/2015. Added the setValueStrings method. 
 * Prakash added several methods specific to race and ethnicity extraction.
 */

public class FlattenRelationAE extends LeoBaseAnnotator {

	/**
	 * Path to the regex file to parse.
	 */
	@LeoConfigurationParameter

	protected String regexFilePath = null;

	/**
	 * Patterns for which if there is a match in the window before the anchor then the relationship is invalid.
	 */
	protected Pattern[] leftPatterns = null;
	protected Pattern[] rightPatterns = null;

	protected Pattern conceptPattern = Pattern.compile(
			"\\b(male|man|female|woman|boy|girl|lady|gentleman|ethnic(ity)?|race|interpreter|identity|origin|background|descent|ancestry|nationality)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern asianPattern = Pattern.compile(
			"\\b((?<!(american|native)\\s{1,3})indian|gujarati|bengali|malayali|tamil|hindi|asian|chinese|mandarin|japanese|korean|vietnamese|burmese|thai|laotian|cambodian|(f|ph)ilipino|sri\\s{0,3}(\\s|-|_)\\s{0,3}lankan|oriental|pakistani?|bangladeshi?)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern blackPattern = Pattern.compile(
			"\\b(african\\s{0,3}(\\s|-|_)\\s{0,3}american|black|AA|haitian|(?<!north\\s{0,3}(-|_)?\\s{0,3})african|sudan|(ni|al)geria|ethiopia|kenya|ghana|uganda|cameroon|congo|zambia)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern hawaiianPattern = Pattern.compile(
			"\\b(hawaiian|pacific\\s{0,3}(\\s|-|_)\\s{0,3}islander|malaysian|polynesian|austronesian)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern nativePattern = Pattern.compile(
			"\\b(native\\s{0,3}(\\s|-|_)\\s{0,3}(american|alaskan)|american\\s{0,3}(\\s|-|_)\\s{0,3}indian|eskimo|alaska\\s{0,3}(\\s|-|_)\\s{0,3}nation|alaskan\\s{0,3}(\\s|-|_)\\s{0,3}native)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern whitePattern = Pattern.compile(
			"\\b(white|caucasian|anglo\\s{0,3}(\\s|-|_)\\s{0,3}saxon|canadian|caucasoid|european|swedish|swiss|polish|danish|dutch|scandinavian|irish|spain|russian|german|french|jewish|israeli?|itlatin|latin|arab(ian)?|egypt(ian)?|libyan?|morocc(o|an)|middle\\s{0,3}(\\s|-|_)\\s{0,3}eastern|north\\s{0,3}(\\s|-|_)\\s{0,3}african|egyptian|syrian|afghan|parsi)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern mixedPattern = Pattern.compile("\\b(mixed|multi|bi)\\s{0,3}(\\s|-|_)\\s{0,3}(race|racial)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	protected Pattern hispanicPattern = Pattern.compile(
			"\\b(south\\s{0,3}(\\s|-|_)\\s{0,3}american|central\\s{0,3}(\\s|-|_)\\s{0,3}american|cuban|mexican|puerto\\s{0,3}(\\s|-|_)\\s{0,3}rican|dominican|jamaican|haitian|(?<!no(n|t)\\s{0,3}(\\s|-|_)\\s{0,3})hispanic|latin(a|o)|spanish)\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	protected Pattern nonHispanicPattern = Pattern.compile("no(n|t)\\s{0,3}(\\s|-|_)\\s{0,3}(hispanic|latin(a|o))\\b",
			Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

	/**
	 * Window service class.
	 */
	protected WindowService windowService = new WindowService(0, 20, Window.class.getCanonicalName());

	/**
	 * Pattern flags for each regex.
	 */
	public static int PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;

	/**
	 * Log messages
	 */
	private static final Logger log = Logger.getLogger(LeoUtils.getRuntimeClass());

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		if (StringUtils.isBlank(regexFilePath))
			throw new ResourceInitializationException("regexFilePath cannot be blank or null", null);
		try {
			parseRegexFile(new File(regexFilePath));
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {

		// get today's date
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		Collection<PdiRelation> relations = AnnotationLibrarian.getAllAnnotationsOfType(aJCas, PdiRelation.type, false);
		if (relations.size() > 0) {

			for (PdiRelation relation : relations) {

				// Create the output annotation

				int relBegin = 0;
				int relEnd = 0;
				if (relation.getBegin() <= 10) {
					relBegin = 0;
				} else {
					relBegin = relation.getBegin() - 10;
				}
				relEnd = relation.getEnd() + 10;
				Relation out = new Relation(aJCas, relBegin, relEnd);

				// set run date
				out.setRunDate(timeStamp);

				out.addToIndexes();
				// Set the string value features
				setValueStrings(relation, out);
			}
		}
	}

	protected void setValueStrings(PdiRelation in, Relation out) {

		// Get the NumericValue annotation from the merged set
		Annotation value = null;
		Annotation measure = null;
		FSArray merged = in.getLinked();
		for (int i = 0; i < merged.size(); i++) {
			Annotation a = (Annotation) merged.get(i);
			String typeName = a.getType().getName();
			if (typeName.equals(Measurement.class.getCanonicalName())) {
				measure = a;
				if (measure != null) {
					// set term
					out.setTerm(measure.getCoveredText());

					// get race
					Map<String, String> race = new HashMap<String, String>();
					race = findRace(measure.getCoveredText());
					String rterm = "";
					for (Map.Entry<String, String> entry : race.entrySet()) {
						if (!rterm.isEmpty()) {
							rterm = rterm + "|";
						}
						rterm = rterm + entry.getKey();
					}
					if (!rterm.isEmpty()) {
						out.setRace(rterm);
					}

					// get ethnicity
					Map<String, String> ethnicity = new HashMap<String, String>();
					ethnicity = findEthnicity(measure.getCoveredText());
					String eterm = "";
					for (Map.Entry<String, String> entry : ethnicity.entrySet()) {
						if (!eterm.isEmpty()) {
							eterm = eterm + "|";
						}
						eterm = eterm + entry.getKey();
					}
					if (!eterm.isEmpty()) {
						out.setEthnicity(eterm);
					}
				}
			} else if (typeName.equals(Concept.class.getCanonicalName())) {
				value = a;
			}
		}

		// Get the values
		String conceptText = value.getCoveredText();
		Matcher m = conceptPattern.matcher(conceptText);
		ArrayList<String> values = new ArrayList<String>(2);
		while (m.find())
			values.add(conceptText.substring(m.start(), m.end()));
		Collections.sort(values);

		// Set the values
		if (values.size() > 0)
			out.setValue(values.get(0).toString());
		if (values.size() > 1)
			out.setValue2(values.get(values.size() - 1).toString());

		// Exit if no value for measurement found
		if (measure == null)
			return;
		// Get the measurement text
		String measureText = measure.getCoveredText();
		if (StringUtils.isNotBlank(measureText))
			out.setValueString(measureText);

	}

	/**
	 * Returns Race , if exist.
	 *
	 * @param patterns
	 * @param text
	 * @return
	 */
	protected Map<String, String> findRace(String text) {

		Map<String, String> raceMap = new HashMap<String, String>();

		Matcher asians = asianPattern.matcher(text);
		while (asians.find()) {
			raceMap.put("asian", text.substring(asians.start(), asians.end()));
			break;
		}

		Matcher blacks = blackPattern.matcher(text);
		while (blacks.find()) {
			raceMap.put("black", text.substring(blacks.start(), blacks.end()));
			break;
		}

		Matcher hawaiians = hawaiianPattern.matcher(text);
		while (hawaiians.find()) {
			raceMap.put("hawaiian", text.substring(hawaiians.start(), hawaiians.end()));
			break;
		}

		Matcher natives = nativePattern.matcher(text);
		while (natives.find()) {
			raceMap.put("native_american", text.substring(natives.start(), natives.end()));
			break;
		}

		Matcher white = whitePattern.matcher(text);
		while (white.find()) {
			raceMap.put("white", text.substring(white.start(), white.end()));
			break;
		}

		Matcher mixed = mixedPattern.matcher(text);
		while (mixed.find()) {
			raceMap.put("mixed", text.substring(mixed.start(), mixed.end()));
			break;
		}

		return raceMap;
	}

	/**
	 * Returns Ethnicity , if exist.
	 *
	 * @param patterns
	 * @param text
	 * @return
	 */
	protected Map<String, String> findEthnicity(String text) {

		Map<String, String> ethnicMap = new HashMap<String, String>();

		Matcher hispanics = hispanicPattern.matcher(text);
		while (hispanics.find()) {
			ethnicMap.put("hispanic", text.substring(hispanics.start(), hispanics.end()));
			break;
		}

		Matcher nonHispanics = nonHispanicPattern.matcher(text);
		while (nonHispanics.find()) {
			ethnicMap.put("non-hispanic", text.substring(nonHispanics.start(), nonHispanics.end()));
			break;
		}

		return ethnicMap;
	}

	/**
	 * Get the patterns from the regex file and stash them in the appropriate lists.
	 *
	 * @param regexFile
	 *            File from which to retrieve the patterns
	 * @throws IOException
	 *             If there is an error reading the file.
	 */
	protected void parseRegexFile(File regexFile) throws IOException {
		if (regexFile == null)
			throw new IllegalArgumentException("regexFile cannot be null");
		// List of Patterns to compile
		ArrayList<Pattern> leftList = new ArrayList<Pattern>();
		ArrayList<Pattern> rightList = new ArrayList<Pattern>();

		int patternType = 3;
		// Read in the lines of the regex file
		List<String> lines = IOUtils.readLines(FileUtils.openInputStream(regexFile));
		for (String line : lines) {
			if (line.startsWith("#")) {
				if (line.startsWith("#LEFTMAP"))
					patternType = 1;
				else if (line.startsWith("#RIGHTMAP"))
					patternType = 2;
			} else if (StringUtils.isNotBlank(line)) {
				if (patternType == 1)
					leftList.add(Pattern.compile(line, PATTERN_FLAGS));
				else if (patternType == 2)
					rightList.add(Pattern.compile(line, PATTERN_FLAGS));
			}
		}
		// Stash each collection
		leftPatterns = leftList.toArray(new Pattern[leftList.size()]);
		rightPatterns = rightList.toArray(new Pattern[rightList.size()]);

	}

	@Override
	public LeoTypeSystemDescription getLeoTypeSystemDescription() {
		TypeDescription relationFtsd;
		String relationParent = "gov.va.vinci.ef.types.Relation";
		String linkParent = "gov.va.vinci.leo.conceptlink.types.Link";
		relationFtsd = new TypeDescription_impl(relationParent, "", "uima.tcas.Annotation");

		relationFtsd.addFeature("RunDate", "", "uima.cas.String");

		// Extracted Term
		relationFtsd.addFeature("Term", "", "uima.cas.String");

		// Race value
		relationFtsd.addFeature("Race", "", "uima.cas.String");

		// Ethnicity value
		relationFtsd.addFeature("Ethnicity", "", "uima.cas.String");

		// Numeric value
		relationFtsd.addFeature("Value", "", "uima.cas.String");

		// Numeric value 2
		relationFtsd.addFeature("Value2", "", "uima.cas.String");

		// String value with units and extra modifiers
		relationFtsd.addFeature("ValueString", "", "uima.cas.String");

		// Snippet value with units and extra modifiers
		relationFtsd.addFeature("SnippetString", "", "uima.cas.String");

		LeoTypeSystemDescription types = new LeoTypeSystemDescription();
		try {
			types.addType(relationFtsd);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return types;
	}

	public String getRegexFilePath() {
		return regexFilePath;
	}

	public FlattenRelationAE setRegexFilePath(String regexFilePath) {
		this.regexFilePath = regexFilePath;
		return this;
	}

}
