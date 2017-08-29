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
	private Double weight;
	
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
	 * @return the weight
	 */
	public Double getWeight() {
		return weight;
	}
	
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}

    /**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MobileForm [id=" + id + ", name=" + name + ", startState=" + startState + ", endState=" + endState
		        + ", pageUrl=" + pageUrl + ", weight=" + weight + "]";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endState == null) ? 0 : endState.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pageUrl == null) ? 0 : pageUrl.hashCode());
		result = prime * result + ((startState == null) ? 0 : startState.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MobileForm other = (MobileForm) obj;
		if (endState == null) {
			if (other.endState != null)
				return false;
		} else if (!endState.equals(other.endState))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pageUrl == null) {
			if (other.pageUrl != null)
				return false;
		} else if (!pageUrl.equals(other.pageUrl))
			return false;
		if (startState == null) {
			if (other.startState != null)
				return false;
		} else if (!startState.equals(other.startState))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
}
