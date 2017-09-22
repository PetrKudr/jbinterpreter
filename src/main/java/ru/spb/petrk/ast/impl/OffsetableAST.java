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
public abstract class OffsetableAST implements AST {
    
    private final int line;
    
    private final int column;

    public OffsetableAST(int line, int column) {
        this.line = line;
        this.column = column;
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
