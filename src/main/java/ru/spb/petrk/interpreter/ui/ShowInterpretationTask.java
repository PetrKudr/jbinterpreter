/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.ui;

import java.awt.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import ru.spb.petrk.interpreter.Interpreter;
import ru.spb.petrk.interpreter.astbased.ASTInterpreter;

/**
 *
 * @author petrk
 */
public class ShowInterpretationTask implements Runnable {
    
    public final String input;
    
    // Modifications of a text in the output are made in the single 
    // thread (InterpreterController's worker). Threrefore synchronization
    // is not required
    public final JTextArea output; 
    
    private volatile Future<?> taskFuture;

    public ShowInterpretationTask(JTextComponent input, JTextArea output) {
        this.input = input.getText();
        this.output = output;
        assert !output.isEditable() : "If output is editable, it must be properly synchronized!";
    }
    
    public void setTaskFuture(Future<?> taskFuture) {
        this.taskFuture = taskFuture;
    }

    @Override
    public void run() {
        output.setText("");
        PrintStream out = new PrintStream(new TextAreaOutputStream());
        Interpreter interpreter = new ASTInterpreter();
        interpreter.interpret(input, out, out);
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
