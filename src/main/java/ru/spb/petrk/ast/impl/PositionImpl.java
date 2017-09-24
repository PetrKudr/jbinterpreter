/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import ru.spb.petrk.ast.AST;

/**
 *
 * @author petrk
 */
public final class PositionImpl implements AST.Position {
    
    public static final PositionImpl EMPTY = new PositionImpl(0, 0, 0);
    
    private final int offset;
    
    private final int line;

    private final int column;

    public PositionImpl(int offset, int line, int column) {
        this.offset = offset;
        this.line = line;
        this.column = column;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
