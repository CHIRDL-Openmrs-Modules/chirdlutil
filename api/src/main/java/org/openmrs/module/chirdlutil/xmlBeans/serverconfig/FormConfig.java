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
import java.util.List;


/**
 *
 * @author Steve McKee
 */
public class FormConfig {
	
	private ArrayList<MobileForm> forms;

	/**
     * @return the forms
     */
    public ArrayList<MobileForm> getForms() {
	    return forms;
    }

	/**
     * @param forms the forms to set
     */
    public void setForms(ArrayList<MobileForm> forms) {
	    this.forms = forms;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("FormConfig:\n");
    	if (forms != null && forms.size() > 0) {
    		for (MobileForm form : forms) {
    			buffer.append(form.toString());
    		}
    	} else {
    		buffer.append("\tNo forms exist.");
    	}
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        if (forms != null) {
	        for (MobileForm form : forms) {
	        	hash = hash * 13 + (form == null ? 0 : form.hashCode());
	        }
        }
        
        return hash;
    }
}
