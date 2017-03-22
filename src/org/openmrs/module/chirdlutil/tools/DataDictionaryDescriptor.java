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
public class DataDictionaryDescriptor {
	
	
	private String filename = null;
	private String question = null;
	private String answer = null;
	private String leafText = null;
	private String promptText = null;
	
    /**
     * @return the filename
     */
    public String getFilename() {
    	return filename;
    }
	
    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
    	this.filename = filename;
    }
	
    /**
     * @return the question
     */
    public String getQuestion() {
    	return question;
    }
	
    /**
     * @param question the question to set
     */
    public void setQuestion(String question) {
    	this.question = question;
    }
	
    /**
     * @return the answer
     */
    public String getAnswer() {
    	return answer;
    }
	
    /**
     * @param answer the answer to set
     */
    public void setAnswer(String answer) {
    	this.answer = answer;
    }
	
    /**
     * @return the leafText
     */
    public String getLeafText() {
    	return leafText;
    }
	
    /**
     * @param leafText the leafText to set
     */
    public void setLeafText(String leafText) {
    	this.leafText = leafText;
    }
	
    /**
     * @return the promptText
     */
    public String getPromptText() {
    	return promptText;
    }
	
    /**
     * @param promptText the promptText to set
     */
    public void setPromptText(String promptText) {
    	this.promptText = promptText;
    }
	
	
}
