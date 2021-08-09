package org.openmrs.module.chirdlutil.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.chirdlutil.util.FlowsheetDescriptor;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

public class CreateFlowsheetConceptMapping {
	
	private static final String FLOWSHEET_CONCEPT_SOURCE_DISPLAY = "flowsheetDisplayConceptSource";
	private static final String FLOWSHEET_CONCEPT_SOURCE_CODE = "flowsheetCodeConceptSource";
	private static final String SOURCE_TYPE_DISPLAY = "Display";
	private static final String SOURCE_TYPE_CODE = "Code";
	private static final Log LOG = LogFactory.getLog(CreateFlowsheetConceptMapping.class);
	private static final String INPUT_ARG = "-input";
	private static final String OUTPUT_ARG = "-output";
	private static final String LOCATION_ARG = "-location";
	private static final String EXAMPLE_MESSAGE = "CreateFlowsheetConceptMapping -output C:\\test\\mappings.csv -input "
			+ "C:\\test\\flowsheetMapping.csv.csv -location ABCD -displayConceptSource \"ABCD Flowsheet Display\" "
			+ "-codeConceptSource \"ABCD Flowsheet Code\"";
	
	/**
	 * Creates a file containing the SQl statements for the concept sources and mappings required to map from the 
	 * internal concepts to the codes and display provided by the EHR.
	 *  
	 * @param mappingArgs The program arguments
	 * @throws IOException
	 */
	public void createFlowsheetConceptMappingFile(FlowsheetConceptMappingArgs mappingArgs) throws IOException {
		try (InputStreamReader inStreamReader = new InputStreamReader(new FileInputStream(mappingArgs.getInput()));
				CSVReader reader = new CSVReader(inStreamReader, ','); 
				PrintWriter writer = new PrintWriter(new FileWriter(mappingArgs.getOutput()));) {
			// Write the Flowsheet ID insert statement
			writeFlowsheetIdSql(reader, writer, mappingArgs);
			
			// Write the concept source insert statements
			writeConceptSourcesSql(writer, mappingArgs);
			
			// Write the concept source location attribute values insert statements
			writeFlowsheetLocationAttributesSql(writer, mappingArgs);
			
			// The next line is blank
			reader.readNext();
			
			// The remainder of the file contains the concept mapping information
			HeaderColumnNameTranslateMappingStrategy<FlowsheetDescriptor> strat = 
					new HeaderColumnNameTranslateMappingStrategy<>();
			
			Map<String, String> map = new HashMap<>();
			map.put("Concept Name", "conceptName");
			map.put("Value Type", "conceptValueType");
			map.put("Description", "conceptDescription");
			map.put("Epic Code", "code");
			map.put("Epic Display", "display");
			
			strat.setType(FlowsheetDescriptor.class);
			strat.setColumnMapping(map);
			
			CsvToBean<FlowsheetDescriptor> csv = new CsvToBean<>();
			List<FlowsheetDescriptor> list = csv.parse(strat, reader);
			if (list == null) {
				list = new ArrayList<>();
			}
			
			writeConceptMappingsSql(writer, list);
		}
	}
	
	/**
	 * Creates and writes the SQL statement to insert the flowsheet ID for the location.
	 * 
	 * @param reader The CSV reader
	 * @param writer The print writer to write to the output file
	 * @param mappingArgs The program arguments
	 * @throws IOException
	 */
	private void writeFlowsheetIdSql(CSVReader reader, PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs) 
			throws IOException {
		// The first line contains the following text: Flowsheet System OID
		reader.readNext();
		
		// The second line contains the flowsheet system OID
		String[] items = reader.readNext();
		if (items == null || items.length == 0) {
			throw new IOException("The file does not contain a flowsheet system OID on line 2, column 1");
		}
		
		String flowsheetId = items[0];
		writer.println("/* Insert the flowsheet ID into the location attribute value table */");
		writer.println(
			"insert into chirdlutilbackports_location_attribute_value (location_id, value, location_attribute_id)");
		writer.println("values ((select location_id");
		writer.println("\\t\t\tfrom location");
		writer.print("\t\t\twhere name = '");
		writer.print(mappingArgs.getLocation());
		writer.println("'), ");
		writer.print("\t\t'");
		writer.print(flowsheetId);
		writer.println("', ");
		writer.println("\t\t(select location_attribute_id");
		writer.println("\t\t\tfrom chirdlutilbackports_location_attribute");
		writer.println("\t\t\twhere name = 'flowsheetSystem'));");
		writer.println();
		writer.flush();
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
	}
	
