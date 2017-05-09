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
public class MobileForm {

	private String id;
	private String name;
	private String startState;
	private String endState;
	private String pageUrl;
	
    /**
     * @return the name
     */
    public String getName() {
    	return name;
    }
	
    /**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }
	
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
	
    /**
     * @return the pageUrl
     */
    public String getPageUrl() {
    	return pageUrl;
    }
	
    /**
     * @param pageUrl the pageUrl to set
     */
    public void setPageUrl(String pageUrl) {
    	this.pageUrl = pageUrl;
    }

	/**
     * @return the id
     */
    public String getId() {
	    return id;
    }

	/**
     * @param id the id to set
     */
    public void setId(String id) {
	    this.id = id;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("MobileForm:\n");
    	buffer.append("\tid: " + id + "\n");
    	buffer.append("\tname: " + name + "\n");
    	buffer.append("\tstartState: " + startState + "\n");
    	buffer.append("\tendState: " + endState + "\n");
    	buffer.append("\tpageUrl: " + pageUrl + "\n");
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (id == null ? 0 : id.hashCode());
        hash = hash * 31 + (name == null ? 0 : name.hashCode());
        hash = hash * 45 + (startState == null ? 0 : startState.hashCode());
        hash = hash * 59 + (endState == null ? 0 : endState.hashCode());
        hash = hash * 73 + (pageUrl == null ? 0 : pageUrl.hashCode());
        
        return hash;
    }
}
