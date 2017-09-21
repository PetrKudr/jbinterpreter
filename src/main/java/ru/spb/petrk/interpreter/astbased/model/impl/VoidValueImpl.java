/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import ru.spb.petrk.interpreter.astbased.model.VoidValue;

/**
 *
 * @author petrk
 */
public final class VoidValueImpl implements VoidValue {
    
    public static final VoidValue INSTANCE = new VoidValueImpl();

    private VoidValueImpl() {}

    @Override
    public String toString() {
        return "void";
    }
}
