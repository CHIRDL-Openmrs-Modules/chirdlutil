package org.openmrs.module.chirdlutil.test.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
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
 * @author davely
 */
public class TestZipUtil extends BaseModuleContextSensitiveTest {
	
	static final String DESTINATION_DIR = "src/test/resources/TestFiles/ZipUtilTest/";
	static final String FILE_NAME_ONE = "FileToZipOne.txt";
	static final String FILE_NAME_ONE_FULL = DESTINATION_DIR + FILE_NAME_ONE;
	static final String FILE_NAME_TWO = "FileToZipTwo.txt";
	static final String FILE_NAME_TWO_FULL = DESTINATION_DIR + FILE_NAME_TWO;
	
	static final String FOLDER_NAME = "FolderToZip";
	static final String FOLDER_PATH = DESTINATION_DIR + FOLDER_NAME + "/";
	
	static final String FILE_NAME_ONE_IN_FOLDER = "/FileInFolderToZipOne.txt";
	static final String FILE_NAME_ONE_IN_FOLDER_FULL =  FOLDER_NAME + FILE_NAME_ONE_IN_FOLDER;
	static final String FILE_NAME_TWO_IN_FOLDER = "/FileInFolderToZipTwo.txt";
	static final String FILE_NAME_TWO_IN_FOLDER_FULL =  FOLDER_NAME + FILE_NAME_TWO_IN_FOLDER;
	
