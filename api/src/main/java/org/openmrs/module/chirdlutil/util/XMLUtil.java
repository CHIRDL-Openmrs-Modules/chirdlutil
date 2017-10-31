package org.openmrs.module.chirdlutil.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.LineSeparator;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains several utility methods to aide in XML processing.
 * 
 * @author Tammy Dugan
 *
 */
public class XMLUtil
{
	private static Log log = LogFactory.getLog(XMLUtil.class);
	
	public static final String DEFAULT_EXPORT_DIRECTORY = "defaultExportDirectory";
	public static final String DEFAULT_MERGE_DIRECTORYY = "defaultMergeDirectory";
	
	/**
	 * Serializes a dom tree to an xml file
	 * 
	 * @param rootElement root element to serialize
	 * @param fileName name of file to write xml to
	 * @param doc xml document
	 * @throws IOException 
	 */
	public static void xmlToFile(Element rootElement, String fileName,
			Document doc) throws IOException 
	{
		try
		{
			FileOutputStream outputFile = new FileOutputStream(fileName);
			xmlToOutputStream(outputFile, doc, rootElement);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("The xml output file: "+fileName+" could not be found.");
		}
	}

	/**
	 * Writes an xml dom to an output stream
	 * 
	 * @param output output stream for xml
	 * @param doc xml document
	 * @param rootElement root element to serialize
	 * @throws IOException 
	 */
	public static void xmlToOutputStream(OutputStream output, Document doc,
			Element rootElement) throws IOException
	{
		OutputFormat format = new OutputFormat(doc);
		format.setLineSeparator(LineSeparator.Windows);
		format.setIndenting(true);
		format.setLineWidth(0);
		format.setPreserveSpace(true);
		format.setEncoding("ISO-8859-1");
		try
		{
			XMLSerializer serializer = new XMLSerializer(output, format);
			serializer.asDOMSerializer();
			serializer.serialize(rootElement);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("Error writing xml to output stream.");
		}
	}

