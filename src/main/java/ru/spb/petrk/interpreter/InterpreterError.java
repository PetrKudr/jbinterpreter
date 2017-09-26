/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;


/**
 * Basic information about error happened during interpretation.
 *
 * @author petrk
 */
public interface InterpreterError {
    
    String getMessage();
    
    int getOffendingStartOffset();
    
    int getOffendingStartLine();
    
    int getOffendingStartColumn();
    
    int getOffendingLength();
}
