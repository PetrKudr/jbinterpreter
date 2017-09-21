/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import ru.spb.petrk.interpreter.astbased.model.IntValue;

/**
 *
 * @author petrk
 */
public final class IntValueImpl implements IntValue {
    
    final int value;

    @Override
    public int value() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    public IntValueImpl(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
