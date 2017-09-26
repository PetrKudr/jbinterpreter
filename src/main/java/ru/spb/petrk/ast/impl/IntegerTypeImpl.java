/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import ru.spb.petrk.ast.IntegerType;

/**
 *
 * @author petrk
 */
public final class IntegerTypeImpl implements IntegerType {
    
    public final static IntegerTypeImpl INSTANCE = new IntegerTypeImpl();
    
    private IntegerTypeImpl() {}
}
