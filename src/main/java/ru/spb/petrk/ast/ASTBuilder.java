/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.MultiplicativeExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.PowerExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.UnaryExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageVisitor;
import ru.spb.petrk.ast.BinaryOperator.OpKind;
import ru.spb.petrk.ast.impl.BinaryOperatorImpl;
import ru.spb.petrk.ast.impl.FloatingLiteralImpl;
import ru.spb.petrk.ast.impl.IntegerLiteralImpl;
import ru.spb.petrk.ast.impl.LambdaExprImpl;
import ru.spb.petrk.ast.impl.MapOperatorImpl;
import ru.spb.petrk.ast.impl.OutStmtImpl;
import ru.spb.petrk.ast.impl.PrintStmtImpl;
import ru.spb.petrk.ast.impl.ProgramStmtImpl;
import ru.spb.petrk.ast.impl.ReduceOperatorImpl;
import ru.spb.petrk.ast.impl.RefExprImpl;
import ru.spb.petrk.ast.impl.SequenceExprImpl;
import ru.spb.petrk.ast.impl.StringLiteralImpl;
import ru.spb.petrk.ast.impl.UnaryOperatorImpl;
import ru.spb.petrk.ast.impl.VarDeclStmtImpl;

/**
 *
 * @author petrk
 */
