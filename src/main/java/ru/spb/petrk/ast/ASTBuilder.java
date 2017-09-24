/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.MultiplicativeExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.PowerExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.UnaryExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageVisitor;
import ru.spb.petrk.ast.AST.Position;
import ru.spb.petrk.ast.BinaryOperator.OpKind;
import ru.spb.petrk.ast.impl.BinaryOperatorImpl;
import ru.spb.petrk.ast.impl.FloatingLiteralImpl;
import ru.spb.petrk.ast.impl.IntegerLiteralImpl;
import ru.spb.petrk.ast.impl.LambdaExprImpl;
import ru.spb.petrk.ast.impl.MapOperatorImpl;
import ru.spb.petrk.ast.impl.OutStmtImpl;
import ru.spb.petrk.ast.impl.PositionImpl;
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
        return new VarDeclStmtImpl(name, expr, tok2StartPos(ctx.IDENTIFIER()));
    }

    @Override
    public OutStmt visitOutStmt(JetBrainsLanguageParser.OutStmtContext ctx) {
        return new OutStmtImpl(
                visitAdditiveExpr(ctx.additiveExpr()), 
                tok2StartPos(ctx.LITERAL_OUT())
        );
    }

    @Override
    public PrintStmt visitPrintStmt(JetBrainsLanguageParser.PrintStmtContext ctx) {
        return new PrintStmtImpl(
                new StringLiteralImpl(
                        ctx.STRING().getText(),
                        tok2StartPos(ctx.STRING()),
                        tok2StopPos(ctx.STRING())
                ),
                tok2StartPos(ctx.LITERAL_PRINT())
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
            return new UnaryOperatorImpl(
                    true, 
                    visitAtom(ctx.atom()),
                    tok2StartPos(ctx.MINUS())
            );
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
        return new RefExprImpl(
                ctx.IDENTIFIER().getText(), 
                tok2StartPos(ctx.IDENTIFIER()), 
                tok2StopPos(ctx.IDENTIFIER())
        );
    }

    @Override
    public SequenceExpr visitSequence(JetBrainsLanguageParser.SequenceContext ctx) {
        return new SequenceExprImpl(
                visitAdditiveExpr(ctx.additiveExpr(0)),
                visitAdditiveExpr(ctx.additiveExpr(1)),
                tok2StartPos(ctx.LCURLY()),
                tok2StopPos(ctx.RCURLY())
        );
    }

    @Override
    public Expr visitNumber(JetBrainsLanguageParser.NumberContext ctx) {
        Position start = tok2StartPos(ctx.getStart());
        Position stop = tok2StopPos(ctx.getStart());
        if (ctx.INTEGER_NUMBER() != null) {
            try {
                return new IntegerLiteralImpl(
                        Integer.parseInt(ctx.INTEGER_NUMBER().getText()),
                        start,
                        stop
                );
            } catch (NumberFormatException ex) {
                throw new ASTBuildException(new ASTUtils.ParserError(
                        "number \""+ ctx.INTEGER_NUMBER().getText() + "\" is too big",
                        start.getOffset(),
                        start.getLine(),
                        start.getColumn(),
                        ctx.INTEGER_NUMBER().getSymbol().getStopIndex() - start.getOffset() + 1
                ));
            }
        }
        assert ctx.DOUBLE_NUMBER() != null;
        try {
            return new FloatingLiteralImpl(
                    Double.parseDouble(ctx.DOUBLE_NUMBER().getSymbol().getText()),
                    start,
                    stop
            );
        } catch (NumberFormatException ex) {
            throw new ASTBuildException(new ASTUtils.ParserError(
                    "number \"" + ctx.DOUBLE_NUMBER().getText() + "\" is too big",
                    start.getOffset(),
                    start.getLine(),
                    start.getColumn(),
                    ctx.DOUBLE_NUMBER().getSymbol().getStopIndex() - start.getOffset() + 1
            ));
        }
    }

    @Override
    public MapOperator visitMapOperator(JetBrainsLanguageParser.MapOperatorContext ctx) {
        return new MapOperatorImpl(
                visitAdditiveExpr(ctx.additiveExpr()), 
                visitMapLambda(ctx.mapLambda()),
                tok2StartPos(ctx.getStart()),
                tok2StopPos(ctx.getStop())
        );
    }
    
    @Override
    public ReduceOperator visitReduceOperator(JetBrainsLanguageParser.ReduceOperatorContext ctx) {
        return new ReduceOperatorImpl(
                visitAdditiveExpr(ctx.additiveExpr(0)), 
                visitAdditiveExpr(ctx.additiveExpr(1)),
                visitReduceLambda(ctx.reduceLambda()),
                tok2StartPos(ctx.getStart()),
                tok2StopPos(ctx.getStop())
        );
    }

    @Override
    public LambdaExpr visitMapLambda(JetBrainsLanguageParser.MapLambdaContext ctx) {
        return new LambdaExprImpl(
                Arrays.asList(ctx.IDENTIFIER().getSymbol().getText()), 
                visitAdditiveExpr(ctx.additiveExpr()),
                tok2StartPos(ctx.IDENTIFIER())
        );
    }

    @Override
    public LambdaExpr visitReduceLambda(JetBrainsLanguageParser.ReduceLambdaContext ctx) {
        Position start = tok2StartPos(ctx.getStart());
        assert ctx.IDENTIFIER().size() == 2 : "Reduce lambda with more than 2 parameters?";
        if (ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(1).getText())) {
            throw new ASTBuildException(new ASTUtils.ParserError(
                    "parameters of the lambda cannot have the same name",
                    start.getOffset(),
                    start.getLine(),
                    start.getColumn(),
                    ctx.IDENTIFIER(1).getSymbol().getStopIndex() - start.getOffset() + 1
            ));
        }
        return new LambdaExprImpl(
                ctx.IDENTIFIER().stream()
                        .map(id -> id.getText())
                        .collect(Collectors.toList()), 
                visitAdditiveExpr(ctx.additiveExpr()),
                start
        );
    }
    
    private static Position tok2StartPos(TerminalNode node) {
        return tok2StartPos(node.getSymbol());
    }
    
    private static Position tok2StartPos(Token tok) {
        int offset = tok.getStartIndex();
        int line = tok.getLine();
        int column = tok.getCharPositionInLine();
        return new PositionImpl(offset, line, column);
    }
    
    private static Position tok2StopPos(TerminalNode node) {
        return tok2StopPos(node.getSymbol());
    }
    
    private static Position tok2StopPos(Token tok) {
        int offset = tok.getStopIndex();
        int line = tok.getLine();
        int column = tok.getCharPositionInLine() + tok.getText().length();
        if (tok.getType() == JetBrainsLanguageLexer.STRING) {
            String tokText = tok.getText();
            int newLineCounter = 0;
            int lastNewLineIndex = 0;
            for (int i = 0; i < tokText.length(); ++i) {
                if (tokText.charAt(i) == '\n') {
                    ++newLineCounter;
                    lastNewLineIndex = i;
                }
            }
            line += newLineCounter;
            column = tokText.length() - lastNewLineIndex;
        }
        return new PositionImpl(offset, line, column);
    }
    
    public static class ASTBuildException extends RuntimeException {
        
        public final ASTUtils.ParserError error;

        public ASTBuildException(ASTUtils.ParserError error) {
            this.error = error;
        }
    }
}
