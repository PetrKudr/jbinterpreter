/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased;

import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.ParamExpr;
import ru.spb.petrk.ast.RefExpr;
import ru.spb.petrk.ast.VarDeclStmt;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;

/**
 *
 * @author petrk
 */
/*package*/ final class ReduceLambdaSymTabImpl implements SymTab {
    
    private final ParamExpr firstParam;
    
    private final Evaluator firstEvaluator;
    
    private final ParamExpr secondParam;
    
    private final Evaluator secondEvaluator;

    public ReduceLambdaSymTabImpl(LambdaExpr lambda, Evaluator firstEval, Evaluator secondEval) {
        assert lambda.getParams().size() == 2;
        this.firstParam = lambda.getParams().get(0);
        this.firstEvaluator = firstEval;
        this.secondParam = lambda.getParams().get(1);
        this.secondEvaluator = secondEval;
    }

    @Override
    public Evaluator getEval(RefExpr expr) {
        if (expr.getName().equals(firstParam.getName())) {
            return firstEvaluator;
        } else if (expr.getName().equals(secondParam.getName())) {
            return secondEvaluator;
        }
        return null;
    }

    @Override
    public void putSym(VarDeclStmt varDecl, Evaluator eval) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void putSym(ParamExpr paramDecl, Evaluator eval) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
