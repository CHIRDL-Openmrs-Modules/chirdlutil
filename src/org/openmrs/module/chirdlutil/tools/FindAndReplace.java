package org.openmrs.module.chirdlutil.tools;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpringLayout;

import org.openmrs.module.chirdlutil.util.IOUtil;

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
        frame = new JFrame("Copy Directory");
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
        JLabel label = new JLabel("Form Directory: ");
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
	            fileChooser.setDialogTitle("Choose Directory Containing Forms");
	            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
	            	File selectedDir = fileChooser.getSelectedFile();
		            directoryField.setText(selectedDir.getAbsolutePath());
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
				File formDir = new File(formDirStr);
				
				if (!formDir.exists()) {
					JOptionPane.showMessageDialog(frame, "The form directory does not exist.", "Error", 
						JOptionPane.ERROR_MESSAGE);
					return;
				} else if (!formDir.isDirectory()) {
					JOptionPane.showMessageDialog(frame, "The form directory is not a directory.", "Error", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
					
				Thread copyThread = new FindAndReplaceThread(frame, new File (formDirStr), findStr, replaceStr);
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
	
	public void searchAndReplace(File sourceDir, String searchString, String replaceString) 
	throws FileNotFoundException, IOException {
		File[] sourceFiles = sourceDir.listFiles();
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
			FileInputStream inStream = new FileInputStream(sourceFile);
			DataInputStream dataInStream = null;
			File newFile = new File(parentDirectory, origFilename + System.currentTimeMillis());
			newFile.createNewFile();
			FileOutputStream outStream = new FileOutputStream(newFile);
			DataOutputStream dataOutStream = null;
			try {
				dataInStream = new DataInputStream(inStream);
				BufferedReader reader = new BufferedReader(new InputStreamReader(dataInStream));
				dataOutStream = new DataOutputStream(outStream);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dataOutStream));
				String line;
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (i != 0) {
						writer.append("\n");
					}
					
					i++;
					line = line.replace(searchString, replaceString);
					writer.append(line);
					writer.flush();
				}
			} finally {
				if (dataInStream != null) {
					dataInStream.close();
				}
				if (dataOutStream != null) {
					dataOutStream.flush();
					dataOutStream.close();
				}
			}
			
			sourceFile.delete();
			sourceFile.createNewFile();
			try {
				IOUtil.copyFile(newFile.getAbsolutePath(), sourceFile.getAbsolutePath());
				newFile.delete();
			} catch (Exception e) {
				throw new IOException("Error copying file back to original.", e);
			}
			
			progressMonitor.setProgress(++count);
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
    	private File formDir;
    	private String find;
    	private String replace;
    	
    	public FindAndReplaceThread(JFrame frame, File formDir, String find, String replace) {
    		this.frame = frame;
    		this.formDir = formDir;
    		this.find = find;
    		this.replace = replace;
    	}
    	
    	public void run() {
    		boolean success = false;
    		try {
	            searchAndReplace(formDir, find, replace);
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
