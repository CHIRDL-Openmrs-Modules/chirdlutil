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
public class MobileClient {

	private String user;
	private String primaryFormId;
	private String[] secondaryFormIds;
	
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
     * @return the secondaryFormIds
     */
    public String[] getSecondaryFormIds() {
    	return secondaryFormIds;
    }

    /**
     * @return the primaryFormId
     */
    public String getPrimaryFormId() {
    	return primaryFormId;
    }

    /**
     * @param primaryForm the primaryForm to set
     */
    public void setPrimaryFormId(String primaryFormId) {
    	this.primaryFormId = primaryFormId;
    }

	/**
     * @param secondaryFormIds the mobileForms to set
     */
    public void setSecondaryFormIds(String[] secondaryFormIds) {
    	this.secondaryFormIds = secondaryFormIds;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("MobileClient:\n");
    	buffer.append("\tuser: " + user + "\n");
    	buffer.append("\tprimaryFormId: " + primaryFormId + "\n");
    	if (secondaryFormIds != null && secondaryFormIds.length > 0) {
    		buffer.append("\tsecondaryFormIds:\n");
    		for (String id : secondaryFormIds) {
    			buffer.append("\t\t" + id + "\n");
    		}
    	}
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (user == null ? 0 : user.hashCode());
        hash = hash * 31 + (primaryFormId == null ? 0 : primaryFormId.hashCode());
        if (secondaryFormIds != null) {
	        for (String id : secondaryFormIds) {
	        	hash = hash * 13 + (id == null ? 0 : id.hashCode());
	        }
        }
        
        return hash;
    }
}
