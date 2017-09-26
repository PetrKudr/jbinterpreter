/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents type of an expression.
 *
 * @author petrk
 */
public interface Type {
    
    /**
     * Checks that this type can be implicitly converted to the given type.
     * 
     * @param other type
     * @return true if convertible, false otherwise
     */
    boolean isConvertibleTo(Type other);
}
