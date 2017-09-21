/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import ru.spb.petrk.interpreter.astbased.model.StringValue;

/**
 *
 * @author petrk
 */
public final class StringValueImpl implements StringValue {
    
    final String value;

    public StringValueImpl(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
    
}
