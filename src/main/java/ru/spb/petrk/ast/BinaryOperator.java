/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents ['+', '-', '*', '/', '^'] operators.
 *
 * @author petrk
 */
public interface BinaryOperator extends Expr {
    
    Expr getLHS();
    
    Expr getRHS();
    
    OpKind getOperation();
    
    public enum OpKind {
        PLUS, 
        MINUS, 
        MULTIPLY, 
        DIVIDE, 
        POWER
    }
}
