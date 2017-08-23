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

import java.util.Arrays;

import org.openmrs.module.chirdlutil.util.SecondaryFormComparator;

/**
 * Bean to hold the mobile client information
 * 
 * @author Steve McKee
 */
public class MobileClient {

	private String user;
	private String primaryFormId;
	private Double maxSecondaryFormWeight;
	private SecondaryForm[] secondaryForms;
	
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
     * @return the secondaryForms
     */
    public SecondaryForm[] getSecondaryForms() {
    	if (secondaryForms == null) {
    		return null;
    	}
    	
    	Arrays.sort(secondaryForms, new SecondaryFormComparator());
    	return secondaryForms;
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
	 * @return the maxSecondaryFormWeight
	 */
	public Double getMaxSecondaryFormWeight() {
		return maxSecondaryFormWeight;
	}
	
	/**
	 * @param maxSecondaryFormWeight the maxSecondaryFormWeight to set
	 */
	public void setMaxSecondaryFormWeight(Double maxSecondaryFormWeight) {
		this.maxSecondaryFormWeight = maxSecondaryFormWeight;
	}

	/**
     * @param secondaryForms the mobileForms to set
     */
    public void setSecondaryForms(SecondaryForm[] secondaryForms) {
    	this.secondaryForms = secondaryForms;
    }

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxSecondaryFormWeight == null) ? 0 : maxSecondaryFormWeight.hashCode());
		result = prime * result + ((primaryFormId == null) ? 0 : primaryFormId.hashCode());
		result = prime * result + Arrays.hashCode(secondaryForms);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		MobileClient other = (MobileClient) obj;
		if (maxSecondaryFormWeight == null) {
			if (other.maxSecondaryFormWeight != null)
				return false;
		} else if (!maxSecondaryFormWeight.equals(other.maxSecondaryFormWeight))
			return false;
		if (primaryFormId == null) {
			if (other.primaryFormId != null)
				return false;
		} else if (!primaryFormId.equals(other.primaryFormId))
			return false;
		if (!Arrays.equals(secondaryForms, other.secondaryForms))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MobileClient [user=" + user + ", primaryFormId=" + primaryFormId + ", maxSecondaryFormWeight="
		        + maxSecondaryFormWeight + ", secondaryForms=" + Arrays.toString(secondaryForms) + "]";
	}
}
