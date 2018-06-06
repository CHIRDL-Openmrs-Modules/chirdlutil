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
public class FormConfig {
    
    private ArrayList<MobileForm> forms;

    /**
     * @return the forms
     */
    public ArrayList<MobileForm> getForms() {
        return this.forms;
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
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("FormConfig:\n");
        if (this.forms != null && this.forms.size() > 0) {
            for (MobileForm form : this.forms) {
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
    @Override
    public int hashCode() {
        int hash = 1;
        if (this.forms != null) {
            for (MobileForm form : this.forms) {
                hash = hash * 13 + (form == null ? 0 : form.hashCode());
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
        FormConfig other = (FormConfig) obj;
        if (this.forms == null) {
            if (other.forms != null) {
                return false;
            }
        } else if (!this.forms.equals(other.forms)) {
            return false;
        }
        return true;
    }
}
