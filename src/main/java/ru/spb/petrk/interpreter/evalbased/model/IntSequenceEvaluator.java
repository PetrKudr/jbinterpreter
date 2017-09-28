/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import java.util.stream.IntStream;

/**
 *
 * @author petrk
 */
public interface IntSequenceEvaluator extends NumberSequenceEvaluator<IntStream> {

    @Override
    default IntSequenceEvaluator asIntSequence() {
        return this;
    }
}
