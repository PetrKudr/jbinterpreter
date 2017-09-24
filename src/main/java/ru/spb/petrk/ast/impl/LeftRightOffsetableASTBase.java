/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import ru.spb.petrk.ast.AST;

/**
 * Helper to store left and right position.
 *
 * @author petrk
 */
public abstract class LeftRightOffsetableASTBase extends LeftOffsetableASTBase implements AST {
    
    private final Position right;

    public LeftRightOffsetableASTBase(Position left, Position right) {
        super(left);
        this.right = right;
    }

    @Override
    public Position getStop() {
        return right;
    }
}
