package org.openmrs.module.chirdlutil.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openmrs.module.chirdlutil.util.FlowsheetDescriptor;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

/**
 * Creates a master document of SQL statements needed to map flowsheet rows provided by a hospital system.
 * 
 * @author Steve McKee
 */
public class CreateFlowsheetConceptMapping {
	
	private static final String IF_IT_DOESNT_EXIST = " if it doesn't exist */";
	private static final String SQL_ADD_THE = "/* Add the ";
	private static final String SQL_FLOWSHEET = " Flowsheet ";
	private static final String SQL_WHERE_NAME = "\t\t\twhere name = '";
	private static final String FLOWSHEET_CONCEPT_SOURCE_DISPLAY = "flowsheetDisplayConceptSource";
	private static final String FLOWSHEET_CONCEPT_SOURCE_CODE = "flowsheetCodeConceptSource";
	private static final String FLOWSHEET_CONCEPT_SOURCE_SYSTEM = "flowsheetSystemConceptSource";
	private static final String SOURCE_TYPE_DISPLAY = "Display";
	private static final String SOURCE_TYPE_CODE = "Code";
	private static final String SOURCE_TYPE_SYSTEM = "System";
	private static final Logger LOG = Logger.getLogger(CreateFlowsheetConceptMapping.class);
	private static final String INPUT_ARG = "-input";
	private static final String OUTPUT_ARG = "-output";
	private static final String LOCATION_ARG = "-location";
	private static final String EXAMPLE_MESSAGE = "CreateFlowsheetConceptMapping -output C:\\test\\mappings.csv -input "
			+ "C:\\test\\flowsheetMapping.csv -location ABCD";
	
	/**
	 * Creates a file containing the SQl statements for the concept sources and mappings required to map from the 
	 * internal concepts to the code and display provided by the EHR.
	 *  
	 * @param mappingArgs The program arguments
	 * @throws IOException
	 */
	public void createFlowsheetConceptMappingFile(FlowsheetConceptMappingArgs mappingArgs) throws IOException {
		BasicConfigurator.configure();
		LOG.setLevel(Level.INFO);
		try (InputStreamReader inStreamReader = new InputStreamReader(new FileInputStream(mappingArgs.getInput()));
				CSVReader reader = new CSVReader(inStreamReader, ','); 
				PrintWriter writer = new PrintWriter(new FileWriter(mappingArgs.getOutput()));) {
			
			// Write the concept source insert statements
			writeConceptSourcesSql(writer, mappingArgs);
			
			// Write the concept source location attribute values insert statements
			writeFlowsheetLocationAttributesSql(writer, mappingArgs);
			
			// The remainder of the file contains the concept mapping information
			HeaderColumnNameTranslateMappingStrategy<FlowsheetDescriptor> strat = 
					new HeaderColumnNameTranslateMappingStrategy<>();
			
			Map<String, String> map = new HashMap<>();
			map.put("Concept Name", "conceptName");
			map.put("Value Type", "conceptValueType");
			map.put("Description", "conceptDescription");
			map.put("Epic Code", "code");
			map.put("Epic Display", "display");
			map.put("Epic Flowsheet System OID", "system");
			
			strat.setType(FlowsheetDescriptor.class);
			strat.setColumnMapping(map);
			
			CsvToBean<FlowsheetDescriptor> csv = new CsvToBean<>();
			List<FlowsheetDescriptor> list = csv.parse(strat, reader);
			if (list == null) {
				list = new ArrayList<>();
			}
			
			writeConceptMappingsSql(writer, list, mappingArgs);
		}
	}
	
	/**
	 * Creates and writes the SQL statement to insert the code and display concept sources.
	 * 
	 * @param writer The print writer to write to the output file
	 * @param mappingArgs The program arguments
	 */
	private void writeConceptSourcesSql(PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs) {
		writeConceptSourceSql(writer, mappingArgs, SOURCE_TYPE_CODE);
		writeConceptSourceSql(writer, mappingArgs, SOURCE_TYPE_DISPLAY);
		writeConceptSourceSql(writer, mappingArgs, SOURCE_TYPE_SYSTEM);
		writer.flush();
	}
	
