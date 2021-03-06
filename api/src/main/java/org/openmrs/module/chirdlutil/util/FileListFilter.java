package org.openmrs.module.chirdlutil.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;

/**
 * @author msheley Filters files in a directory based on the extension and file
 *         name must start with the provided text string.
 * 
 */
public class FileListFilter implements FilenameFilter {
	private String name;
	private HashSet<String> extensionSet = new HashSet<String>();

	public FileListFilter(String name, String extension) {
		this.name = name;
		this.extensionSet.add(extension);
	}
	
	public FileListFilter(String name, HashSet<String> extensions) {
		this.name = name;
		this.extensionSet = extensions;
	}

	public boolean accept(File directory, String filename) {
		boolean ok = true;

		if (name != null) {
			ok &= (filename.startsWith(name + ".") || filename.startsWith(name + "_") 
					|| filename.startsWith("_"+name + "_"));
		}
		
		String fileNameExtension = filename.substring(filename.lastIndexOf(".") + 1);
		ok &= extensionSet.contains("." + fileNameExtension);
	
		return ok;
	}
	
}