	static final String FILE_NAME_ZIP = "newZipFile.zip";
	static final String FILE_NAME_ZIP_FULL = DESTINATION_DIR + "newZipFile.zip";
	static final String NEW_ZIP_FILE_CONTAINING_FOLDER = "ZipFileWithFolder.zip";
	static final String NEW_ZIP_FILE_CONTAINING_FOLDER_FULL = DESTINATION_DIR + NEW_ZIP_FILE_CONTAINING_FOLDER;
	static final String EXTRACT_FILE_DIRECTORY = DESTINATION_DIR + "temp/";
	static final String ENCRYPTION_PASSWORD = "EncryptionPassword";

	
	/**
	 * @throws Exception
	 * @see ZipUtil#zipFiles( File[], String[], String, String, String, String, int)
	 */
	@Test
	public void zipFiles_shouldZipFilesAndSave() throws Exception
	{
	
		 List<File> filesToZip = this.createFileList();

		 //ChirdUtil method to test
		 ZipUtil.zipFiles(new File(FILE_NAME_ZIP_FULL), filesToZip);
		 
		 //Check if the new zip file exists without using Zip4j api
		 File newFile = new File(FILE_NAME_ZIP_FULL);	
		 
		 //Get File as ZipFile 
		 ZipFile newZipFile = new ZipFile(newFile); 
		 
		 //This try/finally is required to close ZipFiles and delete temporary files and directories after an assert
	     try {
	    	 
	    	 assertTrue(newFile.exists() && !newFile.isDirectory(), 
				 "Zip file " + FILE_NAME_ZIP + " was not created.");
		 		 
	    	//Verify zip file and content is correct 	 
			assertTrue(isZipFileValid(newZipFile), "The zip file  ("
					+ FILE_NAME_ZIP + ") is invalid or content does not contain expected files.");

	      	 
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
	public void zipFilesWithPassword_shouldCreatePasswordProtectedZipFile()  throws Exception {

		List<File> filesToZip = this.createFileList();

		//Method to test
		ZipUtil.zipFilesWithPassword(new File(FILE_NAME_ZIP_FULL), filesToZip, ENCRYPTION_PASSWORD);

		//Get the pre-test zipped file
		File newFile = new File(FILE_NAME_ZIP_FULL);

		//Get File as ZipFile with and without password		 
		ZipFile newZipFileNoPassword = new ZipFile(FILE_NAME_ZIP_FULL);
		ZipFile newZipFileWithPassword = new ZipFile(FILE_NAME_ZIP_FULL, ENCRYPTION_PASSWORD.toCharArray());

		try {

			//Without using Zip4j api, check if a new file exists 	 
			assertTrue(newFile.exists() && !newFile.isDirectory(), 
					"Zip file " + FILE_NAME_ZIP_FULL + " was not created.");

			assertTrue(newZipFileWithPassword.isEncrypted(), 
					"Zip file " + FILE_NAME_ZIP_FULL + "should be encrypted, but it is not encrypted.");	

			//Verify by trying to extract files with and without using password.
			assertThrows(net.lingala.zip4j.exception.ZipException.class, () -> newZipFileNoPassword.extractAll(EXTRACT_FILE_DIRECTORY), 
					"ZipException should have been thrown because no password was used.");
			assertDoesNotThrow( () -> newZipFileWithPassword.extractAll(EXTRACT_FILE_DIRECTORY),
					"Exception should not have been thrown because a password was used.");

			//Verify zip file content
			assertTrue(isZipFileValid(newZipFileWithPassword), "The zip file  ("
					+ FILE_NAME_ZIP + ") is invalid or content does not contain expected files.");

		} finally {

			newZipFileNoPassword.close();
			newZipFileWithPassword.close();
			//File tempDir = new File (EXTRACT_FILE_DIRECTORY);
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
			newFile.delete();
		}



	}
	
	/**
	 * @throws Exception
	 * @see ZipUtil#zipFolder(File, File)
	 */
	@Test
	public void zipFolder_shouldCreateZipFileContainingFolder()  throws Exception {
		
		File destinationZipFile = new File(NEW_ZIP_FILE_CONTAINING_FOLDER_FULL);
		
		//Verify that zip file does not exist yet
		assertTrue(!destinationZipFile.exists(), "Zip file already exists.");
		
		//Method to test
		ZipUtil.zipFolder(destinationZipFile, new File(FOLDER_PATH));
		
		ZipFile zipFileToCheck = new ZipFile(NEW_ZIP_FILE_CONTAINING_FOLDER_FULL);
		
		//This try/finally required to close ZipFiles and delete temporary files and directories after an assert
		try {
			
			//Verify the new zip file exists
			assertTrue(destinationZipFile.exists(), 
					 "Zip file (" + destinationZipFile.getName() + ") was not created.");

			//Verify zip file content
			assertTrue(isZipWithFolderValid(zipFileToCheck), "The zip file with folder ("
					+ NEW_ZIP_FILE_CONTAINING_FOLDER + ") is invalid or content does not contain expected files.");

			
		} finally {
			//cleanup
			zipFileToCheck.close();
			if (destinationZipFile.exists()) {
				assertTrue(destinationZipFile.exists() && destinationZipFile.delete(), "Unable to delete zip file.");		
			}
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}
		
	}
	
	/**
	 * @see ZipUtil#zipFolderWithPassword(File, File, String)
	 * @throws Exception
	 */
	@Test
	public void zipFolderWithPassword_shouldCreateZipWithEncryptedFolder()  throws Exception {

		File destinationZipFile = new File(NEW_ZIP_FILE_CONTAINING_FOLDER_FULL);

		ZipUtil.zipFolderWithPassword(destinationZipFile, new File(FOLDER_PATH), ENCRYPTION_PASSWORD);

		assertTrue(destinationZipFile.exists(), 
				"Zip file (" + destinationZipFile.getName() + ") was not created.");

		//Get ZipFile with and without password
		ZipFile newZipFileNotUsingPassword = new ZipFile(destinationZipFile);
		ZipFile newZipFileUsingPassword = new ZipFile(destinationZipFile, ENCRYPTION_PASSWORD.toCharArray());

		try {

			assertTrue(newZipFileUsingPassword.isEncrypted(), 
					"Zip file (" + NEW_ZIP_FILE_CONTAINING_FOLDER 
					+ ") should be encrypted, but it is not encrypted.");	

			//Verify an exception when trying to extract files without using password.
			assertThrows(net.lingala.zip4j.exception.ZipException.class, () -> newZipFileNotUsingPassword.extractAll(EXTRACT_FILE_DIRECTORY), 
					"Expected ZipException for extracting with no password.");
			
			//Verify no exception when trying to extract file using a password
			assertDoesNotThrow( () -> newZipFileUsingPassword.extractAll(EXTRACT_FILE_DIRECTORY),
					 "Exception should not have been thrown because a password was used.");

			//Verify zip file with folder content.
			assertTrue(isZipWithFolderValid(newZipFileUsingPassword), "The zip file with folder ("
			+ NEW_ZIP_FILE_CONTAINING_FOLDER + ") is not a valid zipfile,  or content does not contain expected files.");
			
		} finally {
			newZipFileNotUsingPassword.close();
			newZipFileUsingPassword.close();
			destinationZipFile.delete();
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}

	}
	/**
	 * @see ZipUtil#extractAllFiles(File, File)
	 * @throws Exception
	 */
	@Test
	public void extractAllFiles_shouldExtractAllFilesToFolder()  throws Exception {

		//Pre-test create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFiles(new File(FILE_NAME_ZIP_FULL), filesToZip);

		File newFile = new File(FILE_NAME_ZIP_FULL);

		try {
            //Verify that the file to unzip exists
			assertTrue(newFile.exists(), 
					"Zip file " + newFile.getName() + " was not created prior to test.");

			//ZipUtil method to test
			ZipUtil.extractAllFiles(newFile, new File(EXTRACT_FILE_DIRECTORY));

			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();
			
			int fileCount = 0;
			//Verify that all
			for (File file: files) {
				
				assertTrue(!file.isDirectory() && ( file.getName().equals(FILE_NAME_ONE)   || 
						file.getName().equals(FILE_NAME_TWO)), "Expected files (" + FILE_NAME_ONE + "," + 
								FILE_NAME_TWO + ") where not extracted.");
				fileCount++;
			}
			 
			assertEquals(2, fileCount, "Incorrect number of files extracted from zip file "  
			+ FILE_NAME_ZIP + ".");
			
		}finally {
			newFile.delete();
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}


	}
	
	/**
	 * @see ZipUtil#extractAllEncryptedFiles(File, File, String)
	 * @throws Exception
	 */
	@Test
	public void extractAllEncryptedFiles_shouldExtractAllEncryptedFilesToFolder()  throws Exception {

		//Pre-test create zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFilesWithPassword(new File(FILE_NAME_ZIP_FULL), filesToZip, ENCRYPTION_PASSWORD);

		File testFile = new File(FILE_NAME_ZIP_FULL);	

		try {
			
			assertTrue(testFile.exists(), 
					"Zip file " + testFile.getName() + " was not created prior to extracting all encrypted files test.");

			//ZipUtil method to test
			ZipUtil.extractAllEncryptedFiles(new File(FILE_NAME_ZIP_FULL), 
					new File(EXTRACT_FILE_DIRECTORY), ENCRYPTION_PASSWORD);


			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();
			
			for (File file: files) {
				assertTrue(!file.isDirectory() && ( file.getName().equals(FILE_NAME_ONE)   || 
						file.getName().equals(FILE_NAME_TWO)), "Expected files (" + FILE_NAME_ONE + "," + 
								FILE_NAME_TWO + ") where not extracted.");
			}
			
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
			
			assertThrows(java.util.zip.ZipException.class, () -> ZipUtil.extractAllEncryptedFiles(new File(FILE_NAME_ZIP), 
					new File(EXTRACT_FILE_DIRECTORY), "thisisinvalidpassword"), 
					"ZipException should be thrown for wrong password.");
			
		}finally {
			testFile.delete();
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));		
		}

	}
	
