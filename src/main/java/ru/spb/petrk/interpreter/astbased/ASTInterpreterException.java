/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased;

import ru.spb.petrk.interpreter.InterpreterError;

/**
 *
 * @author petrk
 */
public class ASTInterpreterException extends RuntimeException {
    
    private final InterpreterError error;

    public ASTInterpreterException(InterpreterError error) {
        super(error != null ? error.getMessage() : null);
        this.error = error;
    }

    public InterpreterError getError() {
        return error;
    }
}
