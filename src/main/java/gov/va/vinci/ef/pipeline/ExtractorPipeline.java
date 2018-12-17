package gov.va.vinci.ef.pipeline;

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

import gov.va.vinci.ef.ae.*;
import gov.va.vinci.leo.conceptlink.ae.ConceptLinkAnnotator;
import gov.va.vinci.leo.conceptlink.ae.MatchMakerAnnotator;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.filter.ae.FilterAnnotator;
import gov.va.vinci.leo.regex.ae.RegexAnnotator;
import gov.va.vinci.leo.types.TypeLibrarian;
import gov.va.vinci.leo.window.ae.WindowAnnotator;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;

import java.util.regex.Pattern;

/**
 * Based on the Pipeline class this pipeline is a scalable, optimized echo extraction pipeline that attempts to zero in
 * on concepts and measurements as fast as possible.
 * <p>
 * Created by prakash on 12/06/201.
 * 
 */
public class ExtractorPipeline implements PipelineInterface {
  LeoAEDescriptor pipeline = null;
  LeoTypeSystemDescription description = null;

  protected static String RESOURCE_PATH = "resources/";

  public ExtractorPipeline() {
    this.getLeoTypeSystemDescription();
  }

  @Override
  public LeoAEDescriptor getPipeline() throws Exception {
    if (pipeline != null)
      return pipeline;

    //Build the pipeline
    LeoTypeSystemDescription types = getLeoTypeSystemDescription();
    pipeline = new LeoAEDescriptor();
    
    // Module 1: ConceptRegex -- find mentions of various concepts on ethnicity
    pipeline.addDelegate(new RegexAnnotator()
        .setResource(RESOURCE_PATH + "concept.regex")
        .setCaseSensitive(false)
        .setPatternFlags(Pattern.DOTALL)
        .setOutputType("gov.va.vinci.ef.types.Concept")
        .setName("ConceptRegex")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
    
    // Module 2: WindowAnnotator -- make a context window of -7...+7 tokens around the concept phrase
    pipeline.addDelegate(new WindowAnnotator()
        .setAnchorFeature("Anchor")
        .setWindowSize(7)
        .setInputTypes(new String[]{"gov.va.vinci.ef.types.Concept"})
        .setOutputType("gov.va.vinci.ef.types.ConceptWindow")
        .setName("ConceptWindowAnnotator")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
    
    // Module 3: ContextWindowAnnotator -- create a window of -15 ... +15 tokens around concept phrase
    pipeline.addDelegate(new WindowAnnotator()
        .setAnchorFeature("Anchor")
        .setWindowSize(15)
        .setInputTypes(new String[]{"gov.va.vinci.ef.types.Concept"})
        .setOutputType("gov.va.vinci.ef.types.ContextWindow")
        .setName("ContextWindowAnnotator")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
    
    // Module 4: MeasurementAnnotator -- find measurement value in the context window
    pipeline.addDelegate(new RegexAnnotator()
        .setResource(RESOURCE_PATH + "measure.regex")
        .setCaseSensitive(false)
        .setPatternFlags(Pattern.DOTALL)
        .setInputTypes(new String[]{"gov.va.vinci.ef.types.ContextWindow"})
        .setOutputType("gov.va.vinci.ef.types.Measurement")
        .setName("MeasurementRegex")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
    
  // Module 5: ConceptMeasureFilter -- Filter out all measurement values that do not meet context criteria
  pipeline.addDelegate(new MeasureFilter()
      .setRegexFilePath(RESOURCE_PATH + "invalidMeasure.regex")
      .setName("ConceptMeasureFilter")
      .addLeoTypeSystemDescription(types)
      .getDescriptor());
  
  //Module 9: AAValidatorAnnotator -- Validate if measure is "aa" and in upper case, otherwise remove from index. 
//  pipeline.addDelegate(new AAValidatorAnnotator()
//      .setRemoveIfAnyInvalid(false)
//      .setInputTypes(new String[]{"gov.va.vinci.ef.types.Measurement"})
//      .setName("AAValidatorAnnotator")
//      .addLeoTypeSystemDescription(types)
//      .getDescriptor());
    
    // Module 6: MeasurementValidatorAnnotator -- Filter out all measurement values that do not meet valid range criteria
    pipeline.addDelegate(new RangeValidatorAnnotator()
        .setRemoveIfAnyInvalid(false)
        .setInputTypes(new String[]{"gov.va.vinci.ef.types.Measurement"})
        .setName("MeasurementValidatorAnnotator")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
    
    // Module 7: ConceptToMeasureRelationAnnotator -- Create concept-measure pair based on the relational patterns between concept and measurement.
    pipeline.addDelegate(new MatchMakerAnnotator()
        .setConceptTypeName("gov.va.vinci.ef.types.Concept")
        .setValueTypeName("gov.va.vinci.ef.types.Measurement")
        .setPeekRightFirst(true)
        .setMaxCollectionSize(2)
        .setMaxDifference(2)
        .setMaxDistance(50)
        .setPatternFile(RESOURCE_PATH + "middleStuff.regex")
        .setRemoveCovered(true)
        .setInputTypes(new String[]{"gov.va.vinci.ef.types.Concept",
            "gov.va.vinci.ef.types.Measurement"})
        .setOutputType("gov.va.vinci.ef.types.PdiRelation")
        .setName("ConceptToMeasureRelationAnnotator")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
       
    // Module 8: FlattenRelationAE -- Create a new type for ease of writing results to csv or database
    pipeline.addDelegate(new FlattenRelationAE()
    	.setRegexFilePath(RESOURCE_PATH + "pdiMap.regex")
    	.setName("FlattenRelationAE")
        .addLeoTypeSystemDescription(types)
        .getDescriptor());
       
    /* Classify document. Uncomment ClassifyDocument delegate below, recompile the package and use the 
     * DatabaseListenerConfigClassify.groovy client listener configuration */    
//    pipeline.addDelegate(new LeoAEDescriptor()
//    		.setName("ClassifyDocument")
//    		.setImplementationName(gov.va.vinci.ef.ae.ClassifyDocument.class.getCanonicalName())
//    		.addTypeSystemDescription(types));
    

    return pipeline;
  }


  @Override
  public LeoTypeSystemDescription getLeoTypeSystemDescription() {
    if (description != null)
      return description;
    description = new LeoTypeSystemDescription();
    String linkParent = "gov.va.vinci.leo.conceptlink.types.Link";
    
    //ClassifyAttemt description
	TypeDescription classifyFtsd;
	String classifyParent = "gov.va.vinci.ef.types.Classify";
	classifyFtsd = new TypeDescription_impl(classifyParent, "", "uima.tcas.Annotation");
	classifyFtsd.addFeature("AfricanAmerican", "", "uima.cas.String");
	classifyFtsd.addFeature("Hispanic", "", "uima.cas.String");
    
    // Pattern Type description
    TypeDescription regexFtsd;
    String regexParent = "gov.va.vinci.ef.types.Regex";
    regexFtsd = new TypeDescription_impl(regexParent, "", "uima.tcas.Annotation");
    regexFtsd.addFeature("pattern", "", "uima.cas.String");
    regexFtsd.addFeature("concept", "", "uima.cas.String");
    regexFtsd.addFeature("groups", "", "uima.cas.StringArray");

    // Total type definition
    try {
      description.addType(TypeLibrarian.getCSITypeSystemDescription())
          .addTypeSystemDescription(new WindowAnnotator().getLeoTypeSystemDescription())
          .addTypeSystemDescription(new ConceptLinkAnnotator().getLeoTypeSystemDescription())
          .addType(regexFtsd)
          .addType(classifyFtsd)
          .addType("gov.va.vinci.ef.types.PdiRelation", "", linkParent)
          .addType("gov.va.vinci.ef.types.ConceptWindow", "", "gov.va.vinci.leo.window.types.Window")
          .addType("gov.va.vinci.ef.types.MeasurementWindow", "", "gov.va.vinci.leo.window.types.Window")
          .addType("gov.va.vinci.ef.types.ContextWindow", "", "gov.va.vinci.leo.window.types.Window")
          .addType("gov.va.vinci.ef.types.Anatomy", "", regexParent)
          .addType("gov.va.vinci.ef.types.Measurement", "", linkParent)
          .addType("gov.va.vinci.ef.types.Concept", "", linkParent)
          .addType("gov.va.vinci.ef.types.NumericValue", "", linkParent)
          .addType("gov.va.vinci.kttr.types.RefSt_EfValue", "", "uima.tcas.Annotation")
          .addTypeSystemDescription(new FlattenRelationAE().getLeoTypeSystemDescription())
      ;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return description;
  }
}
