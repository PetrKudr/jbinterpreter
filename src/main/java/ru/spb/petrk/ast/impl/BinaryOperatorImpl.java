/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.BinaryOperator;
import ru.spb.petrk.ast.Expr;

/**
 *
 * @author petrk
 */
public class BinaryOperatorImpl implements BinaryOperator {
    
    private final OpKind kind;
    
    private final Expr LHS;
    
    private final Expr RHS;

    public BinaryOperatorImpl(OpKind kind, Expr LHS, Expr RHS) {
        this.kind = kind;
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
    public OpKind getOperation() {
        return kind;
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(LHS, RHS);
    }
}
