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
        return this.fieldName;
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
        return this.pageNumber;
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
        return this.positionX;
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
        return this.positionY;
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
        return this.rotation;
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
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("ImageMerge:\n");
        buffer.append("\tfieldName: " + this.fieldName + "\n");
        buffer.append("\tpageNumber: " + this.pageNumber + "\n");
        buffer.append("\tpositionX: " + this.positionX + "\n");
        buffer.append("\tpositionY: " + this.positionY + "\n");
        buffer.append("\trotation: " + this.rotation + "\n");
        
        return buffer.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (this.fieldName == null ? 0 : this.fieldName.hashCode());
        hash = hash * 31 + (this.pageNumber == null ? 0 : this.pageNumber.hashCode());
        hash = hash * 31 + (this.positionX == null ? 0 : this.positionX.hashCode());
        hash = hash * 31 + (this.positionY == null ? 0 : this.positionY.hashCode());
        hash = hash * 31 + (this.rotation == null ? 0 : this.rotation.hashCode());
        
        return hash;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImageMerge other = (ImageMerge) obj;
        if (this.fieldName == null) {
            if (other.fieldName != null) {
                return false;
            }
        } else if (!this.fieldName.equals(other.fieldName)) {
            return false;
        }
        if (this.pageNumber == null) {
            if (other.pageNumber != null) {
                return false;
            }
        } else if (!this.pageNumber.equals(other.pageNumber)) {
            return false;
        }
        if (this.positionX == null) {
            if (other.positionX != null) {
                return false;
            }
        } else if (!this.positionX.equals(other.positionX)) {
            return false;
        }
        if (this.positionY == null) {
            if (other.positionY != null) {
                return false;
            }
        } else if (!this.positionY.equals(other.positionY)) {
            return false;
        }
        if (this.rotation == null) {
            if (other.rotation != null) {
                return false;
            }
        } else if (!this.rotation.equals(other.rotation)) {
            return false;
        }
        return true;
    }
}
