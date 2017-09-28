/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.interpreter.evalbased.model.impl.ConstFloatEvaluatorImpl;
import ru.spb.petrk.interpreter.evalbased.model.impl.ConstIntEvaluatorImpl;

/**
 *
 * @author petrk
 */
public final class EvalKindUtils {
    
    public static boolean isEval(Object obj) {
        return obj instanceof Evaluator;
    }
    
    public static boolean isNumberEval(Evaluator eval) {
        return eval instanceof NumberEvaluator;
    }
    
    public static boolean isIntEval(Evaluator eval) {
        return eval instanceof IntEvaluator;
    }
    
    public static boolean isConstIntEval(Evaluator eval) {
        return eval instanceof ConstIntEvaluatorImpl;
    }
    
    public static boolean isFloatEval(Evaluator eval) {
        return eval instanceof FloatEvaluator;
    }
    
    public static boolean isConstFloatEval(Evaluator eval) {
        return eval instanceof ConstFloatEvaluatorImpl;
    }
    
    public static boolean isSequenceEval(Evaluator eval) {
        return eval instanceof SequenceEvaluator;
    }
    
    public static boolean isNumberSequenceEval(Evaluator eval) {
        return eval instanceof NumberSequenceEvaluator;
    }
    
    public static boolean isIntSequenceEval(Evaluator eval) {
        return eval instanceof IntSequenceEvaluator;
    }
    
    public static boolean isFloatSequenceEval(Evaluator eval) {
        return eval instanceof FloatSequenceEvaluator;
    }
    
    public static boolean isSequenceSequenceEval(Evaluator eval) {
        return eval instanceof SequenceSequenceEvaluator;
    }
}
