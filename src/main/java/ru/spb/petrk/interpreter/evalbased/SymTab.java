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
import ru.spb.petrk.interpreter.InterpreterError;
import static ru.spb.petrk.interpreter.evalbased.EvalInterpreter.reportError;
import static ru.spb.petrk.interpreter.evalbased.EvalInterpreter.reportMismatchedTypes;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;

/**
 *
 * @author petrk
 */
public final class SymTab {
    
    private final Map<String, Symbol> table = new HashMap<>(2);
    
    public static SymTab of(LambdaExpr lambda, Evaluator ... evals) {
        assert lambda.getParams().size() == evals.length;
        SymTab st = new SymTab();
        for (int i = 0; i < lambda.getParams().size(); ++i) {
            st.putSym(lambda.getParams().get(i), evals[i]);
        }
        return st;
    } 
    
    public Evaluator getEval(RefExpr expr) {
        Symbol sym = table.get(expr.getName());
        return sym != null && sym.offset < expr.getStart().getOffset() ? sym.eval : null;
    }
    
    public <T extends Evaluator> T getEval(Class<T> cls, RefExpr expr) {
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
    
    public void putSym(VarDeclStmt varDecl, Evaluator eval) {
        putSym(varDecl.getName(), varDecl, eval);
    } 
    
    public void putSym(ParamExpr paramDecl, Evaluator eval) {
        putSym(paramDecl.getName(), paramDecl, eval);
    }
    
    private void putSym(String name, AST decl, Evaluator eval) {
        if (table.containsKey(name)) {
            throw new EvalInterpreterException(reportError(
                    "redeclaration of variable " + "\"" + name + "\"", 
                    decl
            ));
        }
        table.put(name, new Symbol(name, decl.getStart().getOffset(), eval));
    }
    
    private static final class Symbol {
        
        public final String name;
        
        public final int offset;
        
        public final Evaluator eval;

        public Symbol(String name, int offset, Evaluator eval) {
            this.name = name;
            this.offset = offset;
            this.eval = eval;
        }
    }
}
