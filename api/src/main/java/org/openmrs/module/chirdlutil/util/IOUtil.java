/**
 * 
 */
package org.openmrs.module.chirdlutil.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.util.OpenmrsUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;


/**
 * This class contains utility methods to aide in IO processing.
 * 
 * @author Tammy Dugan, Andrew Martin
 * 
 */
public class IOUtil
{
    private static final Logger log = LoggerFactory.getLogger(IOUtil.class);

    private final static int BUFFER_SIZE = 4096;

    /**
     * Provides an easy way to write from a Reader to a Writer with a buffer
     * 
     * @param input Reader content to be written
     * @param output Writer place to write content
     * @param bufferSize int size of data buffer
     * @param closeOutput whether the output Writer should be closed when
     *        finished
     * @throws IOException
     */
    public static void bufferedReadWrite(Reader input, Writer output,
            int bufferSize, boolean closeOutput) throws IOException
    {
        int bytesRead = 0;
        char[] buff = new char[bufferSize];

        while (-1 != (bytesRead = input.read(buff, 0, buff.length)))
        {
            output.write(buff, 0, bytesRead);
            output.flush();
        }

        input.close();
        if (closeOutput)
        {
            output.close();
        }
    }

    /**
     * Provides an easy way to write from a Reader to a Writer with a buffer
     * 
     * @param input Reader content to be written
     * @param output Writer place to write content
     * @param closeOutput whether the output Writer should be closed when
     *        finished
     * @throws IOException
     */
    public void bufferedReadWrite(Reader input, Writer output,
            boolean closeOutput) throws IOException
    {
        bufferedReadWrite(input, output, BUFFER_SIZE, closeOutput);
    }

    /**
     * Provides an easy way to write from a Reader to a Writer with a buffer
     * 
     * @param input Reader content to be written
     * @param output Writer place to write content
     * @throws IOException
     */
    public static void bufferedReadWrite(Reader input, Writer output)
            throws IOException
    {
        bufferedReadWrite(input, output, BUFFER_SIZE, true);
    }

    /**
     * Provides an easy way to write from a Reader to a Writer with a buffer
     * 
     * @param input Reader content to be written
     * @param output Writer place to write content
     * @param bufferSize int size of data buffer
     * @throws IOException
     */
    public static void bufferedReadWrite(Reader input, Writer output,
            int bufferSize) throws IOException
    {
        bufferedReadWrite(input, output, bufferSize, true);
    }

    /**
     * Provides an easy way to write from an InputStream to an OutputStream with
     * a buffer
     * 
     * @param input InputStream content to be written
     * @param output OutputStream place to write content
     * @param closeOutput whether the OutputStream should be closed
     * @throws IOException
     */
    public static void bufferedReadWrite(InputStream input,
            OutputStream output, boolean closeOutput) throws IOException
    {
        bufferedReadWrite(input, output, BUFFER_SIZE, closeOutput);
    }

    /**
     * Provides an easy way to write from an InputStream to an OutputStream with
     * a buffer
     * 
     * @param input InputStream content to be written
     * @param output OutputStream place to write content
     * @throws IOException
     */
    public static void bufferedReadWrite(InputStream input, OutputStream output)
            throws IOException
    {
        bufferedReadWrite(input, output, BUFFER_SIZE, true);
    }

    /**
     * Provides an easy way to write from an InputStream to an OutputStream with
     * a buffer
     * 
     * @param input InputStream content to be written
     * @param output OutputStream place to write content
     * @param bufferSize int size of data buffer
     * @throws IOException
     */
    public static void bufferedReadWrite(InputStream input,
            OutputStream output, int bufferSize) throws IOException
    {
        bufferedReadWrite(input, output, bufferSize, true);
    }