	/**
	 * Creates an empty dom object
	 * 
	 * @return Document newly created dom object
	 */
	public static Document createDOM()
	{
		try
		{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			return doc;
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
		return null;
	}

	/**
	 * Takes in an input file name and parses the xml file into a dom
	 * 
	 * @param inputFilename name of the input file
	 * @return Document dom of the xml from the file 
	 * @throws IOException 
	 */
	public static Document parseXMLFromFile(String inputFilename) throws IOException
	{
		InputStream input = null;

		try
		{
			input = new FileInputStream(inputFilename);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("The xml input file name is: "+inputFilename+". You must provide a valid input file name.");
		}
		return parseXMLFromInputStream(input);
	}

	/**
	 * Parses xml from an input stream into a dom
	 * 
	 * @param input input stream
	 * @return Document dom tree for input xml
	 * @throws IOException 
	 */
	public static Document parseXMLFromInputStream(InputStream input) throws IOException
	{
		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(input);

			return doc;
		}  catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("Error parsing xml input stream.");
		}
	}

	/**
	 * Transforms an input stream to an output stream using an xslt transform
	 * 
	 * @param transformInput data to be transformed
	 * @param transformOutput output of transform
	 * @param xslt transform xslt
	 * @param parameters parameters to the xslt transform 
	 * @throws IOException 
	 */
	public static void transformXML(InputStream transformInput,
			OutputStream transformOutput, InputStream xslt,
			HashMap<String,Object> parameters) throws IOException
	{
		Source xmlSource = new StreamSource(transformInput);
		Source xsltSource = new StreamSource(xslt);

		// create the xslt tranformer
		TransformerFactory transFact = TransformerFactory.newInstance();
		
		try
		{
			Transformer trans = transFact.newTransformer(xsltSource);
			if(parameters != null)
			{
				Iterator<String> keys = parameters.keySet().iterator();
				while(keys.hasNext())
				{
					String name = keys.next();
					Object value = parameters.get(name);
					trans.setParameter(name, value);
				}
			}
			
			trans.transform(xmlSource, new StreamResult(transformOutput));
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("Error transforming xml.");
		}
	}
	
	/**
	 * Serializes an object to xml using JiBx
	 * @param objectToSerialize object to serialize to xml
	 * @param output output stream to write xml to
	 * @throws IOException
	 */
	public static void serializeXML(Object objectToSerialize, OutputStream output) throws IOException 
	{
		try
		{
			IBindingFactory bfact = BindingDirectory.getFactory(objectToSerialize.getClass());
			IMarshallingContext mctx = bfact.createMarshallingContext();
			mctx.marshalDocument(objectToSerialize, "ISO-8859-1", null, output);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("Error writing xml to output stream.");
		}
	}
	
	/**
	 * Deserializes an input stream of xml into an object
	 * using JiBx
	 * @param objectClass type of object to create
	 * @param input xml input
	 * @return Object resulting object
	 * @throws IOException
	 */
	public static Object deserializeXML(Class objectClass, InputStream input) throws IOException
	{
		try
		{
			IBindingFactory bfact = BindingDirectory.getFactory(objectClass);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			return uctx.unmarshalDocument(input, null);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
			throw new IOException("Error parsing xml input stream.");
		}
	}
	
	/**
	 * Returns a node's child node by tag name.
	 * 
	 * @param parentNode The node containing the child node.
	 * @param tagName The tag name of the child node.
	 * @return Node of the parent node or null if a child with the tag name cannot be found.
	 */
	public static Node getChildNode(Node parentNode, String tagName) {
		NodeList nodes = parentNode.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (tagName.equals(node.getNodeName())) {
				return node;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a list of child nodes by tag name.
	 * 
	 * @param parentNode The node containing the child nodes.
	 * @param tagName The tag name of the child nodes.
	 * @return List of Node objects of the parent node.
	 */
	public static List<Node> getChildNodes(Node parentNode, String tagName) {
		NodeList nodes = parentNode.getChildNodes();
		List<Node> finalNodes = new ArrayList<Node>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (tagName.equals(node.getNodeName())) {
				finalNodes.add(node);
			}
		}
		
		return finalNodes;
	}
	
	/**
	 * Returns an attribute value for a node.
	 * 
	 * @param node The node containing the attribute.
	 * @param attributeName The name of the attribute used to find the value.
	 * @return The value of the attribute or null if the attribute cannot be found.
	 */
	public static String getAttributeValue(Node node, String attributeName) {
		return ((Element)node).getAttribute(attributeName);
	}
	
	/**
	 * Returns the value of a node's child node.
	 * 
	 * @param parentNode The node containing the data to find.
	 * @param childTagName The tag name of the child node.
	 * @return The value of a child or null if the child not cannot be found or doesn't contain a value.
	 */
	public static String getChildNodeValue(Node parentNode, String childTagName) {
		Node childNode = XMLUtil.getChildNode(parentNode, childTagName);
		if (childNode != null) {
			return childNode.getTextContent();
		}
		
		return null;
	}
	
	/**
	 * Performs a transformation on the given file with the stylesheet.
	 * 
	 * @param xmlFile The XML file to transform.
	 * @param stylesheet The stylesheet file used for the transformation.
	 * @return The result of the transformation or null if the transformation fails.
	 * @throws Exception When an error occurs during the transformation process.
	 */
	public static String transformFile(File xmlFile, File stylesheet) throws Exception {
		InputStream xmlStream = null;
		InputStream xsltStream = null;
		OutputStream outStream = new ByteArrayOutputStream();
		String result = null;
		try {
			xmlStream = new FileInputStream(xmlFile);
			xsltStream = new FileInputStream(stylesheet);
			XMLUtil.transformXML(xmlStream, outStream, xsltStream, null);
			result = outStream.toString();
		} finally {
			if (xmlStream != null) {
				xmlStream.close();
			}
			if (xsltStream != null) {
				xsltStream.close();
			}
			if (outStream != null) {
				outStream.close();
			}
		}
		
		return result;
	}
	
	/**
	 * Retrieves the scan XML file based on the parameters provided.
	 * 
	 * @param locationId The clinic location ID.
	 * @param formId The ID of the general form.
	 * @param formInstanceId The ID of the instance of the general form.
	 * @param formAttributeName The name of the form attribute used to locate the file.  Constants in this class (
	 * DEFAULT_EXPORT_DIRECTORY,DEFAULT_MERGE_DIRECTORY) should be used for this parameter.
	 * @return A File referencing the merge XML.  Null will be returned if the XML merge file cannot be found.
	 */
	public static File getXmlFile(Integer locationId, Integer formId, Integer formInstanceId, String formAttributeName) {
		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		Set<LocationTag> locationTags = location.getTags();
		FormAttributeValue formLocationValue = null;
		Iterator<LocationTag> setIterator = locationTags.iterator();
		File xmlFile = null;
		while (setIterator.hasNext() && xmlFile == null) {
			LocationTag locationTag = setIterator.next();
			formLocationValue = chirdlUtilBackportsService.getFormAttributeValue(formId, formAttributeName, 
				locationTag.getLocationTagId(), locationId);
			if (formLocationValue == null) {
				continue;
			}
			
			if (DEFAULT_EXPORT_DIRECTORY.equals(formAttributeName)) {
				xmlFile = findScanXmlFile(formLocationValue.getValue(), locationId, formId, formInstanceId);
			} else if (DEFAULT_MERGE_DIRECTORYY.equals(formAttributeName)) {
				xmlFile = findMergeXmlFile(formLocationValue.getValue(), locationId, formId, formInstanceId);
			}
		}
		
		return xmlFile;
	}
	
	/**
	 * Finds the merge XML based on the parameters provided.
	 * 
	 * @param defaultMergeDirectory The directory where the merge files resides.
	 * @param locationId The clinic location ID.
	 * @param formId The ID of the general form.
	 * @param formInstanceId The ID of the instance of the general form.
	 * @return A File referencing the merge XML.  Null will be returned if the XML merge file cannot be found.
	 */
	public static File findScanXmlFile(String defaultMergeDirectory, Integer locationId, Integer formId, Integer formInstanceId) {
		File mergeDirectory = new File(defaultMergeDirectory);
		if (!mergeDirectory.exists()) {
			return null;
		}
		
		File archiveDirectory = new File(mergeDirectory, "Archive");
		int i = 22;
		String filename = locationId + "_" + formId + "_" + formInstanceId + "." + i;
		String rescanFilename = locationId + "_" + formId + "_" + formInstanceId + "_rescan." + i;
		File xmlFile = new File(mergeDirectory, rescanFilename);
		if (xmlFile.exists()) {
			return xmlFile;
		}
		
		xmlFile = new File(mergeDirectory, filename);
		while (i > 18 && !xmlFile.exists()) {
			i--;
			filename = locationId + "_" + formId + "_" + formInstanceId + "." + i;
			rescanFilename = locationId + "_" + formId + "_" + formInstanceId + "_rescan." + i;
			xmlFile = new File(mergeDirectory, rescanFilename);
			if (xmlFile.exists()) {
				return xmlFile;
			}
			
			xmlFile = new File(mergeDirectory, filename);
			if (!xmlFile.exists()) {
				if (archiveDirectory.exists()) {
					xmlFile = new File(archiveDirectory, rescanFilename);
					if (!xmlFile.exists()) {
						xmlFile = new File(archiveDirectory, filename);
					}
				}
			}
		}
		
		if (xmlFile.exists()) {
			return xmlFile;
		}
		
		return null;
	}
	
	/**
	 * Finds the merge XML based on the parameters provided.
	 * 
	 * @param defaultMergeDirectory The directory where the merge files reside.
	 * @param locationId The clinic location ID.
	 * @param formId The ID of the general form.
	 * @param formInstanceId The ID of the instance of the general form.
	 * @return A File referencing the merge XML.  Null will be returned if the XML merge file cannot be found.
	 */
	public static File findMergeXmlFile(String defaultMergeDirectory, Integer locationId, Integer formId, Integer formInstanceId) {
		File mergeDirectory = new File(defaultMergeDirectory);
		if (!mergeDirectory.exists()) {
			return null;
		}
		
		File pendingDirectory = new File(mergeDirectory, ChirdlUtilConstants.FILE_PENDING);
		File xmlFile = findMergeXmlFile(mergeDirectory, pendingDirectory, locationId, formId, formInstanceId);
		
		if (xmlFile != null && xmlFile.exists()) {
			return xmlFile;
		}
		
		File pendingArchiveDirectory = new File(pendingDirectory, ChirdlUtilConstants.FILE_ARCHIVE);
		xmlFile = findMergeXmlFile(pendingDirectory, pendingArchiveDirectory, locationId, formId, formInstanceId);
		
		if (xmlFile != null && xmlFile.exists()) {
			return xmlFile;
		}
		
		File archiveDirectory = new File(mergeDirectory, ChirdlUtilConstants.FILE_ARCHIVE);
		xmlFile = findMergeXmlFile(mergeDirectory, archiveDirectory, locationId, formId, formInstanceId);
		
		if (xmlFile != null && xmlFile.exists()) {
			return xmlFile;
		}
		
		return null;
	}
	
	/**
	 * Finds the merge XML based on the parameters provided.
	 * 
	 * @param mergeDirectory The directory where the merge files reside.
	 * @param subDirectory The subDirectory to search for the merge file.
	 * @param locationId The clinic location ID.
	 * @param formId The ID of the general form.
	 * @param formInstanceId The ID of the instance of the general form.
	 * @return A File referencing the merge XML.  Null will be returned if the XML merge file cannot be found.
	 */
	public static File findMergeXmlFile(File mergeDirectory, File subDirectory, Integer locationId, Integer formId, Integer formInstanceId) {
		String filename = locationId + "_" + formId + "_" + formInstanceId + ChirdlUtilConstants.FILE_EXTENSION_XML;
		File xmlFile = new File(mergeDirectory, filename);
		if (xmlFile.exists()) {
			return xmlFile;
		}
		
		xmlFile = new File(subDirectory, filename);
		if (xmlFile.exists()) {
			return xmlFile;
		}
		
		int i = 22;
		filename = locationId + "_" + formId + "_" + formInstanceId + "." + i;
		xmlFile = new File(mergeDirectory, filename);
		while (i > 18 && !xmlFile.exists()) {
			i--;
			filename = locationId + "_" + formId + "_" + formInstanceId + "." + i;
			xmlFile = new File(mergeDirectory, filename);
			if (!xmlFile.exists()) {
				if (subDirectory.exists()) {
					xmlFile = new File(subDirectory, filename);
				}
			}
		}
		
		if (xmlFile.exists()) {
			return xmlFile;
		}
		
		return null;
	}
	
	/**
	 * Finds the styleheet for the transformation based on the stylesheet filename provided.
	 * 
	 * @param stylesheet The name of the stylesheet file.
	 * @return A File referencing the stylesheet.  Null will be returned if the stylesheet file cannot be found.
	 */
	public static File findStylesheet(String stylesheet) {
		AdministrationService adminService = Context.getAdministrationService();
		String stylesheetDirStr = adminService.getGlobalProperty("chica.stylesheetDirectory");
		if (stylesheetDirStr == null) {
			log.error("Please specify a value for the chica.stylesheetDirectory global property");
			return null;
		}
		
		File stylesheetFile = new File(stylesheetDirStr, stylesheet);
		if (stylesheetFile.exists()) {
			return stylesheetFile;
		}
		
		return null;
	}
}
