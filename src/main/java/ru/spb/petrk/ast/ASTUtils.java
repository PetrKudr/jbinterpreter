/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.ast;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser;

/**
 *
 * @author toor
 */
public final class ASTUtils {
        
    /**
     * Parses input as a program in jblanguage.
     * 
     * @param input - [in] code
     * @param errors - [out] list of lex or parse errors
     * @return AST or null in case of error
     */
    public static final ProgramStmt parse(String input, List<ParserError> errors) {
        assert errors.isEmpty();
        ParserErrorsListener errorsListener = new ParserErrorsListener();
        
        // Init lexer
        CharStream inputStream = new ANTLRInputStream(input);
        JetBrainsLanguageLexer lexer = new JetBrainsLanguageLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorsListener);
        
        TokenStream tokenStream = new CommonTokenStream(lexer);
        
        // Init parser
        JetBrainsLanguageParser parser = new JetBrainsLanguageParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorsListener);
        parser.setErrorHandler(new ExceptionErrorStrategy());
        
        ASTBuilder builder = new ASTBuilder();
        ProgramStmt ast;
        try {
            ast = builder.visitProgram(parser.program());
        } catch (SyntaxException ex) {
            errors.add(new ParserError(ex));
            ast = null;
        } catch (ParseCancellationException ex) {
            errors.add(new ParserError(ex));
            ast = null;
        } catch (RecognitionException ex) {
            errors.add(new ParserError(ex));
            ast = null;
        } catch (ASTBuilder.ASTBuildException ex) {
            errors.add(ex.error);
            ast = null;
        } catch (RuntimeException ex) {
            errors.add(new ParserError());
            ast = null;
        }
        return ast;
    }
    
    public static String position(AST ast) {
        return position(ast.getStart().getLine(), ast.getStart().getColumn());
    }
    
    public static String position(int line, int column) {
        return "line " + line + ":" + (column + 1) + " ";
    }
    
    public static final class ParserError {
        
        public final String message;
        
        public final int offendingStartOffset;

        public final int offendingStartLine;

        public final int offendingStartColumn;

        public final int offendingLength;
        
        /*package*/ ParserError() {
            this.message = "Unrecognized parse or lex error!";
            this.offendingStartOffset = -1;
            this.offendingStartLine = -1;
            this.offendingStartColumn = -1;
            this.offendingLength = 0;
        }

        /*package*/ ParserError(String message, int offendingStartOffset, int offendingStartLine, int offendingStartColumn, int offendingLength) {
            this.message = message;
            this.offendingStartOffset = offendingStartOffset;
            this.offendingStartLine = offendingStartLine;
            this.offendingStartColumn = offendingStartColumn;
            this.offendingLength = offendingLength;
        }
        
        private ParserError(SyntaxException ex) {
            this.message = ex.getMessage();
            this.offendingStartOffset = ex.offset;
            this.offendingStartLine = ex.line;
            this.offendingStartColumn = ex.column;
            this.offendingLength = ex.length;
        }
        
        private ParserError(RecognitionException ex) {
            Token offendingToken = ex.getOffendingToken() != null 
                    ? ex.getOffendingToken()
                    : ((Parser) ex.getRecognizer()).getCurrentToken();
            this.message = ex.getMessage();
            this.offendingStartOffset = offendingToken.getStartIndex();
            this.offendingStartLine = offendingToken.getLine();
            this.offendingStartColumn = offendingToken.getCharPositionInLine();
            this.offendingLength = offendingToken.getStopIndex() - offendingToken.getStartIndex() + 1;
        }
        
        private ParserError(ParseCancellationException ex) {
            if (ex.getCause() instanceof RecognitionException) {
                this.message = ex.getMessage();
                RecognitionException re = (RecognitionException) ex.getCause();
                Token offendingToken = re.getOffendingToken() != null 
                    ? re.getOffendingToken()
                    : ((Parser) re.getRecognizer()).getCurrentToken();
                this.offendingStartOffset = offendingToken.getStartIndex();
                this.offendingStartLine = offendingToken.getLine();
                this.offendingStartColumn = offendingToken.getCharPositionInLine();
                this.offendingLength = offendingToken.getStopIndex() - offendingToken.getStartIndex() + 1;
            } else {
                this.message = "Unrecognized parse or lex error!";
                this.offendingStartOffset = -1;
                this.offendingStartLine = -1;
                this.offendingStartColumn = -1;
                this.offendingLength = 0;
            }
        }

        @Override
        public String toString() {
            return position(offendingStartLine, offendingStartColumn) + message; 
        }
    }
    
    private static final class SyntaxException extends RuntimeException {

        public final int offset;
        
        public final int line;
        
        public final int column;
        
        public final int length;

        public SyntaxException(String message, int offset, int line, int column, int length) {
            super(message);
            this.offset = offset;
            this.line = line;
            this.column = column;
            this.length = length;
        }
    }
    
    private static final class ParserErrorsListener extends BaseErrorListener {
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (e instanceof LexerNoViableAltException) {
                JetBrainsLanguageLexer lexer = (JetBrainsLanguageLexer) recognizer;
                LexerNoViableAltException ex = (LexerNoViableAltException) e;
                throw new SyntaxException(
                        msg, 
                        ex.getStartIndex(), 
                        line, 
                        charPositionInLine, 
                        Math.max(lexer.getCharIndex() - ex.getStartIndex(), 1)
                );
            } else if (offendingSymbol instanceof Token) {
                Token tok = (Token) offendingSymbol;
                throw new SyntaxException(
                        msg, 
                        tok.getStartIndex(), 
                        line, 
                        charPositionInLine, 
                        tok.getStopIndex() - tok.getStartIndex() + 1
                );
            }
            // TODO: fix offset
            throw new SyntaxException(
                    msg, 
                    -1, 
                    line, 
                    charPositionInLine, 
                    0
            );
        }
    }
    
    private static class ExceptionErrorStrategy extends DefaultErrorStrategy {

        @Override
        public void recover(Parser recognizer, RecognitionException e) {
            // Do not recover
            throw e;
        }

        @Override
        public void reportInputMismatch(Parser recognizer, InputMismatchException e) throws RecognitionException {
            Token tok = e.getOffendingToken();
            String msg = "mismatched input " 
                    + getTokenErrorDisplay(tok) 
                    + " expecting one of " 
                    + e.getExpectedTokens().toString(recognizer.getVocabulary());
            RecognitionException ex = new RecognitionException(
                    msg, 
                    recognizer, 
                    recognizer.getInputStream(), 
                    recognizer.getContext()
            );
            ex.initCause(e);
            throw ex;
        }

        @Override
        public void reportMissingToken(Parser recognizer) {
            beginErrorCondition(recognizer);
            Token tok = recognizer.getCurrentToken();
            IntervalSet expecting = getExpectedTokens(recognizer);
            String msg = "missing " + expecting.toString(recognizer.getVocabulary()) 
                    + " at " + getTokenErrorDisplay(tok);
            throw new RecognitionException(
                    msg, 
                    recognizer, 
                    recognizer.getInputStream(), 
                    recognizer.getContext()
            );
        }
    }
}
