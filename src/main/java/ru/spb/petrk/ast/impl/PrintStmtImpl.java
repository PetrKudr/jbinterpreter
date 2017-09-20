/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.List;
import ru.spb.petrk.ast.PrintStmt;
import ru.spb.petrk.ast.Stmt;
import ru.spb.petrk.ast.StringLiteral;

/**
 *
 * @author petrk
 */
public class PrintStmtImpl implements PrintStmt {
    
    private final StringLiteral message;

    public PrintStmtImpl(StringLiteral message) {
        this.message = message;
    }

    @Override
    public StringLiteral getMessage() {
        return message;
    }

    @Override
    public List<Stmt> getChildren() {
        return Arrays.asList(message);
    }
}
