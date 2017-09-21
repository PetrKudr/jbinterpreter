/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import static ru.spb.petrk.ast.ASTKindUtils.*;

/**
 *
 * @author petrk
 */
public interface ASTVisitor<T> {
    
    default T visit(AST ast) {
        if (isProgramStmt(ast)) {
            return visitProgramStmt((ProgramStmt) ast);
        } else if (isUnaryOperator(ast)) {
            return visitUnaryOperator((UnaryOperator) ast);
        } else if (isBinaryOperator(ast)) {
            return visitBinaryOperator((BinaryOperator) ast);
        } else if (isMapOperator(ast)) {
            return visitMapOperator((MapOperator) ast);
        } else if (isReduceOperator(ast)) {
            return visitReduceOperator((ReduceOperator) ast);
        } else if (isIntegerLiteral(ast)) {
            return visitIntegerLiteral((IntegerLiteral) ast);
        } else if (isFloatingLiteral(ast)) {
            return visitFloatingLiteral((FloatingLiteral) ast);
        } else if (isStringLiteral(ast)) {
            return visitStringLiteral((StringLiteral) ast);
        } else if (isSequenceExpr(ast)) {
            return visitSequenceExpr((SequenceExpr) ast);
        } else if (isRefExpr(ast)) {
            return visitRefExpr((RefExpr) ast);
        } else if (isLambdaExpr(ast)) {
            return visitLambdaExpr((LambdaExpr) ast);
        } else if (isVarDeclStmt(ast)) {
            return visitVarDeclStmt((VarDeclStmt) ast);
        } else if (isOutStmt(ast)) {
            return visitOutStmt((OutStmt) ast);
        } else if (isPrintStmt(ast)) {
            return visitPrintStmt((PrintStmt) ast);
        }
        throw new IllegalArgumentException("Unexpected AST node: " + ast.getClass().getName());
    }
    
    T visitUnaryOperator(UnaryOperator op);
    
    T visitBinaryOperator(BinaryOperator op);
    
    T visitMapOperator(MapOperator op);
    
    T visitReduceOperator(ReduceOperator op);
    
    T visitIntegerLiteral(IntegerLiteral literal);
    
    T visitFloatingLiteral(FloatingLiteral literal);
    
    T visitStringLiteral(StringLiteral literal);
    
    T visitSequenceExpr(SequenceExpr expr);
    
    T visitRefExpr(RefExpr expr);
    
    T visitLambdaExpr(LambdaExpr expr);
    
    T visitVarDeclStmt(VarDeclStmt stmt);
    
    T visitOutStmt(OutStmt stmt);
    
    T visitPrintStmt(PrintStmt stmt);
    
    T visitProgramStmt(ProgramStmt stmt);
}
