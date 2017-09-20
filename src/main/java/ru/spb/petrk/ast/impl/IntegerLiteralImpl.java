/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.IntegerLiteral;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class IntegerLiteralImpl implements IntegerLiteral {
    
    private final int value;

    public IntegerLiteralImpl(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public List<Stmt> getChildren() {
        return Collections.emptyList();
    }
}
