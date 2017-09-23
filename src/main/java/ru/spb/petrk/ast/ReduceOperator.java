/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents reduce operator.
 *
 * @author petrk
 */
public interface ReduceOperator extends Expr {
    
    /**
     * 
     * @return sequence to be reduced
     */
    Expr getSequence();
    
    /**
     * 
     * @return the identity value for the accumulating function
     */
    Expr getNeutralValue();
    
    /**
     * Returns an associative function to combine two parameters.
     * 
     * @return lambda of type (a, b) -> expr
     */
    LambdaExpr getLambda();
}
