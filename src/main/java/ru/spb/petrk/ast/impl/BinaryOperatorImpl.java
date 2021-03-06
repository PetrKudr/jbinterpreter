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
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public class BinaryOperatorImpl implements BinaryOperator {
    
    private final OpKind kind;
    
    private final Expr LHS;
    
    private final Expr RHS;
    
    private final Type type;

    public BinaryOperatorImpl(OpKind kind, Expr LHS, Expr RHS, Type type) {
        this.kind = kind;
        this.LHS = LHS;
        this.RHS = RHS;
        this.type = type;
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

    @Override
    public Position getStart() {
        return LHS.getStart();
    }

    @Override
    public Position getStop() {
        return RHS.getStop();
    }

    @Override
    public Type getType() {
        return type;
    }
}
