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
    
    public static boolean isStmt(AST ast) {
        return ast instanceof Stmt;
    }
    
    public static boolean isExpr(AST ast) {
        return ast instanceof Expr;
    }
    
    public static boolean isRefExpr(AST ast) {
        return ast instanceof RefExpr;
    }
    
    public static boolean isBinaryOperator(AST ast) {
        return ast instanceof BinaryOperator;
    }
    
    public static boolean isMapOperator(AST ast) {
        return ast instanceof MapOperator;
    }
    
    public static boolean isReduceOperator(AST ast) {
        return ast instanceof ReduceOperator;
    }
    
    public static boolean isFloatingLiteral(AST ast) {
        return ast instanceof FloatingLiteral;
    }
    
    public static boolean isIntegerLiteral(AST ast) {
        return ast instanceof IntegerLiteral;
    }
    
    public static boolean isStringLiteral(AST ast) {
        return ast instanceof StringLiteral;
    }
    
    public static boolean isSequenceExpr(AST ast) {
        return ast instanceof SequenceExpr;
    }
    
    public static boolean isLambdaExpr(AST ast) {
        return ast instanceof LambdaExpr;
    }
    
    public static boolean isOutStmt(AST ast) {
        return ast instanceof OutStmt;
    }
    
    public static boolean isPrintStmt(AST ast) {
        return ast instanceof PrintStmt;
    }
    
    public static boolean isVarDeclStmt(AST ast) {
        return ast instanceof VarDeclStmt;
    }
    
    public static boolean isProgramStmt(AST ast) {
        return ast instanceof ProgramStmt;
    }
}
