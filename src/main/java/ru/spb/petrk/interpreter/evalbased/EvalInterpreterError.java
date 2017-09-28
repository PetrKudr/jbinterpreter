/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased;

import ru.spb.petrk.ast.ASTUtils;
import ru.spb.petrk.interpreter.InterpreterError;

/**
 *
 * @author petrk
 */
/*package*/ class EvalInterpreterError implements InterpreterError {

    private final String message;
    
    private final int offendingStartOffset;
    
    private final int offendingStartLine;
    
    private final int offendingStartColumn;
    
    private final int offendingLength;

    public EvalInterpreterError(String message, int offendingStartOffset, int offendingStartLine, int offendingStartColumn, int offendingLength) {
        this.message = message;
        this.offendingStartOffset = offendingStartOffset;
        this.offendingStartLine = offendingStartLine;
        this.offendingStartColumn = offendingStartColumn;
        this.offendingLength = offendingLength;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getOffendingStartOffset() {
        return offendingStartOffset;
    }

    @Override
    public int getOffendingStartLine() {
        return offendingStartLine;
    }

    @Override
    public int getOffendingStartColumn() {
        return offendingStartColumn;
    }

    @Override
    public int getOffendingLength() {
        return offendingLength;
    }
    
    @Override
    public String toString() {
        return ASTUtils.position(offendingStartLine, offendingStartColumn) + message; 
    }
}
