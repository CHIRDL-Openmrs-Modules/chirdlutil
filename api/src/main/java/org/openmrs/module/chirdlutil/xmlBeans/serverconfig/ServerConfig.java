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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Steve McKee
 */
public class ServerConfig {

    private MobileClients mobileClients;
    private FormConfig formConfig;
    private PDFImageMerge pdfImageMerge;
    private Map<String, List<MobileForm>> userAllFormsMap = new ConcurrentHashMap<>();
    private Map<String, MobileForm> userPrimaryFormMap = new ConcurrentHashMap<>();
    private Map<String, List<MobileForm>> userSecondaryFormsMap = new ConcurrentHashMap<>();
    private Map<String, MobileForm> formIdToFormMap = new ConcurrentHashMap<>();
    private Map<String, MobileForm> formNameToFormMap = new ConcurrentHashMap<>();
    private Map<String, MobileClient> userToClientMap = new ConcurrentHashMap<>();

    /**
     * @return the mobileClients
     */
    public MobileClients getMobileClients() {
        return this.mobileClients;
    }

    /**
     * @param mobileClients the mobileClients to set
     */
    public void setMobileClients(MobileClients mobileClients) {
        this.mobileClients = mobileClients;
    }
    
    /**
     * @return the formConfig
     */
    public FormConfig getFormConfig() {
        return this.formConfig;
    }

    /**
     * @param formConfig the formConfig to set
     */
    public void setFormConfig(FormConfig formConfig) {
        this.formConfig = formConfig;
    }
    
    /**
     * @return the pdfImageMerge
     */
    public PDFImageMerge getPdfImageMerge() {
        return this.pdfImageMerge;
    }
    
    /**
     * @param pdfImageMerge the pdfImageMerge to set
     */
    public void setPdfImageMerge(PDFImageMerge pdfImageMerge) {
        this.pdfImageMerge = pdfImageMerge;
    }

    /**
     * @param user The user the forms are associated with.
     * @return all forms for this mobile client
     */
    public List<MobileForm> getAllMobileForms(String user) {
        List<MobileForm> forms = this.userAllFormsMap.get(user);
        if (forms != null) {
            return forms;
        }
            
        forms = new ArrayList<>();
        MobileClient client = getMobileClient(user);
        if (client != null) {
            String primaryFormId = client.getPrimaryFormId();
            if (primaryFormId != null) {
                MobileForm form = getMobileFormById(primaryFormId);
                if (form != null) {
                    forms.add(form);
                }
            }
            
            SecondaryForm[] secondaryForms = client.getSecondaryForms();
            for (SecondaryForm secondaryForm : secondaryForms) {
                String id = secondaryForm.getId();
                MobileForm form = getMobileFormById(id);
                if (form != null) {
                    forms.add(form);
                }
            }
        }
        
        this.userAllFormsMap.put(user, forms);
        return forms;
    }
    
    /**
     * @param user The user the form is associated with.
     * @return primary form for the user.
     */
    public MobileForm getPrimaryForm(String user) {
        MobileForm primaryForm = this.userPrimaryFormMap.get(user);
        if (primaryForm != null) {
            return primaryForm;
        }
        
        String primaryFormId = null;
        MobileClient client = getMobileClient(user);
        if (client != null) {
            primaryFormId = client.getPrimaryFormId();
        }
        
        if (primaryFormId != null) {
            primaryForm = getMobileFormById(primaryFormId);
            this.userPrimaryFormMap.put(user, primaryForm);
            return primaryForm;
        }
        
        return null;
    }
    
    /**
     * @param user The user the forms are associated with.
     * @return secondary forms for the use.
     */
    public List<MobileForm> getSecondaryForms(String user) {
        List<MobileForm> secondaryForms = this.userSecondaryFormsMap.get(user);
        if (secondaryForms != null) {
            return secondaryForms;
        }
            
        secondaryForms = new ArrayList<>();
        MobileClient client = getMobileClient(user);
        if (client == null) {
            return null;
        }
        
        SecondaryForm[] clientSecondaryForms = client.getSecondaryForms();
        for (SecondaryForm clientSecondaryForm : clientSecondaryForms) {
            String formId = clientSecondaryForm.getId();
            MobileForm form = getMobileFormById(formId);
            if (form != null) {
                secondaryForms.add(form);
            }
        }
        
        this.userSecondaryFormsMap.put(user, secondaryForms);
        return secondaryForms;
    }
    
