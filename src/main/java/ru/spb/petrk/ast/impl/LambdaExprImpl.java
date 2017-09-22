/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.Expr;
import ru.spb.petrk.ast.LambdaExpr;

/**
 *
 * @author petrk
 */
public class LambdaExprImpl extends OffsetableAST implements LambdaExpr {
    
    private final List<String> parameters;
    
    private final Expr body;

    public LambdaExprImpl(List<String> parameters, Expr body, int line, int column) {
        super(line, column);
        this.parameters = Collections.unmodifiableList(parameters);
        this.body = body;
    }

    @Override
    public List<String> getParams() {
        return parameters;
    }

    @Override
    public Expr getBody() {
        return body;
    }

    @Override
    public List<AST> getChildren() {
        return Arrays.asList(body);
    }
}
