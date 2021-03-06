/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.StringLiteral;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public class StringLiteralImpl extends LeftRightOffsetableASTBase implements StringLiteral {
    
    private final String string;

    public StringLiteralImpl(String string, Position left, Position right) {
        super(left, right);
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public Type getType() {
        return StringTypeImpl.INSTANCE;
    }

    @Override
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
