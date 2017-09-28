/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.Expr;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.ParamExpr;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public class LambdaExprImpl extends LeftOffsetableASTBase implements LambdaExpr {
    
    private final List<ParamExpr> parameters;
    
    private final Expr body;

    public LambdaExprImpl(List<ParamExpr> parameters, Expr body, Position left) {
        super(left);
        this.parameters = Collections.unmodifiableList(parameters);
        this.body = body;
    }

    @Override
    public List<ParamExpr> getParams() {
        return parameters;
    }

    @Override
    public Expr getBody() {
        return body;
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public List<AST> getChildren() {
        List<AST> children = new ArrayList<>(parameters);
        children.add(body);
        return children;
    }

    @Override
    public Position getStop() {
        return body.getStop();
    }
}
