package org.openmrs.module.chirdlutil.test.util;

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
	
	String DESTINATION_DIR = "src/test/resources/TestFiles/";
	String FILE_NAME_ONE = this.DESTINATION_DIR + "FileToZipOne.txt";
	String FILE_NAME_TWO = this.DESTINATION_DIR + "FileToZipTwo.txt";
	String FILE_NAME_ZIP = this.DESTINATION_DIR + "newZipFile.zip";
	String ZIP_FILE_WITH_FOLDER = this.DESTINATION_DIR + "ZipFileWithFolder.zip";
	String EXTRACT_FILE_DIRECTORY = this.DESTINATION_DIR + "Extract_Dir/";
	String FOLDER_NAME = "TestFolderToZip";
	String FOLDER_PATH = this.DESTINATION_DIR + this.FOLDER_NAME + "/";
	
	String ENCRYPTION_PASSWORD = "EnCr&ptedP@ssw0rd";

	File testFileOne;
	File testFileTwo;
	
	
	@Test
	public void zipFiles_shouldZipFilesAndSave() throws Exception
	{
	
		 List<File> filesToZip = this.initFiles();
		
		 //chirdutil method to test
		 ZipUtil.zipFiles(new File(this.FILE_NAME_ZIP), filesToZip);
		 
		 //Without using Zip4j api, check if a new file exists 
		 File newFile = new File(this.FILE_NAME_ZIP);		 
		 assertTrue(newFile.exists() && !newFile.isDirectory(), 
				 "Zip file " + FILE_NAME_ZIP + " was not created.");
		 		 
		 //Verify if zip file type and content is correct
	     ZipFile newZipFile = new ZipFile(newFile);
	     
	     try {
	    	 checkZip(newZipFile);	
	     } finally {
	    	 newZipFile.close();
	    	 newFile.delete();
	     }
	 
	}
	
	@Test
	public void zipFilesWithPassword_shouldCreatePasswordProtectedZipFile()  throws Exception {
		
		List<File> filesToZip = this.initFiles();
		
		ZipUtil.zipFilesWithPassword(new File(this.FILE_NAME_ZIP), filesToZip, this.ENCRYPTION_PASSWORD);
		
		 File newFile = new File(this.FILE_NAME_ZIP);
		 ZipFile newZipFileNoPassword = new ZipFile(this.FILE_NAME_ZIP);
		 ZipFile newZipFileWithPassword = new ZipFile(this.FILE_NAME_ZIP, this.ENCRYPTION_PASSWORD.toCharArray());
		 
		 try {
			//Without using Zip4j api, check if a new file exists 	 
			 assertTrue(newFile.exists() && !newFile.isDirectory(), 
				 "Zip file " + this.FILE_NAME_ZIP + " was not created.");
			 
			 assertTrue(newZipFileWithPassword.isEncrypted(), 
						"Zip file should be encrypted, but it is not encrypted.");	
			 
			 //Verify unable to access if no password was used
			 assertThrows(ZipException.class, () -> newZipFileNoPassword.extractAll("src/test/resources/TestFiles/temp"), 
					"ZipException should have been thrown because no password was used.");
		
			 checkZip(newZipFileWithPassword);
			
		 } finally {
			 
			 newZipFileNoPassword.close();
			 newZipFileWithPassword.close();
			 File tempDir = new File ("src/test/resources/TestFiles/temp");
			 tempDir.delete();
			 newFile.delete();
		 }
			
	

	}
	
	
	@Test
	public void zipFolder_shouldCreateZipFileContainingFolder()  throws Exception {
		
		File destinationZipFile = new File(this.ZIP_FILE_WITH_FOLDER);
		
		ZipUtil.zipFolder(destinationZipFile, new File(this.FOLDER_PATH));
		
		assertTrue(destinationZipFile.exists(), 
				 "Zip file " + destinationZipFile.getName() + " was not created.");
		 
		//Verify zip file content
		ZipFile zipFileToCheck = new ZipFile(this.ZIP_FILE_WITH_FOLDER);
		
		try {
			checkZipWithFolder(zipFileToCheck);
		} finally {
			zipFileToCheck.close();
			destinationZipFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}
		
	}
	
	@Test
	public void zipFolderWithPassword_shouldCreateZipWithEncryptedFolder()  throws Exception {
	
		File destinationZipFile = new File(this.ZIP_FILE_WITH_FOLDER);
		ZipFile newZipFileNoPassword = new ZipFile(this.ZIP_FILE_WITH_FOLDER);
		ZipFile newZipFileWithPassword = new ZipFile(this.ZIP_FILE_WITH_FOLDER, this.ENCRYPTION_PASSWORD.toCharArray());
		try {
			ZipUtil.zipFolderWithPassword(destinationZipFile, new File(this.FOLDER_PATH), this.ENCRYPTION_PASSWORD);
		
			assertTrue(destinationZipFile.exists(), 
				 "Zip file " + destinationZipFile.getName() + " was not created.");
		
			//Verify unable to access if no password was used
			assertThrows(ZipException.class, () -> newZipFileNoPassword.extractAll("src/test/resources/TestFiles/temp"), 
				"ZipException should have been thrown because no password was used.");
	     
			assertTrue(newZipFileWithPassword.isEncrypted(), 
					"Zip file should be encrypted, but it is not encrypted.");	
		 
			//Verify zip file content
			checkZipWithFolder(newZipFileWithPassword);
			
		} finally {
			newZipFileNoPassword.close();
			newZipFileWithPassword.close();
			File tempDir = new File ("src/test/resources/TestFiles/temp");
			tempDir.delete();
			destinationZipFile.delete();
			this.deleteDirectory(new File(this.EXTRACT_FILE_DIRECTORY));
		}
		
	}
	
	@Test
	public void extractAllFiles_shouldExtractAllFilesToFolder()  throws Exception {
		
		
//		ZipUtil.extractAllFiles(testFileTwo, testFileOne);
		//ewZipFileNoPassword.extractAll("src/test/resources/TestFiles/temp"), 
		//"ZipException should have been thrown because no password was used.");
		
	}
	
	
	
	private List<File> initFiles() {
		
		 this.testFileOne = new File(FILE_NAME_ONE);
		 this.testFileTwo = new File(FILE_NAME_TWO);
		
		ArrayList<File> files = new ArrayList<>();
		files.add(testFileOne);
		files.add(testFileTwo);
		return files;
		
	}
	
	private void checkZip (ZipFile zipFile) throws Exception {
	
		Assertions.assertTrue(zipFile.isValidZipFile(), 
	    		 "File " + this.FILE_NAME_ZIP + " is not a valid zip file.");

		List<FileHeader> fileHeaders = zipFile.getFileHeaders();

		for (FileHeader fileHeader: fileHeaders) {		
			 Assertions.assertTrue(fileHeader.getFileName().equals(this.testFileOne.getName()) ||
					fileHeader.getFileName().equals(this.testFileTwo.getName()));			 
		}						
		 
		return;		
	}
	
	
	private void checkZipWithFolder (ZipFile zipFile) throws Exception {
		
		Assertions.assertTrue(zipFile.isValidZipFile(), 
	    		 "File " + zipFile.getFile().getName() + " is not a valid zip file.");

		List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        
		for (FileHeader fileHeader: fileHeaders) {	

			 Assertions.assertTrue(fileHeader.getFileName().equals(this.FOLDER_NAME + "/" )  ||
					fileHeader.getFileName().equals(this.FOLDER_NAME + "/FileInFolderToZipOne.txt") ||
					fileHeader.getFileName().equals(this.FOLDER_NAME + "/FileInFolderToZipTwo.txt" ));			 
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
