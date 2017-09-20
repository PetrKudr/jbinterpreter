/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 *
 * @author petrk
 */
public interface ReduceOperator extends Expr {
    
    Expr getSequence();
    
    Expr getNeutralValue();
    
    /**
     * (a, b) -> expr
     * @return lambda
     */
    LambdaExpr getLambda();
}
