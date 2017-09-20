/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.List;
import ru.spb.petrk.ast.Expr;
import ru.spb.petrk.ast.Stmt;
import ru.spb.petrk.ast.VarDeclStmt;

/**
 *
 * @author petrk
 */
public class VarDeclStmtImpl implements VarDeclStmt {
    
    private final String name;
    
    private final Expr initializer;

    public VarDeclStmtImpl(String name, Expr initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Expr getInitializer() {
        return initializer;
    }

    @Override
    public List<Stmt> getChildren() {
        return Arrays.asList(initializer);
    }
}
