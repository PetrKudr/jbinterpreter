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
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;

/**
 *
 * @author petrk
 */
/*package*/ final class SymTabImpl implements SymTab {
    
    private final Map<String, Symbol> table = new HashMap<>(2);
    
    @Override
    public Evaluator getEval(RefExpr expr) {
        Symbol sym = table.get(expr.getName());
        return sym != null && sym.offset < expr.getStart().getOffset() ? sym.eval : null;
    }
    
    @Override
    public void putSym(VarDeclStmt varDecl, Evaluator eval) {
        putSym(varDecl.getName(), varDecl, eval);
    } 
    
    @Override
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
        table.put(name, new Symbol(decl.getStart().getOffset(), eval));
    }
    
    private static final class Symbol {
        
        public final int offset;
        
        public final Evaluator eval;

        public Symbol(int offset, Evaluator eval) {
            this.offset = offset;
            this.eval = eval;
        }
    }
}