	/**
	 * Creates and writes the SQL statement to insert a concept source.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mappingArgs The program arguments
	 * @param sourceType The source type, either "Code", "Display", or "System"
	 */
	private void writeConceptSourceSql(
			PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs, String sourceType) {
		writer.print("/* Add the flowsheet ");
		writer.print(sourceType.toLowerCase());
		writer.println(" concept source for the location */");
		writer.println("insert into concept_reference_source(name, description, creator, date_created, uuid) ");
		writer.print("VALUES('");
		writer.print(mappingArgs.getLocation());
		writer.print(SQL_FLOWSHEET);
		writer.print(sourceType);
		writer.print("', 'Flowsheet ");
		writer.print(sourceType.toLowerCase());
		writer.print(" concept source for the ");
		writer.print(mappingArgs.getLocation());
		writer.println(" location.', 1, NOW(), UUID());");
		writer.println();
	}
	
	/**
	 * Creates the insert statements for the concept source location attribute values.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mappingArgs The program arguments
	 */
	private void writeFlowsheetLocationAttributesSql(PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs) {
		writeFlowsheetLocationAttributeSql(writer, mappingArgs, SOURCE_TYPE_CODE, FLOWSHEET_CONCEPT_SOURCE_CODE);
		writeFlowsheetLocationAttributeSql(writer, mappingArgs, SOURCE_TYPE_DISPLAY, FLOWSHEET_CONCEPT_SOURCE_DISPLAY);
		writeFlowsheetLocationAttributeSql(writer, mappingArgs, SOURCE_TYPE_SYSTEM, FLOWSHEET_CONCEPT_SOURCE_SYSTEM);
		writer.flush();
	}
	
	/**
	 * Creates the insert statement for the concept source location attribute value.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mappingArgs The program arguments
	 * @param sourceType The source type, either "Code" or "Display"
	 * @param attrName The location attribute name, either "flowsheetCodeConceptSource" or 
	 * "flowsheetDisplayConceptSource"
	 */
	private void writeFlowsheetLocationAttributeSql(
			PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs, String sourceType, String attrName) {
		writer.print("/* Add the flowsheet ");
		writer.print(sourceType.toLowerCase());
		writer.println(" concept source location attribute value for the location */");
		writer.println(
			"insert into chirdlutilbackports_location_attribute_value (location_id, value, location_attribute_id)");
		writer.println("values ((select location_id");
		writer.println("\t\t\tfrom location");
		writer.print(SQL_WHERE_NAME);
		writer.print(mappingArgs.getLocation());
		writer.println("'),");
		writer.print("\t\t'");
		writer.print(mappingArgs.getLocation());
		writer.print(SQL_FLOWSHEET);
		writer.print(sourceType);
		writer.println("',");
		writer.println("\t\t(select location_attribute_id");
		writer.println("\t\t\tfrom chirdlutilbackports_location_attribute");
		writer.print(SQL_WHERE_NAME);
		writer.print(attrName);
		writer.println("'));");
		writer.println();
	}
	
	/**
	 * Writes the concept mapping SQL for all concepts in the provided mapping list.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mappings The concept mappings used to create the SQL statements
	 * @param mappingArgs The program arguments
	 */
	private void writeConceptMappingsSql(
			PrintWriter writer, List<FlowsheetDescriptor> mappings, FlowsheetConceptMappingArgs mappingArgs) {
		Set<String> usedCodes = new HashSet<>();
		Set<String> usedDisplays = new HashSet<>();
		Set<String> usedSystems = new HashSet<>();
		for (FlowsheetDescriptor mapping : mappings) {
			if (StringUtils.isBlank(mapping.getCode()) || StringUtils.isBlank(mapping.getConceptName()) 
					|| StringUtils.isBlank(mapping.getDisplay()) || StringUtils.isBlank(mapping.getSystem())) {
				LOG.warn("Invalid mapping found.  Please ensure the Concept Name, Epic Code, Epic "
						+ "Display, and Epic Flowsheet System OID values exist: " + mapping);
				continue;
			}
			
			writeConceptMappingSql(writer, mapping, mappingArgs, usedCodes, usedDisplays, usedSystems);
		}
		
		usedCodes.clear();
		usedDisplays.clear();
		usedSystems.clear();
	}
	
