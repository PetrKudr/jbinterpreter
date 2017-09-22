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
import ru.spb.petrk.ast.ReduceOperator;
import ru.spb.petrk.ast.SequenceExpr;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class ReduceOperatorImpl extends OffsetableAST implements ReduceOperator {
    
    private final Expr sequence;
    
    private final Expr neutralValue;
    
    private final LambdaExpr lambda;

    public ReduceOperatorImpl(
            Expr sequence, Expr neutralValue, LambdaExpr lambda, 
            int line, int column
    ) {
        super(line, column);
        this.sequence = sequence;
        this.neutralValue = neutralValue;
        this.lambda = lambda;
    }

    @Override
    public Expr getSequence() {
        return sequence;
    }

    @Override
    public Expr getNeutralValue() {
        return neutralValue;
    }

    @Override
    public LambdaExpr getLambda() {
        return lambda;
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(sequence, neutralValue, lambda);
    }
}
