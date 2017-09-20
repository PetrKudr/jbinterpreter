/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.RefExpr;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class RefExprImpl implements RefExpr {
    
    private final String name;

    public RefExprImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<AST> getChildren() {
        return Collections.emptyList();
    }
}
