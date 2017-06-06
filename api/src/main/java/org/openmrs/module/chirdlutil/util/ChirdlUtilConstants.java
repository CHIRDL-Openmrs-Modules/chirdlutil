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
	public static final String IDENTIFIER_TYPE_MRN_EHR = "MRN_EHR";

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
	public static final String PERSON_ATTRIBUTE_PATIENT_ACCOUNT_NUMBER = "Patient Account Number";
	public static final String PERSON_ATTRIBUTE_ETHNICITY = "Ethnicity";
	public static final String PERSON_ATTRIBUTE_EMAIL = "email";

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
	public static final String STATE_PROCESS_VITALS = "Processed Vitals HL7";
	public static final String STATE_CHECKIN = "CHECKIN";
	public static final String STATE_HL7_PROCESS_REGISTRATION_OBS = "HL7ProcessRegistrationObs";
	public static final String STATE_EXPORT_VITALS = "Export Vitals";
	public static final String STATE_EXPORT_POC = "Export POC";
	public static final String STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION = "PSF WAIT FOR ELECTRONIC SUBMISSION";
	public static final String STATE_PWS_PROCESS = "PWS_process";
	public static final String STATE_PWS_WAIT_FOR_SUBMISSION = "PWS WAIT FOR SUBMISSION";
	public static final String STATE_QUERY_KITE_PWS = "QUERY KITE PWS";
	public static final String STATE_QUERY_KITE_PSF = "QUERY KITE PSF";
	public static final String STATE_QUERY_KITE_ALIAS = "QUERY KITE Alias";
	public static final String STATE_PSF_CREATE = "PSF_create";
	public static final String STATE_RANDOMIZE = "Randomize";
	public static final String STATE_PSF_PRINTED = "PSF_printed";
	public static final String STATE_PSF_PROCESS = "PSF_process";
	public static final String STATE_PWS_CREATE = "PWS_create";
	public static final String STATE_PWS_PRINTED = "PWS_printed";
	public static final String STATE_FINISHED = "FINISHED";
	public static final String STATE_PSF_REPRINT = "PSF_reprint";
	public static final String STATE_PWS_REPRINT = "PWS_reprint";
	public static final String STATE_GREASE_BOARD_PRINT_PWS = "Grease Board Print PWS";
	
	/*
	 * 
	 */
	
	/*
	 * Rules
	 */
	public static final String RULE_CREATE_JIT = "CREATE_JIT";
	public static final String RULE_PROVIDER_NAME = "providerName";
	/*
	 * 
	 */
	
	/*
	 * Form Instance Attributes
	 */
	public static final String FORM_INST_ATTR_TRIGGER = "trigger";
	public static final String FORM_INSTANCE_ATTR_FAX_ID = "faxId";
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
	public static final String FORM_ATTR_STYLESHEET = "stylesheet";
	public static final String FORM_ATTRIBUTE_AUTO_FAX = "auto-fax";
	public static final String FORM_ATTRIBUTE_IMAGE_DIRECTORY = "imageDirectory";
	public static final String FORM_ATTRIBUTE_DISPLAY_GP_HEADER = "displayGroupHeader";
	
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
	public static final String LOCATION_ATTR_CLINIC_FAX_NUMBER = "clinicFaxNumber";
	public static final String LOCATION_ATTR_CLINIC_FAX_NUMBER_DISPLAY = "clinicFaxNumberDisplay";
	public static final String LOCATION_ATTR_CLINIC_DISPLAY_NAME = "clinicDisplayName";
	public static final String LOCATION_ATTR_DISPLAY_CONFIDENTIALITY_NOTICE = "displayConfidentialityNoticeMobileGreaseBoard";
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
	public static final String FILE_EXTENSION_HL7 = ".hl7";
	public static final String FILE_EXTENSION_TIF = ".tif";
	public static final String FILE_EXTENSION_TIFF = ".tiff";
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
	public static final String GLOBAL_PROP_IU_HEALTH_CERNER_ENCRYPTION_KEY = "chica.iuHealthCernerEncryptionKey";
	public static final String GLOBAL_PROP_QUERY_MEDS = "chica.queryMeds";
	public static final String GLOBAL_PROP_IMMUNIZATION_QUERY_ACTIVATED = "chica.ImmunizationQueryActivated";
	public static final String GLOBAL_PROP_IMMUNIZATION_LIST_TIMEOUT = "chica.immunizationListTimeout";
	public static final String GLOBAL_PROP_MRF_ARCHIVE_DIRECTORY = "chica.mrfArchiveDirectory";
	public static final String GLOBAL_PROP_MRF_QUERY_CONFIG_FILE = "chica.mrfQueryConfigFile";
	public static final String GLOBAL_PROP_MRF_QUERY_PASSWORD = "chica.MRFQueryPassword";
	public static final String GLOBAL_PROP_MRF_QUERY_TIMEOUT = "chica.kiteTimeout";
	public static final String GLOBAL_PROP_MRF_TARGET_ENDPOINT = "chica.MRFQueryTargetEndpoint";
	public static final String GLOBAL_PROP_PERFORM_MRF_QUERY = "chica.performMRFQuery";
	public static final String GLOBAL_PROP_MRF_QUERY_NAMESPACE = "chica.MRFQueryNamespace";
	public static final String GLOBAL_PROP_MRF_ERROR_DIRECTORY = "chica.mrfParseErrorDirectory";
	public static final String GLOBAL_PROP_ENCRYPTION_KEY = "chica.encryptionKey";
	public static final String GLOBAL_PROP_ENABLE_MANUAL_CHECKIN = "chica.enableManualCheckin";
	public static final String GLOBAL_PROP_GREASEBOARD_REFRESH = "chica.greaseBoardRefresh";
	public static final String GLOBAL_PROP_OUTGOING_NOTE_INCLUDE_PV1 = "chica.outgoingNoteIncludePV1";
	public static final String GLOBAL_PROP_OUTGOING_NOTE_TXA_UNIQUE_DOC_NUMBER = "chica.outgoingNoteTXAUniqueDocumentNumber";
	public static final String GLOBAL_PROP_OUTGOING_NOTE_TXA_DOC_AVAILABILITY_STATUS = "chica.outgoingNoteTXADocumentAvailabilityStatus";
	public static final String GLOBAL_PROP_OUTGOING_NOTE_HOST = "chica.outgoingNoteHost";
	public static final String GLOBAL_PROP_OUTGOING_NOTE_PORT = "chica.outgoingNotePort";
	public static final String GLOBAL_PROP_SESSION_TIMEOUT_WARNING= "chica.sessionTimeoutWarning";
	public static final String GLOBAL_PROP_OUTGOING_FAX_SENDER = "atd.outgoingFaxFrom";
	public static final String GLOBAL_PROP_OUTGOING_FAX_RECIPIENT = "atd.defaultOutgoingFaxRecipient";
	public static final String GLOBAL_PROP_OUTGOING_FAX_WSDL_LOCATION = "atd.outgoingFaxWsdlLocation";
	public static final String GLOBAL_PROP_OUTGOING_FAX_USERNAME = "atd.outgoingFaxUsername";
	public static final String GLOBAL_PROP_OUTGOING_FAX_PASSWORD = "atd.outgoingFaxPassword";
	public static final String GLOBAL_PROP_OUTGOING_FAX_PRIORITY = "atd.outgoingFaxPriority";
	public static final String GLOBAL_PROP_OUTGOING_FAX_RESOLUTION = "atd.outgoingFaxResolution";
	public static final String GLOBAL_PROP_OUTGOING_FAX_SEND_TIME = "atd.outgoingFaxSendTime";
	public static final String GLOBAL_PROP_VITALS_USE_VISIT_NUMBER = "chica.vitalsUseVisitNumberLookupEncounter";
	public static final String GLOBAL_PROP_HAPI_CHARACTER_ENCODING = "sockethl7listener.hapiCharacterEncoding";
	public static final String GLOBAL_PROP_UNKNOWN_PROVIDER_ID = "sockethl7listener.unknownProviderId";
	public static final String GLOBAL_PROP_FORM_TIME_LIMIT = "chica.formTimeLimit";
	public static final String GLOBAL_PROP_FHIR_CONFIG_FILE = "chirdl_fhir.fhirConfigFile";

	/*
	 * 
	 */
	
	/*
	 * General Information
	 */
	public static final String GENERAL_INFO_COMMA = ",";
	public static final String GENERAL_INFO_UNDERSCORE = "_";
	public static final String GENERAL_INFO_TRUE = "true";
	public static final String GENERAL_INFO_FALSE = "false";
	public static final String GENERAL_INFO_FORWARD_SLASH = "/";
	public static final String GENERAL_INFO_BACK_SLASH = "\\";
	public static final String GENERAL_INFO_NUMBER_SIGN = "#";
	public static final String GENERAL_INFO_DEFAULT_DELIMITER = "\\^\\^";
	public static final String GENERAL_INFO_EMPTY_STRING = "";
	public static final String GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED = "\r\n";
	public static final String GENERAL_INFO_DASH = "-";
	public static final String GENERAL_INFO_QUESTION_MARK = "?";
	public static final String GENERAL_INFO_EQUAL = "=";
	public static final String GENERAL_INFO_AMPERSAND = "&";
	public static final String GENERAL_INFO_PIPE_DELIMITER = "|";
	
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
	public static final String PARAMETER_4 = "param4";
	public static final String PARAMETER_SESSION_ID = "sessionId";
	public static final String PARAMETER_LOCATION_TAG_ID = "locationTagId";
	public static final String PARAMETER_FORM_INSTANCE = "formInstance";
	public static final String PARAMETER_FORM_NAME = "formName";
	public static final String PARAMETER_TRIGGER = "trigger";
	public static final String PARAMETER_AUTO_PRINT = "autoPrint";
	public static final String PARAMETER_ENCOUNTER_ID = "encounterId";
	public static final String PARAMETER_LOCATION = "location";
	public static final String PARAMETER_LOCATION_ID = "locationId";
	public static final String PARAMETER_VALUE_PRODUCE = "PRODUCE";
	public static final String PARAMETER_VALUE_CONSUME = "CONSUME";
	public static final String PARAMETER_MODE = "mode";
	public static final String PARAMETER_FORCE_AUTO_PRINT = "forceAutoPrint";
	public static final String PARAMETER_TAG_NAME = "tagName";
	public static final String PARAMETER_DESCRIPTION = "description";
	public static final String PARAMETER_FORM = "form";
	public static final String PARAMETER_HIDDEN_SUBMIT = "hiddenSubmit";
	public static final String PARAMETER_FINISH = "Finish";
	public static final String PARAMETER_NAME = "name";
	public static final String PARAMETER_USERNAME = "username";
	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_PROGRAM = "program";
	public static final String PARAMETER_ESTABLISHED_TAG = "establishedTag";
	public static final String PARAMETER_MISSING_FORM_INSTANCE = "missingFormInstance";
	public static final String PARAMETER_MISSING_ENCOUNTER = "missingEncounter";
	public static final String PARAMETER_INVALID_END_STATE = "invalidEndState";
	public static final String PARAMETER_INVALID_START_STATE = "invalidStartState";
	public static final String PARAMETER_MISSING_END_STATE = "missingEndState";
	public static final String PARAMETER_MISSING_START_STATE = "missingStartState";
	public static final String PARAMETER_INVALID_PATIENT = "invalidPatient";
	public static final String PARAMETER_INVALID_FORM = "invalidForm";
	public static final String PARAMETER_END_STATE = "endState";
	public static final String PARAMETER_START_STATE = "startState";
	public static final String PARAMETER_FAILED_AUTHENTICATION = "failedAuthentication";
	public static final String PARAMETER_MISSING_PROVIDER_ID = "missingProviderId";
	public static final String PARAMETER_MISSING_PASSWORD = "missingPassword";
	public static final String PARAMETER_MISSING_USER = "missingUser";
	public static final String PARAMETER_MISSING_MRN = "missingMRN";
	public static final String PARAMETER_MISSING_FORM = "missingForm";
	public static final String PARAMETER_MISSING_FORM_PAGE = "missingFormPage";
	public static final String PARAMETER_MRN = "mrn";
	public static final String PARAMETER_FORM_PAGE = "formPage";
	public static final String PARAMETER_INVALID_VENDOR = "invalidVendor";
	public static final String PARAMETER_MISSING_VENDOR = "missingVendor";
	public static final String PARAMETER_VAL_TRUE = "true";
	public static final String PARAMETER_HAS_ERRORS = "hasErrors";
	public static final String PARAMETER_VENDOR = "vendor";
	public static final String PARAMETER_SHOW_HANDOUTS = "showHandouts";
	public static final String PARAMETER_PATIENT_NAME = "patientName";
	public static final String PARAMETER_PATIENT_ID = "patientId";
	public static final String PARAMETER_PROVIDER_ID = "providerId";
	public static final String PARAMETER_FORM_TIME_LIMIT = "formTimeLimit";
	public static final String PARAMETER_CODE = "code";
	public static final String PARAMETER_GENDER = "gender";
	public static final String PARAMETER_PATIENT = "patient";
	public static final String PARAMETER_IDENTIFIER = "identifier";
	
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
	public static final String HTTP_HEADER_EXPIRES = "Expires";
	public static final String HTTP_AUTHORIZATION_HEADER = "Authorization";
	public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String HTTP_CONTENT_TYPE_TEXT_XML = "text/xml";
	public static final String HTTP_CONTENT_TYPE_TEXT_HTML = "text/html";
	public static final String HTTP_CONTENT_TYPE_IMAGE_PNG = "image/png";
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
	
	/*
	 * Location strings
	 */
	public static final String LOCATION_RIIUMG = "RIIUMG";
	/*
	 * 
	 */

    /*
	 * Error levels
	 */
	public static final String ERROR_LEVEL_ERROR = "Error";
	public static final String ERROR_LEVEL_WARNING = "Warning";
	public static final String ERROR_LEVEL_DEBUG = "Debug";
	public static final String ERROR_LEVEL_VERBOSE = "Verbose";
	public static final String ERROR_LEVEL_FATAL = "Fatal";
	/*
	 * 
	 */
	
	/*
	 * HL7 Segments
	 */
	public static final String HL7_SEGMENT_MESSAGE_ACKNOWLEDGMENT_MSA = "MSA";
	public static final String HL7_SEGMENT_MESSAGE_HEADER_MSH = "MSH";
	public static final String HL7_SEGMENT_PATIENT_IDENTIFICATION_PID = "PID";
	public static final String HL7_SEGMENT_PATIENT_VISIT_PV1 = "PV1";
	public static final String HL7_SEGMENT_INSURANCE_IN1 = "IN1";
	public static final String HL7_SEGMENT_INSURANCE_IN2 = "IN2";
	public static final String HL7_SEGMENT_NEXT_OF_KIN_NK1 = "NK1";
	public static final String HL7_SEGMENT_NEXT_OF_KIN_NK2 = "NK2";
	public static final String HL7_SEGMENT_OBSERVATION_OBX = "OBX";
	public static final String HL7_SEGMENT_OBSERVATION_REQUEST_OBR = "OBR";
	public static final String HL7_SEGMENT_PATIENT_ADDITIONAL_INFO_PD1 = "PD1";
	public static final String HL7_SEGMENT_EVENT_TYPE_EVN = "EVN";
	public static final String HL7_SEGMENT_Z_SEGMENT_ZPV = "ZPV";
	 /*
	  * 
	  */
	
	/*
	 * HL7 Constants
	 */
	public static final String HL7_ENCODING_CHARS = "^~\\&";
	public static final String HL7_FIELD_SEPARATOR = "|";
	public static final String HL7_DATATYPE_TX = "TX";
	public static final String HL7_RESULT_STATUS = "F";
	public static final String HL7_MDM = "MDM";
	public static final String HL7_EVENT_CODE_T02 = "T02";
	public static final String HL7_VERSION_2_2 = "2.2";
	public static final String HL7_START_OF_MESSAGE = "\u000b";
    public static final String HL7_SEGMENT_SEPARATOR = "\r";
    public static final String HL7_END_OF_MESSGAE = "\u001c";
	
	/*
	 * HL7 Acknowledgment Codes
	 */
	public static final String HL7_ACK_CODE_APPLICATION_ACCEPT = "AA";
	public static final String HL7_ACK_CODE_APPLICATION_ERROR = "AE";
	public static final String HL7_ACK_CODE_APPLICATION_REJECT = "AR";
	 /*
	  * 
	  */
	
	/*
	 * Constants used to determine how to replace special characters
	 */
	public static final String MESSAGE_XML = "XML";
	public static final String MESSAGE_HL7 = "HL7";
	public static final String MESSAGE_CDATA = "CDATA";
	/*
	 * 
	 */
	
	/*
	 * Data Sources
	 */
	public static final String DATA_SOURCE_IN_MEMORY = "RMRS";
	public static final String DATA_SOURCE_IU_HEALTH_MEDICAL_RECORD = "IU Health MRF Dump";
	public static final String DATA_SOURCE_IU_HEALTH_VITALS = "IU Health Vitals";
	/*
	 * 
	 */
	
	/*
	 * Encounter attribute constants
	 */
	public static final String ENCOUNTER_ATTRIBUTE_VISIT_NUMBER = "Visit Number";
	public static final String ENCOUNTER_ATTRIBUTE_ORIGINAL_LOCATION = "Original Location";
	
	/*
	 * Scheduled task properties 
	 */
	public static final String TASK_PROPERTY_SOCKET_READ_TIMEOUT = "socketReadTimeout";
	public static final String TASK_PROPERTY_HOST = "host";
	public static final String TASK_PROPERTY_PORT= "port";
	public static final String TASK_PROPERTY_THREAD_SLEEP_TIME = "threadSleepTime";
	
	/*
	 * Date constants
	 */
	public static final String DATE_FORMAT_yyyy_MM_dd_HH_mm = "yyyyMMddHHmm";
	public static final String DATE_FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_yyyy_MM_dd = "yyyyMMdd";
	public static final String DATE_FORMAT_MMM_d_yyyy = "MMM d yyyy";
	public static final String DATE_FORMAT_h_mm_a = "h:mma";
	/*
	 * Fax service
	 */
	public static final int FAX_PRIORITY_URGENT = 3;
	public static final int FAX_PRIORITY_HIGH = 2;
	public static final int FAX_PRIORITY_NORMAL = 1;
	public static final int FAX_PRIORITY_LOW = 0;
	public static final int FAX_RESOLUTION_HIGH = 1;
	public static final int FAX_RESOLUTION_LOW = 0;
	public static final String FAX_SEND_TIME_IMMEDIATE = "0.0";
	public static final String FAX_SEND_TIME_OFF_PEAK = "1.0";
	/*
	 * 
	 */
	
	/*
	 * Hapi constants
	 */
	public static final String HAPI_CHARSET_PROPERTY_KEY = "ca.uhn.hl7v2.llp.charset";
	/*
	 * 
	 */
	
	public static final String FORM_PWS = "PWS";
	
	/*
	 * Greaseboard indicators for PWS Ready 
	 */
	public static final String PWS_READY = "PWS Ready";
	public static final String PWS_READY_AWAITING_PSF = "PWS Ready/Awaiting PSF";
	public static final String PWS_READY_AWAITING_VITALS = "PWS Ready/Awaiting Vitals";
	
	/*
	 * Location Configuration constants
	 */
	public static final String LOC_TAG_ATTR_FORM_EDIT = "editClinicTagAttributeForm"; 
	public static final String LOC_TAG_FORM_CREATE = "createClinicTagForm";
	
	/*
	 * Encounter Role constants
	 */
	public static final String ENCOUNTER_ROLE_ATTENDING_PROVIDER = "Attending Provider";
	
	/*
	 * Gender constants
	 */
	public static final String GENDER_UNKNOWN = "U";
	public static final String GENDER_MALE = "M";
	public static final String GENDER_FEMALE = "F";
	public static final String GENDER_FEMALE_STRING = "female";
	public static final String GENDER_MALE_STRING = "male";
	public static final String GENDER_UNKNOWN_STRING = "unknown";
	
}