	/**
	 * Creates and writes the sQL statement to insert a concept source.
	 * 
	 * @param writer The print writer to write the output file
	 * @param mappingArgs The program arguments
	 * @param sourceType The source type, either "Code" or "Display"
	 */
	private void writeConceptSourceSql(
			PrintWriter writer, FlowsheetConceptMappingArgs mappingArgs, String sourceType) {
		writer.print("/* Add the flowsheet ");
		writer.print(sourceType.toLowerCase());
		writer.println(" concept source for the location */");
		writer.println("insert into concept_reference_source(name, description, creator, date_created, uuid) ");
		writer.print("VALUES('");
		writer.print(mappingArgs.getLocation());
		writer.print(" Flowsheet ");
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
		writer.print("\t\t\twhere name = '");
		writer.print(mappingArgs.getLocation());
		writer.println("'),");
		writer.print("\t\t'");
		writer.print(mappingArgs.getLocation());
		writer.print(" Flowsheet ");
		writer.print(sourceType);
		writer.println("',");
		writer.println("\t\t(select location_attribute_id");
		writer.println("\t\t\tfrom chirdlutilbackports_location_attribute");
		writer.print("\t\t\twhere name = '");
		writer.print(attrName);
		writer.println("'));");
		writer.println();
	}
	
	/**
	 * Writes the concept mapping SQL for all concepts in the provided mapping list.
	 * 
	 * @param writer The print writer to write the output file.
	 * @param mappings The concept mappings used to create the SQL statements
	 */
	private void writeConceptMappingsSql(PrintWriter writer, List<FlowsheetDescriptor> mappings) {
		for (FlowsheetDescriptor mapping : mappings) {
			if (StringUtils.isBlank(mapping.getCode()) || StringUtils.isBlank(mapping.getConceptName()) 
					|| StringUtils.isBlank(mapping.getDisplay())) {
				System.err.println("Invalid mapping found.  Please ensure the Concept Name, Epic Code, and Epic "
						+ "Display values exist: " + mapping);
				continue;
			}
			
			writeConceptMappingSql(writer, mapping);
		}
	}
	
	private void writeConceptMappingSql(PrintWriter writer, FlowsheetDescriptor mapping) {
		// Write the insert statements for the concept reference term
		writeConceptRefTermSql(writer, FLOWSHEET_CONCEPT_SOURCE_CODE, mapping.getCode());
		writeConceptRefTermSql(writer, FLOWSHEET_CONCEPT_SOURCE_DISPLAY, mapping.getDisplay());
		
		// Write the insert statement for the concept reference map
	}
	
	private void writeConceptRefTermSql(PrintWriter writer, String conceptSource, String code) {
		writer.println("insert into concept_reference_term (concept_source_id, code, creator, date_created, uuid)");
		writer.print("values((select concept_source_id \n\t\t\tfrom concept_reference_source \n\t\t\twhere name = '");
		writer.print(conceptSource);
		writer.print("'), \n\t\t'");
		writer.print(code);
		writer.println("', 1, NOW(), UUID());");
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
		File outputFile = null;
		File inputFile = null;
		String location = null;
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
				if (isInput) {
					inputFile = new File(arg);
					if (!inputFile.exists() || !inputFile.canRead() || inputFile.isDirectory()) {
						System.err.println("Cannot locate or read input file " + arg);
						return null;
					}
					
					mappingArgs.setInput(inputFile);
				} else if (isOutput) {
					outputFile = new File(arg);
					mappingArgs.setOutput(outputFile);
				} else if (isLocation) {
					location = arg;
					mappingArgs.setLocation(location);
				}
			}
		}
		
		if (outputFile == null) {
			System.err.println("No output file specified.");
			System.err.println(EXAMPLE_MESSAGE);
			return null;
		}
			
		if (inputFile == null) {
			System.err.println("No input files specified.");
			System.err.println(EXAMPLE_MESSAGE);
			return null;
		}
		
		if (StringUtils.isBlank(location)) {
			System.err.println("No location specified.");
			System.err.println(EXAMPLE_MESSAGE);
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
            System.err.println("Please specify the correct parameters.");
            System.err.println(EXAMPLE_MESSAGE);
            System.exit(1);
        }
		
		FlowsheetConceptMappingArgs mappingArgs = createFlowsheetConceptMappingArgs(args);
		if (mappingArgs == null) {
			System.exit(1);
		}
		
		try {
			new CreateFlowsheetConceptMapping().createFlowsheetConceptMappingFile(mappingArgs);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	
}
