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
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.MapOperator;
import ru.spb.petrk.ast.SequenceType;

/**
 *
 * @author petrk
 */
public class MapOperatorImpl extends LeftRightOffsetableASTBase implements MapOperator {
    
    private final Expr sequence;
    
    private final LambdaExpr lambda;

    public MapOperatorImpl(Expr sequence, LambdaExpr lambda, Position left, Position right) {
        super(left, right);
        this.sequence = sequence;
        this.lambda = lambda;
    }

    @Override
    public Expr getSequence() {
        return sequence;
    }

    @Override
    public LambdaExpr getLambda() {
        return lambda;
    }

    @Override
    public SequenceType getType() {
        return new SequenceTypeImpl(lambda.getType());
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(sequence, lambda);
    }
}
