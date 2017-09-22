/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.IntegerLiteral;

/**
 *
 * @author petrk
 */
public class IntegerLiteralImpl extends OffsetableAST implements IntegerLiteral {
    
    private final int value;

    public IntegerLiteralImpl(int value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