/*package*/ class ASTBuilder extends AbstractParseTreeVisitor<AST> implements JetBrainsLanguageVisitor<AST> {

    @Override
    public ProgramStmt visitProgram(JetBrainsLanguageParser.ProgramContext ctx) {
        List<Stmt> statements = ctx.stmt().stream()
                .map(stmt -> visitStmt(stmt))
                .collect(Collectors.toList());
        return new ProgramStmtImpl(statements);
    }

    @Override
    public Stmt visitStmt(JetBrainsLanguageParser.StmtContext ctx) {
        if (ctx.varStmt() != null) {
            return visitVarStmt(ctx.varStmt());
        } else if (ctx.outStmt() != null) {
            return visitOutStmt(ctx.outStmt());
        }
        assert ctx.printStmt() != null;
        return visitPrintStmt(ctx.printStmt());
    }

    @Override
    public VarDeclStmt visitVarStmt(JetBrainsLanguageParser.VarStmtContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Expr expr = visitAdditiveExpr(ctx.additiveExpr());
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new VarDeclStmtImpl(name, expr, line, column);
    }

    @Override
    public OutStmt visitOutStmt(JetBrainsLanguageParser.OutStmtContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new OutStmtImpl(visitAdditiveExpr(ctx.additiveExpr()), line, column);
    }

    @Override
    public PrintStmt visitPrintStmt(JetBrainsLanguageParser.PrintStmtContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new PrintStmtImpl(
                new StringLiteralImpl(
                        ctx.STRING().getText(),
                        ctx.STRING().getSymbol().getLine(),
                        ctx.STRING().getSymbol().getCharPositionInLine()
                ),
                line,
                column
        );
    }

    @Override
    public Expr visitAdditiveExpr(JetBrainsLanguageParser.AdditiveExprContext ctx) {
        Expr LHS = visitMultiplicativeExpr(ctx.multiplicativeExpr(0));
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
            MultiplicativeExprContext rhsCtx = (MultiplicativeExprContext) ctx.getChild(i + 1);
            Expr RHS = visitMultiplicativeExpr(rhsCtx);
            LHS = new BinaryOperatorImpl(opKind, LHS, RHS);
        }
        return LHS;
    }

    @Override
    public Expr visitMultiplicativeExpr(JetBrainsLanguageParser.MultiplicativeExprContext ctx) {
        Expr LHS = visitPowerExpr(ctx.powerExpr(0));
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
            PowerExprContext rhsCtx = (PowerExprContext) ctx.getChild(i + 1);
            Expr RHS = visitPowerExpr(rhsCtx);
            LHS = new BinaryOperatorImpl(opKind, LHS, RHS);
        }
        return LHS;
    }

    @Override
    public Expr visitPowerExpr(JetBrainsLanguageParser.PowerExprContext ctx) {
        // power operation should be right-associative
        List<UnaryExprContext> unaryExprCtxs = ctx.unaryExpr();
        final int lastCtx = unaryExprCtxs.size() - 1;
        Expr res = visitUnaryExpr(unaryExprCtxs.get(lastCtx));
        for (int i = lastCtx - 1; i >= 0; --i) {
            res = new BinaryOperatorImpl(
                    OpKind.POWER, 
                    visitUnaryExpr(unaryExprCtxs.get(i)), 
                    res
            );
        }
        return res;
    }

    @Override
    public Expr visitUnaryExpr(JetBrainsLanguageParser.UnaryExprContext ctx) {
        if (ctx.MINUS() != null) {
            int line = ctx.getStart().getLine();
            int column = ctx.getStart().getCharPositionInLine();
            return new UnaryOperatorImpl(true, visitAtom(ctx.atom()), line, column);
        }
        return visitAtom(ctx.atom());
    }

    @Override
    public Expr visitAtom(JetBrainsLanguageParser.AtomContext ctx) {
        if (ctx.additiveExpr() != null) {
            return visitAdditiveExpr(ctx.additiveExpr());
        } else if (ctx.sequence() != null) {
            return visitSequence(ctx.sequence());
        } else if (ctx.number() != null) {
            return visitNumber(ctx.number());
        } else if (ctx.mapOperator() != null) {
            return visitMapOperator(ctx.mapOperator());
        } else if (ctx.reduceOperator() != null) {
            return visitReduceOperator(ctx.reduceOperator());
        }
        assert ctx.IDENTIFIER() != null;
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new RefExprImpl(ctx.IDENTIFIER().getSymbol().getText(), line, column);
    }

    @Override
    public SequenceExpr visitSequence(JetBrainsLanguageParser.SequenceContext ctx) {
        return new SequenceExprImpl(
                visitAdditiveExpr(ctx.additiveExpr(0)),
                visitAdditiveExpr(ctx.additiveExpr(1))
        );
    }

    @Override
    public Expr visitNumber(JetBrainsLanguageParser.NumberContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        if (ctx.INTEGER_NUMBER() != null) {
            try {
                return new IntegerLiteralImpl(
                        Integer.parseInt(ctx.INTEGER_NUMBER().getText()),
                        line,
                        column
                );
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(
                        ASTUtils.position(line, column) + "number \"" 
                                + ctx.INTEGER_NUMBER().getText() 
                                + "\" is too big"
                );
            }
        }
        assert ctx.DOUBLE_NUMBER() != null;
        try {
            return new FloatingLiteralImpl(
                    Double.parseDouble(ctx.DOUBLE_NUMBER().getSymbol().getText()),
                    line,
                    column
            );
        } catch (NumberFormatException ex) {
            throw new NumberFormatException(
                    ASTUtils.position(line, column) + "number \"" 
                            + ctx.DOUBLE_NUMBER().getText() 
                            + "\" is too big"
            );
        }
    }

    @Override
    public MapOperator visitMapOperator(JetBrainsLanguageParser.MapOperatorContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new MapOperatorImpl(
                visitAdditiveExpr(ctx.additiveExpr()), 
                visitMapLambda(ctx.mapLambda()),
                line,
                column
        );
    }
    
    @Override
    public ReduceOperator visitReduceOperator(JetBrainsLanguageParser.ReduceOperatorContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new ReduceOperatorImpl(
                visitAdditiveExpr(ctx.additiveExpr(0)), 
                visitAdditiveExpr(ctx.additiveExpr(1)),
                visitReduceLambda(ctx.reduceLambda()),
                line,
                column
        );
    }

    @Override
    public LambdaExpr visitMapLambda(JetBrainsLanguageParser.MapLambdaContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new LambdaExprImpl(
                Arrays.asList(ctx.IDENTIFIER().getSymbol().getText()), 
                visitAdditiveExpr(ctx.additiveExpr()),
                line,
                column
        );
    }

    @Override
    public LambdaExpr visitReduceLambda(JetBrainsLanguageParser.ReduceLambdaContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return new LambdaExprImpl(
                ctx.IDENTIFIER().stream()
                        .map(id -> id.getSymbol().getText())
                        .collect(Collectors.toList()), 
                visitAdditiveExpr(ctx.additiveExpr()),
                line,
                column
        );
    }
}
