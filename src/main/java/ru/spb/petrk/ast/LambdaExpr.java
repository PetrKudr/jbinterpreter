/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.List;

/**
 * Represents declaration of lambda.
 *
 * @author petrk
 */
public interface LambdaExpr extends Expr {
    
    List<String> getParams();
    
    Expr getBody();
}
