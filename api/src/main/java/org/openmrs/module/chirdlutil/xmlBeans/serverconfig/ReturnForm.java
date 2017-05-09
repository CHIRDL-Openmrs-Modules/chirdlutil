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
package org.openmrs.module.chirdlutil.xmlBeans.serverconfig;


/**
 *
 * @author Steve McKee
 */
public class ReturnForm {

	private String startState;
	private String endState;
	
    /**
     * @return the startState
     */
    public String getStartState() {
    	return startState;
    }
	
    /**
     * @param startState the startState to set
     */
    public void setStartState(String startState) {
    	this.startState = startState;
    }
	
    /**
     * @return the endState
     */
    public String getEndState() {
    	return endState;
    }
	
    /**
     * @param endState the endState to set
     */
    public void setEndState(String endState) {
    	this.endState = endState;
    }
	
}
