/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import java.util.Iterator;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
import ru.spb.petrk.interpreter.evalbased.SymTab;

/**
 *
 * @author petrk
 */
public interface SequenceEvaluator<S extends BaseStream> extends Evaluator {

    S stream(SymTab symTab);

    @Override
    default String asString(SymTab symTab) {
        boolean first = true;
        Iterator<?> valsIter = stream(symTab).iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while (valsIter.hasNext()) {
            if (!first) {
                sb.append(", ");
            }
            Object next = valsIter.next();
            if (EvalKindUtils.isEval(next)) {
                sb.append(((Evaluator) next).asString(symTab));
            } else {
                sb.append(next);
            }
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
