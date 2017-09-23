/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents map operator.
 *
 * @author petrk
 */
public interface MapOperator extends Expr {
    
    /**
     * @return original sequence
     */
    Expr getSequence();
    
    /**
     * Returns a function to apply to each element.
     * 
     * @return lambda of type (a) -> expr
     */
    LambdaExpr getLambda();
}
