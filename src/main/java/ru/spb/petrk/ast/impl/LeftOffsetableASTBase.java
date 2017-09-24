/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import ru.spb.petrk.ast.AST;

/**
 * Helper to store left position.
 *
 * @author petrk
 */
public abstract class LeftOffsetableASTBase implements AST {
    
    private final Position left;

    public LeftOffsetableASTBase(Position left) {
        this.left = left;
    }

    @Override
    public Position getStart() {
        return left;
    }
}