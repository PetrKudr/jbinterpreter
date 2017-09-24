/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import ru.spb.petrk.ast.ASTUtils;

/**
 *
 * @author petrk
 */
public final class InterpreterError {
    
    public final String message;
    
    public final int offendingStartOffset;
    
    public final int offendingStartLine;
    
    public final int offendingStartColumn;
    
    public final int offendingLength;

    public InterpreterError(String message, int offendingStartOffset, 
            int offendingStartLine, int offendingStartColumn, 
            int offendingLength) {
        this.message = message;
        this.offendingStartOffset = offendingStartOffset;
        this.offendingStartLine = offendingStartLine;
        this.offendingStartColumn = offendingStartColumn;
        this.offendingLength = offendingLength;
    }
    
    @Override
    public String toString() {
        return ASTUtils.position(offendingStartLine, offendingStartColumn) + message; 
    }
}
