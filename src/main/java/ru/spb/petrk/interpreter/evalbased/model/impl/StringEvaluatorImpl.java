/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;
import ru.spb.petrk.interpreter.evalbased.model.StringEvaluator;

/**
 *
 * @author petrk
 */
public final class StringEvaluatorImpl implements StringEvaluator {
    
    final String value;

    public StringEvaluatorImpl(String value) {
        this.value = value;
    }

    @Override
    public Evaluator bind(SymTab st) {
        return this;
    }

    @Override
    public String value(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return value;
    }
    
    @Override
    public String asString(SymTab st) {
        return String.valueOf(value(st));
    }
}
