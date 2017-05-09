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
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;

/**
 * Utility class for handling zip files.
 * 
 * @author Steve McKee
 */
public class ZipUtil {
	
	private static Log log = LogFactory.getLog(ZipUtil.class);
	
	public static final String NOTIFICATION_MAIL_SENDER = "chica.notification@iu.edu";
	
	/**
	 * Creates a zip file containing all the provided files.
	 * 
	 * @param destinationZipFile The location of the new zip file.
	 * @param filesToAdd The files to add to the zip file.
	 * @throws ZipException
	 */
	public static void zipFiles(File destinationZipFile, ArrayList<File> filesToAdd) throws ZipException {
		ZipParameters parameters = generateParameters(false, null);
		ZipFile zipFile;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zipFile = new ZipFile(destinationZipFile);
			
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
	 * @throws ZipException
	 */
	public static void zipFilesWithPassword(File destinationZipFile, ArrayList<File> filesToAdd, String encryptionPassword)
	                                                                                                                       throws ZipException {
		ZipParameters parameters = generateParameters(true, encryptionPassword);
		ZipFile zipFile;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zipFile = new ZipFile(destinationZipFile);
			
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
	 * @throws ZipException
	 */
	public static void zipFolder(File destinationZipFile, File folder) throws ZipException {
		ZipParameters parameters = generateParameters(false, null);
		ZipFile zipFile;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zipFile = new ZipFile(destinationZipFile);
			
			// Now add files to the zip file
			// Note: To add a single file, the method addFile can be used
			// Note: If the zip file already exists and if this zip file is a split file
			// then this method throws an exception as Zip Format Specification does not 
			// allow updating split zip files
			zipFile.addFolder(folder, parameters);
		}
		catch (net.lingala.zip4j.exception.ZipException e) {
			log.error("Error zipping folder", e);
			throw new ZipException(e.getMessage());
		}
	}
	
	/**
	 * Creates a password protected zip file containing the provided folder.
	 * 
	 * @param destinationZipFile The location of the new zip file.
	 * @param folder The folder to add to the zip file.
	 * @param encryptionPassword The password for the zip file.
	 * @throws ZipException
	 */
	public static void zipFolderWithPassword(File destinationZipFile, File folder, String encryptionPassword)
	                                                                                                         throws ZipException {
		ZipParameters parameters = generateParameters(true, encryptionPassword);
		ZipFile zipFile;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zipFile = new ZipFile(destinationZipFile);
			
			// Now add files to the zip file
			// Note: To add a single file, the method addFile can be used
			// Note: If the zip file already exists and if this zip file is a split file
			// then this method throws an exception as Zip Format Specification does not 
			// allow updating split zip files
			zipFile.addFolder(folder, parameters);
		}
		catch (net.lingala.zip4j.exception.ZipException e) {
			log.error("Error zipping folder with password", e);
			throw new ZipException(e.getMessage());
		}
	}
	
