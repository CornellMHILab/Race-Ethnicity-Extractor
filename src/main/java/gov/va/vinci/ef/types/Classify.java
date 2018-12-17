

/* First created by JCasGen Wed Jan 11 09:29:43 MST 2017 */
package gov.va.vinci.ef.types;

/*
 * #%L
 * Document classifier
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
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Jan 11 09:29:43 MST 2017
 * XML source: /var/folders/9p/6qt9dlgd1ks8d036hkqb9kz00000gn/T/leoTypeDescription_42fa7ddf-ce58-47c5-bbbd-f2621b629d817793577776427376679.xml
 * @generated */
public class Classify extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Classify.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Classify() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Classify(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Classify(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
  
  //*--------------*
  //* Feature: AfricanAmerican

  /** getter for AfricanAmerican - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAfricanAmerican() {
    if (Classify_Type.featOkTst && ((Classify_Type)jcasType).casFeat_AfricanAmerican == null)
      jcasType.jcas.throwFeatMissing("AfricanAmerican", "gov.va.vinci.echo.types.Classify");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Classify_Type)jcasType).casFeatCode_AfricanAmerican);}
    
  /** setter for AfricanAmerican - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAfricanAmerican(String v) {
    if (Classify_Type.featOkTst && ((Classify_Type)jcasType).casFeat_AfricanAmerican == null)
      jcasType.jcas.throwFeatMissing("AfricanAmerican", "gov.va.vinci.echo.types.Classify");
    jcasType.ll_cas.ll_setStringValue(addr, ((Classify_Type)jcasType).casFeatCode_AfricanAmerican, v);}
   
//*--------------*
//* Feature: Hispanic

/** getter for Hispanic - gets 
 * @generated
 * @return value of the feature 
 */
public String getHispanic() {
  if (Classify_Type.featOkTst && ((Classify_Type)jcasType).casFeat_Hispanic == null)
    jcasType.jcas.throwFeatMissing("Hispanic", "gov.va.vinci.echo.types.Classify");
  return jcasType.ll_cas.ll_getStringValue(addr, ((Classify_Type)jcasType).casFeatCode_Hispanic);}
  
/** setter for Hispanic - sets  
 * @generated
 * @param v value to set into the feature 
 */
public void setHispanic(String v) {
  if (Classify_Type.featOkTst && ((Classify_Type)jcasType).casFeat_Hispanic == null)
    jcasType.jcas.throwFeatMissing("Hispanic", "gov.va.vinci.echo.types.Classify");
  jcasType.ll_cas.ll_setStringValue(addr, ((Classify_Type)jcasType).casFeatCode_Hispanic, v);}


}
    