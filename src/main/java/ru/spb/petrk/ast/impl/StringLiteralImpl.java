/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.Stmt;
import ru.spb.petrk.ast.StringLiteral;

/**
 *
 * @author petrk
 */
public class StringLiteralImpl extends OffsetableAST implements StringLiteral {
    
    private final String string;

    public StringLiteralImpl(String string, int line, int column) {
        super(line, column);
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
