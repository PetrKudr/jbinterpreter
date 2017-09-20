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
public interface MapOperator extends Expr {
    
    Expr getSequence();
    
    /**
     * (a) -> expr
     * @return lambda
     */
    LambdaExpr getLambda();
}
