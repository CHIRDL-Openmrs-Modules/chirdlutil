package org.openmrs.module.chirdlutil.test.util;

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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.ZipUtil;
import org.openmrs.notification.MessageException;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

/**
 * Tests the utility class for handling zip files.
 * @see org.openmrs.module.chirdlutil.util.ZipUtil
 * @author Meena Sheley
 */

public class TestZipUtil extends BaseModuleContextSensitiveTest {
	
	static final String DESTINATION_DIR = "src/test/resources/TestFiles/ZipUtilTest/";
	static final String FILE_NAME_ONE = "FileToZipOne.txt";
	static final String FILE_NAME_ONE_FULL = DESTINATION_DIR + FILE_NAME_ONE;
	static final String FILE_NAME_TWO = "FileToZipTwo.txt";
	static final String FILE_NAME_TWO_FULL = DESTINATION_DIR + FILE_NAME_TWO;
	
	static final String FOLDER_NAME = "FolderToZip/";
	static final String FOLDER_PATH = DESTINATION_DIR + FOLDER_NAME;
	
	static final String FILE_NAME_ONE_FOR_FOLDER = "FileInFolderToZipOne.txt";
	static final String FILE_NAME_ONE_FOR_FOLDER_FULL =  FOLDER_NAME + FILE_NAME_ONE_FOR_FOLDER;
	static final String FILE_NAME_TWO_FOR_FOLDER = "FileInFolderToZipTwo.txt";
	static final String FILE_NAME_TWO_FOR_FOLDER_FULL =  FOLDER_NAME + FILE_NAME_TWO_FOR_FOLDER;
	
	static final String FILE_NAME_ZIP = "newZipFile.zip";
	static final String FILE_NAME_ZIP_FULL = DESTINATION_DIR + "newZipFile.zip";
	static final String NEW_ZIP_FILE_CONTAINING_FOLDER = "ZipFileWithFolder.zip";
	static final String NEW_ZIP_FILE_CONTAINING_FOLDER_FULL = DESTINATION_DIR + NEW_ZIP_FILE_CONTAINING_FOLDER;
	static final String EXTRACT_FILE_DIRECTORY = DESTINATION_DIR + "temp/";
	static final String ENCRYPTION_PASSWORD = "EncrYpT1onP@ssw0rd";

	
	/**
	 * @throws Exception
	 * @see ZipUtil#zipFiles( File[], String[], String, String, String, String, int)
	 */
	@Test
	public void zipFiles_shouldZipFilesAndSave() throws Exception {

		List<File> filesToZip = this.createFileList();
		
		File destinationZipFile = new File(FILE_NAME_ZIP_FULL);
		
		// Verify that zip file does NOT exist yet
		if (destinationZipFile.exists()) {
			destinationZipFile.delete();
		}

		// ChirdUtil method to test
		ZipUtil.zipFiles(new File(FILE_NAME_ZIP_FULL), filesToZip);

		// Get the new zip file as File
		File newFile = new File(FILE_NAME_ZIP_FULL);

		// Get the new zip file as ZipFile (Zip4j)
		ZipFile newZipFile = new ZipFile(newFile);

		try {

			assertTrue(newFile.exists() && !newFile.isDirectory(), "Zip file " + FILE_NAME_ZIP_FULL + " was not created.");

			// Verify zip file and content is correct by reading zip headers
			assertTrue(this.isZipValid(newZipFile, this.createFileNamesList()),
			        "The zip file  (" + FILE_NAME_ZIP_FULL + ") is invalid or content does not contain expected files.");

		} finally {
			newZipFile.close();
			newFile.delete();
		}

	}
	
