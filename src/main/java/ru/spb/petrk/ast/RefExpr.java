/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents reference to a variable.
 *
 * @author petrk
 */
public interface RefExpr extends Expr {
    
    String getName();
}