	/**
	 * Writes the concept reference term and mapping SQL statements for the provided mapping.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mapping The concept mapping
	 * @param mappingArgs The program arguments
	 * @param usedCodes Set containing codes that have already been written to prevent duplicate code insertions.
	 * @param usedDisplays Set containing displays that have already been written to prevent duplicate display 
	 * insertions.
	 * @param usedSystems Set containing systems that have already been written to prevent duplicate system insertions.
	 */
	private void writeConceptMappingSql(PrintWriter writer, FlowsheetDescriptor mapping, 
			FlowsheetConceptMappingArgs mappingArgs, Set<String> usedCodes, Set<String> usedDisplays, 
			Set<String> usedSystems) {
		// Write the insert statements for the code concept reference term
		writer.print(SQL_ADD_THE);
		writer.write(mapping.getConceptName());
		writer.print(" code concept reference term for the flowsheet code concept source");
		writer.println(IF_IT_DOESNT_EXIST);
		writeConceptRefTermSql(writer, SOURCE_TYPE_CODE, mapping.getCode(), mappingArgs, usedCodes);
		
		// Write the concept term mapping for the code concept reference term
		writer.print(SQL_ADD_THE);
		writer.write(mapping.getConceptName());
		writer.print(" code concept reference term mapping for the flowsheet code concept source");
		writer.println(IF_IT_DOESNT_EXIST);
		writeConceptRefTermMapping(writer, mappingArgs, mapping, SOURCE_TYPE_CODE, mapping.getCode());
		
		// Write the insert statements for the display concept reference term
		writer.print(SQL_ADD_THE);
		writer.write(mapping.getConceptName());
		writer.print(" display concept reference term for the flowsheet display concept source");
		writer.println(IF_IT_DOESNT_EXIST);
		writeConceptRefTermSql(writer, SOURCE_TYPE_DISPLAY, mapping.getDisplay(), mappingArgs, usedDisplays);
		
		// Write the concept term mapping for the display concept reference term
		writer.print(SQL_ADD_THE);
		writer.write(mapping.getConceptName());
		writer.print(" display concept reference term mapping for the flowsheet display concept source");
		writer.println(IF_IT_DOESNT_EXIST);
		writeConceptRefTermMapping(writer, mappingArgs, mapping, SOURCE_TYPE_DISPLAY, mapping.getDisplay());
		
		// Write the insert statements for the system concept reference term
		writer.print(SQL_ADD_THE);
		writer.write(mapping.getConceptName());
		writer.print(" system concept reference term for the flowsheet system concept source");
		writer.println(IF_IT_DOESNT_EXIST);
		writeConceptRefTermSql(writer, SOURCE_TYPE_SYSTEM, mapping.getSystem(), mappingArgs, usedSystems);
		
		// Write the concept term mapping for the system concept reference term
		writer.print(SQL_ADD_THE);
		writer.write(mapping.getConceptName());
		writer.print(" system concept reference term mapping for the flowsheet system concept source");
		writer.println(IF_IT_DOESNT_EXIST);
		writeConceptRefTermMapping(writer, mappingArgs, mapping, SOURCE_TYPE_SYSTEM, mapping.getSystem());
	}
	
	/**
	 * Writes the concept reference term SQL for the provided concept source and code.
	 * 
	 * @param writer The print writer to write the output file
	 * @param sourceType The source type, either "Code", "Display", or "System"
	 * @param code The concept reference term code (value)
	 * @param mappingArgs The flowsheet mapping values
	 * @param usedCodes Previously used values so we don't insert them again.
	 */
	private void writeConceptRefTermSql(PrintWriter writer, String sourceType, String code, 
			FlowsheetConceptMappingArgs mappingArgs, Set<String> usedCodes) {
		if (!usedCodes.contains(code)) {
			writer.println("insert into concept_reference_term (concept_source_id, code, creator, date_created, uuid)");
			writer.print("values((select concept_source_id \n\t\t\tfrom concept_reference_source \n\t\t\twhere name = '");
			writer.print(mappingArgs.getLocation());
			writer.print(SQL_FLOWSHEET);
			writer.print(sourceType);
			writer.print("'), \n\t\t'");
			writer.print(code);
			writer.println("', 1, NOW(), UUID());");
			writer.println();
			usedCodes.add(code);
		}
	}
	
