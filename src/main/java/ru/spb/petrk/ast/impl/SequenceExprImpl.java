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
import ru.spb.petrk.ast.SequenceExpr;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class SequenceExprImpl implements SequenceExpr {
    
    private final Expr LHS;
    
    private final Expr RHS;

    public SequenceExprImpl(Expr LHS, Expr RHS) {
        this.LHS = LHS;
        this.RHS = RHS;
    }

    @Override
    public Expr getLHS() {
        return LHS;
    }

    @Override
    public Expr getRHS() {
        return RHS;
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(LHS, RHS);
    }
}