	/**
	 * @throws Exception
	 * @see ZipUtil#zipFilesWithPassword(File, File, String)
	 */
	@Test
	public void zipFilesWithPassword_shouldCreatePasswordProtectedZipFile() throws Exception {

		List<File> filesToZip = this.createFileList();
		
		File destinationZipFile = new File(FILE_NAME_ZIP_FULL);
		
		// Verify that zip file does NOT exist yet
		if (destinationZipFile.exists()) {
			destinationZipFile.delete();
		}

		// Method to test
		ZipUtil.zipFilesWithPassword(destinationZipFile, filesToZip, ENCRYPTION_PASSWORD);

		// Get file as File
		File newFile = new File(FILE_NAME_ZIP_FULL);

		// Get file as ZipFile with and without password
		ZipFile newZipFileNoPassword = new ZipFile(FILE_NAME_ZIP_FULL);
		ZipFile newZipFileWithPassword = new ZipFile(FILE_NAME_ZIP_FULL, ENCRYPTION_PASSWORD.toCharArray());

		try {

			// Without using Zip4j api, check if a new file exists
			assertTrue(newFile.exists() && !newFile.isDirectory(),
			        "Zip file " + FILE_NAME_ZIP_FULL + " was not created.");

			assertTrue(newZipFileWithPassword.isEncrypted(),
			        "Zip file " + FILE_NAME_ZIP_FULL + "should be encrypted, but it is not encrypted.");

			// Verify encryption by trying to extract files with and without using password.
			assertThrows(net.lingala.zip4j.exception.ZipException.class,
			        () -> newZipFileNoPassword.extractAll(EXTRACT_FILE_DIRECTORY),
			        "ZipException should have been thrown because no password was used.");
			assertDoesNotThrow(() -> newZipFileWithPassword.extractAll(EXTRACT_FILE_DIRECTORY),
			        "Exception should not have been thrown because a password was used.");

			// Verify zip file and content is correct by reading zip headers
			assertTrue(this.isZipValid(newZipFileWithPassword, this.createFileNamesList()),
			        "The zip file  (" + FILE_NAME_ZIP_FULL + ") is invalid or content does not contain expected files.");

		} finally {

			newZipFileNoPassword.close();
			newZipFileWithPassword.close();
			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
			newFile.delete();
		}

	}

	/**
	 * @throws Exception
	 * @see ZipUtil#zipFolder(File, File)
	 */
	@Test
	public void zipFolder_shouldCreateZipFileContainingFolder() throws Exception {

		File destinationZipFile = new File(NEW_ZIP_FILE_CONTAINING_FOLDER_FULL);

		// Verify that zip file does NOT exist yet
		if (destinationZipFile.exists()) {
			destinationZipFile.delete();
		}

		// Method to test
		ZipUtil.zipFolder(destinationZipFile, new File(FOLDER_PATH));

		ZipFile zipFileToCheck = new ZipFile(NEW_ZIP_FILE_CONTAINING_FOLDER_FULL);

		try {

			// Verify the new zip file exists
			assertTrue(destinationZipFile.exists(), "Zip file (" + destinationZipFile.getAbsolutePath() + ") was not created.");

			// Verify zip file content
			assertTrue(isZipValid(zipFileToCheck, this.createFileFolderNamesList()), "The zip file with folder ("
			        + NEW_ZIP_FILE_CONTAINING_FOLDER_FULL + ") is invalid or content does not contain expected files.");

		} finally {
			zipFileToCheck.close();
			if (destinationZipFile.exists()) {
				assertTrue(destinationZipFile.exists() && destinationZipFile.delete(), "Unable to delete zip file "
						+ destinationZipFile.getAbsolutePath() + ".");
			}
			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}

	}
	
	/**
	 * @see ZipUtil#zipFolderWithPassword(File, File, String)
	 * @throws Exception
	 */
	@Test
	public void zipFolderWithPassword_shouldCreateZipWithEncryptedFolder() throws Exception {

		File destinationZipFile = new File(NEW_ZIP_FILE_CONTAINING_FOLDER_FULL);

		ZipUtil.zipFolderWithPassword(destinationZipFile, new File(FOLDER_PATH), ENCRYPTION_PASSWORD);

		assertTrue(destinationZipFile.exists(), "Zip file (" + destinationZipFile.getAbsolutePath() + ") was not created.");

		// Get ZipFile with and without password
		ZipFile newZipFileNotUsingPassword = new ZipFile(destinationZipFile);
		ZipFile newZipFileUsingPassword = new ZipFile(destinationZipFile, ENCRYPTION_PASSWORD.toCharArray());

		try {

			assertTrue(newZipFileUsingPassword.isEncrypted(),
			        "Zip file (" + NEW_ZIP_FILE_CONTAINING_FOLDER_FULL + ") should be encrypted, but it is not encrypted.");

			// Verify an exception when trying to extract files without using password.
			assertThrows(net.lingala.zip4j.exception.ZipException.class,
			        () -> newZipFileNotUsingPassword.extractAll(EXTRACT_FILE_DIRECTORY),
			        "Expected ZipException for extracting with no password.");

			// Verify no exception when trying to extract file using a password
			assertDoesNotThrow(() -> newZipFileUsingPassword.extractAll(EXTRACT_FILE_DIRECTORY),
			        "Exception should not have been thrown because a password was used.");

			// Verify zip file with folder content.
			assertTrue(isZipValid(newZipFileUsingPassword, this.createFileFolderNamesList()),
			        "The zip file with folder (" + NEW_ZIP_FILE_CONTAINING_FOLDER_FULL
			                + ") is not a valid zipfile,  or content does not contain expected files.");

		} finally {
			newZipFileNotUsingPassword.close();
			newZipFileUsingPassword.close();
			destinationZipFile.delete();
			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}

	}
	
