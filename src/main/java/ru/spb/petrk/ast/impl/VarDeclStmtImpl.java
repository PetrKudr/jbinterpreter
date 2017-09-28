/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Arrays;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.Expr;
import ru.spb.petrk.ast.VarDeclStmt;

/**
 *
 * @author petrk
 */
public class VarDeclStmtImpl extends LeftOffsetableASTBase implements VarDeclStmt {
    
    private final String name;
    
    private final Expr initializer;

    public VarDeclStmtImpl(String name, Expr initializer, Position left) {
        super(left);
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
    public List<AST> getChildren() {
        return Arrays.asList(initializer);
    }

    @Override
    public Position getStop() {
        return initializer.getStop();
    }
}
