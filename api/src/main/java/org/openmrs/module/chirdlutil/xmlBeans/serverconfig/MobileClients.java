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
public class MobileClients {

	private ArrayList<MobileClient> mobileClients;

    /**
     * @return the mobileClients
     */
    public ArrayList<MobileClient> getMobileClients() {
    	return mobileClients;
    }
	
    /**
     * @param mobileClients the mobileClients to set
     */
    public void setMobileClients(ArrayList<MobileClient> mobileClients) {
    	this.mobileClients = mobileClients;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("MobileClients:\n");
    	if (mobileClients != null && mobileClients.size() > 0) {
    		for (MobileClient client : mobileClients) {
    			buffer.append(client.toString());
    		}
    	} else {
    		buffer.append("\tNo mobile clients exist.");
    	}
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        if (mobileClients != null) {
	        for (MobileClient client : mobileClients) {
	        	hash = hash * 13 + (client == null ? 0 : client.hashCode());
	        }
        }
        
        return hash;
    }
}
