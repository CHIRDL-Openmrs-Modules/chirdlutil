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
 * Bean containing the information provided from the concept import file.
 * 
 * @author Tammy Dugan
 */
public class PriorityDescriptor {
	
	private String name = null;
	private String newPriority = null;
	
	/**
	 * Constructor Method
	 */
	public PriorityDescriptor() {
	}

	
    /**
     * @return the name
     */
    public String getName() {
    	return this.name;
    }

	
    /**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }

    /**
     * @return the newPriority
     */
    public String getNewPriority() {
    	return this.newPriority;
    }

	
    /**
     * @param newPriority the newPriority to set
     */
    public void setNewPriority(String newPriority) {
    	this.newPriority = newPriority;
    }

	
	
}
