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
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
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
        ParserErrorsListener errorsListener = new ParserErrorsListener(errors);
        
        // Init lexer
        CharStream inputStream = new ANTLRInputStream(input);
        JetBrainsLanguageLexer lexer = new JetBrainsLanguageLexer(inputStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorsListener);
        
        TokenStream tokenStream = new CommonTokenStream(lexer);
        
        // Init parser
        JetBrainsLanguageParser parser = new JetBrainsLanguageParser(tokenStream);
        parser.removeErrorListeners();
        parser.setErrorHandler(new BailErrorStrategy());
        
        ASTBuilder builder = new ASTBuilder();
        ProgramStmt ast;
        try {
            ast = builder.visitProgram(parser.program());
        } catch (ParseCancellationException ex) {
            errorsListener.addError("Parse error!");
            ast = null;
        }
        return ast;
    }
    
    private static final class ParserErrorsListener extends BaseErrorListener {
        
        private final List<String> errors;

        public ParserErrorsListener(List<String> errors) {
            this.errors = errors;
        }
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine,
                String msg, RecognitionException e) {
            errors.add("line " + line + ":" + charPositionInLine + " " + msg);
        }
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
