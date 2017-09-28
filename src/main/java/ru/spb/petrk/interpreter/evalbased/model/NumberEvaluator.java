/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

/**
 *
 * @author petrk
 */
public interface NumberEvaluator extends Evaluator {

    FloatEvaluator asFloat();
    
    IntEvaluator asInt();
}
