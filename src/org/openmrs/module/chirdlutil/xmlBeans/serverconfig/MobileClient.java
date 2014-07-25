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

import java.util.ArrayList;


/**
 *
 * @author Steve McKee
 */
public class MobileClient {

	private String user;
	private MobileForms mobileForms;
	
    /**
     * @return the user
     */
    public String getUser() {
    	return user;
    }
	
    /**
     * @param user the user to set
     */
    public void setUser(String user) {
    	this.user = user;
    }
	
    /**
     * @return the mobileForms
     */
    public MobileForms getMobileForms() {
    	return mobileForms;
    }

    /**
     * @param mobileForms the mobileForms to set
     */
    public void setMobileForms(MobileForms mobileForms) {
    	this.mobileForms = mobileForms;
    }
}
