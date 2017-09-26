/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.FloatingLiteral;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public class FloatingLiteralImpl extends LeftRightOffsetableASTBase implements FloatingLiteral {
    
    private final double value;

    public FloatingLiteralImpl(double value, Position left, Position right) {
        super(left, right);
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return FloatingTypeImpl.INSTANCE;
    }

    @Override
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
