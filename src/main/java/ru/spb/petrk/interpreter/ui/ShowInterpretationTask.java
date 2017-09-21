/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.ui;

import java.io.PrintStream;
import javax.swing.JTextArea;
import ru.spb.petrk.interpreter.Interpreter;
import ru.spb.petrk.interpreter.astbased.ASTBasedInterpreter;

/**
 *
 * @author petrk
 */
public class ShowInterpretationTask implements Runnable {
    
    public final String input;
    
    public final JTextArea output;

    public ShowInterpretationTask(JTextArea input, JTextArea output) {
        this.input = input.getText();
        this.output = output;
    }

    @Override
    public void run() {
        output.setText("");
        PrintStream out = new PrintStream(new TextAreaOutputStream(output));
        Interpreter interpreter = new ASTBasedInterpreter();
        interpreter.interpret(input, out, out);
    }
}
