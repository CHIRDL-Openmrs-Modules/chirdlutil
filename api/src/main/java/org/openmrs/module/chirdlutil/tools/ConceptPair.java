/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chirdlutil.tools;


/**
 *
 */
public class ConceptPair {
	
	private String questionConceptName = null;
	private String answerConceptName = null;
	
    /**
     * 
     * @param questionConceptName
     * @param answerConceptName
     */
    public ConceptPair(String questionConceptName, String answerConceptName) {
	    this.questionConceptName = questionConceptName;
	    this.answerConceptName = answerConceptName;
    }

	/**
     * @return the questionConceptName
     */
    public String getQuestionConceptName() {
    	return questionConceptName;
    }
	
    /**
     * @param questionConceptName the questionConceptName to set
     */
    public void setQuestionConceptName(String questionConceptName) {
    	this.questionConceptName = questionConceptName;
    }
	
    /**
     * @return the answerConceptName
     */
    public String getAnswerConceptName() {
    	return answerConceptName;
    }
	
    /**
     * @param answerConceptName the answerConceptName to set
     */
    public void setAnswerConceptName(String answerConceptName) {
    	this.answerConceptName = answerConceptName;
    }
    
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConceptPair [questionConceptName=" + questionConceptName + ", answerConceptName=" + answerConceptName
				+ "]";
	}
	
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (questionConceptName == null ? 0 : questionConceptName.hashCode());
        hash = hash * 31 + (answerConceptName == null ? 0 : answerConceptName.hashCode());
        
        return hash;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConceptPair other = (ConceptPair) obj;
		if (questionConceptName == null) {
			if (other.questionConceptName != null)
				return false;
		} else if (!questionConceptName.equals(other.questionConceptName))
			return false;
		if (answerConceptName == null) {
			if (other.answerConceptName != null)
				return false;
		} else if (!answerConceptName.equals(other.answerConceptName))
			return false;
		return true;
	}
}
