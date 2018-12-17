
/* First created by JCasGen Wed Jan 11 09:29:43 MST 2017 */
package gov.va.vinci.ef.types;

/*
 * #%L
 * Document Classifier
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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Jan 11 09:29:43 MST 2017
 * @generated */
public class Classify_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Classify_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Classify_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Classify(addr, Classify_Type.this);
  			   Classify_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Classify(addr, Classify_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Classify.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.echo.types.Classify");
 
  /** @generated */
  final Feature casFeat_AfricanAmerican;
  /** @generated */
  final int     casFeatCode_AfricanAmerican;
  /** @generated */
  final Feature casFeat_Hispanic;
  /** @generated */
  final int     casFeatCode_Hispanic;
  
  
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAfricanAmerican(int addr) {
        if (featOkTst && casFeat_AfricanAmerican == null)
      jcas.throwFeatMissing("AfricanAmerican", "gov.va.vinci.echo.types.Classify");
    return ll_cas.ll_getStringValue(addr, casFeatCode_AfricanAmerican);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAfricanAmerican(int addr, String v) {
        if (featOkTst && casFeat_AfricanAmerican == null)
      jcas.throwFeatMissing("AfricanAmerican", "gov.va.vinci.echo.types.Classify");
    ll_cas.ll_setStringValue(addr, casFeatCode_AfricanAmerican, v);}

  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getHispanic(int addr) {
        if (featOkTst && casFeat_Hispanic == null)
      jcas.throwFeatMissing("Hispanic", "gov.va.vinci.echo.types.Classify");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Hispanic);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setHispanic(int addr, String v) {
        if (featOkTst && casFeat_Hispanic == null)
      jcas.throwFeatMissing("Hispanic", "gov.va.vinci.echo.types.Classify");
    ll_cas.ll_setStringValue(addr, casFeatCode_Hispanic, v);}
  
  
  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Classify_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

    casFeat_AfricanAmerican = jcas.getRequiredFeatureDE(casType, "AfricanAmerican", "uima.cas.String", featOkTst);
    casFeatCode_AfricanAmerican  = (null == casFeat_AfricanAmerican) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_AfricanAmerican).getCode();
       
    casFeat_Hispanic = jcas.getRequiredFeatureDE(casType, "Hispanic", "uima.cas.String", featOkTst);
    casFeatCode_Hispanic  = (null == casFeat_Hispanic) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Hispanic).getCode();

  }
}



    