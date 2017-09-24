/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast.impl;

import java.util.Collections;
import java.util.List;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.ProgramStmt;
import ru.spb.petrk.ast.Stmt;

/**
 *
 * @author petrk
 */
public class ProgramStmtImpl implements ProgramStmt {
    
    private final List<Stmt> statements;

    public ProgramStmtImpl(List<Stmt> statements) {
        this.statements = Collections.unmodifiableList(statements);
    }

    @Override
    public List<Stmt> getStatements() {
        return statements;
    }

    @Override
    public List<Stmt> getChildren() {
        return statements;
    }

    @Override
    public Position getStart() {
        return statements.isEmpty() 
                ? PositionImpl.EMPTY 
                : statements.get(0).getStart();
    }

    @Override
    public Position getStop() {
        return statements.isEmpty() 
                ? PositionImpl.EMPTY 
                : statements.get(statements.size() - 1).getStop();
    }
}
