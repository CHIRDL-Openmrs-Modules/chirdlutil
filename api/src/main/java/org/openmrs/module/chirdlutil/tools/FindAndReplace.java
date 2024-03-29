package org.openmrs.module.chirdlutil.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpringLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * This class will take a given file, replace a given string in the file with another string.
 *
 * @author Steve McKee
 */
public class FindAndReplace {
    
    private static final Logger LOG = LoggerFactory.getLogger(FindAndReplace.class);
    
    JTextField directoryField = new JTextField(30);
    JTextField findField = new JTextField(30);
    JTextField replaceField = new JTextField(30);
    JFrame frame;
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    void createAndShowGUI() {
        //Create and set up the window.
        this.frame = new JFrame("Find and Replace");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getGlassPane().addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Do nothing while the glass pane is visible.
            }
        });

        //Set up the content pane.
        Container contentPane = this.frame.getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);

        //Create and add the components.
        JLabel label = new JLabel("File/Directory: ");
        JLabel label2 = new JLabel("Find: ");
        JLabel label3 = new JLabel("Replace With: ");
        
        JButton dirButton = new JButton("Browse...");
        setDirectoryAction(dirButton);
        JButton goButton = new JButton("Go");
        setGoAction(goButton);
        JButton closeButton = new JButton("Close");
        setCloseButtonAction(closeButton);
        goButton.setPreferredSize(closeButton.getPreferredSize());
        
        JPanel panel = new JPanel(layout);
        label.setLabelFor(this.directoryField);
        label2.setLabelFor(this.findField);
        panel.add(label);
        panel.add(this.directoryField);
        panel.add(dirButton);
        panel.add(label2);
        panel.add(this.findField);
        panel.add(new JLabel());
        panel.add(label3);
        panel.add(this.replaceField);
        panel.add(new JLabel());
        
        this.findField.setPreferredSize(this.directoryField.getPreferredSize());
        this.replaceField.setPreferredSize(this.directoryField.getPreferredSize());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(goButton);
        buttonPanel.add(closeButton);
        panel.add(new JLabel());
        panel.add(buttonPanel);
        panel.add(new JLabel());
        
        //Lay out the panel.
        SpringUtilities.makeCompactGrid(panel,
                                        4, 3, //rows, cols
                                        6, 6, //initX, initY
                                        6, 6);//xPad, yPad
        
        //Set up the content pane.
        panel.setOpaque(true);  //content panes must be opaque
        this.frame.setContentPane(panel);

        //Display the window.
        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    private void setDirectoryAction(JButton copyBrowseButton) {
        copyBrowseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(FindAndReplace.this.directoryField.getText());
                fileChooser.setDialogTitle("Choose a File or Directory");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.setMultiSelectionEnabled(false);
                int returnVal = fileChooser.showOpenDialog(FindAndReplace.this.frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File[] selectedDirs = fileChooser.getSelectedFiles();
//                    StringBuffer files = new StringBuffer();
//                    for (int i = 0; i < selectedDirs.length; i++) {
//                        if (i != 0) {
//                            files.append(",");
//                        }
//                        
//                        files.append(selectedDirs[i].getAbsolutePath());
//                    }
                    File selectedFile = fileChooser.getSelectedFile();
                    FindAndReplace.this.directoryField.setText(selectedFile.getAbsolutePath());
                }
            }
            
        });
    }
    
    private void setGoAction(JButton goButton) {
        goButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String formDirStr = FindAndReplace.this.directoryField.getText();
                String findStr = FindAndReplace.this.findField.getText();
                String replaceStr = FindAndReplace.this.replaceField.getText();
                File formSource = new File(formDirStr);
                
                if (!formSource.exists()) {
                    JOptionPane.showMessageDialog(FindAndReplace.this.frame, "The file/directory does not exist.", "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                    
                Thread copyThread = new FindAndReplaceThread(FindAndReplace.this.frame, formSource, findStr, replaceStr);
                Component glassPane = FindAndReplace.this.frame.getGlassPane();
                glassPane.setVisible(true);
                glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                copyThread.start();
            }
            
        });
    }
    
    private void setCloseButtonAction(JButton closeButton) {
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
            
        });
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void searchAndReplace(File source, String searchString, String replaceString) 
    throws FileNotFoundException, IOException {
        File[] sourceFiles = null;
        if (source.isDirectory()) {
            sourceFiles = source.listFiles();
        } else {
            sourceFiles = new File[] { source };
        }
        
        ProgressMonitor progressMonitor = new ProgressMonitor(this.frame, "Processing Files...", "", 0, sourceFiles.length);
        progressMonitor.setMillisToDecideToPopup(1);
        progressMonitor.setMillisToPopup(1);
        int count = 0;
        for (File sourceFile : sourceFiles) {
            if (progressMonitor.isCanceled()) {
                break;
            }
            
            progressMonitor.setProgress(++count);
            progressMonitor.setNote("Processing: " + sourceFile.getAbsolutePath());
            if (sourceFile.isDirectory()) {
                List<File> childFiles = Arrays.asList(sourceFile.listFiles());
                for (File childFile : childFiles) {
                    searchAndReplace(childFile, searchString, replaceString);
                }
            }
            
            String origFilename = sourceFile.getName();
            File parentDirectory = sourceFile.getParentFile();
            File newFile = new File(parentDirectory, origFilename + System.currentTimeMillis());
            if (!newFile.createNewFile()) {
                LOG.error("Could not create new file: {}", newFile.getAbsolutePath());
                continue;
            }
            
            byte[] searchBytes = searchString.getBytes();
            byte[] replaceBytes = replaceString.getBytes();
            int searchSize = searchBytes.length;
            Queue byteQueue = new LinkedList();
            try (BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(sourceFile));
                BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(newFile))) {
                int c;
                while ((c = fileReader.read()) != -1) {
                    byteQueue.add(c);
                    while (byteQueue.size() > searchSize) {
                        fileWriter.write((Integer)byteQueue.poll());
                    }
                    
                    if (byteQueue.size() < searchSize) {
                        continue;
                    }
                    
                    Integer[] byteArray = new Integer[byteQueue.size()];
                    byteQueue.toArray(byteArray);
                    boolean match = true;
                    for (int i = 0; i < byteArray.length; i++) {
                        int fileByte = byteArray[i];
                        int searchByte = searchBytes[i];
                        if (fileByte != searchByte) {
                            match = false;
                            break;
                        }
                    }
                    
                    if (match) {
                        byteQueue.clear();
                        for (int i = 0; i < replaceBytes.length; i++) {
                            byteQueue.add((int)replaceBytes[i]);
                        }
                    }
                }
                
                while (!byteQueue.isEmpty()) {
                    fileWriter.write((Integer)byteQueue.poll());
                }
            }
            
            if (!sourceFile.delete()) {
                LOG.error("Could not delete file: {}", sourceFile.getAbsolutePath());
                continue;
            }
            
            if (!sourceFile.createNewFile()) {
                LOG.error("Could not create new file: {}", sourceFile.getAbsolutePath());
                continue;
            }
            
            copyFile(newFile, sourceFile);
            if (!newFile.delete()) {
                LOG.error("Could not delete file: {}", newFile.getAbsolutePath());
            }
        }
    }
    
    private void copyFile(File sourceFile, File destFile) throws IOException { 
        if(!destFile.exists()) {  
            if (!destFile.createNewFile()) {
                LOG.error("Could not create new file: {}", destFile.getAbsolutePath());
                return;
            }
        } 
        
        try (FileInputStream sourceInStream = new FileInputStream(sourceFile);
                FileOutputStream destOutStream = new FileOutputStream(destFile);
                FileChannel source = sourceInStream.getChannel();  
                FileChannel destination = destOutStream.getChannel()) {  
              
            destination.transferFrom(source, 0, source.size()); 
        }
    }
    
    /**
     * Auto generated method comment
     * 
     * @param args
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FindAndReplace far = new FindAndReplace();
                far.createAndShowGUI();
            }
        });
    }
    
    private class FindAndReplaceThread extends Thread {
        
        JFrame jFrame;
        private File formSource;
        private String find;
        private String replace;
        
        public FindAndReplaceThread(JFrame frame, File formSource, String find, String replace) {
            this.jFrame = frame;
            this.formSource = formSource;
            this.find = find;
            this.replace = replace;
        }
        
        @Override
        public void run() {
            boolean success = false;
            try {
                searchAndReplace(this.formSource, this.find, this.replace);
                success = true;
            }
            catch (final Exception e) {
                LOG.error(Util.getStackTrace(e));
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Component glassPane = FindAndReplaceThread.this.jFrame.getGlassPane();
                        glassPane.setVisible(false);
                        glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        JOptionPane.showMessageDialog(FindAndReplaceThread.this.jFrame, "Error executing find and replace:\n" + e.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            
            if (success) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Component glassPane = FindAndReplaceThread.this.jFrame.getGlassPane();
                        glassPane.setVisible(false);
                        glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        JOptionPane.showMessageDialog(FindAndReplaceThread.this.jFrame, "Finished!", "Complete", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        }
    }
}
