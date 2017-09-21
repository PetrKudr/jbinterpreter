/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import ru.spb.petrk.interpreter.astbased.model.FloatingValue;

/**
 *
 * @author petrk
 */
public final class FloatingValueImpl implements FloatingValue {
    
    final double value;

    @Override
    public double value() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    public FloatingValueImpl(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
