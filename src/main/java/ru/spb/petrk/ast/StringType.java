/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 * Represents string type.
 *
 * @author petrk
 */
public interface StringType extends Type {
    
    public static String thisTypeName() {
        return "String";
    }
}
