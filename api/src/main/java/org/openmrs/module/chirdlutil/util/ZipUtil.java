package org.openmrs.module.chirdlutil.util;

/*
 * Copyright 2010 Srikanth Reddy Lingala  
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipException;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.notification.Message;
import org.openmrs.notification.MessageException;
import org.openmrs.notification.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

/**
 * Utility class for handling zip files.
 * 
 * @author Steve McKee
 */
public class ZipUtil {
    
    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);
    
    /**
     * Private constructor method
     */
    private ZipUtil() {
    	// Intentionally left blank.
    }
    
    /**
     * Creates a zip file containing all the provided files.
     * 
     * @param destinationZipFile The location of the new zip file.
     * @param filesToAdd The files to add to the zip file.
     * @throws IOException
     */
    public static void zipFiles(File destinationZipFile, List<File> filesToAdd) throws IOException {
        ZipParameters parameters = generateParameters(false);
        try (ZipFile zipFile = new ZipFile(destinationZipFile)) {
            // Now add files to the zip file
            // Note: To add a single file, the method addFile can be used
            // Note: If the zip file already exists and if this zip file is a split file
            // then this method throws an exception as Zip Format Specification does not 
            // allow updating split zip files
            zipFile.addFiles(filesToAdd, parameters);
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error zipping files", e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Creates a password protected zip file containing all the provided files.
     * 
     * @param destinationZipFile The location of the new zip file.
     * @param filesToAdd The files to add to the zip file.
     * @param encryptionPassword The password for the zip file.
     * @throws IOException
     */
    public static void zipFilesWithPassword(File destinationZipFile, List<File> filesToAdd, String encryptionPassword)
    throws IOException {
        ZipParameters parameters = generateParameters(true);
        try (ZipFile zipFile = new ZipFile(destinationZipFile, encryptionPassword.toCharArray())) {
            // Now add files to the zip file
            // Note: To add a single file, the method addFile can be used
            // Note: If the zip file already exists and if this zip file is a split file
            // then this method throws an exception as Zip Format Specification does not 
            // allow updating split zip files
            zipFile.addFiles(filesToAdd, parameters);
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error zipping files with password", e);
            throw new ZipException(e.getMessage());
        }

    }
    
    /**
     * Creates a zip file containing the provided folder.
     * 
     * @param destinationZipFile The location of the new zip file.
     * @param folder The folder to add to the zip file.
     * @throws IOException
     */
    public static void zipFolder(File destinationZipFile, File folder) throws IOException {
        ZipParameters parameters = generateParameters(false);
        try (ZipFile zipFile = new ZipFile(destinationZipFile)) {
            // Now add files to the zip file
            // Note: To add a single file, the method addFile can be used
            // Note: If the zip file already exists and if this zip file is a split file
            // then this method throws an exception as Zip Format Specification does not 
            // allow updating split zip files
            zipFile.addFolder(folder, parameters);
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error zipping folder {}",folder, e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Creates a password protected zip file containing the provided folder.
     * 
     * @param destinationZipFile The location of the new zip file.
     * @param folder The folder to add to the zip file.
     * @param encryptionPassword The password for the zip file.
     * @throws IOException
     */
    public static void zipFolderWithPassword(File destinationZipFile, File folder, String encryptionPassword)
    throws IOException {
        ZipParameters parameters = generateParameters(true);
        try (ZipFile zipFile = new ZipFile(destinationZipFile, encryptionPassword.toCharArray())) {
            // Now add files to the zip file
            // Note: To add a single file, the method addFile can be used
            // Note: If the zip file already exists and if this zip file is a split file
            // then this method throws an exception as Zip Format Specification does not 
            // allow updating split zip files
            zipFile.addFolder(folder, parameters);
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error zipping folder {} with password", folder, e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Extracts all files from a zip file to the provided folder.
     * 
     * @param zipFile The file to unzip.
     * @param destinationFolder The folder where the files will be placed.
     * @throws IOException
     */
    public static void extractAllFiles(File zipFile, File destinationFolder) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            // Extract the ZipFile to the specified folder.
            zip.extractAll(destinationFolder.getAbsolutePath());
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error extracting all files from zip {}", zipFile , e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Extracts all files from a password protected zip file to the provided folder.
     * 
     * @param zipFile The file to unzip.
     * @param destinationFolder The folder where the files will be placed.
     * @param password The password for the zip file.
     * @throws IOException
     */
    public static void extractAllEncryptedFiles(File zipFile, File destinationFolder, String password) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile, password.toCharArray())) {
            // Extract the ZipFile to the specified folder.
            zip.extractAll(destinationFolder.getAbsolutePath());
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error extracting all files from encrypted zip {}", zipFile, e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Extracts a specific file from a zip file.
     * 
     * @param zipFile The zip file containing the file to be extracted.
     * @param filenameInZip The filename of the file in the zip to be extracted.
     * @param destinationFolder The folder where the extracted file will be placed.
     * @throws IOException
     */
    public static void extractFile(File zipFile, String filenameInZip, File destinationFolder) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            // Extract the ZipFile to the specified folder.
            zip.extractFile(filenameInZip, destinationFolder.getAbsolutePath());
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error extracting file from zip {}", zipFile, e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Extracts a specific file from a password protected zip file.
     * 
     * @param zipFile The zip file containing the file to be extracted.
     * @param filenameInZip The filename of the file in the zip to be extracted.
     * @param destinationFolder The folder where the extracted file will be placed.
     * @param password The password for the zip file.
     * @throws IOException
     */
    public static void extractEncryptedFile(File zipFile, String filenameInZip, File destinationFolder, String password)
    throws IOException {
        try (ZipFile zip = new ZipFile(zipFile, password.toCharArray())) {
            // Extract the ZipFile to the specified folder.
            zip.extractFile(filenameInZip, destinationFolder.getAbsolutePath());
        }
        catch (net.lingala.zip4j.exception.ZipException e) {
            log.error("Error extracting file from encrypted zip {}", zipFile, e);
            throw new ZipException(e.getMessage());
        }
    }
    
    /**
     * Utility method to create zip parameters.
     * 
     * @param encrypt Boolean explaining whether or not the encryption flag should be set on the
     *            parameters.
     * @return
     */
    private static ZipParameters generateParameters(boolean encrypt) {
        // Initiate Zip Parameters which define various properties such
        // as compression method, etc. More parameters are explained in other
        // examples
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE); // set compression method to deflate compression
        
        // Set the compression level. This value has to be in between 0 to 9
        // Several predefined compression levels are available
        // DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
        // DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
        // DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
        // DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
        // DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        
        if (encrypt) {
            // Set the encryption flag to true
            // If this is set to false, then the rest of encryption properties are ignored
            parameters.setEncryptFiles(true);
            
            // Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(EncryptionMethod.AES);
            
            // Set AES Key strength. Key strengths available for AES encryption are:
            // AES_STRENGTH_128 - For both encryption and decryption
            // AES_STRENGTH_192 - For decryption only
            // AES_STRENGTH_256 - For both encryption and decryption
            // Key strength 192 cannot be used for encryption. But if a zip file already has a
            // file encrypted with key strength of 192, then Zip4j can decrypt this file
            parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        }
        
        return parameters;
    }
    
    /**
     * Implements a runnable thread to zip a given list of files and sends them via email.
     * 
     * @param files The files to zip and email.
     * @param emailAddresses The list of email recipients.
     * @param subject The email subject.
     * @param body The email body.
     * @param zipPassword A password for the zip file. If this is null or empty, the zip file will
     *            not be password protected.
     * @param zipFilename The name of zip file to create. The will be appended with a UUID to
     *            prevent possible duplicates.
     * @param fileSearchTime The amount of time (in seconds) to spend looking for each file to zip.
     */
    public static void zipAndEmailFiles(final File[] files, final String[] emailAddresses, final String subject,
                                        final String body, final String zipPassword, final String zipFilename,
                                        final int fileSearchTime) {
        Runnable emailRunnable = () -> {
        	try{
        		executeZipAndEmailFiles(files, emailAddresses, subject, body, zipPassword, zipFilename, fileSearchTime);
        	} catch (Exception e) {
                log.error(e.getMessage());
                log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
                Thread.currentThread().interrupt();
            }
        };
        	
        Daemon.runInDaemonThread(emailRunnable, Util.getDaemonToken());
    }
    
    
    
    /**
     * Zips a given list of files and sends them via email.
     * Recommend to execute in a Daemon thread.
     * @see ZipUtil#zipAndEmailFiles(File[], String[], String, String, String, String, int)
     * @param files
     * @param emailAddresses
     * @param subject
     * @param body
     * @param zipPassword
     * @param zipFilename
     * @param fileSearchTime
     * @throws MessageException 
     * @throws Exception 
     * 
     **/
	public static void executeZipAndEmailFiles(final File[] files, final String[] emailAddresses, final String subject,
	        final String body, final String zipPassword, final String zipFilename, final int fileSearchTime)
	        throws IOException, MessageException {

		List<File> filesToZip = createFileList(files, fileSearchTime);
		if (filesToZip.isEmpty()) {
			return;
		}

		File targetZipFile = null;
		try {
			// The zip utility does not work when creating a file using
			// File.createTempFile(name, extension). A file with
			// the specified name cannot already exist or it will fail.
			File baseDir = new File(System.getProperty("java.io.tmpdir"));
			String extension = ".zip";
			String newzipFilename = zipFilename + "_" + UUID.randomUUID();
			targetZipFile = new File(baseDir, newzipFilename + extension);
			while (targetZipFile.exists()) {
				newzipFilename = zipFilename + "_" + UUID.randomUUID();
				targetZipFile = new File(baseDir, newzipFilename + extension);
			}

			try {
				if (!StringUtils.isBlank(zipPassword)) {
					zipFilesWithPassword(targetZipFile, filesToZip, zipPassword);
				} else {
					zipFiles(targetZipFile, filesToZip);
				}

			} catch (net.lingala.zip4j.exception.ZipException e) {
				log.error("Error extracting file from encrypted zip {}", targetZipFile, e);
				throw new ZipException(e.getMessage());
			}

			String addresses = String.join(ChirdlUtilConstants.GENERAL_INFO_COMMA, emailAddresses);
			String emailFrom = Context.getAdministrationService()
			        .getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MAIL_FROM);

			MessageService messageService = Context.getMessageService();

			Message message = messageService.createMessage(addresses, emailFrom, subject, body, newzipFilename,
			        ChirdlUtilConstants.MIME_TYPE_ZIP, targetZipFile.getAbsolutePath());
			messageService.sendMessage(message);

		} finally {
			if (targetZipFile != null && targetZipFile.exists() && !targetZipFile.delete()) {
				log.error("Unable to delete file: {}", targetZipFile.getAbsolutePath());
			}
		}
	}

    
    /**
     * Searches for files to zip and adds to a list if found.
     * @param files Array of files to add to to list
     * @param fileSearchTime Number of times to retry search for files to zip.
     * @return List of files to zip. Returns an empty list if no files were found.
     */
    private static List<File> createFileList(final File[] files, final int fileSearchTime){
    	
        ArrayList<File> filesToZip = new ArrayList<>();
         
        int maxTime = -1;
        for (File file : files) {
            while (!file.exists() && maxTime < fileSearchTime) {
                maxTime++;
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    log.error("Interrupted thread error", e);
                    Thread.currentThread().interrupt();
                }
            }
            if (!file.exists()) {
                log.error("Cannot find the file to zip and email: {}", file.getAbsolutePath());
                return filesToZip;
            }
            
            filesToZip.add(file);
            
        }
        
        return filesToZip;
        
    }
}
