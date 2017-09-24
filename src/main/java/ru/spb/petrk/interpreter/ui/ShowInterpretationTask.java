/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.ui;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;
import ru.spb.petrk.interpreter.Interpreter;
import ru.spb.petrk.interpreter.InterpreterError;
import ru.spb.petrk.interpreter.InterpreterListener;
import ru.spb.petrk.interpreter.astbased.ASTInterpreter;

/**
 *
 * @author petrk
 */
public class ShowInterpretationTask implements Runnable {
    
    private final static Logger LOG = Logger.getLogger(ShowInterpretationTask.class.getName());
    
    private final String text;
    
    private final RSyntaxTextArea input;
    
    // Modifications of a text in the output are made in the single 
    // thread (InterpreterController's worker). Threrefore synchronization
    // is not required
    private final JTextArea output; 
    
    private volatile Future<?> taskFuture;

    public ShowInterpretationTask(RSyntaxTextArea input, JTextArea output) {
        this.text = input.getText();
        this.input = input;
        this.output = output;
        assert !output.isEditable() : "If output is editable, it must be properly synchronized!";
    }
    
    public void setTaskFuture(Future<?> taskFuture) {
        this.taskFuture = taskFuture;
    }

    @Override
    public void run() {
        output.setText("");
        clearErrors(input);
        final PrintStream out = new PrintStream(new TextAreaOutputStream());
        Interpreter interpreter = new ASTInterpreter();
        interpreter.interpret(text, new InterpreterListener() {
            
            private boolean hadOutput = false;
            
            @Override
            public void onOut(String msg) {
                hadOutput = true;
                out.print(msg);
            }

            @Override
            public void onError(InterpreterError error) {
                if (hadOutput) {
                    out.println();
                    hadOutput = false;
                }
                out.println(error.toString());
                highlightError(input, error);
            }
        });
    }
    
    private static void clearErrors(RSyntaxTextArea pane) {
        SwingUtilities.invokeLater(() -> {
            pane.getHighlighter().removeAllHighlights();
        });
    }
    
    private static void highlightError(RSyntaxTextArea pane, InterpreterError error) {
        if (error.offendingLength > 0) {
            SwingUtilities.invokeLater(() -> {
                    try {
                        pane.getHighlighter().addHighlight(
                                error.offendingStartOffset, 
                                error.offendingStartOffset + error.offendingLength, 
                                new SquiggleUnderlineHighlightPainter(Color.RED)
                        );
                    } catch (BadLocationException ex) {
                        LOG.log(Level.FINE, ex.getMessage(), ex);
                    }
            });
        }
    }
    
    // OutputStream adapter for JTextArea
    private class TextAreaOutputStream extends OutputStream {
    
        private final static int OUTPUT_THRESHOLD = 4 * 1024; // 4Kb 

        private int charsWritten = 0;
        
        @Override
        public void write(int b) throws IOException {
            if (charsWritten == -1) {
                return; // task was cancelled
            }
            if (charsWritten == Integer.MAX_VALUE) {
                charsWritten = 0; // corner case, reset counter
            }
            if (charsWritten > OUTPUT_THRESHOLD && taskFuture != null) {
                Component topComponent = output;
                while (topComponent.getParent() != null) {
                    topComponent = topComponent.getParent();
                }
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                        topComponent, 
                        "Interpretation prints a lot of data. Do you want to cancel interpretation?"
                )) {
                    if (!taskFuture.isCancelled()) {
                        taskFuture.cancel(true); // cancel our task
                    }
                    charsWritten = -1; // prevent potential future writings
                    return;
                } else {
                    charsWritten = 0; // reset counter and continue
                }
            }
            ++charsWritten;
            output.append(String.valueOf((char) b));
            output.setCaretPosition(output.getDocument().getLength());
        }
    }
}
