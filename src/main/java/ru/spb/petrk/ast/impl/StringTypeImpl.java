/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import static ru.spb.petrk.ast.ASTKindUtils.isStringType;
import ru.spb.petrk.ast.StringType;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public final class StringTypeImpl implements StringType {
    
    public final static StringTypeImpl INSTANCE = new StringTypeImpl();
    
    private StringTypeImpl() {}
    
    @Override
    public boolean isCompatibleWith(Type other) {
        return isStringType(other);
    }

    @Override
    public Type common(Type other) {
        assert isCompatibleWith(other);
        return this;
    }
}
