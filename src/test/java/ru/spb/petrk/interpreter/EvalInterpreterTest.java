/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import ru.spb.petrk.interpreter.evalbased.EvalInterpreter;

/**
 *
 * @author petrk
 */
public class EvalInterpreterTest extends InterpreterTest {

    @Override
    protected Interpreter createInterpreter() {
        return new EvalInterpreter();
    }
}
