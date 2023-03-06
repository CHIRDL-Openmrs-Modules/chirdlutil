package org.openmrs.module.chirdlutil.test.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.chirdlutil.util.ZipUtil;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

/**
 * @author davely
 */
public class TestZipUtil extends BaseModuleContextSensitiveTest {
	
	String DESTINATION_DIR = "src/test/resources/TestFiles/ZipUtilTest/";
	String FILE_NAME_ONE = "FileToZipOne.txt";
	String FILE_NAME_ONE_FULL = this.DESTINATION_DIR + this.FILE_NAME_ONE;
	String FILE_NAME_TWO = "FileToZipTwo.txt";
	String FILE_NAME_TWO_FULL = this.DESTINATION_DIR + this.FILE_NAME_TWO;
	
	String FOLDER_NAME = "FolderToZip";
	String FOLDER_PATH = this.DESTINATION_DIR + this.FOLDER_NAME + "/";
	
	String FILE_NAME_ONE_IN_FOLDER = "/FileInFolderToZipOne.txt";
	String FILE_NAME_ONE_IN_FOLDER_FULL =  this.FOLDER_NAME + this.FILE_NAME_ONE_IN_FOLDER;
	String FILE_NAME_TWO_IN_FOLDER = "/FileInFolderToZipTwo.txt";
	String FILE_NAME_TWO_IN_FOLDER_FULL =  this.FOLDER_NAME + this.FILE_NAME_TWO_IN_FOLDER;
	
	String FILE_NAME_ZIP = this.DESTINATION_DIR + "newZipFile.zip";
	String NEW_ZIP_FILE_WITH_FOLDER = this.DESTINATION_DIR + "ZipFileWithFolder.zip";
	String EXTRACT_FILE_DIRECTORY = this.DESTINATION_DIR + "temp/";
	
	String ENCRYPTION_PASSWORD = "EnCr&ptionP@ssw0rd";

//	File testFileOne;
//	File testFileTwo;
	
	
	@Test
	public void zipFiles_shouldZipFilesAndSave() throws Exception
	{
	
		 List<File> filesToZip = this.createFileList();

		 //ChirdUtil method to test
		 ZipUtil.zipFiles(new File(this.FILE_NAME_ZIP), filesToZip);
		 
		 //Check if the new zip file exists without using Zip4j api
		 File newFile = new File(this.FILE_NAME_ZIP);	
		 
		 //Get File as ZipFile 
		 ZipFile newZipFile = new ZipFile(newFile); 
		 
		 //This try/finally is required to close ZipFiles and delete temporary files and directories after an assert
	     try {
	    	 
	    	 assertTrue(newFile.exists() && !newFile.isDirectory(), 
				 "Zip file " + this.FILE_NAME_ZIP + " was not created.");
		 		 
	    	 //Verify if ZipFile type and content is correct 	 
	      	 checkZip(newZipFile);	
	      	 
	     } finally {
	    	 newZipFile.close();
	    	 newFile.delete();
	     }
	 
	}
	
	@Test
	public void zipFilesWithPassword_shouldCreatePasswordProtectedZipFile()  throws Exception {

		List<File> filesToZip = this.createFileList();

		//Method to test
		ZipUtil.zipFilesWithPassword(new File(this.FILE_NAME_ZIP), filesToZip, this.ENCRYPTION_PASSWORD);

		File newFile = new File(this.FILE_NAME_ZIP);

		//Get File as ZipFile with and without password		 
		ZipFile newZipFileNoPassword = new ZipFile(this.FILE_NAME_ZIP);
		ZipFile newZipFileWithPassword = new ZipFile(this.FILE_NAME_ZIP, this.ENCRYPTION_PASSWORD.toCharArray());

		//This try/finally required to close ZipFiles and delete temporary files and directories after an assert
		try {

			//Without using Zip4j api, check if a new file exists 	 
			assertTrue(newFile.exists() && !newFile.isDirectory(), 
					"Zip file " + this.FILE_NAME_ZIP + " was not created.");

			assertTrue(newZipFileWithPassword.isEncrypted(), 
					"Zip file should be encrypted, but it is not encrypted.");	

			//Verify by trying to extract files with and without using password.
			assertThrows(ZipException.class, () -> newZipFileNoPassword.extractAll(this.EXTRACT_FILE_DIRECTORY), 
					"ZipException should have been thrown because no password was used.");
			assertDoesNotThrow( () -> newZipFileWithPassword.extractAll(this.EXTRACT_FILE_DIRECTORY),
					"Exception should not have been thrown because a password was used.");

			checkZip(newZipFileWithPassword);

		} finally {

			newZipFileNoPassword.close();
			newZipFileWithPassword.close();
			File tempDir = new File (this.EXTRACT_FILE_DIRECTORY);
			tempDir.delete();
			newFile.delete();
		}



	}
	
	
	@Test
	public void zipFolder_shouldCreateZipFileContainingFolder()  throws Exception {
		
		File destinationZipFile = new File(this.NEW_ZIP_FILE_WITH_FOLDER);
		
		//Method to test
		ZipUtil.zipFolder(destinationZipFile, new File(this.FOLDER_PATH));
		
		ZipFile zipFileToCheck = new ZipFile(this.NEW_ZIP_FILE_WITH_FOLDER);
		
		//This try/finally required to close ZipFiles and delete temporary files and directories after an assert
		try {
			
			assertTrue(destinationZipFile.exists(), 
					 "Zip file (" + destinationZipFile.getName() + ") was not created.");

			//Verify zip file content
			checkZipWithFolder(zipFileToCheck);
			
		} finally {
			zipFileToCheck.close();
			destinationZipFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}
		
	}
	
