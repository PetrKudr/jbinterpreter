/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import junit.framework.TestCase;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;

/**
 *
 * @author petrk
 */
public class LexerTest extends TestCase {
   
    @Test
    public void testNumber() throws Exception {
        assertEquals("[INTEGER_NUMBER, 123456] [EOF]", lex("123456"));
        assertEquals("[INTEGER_NUMBER, 123] [INTEGER_NUMBER, 456] [EOF]", lex("123 456"));
        assertEquals("[DOUBLE_NUMBER, 1.0] [EOF]", lex("1.0"));
        assertEquals("[DOUBLE_NUMBER, 1.3E+3] [EOF]", lex("1.3E+3"));
        assertEquals("[DOUBLE_NUMBER, 10.05E-03] [EOF]", lex("10.05E-03"));
    }
    
    @Test
    public void testIdentifier() throws Exception {
        assertEquals("[IDENTIFIER, abc] [EOF]", lex("abc"));
        assertEquals("[IDENTIFIER, abc] [IDENTIFIER, def] [EOF]", lex("abc def"));
    }
    
    @Test
    public void testVarDeclaration() throws Exception {
        assertEquals("[LITERAL_VAR, var] [IDENTIFIER, abc] [EOF]", lex("var abc"));
    }
    
    @Test
    public void testExampleProgram() throws Exception {
        assertEquals(
                "[LITERAL_VAR, var] " 
                        + "[IDENTIFIER, n] "
                        + "[ASSIGN, =] "
                        + "[INTEGER_NUMBER, 500] "
                        + "[LITERAL_VAR, var] "
                        + "[IDENTIFIER, sequence] "
                        + "[ASSIGN, =] "
                        + "[LITERAL_MAP, map] "
                        + "[LPAREN, (] "
                        + "[LCURLY, {] "
                        + "[INTEGER_NUMBER, 0] "
                        + "[COMMA, ,] "
                        + "[IDENTIFIER, n] "
                        + "[RCURLY, }] "
                        + "[COMMA, ,] "
                        + "[IDENTIFIER, i] "
                        + "[ARROW, ->] "
                        + "[LPAREN, (] "
                        + "[MINUS, -] "
                        + "[INTEGER_NUMBER, 1] "
                        + "[RPAREN, )] "
                        + "[POWER, ^] "
                        + "[IDENTIFIER, i] "
                        + "[DIVIDE, /] "
                        + "[LPAREN, (] "
                        + "[DOUBLE_NUMBER, 2.0] "
                        + "[STAR, *] "
                        + "[IDENTIFIER, i] "
                        + "[PLUS, +] "
                        + "[INTEGER_NUMBER, 1] "
                        + "[RPAREN, )] "
                        + "[RPAREN, )] "
                        + "[LITERAL_VAR, var] "
                        + "[IDENTIFIER, pi] "
                        + "[ASSIGN, =] "
                        + "[INTEGER_NUMBER, 4] "
                        + "[STAR, *] "
                        + "[LITERAL_REDUCE, reduce] "
                        + "[LPAREN, (] "
                        + "[IDENTIFIER, sequence] "
                        + "[COMMA, ,] "
                        + "[INTEGER_NUMBER, 0] "
                        + "[COMMA, ,] "
                        + "[IDENTIFIER, x] "
                        + "[IDENTIFIER, y] "
                        + "[ARROW, ->] "
                        + "[IDENTIFIER, x] "
                        + "[PLUS, +] "
                        + "[IDENTIFIER, y] "
                        + "[RPAREN, )] "
                        + "[LITERAL_PRINT, print] "
                        + "[STRING, \"pi = \"] "
                        + "[LITERAL_OUT, out] "
                        + "[IDENTIFIER, pi] "
                        + "[EOF]", 
                lex(
                    "var n = 500\n" +
                    "var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))\n" +
                    "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
                    "print \"pi = \"\n" +
                    "out pi"
                )
        );
    }
    
    @Test
    public void testLexerError() throws Exception {
        assertEquals("line 1:6 token recognition error at: '.'", lex("abc123.54"));
        assertEquals("line 1:6 token recognition error at: '.'", lex("abc123.54 123E-1"));
    }
    
    private String lex(String input) {
        CharStream inputStream = new ANTLRInputStream(input);
        JetBrainsLanguageLexer lexer = new JetBrainsLanguageLexer(inputStream);
        
        AccumulatingErrorsListener errorsListener = new AccumulatingErrorsListener();
        lexer.addErrorListener(errorsListener);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        List<Token> tokens = new ArrayList<>();
        Token tok;
        while ((tok = tokenStream.LT(1)).getType() != Token.EOF) {
            tokens.add(tok);
            tokenStream.consume();
        }
        if (!errorsListener.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            Vocabulary vocabulary = lexer.getVocabulary();
            for (Token token : tokens) {
                sb.append('[')
                        .append(vocabulary.getSymbolicName(token.getType()))
                        .append(", ")
                        .append(token.getText())
                        .append("] ");
            }
            sb.append("[EOF]");
            return sb.toString();
        } else {
            return errorsListener.getErrorsAsString();
        }
    }
    
    /*package*/ static final class AccumulatingErrorsListener extends BaseErrorListener {
        
        private final List<String> errors = new ArrayList();
        
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
        
        public String getErrorsAsString() {
            StringBuilder sb = new StringBuilder();
            for (String error : errors) {
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(error);
            }
            return sb.toString();
        }
    }
}