	/**
	 * Writes the concept reference term mapping SQL for the provided mapping.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mappingArgs The program arguments
	 * @param mapping The concept mapping
	 * @param sourceType The source type, either "Code", "Display", or "System"
	 * @param code The reference term code value
	 */
	private void writeConceptRefTermMapping(PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs, 
			FlowsheetDescriptor mapping, String sourceType, String code) {
		writer.print("insert into concept_reference_map (concept_reference_term_id, concept_map_type_id, creator, ");
		writer.println("date_created, concept_id, uuid)");
		writer.println("values((select concept_reference_term_id \n\t\t\tfrom concept_reference_term");
		writer.print("\t\t\twhere concept_source_id = (select concept_source_id\n\t\t\t\t\t\t\t\t\t\tfrom concept_reference_source");
		writer.print("\n\t\t\t\t\t\t\t\t\t\twhere name = '");
		writer.print(mappingArgs.getLocation());
		writer.print(SQL_FLOWSHEET);  
		writer.print(sourceType);
		writer.print("')\n\t\t\tand code = '");
		writer.print(code);
		writer.print("'),");
		writer.print("\n\t\t(select concept_map_type_id \n\t\t\tfrom concept_map_type \n\t\t\twhere name = ");
		writer.println("'NARROWER-THAN'), 1, NOW(),");
		writer.print("\t\t(select concept_id \n\t\t\tfrom concept_name \n\t\t\twhere name = '");
		writer.print(mapping.getConceptName());
		writer.println("'), UUID());");
		writer.println();
	}
	
	/**
	 * Creates the arguments object from the arguments provided to the application.
	 * 
	 * @param args The arguments provided to the application
	 * @return Object containing the program arguments
	 */
	private static FlowsheetConceptMappingArgs createFlowsheetConceptMappingArgs(String[] args) {
		FlowsheetConceptMappingArgs mappingArgs = new FlowsheetConceptMappingArgs();
		boolean isInput = false;
		boolean isOutput = false;
		boolean isLocation = false;
		for (String arg : args) {
			if (INPUT_ARG.equalsIgnoreCase(arg)) {
				isInput = true;
				isOutput = false;
				isLocation = false;
			} else if (OUTPUT_ARG.equalsIgnoreCase(arg)) {
				isInput = false;
				isOutput = true;
				isLocation = false;
			} else if (LOCATION_ARG.equalsIgnoreCase(arg)) {
				isInput = false;
				isOutput = false;
				isLocation = true;
			} else {
				setArgument(mappingArgs, isInput, isOutput, isLocation, arg);
			}
		}
		
		return mappingArgs;
	}
	
	/**
	 * Sets the mapping argument.
	 * 
	 * @param mappingArgs The program arguments
	 * @param isInput True if the argument is the input file, false otherwise
	 * @param isOutput True if the argument is the output file, false otherwise
	 * @param isLocation True if the argument is the location, false otherwise
	 * @param arg The argument
	 */
	private static void setArgument(FlowsheetConceptMappingArgs mappingArgs, boolean isInput, boolean isOutput, 
			boolean isLocation, String arg) {
		if (isInput) {
			File inputFile = new File(arg);
			if (!inputFile.exists() || !inputFile.canRead() || inputFile.isDirectory()) {
				LOG.error("Cannot locate or read input file " + arg);
				return;
			}
			
			mappingArgs.setInput(inputFile);
		} else if (isOutput) {
			File outputFile = new File(arg);
			mappingArgs.setOutput(outputFile);
		} else if (isLocation) {
			mappingArgs.setLocation(arg);
		}
	}
	
	/**
	 * Main method
	 * 
	 * @param args The program arguments
	 */
	public static void main(String[] args) {
		if (args == null || args.length == 0) {
            LOG.error("Please specify the correct parameters.");
            LOG.error(EXAMPLE_MESSAGE);
            System.exit(1);
        }
		
		FlowsheetConceptMappingArgs mappingArgs = createFlowsheetConceptMappingArgs(args);
		if (mappingArgs.getOutput() == null) {
			LOG.error("No output file specified.");
			LOG.error(EXAMPLE_MESSAGE);
		} else if (mappingArgs.getInput() == null) {
			LOG.error("No input files specified.");
			LOG.error(EXAMPLE_MESSAGE);
		} else if (StringUtils.isBlank(mappingArgs.getLocation())) {
			LOG.error("No location specified.");
			LOG.error(EXAMPLE_MESSAGE);
		}
		
		try {
			new CreateFlowsheetConceptMapping().createFlowsheetConceptMappingFile(mappingArgs);
			LOG.info("Successfully created flowsheet report file: " + mappingArgs.getOutput().getAbsolutePath());
		}
		catch (Exception e) {
			LOG.error("Error creating flowsheet mappings", e);
			System.exit(1);
		}
		
		System.exit(0);
	}
	
}