	/**
	 * Extracts all files from a zip file to the provided folder.
	 * 
	 * @param zipFile The file to unzip.
	 * @param destinationFolder The folder where the files will be placed.
	 * @throws ZipException
	 */
	public static void extractAllFiles(File zipFile, File destinationFolder) throws ZipException {
		ZipFile zip;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zip = new ZipFile(zipFile);
			
			// Extract the ZipFile to the specified folder.
			zip.extractAll(destinationFolder.getAbsolutePath());
		}
		catch (net.lingala.zip4j.exception.ZipException e) {
			log.error("Error extracting all files from zip", e);
			throw new ZipException(e.getMessage());
		}
	}
	
	/**
	 * Extracts all files from a password protected zip file to the provided folder.
	 * 
	 * @param zipFile The file to unzip.
	 * @param destinationFolder The folder where the files will be placed.
	 * @param password The password for the zip file.
	 * @throws ZipException
	 */
	public static void extractAllEncryptedFiles(File zipFile, File destinationFolder, String password) throws ZipException {
		ZipFile zip;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zip = new ZipFile(zipFile);
			
			// Set the password
			zip.setPassword(password);
			
			// Extract the ZipFile to the specified folder.
			zip.extractAll(destinationFolder.getAbsolutePath());
		}
		catch (net.lingala.zip4j.exception.ZipException e) {
			log.error("Error extracting all files from encrypted zip", e);
			throw new ZipException(e.getMessage());
		}
	}
	
	/**
	 * Extracts a specific file from a zip file.
	 * 
	 * @param zipFile The zip file containing the file to be extracted.
	 * @param filenameInZip The filename of the file in the zip to be extracted.
	 * @param destinationFolder The folder where the extracted file will be placed.
	 * @throws ZipException
	 */
	public static void extractFile(File zipFile, String filenameInZip, File destinationFolder) throws ZipException {
		ZipFile zip;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zip = new ZipFile(zipFile);
			
			// Extract the ZipFile to the specified folder.
			zip.extractFile(filenameInZip, destinationFolder.getAbsolutePath());
		}
		catch (net.lingala.zip4j.exception.ZipException e) {
			log.error("Error extracting file from zip", e);
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
	 * @throws ZipException
	 */
	public static void extractEncryptedFile(File zipFile, String filenameInZip, File destinationFolder, String password)
	                                                                                                                    throws ZipException {
		ZipFile zip;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zip = new ZipFile(zipFile);
			
			// Set the password
			zip.setPassword(password);
			
			// Extract the ZipFile to the specified folder.
			zip.extractFile(filenameInZip, destinationFolder.getAbsolutePath());
		}
		catch (net.lingala.zip4j.exception.ZipException e) {
			log.error("Error extracting file from encrypted zip", e);
			throw new ZipException(e.getMessage());
		}
	}
	
	/**
	 * Utility method to create zip parameters.
	 * 
	 * @param encrypt Boolean explaining whether or not the encryption flag should be set on the
	 *            parameters.
	 * @param encryptionPassword The password for the encryption.
	 * @return
	 */
	private static ZipParameters generateParameters(boolean encrypt, String encryptionPassword) {
		// Initiate Zip Parameters which define various properties such
		// as compression method, etc. More parameters are explained in other
		// examples
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
		
		// Set the compression level. This value has to be in between 0 to 9
		// Several predefined compression levels are available
		// DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
		// DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
		// DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
		// DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
		// DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		
		if (encrypt) {
			// Set the encryption flag to true
			// If this is set to false, then the rest of encryption properties are ignored
			parameters.setEncryptFiles(true);
			
			// Set the encryption method to AES Zip Encryption
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
			
			// Set AES Key strength. Key strengths available for AES encryption are:
			// AES_STRENGTH_128 - For both encryption and decryption
			// AES_STRENGTH_192 - For decryption only
			// AES_STRENGTH_256 - For both encryption and decryption
			// Key strength 192 cannot be used for encryption. But if a zip file already has a
			// file encrypted with key strength of 192, then Zip4j can decrypt this file
			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			
			// Set password
			parameters.setPassword(encryptionPassword);
		}
		
		return parameters;
	}
	
	/**
	 * Zips a given list of files and sends them via email.
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
		Runnable emailRunnable = new Runnable() {
			
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				Context.openSession();
				try {
					AdministrationService adminService = Context.getAdministrationService();
					Context.authenticate(adminService.getGlobalProperty("scheduler.username"), 
						adminService.getGlobalProperty("scheduler.password"));
					int maxTime = -1;
					ArrayList<File> filesToZip = new ArrayList<File>();
					for (File file : files) {
						while (!file.exists() && maxTime < fileSearchTime) {
							maxTime++;
							try {
								Thread.sleep(1000);
							}
							catch (InterruptedException e) {
								log.error("Interrupted thread error", e);
							}
						}
						if (!file.exists()) {
							log.error("Cannot find the following file to zip and email: " + file.getAbsolutePath());
							return;
						}
						
						filesToZip.add(file);
					}
					
					File targetZipFile = null;
					try {
						// The zip utility does not work when creating a file using File.createTempFile(name, extension).  A file with 
						// the specified name cannot already exist or it will fail.
						File baseDir = new File(System.getProperty("java.io.tmpdir"));
						String extension = ".zip";
						targetZipFile = new File(baseDir, zipFilename + "_" + UUID.randomUUID() + extension);
						while (targetZipFile.exists()) {
							targetZipFile = new File(baseDir, zipFilename + "_" + UUID.randomUUID() + extension);
						}
						
						if (zipPassword != null && zipPassword.trim().length() > 0) {
							zipFilesWithPassword(targetZipFile, filesToZip, zipPassword);
						} else {
							zipFiles(targetZipFile, filesToZip);
						}
						
						File[] attachments = new File[] { targetZipFile };
						
						String smtpMailHost = Context.getAdministrationService()
						        .getGlobalProperty("chirdlutil.smtpMailHost");
						if (smtpMailHost == null) {
							log.error("Please specify global property chirdlutil.smtpMailHost for correct email operability.");
							return;
						}
						
						Properties mailProps = new Properties();
						mailProps.put("mail.smtp.host", smtpMailHost);
						MailSender mailSender = new MailSender(mailProps);
						mailSender.sendMail(NOTIFICATION_MAIL_SENDER, emailAddresses, subject, body, attachments);
					}
					catch (Exception e) {
						log.error("Error zipping and sending email", e);
						return;
					}
					finally {
						if (targetZipFile != null && targetZipFile.exists()) {
							targetZipFile.delete();
						}
					}
				}
				catch (Exception e) {
					log.error(e.getMessage());
					log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				}
				finally {
					Context.flushSession();
					Context.closeSession();
				}
			}
			
		};
		
		Thread emailThread = new Thread(emailRunnable);
		emailThread.start();
	}
}
