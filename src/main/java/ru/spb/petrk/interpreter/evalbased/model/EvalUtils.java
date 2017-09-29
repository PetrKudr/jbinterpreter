/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.ast.*;

/**
 *
 * @author petrk
 */
public final class EvalUtils {
    
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
