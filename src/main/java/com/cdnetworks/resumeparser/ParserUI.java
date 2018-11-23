package com.cdnetworks.resumeparser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserUI extends JPanel implements ActionListener {
    
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ParserUI.class);
    
    private JProgressBar pBar;
    private Timer timer;
    
    private ParserController parseControl;
    
    // UI components
    
    private final JLabel keywordsLabel;
    private final JLabel resumeFolderLabel;
    private final JLabel outputFolderLabel;
    private JTextField keywordsField;
    private JTextField resumeFolderField;
    private JTextField outputFolderField;
    private JButton resumeFolderButton;
    private JButton outputFolderButton;
    private JTextArea taskOutput;
    private JButton submitButton;
    private JButton cancelButton;
    
    static final int BAR_MIN = 0;
    static final int BAR_MAX = 100;
    
    Thread t;
    private String outputValidate;
    
    public ParserUI() {
        super(new BorderLayout());
        //parseControl = new ParserController();
        
        int fontSize = pointToPixel(10);
        
        // Create UI components
        keywordsLabel = new JLabel("Keywords: ");
        keywordsLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        keywordsField = new JTextField(28);
        keywordsField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        keywordsField.getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                String query = keywordsField.getText();
                if(query.isEmpty() || !validate(query)){
                    submitButton.setEnabled(false);
                } else {
                    submitButton.setEnabled(true);
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                String query = keywordsField.getText();
                if(query.isEmpty() || !validate(query)){
                    submitButton.setEnabled(false);
                } else {
                    submitButton.setEnabled(true);
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                String query = keywordsField.getText();
                if(query.isEmpty() || !validate(query)){
                    submitButton.setEnabled(false);
                } else {
                    submitButton.setEnabled(true);
                }
            }
        });
        
        resumeFolderLabel = new JLabel("Resume Folder: ");
        outputFolderLabel = new JLabel("Output Folder: ");
        resumeFolderLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        outputFolderLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        resumeFolderField = new JTextField(16);
        resumeFolderField.setFont(new Font("Arial", Font.PLAIN, fontSize));
        resumeFolderField.setEditable(false);
        resumeFolderField.setBackground(Color.white);
        outputFolderField = new JTextField(16);
        outputFolderField.setFont(new Font("Arial", Font.PLAIN, fontSize));
        outputFolderField.setEditable(false);
        outputFolderField.setBackground(Color.white);
        resumeFolderButton = new JButton("Select Folder");
        resumeFolderButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        outputFolderButton = new JButton("Select Folder");
        outputFolderButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        submitButton.setActionCommand("start");
        submitButton.addActionListener(this);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        cancelButton.setEnabled(false);
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        
        pBar = new JProgressBar(BAR_MIN, BAR_MAX);
        pBar.setValue(0);
        pBar.setStringPainted(true);
        
        taskOutput = new JTextArea(5,20);
        taskOutput.setFont(new Font("Arial", Font.PLAIN, fontSize));
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
        taskOutput.setCursor(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);
        
        // Add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;     
        mainPanel.add(keywordsLabel, constraints);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        mainPanel.add(keywordsField, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;     
        constraints.gridwidth = 1;
        mainPanel.add(resumeFolderLabel, constraints);
        constraints.gridx = 1;
        mainPanel.add(resumeFolderField, constraints);
        constraints.gridx = 2;
        mainPanel.add(resumeFolderButton, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 2;     
        mainPanel.add(outputFolderLabel, constraints);
        constraints.gridx = 1;
        mainPanel.add(outputFolderField, constraints);
        constraints.gridx = 2;
        mainPanel.add(outputFolderButton, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 3;
        mainPanel.add(pBar, constraints);
        
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.PAGE_END;
        mainPanel.add(submitButton, constraints);
        
        constraints.gridx = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LAST_LINE_END;
        mainPanel.add(cancelButton, constraints);
        
        super.add(mainPanel, BorderLayout.PAGE_START);
        super.add(new JScrollPane(taskOutput), BorderLayout.SOUTH);
        super.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        timer = new Timer(100, (ActionEvent event) -> {
            pBar.setValue(parseControl.getPercent());
            String output = parseControl.getOutput();
            if(output != null && !output.equals(outputValidate)) {
                outputValidate = output;
                taskOutput.append(output + '\n');
                taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
            }
            if(parseControl.getPercent() == BAR_MAX) {
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                submitButton.setEnabled(true);
                cancelButton.setEnabled(false);
                setCursor(null);
                pBar.setValue(BAR_MAX);
            }
        });
        
        resumeFolderButton.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               try {
                    String chooserTitle = "Resume Folder Location";
                    
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File("."));
                    chooser.setDialogTitle(chooserTitle);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    
                    chooser.setAcceptAllFileFilterUsed(false);
                    
                    if (chooser.showOpenDialog(resumeFolderButton) == JFileChooser.APPROVE_OPTION) { 
                        resumeFolderField.setText(chooser.getSelectedFile().toString());
                    }
                    else {
                            System.out.println("No Selection ");
                    }
               } catch (HeadlessException ex) {
                    LOGGER.info(ex.toString());
               }
           }
        });
        
        outputFolderButton.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               try {
                    String chooserTitle = "Output Folder Location";
                    
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File("."));
                    chooser.setDialogTitle(chooserTitle);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    
                    chooser.setAcceptAllFileFilterUsed(false);
                    
                    if (chooser.showOpenDialog(resumeFolderButton) == JFileChooser.APPROVE_OPTION) { 
                        outputFolderField.setText(chooser.getSelectedFile().toString());
                    }
                    else {
                            System.out.println("No Selection ");
                    }
               } catch (HeadlessException ex) {
                    LOGGER.info(ex.toString());
               }
           }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        if(evt.getActionCommand().equals("start")) {
            submitButton.setEnabled(false);
            cancelButton.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            String keywords = keywordsField.getText();
            String resumeFolderLocation = resumeFolderField.getText();
            String outputFolderLocation = outputFolderField.getText();
            timer.start();
            try {
                parseControl = new ParserController(keywords,resumeFolderLocation,outputFolderLocation);
                t = new Thread(parseControl);
                t.start();
                if(parseControl.getPercent() == 100) {
                    taskOutput.append("Resumes processed. \n");
                    taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
                }
            } catch (Exception ex) {
                taskOutput.append("Process Error. Please try again. \n");
                taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
            }
        }
        if(evt.getActionCommand().equals("cancel")) {
            timer.stop();
            submitButton.setEnabled(true);
            cancelButton.setEnabled(false);
            setCursor(null);
            pBar.setValue(BAR_MIN);
            if (t != null) {
                t.interrupt();
            }
        }
    }
    
    private static void displayUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        JFrame window = new JFrame("CDN Resume Parser V1.4.0");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent mainPane = new ParserUI();
        mainPane.setOpaque(true);
        window.setContentPane(mainPane);
        
        window.pack();
        window.setVisible(true);
    }
    
    private static int pointToPixel(float pt) {
        float ppi = Toolkit.getDefaultToolkit().getScreenResolution();
        return (int) Math.round(pt / (72.0 / ppi));
    }
    
    private static boolean validate(String query) {
        Pattern p = Pattern.compile("[\\\\\\\\!`'$%&*,/:;<=>?@\\[\\]\\\\[\\\\]^_{|}~0-9]+");
        Matcher m = p.matcher(query);
        return !m.find();
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(ParserUI::displayUI);
    }
}
