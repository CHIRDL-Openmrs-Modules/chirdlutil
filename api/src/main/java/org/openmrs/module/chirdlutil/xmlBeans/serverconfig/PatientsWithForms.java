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
public class PatientsWithForms {

	private ArrayList<ReturnForm> returnForms;
	
    /**
     * @return the returnForms
     */
    public ArrayList<ReturnForm> getReturnForms() {
    	return returnForms;
    }
	
    /**
     * @param returnForms the returnForms to set
     */
    public void setReturnForms(ArrayList<ReturnForm> returnForms) {
    	this.returnForms = returnForms;
    }
	
}
