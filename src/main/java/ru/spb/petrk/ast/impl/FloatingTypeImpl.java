/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import static ru.spb.petrk.ast.ASTKindUtils.isFloatingType;
import static ru.spb.petrk.ast.ASTKindUtils.isIntegerType;
import ru.spb.petrk.ast.FloatingType;
import ru.spb.petrk.ast.Type;

/**
 *
 * @author petrk
 */
public final class FloatingTypeImpl implements FloatingType {
    
    public final static FloatingTypeImpl INSTANCE = new FloatingTypeImpl();
    
    private FloatingTypeImpl() {}

    @Override
    public boolean isCompatibleWith(Type other) {
        return isIntegerType(other) || isFloatingType(other);
    }
}
