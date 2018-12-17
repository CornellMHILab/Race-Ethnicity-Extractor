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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import gov.va.vinci.leo.AnnotationLibrarian;
import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import org.apache.commons.validator.GenericValidator;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate measure annotations for the special case of "AA" measure for african american.
 * Created by prakash on 01/11/18.
 */
public class AAValidatorAnnotator extends LeoBaseAnnotator {

	/**
	 * Pattern for finding numbers in the covered text.
	 */
	protected Pattern aaPattern = Pattern.compile("\\s{1,4}aa\\s{1,4}");

	/**
	 * Remove the annotation if measure is given as "aa" in lower case. are invalid.
	 */
	@LeoConfigurationParameter
	protected boolean removeIfAnyInvalid = true;

	@Override
	public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
		for (String inputType : inputTypes) {
			try {
				Collection<Annotation> annotations = AnnotationLibrarian.getAllAnnotationsOfType(aJCas, inputType, false);
				for (Annotation a : annotations) {
					String covered = a.getCoveredText();
					
					System.out.println("Covered " + covered);
					
					Matcher m = aaPattern.matcher(covered);
					boolean foundPattern = false, isUpper = false;
					while (m.find()) {
						foundPattern = true;
						String strVal = covered.substring(m.start(), m.end());
						if (strVal.equals(strVal.toUpperCase())) {
							isUpper = true;
						}
					}
					if (foundPattern && !isUpper)
						a.removeFromIndexes();
				}
			} catch (CASException e) {
				throw new AnalysisEngineProcessException(e);
			}
		}
	}

	public boolean isRemoveIfAnyInvalid() {
		return removeIfAnyInvalid;
	}

	public AAValidatorAnnotator setRemoveIfAnyInvalid(boolean removeIfAnyInvalid) {
		this.removeIfAnyInvalid = removeIfAnyInvalid;
		return this;
	}

}
