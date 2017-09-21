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
    
    public static final ProgramStmt parse(String input, List<String> errors) {
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
        } catch (ParseCancellationException ex) {
            if (ex.getMessage() != null) {
                errors.add(ex.getMessage());
            } else {
                errors.add("Unrecognized parse error!");
            }
            ast = null;
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null) {
                errors.add(ex.getMessage());
            } else {
                errors.add("Unrecognized lex or parse error!");
            }
            ast = null;
        }
        return ast;
    }
    
    private static final class ParserErrorsListener extends BaseErrorListener {
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine,
                String msg, RecognitionException e) {
            throw new ParseCancellationException(
                    "line " + line + ":" + charPositionInLine + " " + msg, 
                    e
            );
        }
    }
    
    private static class ExceptionErrorStrategy extends DefaultErrorStrategy {

        @Override
        public void recover(Parser recognizer, RecognitionException e) {
            // Do not recover
            throw e;
        }
        
        private String position(int line, int column) {
            return "line " + line + ":" + column + " ";
        }

        @Override
        public void reportInputMismatch(Parser recognizer, InputMismatchException e) throws RecognitionException {
            Token tok = e.getOffendingToken();
            String msg = position(tok.getLine(), tok.getCharPositionInLine()) + 
                    "mismatched input " 
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
            String msg = position(tok.getLine(), tok.getCharPositionInLine()) 
                    + "missing " + expecting.toString(recognizer.getVocabulary()) 
                    + " at " + getTokenErrorDisplay(tok);
            throw new RecognitionException(msg, recognizer, recognizer.getInputStream(), recognizer.getContext());
        }
    }
}
