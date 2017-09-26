/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Objects;
import ru.spb.petrk.ast.ASTKindUtils;
import static ru.spb.petrk.ast.ASTKindUtils.isSequenceType;
import ru.spb.petrk.ast.SequenceType;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public final class SequenceTypeImpl implements SequenceType {
    
    private final Type elemType;

    public SequenceTypeImpl(Type elemType) {
        this.elemType = elemType;
    }

    @Override
    public Type getElementType() {
        return elemType;
    }

    @Override
    public boolean isConvertibleTo(Type other) {
        return isSequenceType(other)
                && elemType.isConvertibleTo(((SequenceType) other).getElementType());
    }
}
