package org.openmrs.module.chirdlutil.test.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.chirdlutil.util.ZipUtil;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

/**
 * @author davely
 */
public class TestZipUtil extends BaseModuleContextSensitiveTest {

	@Test
	public void zipFilesTest() throws Exception
	{
		String fileNameOne = "src/test/resources/TestFiles/TestFileOne.txt";
		String fileNameTwo = "src/test/resources/TestFiles/TestFileTwo.txt";
		
		File testFileOne = new File(fileNameOne);
		File testFileTwo = new File(fileNameTwo);
		if (!testFileOne.exists() || !testFileTwo.exists()) {
			System.out.println("Did not test. Test files do not exist.");
			return;
		}
		
		//Test the chirdlutil method -will write to disk
		
		//read the test files
		 ArrayList<File> files = new ArrayList<>();
		 files.add(testFileOne);
		 files.add(testFileTwo);
		 
		 //chirdutil method to test
		 ZipUtil.zipFiles(new File("src/test/resources/TestFiles/testZipFile.zip"), files);
		 
		 //final check if new zip file exists and if content correct
		 File f = new File("src/test/resources/TestFiles/testZipFile.zip");		 
		 Assertions.assertTrue(f.exists() && !f.isDirectory());
		 
		 //verify content
	     ZipFile zipFileToCheck = new ZipFile(f);
	     Assertions.assertTrue(zipFileToCheck.isValidZipFile());
	     
	     List<FileHeader> fileHeaders = zipFileToCheck.getFileHeaders();
		 for (FileHeader fileHeader: fileHeaders) {		
			Assertions.assertTrue((fileHeader.getFileName().equals("TestFileOne.txt")  ||
					fileHeader.getFileName().equals("TestFileTwo.txt")), "filename = " + fileHeader.getFileName());		
		 } 
		 
	     zipFileToCheck.close();
	     f.delete();
	  
		//Tests only Zip4j not our method
			/*	Assertions.assertTrue(testFileOne.isFile(), "Test file" + fileNameOne + " does not exist.");
				Assertions.assertTrue(testFileTwo.isFile(), "Test file" + fileNameTwo + "does not exist" );
				
				 if (testFileOne.isFile() &&  testFileTwo.isFile()) {
					File testZipFile = new File("testZipFile.txt");
					
					//Tests only the zip4j functioning
					ZipFile zipFile = new ZipFile(testZipFile);
					zipFile.addFile(testFileOne);
					zipFile.addFile(testFileTwo);
					Assertions.assertTrue(zipFile.isValidZipFile());
					List<FileHeader> fileHeaders = zipFile.getFileHeaders();
					for (FileHeader fileHeader: fileHeaders) {
						
						Assertions.assertTrue((fileHeader.getFileName().equals("TestFileOne.txt")  ||
								fileHeader.getFileName().equals("TestFileTwo.txt")), "filename = " + fileHeader.getFileName());
							
					} 
				 }
			 */
		
		 
		
		
	}
	
	
}
