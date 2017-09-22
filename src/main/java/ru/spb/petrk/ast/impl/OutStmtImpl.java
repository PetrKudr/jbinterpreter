/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.Expr;
import ru.spb.petrk.ast.OutStmt;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class OutStmtImpl extends OffsetableAST implements OutStmt {
    
    private final Expr expr;

    public OutStmtImpl(Expr expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Expr getExpression() {
        return expr;
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(expr);
    }
}
