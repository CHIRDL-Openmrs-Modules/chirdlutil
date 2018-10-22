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
 * Contains the image merge information for a given form.
 * 
 * @author Steve McKee
 */
public class ImageForm {
    
    private ArrayList<ImageMerge> imageMerges;
    private String name;
    
    /**
     * @return the imageMerges
     */
    public ArrayList<ImageMerge> getImageMerges() {
        return this.imageMerges;
    }
    
    /**
     * @param imageMerges the imageMerges to set
     */
    public void setImageMerges(ArrayList<ImageMerge> imageMerges) {
        this.imageMerges = imageMerges;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("ImageForm: " + this.name + "\n");
        if (this.imageMerges != null && this.imageMerges.size() > 0) {
            for (ImageMerge imageMerge : this.imageMerges) {
                buffer.append(imageMerge.toString());
            }
        } else {
            buffer.append("\tNo image merges exist.");
        }
        
        return buffer.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if (this.imageMerges != null) {
            for (ImageMerge imageMerge : this.imageMerges) {
                hash = hash * 13 + (imageMerge == null ? 0 : imageMerge.hashCode());
            }
        }
        
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
        ImageForm other = (ImageForm) obj;
        if (this.imageMerges == null) {
            if (other.imageMerges != null) {
                return false;
            }
        } else if (!this.imageMerges.equals(other.imageMerges)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
