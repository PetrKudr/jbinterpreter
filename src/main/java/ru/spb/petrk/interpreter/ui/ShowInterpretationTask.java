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
import java.util.concurrent.atomic.AtomicBoolean;
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

/**
 *
 * @author petrk
 */
public class ShowInterpretationTask implements Runnable {
    
    private final static int OUTPUT_THRESHOLD = 8 * 1024; // 8Kb 
    
    private final static int OUTPUT_CANCELLED = -1;
    
    private final static Logger LOG = Logger.getLogger(ShowInterpretationTask.class.getName());
    
    private final AtomicBoolean canceller = new AtomicBoolean();
    
    private final String text;
    
    private final RSyntaxTextArea input;
    
    private final JTextArea output; 
    
    private final Interpreter interpreter;

    private int charsWritten = 0;

    public ShowInterpretationTask(RSyntaxTextArea input, JTextArea output, Interpreter interpreter) {
        this.text = input.getText();
        this.input = input;
        this.output = output;
        this.interpreter = interpreter;
    }

    public void cancel() {
        canceller.set(true);
    }

    @Override
    public void run() {
        boolean finishedSuccessfully = false;
        long startTime = 0;
        try {
            clearOutput(output);
            clearErrors(input);
            final PrintStream out = new PrintStream(new TextAreaOutputStream());
            startTime =  System.currentTimeMillis();
            finishedSuccessfully = interpreter.interpret(text, new InterpreterListener() {

                private boolean hadOutput = false;

                @Override
                public void onOut(String msg) {
                    if (checkSizeOfOutput(msg.length())) {
                        SwingUtilities.invokeLater(() -> {
                            hadOutput = true;
                            out.print(msg);
                        });
                    }
                }

                @Override
                public void onError(InterpreterError error) {
                    if (checkSizeOfOutput(error.getMessage().length())) {
                        SwingUtilities.invokeLater(() -> {
                            if (hadOutput) {
                                out.println();
                                hadOutput = false;
                            }
                            out.println(error.toString());
                            highlightError(input, error);
                        });
                    }
                }
            }, canceller);
        } catch (Throwable thr) {
            LOG.log(Level.INFO, thr.getMessage(), thr);
        }
        
        final boolean finalFinishedSuccessfully = finishedSuccessfully;
        if (finalFinishedSuccessfully) {
            final long finalStartTime = startTime;
            final long finalStopTime = System.currentTimeMillis();
            SwingUtilities.invokeLater(() -> {
                if (finalStopTime >= finalStartTime) {
                    output.append("\n\n");
                    output.append("Interpretation is finished [" + String.valueOf(finalStopTime - finalStartTime) + " ms]");
                } else {
                    output.append("\n\n");
                    output.append("Interpretation is finished [Failed to measure time]");
                }
            });
        }
    }
    
    
    private static void clearOutput(JTextArea output) {
        SwingUtilities.invokeLater(() -> {
            output.setText("");
        });
    }
    
    private static void clearErrors(RSyntaxTextArea pane) {
        SwingUtilities.invokeLater(() -> {
            pane.getHighlighter().removeAllHighlights();
        });
    }
    
    private static void highlightError(RSyntaxTextArea pane, InterpreterError error) {
        assert SwingUtilities.isEventDispatchThread();
        if (error.getOffendingLength() > 0) {
            final int textEnd = pane.getDocument().getLength();
            try {
                pane.getHighlighter().addHighlight(
                        Math.min(textEnd, error.getOffendingStartOffset()), 
                        Math.min(textEnd, error.getOffendingStartOffset() + error.getOffendingLength()), 
                        new SquiggleUnderlineHighlightPainter(Color.RED)
                );
            } catch (BadLocationException ex) {
                LOG.log(Level.FINE, ex.getMessage(), ex);
            }
        }
    }
    
    private boolean checkSizeOfOutput(int added) {
        if (OUTPUT_THRESHOLD == OUTPUT_CANCELLED) {
            return true; // threshold is disabled
        }
        if (charsWritten == OUTPUT_CANCELLED) {
            return false; // task was cancelled
        }
        charsWritten += added;
        if (charsWritten > OUTPUT_THRESHOLD) {
            Component topComponent = output;
            while (topComponent.getParent() != null) {
                topComponent = topComponent.getParent();
            }
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                    topComponent, 
                    "Interpretation prints a lot of data. Do you want to cancel interpretation?"
            )) {
                canceller.set(true);
                charsWritten = OUTPUT_CANCELLED; // prevent potential future writings
                return false;
            } else {
                charsWritten = 0; // reset counter and continue
            }
        }
        return true;
    }
    
    // OutputStream adapter for JTextArea
    private class TextAreaOutputStream extends OutputStream {
        
        @Override
        public void write(int b) throws IOException {
            assert SwingUtilities.isEventDispatchThread();
            output.append(String.valueOf((char) b));
            output.setCaretPosition(output.getDocument().getLength());
        }
    }
}
