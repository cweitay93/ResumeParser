/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdnetworks.resumeparser;

import static com.cdnetworks.resumeparser.ParserUI.BAR_MAX;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jerry-chung
 */
public class NewUI extends javax.swing.JFrame {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(ParserUI.class); //error
    
    private ParserController parseControl;
    
    static final int BAR_MIN = 0;
    static final int BAR_MAX = 100;
    private Timer timer;
    Thread t;
   
    /**
     * Creates new form NewUI
     */
    public NewUI() {
        initComponents();
        panelOverlay.setBackground(new Color(255,255,255,40));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel2 = new javax.swing.JPanel();
        keywordsLabel = new javax.swing.JLabel();
        keywordsField = new javax.swing.JTextField();
        separator1 = new javax.swing.JSeparator();
        resumeFolderButton = new javax.swing.JButton();
        resumeFolderLabel = new javax.swing.JLabel();
        resumeFolderField = new javax.swing.JTextField();
        separator2 = new javax.swing.JSeparator();
        outputFolderLabel = new javax.swing.JLabel();
        outputFolderField = new javax.swing.JTextField();
        outputFolderButton = new javax.swing.JButton();
        separator3 = new javax.swing.JSeparator();
        submitButton = new java.awt.Button();
        cancelButton = new java.awt.Button();
        pBar = new javax.swing.JProgressBar();
        panelOverlay = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Resume Parser 1.5.0");
        setBackground(new java.awt.Color(255, 255, 255));
        setLocation(new java.awt.Point(120, 120));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        keywordsLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        keywordsLabel.setForeground(new java.awt.Color(0, 0, 51));
        keywordsLabel.setText("Keywords");
        jPanel2.add(keywordsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        keywordsField.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        keywordsField.setBorder(null);
        keywordsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordsFieldActionPerformed(evt);
            }
        });
        keywordsField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                keywordsFieldKeyTyped(evt);
            }
        });
        jPanel2.add(keywordsField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 400, 30));

        separator1.setForeground(new java.awt.Color(102, 102, 102));
        jPanel2.add(separator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 400, 10));

        resumeFolderButton.setBackground(new java.awt.Color(255, 255, 255));
        resumeFolderButton.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        resumeFolderButton.setForeground(new java.awt.Color(0, 0, 153));
        resumeFolderButton.setText("Select Folder");
        resumeFolderButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        resumeFolderButton.setContentAreaFilled(false);
        resumeFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resumeFolderButtonMouseClicked(evt);
            }
        });
        resumeFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeFolderButtonActionPerformed(evt);
            }
        });
        jPanel2.add(resumeFolderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, -1, -1));

        resumeFolderLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        resumeFolderLabel.setForeground(new java.awt.Color(0, 0, 51));
        resumeFolderLabel.setText("Resume Folder");
        jPanel2.add(resumeFolderLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        resumeFolderField.setEditable(false);
        resumeFolderField.setBackground(new java.awt.Color(250, 250, 250));
        resumeFolderField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        resumeFolderField.setBorder(null);
        resumeFolderField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeFolderFieldActionPerformed(evt);
            }
        });
        jPanel2.add(resumeFolderField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 300, 30));

        separator2.setForeground(new java.awt.Color(102, 102, 102));
        jPanel2.add(separator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 400, 10));

        outputFolderLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        outputFolderLabel.setForeground(new java.awt.Color(0, 0, 51));
        outputFolderLabel.setText("Output Folder");
        jPanel2.add(outputFolderLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, -1, -1));

        outputFolderField.setEditable(false);
        outputFolderField.setBackground(new java.awt.Color(250, 250, 250));
        outputFolderField.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        outputFolderField.setBorder(null);
        outputFolderField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFolderFieldActionPerformed(evt);
            }
        });
        jPanel2.add(outputFolderField, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 300, 30));

        outputFolderButton.setBackground(new java.awt.Color(255, 255, 255));
        outputFolderButton.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        outputFolderButton.setForeground(new java.awt.Color(0, 0, 153));
        outputFolderButton.setText("Select Folder");
        outputFolderButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        outputFolderButton.setContentAreaFilled(false);
        outputFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                outputFolderButtonMouseClicked(evt);
            }
        });
        outputFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFolderButtonActionPerformed(evt);
            }
        });
        jPanel2.add(outputFolderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 280, -1, -1));

        separator3.setForeground(new java.awt.Color(102, 102, 102));
        jPanel2.add(separator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, 400, 10));

        submitButton.setBackground(new java.awt.Color(255, 153, 0));
        submitButton.setEnabled(false);
        submitButton.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        submitButton.setForeground(new java.awt.Color(255, 255, 255));
        submitButton.setLabel("Submit");
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                submitButtonMouseClicked(evt);
            }
        });
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        jPanel2.add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, 190, 30));

        cancelButton.setBackground(new java.awt.Color(235, 235, 235));
        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cancelButton.setEnabled(false);
        cancelButton.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        cancelButton.setLabel("Cancel");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });
        jPanel2.add(cancelButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 340, 190, 30));

        pBar.setBackground(new java.awt.Color(255, 255, 255));
        pBar.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        pBar.setForeground(new java.awt.Color(0, 204, 51));
        pBar.setBorderPainted(false);
        pBar.setFocusable(false);
        jPanel2.add(pBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 490, 10));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 0, 460, -1));

        panelOverlay.setBackground(new java.awt.Color(255, 255, 255));
        panelOverlay.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Impact", 0, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Resume Parser");
        panelOverlay.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));

        jLabel2.setBackground(new java.awt.Color(204, 204, 204));
        jLabel2.setFont(new java.awt.Font("Impact", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(245, 245, 245));
        jLabel2.setText("Version 1.5.0");
        panelOverlay.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, -1));

        getContentPane().add(panelOverlay, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 290, 420));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/desktop-1149231_640.jpg"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 290, 420));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void keywordsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keywordsFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keywordsFieldActionPerformed

    private void resumeFolderFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeFolderFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resumeFolderFieldActionPerformed

    private void resumeFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumeFolderButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resumeFolderButtonActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_submitButtonActionPerformed

    private void resumeFolderButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resumeFolderButtonMouseClicked
        // TODO add your handling code here:
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
    }//GEN-LAST:event_resumeFolderButtonMouseClicked

    private void outputFolderFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputFolderFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outputFolderFieldActionPerformed

    private void outputFolderButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputFolderButtonMouseClicked
        // TODO add your handling code here:
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
    }//GEN-LAST:event_outputFolderButtonMouseClicked

    private void outputFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputFolderButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_outputFolderButtonActionPerformed

    private void keywordsFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keywordsFieldKeyTyped

        String query = keywordsField.getText();
        if(query.isEmpty() || !validate(query)){
            submitButton.setEnabled(false);
        } else {
            submitButton.setEnabled(true);
        }
    }//GEN-LAST:event_keywordsFieldKeyTyped

    private void submitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_submitButtonMouseClicked
        // TODO add your handling code here:
        timer = new Timer(100, (ActionEvent event) -> {
            pBar.setValue(parseControl.getPercent());
            String output = parseControl.getOutput();
            if(parseControl.getPercent() == BAR_MAX) {
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                submitButton.setEnabled(true);
                cancelButton.setEnabled(false);
                setCursor(null);
                pBar.setValue(BAR_MAX);
            }
        });
        
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
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
    }//GEN-LAST:event_submitButtonMouseClicked

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        // TODO add your handling code here:
        timer.stop();
        submitButton.setEnabled(true);
        cancelButton.setEnabled(false);
        setCursor(null);
        pBar.setValue(BAR_MIN);
        if (t != null) {
            t.interrupt();
        }
    }//GEN-LAST:event_cancelButtonMouseClicked

    private static boolean validate(String query) {
        Pattern p = Pattern.compile("[\\\\\\\\!`'$%&*,/:;<=>?@\\[\\]\\\\[\\\\]^_{|}~0-9]+");
        Matcher m = p.matcher(query);
        return !m.find();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LOGGER.info(ex.toString());
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button cancelButton;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField keywordsField;
    private javax.swing.JLabel keywordsLabel;
    private javax.swing.JButton outputFolderButton;
    private javax.swing.JTextField outputFolderField;
    private javax.swing.JLabel outputFolderLabel;
    private javax.swing.JProgressBar pBar;
    private javax.swing.JPanel panelOverlay;
    private javax.swing.JButton resumeFolderButton;
    private javax.swing.JTextField resumeFolderField;
    private javax.swing.JLabel resumeFolderLabel;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JSeparator separator3;
    private java.awt.Button submitButton;
    // End of variables declaration//GEN-END:variables
}
