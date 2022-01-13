package org.openmrs.module.chirdlutil.tools;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChangeFileExtensions {
    
    private static final Logger LOG = LoggerFactory.getLogger(ChangeFileExtensions.class);
    
    public void changeExtensions(File directory, String origExt, String newExt) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                changeExtensions(file, origExt, newExt);
            }
            
            String filename = file.getName();
            if (filename.endsWith(origExt)) {
                String subName = filename.substring(0, filename.lastIndexOf("."));
                String newName = subName + newExt;
                File newFile = new File(file.getParentFile(), newName);
                if (!file.renameTo(newFile)) {
                    LOG.error(String.format("Unable to rename file %1$s to %2$s", file.getAbsolutePath(), newName));
                }
            }
        }
    }
    
    private void printUsage() {
        LOG.info("Arguments:");
        LOG.info("Arg 1: The directory to check (Note: this program is recursive and will apply to all " +
                "sub-directories.");
        LOG.info("Arg 2: The original file extension");
        LOG.info("Arg 3: The new file extension");
        LOG.info("");
        LOG.info("Usage:");
        LOG.info("ChangeFileExtensions C:\\temp 20 xml");
        LOG.info("This will change the extension of files with the extension of 20 to xml in the " +
                "C:\temp directory");
    }
    
    /**
     * Auto generated method comment
     * 
     * @param args
     */
    public static void main(String[] args) {
        ChangeFileExtensions changeExt = new ChangeFileExtensions();
        if (args.length != 3) {
            changeExt.printUsage();
            System.exit(1);
        }
        
        String directoryStr = args[0];
        String origExt = args[1];
        String newExt = args[2];
        
        File directory = new File(directoryStr);
        if (!directory.exists()) {
            LOG.error(String.format("The directory %s does not exist", directoryStr));
            LOG.error("");
            changeExt.printUsage();
            System.exit(1);
        } else if (!directory.isDirectory()) {
            LOG.error(String.format("The directory %s is not a directory", directoryStr));
            LOG.error("");
            changeExt.printUsage();
            System.exit(1);
        }
        
        if (!origExt.startsWith(".")) {
            origExt = "." + origExt;
        }
        
        if (!newExt.startsWith(".")) {
            newExt = "." + newExt;
        }
        
        changeExt.changeExtensions(directory, origExt, newExt);
        
        System.exit(0);
    }
    
}
