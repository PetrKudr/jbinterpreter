/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.interpreter.evalbased.SymTab;


/**
 *
 * @author petrk
 */
public interface FloatEvaluator extends NumberEvaluator {

    double value(SymTab symTab);

    @Override
    public default FloatEvaluator asFloat() {
        return this;
    }
}