	/**
	 * @see ZipUtil#extractAllFiles(File, File)
	 * @throws Exception
	 */
	@Test
	public void extractAllFiles_shouldExtractAllFilesToFolder() throws Exception {

		// Pre-test create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFiles(new File(FILE_NAME_ZIP_FULL), filesToZip);

		File newFile = new File(FILE_NAME_ZIP_FULL);

		try {
			// Verify that the file to unzip exists
			assertTrue(newFile.exists(), "Zip file " + newFile.getAbsolutePath() + " was not created prior to test.");

			// ZipUtil method to test
			ZipUtil.extractAllFiles(newFile, new File(EXTRACT_FILE_DIRECTORY));

			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			int fileCount = 0;
			// Verify that all files were zipped
			for (File file : files) {

				assertTrue(!file.isDirectory() &&
						(file.getName().equals(FILE_NAME_ONE) || file.getName().equals(FILE_NAME_TWO)),
						"Expected files (" + FILE_NAME_ONE + "," + FILE_NAME_TWO + ") where not extracted.");
				fileCount++;
			}

			assertEquals(files.length, fileCount, "Incorrect number of files extracted from zip file " 
					+ FILE_NAME_ZIP_FULL + ".");

		} finally {
			newFile.delete();
			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}

	}
	
	/**
	 * @see ZipUtil#extractAllEncryptedFiles(File, File, String)
	 * @throws Exception
	 */
	@Test
	public void extractAllEncryptedFiles_shouldExtractAllEncryptedFilesToFolder() throws Exception {

		// Pre-test create zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFilesWithPassword(new File(FILE_NAME_ZIP_FULL), filesToZip, ENCRYPTION_PASSWORD);

		File testFile = new File(FILE_NAME_ZIP_FULL);

		try {

			assertTrue(testFile.exists(), "Zip file " + testFile.getAbsolutePath()
			        + " was not created prior to extracting all encrypted files test.");

			// ZipUtil method to test
			ZipUtil.extractAllEncryptedFiles(new File(FILE_NAME_ZIP_FULL), new File(EXTRACT_FILE_DIRECTORY),
			        ENCRYPTION_PASSWORD);

			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			for (File file : files) {
				assertTrue(
				        !file.isDirectory()
				                && (file.getName().equals(FILE_NAME_ONE) || file.getName().equals(FILE_NAME_TWO)),
				        "Expected files (" + FILE_NAME_ONE + "," + FILE_NAME_TWO + ") where not extracted.");
			}

			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));

