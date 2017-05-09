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
public class NoteContentDescriptor {
	
	private String ruleName = null;
	private String yesNote = null;
	private String noNote = null;
	private String yesHeading = null;
	private String noHeading = null;
	
    /**
     * @return the ruleName
     */
    public String getRuleName() {
    	return ruleName;
    }
	
    /**
     * @param ruleName the ruleName to set
     */
    public void setRuleName(String ruleName) {
    	this.ruleName = ruleName;
    }
	
    /**
     * @return the yesNote
     */
    public String getYesNote() {
    	return yesNote;
    }
	
    /**
     * @param yesNote the yesNote to set
     */
    public void setYesNote(String yesNote) {
    	this.yesNote = yesNote;
    }
	
    /**
     * @return the noNote
     */
    public String getNoNote() {
    	return noNote;
    }
	
    /**
     * @param noNote the noNote to set
     */
    public void setNoNote(String noNote) {
    	this.noNote = noNote;
    }

	
    /**
     * @return the yesHeading
     */
    public String getYesHeading() {
    	return yesHeading;
    }

	
    /**
     * @param yesHeading the yesHeading to set
     */
    public void setYesHeading(String yesHeading) {
    	this.yesHeading = yesHeading;
    }

	
    /**
     * @return the noHeading
     */
    public String getNoHeading() {
    	return noHeading;
    }

	
    /**
     * @param noHeading the noHeading to set
     */
    public void setNoHeading(String noHeading) {
    	this.noHeading = noHeading;
    }
	
   
	
	
}
