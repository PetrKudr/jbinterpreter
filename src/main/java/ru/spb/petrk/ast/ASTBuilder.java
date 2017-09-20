/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import ru.spb.petrk.antlr4.JetBrainsLanguageBaseVisitor;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.Multiplicative_exprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.Power_exprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.Unary_exprContext;
import ru.spb.petrk.ast.BinaryOperator.OpKind;
import ru.spb.petrk.ast.impl.BinaryOperatorImpl;
import ru.spb.petrk.ast.impl.FloatingLiteralImpl;
import ru.spb.petrk.ast.impl.IntegerLiteralImpl;
import ru.spb.petrk.ast.impl.LambdaExprImpl;
import ru.spb.petrk.ast.impl.MapOperatorImpl;
import ru.spb.petrk.ast.impl.ProgramStmtImpl;
import ru.spb.petrk.ast.impl.ReduceOperatorImpl;
import ru.spb.petrk.ast.impl.RefExprImpl;
import ru.spb.petrk.ast.impl.SequenceExprImpl;
import ru.spb.petrk.ast.impl.UnaryOperatorImpl;
import ru.spb.petrk.ast.impl.VarDeclStmtImpl;

/**
 *
 * @author petrk
 */
public class ASTBuilder extends JetBrainsLanguageBaseVisitor<Stmt> {

    @Override
    public ProgramStmt visitProgram(JetBrainsLanguageParser.ProgramContext ctx) {
        List<Stmt> statements = ctx.stmt().stream()
                .map(stmt -> visitStmt(stmt))
                .collect(Collectors.toList());
        return new ProgramStmtImpl(statements);
    }

    @Override
    public VarDeclStmt visitVar_stmt(JetBrainsLanguageParser.Var_stmtContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Expr expr = visitAdditive_expr(ctx.additive_expr());
        return new VarDeclStmtImpl(name, expr);
    }

    @Override
    public Expr visitAdditive_expr(JetBrainsLanguageParser.Additive_exprContext ctx) {
        Expr LHS = visitMultiplicative_expr(ctx.multiplicative_expr(0));
        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode op = (TerminalNode) ctx.getChild(i);
            OpKind opKind;
            if (JetBrainsLanguageLexer.PLUS == op.getSymbol().getType()) {
                opKind = OpKind.PLUS;
            } else {
                assert JetBrainsLanguageLexer.MINUS == op.getSymbol().getType() 
                        : "Unexpected op kind: " + op.getText();
                opKind = OpKind.MINUS;
            }
            Multiplicative_exprContext rhsCtx = (Multiplicative_exprContext) ctx.getChild(i + 1);
            Expr RHS = visitMultiplicative_expr(rhsCtx);
            LHS = new BinaryOperatorImpl(opKind, LHS, RHS);
        }
        return LHS;
    }

    @Override
    public Expr visitMultiplicative_expr(JetBrainsLanguageParser.Multiplicative_exprContext ctx) {
        Expr LHS = visitPower_expr(ctx.power_expr(0));
        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode op = (TerminalNode) ctx.getChild(i);
            OpKind opKind;
            if (JetBrainsLanguageLexer.STAR == op.getSymbol().getType()) {
                opKind = OpKind.MULTIPLY;
            } else {
                assert JetBrainsLanguageLexer.DIVIDE == op.getSymbol().getType() 
                        : "Unexpected op kind: " + op.getText();
                opKind = OpKind.DIVIDE;
            }
            Power_exprContext rhsCtx = (Power_exprContext) ctx.getChild(i + 1);
            Expr RHS = visitPower_expr(rhsCtx);
            LHS = new BinaryOperatorImpl(opKind, LHS, RHS);
        }
        return LHS;
    }

    @Override
    public Expr visitPower_expr(JetBrainsLanguageParser.Power_exprContext ctx) {
        // power operation should be right-associative
        List<Unary_exprContext> unaryExprCtxs = ctx.unary_expr();
        final int lastCtx = unaryExprCtxs.size() - 1;
        Expr res = visitUnary_expr(unaryExprCtxs.get(lastCtx));
        for (int i = lastCtx - 1; i >= 0; --i) {
            res = new BinaryOperatorImpl(
                    OpKind.POWER, 
                    visitUnary_expr(unaryExprCtxs.get(i)), 
                    res
            );
        }
        return res;
    }

    @Override
    public Expr visitUnary_expr(JetBrainsLanguageParser.Unary_exprContext ctx) {
        if (ctx.MINUS() != null) {
            return new UnaryOperatorImpl(true, visitAtom(ctx.atom()));
        }
        return visitAtom(ctx.atom());
    }

    @Override
    public Expr visitAtom(JetBrainsLanguageParser.AtomContext ctx) {
        if (ctx.additive_expr() != null) {
            return visitAdditive_expr(ctx.additive_expr());
        } else if (ctx.sequence() != null) {
            return visitSequence(ctx.sequence());
        } else if (ctx.number() != null) {
            return visitNumber(ctx.number());
        } else if (ctx.map_operator() != null) {
            return visitMap_operator(ctx.map_operator());
        } else if (ctx.reduce_operator() != null) {
            return visitReduce_operator(ctx.reduce_operator());
        }
        assert ctx.IDENTIFIER() != null;
        return new RefExprImpl(ctx.IDENTIFIER().getSymbol().getText());
    }

    @Override
    public SequenceExpr visitSequence(JetBrainsLanguageParser.SequenceContext ctx) {
        return new SequenceExprImpl(
                visitAdditive_expr(ctx.additive_expr(0)),
                visitAdditive_expr(ctx.additive_expr(1))
        );
    }

    @Override
    public Expr visitNumber(JetBrainsLanguageParser.NumberContext ctx) {
        if (ctx.INTEGER_NUMBER() != null) {
            return new IntegerLiteralImpl(Integer.parseInt(
                    ctx.INTEGER_NUMBER().getSymbol().getText()
            ));
        }
        assert ctx.DOUBLE_NUMBER() != null;
        return new FloatingLiteralImpl(Double.parseDouble(
                ctx.DOUBLE_NUMBER().getSymbol().getText()
        ));
    }

    @Override
    public MapOperator visitMap_operator(JetBrainsLanguageParser.Map_operatorContext ctx) {
        return new MapOperatorImpl(
                visitAdditive_expr(ctx.additive_expr()), 
                visitMap_lambda(ctx.map_lambda())
        );
    }
    
    @Override
    public ReduceOperator visitReduce_operator(JetBrainsLanguageParser.Reduce_operatorContext ctx) {
        return new ReduceOperatorImpl(
                visitAdditive_expr(ctx.additive_expr(0)), 
                visitAdditive_expr(ctx.additive_expr(1)),
                visitReduce_lambda(ctx.reduce_lambda())
        );
    }

    @Override
    public LambdaExpr visitMap_lambda(JetBrainsLanguageParser.Map_lambdaContext ctx) {
        return new LambdaExprImpl(
                Arrays.asList(ctx.IDENTIFIER().getSymbol().getText()), 
                visitAdditive_expr(ctx.additive_expr())
        );
    }

    @Override
    public LambdaExpr visitReduce_lambda(JetBrainsLanguageParser.Reduce_lambdaContext ctx) {
        return new LambdaExprImpl(
                ctx.IDENTIFIER().stream()
                        .map(id -> id.getSymbol().getText())
                        .collect(Collectors.toList()), 
                visitAdditive_expr(ctx.additive_expr())
        );
    }
}