	@Test
	public void zipFolderWithPassword_shouldCreateZipWithEncryptedFolder()  throws Exception {

		File destinationZipFile = new File(this.NEW_ZIP_FILE_WITH_FOLDER);

		ZipUtil.zipFolderWithPassword(destinationZipFile, new File(this.FOLDER_PATH), this.ENCRYPTION_PASSWORD);

		assertTrue(destinationZipFile.exists(), 
				"Zip file " + destinationZipFile.getName() + " was not created.");

		//Get ZipFile with and without password
		ZipFile newZipFileNotUsingPassword = new ZipFile(destinationZipFile);
		ZipFile newZipFileUsingPassword = new ZipFile(destinationZipFile, this.ENCRYPTION_PASSWORD.toCharArray());

		try {

			assertTrue(newZipFileUsingPassword.isEncrypted(), 
					"Zip file should be encrypted, but it is not encrypted.");	

			//Verify by trying to extract files with and without using password.
			assertThrows(ZipException.class, () -> newZipFileNotUsingPassword.extractAll(this.EXTRACT_FILE_DIRECTORY), 
					"ZipException should have been thrown because no password was used.");
			assertDoesNotThrow( () -> newZipFileUsingPassword.extractAll(this.EXTRACT_FILE_DIRECTORY),
					 "Exception should not have been thrown because a password was used.");

			//Verify zip file content
			checkZipWithFolder(newZipFileUsingPassword);

		} finally {
			newZipFileNotUsingPassword.close();
			newZipFileUsingPassword.close();
			File tempDir = new File (this.DESTINATION_DIR);
			tempDir.delete();
			destinationZipFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}

	}
	
