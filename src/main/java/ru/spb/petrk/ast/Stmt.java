/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.List;


/**
 * Serves as a root for every AST node
 * 
 * @author petrk
 */
public interface Stmt {
    
    List<Stmt> getChildren();
}
