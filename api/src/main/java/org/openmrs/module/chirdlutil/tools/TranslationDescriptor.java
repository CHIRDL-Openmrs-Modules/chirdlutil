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
public class TranslationDescriptor {
	
	private String fileName = null;
	private String translation = null;
	private String english = null;
	
    /**
     * @param fileName2
     * @param translation2
     */
    public TranslationDescriptor(String fileName, String translation, String english) {
	    this.fileName = fileName;
	    this.translation = translation;
	    this.english = english;
    }

	/**
     * @return the fileName
     */
    public String getFileName() {
    	return fileName;
    }
	
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
    	this.fileName = fileName;
    }
	
    /**
     * @return the translation
     */
    public String getTranslation() {
    	return translation;
    }
	
    /**
     * @param translation the translation to set
     */
    public void setTranslation(String translation) {
    	this.translation = translation;
    }

	
    /**
     * @return the english
     */
    public String getEnglish() {
    	return english;
    }

	
    /**
     * @param english the english to set
     */
    public void setEnglish(String english) {
    	this.english = english;
    }
	
	
}
