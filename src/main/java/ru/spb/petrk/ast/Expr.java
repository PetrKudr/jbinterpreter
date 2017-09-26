/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents an AST node that corresponds to the expression rule in the language.
 * 
 * @author petrk
 */
public interface Expr extends AST {
    
    Type getType();
}
