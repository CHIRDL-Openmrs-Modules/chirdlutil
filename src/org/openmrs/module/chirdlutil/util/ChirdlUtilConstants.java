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
package org.openmrs.module.chirdlutil.util;


/**
 * Constants class for dependent modules
 * 
 * @author Steve McKee
 */
public final class ChirdlUtilConstants {
	
	/*
	 * User property constants
	 */
	public static final String USER_PROPERTY_LOCATION = "location";
	
	public static final String USER_PROPERTY_LOCATION_TAGS = "locationTags";
	/*
	 * 
	 */
	
	/*
	 * Identifier type constants
	 */
	public static final String IDENTIFIER_TYPE_MRN = "MRN_OTHER";
	public static final String IDENTIFIER_TYPE_SSN = "SSN";
	public static final String IDENTIFIER_TYPE_IMMUNIZATION_REGISTRY = "Immunization Registry";

	/*
	 * 
	 */

	/*
	 * Person attribute type constants
	 */
	public static final String PERSON_ATTRIBUTE_RELIGION = "Religion";
	public static final String PERSON_ATTRIBUTE_MARITAL_STATUS = "Civil Status";
	public static final String PERSON_ATTRIBUTE_MAIDEN_NAME = "Mother's maiden name";
	public static final String PERSON_ATTRIBUTE_NEXT_OF_KIN = "Next of Kin";
	public static final String PERSON_ATTRIBUTE_TELEPHONE = "Telephone Number";
	public static final String PERSON_ATTRIBUTE_CITIZENSHIP = "Citizenship";
	public static final String PERSON_ATTRIBUTE_RACE = "Race";
	public static final String PERSON_ATTRIBUTE_SSN = "SSN";

	/*
	 * 
	 */

	/*
	 * Chirdlutilbackports error category constants
	 */
	public static final String ERROR_QUERY_KITE_CONNECTION = "Query Kite Connection";
	public static final String ERROR_GENERAL = "General Error";
	public static final String ERROR_PSF_SCAN = "PSF Scan";
	public static final String ERROR_PWS_SCAN = "PWS Scan";
	public static final String ERROR_HL7_PARSING = "Hl7 Parsing";
	public static final String ERROR_MRN_VALIDITY = "MRN Validity";
	public static final String ERROR_XML_PARSING = "XML Parsing";
	public static final String ERROR_ID_VALIDITY = "ID Validity";
	public static final String ERROR_QUERY_MED_LIST_CONNECTION = "Query Medication List Connection";
	public static final String ERROR_HL7_EXPORT = "Hl7 Export";
	public static final String ERROR_SUPPORT_PAGE = "Support Page";
	public static final String ERROR_BAD_SCANS = "Bad Scans";
	public static final String ERROR_QUERY_IMMUNIZATION_CONNECTION = "Query Immunization List Connection";
	public static final String ERROR_GIS_CLINIC_ADDRESS_USED = "GIS Clinic Address Used";
	public static final String ERROR_MEDICAL_LEGAL_PAGE = "Medical Legal Page";
	public static final String ERROR_DIABETES_PAGE = "Diabetes Page";
	public static final String ERROR_WEB_SERVICE = "Web Service Error";

	
	/*
	 * Location tag attribute constants
	 */
	public static final String LOC_TAG_ATTR_ACTIVE_PRINTER_STATION = "ActivePrinterStation";
	/*
	 * 
	 */
}