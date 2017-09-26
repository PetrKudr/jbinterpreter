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
import ru.spb.petrk.ast.Type;
import ru.spb.petrk.ast.UnaryOperator;

/**
 *
 * @author petrk
 */
public class UnaryOperatorImpl extends LeftOffsetableASTBase implements UnaryOperator {
    
    private final boolean minus;
    
    private final Expr expr;

    public UnaryOperatorImpl(boolean minus, Expr expr, Position left) {
        super(left);
        this.minus = minus;
        this.expr = expr;
    }

    @Override
    public boolean isMinus() {
        return minus;
    }

    @Override
    public Expr getExpr() {
        return expr;
    }

    @Override
    public Type getType() {
        return expr.getType();
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(expr);
    }

    @Override
    public Position getStop() {
        return expr.getStop();
    }
}
