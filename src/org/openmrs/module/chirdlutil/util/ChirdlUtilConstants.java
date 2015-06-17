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
	public static final String LOC_TAG_ATTR_AGE_LIMIT_AT_CHECKIN = "CheckinAgeLimit";
	/*
	 * 
	 */
	
	/*
	 * States
	 */
	public static final String STATE_ERROR_STATE = "ErrorState";
	public static final String STATE_JIT_CREATE = "JIT_create";
	public static final String STATE_JIT_MOBILE_CREATE = "JIT_mobile_create";
	public static final String STATE_CHECKIN = "CHECKIN";
	/*
	 * 
	 */
	
	/*
	 * Rules
	 */
	public static final String RULE_CREATE_JIT = "CREATE_JIT";
	/*
	 * 
	 */
	
	/*
	 * Form Instance Attributes
	 */
	public static final String FORM_INST_ATTR_TRIGGER = "trigger";
	/*
	 * 
	 */
	
	/*
	 * Form Instance Attribute Values
	 */
	public static final String FORM_INST_ATTR_VAL_FORCE_PRINT = "forcePrint";
	/*
	 * 
	 */
	
	/*
	 * Form Attributes
	 */
	public static final String FORM_ATTR_DEFAULT_MERGE_DIRECTORY = "defaultMergeDirectory";
	public static final String FORM_ATTR_DEFAULT_EXPORT_DIRECTORY = "defaultExportDirectory";
	public static final String FORM_ATTR_OUTPUT_TYPE = "outputType";
	public static final String FORM_ATTR_MOBILE_ONLY = "mobileOnly";
	public static final String FORM_ATTR_DISPLAY_NAME = "displayName";
	public static final String FORM_ATTR_FORCE_PRINTABLE = "forcePrintable";
	public static final String FORM_ATTR_AGE_MIN = "ageMin";
	public static final String FORM_ATTR_AGE_MAX = "ageMax";
	public static final String FORM_ATTR_AGE_MIN_UNITS = "ageMinUnits";
	public static final String FORM_ATTR_AGE_MAX_UNITS = "ageMaxUnits";
	public static final String FORM_ATTR_REQUIRES_PDF_IMAGE_MERGE = "requriesPDFImageMerge";
	public static final String FORM_ATTR_DEFAULT_PRINTER = "defaultPrinter";
	public static final String FORM_ATTR_USE_ALTERNATE_PRINTER = "useAlternatePrinter";
	public static final String FORM_ATTR_ALTERNATE_PRINTER = "alternatePrinter";
	public static final String FORM_ATTR_DISPLAY_AND_UPDATE_PREVIOUS_VALUES = "displayAndUpdatePreviousValues";
	/*
	 * 
	 */
	
	/*
	 * Form Attributes Values
	 */
	public static final String FORM_ATTR_VAL_TELEFORM_XML = "teleformXML";
	public static final String FORM_ATTR_VAL_TELEFORM_PDF = "teleformPdf";
	public static final String FORM_ATTR_VAL_PDF = "pdf";
	public static final String FORM_ATTR_VAL_TRUE = "true";
	public static final String FORM_ATTR_VAL_FALSE = "false";
	/*
	 * 
	 */
	
	/*
	 * Location Attribute Values
	 */
	public static final String LOCATION_ATTR_PAGER_MESSAGE = "pagerMessage";
	/*
	 * 
	 */
	
	/*
	 * File Information
	 */
	public static final String FILE_PENDING = "Pending";
	public static final String FILE_ARCHIVE = "Archive";
	public static final String FILE_PDF = "pdf";
	public static final String FILE_EXTENSION_XML = ".xml";
	public static final String FILE_EXTENSION_XMLE = ".xmle";
	public static final String FILE_EXTENSION_PDF = ".pdf";
	public static final String FILE_EXTENSION_19 = ".19";
	public static final String FILE_EXTENSION_20 = ".20";
	public static final String FILE_EXTENSION_22 = ".22";
	public static final String FILE_EXTENSION_23 = ".23";
	public static final String FILE_PDF_TEMPLATE = "_template.pdf";
	/*
	 * 
	 */
	
	/*
	 * Global Properties
	 */
	public static final String GLOBAL_PROP_DEFAULT_OUTPUT_TYPE = "atd.defaultOutputType";
	public static final String GLOBAL_PROP_PDF_TEMPLATE_DIRECTORY = "atd.pdfTemplateDirectory";
	public static final String GLOBAL_PROP_PASSCODE = "chica.passcode";
	public static final String GLOBAL_PROP_PAGER_NUMBER = "chica.pagerNumber";
	public static final String GLOBAL_PROP_PAGER_NUMBER_URL_PARAM = "chica.pagerUrlNumberParam";
	public static final String GLOBAL_PROP_PAGER_NUMBER_MESSAGE_PARAM = "chica.pagerUrlMessageParam";
	public static final String GLOBAL_PROP_PAGER_BASE_URL = "chica.pagerBaseURL";
	public static final String GLOBAL_PROP_PAGER_WAIT_TIME_BEFORE_REPAGE = "chica.pagerWaitTimeBeforeRepage";
	public static final String GLOBAL_PROP_G3_ENCRYPTION_KEY = "chica.g3EncryptionKey";
	/*
	 * 
	 */
	
	/*
	 * General Information
	 */
	public static final String GENERAL_INFO_COMMA = ",";
	public static final String GENERAL_INFO_UNDERSCORE = "_";
	/*
	 * 
	 */
	
	/*
	 * Parameters
	 */
	public static final String PARAMETER_0 = "param0";
	public static final String PARAMETER_1 = "param1";
	public static final String PARAMETER_2 = "param2";
	public static final String PARAMETER_3 = "param3";
	public static final String PARAMETER_SESSION_ID = "sessionId";
	public static final String PARAMETER_LOCATION_TAG_ID = "locationTagId";
	public static final String PARAMETER_FORM_INSTANCE = "formInstance";
	public static final String PARAMETER_FORM_NAME = "formName";
	public static final String PARAMETER_TRIGGER = "trigger";
	public static final String PARAMETER_AUTO_PRINT = "autoPrint";
	/*
	 * 
	 */
	
	/*
	 * HTTP Information
	 */
	public static final String HTTP_HEADER_AUTHENTICATE = "WWW-Authenticate";
	public static final String HTTP_HEADER_AUTHENTICATE_BASIC_CHICA = "BASIC realm=\"chica\"";
	public static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HTTP_HEADER_CACHE_CONTROL_NO_CACHE = "no-cache";
	public static final String HTTP_AUTHORIZATION_HEADER = "Authorization";
	public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String HTTP_CONTENT_TYPE_TEXT_XML = "text/xml";
	public static final String HTTP_CONTENT_TYPE_APPLICATION_PDF = "application/pdf";
	public static final String HTTP_CACHE_CONTROL_PUBLIC = "public";
	public static final String HTTP_CACHE_CONTROL_MAX_AGE = "max-age";
	
	/*
	 * 
	 */
	
	/*
	 * HTML Information
	 */
	public static final String HTML_SPAN_START = "<span>";
	public static final String HTML_SPAN_END = "</span>";
	/*
	 * 
	 */
	
	/*
	 * XML Information
	 */
	public static final String XML_START_TAG = "<";
	public static final String XML_END_TAG = ">";
	/*
	 * 
	 */

	/* Unit constants
	 * 
	 */
	public static final String MEASUREMENT_LB = "lb";
	public static final String MEASUREMENT_IN = "in";
	public static final String MEASUREMENT_CM = "cm";
	public static final String MEASUREMENT_KG = "kg";
	
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	/*
	 * 
	 */
	
	/*
	 * Global property constants
	 */
	public static final String GLOBAL_PROPERTY_SCHEDULER_USERNAME = "scheduler.username";
	public static final String GLOBAL_PROPERTY_SCHEDULER_PASSWORD = "scheduler.password";
	/*
	 * 
	 */
	
	/*
	 * Concept information
	 */
	public static final String CONCEPT_DATATYPE_TEXT = "Text";
	public static final String CONCEPT_CLASS_CHICA = "CHICA";
	/*
	 * 
	 */
	
	/*
	 * Data sources
	 */
	public static final String DATA_SOURCE_FORM = "form";
	/*
	 * 
	 */
	
	/*
	 * Form information
	 */
	public static final String FORM_FIELD_TYPE_EXPORT = "Export Field";
	/*
	 * 
	 */
	
	/*
	 * Encryption algorithms
	 */
	public static final String ENCRYPTION_AES = "AES";
	/*
	 * 
	 */
	
	/*
	 * Encoding
	 */
	public static final String ENCODING_UTF8 = "UTF-8";
	/*
	 * 
	 */
}

