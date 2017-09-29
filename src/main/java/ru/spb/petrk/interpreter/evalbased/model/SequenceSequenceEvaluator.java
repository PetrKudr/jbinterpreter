/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import java.util.stream.Stream;
import ru.spb.petrk.interpreter.evalbased.SymTab;

/**
 * Represents evaluator for a sequence of sequences.
 *
 * @author petrk
 */
public interface SequenceSequenceEvaluator extends SequenceEvaluator<Stream<SequenceEvaluator>> {
    
    @Override
    SequenceSequenceEvaluator binded(SymTab st);
}
