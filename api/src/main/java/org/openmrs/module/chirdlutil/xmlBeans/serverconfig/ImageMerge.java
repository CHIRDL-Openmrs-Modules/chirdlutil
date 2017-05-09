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
 * Holds the information for merging an image onto a PDF document.
 * 
 * @author Steve McKee
 */
public class ImageMerge {
	
	private String fieldName;
	private Integer pageNumber;
	private Float positionX;
	private Float positionY;
	private Float rotation;
	
    /**
     * @return the fieldName
     */
    public String getFieldName() {
    	return fieldName;
    }
	
    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
    	this.fieldName = fieldName;
    }
	
    /**
     * @return the pageNumber
     */
    public Integer getPageNumber() {
    	return pageNumber;
    }
	
    /**
     * @param pageNumber the pageNumber to set
     */
    public void setPageNumber(Integer pageNumber) {
    	this.pageNumber = pageNumber;
    }
	
    /**
     * @return the positionX
     */
    public Float getPositionX() {
    	return positionX;
    }
	
    /**
     * @param positionX the positionX to set
     */
    public void setPositionX(Float positionX) {
    	this.positionX = positionX;
    }
	
    /**
     * @return the positionY
     */
    public Float getPositionY() {
    	return positionY;
    }
	
    /**
     * @param positionY the positionY to set
     */
    public void setPositionY(Float positionY) {
    	this.positionY = positionY;
    }
	
    /**
     * @return the rotation
     */
    public Float getRotation() {
    	return rotation;
    }
	
    /**
     * @param rotation the rotation to set
     */
    public void setRotation(Float rotation) {
    	this.rotation = rotation;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("ImageMerge:\n");
    	buffer.append("\tfieldName: " + fieldName + "\n");
    	buffer.append("\tpageNumber: " + pageNumber + "\n");
    	buffer.append("\tpositionX: " + positionX + "\n");
    	buffer.append("\tpositionY: " + positionY + "\n");
    	buffer.append("\trotation: " + rotation + "\n");
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (fieldName == null ? 0 : fieldName.hashCode());
        hash = hash * 31 + (pageNumber == null ? 0 : pageNumber.hashCode());
        hash = hash * 31 + (positionX == null ? 0 : positionX.hashCode());
        hash = hash * 31 + (positionY == null ? 0 : positionY.hashCode());
        hash = hash * 31 + (rotation == null ? 0 : rotation.hashCode());
        
        return hash;
    }
}
