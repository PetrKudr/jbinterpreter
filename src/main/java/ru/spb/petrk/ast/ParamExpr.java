/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents a parameter of lambda function.
 *
 * @author petrk
 */
public interface ParamExpr extends Expr {
    
    String getName();
}
