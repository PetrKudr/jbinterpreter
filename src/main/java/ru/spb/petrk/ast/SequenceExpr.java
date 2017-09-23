/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents declaration of sequence.
 *
 * @author petrk
 */
public interface SequenceExpr extends Expr {
    
    /**
     * 
     * @return left border of this sequence
     */
    Expr getLHS();
    
    /**
     * 
     * @return right border of this sequence
     */
    Expr getRHS();
}