	@Test
	public void extractAllFiles_shouldExtractAllFilesToFolder()  throws Exception {

		//Create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFiles(new File(this.FILE_NAME_ZIP), filesToZip);

		File newFile = new File(this.FILE_NAME_ZIP);

		try {

			assertTrue(newFile.exists(), 
					"Zip file " + newFile.getName() + " was not created prior to test.");

			//method to test
			ZipUtil.extractAllFiles(newFile, new File(this.EXTRACT_FILE_DIRECTORY));

			File extractedDir = new File(this.EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();
			
			for (File file: files) {
				
				assertTrue(!file.isDirectory() && ( file.getName().equals(this.FILE_NAME_ONE)   || 
						file.getName().equals(this.FILE_NAME_TWO)), "Expected files (" + this.FILE_NAME_ONE + "," + 
								this.FILE_NAME_TWO + ") where not extracted.");
			}
			
		}finally {
			newFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}


	}
	
	@Test
	public void extractAllEncryptedFiles_shouldExtractAllEncryptedFilesToFolder()  throws Exception {

		//Create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFilesWithPassword(new File(this.FILE_NAME_ZIP), filesToZip, this.ENCRYPTION_PASSWORD);

		File testFile = new File(this.FILE_NAME_ZIP);	

		try {
			
			assertTrue(testFile.exists(), 
					"Zip file " + testFile.getName() + " was not created prior to extracting all encrypted files test.");

			//Method to test
			ZipUtil.extractAllEncryptedFiles(new File(this.FILE_NAME_ZIP), 
					new File(this.EXTRACT_FILE_DIRECTORY), this.ENCRYPTION_PASSWORD);


			File extractedDir = new File(this.EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			for (File file: files) {
				assertTrue(!file.isDirectory() && ( file.getName().equals(this.FILE_NAME_ONE)   || 
						file.getName().equals(this.FILE_NAME_TWO)), "Expected files (" + this.FILE_NAME_ONE + "," + 
								this.FILE_NAME_TWO + ") where not extracted.");
			}
			
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
			
			assertThrows(java.util.zip.ZipException.class, () -> ZipUtil.extractAllEncryptedFiles(new File(this.FILE_NAME_ZIP), 
					new File(this.EXTRACT_FILE_DIRECTORY), "thisisinvalidpassword"), 
					"ZipException should be thrown for wrong password.");
			
		}finally {
			
			testFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
			
		}

	}
	
	@Test
	public void extractFile_shouldExtractSpecificFile()  throws Exception {
		//Create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFiles(new File(this.FILE_NAME_ZIP), filesToZip);

		File testFile = new File(this.FILE_NAME_ZIP);	
		
		try {
			assertTrue(testFile.exists(), 
					"Zip file " + testFile.getName() + " was not created prior to test.");
			//Method to test
			ZipUtil.extractFile(new File(this.FILE_NAME_ZIP), this.FILE_NAME_ONE, 
					new File(this.EXTRACT_FILE_DIRECTORY));
			
			File extractedDir = new File(this.EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();

			for (File file: files) {
				assertTrue(!file.isDirectory() &&  file.getName().equals(this.FILE_NAME_ONE), 
						"Expected file (" + this.FILE_NAME_ONE + ") was not extracted.");
			}
			

		}finally {
			testFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}
	}
	
	
	@Test
	public void extractEncryptedFile_shouldExtractSpecificEncryptedFile()  throws Exception {
		//Create test zip file
		List<File> filesToZip = this.createFileList();
		ZipUtil.zipFilesWithPassword(new File(this.FILE_NAME_ZIP), filesToZip, this.ENCRYPTION_PASSWORD);

		File testFile = new File(this.FILE_NAME_ZIP);	
		
		try {
			assertTrue(testFile.exists(), 
					"Zip file " + testFile.getName() + " was not created prior to test.");
			
			//Method to test
			ZipUtil.extractEncryptedFile(new File(this.FILE_NAME_ZIP), this.FILE_NAME_ONE, 
					new File(this.EXTRACT_FILE_DIRECTORY), this.ENCRYPTION_PASSWORD);
			
			File extractedDir = new File(this.EXTRACT_FILE_DIRECTORY);
			File[] files = extractedDir.listFiles();
			
			for (File file: files) {
				assertTrue(!file.isDirectory() &&  file.getName().equals(this.FILE_NAME_ONE), 
						"Expected file (" + this.FILE_NAME_ONE + ") was not extracted.");
			}
			
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
			assertThrows(java.util.zip.ZipException.class, () -> ZipUtil.extractEncryptedFile(new File(this.FILE_NAME_ZIP), this.FILE_NAME_ONE, 
					new File(this.EXTRACT_FILE_DIRECTORY), "thisisthewrongpassword"), 
					"ZipException should be thrown for wrong password.");

		}finally {
			testFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}
	}
	
	
	/**
	 * Creates a list of test files to be zipped
	 *
	 * @return the list of test files
	 */
	private List<File> createFileList() {
		
		File testFileOne = new File(this.FILE_NAME_ONE_FULL);
		File testFileTwo = new File(this.FILE_NAME_TWO_FULL);
		
		ArrayList<File> files = new ArrayList<>();
		files.add(testFileOne);
		files.add(testFileTwo);
		return files;
		
	}
	
	/**
	 * Verifies that this file is a zip file and that it contains the zipped files
	 * @param zipFile
	 * @throws Exception
	 */
	private void checkZip (ZipFile zipFile) throws Exception {
	
		Assertions.assertTrue(zipFile.isValidZipFile(), 
	    		 "File " + this.FILE_NAME_ZIP + " is not a valid zip file.");

		List<FileHeader> fileHeaders = zipFile.getFileHeaders();

		for (FileHeader fileHeader: fileHeaders) {		
			 Assertions.assertTrue(fileHeader.getFileName().equals(this.FILE_NAME_ONE) ||
					fileHeader.getFileName().equals(this.FILE_NAME_TWO));			 
		}						
		 
		return;		
	}
	
	
	private void checkZipWithFolder (ZipFile zipFile) throws Exception {
		
		Assertions.assertTrue(zipFile.isValidZipFile(), 
	    		 "File (" + zipFile.getFile().getName() + ") is not a valid zip file.");

		List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        
		for (FileHeader fileHeader: fileHeaders) {	

			 Assertions.assertTrue(fileHeader.getFileName().equals(this.FOLDER_NAME + "/" )  ||
					fileHeader.getFileName().equals(this.FOLDER_NAME + this.FILE_NAME_ONE_IN_FOLDER) ||
					fileHeader.getFileName().equals(this.FOLDER_NAME + this.FILE_NAME_TWO_IN_FOLDER ),
					"Zip file (" + zipFile.getFile().getName() + ") does not contain the folder or the files");			 
		}	
		
		return;		
	}
	
	
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
