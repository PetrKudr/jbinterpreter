/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import ru.spb.petrk.ast.StringType;

/**
 *
 * @author petrk
 */
public final class StringTypeImpl implements StringType {
    
    public final static StringTypeImpl INSTANCE = new StringTypeImpl();
    
    private StringTypeImpl() {}
}
