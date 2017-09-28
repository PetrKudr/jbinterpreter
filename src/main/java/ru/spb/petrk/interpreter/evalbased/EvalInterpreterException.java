/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased;

import ru.spb.petrk.interpreter.InterpreterError;

/**
 *
 * @author petrk
 */
public class EvalInterpreterException extends RuntimeException {
    
    private final InterpreterError error;

    public EvalInterpreterException(InterpreterError error) {
        super(error.getMessage());
        this.error = error;
    }

    public InterpreterError getError() {
        return error;
    }
}
