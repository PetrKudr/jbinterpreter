/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased;

import java.util.HashMap;
import java.util.Map;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.ParamExpr;
import ru.spb.petrk.ast.RefExpr;
import ru.spb.petrk.ast.VarDeclStmt;
import static ru.spb.petrk.interpreter.evalbased.EvalInterpreter.reportError;
import static ru.spb.petrk.interpreter.evalbased.EvalInterpreter.reportMismatchedTypes;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;

/**
 *
 * @author petrk
 */
public interface SymTab {
    
    public static SymTab of(LambdaExpr lambda, Evaluator ... evals) {
        assert lambda.getParams().size() == evals.length;
        switch (evals.length) {
            case 2:
                return new ReduceLambdaSymTabImpl(lambda, evals[0], evals[1]);
            case 1:
                return new MapLambdaSymTabImpl(lambda, evals[0]);
            default:
                throw new AssertionError("Lambda with " + lambda.getParams().size() + " parameters?!");
        }
    } 
    
    /**
     * Returns evaluator for the given reference.
     * 
     * @param expr
     * @return evaluator
     */
    Evaluator getEval(RefExpr expr);
    
    /**
     * Puts evaluator for declared variable.
     * 
     * @param varDecl
     * @param eval 
     */
    void putSym(VarDeclStmt varDecl, Evaluator eval);
    
    /**
     * Puts evaluator for declared parameter.
     * 
     * @param paramDecl
     * @param eval 
     */
    void putSym(ParamExpr paramDecl, Evaluator eval);
    
    /**
     * Returns evaluator of the given type for the given reference.
     * 
     * @param <T>
     * @param cls
     * @param expr
     * @return 
     */
    default <T extends Evaluator> T getEval(Class<T> cls, RefExpr expr) {
        Evaluator eval = getEval(expr);
        if (eval == null) {
            throw new EvalInterpreterException(reportError(
                    "unresolved variable \"" + expr.getName() + "\"", 
                    expr
            ));
        } else if (!cls.isAssignableFrom(eval.getClass())) {
            throw new EvalInterpreterException(reportMismatchedTypes(
                    cls,
                    eval,
                    expr
            ));
        }
        return (T) eval;
    }
}