    /**
     * Provides an easy way to write from an input stream to an output stream
     * with a buffer
     * 
     * @param input InputStream content to be written
     * @param output OutputStream place to write content
     * @param bufferSize int size of data buffer
     * @param closeOutput boolean whether the OutputStream should be closed
     * @throws IOException
     */
    public static void bufferedReadWrite(InputStream input,
            OutputStream output, int bufferSize, boolean closeOutput)
            throws IOException
    {
        int bytesRead = 0;
        byte[] buff = new byte[bufferSize];
        BufferedInputStream inputStream = new BufferedInputStream(input);
        BufferedOutputStream outputStream = new BufferedOutputStream(output);

        try
        {
            while (-1 != (bytesRead = inputStream.read(buff, 0, buff.length)))
            {
                outputStream.write(buff, 0, bytesRead);
            }

            inputStream.close();

            if (closeOutput)
            {
                outputStream.close();
            } else
            {
                outputStream.flush();
            }

        } catch (IOException ex)
        {
            log.error(ex.getMessage());
            log.error(Util.getStackTrace(ex));
            outputStream.close();
            throw new IOException(
                    "An error occurred while trying to write to the output stream");
        }
    }
    
    /**
     * Copies the source file to the target file location
     * @param sourceFilename name of the source file location
     * @param targetFilename name of the target file location
     * @throws Exception
     */
    public static void copyFile(String sourceFilename, String targetFilename) throws Exception
    {
        copyFile(sourceFilename,targetFilename,false);
    }
    
    /**
     * Copies the source file to the target file location
     * @param sourceFilename name of the source file location
     * @param targetFilename name of the target file location
     * @throws Exception
     * 
     * Please use this method with caution, the file size is type casted to int as opposed to long which is what the length() returns.
     */
    public static void copyFile(String sourceFilename, String targetFilename, boolean useBufferSize) throws Exception
    {
        File srcFile = new File(sourceFilename);
        try (FileInputStream sourceFile = new FileInputStream(srcFile);
            FileOutputStream targetFile = new FileOutputStream(new File(targetFilename)))
        {
            if(useBufferSize)
            {
                IOUtil.bufferedReadWrite(sourceFile, targetFile, (int)srcFile.length());
            }
            else
            {
                IOUtil.bufferedReadWrite(sourceFile, targetFile);
            }
            targetFile.close();
            sourceFile.close();
        } catch (Exception e)
        {
            log.error(e.getMessage());
            log.error(Util.getStackTrace(e));
            throw new Exception("copyFile using Buffer size: Error copying " + sourceFilename + " to "
                    + targetFilename);
        }
    }
    /**
     * Deletes the given file
     * @param filename file to delete
     */
    public static void deleteFile(String filename)
    {
        File file = new File(filename);

        if (!file.exists())
        {
            log.error("Delete failed. File {} does not exist.", filename);
            return;
        }

        if (!file.canWrite())
        {
            log.error("Delete failed. File {} is not writable.", filename);
            return;
        }

        // If it is a directory, make sure it is empty
        if (file.isDirectory())
        {
            String[] files = file.list();
            if (files.length > 0)
            {
                log.error("Delete failed. Directory {} is not empty.", filename);
                return;
            }
        }
        
        // Attempt to delete it
        boolean success = file.delete();

        if (!success)
        {
            //try garbage collection to release locks on file
            System.gc();
            // Attempt to delete again
            success = file.delete();
            
            if(!success)
            {
                log.error("Delete failed. Could not delete file {}.", filename);
            }
        }
    }
    
    /**
     * Renames old file to new filename
     * @param oldname old file name
     * @param newname new file name
     */
    public static void renameFile(String oldname, String newname)
    {
        File file = new File(oldname);

        if (!file.exists())
        {
            log.error("Rename failed. File {} does not exist.", oldname);
            return;
        }

        if (!file.canWrite())
        {
            log.error("Rename failed. File {} is not writable.", oldname);
            return;
        }

        // If it is a directory, make sure it is empty
        if (file.isDirectory())
        {
            String[] files = file.list();
            if (files.length > 0)
            {
                log.error("Rename failed. Directory {} is not empty.", oldname);
                return;
            }
        }
        
        // Attempt to rename it
        boolean success = file.renameTo(new File(newname));

        if (!success)
        {
            //try garbage collection to release locks on file
            System.gc();
            // Attempt to rename again
            success = file.renameTo(new File(newname));
            
            if(!success)
            {
                log.error("Rename failed. Could not rename file {} to {}.",oldname, newname);
            }
        }
    }
    
    /**
     * Returns a list of file names within a specific directory
     * 
     * @param xmlDirectory specific directory
     * @return String[] list of file names
     */
    public static String[] getFilesInDirectory(String xmlDirectory)
    {
        File dir = new File(xmlDirectory);

        return dir.list();
    }
    
