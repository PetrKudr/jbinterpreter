/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.MultiplicativeExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.PowerExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.ReduceLambdaContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser.UnaryExprContext;
import ru.spb.petrk.antlr4.JetBrainsLanguageVisitor;
import ru.spb.petrk.ast.AST.Position;
import static ru.spb.petrk.ast.ASTKindUtils.*;
import ru.spb.petrk.ast.BinaryOperator.OpKind;
import ru.spb.petrk.ast.impl.BinaryOperatorImpl;
import ru.spb.petrk.ast.impl.FloatingLiteralImpl;
import ru.spb.petrk.ast.impl.FloatingTypeImpl;
import ru.spb.petrk.ast.impl.IntegerLiteralImpl;
import ru.spb.petrk.ast.impl.IntegerTypeImpl;
import ru.spb.petrk.ast.impl.LambdaExprImpl;
import ru.spb.petrk.ast.impl.MapOperatorImpl;
import ru.spb.petrk.ast.impl.OutStmtImpl;
import ru.spb.petrk.ast.impl.ParamExprImpl;
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
    
    private final Deque<Map<String, Type>> symTab = new ArrayDeque<>();

    @Override
    public ProgramStmt visitProgram(JetBrainsLanguageParser.ProgramContext ctx) {
        pushSymTab();
        try {
            List<Stmt> statements = ctx.stmt().stream()
                    .map(stmt -> visitStmt(stmt))
                    .collect(Collectors.toList());
            return new ProgramStmtImpl(statements);
        } finally {
            popSymTab();
        }
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
        putSym(name, expr.getType());
        return new VarDeclStmtImpl(name, expr, tok2StartPos(ctx.LITERAL_VAR()));
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
            LHS = new BinaryOperatorImpl(opKind, LHS, RHS, typeOfBinOp(LHS, RHS));
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
            LHS = new BinaryOperatorImpl(opKind, LHS, RHS, typeOfBinOp(LHS, RHS));
        }
        return LHS;
    }

    @Override
    public Expr visitPowerExpr(JetBrainsLanguageParser.PowerExprContext ctx) {
        // power operation should be right-associative
        List<UnaryExprContext> unaryExprCtxs = ctx.unaryExpr();
        final int lastCtx = unaryExprCtxs.size() - 1;
        Expr RHS = visitUnaryExpr(unaryExprCtxs.get(lastCtx));
        for (int i = lastCtx - 1; i >= 0; --i) {
            Expr LHS = visitUnaryExpr(unaryExprCtxs.get(i));
            RHS = new BinaryOperatorImpl(OpKind.POWER, LHS, RHS, typeOfBinOp(LHS, RHS));
        }
        return RHS;
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
        String name = ctx.IDENTIFIER().getText();
        if (!hasSym(name)) {
            Token ident = ctx.IDENTIFIER().getSymbol();
            throw new ASTBuildException(symTabDepth(), null, reportError(
                    "unresolved variable: \"" + name + "\"", 
                    ident
            ));
        }
        return new RefExprImpl(
                name, 
                getSym(name),
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
                throw new ASTBuildException(symTabDepth(), null, reportError(
                        "number \""+ ctx.INTEGER_NUMBER().getText() + "\" is too big",
                        ctx.INTEGER_NUMBER()
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
            throw new ASTBuildException(symTabDepth(), null, reportError(
                    "number \"" + ctx.DOUBLE_NUMBER().getText() + "\" is too big",
                    ctx.DOUBLE_NUMBER()
            ));
        }
    }

    @Override
    public MapOperator visitMapOperator(JetBrainsLanguageParser.MapOperatorContext ctx) {
        // parse sequence
        Expr sequence = visitAdditiveExpr(ctx.additiveExpr());
        if (!isSequenceType(sequence.getType())) {
            throw new ASTBuildException(symTabDepth(), sequence, reportMismatchedTypes(
                    sequence, SequenceType.class
            ));
        }
        
        // parse lambda
        LambdaExpr lambda;
        pushSymTab();
        String lambdaParam = ctx.mapLambda().IDENTIFIER().getText();
        putSym(lambdaParam, ((SequenceType) sequence.getType()).getElementType());
        try {
            lambda = visitMapLambda(ctx.mapLambda());
        } finally {
            popSymTab();
        }
        
        return new MapOperatorImpl(
                sequence, 
                lambda,
                tok2StartPos(ctx.getStart()),
                tok2StopPos(ctx.getStop())
        );
    }
    
    @Override
    public ReduceOperator visitReduceOperator(JetBrainsLanguageParser.ReduceOperatorContext ctx) {
        // parse sequence
        Expr sequence = visitAdditiveExpr(ctx.additiveExpr(0));
        if (!isSequenceType(sequence.getType())) {
            throw new ASTBuildException(symTabDepth(), sequence, reportMismatchedTypes(
                    sequence, SequenceType.class
            ));
        }
        
        // parse neutral
        Expr neutral = visitAdditiveExpr(ctx.additiveExpr(1));
        
        // parse lambda
        LambdaExpr lambda = visitReduceLambda(ctx.reduceLambda(), neutral, sequence);
        
        return new ReduceOperatorImpl(
                sequence, 
                neutral,
                lambda,
                tok2StartPos(ctx.getStart()),
                tok2StopPos(ctx.getStop())
        );
    }
    
    private LambdaExpr visitReduceLambda(ReduceLambdaContext ctx, Expr neutral, Expr sequence) {
        // Basically, we need to check that lambda has type of "(T a, T b) -> T"
        
        // Check that sequence has sequence type
        if (!isSequenceType(sequence.getType())) {
            throw new ASTBuildException(symTabDepth(), sequence, reportMismatchedTypes(
                    sequence, SequenceType.class
            ));
        }        
        SequenceType seqType = (SequenceType) sequence.getType();
        Type seqElemType = seqType.getElementType();
        
        // Check that identity element has compatible type with seqElemType
        if (!neutral.getType().isCompatibleWith(seqElemType)) {
            throw new ASTBuildException(symTabDepth(), neutral, reportError(
                    "the type of neutral element differs from the type of sequence elements: "
                    + "expected \"" + ASTUtils.getGeneralizedTypeName(seqElemType) + "\", "
                    + "but found \"" + ASTUtils.getGeneralizedTypeName(neutral.getType()) + "\"",
                    neutral
            ));
        }
        Type neutralType = neutral.getType();
        
        Type widerType = seqElemType.common(neutralType);
        
        // Let's build lambda of type (x y) -> ...
        // assuming that 
        //  - x = neutral
        //  - y = element of the sequence
        final String lambdaFirstParam = ctx.IDENTIFIER(0).getText();
        final String lambdaSecondParam = ctx.IDENTIFIER(1).getText();
        pushSymTab();
        putSym(lambdaFirstParam, widerType);
        putSym(lambdaSecondParam, widerType);
        LambdaExpr lambda;
        try {
            lambda = visitReduceLambda(ctx);
        } catch (ASTBuildException ex) {
            //if (symTabDepth() == ex.contextDepth && isRefExpr(ex.offendingExpr)) {
            //    RefExpr ref = (RefExpr) ex.offendingExpr;
            //    if (ref.getName().equals(lambdaFirstParam)) {
            //        throw new ASTBuildException(symTabDepth(), ref, reportError(
            //                "error when \"" + lambdaFirstParam + "\" is the neutral element: " + ex.getMessage(), 
            //                ex.error
            //        ));
            //    } else if (ref.getName().equals(lambdaSecondParam)) {
            //        throw new ASTBuildException(symTabDepth(), ref, reportError(
            //                "error when \"" + lambdaSecondParam + "\" is a sequence element: " + ex.getMessage(), 
            //                ex.error
            //        ));
            //    }
            //}
            throw ex; // just rethrow
        } finally {
            popSymTab();
        }
        
        // Check that lambda returns type compatible with sequence element type
        if (!lambda.getType().isCompatibleWith(seqElemType)) {
            ParamExpr firstParm = lambda.getParams().get(0);
            ParamExpr lastParm = lambda.getParams().get(1);
            throw new ASTBuildException(symTabDepth(), lambda, reportError(
                    "if \"" + lambdaFirstParam + "\" is the neutral element "
                            + "(\"" + ASTUtils.getTypeName(neutralType) + "\") and "
                            + "\"" + lambdaSecondParam + "\" is a sequence element "
                            + "(\"" + ASTUtils.getTypeName(seqElemType) + "\"), "
                            + "then reduction has "
                            + "\"" + ASTUtils.getTypeName(lambda.getType()) + "\" type" 
                            + ", but \"" + ASTUtils.getGeneralizedTypeName(seqElemType) + "\" type expected", 
                    firstParm,
                    lastParm.getStop().getOffset() - firstParm.getStart().getOffset() + 1
            ));
        }
        return lambda;
    }

    @Override
    public LambdaExpr visitMapLambda(JetBrainsLanguageParser.MapLambdaContext ctx) {
        Token ident = ctx.IDENTIFIER().getSymbol();
        String identName = ident.getText();
        return new LambdaExprImpl(
                Arrays.asList(new ParamExprImpl(
                        identName, 
                        getSym(identName), 
                        tok2StartPos(ident), 
                        tok2StopPos(ident))
                ), 
                visitAdditiveExpr(ctx.additiveExpr()),
                tok2StartPos(ctx.IDENTIFIER())
        );
    }

    @Override
    public LambdaExpr visitReduceLambda(JetBrainsLanguageParser.ReduceLambdaContext ctx) {
        Position start = tok2StartPos(ctx.getStart());
        if (ctx.IDENTIFIER(0).getText().equals(ctx.IDENTIFIER(1).getText())) {
            throw new ASTBuildException(symTabDepth(), null, reportError(
                    "parameters of the lambda cannot have the same name",
                    ctx.IDENTIFIER(0),
                    ctx.IDENTIFIER(1).getSymbol().getStopIndex() - start.getOffset() + 1
            ));
        }
        return new LambdaExprImpl(
                ctx.IDENTIFIER().stream()
                        .map(id -> new ParamExprImpl(
                                id.getText(),
                                getSym(id.getText()),
                                tok2StartPos(id), 
                                tok2StopPos(id)))
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
    
    private Type typeOfBinOp(Expr lhs, Expr rhs) {
        if (isNumberType(lhs.getType()) && isNumberType(rhs.getType())) {
            if (isIntegerType(lhs.getType()) && isIntegerType(rhs.getType())) {
                return IntegerTypeImpl.INSTANCE;
            }
            assert isFloatingType(lhs.getType()) || isFloatingType(rhs.getType());
            return FloatingTypeImpl.INSTANCE;
        }
        Expr notANumber = !isNumberType(lhs.getType()) ? lhs : rhs;
        throw new ASTBuildException(symTabDepth(), notANumber, reportMismatchedTypes(notANumber, NumberType.class));
    }
    
    private ASTUtils.ParserError reportMismatchedTypes(Expr expr, Class<? extends Type> expected) {
        return reportError(
                    "mismatched types: " 
                        + "expected \"" + ASTUtils.getTypeName(expected) + "\"," 
                        + " but found \"" + ASTUtils.getTypeName(expr.getType()) + "\"", 
                    expr
        );
    }
    
    private ASTUtils.ParserError reportError(String message, AST ast) {
        return reportError(
                message, 
                ast, 
                ast.getStop().getOffset() - ast.getStart().getOffset() + 1
        );
    }
    
    private ASTUtils.ParserError reportError(String message, AST ast, int length) {
        return new ASTUtils.ParserError(
                    message, 
                    ast.getStart().getOffset(), 
                    ast.getStart().getLine(),
                    ast.getStart().getColumn(), 
                    length
        );
    }
    
    private ASTUtils.ParserError reportError(String message, Token token) {
        return reportError(
                    message, 
                    token,
                    token.getStopIndex() - token.getStartIndex() + 1
        );
    }
    
    private ASTUtils.ParserError reportError(String message, Token token, int length) {
        return new ASTUtils.ParserError(
                    message, 
                    token.getStartIndex(), 
                    token.getLine(),
                    token.getCharPositionInLine(), 
                    length
        );
    }
    
    private ASTUtils.ParserError reportError(String message, TerminalNode node) {
        return reportError(message, node.getSymbol());
    }
    
    private ASTUtils.ParserError reportError(String message, TerminalNode node, int length) {
        return reportError(message, node.getSymbol(), length);
    }
    
    private ASTUtils.ParserError reportError(String newMessage, ASTUtils.ParserError error) {
        return new ASTUtils.ParserError(
                    newMessage, 
                    error.offendingStartOffset, 
                    error.offendingStartLine,
                    error.offendingStartColumn, 
                    error.offendingLength
        );
    }
    
    private void pushSymTab() {
        symTab.push(new HashMap<>());
    }
    
    private void popSymTab() {
        symTab.pop();
    }
    
    private Type getSym(String name) {
        return symTab.peek().get(name);
    }
    
    private boolean hasSym(String name) {
        return symTab.peek().containsKey(name);
    }
    
    private void putSym(String name, Type type) {
        symTab.peek().put(name, type);
    }
    
    private int symTabDepth() {
        return symTab.size();
    }
    
    public static class ASTBuildException extends RuntimeException {
        
        public final int contextDepth; // depth of symtab
        
        public final Expr offendingExpr; // may be null
        
        public final ASTUtils.ParserError error;

        public ASTBuildException(int contextDepth, Expr expr, ASTUtils.ParserError error) {
            super(error.message);
            this.contextDepth = contextDepth;
            this.offendingExpr = expr;
            this.error = error;
        }
    }
}
