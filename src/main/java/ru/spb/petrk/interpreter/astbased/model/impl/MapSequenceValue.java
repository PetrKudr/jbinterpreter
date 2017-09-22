/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.interpreter.astbased.ASTBasedInterpreter;
import ru.spb.petrk.interpreter.astbased.model.SequenceValue;
import ru.spb.petrk.interpreter.astbased.model.Value;

/**
 *
 * @author petrk
 */
public final class MapSequenceValue implements SequenceValue {
    
    final SequenceValue sequence;
    
    final LambdaExpr lambda;

    public MapSequenceValue(SequenceValue sequence, LambdaExpr lambda) {
        this.sequence = sequence;
        this.lambda = lambda;
    }

    @Override
    public Iterator<Value> iterator() {
        final Iterator<Value> orig = sequence.iterator();
        return new Iterator<Value>() {
            @Override
            public boolean hasNext() {
                return orig.hasNext();
            }

            @Override
            public Value next() {
                return new ASTBasedInterpreter().interpret(
                        lambda.getBody(), 
                        symTabOf(orig.next())
                );
            }
        };
    }

    @Override
    public Stream<Value> stream() {
        return sequence.stream().map(val -> new ASTBasedInterpreter().interpret(
                lambda.getBody(),
                symTabOf(val)
        ));
    }

    @Override
    public String toString() {
        return asString();
    }
    
    private Map<String, Value> symTabOf(Value val) {
        final Map<String, Value> symTab = new HashMap<>(1);
        symTab.put(lambda.getParams().get(0), val);
        return symTab;
    }
}
