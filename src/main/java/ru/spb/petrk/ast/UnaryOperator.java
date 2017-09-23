/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents unary '+' and '-' operators
 *
 * @author petrk
 */
public interface UnaryOperator extends Expr {
    
    boolean isMinus();
    
    Expr getExpr();
}
