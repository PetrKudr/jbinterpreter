/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased.model.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.interpreter.astbased.ASTInterpreter;
import ru.spb.petrk.interpreter.astbased.model.SequenceValue;
import ru.spb.petrk.interpreter.astbased.model.Value;

/**
 *
 * @author petrk
 */
public final class MapSequenceValue implements SequenceValue {
    
    private final AtomicBoolean canceller;
    
    private final SequenceValue sequence;
    
    private final LambdaExpr lambda;

    public MapSequenceValue(AtomicBoolean canceller, SequenceValue sequence, LambdaExpr lambda) {
        this.canceller = canceller;
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
                return new ASTInterpreter().interpret(
                        lambda.getBody(), 
                        symTabOf(orig.next()),
                        canceller
                );
            }
        };
    }

    @Override
    public Stream<Value> stream() {
        return sequence.stream().map(val -> new ASTInterpreter().interpret(
                lambda.getBody(),
                symTabOf(val),
                canceller
        ));
    }

    @Override
    public String toString() {
        return asString();
    }
    
    private Map<String, Value> symTabOf(Value val) {
        final Map<String, Value> symTab = new HashMap<>(1);
        symTab.put(lambda.getParams().get(0).getName(), val);
        return symTab;
    }
}
