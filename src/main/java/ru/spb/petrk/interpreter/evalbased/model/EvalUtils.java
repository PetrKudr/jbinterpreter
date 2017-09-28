/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.ast.*;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.impl.FloatSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.IntSequenceEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.SequenceSequenceEvaluatorImpl;

/**
 *
 * @author petrk
 */
public final class EvalUtils {
    
    public static SequenceEvaluator injectSymTab(SequenceEvaluator eval, SymTab st) {
        if (EvalKindUtils.isIntSequenceEval(eval)) {
            IntSequenceEvaluator intSeqEval = (IntSequenceEvaluator) eval;
            return new IntSequenceEvaluatorImpl((any) -> intSeqEval.stream(st));
        } else if (EvalKindUtils.isIntSequenceEval(eval)) {
            FloatSequenceEvaluator floatSeqEval = (FloatSequenceEvaluator) eval;
            return new FloatSequenceEvaluatorImpl((any) -> floatSeqEval.stream(st));
        } else if (EvalKindUtils.isSequenceSequenceEval(eval)) {
            SequenceSequenceEvaluator seqSeqEval = (SequenceSequenceEvaluator) eval;
            return new SequenceSequenceEvaluatorImpl((any) -> seqSeqEval.stream(st));
        }
        throw new AssertionError("Unexpected kind of evaluator: " + eval.getClass());
    }
    
    public static String getEvalTypeName(Class<? extends Evaluator> cls) {
        if (IntEvaluator.class.isAssignableFrom(cls)) {
            return ASTUtils.getTypeName(IntegerType.class);
        } else if (FloatEvaluator.class.isAssignableFrom(cls)) {
            return ASTUtils.getTypeName(FloatingType.class);
        } else if (NumberEvaluator.class.isAssignableFrom(cls)) {
            return ASTUtils.getTypeName(NumberType.class);
        } else if (IntSequenceEvaluator.class.isAssignableFrom(cls)) {
            return "Sequence of Integer";
        } else if (FloatSequenceEvaluator.class.isAssignableFrom(cls)) {
            return "Sequence of Float";
        } else if (SequenceSequenceEvaluator.class.isAssignableFrom(cls)) {
            return "Sequence of Sequence";
        } else if (SequenceEvaluator.class.isAssignableFrom(cls)) {
            return "Sequence";
        } 
        return "Unexpected eval type!";
    }
    
    public static String getEvalTypeName(Evaluator eval) {
        return getEvalTypeName(eval.getClass());
    }
}