    /**
     * @param user The user used to determine the result.
     * @return MobileClient information for the provided user.
     */
    public MobileClient getMobileClient(String user) {
        if (this.mobileClients == null) {
            return null;
        }
        
        MobileClient cachedClient = this.userToClientMap.get(user);
        if (cachedClient != null) {
            return cachedClient;
        }
        
        for (MobileClient client : this.mobileClients.getMobileClients()) {
            if (client.getUser().equals(user)) {
                this.userToClientMap.put(user, client);
                return client;
            }
        }
        
        return null;
    }
    
    /**
     * @param id The id of the mobile form.
     * @return The MobileForm information associated with the provided id.
     */
    public MobileForm getMobileFormById(String id) {
        if (this.formConfig == null || id == null) {
            return null;
        }
        
        MobileForm cachedForm = this.formIdToFormMap.get(id);
        if (cachedForm != null) {
            return cachedForm;
        }
        
        for (MobileForm form : this.formConfig.getForms()) {
            if (id.equals(form.getId())) {
                this.formIdToFormMap.put(id, form);
                return form;
            }
        }
        
        return null;
    }
    
    /**
     * @param name The name of the mobile form
     * @return The MobileForm information associated with the provided name.
     */
    public MobileForm getMobileFormByName(String name) {
        if (this.formConfig == null || name == null) {
            return null;
        }
        
        MobileForm cachedForm = this.formNameToFormMap.get(name);
        if (cachedForm != null) {
            return cachedForm;
        }
        
        for (MobileForm form : this.formConfig.getForms()) {
            if (name.equals(form.getName())) {
                this.formNameToFormMap.put(name, form);
                return form;
            }
        }
        
        return null;
    }
    
    /**
     * @param formName The name of the PDF image form.
     * @return ImageForm object conting the image information for the specified PDF form.
     */
    public ImageForm getPDFImageForm(String formName) {
        if (this.pdfImageMerge == null) {
            return null;
        }
        
        List<ImageForm> forms = this.pdfImageMerge.getImageForms();
        if (forms == null) {
            return null;
        }
        
        for (ImageForm form : forms) {
            if (form.getName().equals(formName)) {
                return form;
            }
        }
        
        return null;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("ServerConfig:\n");
        if (this.mobileClients != null ) {
            buffer.append(this.mobileClients.toString());
        } 
        
        if (this.formConfig != null) {
            buffer.append(this.formConfig.toString());
        } 
        
        if (this.pdfImageMerge != null) {
            buffer.append(this.pdfImageMerge.toString());
        }
        
        return buffer.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 13 + (this.mobileClients == null ? 0 : this.mobileClients.hashCode());
        hash = hash * 31 + (this.formConfig == null ? 0 : this.formConfig.hashCode());
        hash = hash * 13 + (this.pdfImageMerge == null ? 0 : this.pdfImageMerge.hashCode());
        
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
        ServerConfig other = (ServerConfig) obj;
        if (this.formConfig == null) {
            if (other.formConfig != null) {
                return false;
            }
        } else if (!this.formConfig.equals(other.formConfig)) {
            return false;
        }
        if (this.formIdToFormMap == null) {
            if (other.formIdToFormMap != null) {
                return false;
            }
        } else if (!this.formIdToFormMap.equals(other.formIdToFormMap)) {
            return false;
        }
        if (this.formNameToFormMap == null) {
            if (other.formNameToFormMap != null) {
                return false;
            }
        } else if (!this.formNameToFormMap.equals(other.formNameToFormMap)) {
            return false;
        }
        if (this.mobileClients == null) {
            if (other.mobileClients != null) {
                return false;
            }
        } else if (!this.mobileClients.equals(other.mobileClients)) {
            return false;
        }
        if (this.pdfImageMerge == null) {
            if (other.pdfImageMerge != null) {
                return false;
            }
        } else if (!this.pdfImageMerge.equals(other.pdfImageMerge)) {
            return false;
        }
        if (this.userAllFormsMap == null) {
            if (other.userAllFormsMap != null) {
                return false;
            }
        } else if (!this.userAllFormsMap.equals(other.userAllFormsMap)) {
            return false;
        }
        if (this.userPrimaryFormMap == null) {
            if (other.userPrimaryFormMap != null) {
                return false;
            }
        } else if (!this.userPrimaryFormMap.equals(other.userPrimaryFormMap)) {
            return false;
        }
        if (this.userSecondaryFormsMap == null) {
            if (other.userSecondaryFormsMap != null) {
                return false;
            }
        } else if (!this.userSecondaryFormsMap.equals(other.userSecondaryFormsMap)) {
            return false;
        }
        if (this.userToClientMap == null) {
            if (other.userToClientMap != null) {
                return false;
            }
        } else if (!this.userToClientMap.equals(other.userToClientMap)) {
            return false;
        }
        return true;
    }
}
