/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.Expr;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class LambdaExprImpl implements LambdaExpr {
    
    private final List<String> parameters;
    
    private Expr body;

    public LambdaExprImpl(List<String> parameters, Expr body) {
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
    public List<Stmt> getChildren() {
        return Arrays.asList(body);
    }
}
