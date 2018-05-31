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
 * Contains all the PDF forms that need images merged.
 * 
 * @author Steve McKee
 */
public class PDFImageMerge {
    
    private ArrayList<ImageForm> imageForms;

    /**
     * @return the imageForms
     */
    public ArrayList<ImageForm> getImageForms() {
        return this.imageForms;
    }

    
    /**
     * @param imageForms the imageForms to set
     */
    public void setImageForms(ArrayList<ImageForm> imageForms) {
        this.imageForms = imageForms;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("PDFImageMerge:\n");
        if (this.imageForms != null && this.imageForms.size() > 0) {
            for (ImageForm imageForm : this.imageForms) {
                buffer.append(imageForm.toString());
            }
        } else {
            buffer.append("\tNo image forms exist.");
        }
        
        return buffer.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if (this.imageForms != null) {
            for (ImageForm imageForm : this.imageForms) {
                hash = hash * 13 + (imageForm == null ? 0 : imageForm.hashCode());
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
        PDFImageMerge other = (PDFImageMerge) obj;
        if (this.imageForms == null) {
            if (other.imageForms != null) {
                return false;
            }
        } else if (!this.imageForms.equals(other.imageForms)) {
            return false;
        }
        return true;
    }
}
