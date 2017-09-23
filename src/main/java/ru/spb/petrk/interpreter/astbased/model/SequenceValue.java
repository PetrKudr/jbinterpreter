/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 *
 * @author petrk
 */
public interface SequenceValue extends Value, Iterable<Value> {
    
    Stream<Value> stream();

    default String asString() {
        boolean first = true;
        Iterator<Value> valsIter = iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while (valsIter.hasNext()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(valsIter.next());
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