    public static File[] getFilesInDirectory(String directoryName, final String[] fileExtensions)
    {
        File directory = new File(directoryName);

        File[] files = directory.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                for(String currFileExtension:fileExtensions)
                {
                    if(name.endsWith(currFileExtension)){
                        return true;
                    }
                }
                return false;
            }
        });
        return files;
    }
    
    /**
     * Returns the file name without an extension or directory path
     * @param filepath path to the file
     * @return String file name without an extension or directory path
     */
    public static String getFilenameWithoutExtension(String filepath)
    {
        String filename = filepath;
        int index = filename.lastIndexOf("/");
        int index2 = filename.lastIndexOf("\\");
        
        if(index2 > index)
        {
            index = index2;
        }
        
        if(index>-1)
        {
            filename = filename.substring(index+1);
        }
                
        index = filename.lastIndexOf(".");
        
        if(index > -1)
        {
            filename = filename.substring(0,index);
        }
        
        return filename;
    }
    
    public static String getDirectoryName(String filepath)
    {
        //if there is no dot, then the filepath 
        //is already a directory
        if(filepath.lastIndexOf(".")<0){
            return filepath;
        }
        String filename = filepath;
        int index = filename.lastIndexOf("/");
        int index2 = filename.lastIndexOf("\\");
        
        if(index2 > index)
        {
            index = index2;
        }
        
        if(index>-1)
        {
            filename = filename.substring(0,index);
        }
            
        return filename;
    }
    
    /**
     * Adds slashes if needed to a file directory
     * @param fileDirectory file directory path
     * @return String formatted file directory path
     */
    public static String formatDirectoryName(String fileDirectory)
    {
        if(fileDirectory == null||
            fileDirectory.length()==0){
            fileDirectory = OpenmrsUtil.getApplicationDataDirectory();
        }
        
        if(!(fileDirectory.endsWith("/")||fileDirectory.endsWith("\\")))
        {
            fileDirectory+="/";
        }
        return fileDirectory;
    }
    
    /**
     * 
     * Converts a TIF file into a PDF file using itext library
     * 
     * @param tiff
     * @param pdf
     * @throws Exception 
     */
    public static void convertTifToPDF(String tiff,OutputStream pdf) throws Exception {
        
        try {
            Document document = new Document(PageSize.LETTER,0, 0, 0, 0);
            Rectangle rect = document.getPageSize();
            Float pageWidth = rect.getWidth();
            Float pageHeight = rect.getHeight();
            
            PdfWriter.getInstance(document, pdf);
            
            RandomAccessFileOrArray ra = new RandomAccessFileOrArray(tiff);
            document.open();
            int pages = TiffImage.getNumberOfPages(ra);
            for (int i = 1; i <= pages; i++) {
                Image image = TiffImage.getTiffImage(ra, i);
                
                Float heightPercent = (pageHeight/image.getScaledHeight())*100;
                Float widthPercent = (pageWidth/image.getScaledWidth())*100;
                
                image.scalePercent(heightPercent, widthPercent);
                
                document.add(image);
                if(i < pages){
                    document.newPage();
                }
            }

            document.close();
            ra.close();
        }
        catch (Exception e) {
            log.error("Error converting tiff to PDF", e);
            throw e;
        }
        
    }
    
    /**
     * Finds an image file based on location id, form id, and form instance id 
     * in the provided directory.
     * 
     * @param imageFilename String containing the location id + form id + form instance id.
     * @param imageDir The directory to search for the file.
     * 
     * @return File matching the search criteria.
     */
    public static File searchForImageFile(String imageFilename, String imageDir) {
        //This FilenameFilter will get ALL tifs starting with the filename
        //including of rescan versions nnn_1.tif, nnn_2.tif, etc
        File imagefile = searchForFile(imageFilename, imageDir, ".tif");
        return imagefile;
    }
    
    public static Properties getProps(String filename)
    {
        try
        {

            Properties prop = new Properties();
            InputStream propInputStream = new FileInputStream(filename);
            prop.loadFromXML(propInputStream);
            return prop;

        } catch (FileNotFoundException e)
        {

        } catch (InvalidPropertiesFormatException e)
        {

        } catch (IOException e)
        {
            // TODO Auto-generated catch block

        }
        return null;
    }
    
    /**
     * Creates a String from an input stream.
     * 
     * @param inputStream The input stream to convert to a string.
     * @return String containing the information from the input stream.
     * @throws IOException
     */
    public static String output(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        
        return sb.toString();
    }
    /**
     * Finds a file by directory, name, file type.
     * 
     * @param filename 
     * @param directory The directory to search for the file.
     * @param extension Acceptable file extension.
     * 
     * @return File matching the search criteria.
     */
    public static File searchForFile(String filename, String directory, String extension) {
        //This FilenameFilter will get all files that start with the filename, and end with any 
        //extensions in the extensions set - including rescan versions of files.
        //such as nnn_1.tif, nnn_2.tif, etc
        HashSet<String> extensions = new HashSet<>();
        extensions.add(extension);
        File imagefile =  searchForFile(filename, directory, extensions);
        return imagefile;
    }
    
    /**
     * Finds an a file with any extensions contained in the extensions set. 
     *         //This FilenameFilter will get ALL 
        //including of rescan versions nnn_1.tif, nnn_2.tif, etc
     * 
     * @param filename String containing the location id + form id + form instance id.
     * @param directory The directory to search for the file.
     * @param extensions Set of acceptable file extensions.
     * 
     * @return File matching the search criteria.
     */
    public static File searchForFile(String filename, String directory, HashSet<String> extensions) {

        FilenameFilter filtered = new FileListFilter(filename, extensions);
        File dir = new File(directory);
        File[] files = dir.listFiles(filtered);
        if (!(files == null || files.length == 0)) {
            //This FileDateComparator will list in order
            //with newest file first.
            Arrays.sort(files, new FileDateComparator());
            filename = files[0].getPath();
        }
        File imagefile = new File(filename);
        return imagefile;
    }
    
    /**
     * Locates the merge file on disk.
     * 
     * @param formInstanceTag FormInstanceTag object containing the information needed to find the merge file on disk
     * @return File containing path of the merge file.
     */
    public static File getMergeFile(FormInstanceTag formInstanceTag) {
        Integer formId = formInstanceTag.getFormId();
        Integer formInstanceId = formInstanceTag.getFormInstanceId();
        Integer locationId = formInstanceTag.getLocationId();
        Integer locationTagId = formInstanceTag.getLocationTagId();
        List<String> possibleMergeFilenames = new ArrayList<>();
        String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
                .getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, locationTagId, locationId));
        String pendingMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
                .getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, locationTagId, locationId))
                + ChirdlUtilConstants.FILE_PENDING + File.separator;
        
        // Parse the merge file
        FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
        possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);
        possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_20);
        possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_22);
        possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_23);
        possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_19);
        possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);
        possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_20);
        possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_22);
        possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_23);
        possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_19);
        
        for (String currFilename : possibleMergeFilenames) {
            File file = new File(currFilename);
            if (file.exists()) {
                return file;
            }
        }
        
        return null;
    }
    
    /**
     * Writes a PDF document to an ouput stream containing the phrase "Forms Not Available".
     * 
     * @param outputStream The output stream destination of the PDF document.
     * @param additionalMessage Appends this string after teh "Forms Not Available" phrase.
     * @throws Exception
     */
    public static void createFormNotAvailablePDF (OutputStream outputStream, String additionalMessage) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        float fontSize = 40f;
        float additionalMessageFontSize = 10f;
        
        Paragraph p1 = new Paragraph("\n\nForm", FontFactory.getFont(FontFactory.TIMES_BOLD, fontSize));
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        
        Paragraph p2 = new Paragraph("Not", FontFactory.getFont(FontFactory.TIMES_BOLD, fontSize));
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);
        
        Paragraph p3 = new Paragraph("Available", FontFactory.getFont(FontFactory.TIMES_BOLD, fontSize));
        p3.setAlignment(Element.ALIGN_CENTER);
        document.add(p3);
        
        if (additionalMessage != null) {
            Paragraph p4 = new Paragraph("\n" + additionalMessage, FontFactory.getFont(FontFactory.TIMES_BOLD, additionalMessageFontSize));
            p4.setAlignment(Element.ALIGN_CENTER);
            document.add(p4);
        }
        
        document.close();
    }
}
