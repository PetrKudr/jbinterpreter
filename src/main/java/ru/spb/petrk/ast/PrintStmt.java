/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents print statement.
 *
 * @author petrk
 */
public interface PrintStmt extends Stmt {
    
    StringLiteral getMessage();
}
