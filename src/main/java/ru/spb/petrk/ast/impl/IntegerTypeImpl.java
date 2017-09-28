/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import static ru.spb.petrk.ast.ASTKindUtils.isFloatingType;
import static ru.spb.petrk.ast.ASTKindUtils.isIntegerType;
import ru.spb.petrk.ast.IntegerType;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public final class IntegerTypeImpl implements IntegerType {
    
    public final static IntegerTypeImpl INSTANCE = new IntegerTypeImpl();
    
    private IntegerTypeImpl() {}

    @Override
    public boolean isCompatibleWith(Type other) {
        return isIntegerType(other) || isFloatingType(other);
    }

    @Override
    public Type common(Type other) {
        assert isCompatibleWith(other);
        return other; // if other is floating, it is wider
    }
}
