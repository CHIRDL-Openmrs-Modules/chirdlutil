package org.openmrs.module.chirdlutil.tools;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateFlowsheetConceptMapping {
	
	private static final Log LOG = LogFactory.getLog(CreateFlowsheetConceptMapping.class);
	private static final String INPUT_ARG = "-input";
	private static final String OUTPUT_ARG = "-output";
	private static final String LOCATION_ARG = "-location";
	private static final String DISPLAY_CONCEPT_SOURCE_ARG = "-displayConceptSource";
	private static final String CODE_CONCEPT_SOURCE_ARG = "-codeConceptSource";
	private static final String EXAMPLE_MESSAGE = "CreateFlowsheetConceptMapping -output C:\\test\\mappings.csv -input "
			+ "C:\\test\\flowsheetMapping.csv.csv -location ABCD -displayConceptSource \"ABCD Flowsheet Display\" "
			+ "-codeConceptSource \"ABCD Flowsheet Code\"";
	
	private static FlowsheetConceptMappingArgs createFlowsheetConceptMappingArgs(String[] args) {
		FlowsheetConceptMappingArgs mappingArgs = new FlowsheetConceptMappingArgs();
		File outputFile = null;
		File inputFile = null;
		String location = null;
		String displayConceptSource = null;
		String codeConceptSource = null;
		boolean isInput = false;
		boolean isOutput = false;
		boolean isLocation = false;
		boolean isDisplayConceptSource = false;
		boolean isCodeConceptSource = false;
		for (String arg : args) {
			if (INPUT_ARG.equalsIgnoreCase(arg)) {
				isInput = true;
				isOutput = false;
				isLocation = false;
				isDisplayConceptSource = false;
				isCodeConceptSource = false;
			} else if (OUTPUT_ARG.equalsIgnoreCase(arg)) {
				isInput = false;
				isOutput = true;
				isLocation = false;
				isDisplayConceptSource = false;
				isCodeConceptSource = false;
			} else if (LOCATION_ARG.equalsIgnoreCase(arg)) {
				isInput = false;
				isOutput = false;
				isLocation = true;
				isDisplayConceptSource = false;
				isCodeConceptSource = false;
			} else if (DISPLAY_CONCEPT_SOURCE_ARG.equalsIgnoreCase(arg)) {
				isInput = false;
				isOutput = false;
				isLocation = false;
				isDisplayConceptSource = true;
				isCodeConceptSource = false;
			} else if (CODE_CONCEPT_SOURCE_ARG.equalsIgnoreCase(arg)) {
				isInput = false;
				isOutput = false;
				isLocation = false;
				isDisplayConceptSource = false;
				isCodeConceptSource = true;
			}
			else {
				if (isInput) {
					inputFile = new File(arg);
					if (!inputFile.exists() || !inputFile.canRead() || inputFile.isDirectory()) {
						LOG.error("Cannot locate or read input file " + arg);
						return null;
					}
					
					mappingArgs.setInput(inputFile);
				} else if (isOutput) {
					outputFile = new File(arg);
					mappingArgs.setOutput(outputFile);
				} else if (isLocation) {
					location = arg;
					mappingArgs.setLocation(location);
				} else if (isDisplayConceptSource) {
					displayConceptSource = arg;
					mappingArgs.setDisplayConceptSource(displayConceptSource);
				} else if (isCodeConceptSource) {
					codeConceptSource = arg;
					mappingArgs.setCodeConceptSource(codeConceptSource);
				}
			}
		}
		
		if (outputFile == null) {
			LOG.error("No output file specified.");
			LOG.error(EXAMPLE_MESSAGE);
			return null;
		}
			
		if (inputFile == null) {
			LOG.error("No input files specified.");
			LOG.error(EXAMPLE_MESSAGE);
			return null;
		}
		
		if (StringUtils.isBlank(location)) {
			LOG.error("No location specified.");
			LOG.error(EXAMPLE_MESSAGE);
			return null;
		}
		
		if (StringUtils.isBlank(displayConceptSource)) {
			LOG.error("No displayConceptSource specified.");
			LOG.error(EXAMPLE_MESSAGE);
			return null;
		}
		
		if (StringUtils.isBlank(codeConceptSource)) {
			LOG.error("No codeConceptSource specified.");
			LOG.error(EXAMPLE_MESSAGE);
			return null;
		}
		
		return mappingArgs;
	}
	
	/**
	 * Main method
	 * 
	 * @param args The program arguments
	 */
	public static void main(String[] args) {
		if (args == null || args.length == 0) {
            LOG.error("Please specify the output and input files.");
            LOG.error(EXAMPLE_MESSAGE);
            System.exit(1);
        }
		
		FlowsheetConceptMappingArgs mappingArgs = createFlowsheetConceptMappingArgs(args);
		if (mappingArgs == null) {
			System.exit(1);
		}
	}
	
}
