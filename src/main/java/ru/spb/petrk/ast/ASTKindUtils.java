/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

/**
 *
 * @author petrk
 */
public final class ASTKindUtils {
    
    public static boolean isExpr(Stmt stmt) {
        return stmt instanceof Expr;
    }
    
    public static boolean isRefExpr(Stmt stmt) {
        return stmt instanceof RefExpr;
    }
    
    public static boolean isBinaryOperator(Stmt stmt) {
        return stmt instanceof BinaryOperator;
    }
    
    public static boolean isMapOperator(Stmt stmt) {
        return stmt instanceof MapOperator;
    }
    
    public static boolean isReduceOperator(Stmt stmt) {
        return stmt instanceof ReduceOperator;
    }
    
    public static boolean isFloatingLiteral(Stmt stmt) {
        return stmt instanceof FloatingLiteral;
    }
    
    public static boolean isIntegerLiteral(Stmt stmt) {
        return stmt instanceof IntegerLiteral;
    }
    
    public static boolean isStringLiteral(Stmt stmt) {
        return stmt instanceof StringLiteral;
    }
    
    public static boolean isSequenceExpr(Stmt stmt) {
        return stmt instanceof SequenceExpr;
    }
    
    public static boolean isLambdaExpr(Stmt stmt) {
        return stmt instanceof LambdaExpr;
    }
    
    public static boolean isOutStmt(Stmt stmt) {
        return stmt instanceof OutStmt;
    }
    
    public static boolean isPrintStmt(Stmt stmt) {
        return stmt instanceof PrintStmt;
    }
    
    public static boolean isVarDeclStmt(Stmt stmt) {
        return stmt instanceof VarDeclStmt;
    }
}
