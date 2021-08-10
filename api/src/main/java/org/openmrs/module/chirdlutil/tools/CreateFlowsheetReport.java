package org.openmrs.module.chirdlutil.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.ConceptDescriptor;
import org.openmrs.module.chirdlutil.util.FlowsheetDescriptor;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

/**
 * Creates a master document of all numeric and parent coded concepts based upon existing concept files.
 * 
 * @author Steve McKee
 */
public class CreateFlowsheetReport {
	
	private static final Logger LOG = Logger.getLogger(CreateFlowsheetReport.class);
	private static final String INPUT_ARG = "-input";
	private static final String OUTPUT_ARG = "-output";
	private static final String EXAMPLE_MESSAGE = "CreateFlowsheetReport -output C:\\test\\report.csv -input "
			+ "C:\\test\\module\\concepts.csv C:\\test\\module2\\concepts.csv";
	private static final String DATATYPE_CODED = "Coded";
	private static final String DATATYPE_NUMERIC = "Numeric";
	private static final String DATATYPE_STRING = "String";
	
	/**
	 * Creates the flowsheet report based upon the provided concept files.
	 * 
	 * @param outputFile The location the flowsheet report will be written
	 * @param inputFiles The list of input concept CSV files
	 * @throws IOException
	 */
	public void createFlowsheetReport(File outputFile, List<File> inputFiles) throws IOException {
		BasicConfigurator.configure();
		LOG.setLevel(Level.INFO);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                CSVWriter csvWriter = new CSVWriter(writer);) {
			createHeaders(csvWriter);
			List<ConceptDescriptor> concepts = readConcepts(inputFiles);
			List<FlowsheetDescriptor> flowsheetConcepts = createFlowsheetConcepts(concepts);
			writeFlowsheetConcepts(flowsheetConcepts, csvWriter);
		}
	}
	
	/**
	 * Creates the headings at the beginning of the flowsheet report
	 * 
	 * @param csvWriter The CSV writer
	 * @throws IOException
	 */
	private void createHeaders(CSVWriter csvWriter) throws IOException {
		 String[] item = new String[1];
         item[0] = "Flowsheet System OID";
         csvWriter.writeNext(item);
         csvWriter.flush();
         
         item[0] = "Please provide the flowsheet system OID here.";
         csvWriter.writeNext(item);
         csvWriter.flush();
         
         item[0] = ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
         csvWriter.writeNext(item);
         csvWriter.flush();
         
         item = new String[5];
         item[0] = "Concept Name";
         item[1] = "Value Type";
         item[2] = "Description";
         item[3] = "Epic Code";
         item[4] = "Epic Display";
         csvWriter.writeNext(item);
         csvWriter.flush();
	}
	
	/**
	 * Reads the current concept files into a list.  This returns all concepts found in the concepts files.
	 * 
	 * @param inputFiles The list of concept files
	 * @return List of concept descriptors for all concept files provided
	 * @throws IOException
	 */
	private List<ConceptDescriptor> readConcepts(List<File> inputFiles) throws IOException {
		List<ConceptDescriptor> concepts = new ArrayList<>();
		for (File inputFile : inputFiles) {
			try (InputStreamReader inStreamReader = new InputStreamReader(new FileInputStream(inputFile));
					CSVReader reader = new CSVReader(inStreamReader, ',');) {
				HeaderColumnNameTranslateMappingStrategy<ConceptDescriptor> strat = 
						new HeaderColumnNameTranslateMappingStrategy<>();
				
				Map<String, String> map = new HashMap<>();
				map.put("name", "name");
				map.put("concept class", "conceptClass");
				map.put("datatype", "datatype");
				map.put("description", "description");
				map.put("parent concept", "parentConcept");
				map.put("units", "units");
				
				strat.setType(ConceptDescriptor.class);
				strat.setColumnMapping(map);
				
				CsvToBean<ConceptDescriptor> csv = new CsvToBean<>();
				concepts.addAll(csv.parse(strat, reader));
			}
		}
		
		return concepts;
	}
	
	/**
	 * Returns the flowsheet descriptors which contain only the concepts defined as numeric or if coded, do not have a 
	 * parent concept defined.
	 * 
	 * @param concepts The list of all concepts parsed from the concept files
	 * @return The flowsheet descriptors which contain only the concepts defined as numeric or if coded, do not have a 
	 * parent concept defined
	 * @throws IOException
	 */
	private List<FlowsheetDescriptor> createFlowsheetConcepts(List<ConceptDescriptor> concepts) throws IOException {
		Set<FlowsheetDescriptor> flowsheetSet = new LinkedHashSet<>();
		for (ConceptDescriptor concept : concepts) {
			// If a parent concept is stated, do not add to set
			if (StringUtils.isNotBlank(concept.getParentConcept())) {
				continue;
			}
			
			FlowsheetDescriptor flowsheetRow = new FlowsheetDescriptor();
			flowsheetRow.setConceptName(concept.getName());
			String dataType = concept.getDatatype();
			if (DATATYPE_CODED.equalsIgnoreCase(dataType)) {
				flowsheetRow.setConceptValueType(DATATYPE_STRING);
			} else if (DATATYPE_NUMERIC.equals(dataType)) {
				flowsheetRow.setConceptValueType(DATATYPE_NUMERIC);
			} else {
				throw new IOException("Concept " + concept.getName() + " does not contain a valid data type.");
			}
			
			flowsheetRow.setConceptDescription(concept.getDescription());
			flowsheetRow.setCode(ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);
			flowsheetRow.setDisplay(ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);
			flowsheetSet.add(flowsheetRow);
		}
		
		return new ArrayList<>(flowsheetSet);
	}
	
	/**
	 * Writes the flowsheet descriptors to the output file.
	 * 
	 * @param flowsheetConcepts The flowsheet concepts to write to the output file
	 * @param csvWriter The CSV writer
	 * @throws IOException
	 */
	private void writeFlowsheetConcepts(List<FlowsheetDescriptor> flowsheetConcepts, CSVWriter csvWriter) 
			throws IOException {
		for (FlowsheetDescriptor flowsheetRow : flowsheetConcepts) {
			String[] item = new String[5];
            item[0] = flowsheetRow.getConceptName();
            item[1] = flowsheetRow.getConceptValueType();
            item[2] = flowsheetRow.getConceptDescription();
            item[3] = flowsheetRow.getCode();
            item[4] = flowsheetRow.getDisplay();
            csvWriter.writeNext(item);
            csvWriter.flush();
		}
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
		
		try {
			File outputFile = null;
			List<File> inputFiles = new ArrayList<>();
			boolean isInput = false;
			boolean isOutput = false;
			for (String arg : args) {
				if (INPUT_ARG.equalsIgnoreCase(arg)) {
					isInput = true;
					isOutput = false;
				} else if (OUTPUT_ARG.equalsIgnoreCase(arg)) {
					isInput = false;
					isOutput = true;
				} else {
					if (isInput) {
						File inputFile = new File(arg);
						if (!inputFile.exists() || !inputFile.canRead() || inputFile.isDirectory()) {
							LOG.error("Cannot locate or read input file " + arg);
							System.exit(1);
						}
						
						inputFiles.add(inputFile);
					} else if (isOutput) {
						outputFile = new File(arg);
					}
				}
			}
			
			if (outputFile == null) {
				LOG.error("No output file specified.");
				LOG.error(EXAMPLE_MESSAGE);
				System.exit(1);
			}
				
			if (inputFiles.isEmpty()) {
				LOG.error("No input files specified.");
				LOG.error(EXAMPLE_MESSAGE);
				System.exit(1);
			}
			
			CreateFlowsheetReport flowsheetCreator = new CreateFlowsheetReport();
			flowsheetCreator.createFlowsheetReport(outputFile, inputFiles);
			LOG.info("Successfully created flowsheet report file: " + outputFile.getAbsolutePath());
		} catch (Exception e) {
			LOG.error("Error creating flowsheet report.", e);
			System.exit(1);
		}
		
		System.exit(0);
	}
}