			assertThrows(
			        java.util.zip.ZipException.class, () -> ZipUtil.extractAllEncryptedFiles(new File(FILE_NAME_ZIP),
			                new File(EXTRACT_FILE_DIRECTORY), "thisisinvalidpassword"),
			        "ZipException should be thrown for wrong password.");

		} finally {
			testFile.delete();
			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}

	}
	
	/**
	 * @see ZipUtil#extractFile(File, String, File)
	 * @throws Exception
	 */
	@Test
	public void extractFile_shouldExtractSpecificFile() throws Exception {

		// Create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFiles(new File(FILE_NAME_ZIP_FULL), filesToZip);

		File testFile = new File(FILE_NAME_ZIP_FULL);

		try {
			assertTrue(testFile.exists(), "Zip file " + testFile.getAbsolutePath() + " was not created prior to test.");
			// Method to test
			ZipUtil.extractFile(new File(FILE_NAME_ZIP_FULL), FILE_NAME_ONE, new File(EXTRACT_FILE_DIRECTORY));

			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			for (File file : files) {
				assertTrue(!file.isDirectory() && file.getName().equals(FILE_NAME_ONE),
				        "Expected file (" + FILE_NAME_ONE + ") was not extracted.");
			}

		} finally {
			testFile.delete();
			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}
	}
	
	/**
	 * @see ZipUtil#extractEncryptedFile(File, String, File, String)
	 * @throws Exception
	 */
	@Test
	public void extractEncryptedFile_shouldExtractSpecificEncryptedFile() throws Exception {

		List<File> filesToZip = this.createFileList();

		// Pre-test create zip file
		ZipUtil.zipFilesWithPassword(new File(FILE_NAME_ZIP_FULL), filesToZip, ENCRYPTION_PASSWORD);

		// Get the pre-test zipped file
		File testFile = new File(FILE_NAME_ZIP_FULL);

		try {

			// Verify pre-test zipped file exists
			assertTrue(testFile.exists(),
			        "Pre-test zip file " + testFile.getAbsolutePath() + " was not created prior to test.");

			// ZipUtil method to test
			ZipUtil.extractEncryptedFile(new File(FILE_NAME_ZIP_FULL), FILE_NAME_ONE, new File(EXTRACT_FILE_DIRECTORY),
			        ENCRYPTION_PASSWORD);

			// Get the directory that contains the extracted file
			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			for (File file : files) {

				// Verify the correct file was extracted.
				assertTrue(!file.isDirectory() && file.getName().equals(FILE_NAME_ONE),
				        "Expected file (" + FILE_NAME_ONE + ") was not extracted.");
			}

			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));

			// Test extraction with the wrong password.
			assertThrows(java.util.zip.ZipException.class,
			        () -> ZipUtil.extractEncryptedFile(new File(FILE_NAME_ZIP_FULL), FILE_NAME_ONE,
			                new File(EXTRACT_FILE_DIRECTORY), "thisisthewrongpassword"),
			        "ZipException should be thrown for wrong password.");

		} finally {
			if (testFile.exists()) {
				testFile.delete();
			}

			Util.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}
	}
	
	/**
	 * @see ZipUtil#zipAndEmailFiles( File[], String[], String, String, String, String, int)
	 * @throws Exception
	 **/
	@Test
	public void executeZipAndEmailFiles_shouldZipFilesAndSendEmails() throws Exception {

		String[] invalidEmailAddresses = new String[] { "123.example.com", "456.example.com", "789.example.com" };
		File[] filesToZip = new File[] { new File(FILE_NAME_ONE_FULL), new File(FILE_NAME_TWO_FULL) };
		String subject = "Test email subject";
		String body = "Test email body.";

		Context.getAdministrationService().setGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MAIL_FROM,
		        "test@example.com");

		try {
			ZipUtil.executeZipAndEmailFiles(filesToZip, invalidEmailAddresses, subject, body, ENCRYPTION_PASSWORD,
			        FILE_NAME_ZIP, 5);
		} catch (Exception e) {

			// Expect a MessageException for this test, but fail if any other exception was
			// thrown.
			assertTrue((e instanceof MessageException), "Expected Message Exception, but was " + Util.getStackTrace(e));
		}

	}
	
	
	/**
	 * Creates a list of test files to be zipped
	 *
	 * @return the list of test files
	 */
	private List<File> createFileList() {

		File testFileOne = new File(FILE_NAME_ONE_FULL);
		File testFileTwo = new File(FILE_NAME_TWO_FULL);

		ArrayList<File> files = new ArrayList<>();
		files.add(testFileOne);
		files.add(testFileTwo);
		return files;

	}
	
	
	/**
	 * Creates a list of expected test folder and file names
	 *
	 * @return the list of test file names
	 */
	private List<String> createFileFolderNamesList() {

		String fileNameOne = (TestZipUtil.FOLDER_NAME + TestZipUtil.FILE_NAME_ONE_FOR_FOLDER).replace("//", "\\");
		String fileNameTwo = (TestZipUtil.FOLDER_NAME + TestZipUtil.FILE_NAME_TWO_FOR_FOLDER).replace("//", "\\");
		String folder = (TestZipUtil.FOLDER_NAME).replace("//", "\\");

		List<String> names = new ArrayList<>();

		names.add(fileNameOne);
		names.add(fileNameTwo);
		names.add(folder);
		return names;

	}
	
	/**
	 * Creates is list of expected test file names
	 * @return
	 */
	private List<String> createFileNamesList() {

		String fileNameOne = (TestZipUtil.FILE_NAME_ONE).replace("//", "\\");
		String fileNameTwo = (TestZipUtil.FILE_NAME_TWO).replace("//", "\\");

		List<String> names = new ArrayList<>();
		names.add(fileNameOne);
		names.add(fileNameTwo);

		return names;

	}
	
	
	/**
	 * Checks that a file is a valid zip file and contains expected content.
	 * 
	 * @param zipFile is zip file to
	 * @param fileNames list of file names expected to be in zip file
	 * 
	 */
	private boolean isZipValid(ZipFile zipFile, List<String> fileNames) {

		// Check if valid zip file by checking zip headers.
		List<FileHeader> fileHeaders = new ArrayList<>();
		if (zipFile == null || !zipFile.isValidZipFile()) {
			return false;
		}

		// Get the file headers to check content
		try {
			fileHeaders = zipFile.getFileHeaders();
		} catch (ZipException e) {
			return false;
		}

		if (fileHeaders.isEmpty()) {
			return false;
		}

		if (fileNames.size() != fileHeaders.size()) {
			return false;
		}

		// Verify the expected files are contained in the zip file.
		int fileMatchCount = 0;
		for (FileHeader fileHeader : fileHeaders) {
			for (String fileName : fileNames) {
				if (fileHeader.getFileName().equals(fileName)) {
					fileMatchCount++;
					break;
				}
			}

		}

		// Verify the file count is correct
		if (fileMatchCount != fileNames.size()) {
			return false;
		}

		return true;

	}
	
}
