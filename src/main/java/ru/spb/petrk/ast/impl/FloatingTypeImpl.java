/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import ru.spb.petrk.ast.FloatingType;

/**
 *
 * @author petrk
 */
public final class FloatingTypeImpl implements FloatingType {
    
    public final static FloatingTypeImpl INSTANCE = new FloatingTypeImpl();
    
    private FloatingTypeImpl() {}
}
