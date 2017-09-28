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
/*package*/ final class MapLambdaSymTabImpl implements SymTab {
    
    private final ParamExpr param;
    
    private final Evaluator eval;

    public MapLambdaSymTabImpl(LambdaExpr lambda, Evaluator eval) {
        assert lambda.getParams().size() == 1;
        this.param = lambda.getParams().get(0);
        this.eval = eval;
    }

    @Override
    public Evaluator getEval(RefExpr expr) {
        if (expr.getName().equals(param.getName())) {
            return eval;
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
