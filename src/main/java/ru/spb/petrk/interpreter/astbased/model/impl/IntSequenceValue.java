/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import java.util.Iterator;
import ru.spb.petrk.interpreter.astbased.model.IntValue;
import ru.spb.petrk.interpreter.astbased.model.SequenceValue;
import ru.spb.petrk.interpreter.astbased.model.Value;

/**
 *
 * @author petrk
 */
public final class IntSequenceValue implements SequenceValue {
    
    final IntValue left;
    
    final IntValue right;

    public IntSequenceValue(IntValue left, IntValue right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Iterator<Value> values() {
        final int from = left.value();
        final int to = right.value();
        return new Iterator<Value>() {
            int current = from;

            @Override
            public boolean hasNext() {
                return current <= to;
            }

            @Override
            public IntValue next() {
                return new IntValueImpl(current++);
            }
        };
    }

    @Override
    public String toString() {
        return asString();
    }
    
}
