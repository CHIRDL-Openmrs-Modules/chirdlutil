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

/**
 * This class will take a given file, replace a given string in the file with another string.
 *
 * @author Steve McKee
 */
public class FindAndReplace {
	
	private JTextField directoryField = new JTextField(30);
    private JTextField findField = new JTextField(30);
    private JTextField replaceField = new JTextField(30);
    private JFrame frame;
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Find and Replace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getGlassPane().addMouseListener(new MouseAdapter() {
        	
        	public void mouseClicked(MouseEvent e) {
        		// Do nothing while the glass pane is visible.
        	}
        });

        //Set up the content pane.
        Container contentPane = frame.getContentPane();
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
        label.setLabelFor(directoryField);
        label2.setLabelFor(findField);
        panel.add(label);
        panel.add(directoryField);
        panel.add(dirButton);
        panel.add(label2);
        panel.add(findField);
        panel.add(new JLabel());
        panel.add(label3);
        panel.add(replaceField);
        panel.add(new JLabel());
        
        findField.setPreferredSize(directoryField.getPreferredSize());
        replaceField.setPreferredSize(directoryField.getPreferredSize());
        
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
        frame.setContentPane(panel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    private void setDirectoryAction(JButton copyBrowseButton) {
    	copyBrowseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(directoryField.getText());
	            fileChooser.setDialogTitle("Choose a File or Directory");
	            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	            fileChooser.setMultiSelectionEnabled(false);
	            int returnVal = fileChooser.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
//		            File[] selectedDirs = fileChooser.getSelectedFiles();
//		            StringBuffer files = new StringBuffer();
//		            for (int i = 0; i < selectedDirs.length; i++) {
//		            	if (i != 0) {
//		            		files.append(",");
//		            	}
//		            	
//		            	files.append(selectedDirs[i].getAbsolutePath());
//		            }
	            	File selectedFile = fileChooser.getSelectedFile();
		            directoryField.setText(selectedFile.getAbsolutePath());
	            }
            }
    		
    	});
    }
    
    private void setGoAction(JButton goButton) {
    	goButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
				String formDirStr = directoryField.getText();
				String findStr = findField.getText();
				String replaceStr = replaceField.getText();
				File formSource = new File(formDirStr);
				
				if (!formSource.exists()) {
					JOptionPane.showMessageDialog(frame, "The file/directory does not exist.", "Error", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				Thread copyThread = new FindAndReplaceThread(frame, formSource, findStr, replaceStr);
				Component glassPane = frame.getGlassPane();
				glassPane.setVisible(true);
				glassPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				copyThread.start();
            }
    		
    	});
    }
    
    private void setCloseButtonAction(JButton closeButton) {
    	closeButton.addActionListener(new ActionListener() {

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
		
		ProgressMonitor progressMonitor = new ProgressMonitor(frame, "Processing Files...", "", 0, sourceFiles.length);
		progressMonitor.setMillisToDecideToPopup(1);
		progressMonitor.setMillisToPopup(1);
		int count = 0;
		for (File sourceFile : sourceFiles) {
			if (progressMonitor.isCanceled()) {
				break;
			}
			
			progressMonitor.setNote("Processing: " + sourceFile.getAbsolutePath());
			if (sourceFile.isDirectory()) {
				List<File> childFiles = Arrays.asList(sourceFile.listFiles());
				for (File childFile : childFiles) {
					searchAndReplace(childFile, searchString, replaceString);
				}
			}
			
			String origFilename = sourceFile.getName();
			File parentDirectory = sourceFile.getParentFile();
			BufferedInputStream fileReader = null;
			File newFile = new File(parentDirectory, origFilename + System.currentTimeMillis());
			newFile.createNewFile();
			BufferedOutputStream fileWriter = null;
			byte[] searchBytes = searchString.getBytes();
			byte[] replaceBytes = replaceString.getBytes();
			int searchSize = searchBytes.length;
			Queue byteQueue = new LinkedList();
			try {
				fileReader = new BufferedInputStream(new FileInputStream(sourceFile));
				fileWriter = new BufferedOutputStream(new FileOutputStream(newFile));
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
			} finally {
				if (fileReader != null) {
					fileReader.close();
				}
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
			}
			
			sourceFile.delete();
			sourceFile.createNewFile();
			copyFile(newFile, sourceFile);
			newFile.delete();
			
			progressMonitor.setProgress(++count);
		}
	}
	
	private void copyFile(File sourceFile, File destFile) throws IOException { 
    	if(!destFile.exists()) {  
    		destFile.createNewFile(); 
    	} 
    	
    	FileChannel source = null; 
    	FileChannel destination = null; 
    	try {  
    		source = new FileInputStream(sourceFile).getChannel();  
    		destination = new FileOutputStream(destFile).getChannel();  
    		destination.transferFrom(source, 0, source.size()); 
    	} finally {  
    		if(source != null) {   
    			source.close();  
    		}  
    		
    		if(destination != null) {   
    			destination.close();  
    		}
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
            public void run() {
            	FindAndReplace far = new FindAndReplace();
                far.createAndShowGUI();
            }
        });
	}
	
	private class FindAndReplaceThread extends Thread {
    	
    	private JFrame frame;
    	private File formSource;
    	private String find;
    	private String replace;
    	
    	public FindAndReplaceThread(JFrame frame, File formSource, String find, String replace) {
    		this.frame = frame;
    		this.formSource = formSource;
    		this.find = find;
    		this.replace = replace;
    	}
    	
    	public void run() {
    		boolean success = false;
    		try {
	            searchAndReplace(formSource, find, replace);
	            success = true;
            }
            catch (final Exception e) {
	            e.printStackTrace();
	            javax.swing.SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	Component glassPane = frame.getGlassPane();
	        			glassPane.setVisible(false);
	            		glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	            		JOptionPane.showMessageDialog(frame, "Error executing find and replace:\n" + e.getMessage(), 
	            			"Error", JOptionPane.ERROR_MESSAGE);
	                }
	            });
            }
            
            if (success) {
	    		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	Component glassPane = frame.getGlassPane();
	        			glassPane.setVisible(false);
	            		glassPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	            		JOptionPane.showMessageDialog(frame, "Finished!", "Complete", JOptionPane.INFORMATION_MESSAGE);
	                }
	            });
            }
    	}
    }
}
