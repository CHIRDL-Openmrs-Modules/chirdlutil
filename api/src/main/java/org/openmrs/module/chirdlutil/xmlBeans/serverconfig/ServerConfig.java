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
	private Map<String, List<MobileForm>> userAllFormsMap = new ConcurrentHashMap<String, List<MobileForm>>();
	private Map<String, MobileForm> userPrimaryFormMap = new ConcurrentHashMap<String, MobileForm>();
	private Map<String, List<MobileForm>> userSecondaryFormsMap = new ConcurrentHashMap<String, List<MobileForm>>();
	private Map<String, MobileForm> formIdToFormMap = new ConcurrentHashMap<String, MobileForm>();
	private Map<String, MobileForm> formNameToFormMap = new ConcurrentHashMap<String, MobileForm>();
	private Map<String, MobileClient> userToClientMap = new ConcurrentHashMap<String, MobileClient>();

    /**
     * @return the mobileClients
     */
    public MobileClients getMobileClients() {
    	return mobileClients;
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
	    return formConfig;
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
    	return pdfImageMerge;
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
		List<MobileForm> forms = userAllFormsMap.get(user);
		if (forms != null) {
			return forms;
		} else {
			forms = new ArrayList<MobileForm>();
		}
		
		MobileClient client = getMobileClient(user);
		if (client != null) {
			String primaryFormId = client.getPrimaryFormId();
			if (primaryFormId != null) {
				MobileForm form = getMobileFormById(primaryFormId);
				if (form != null) {
					forms.add(form);
				}
			}
			
			String[] secondaryFormIds = client.getSecondaryFormIds();
			if (secondaryFormIds != null) {
				for (String id : secondaryFormIds) {
					MobileForm form = getMobileFormById(id);
					if (form != null) {
						forms.add(form);
					}
				}
			}
		}
		
		userAllFormsMap.put(user, forms);
		return forms;
	}
	
	/**
	 * @param user The user the form is associated with.
	 * @return primary form for the user.
	 */
	public MobileForm getPrimaryForm(String user) {
		MobileForm primaryForm = userPrimaryFormMap.get(user);
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
			userPrimaryFormMap.put(user, primaryForm);
			return primaryForm;
		}
		
		return null;
	}
	
	/**
	 * @param user The user the forms are associated with.
	 * @return secondary forms for the use.
	 */
	public List<MobileForm> getSecondaryForms(String user) {
		List<MobileForm> secondaryForms = userSecondaryFormsMap.get(user);
		if (secondaryForms != null) {
			return secondaryForms;
		} else {
			secondaryForms = new ArrayList<MobileForm>();
		}
		
		MobileClient client = getMobileClient(user);
		if (client == null) {
			return null;
		}
		
		String[] formIds = client.getSecondaryFormIds();
		if (formIds == null) {
			return null;
		}
		
		for (String formId : formIds) {
			MobileForm form = getMobileFormById(formId);
			if (form != null) {
				secondaryForms.add(form);
			}
		}
		
		userSecondaryFormsMap.put(user, secondaryForms);
		return secondaryForms;
	}
	
	/**
	 * @param user The user used to determine the result.
	 * @return MobileClient information for the provided user.
	 */
	public MobileClient getMobileClient(String user) {
		if (mobileClients == null) {
			return null;
		}
		
		MobileClient cachedClient = userToClientMap.get(user);
		if (cachedClient != null) {
			return cachedClient;
		}
		
		for (MobileClient client : mobileClients.getMobileClients()) {
			if (client.getUser().equals(user)) {
				userToClientMap.put(user, client);
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
		if (formConfig == null || id == null) {
			return null;
		}
		
		MobileForm cachedForm = formIdToFormMap.get(id);
		if (cachedForm != null) {
			return cachedForm;
		}
		
		for (MobileForm form : formConfig.getForms()) {
			if (id.equals(form.getId())) {
				formIdToFormMap.put(id, form);
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
		if (formConfig == null || name == null) {
			return null;
		}
		
		MobileForm cachedForm = formNameToFormMap.get(name);
		if (cachedForm != null) {
			return cachedForm;
		}
		
		for (MobileForm form : formConfig.getForms()) {
			if (name.equals(form.getName())) {
				formNameToFormMap.put(name, form);
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
		if (pdfImageMerge == null) {
			return null;
		}
		
		List<ImageForm> forms = pdfImageMerge.getImageForms();
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
    public String toString() {
    	StringBuffer buffer = new StringBuffer("ServerConfig:\n");
    	if (mobileClients != null ) {
    		buffer.append(mobileClients.toString());
    	} 
    	
    	if (formConfig != null) {
    		buffer.append(formConfig.toString());
    	} 
    	
    	if (pdfImageMerge != null) {
    		buffer.append(pdfImageMerge.toString());
    	}
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 13 + (mobileClients == null ? 0 : mobileClients.hashCode());
        hash = hash * 31 + (formConfig == null ? 0 : formConfig.hashCode());
        hash = hash * 13 + (pdfImageMerge == null ? 0 : pdfImageMerge.hashCode());
        
        return hash;
    }
}
