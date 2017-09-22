/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author petrk
 */
public class JInterpreter extends javax.swing.JFrame {

    private final InterpreterController controller = new InterpreterController();
    
    private final UndoManager editorUndoManager = new UndoManager();
    
    private final JFileChooser fileChooser = new JFileChooser();
    
    private File openedFile = null;
    
    /**
     * Creates new form JInterpreter
     */
    public JInterpreter() {
        initComponents();
        
        // Set undo/redo
        editorArea.getDocument().addUndoableEditListener(editorUndoManager);
        InputMap im = editorArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = editorArea.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

        am.put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorUndoManager.canUndo()) {
                    editorUndoManager.undo();
                }
            }
        });
        am.put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editorUndoManager.canRedo()) {
                    editorUndoManager.redo();
                }
            }
        });
        
        // Set interpretation
        editorArea.getDocument().addDocumentListener(new DocumentListener() {
            
            private void onUpdate(DocumentEvent e) {
                controller.post(new ShowInterpretationTask(editorArea, outputArea));
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                onUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Do nothing
            }
        });
        
        editorArea.addCaretListener((CaretEvent e) -> {
            try {
                int caretPos = e.getDot();
                int line = editorArea.getLineOfOffset(caretPos);
                int column = caretPos - editorArea.getLineStartOffset(line);
                editorPositionLabel.setText(String.format("%d:%d", line + 1, column + 1));
            } catch (BadLocationException ex) {
                editorPositionLabel.setText("bad location");
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        editorArea = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        statusBarPanel = new javax.swing.JPanel();
        editorPositionLabel = new javax.swing.JLabel();
        mainMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        miOpenFile = new javax.swing.JMenuItem();
        miSaveFile = new javax.swing.JMenuItem();
        miSaveFileAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miExit = new javax.swing.JMenuItem();
        menuExamples = new javax.swing.JMenu();
        miPi = new javax.swing.JMenuItem();
        miSeqOfSeq = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerLocation(340);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(58, 8));

        editorArea.setColumns(20);
        editorArea.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        editorArea.setRows(5);
        jScrollPane3.setViewportView(editorArea);

        jSplitPane1.setLeftComponent(jScrollPane3);

        outputArea.setEditable(false);
        outputArea.setBackground(new java.awt.Color(206, 196, 187));
        outputArea.setColumns(20);
        outputArea.setForeground(new java.awt.Color(40, 40, 40));
        outputArea.setRows(5);
        jScrollPane4.setViewportView(outputArea);

        jSplitPane1.setRightComponent(jScrollPane4);

        statusBarPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        editorPositionLabel.setText("1:1");

        javax.swing.GroupLayout statusBarPanelLayout = new javax.swing.GroupLayout(statusBarPanel);
        statusBarPanel.setLayout(statusBarPanelLayout);
        statusBarPanelLayout.setHorizontalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(editorPositionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        statusBarPanelLayout.setVerticalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editorPositionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
        );

        menuFile.setText("File");

        miOpenFile.setText("Open File...");
        miOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miOpenFileActionPerformed(evt);
            }
        });
        menuFile.add(miOpenFile);

        miSaveFile.setText("Save");
        miSaveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveFileActionPerformed(evt);
            }
        });
        menuFile.add(miSaveFile);

        miSaveFileAs.setText("Save As...");
        miSaveFileAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveFileAsActionPerformed(evt);
            }
        });
        menuFile.add(miSaveFileAs);
        menuFile.add(jSeparator1);

        miExit.setText("Exit");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        menuFile.add(miExit);

        mainMenuBar.add(menuFile);

        menuExamples.setText("Examples");

        miPi.setText("Pi");
        miPi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miPiActionPerformed(evt);
            }
        });
        menuExamples.add(miPi);

        miSeqOfSeq.setText("Sequence of sequences");
        miSeqOfSeq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSeqOfSeqActionPerformed(evt);
            }
        });
        menuExamples.add(miSeqOfSeq);

        mainMenuBar.add(menuExamples);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(statusBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_miExitActionPerformed

    private void miPiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miPiActionPerformed
        editorArea.setText(
                "var n = 500\n" + 
                "var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))\n" + 
                "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
                "print \"pi = \"\n" +
                "out pi"
        );
    }//GEN-LAST:event_miPiActionPerformed

    private void miOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miOpenFileActionPerformed
        int retVal = fileChooser.showOpenDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.exists()) {
                File oldOpenedFile = openedFile;
                String oldText = editorArea.getText();
                editorArea.setText("");
                openedFile = file;
                try {
                    Files.lines(file.toPath()).forEach(line -> {
                        editorArea.append(line);
                        editorArea.append("\n");
                    });
                } catch (IOException ex) {
                    openedFile = oldOpenedFile;
                    editorArea.setText(oldText);
                    JOptionPane.showMessageDialog(this, "Failed to open file: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "File " + file + " doesn't exist");
            }
        }
    }//GEN-LAST:event_miOpenFileActionPerformed

    private void miSaveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveFileActionPerformed
        if (openedFile == null) {
            miSaveFileAsActionPerformed(evt);
        } else {
            saveFile(openedFile);
        }
    }//GEN-LAST:event_miSaveFileActionPerformed

    private void miSaveFileAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveFileAsActionPerformed
        int retVal = fileChooser.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            saveFile(file);
        }
    }//GEN-LAST:event_miSaveFileAsActionPerformed

    private void miSeqOfSeqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSeqOfSeqActionPerformed
        editorArea.setText(
                "var seqOfSeq = map({1, 3}, val->{1, val})\n" +
                "print \"Sum(\" \n" +
                "out seqOfSeq\n" +
                "print \") = \"\n" +
                "var seqOfSum = reduce(seqOfSeq, {0, 0}, lSeq rSeq -> {\n" +
                "  reduce(lSeq, 0, l r -> l + r) + reduce(rSeq, 0, l r -> l + r),\n" +
                "  reduce(lSeq, 0, l r -> l + r) + reduce(rSeq, 0, l r -> l + r)\n" +
                "})\n" +
                "out reduce(seqOfSum, 0, l r -> l + r)"
        );
    }//GEN-LAST:event_miSeqOfSeqActionPerformed

    private void saveFile(File toFile) {
        File oldOpenedFile = openedFile;
        openedFile = toFile;
        try {
            Files.write(
                    toFile.toPath(),
                    editorArea.getText().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.CREATE
            );
        } catch (IOException ex) {
            openedFile = oldOpenedFile;
            JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage());
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JInterpreter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JInterpreter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JInterpreter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JInterpreter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JInterpreter().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea editorArea;
    private javax.swing.JLabel editorPositionLabel;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenu menuExamples;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miOpenFile;
    private javax.swing.JMenuItem miPi;
    private javax.swing.JMenuItem miSaveFile;
    private javax.swing.JMenuItem miSaveFileAs;
    private javax.swing.JMenuItem miSeqOfSeq;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JPanel statusBarPanel;
    // End of variables declaration//GEN-END:variables
}
