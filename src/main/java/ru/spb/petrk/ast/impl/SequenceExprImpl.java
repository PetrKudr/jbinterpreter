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
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public class SequenceExprImpl extends LeftRightOffsetableASTBase implements SequenceExpr {
    
    private final Expr LHS;
    
    private final Expr RHS;

    public SequenceExprImpl(Expr LHS, Expr RHS, Position left, Position right) {
        super(left, right);
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
    public Type getType() {
        return new SequenceTypeImpl(IntegerTypeImpl.INSTANCE);
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(LHS, RHS);
    }
}