	/**
	 * @see ZipUtil#extractFile(File, String, File)
	 * @throws Exception
	 */
	@Test
	public void extractFile_shouldExtractSpecificFile()  throws Exception {
		
		//Create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFiles(new File(FILE_NAME_ZIP_FULL), filesToZip);

		File testFile = new File(FILE_NAME_ZIP_FULL);	
		
		try {
			assertTrue(testFile.exists(), 
					"Zip file " + testFile.getName() + " was not created prior to test.");
			//Method to test
			ZipUtil.extractFile(new File(FILE_NAME_ZIP_FULL), FILE_NAME_ONE, 
					new File(EXTRACT_FILE_DIRECTORY));
			
			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			for (File file: files) {
				assertTrue(!file.isDirectory() &&  file.getName().equals(FILE_NAME_ONE), 
						"Expected file (" + FILE_NAME_ONE + ") was not extracted.");
			}
			

		}finally {
			testFile.delete();
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}
	}
	
	/**
	 * @see ZipUtil#extractEncryptedFile(File, String, File, String)
	 * @throws Exception
	 */
	@Test
	public void extractEncryptedFile_shouldExtractSpecificEncryptedFile()  throws Exception {
		

		List<File> filesToZip = this.createFileList();
		
		//Pre-test create zip file
		ZipUtil.zipFilesWithPassword(new File(FILE_NAME_ZIP_FULL), filesToZip, ENCRYPTION_PASSWORD);

		//Get the pre-test zipped file
		File testFile = new File(FILE_NAME_ZIP_FULL);	
		
		try {
				
			//Verify pre-test zipped file exists
			assertTrue(testFile.exists(), 
					"Pre-test zip file " + testFile.getName() + " was not created prior to test.");
			
			//ZipUtil method to test
			ZipUtil.extractEncryptedFile(new File(FILE_NAME_ZIP_FULL), FILE_NAME_ONE, 
					new File(EXTRACT_FILE_DIRECTORY), ENCRYPTION_PASSWORD);
			
			//Get the directory that contains the extracted file
			File extractedDir = new File(EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();
	
			for (File file: files) {
				
				//Verify the correct file was extracted. 
				assertTrue(!file.isDirectory() &&  file.getName().equals(FILE_NAME_ONE), 
						"Expected file (" + FILE_NAME_ONE + ") was not extracted.");
			}
			
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
			
			//Test extraction with the wrong password.
			assertThrows(java.util.zip.ZipException.class, () -> ZipUtil.extractEncryptedFile(new File(FILE_NAME_ZIP_FULL), FILE_NAME_ONE, 
					new File(EXTRACT_FILE_DIRECTORY), "thisisthewrongpassword"), 
					"ZipException should be thrown for wrong password.");

		}finally {
			if (testFile.exists()) {
				testFile.delete();
			}
			
			this.deleteDirectory(new File(EXTRACT_FILE_DIRECTORY));
		}
	}
	
	/**
	 * @see ZipUtil#zipAndEmailFiles( File[], String[], String, String, String, String, int)
	 * @throws Exception
	 **/
	@Test
	public void executeZipAndEmailFiles_shouldZipFilesAndSendEmails()  throws Exception {
		
		
		String[] invalidEmailAddresses = new String[] {"123.example.com", "456.example.com", "789.example.com"};
		File[] filesToZip = new File[] {new File(FILE_NAME_ONE_FULL), new File(FILE_NAME_TWO_FULL)};
		String subject = "Test zip email delivery";
		String body = "This email tests zipped file email delivery";
		
		Context.getAdministrationService()
			.setGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MAIL_FROM, "msheley@iu.edu");

		try {
		    ZipUtil.executeZipAndEmailFiles(filesToZip, invalidEmailAddresses, 
				subject, body,  ENCRYPTION_PASSWORD, FILE_NAME_ZIP, 5);
	    }catch (Exception e) {	

	    	//Expect a MessageException for this test, but fail if any other exception was thrown.
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
	 * Verifies that this file is a zip file and that it contains the expected files
	 * @param zipFile
	 * @throws Exception
	 */
	private void checkZip (ZipFile zipFile) throws Exception {
	
		Assertions.assertTrue(zipFile.isValidZipFile(), 
	    		 "File " + FILE_NAME_ZIP + " is not a valid zip file.");

		List<FileHeader> fileHeaders = zipFile.getFileHeaders();

		for (FileHeader fileHeader: fileHeaders) {		
			 Assertions.assertTrue(fileHeader.getFileName().equals(FILE_NAME_ONE) ||
					fileHeader.getFileName().equals(FILE_NAME_TWO));			 
		}						
		 
		return;		
	}

	
	/**
	 * Checks that a file is a valid zip file and contains expected content.
	 * 
	 * @param zipFile is zip file to
	 * @throws ZipException
	 */
	private boolean isZipWithFolderValid(ZipFile zipFile)    {
		
	    List<FileHeader> fileHeaders;
	    if (!zipFile.isValidZipFile()) {
	    	return false;
	    }
	    
		try {
			fileHeaders = zipFile.getFileHeaders();
		} catch (ZipException e) {
			return false;
		}
	
	   if (fileHeaders.isEmpty()){
		   return false;
	   }

		for (FileHeader fileHeader : fileHeaders) {
			
			if (!fileHeader.getFileName().equals(FOLDER_NAME + "/")
            && !fileHeader.getFileName().equals(FOLDER_NAME + FILE_NAME_ONE_IN_FOLDER)
            && !fileHeader.getFileName().equals(FOLDER_NAME + FILE_NAME_TWO_IN_FOLDER)) {
				return false;
			}
		
		}
		return true;

		
	}
	
	/**
	 * Checks that a file is a valid zip file and contains expected content.
	 * 
	 * @param zipFile is zip file to
	 * @throws ZipException
	 */
	private boolean isZipFileValid(ZipFile zipFile)    {
		
				
	    List<FileHeader> fileHeaders;
	    if (!zipFile.isValidZipFile()) {
	    	return false;
	    }
	    
		try {
			fileHeaders = zipFile.getFileHeaders();
		} catch (ZipException e) {
			return false;
		}
	
	   if (fileHeaders.isEmpty()){
		   return false;
	   }

		for (FileHeader fileHeader : fileHeaders) {
			
			if (!fileHeader.getFileName().equals(FILE_NAME_ONE) &&
					!fileHeader.getFileName().equals(FILE_NAME_TWO)) {
				return false;
			}
		
		}
		return true;

		
	}
	
	
	/**
	 * Recursive method to delete a directory and all its files and sub-directories.
	 * @param directory the directory to be deleted
	 * @return true if the file or directory is successfully deleted
	 */
	private boolean deleteDirectory(File directory) {
	    File[] contents = directory.listFiles();
	    if (contents != null) {
	        for (File file : contents) {
	            deleteDirectory(file);
	        }   
	    }
	    return directory.delete();
	}
	
}
