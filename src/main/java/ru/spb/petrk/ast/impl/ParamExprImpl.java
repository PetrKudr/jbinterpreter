/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.ParamExpr;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public final class ParamExprImpl extends LeftRightOffsetableASTBase implements ParamExpr {
    
    private final String name;
    
    private final Type type;

    public ParamExprImpl(String name, Type type, Position left, Position right) {
        super(left, right);
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }
    
    @Override
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
