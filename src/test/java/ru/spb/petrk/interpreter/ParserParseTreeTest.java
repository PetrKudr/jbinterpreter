/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.Test;
import ru.spb.petrk.antlr4.JetBrainsLanguageBaseListener;
import ru.spb.petrk.antlr4.JetBrainsLanguageLexer;
import ru.spb.petrk.antlr4.JetBrainsLanguageListener;
import ru.spb.petrk.antlr4.JetBrainsLanguageParser;
import ru.spb.petrk.interpreter.LexerTest.AccumulatingErrorsListener;

/**
 *
 * @author petrk
 */
public class ParserParseTreeTest extends TestCase {
    
    @Test
    public void testVarDeclaration() throws Exception {
        System.err.println(parse("var abc = 1 + 1"));
        assertEquals(
"(program\n" +
"  (stmt var abc =\n" +
"    (expr\n" +
"      (additive_expr\n" +
"        (multiplicative_expr\n" +
"          (power_expr\n" +
"            (unary_expr\n" +
"              (atom\n" +
"                (number 1\n" +
"                )\n" +
"              )\n" +
"            )\n" +
"          )\n" +
"        ) +\n" +
"        (multiplicative_expr\n" +
"          (power_expr\n" +
"            (unary_expr\n" +
"              (atom\n" +
"                (number 1\n" +
"                )\n" +
"              )\n" +
"            )\n" +
"          )\n" +
"        )\n" +
"      )\n" +
"    )\n" +
"  ) <EOF>\n" +
")", parse("var abc = 1 + 1"));
    }
    
    
    private String parse(String input) {
        CharStream inputStream = new ANTLRInputStream(input);
        JetBrainsLanguageLexer lexer = new JetBrainsLanguageLexer(inputStream);
        AccumulatingErrorsListener errorsListener = new AccumulatingErrorsListener();
        lexer.addErrorListener(errorsListener);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        JetBrainsLanguageParser parser = new JetBrainsLanguageParser(tokenStream);
        parser.setErrorHandler(new BailErrorStrategy());
        parser.addErrorListener(errorsListener);
        TreePrinterListener treePrinter = new TreePrinterListener(parser);
        ParseTreeWalker.DEFAULT.walk(treePrinter, parser.program());
        if (!errorsListener.hasErrors()) {
            return treePrinter.toString();
        } else {
            return errorsListener.getErrorsAsString();
        }
    }
    
    public class TreePrinterListener implements ParseTreeListener {
        
        private final static int INDENTATION = 2;
        
        private final List<String> ruleNames;
        
        private final StringBuilder treeBuilder = new StringBuilder();

        public TreePrinterListener(Parser parser) {
            this.ruleNames = Arrays.asList(parser.getRuleNames());
        }

        public TreePrinterListener(List<String> ruleNames) {
            this.ruleNames = ruleNames;
        }

        @Override
        public void visitTerminal(TerminalNode node) {
            if (treeBuilder.length() > 0) {
                treeBuilder.append(' ');
            }
            treeBuilder.append(Utils.escapeWhitespace(Trees.getNodeText(node, ruleNames), false));
        }

        @Override
        public void visitErrorNode(ErrorNode node) {
            if (treeBuilder.length() > 0) {
                treeBuilder.append(' ');
            }
            treeBuilder.append(Utils.escapeWhitespace(Trees.getNodeText(node, ruleNames), false));
        }

        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            if (treeBuilder.length() > 0) {
                treeBuilder.append('\n');
                repeat((ctx.depth() - 1) * INDENTATION, ' ');
            }
            if (ctx.getChildCount() > 0) {
                treeBuilder.append('(');
            }

            int ruleIndex = ctx.getRuleIndex();
            String ruleName;
            if (ruleIndex >= 0 && ruleIndex < ruleNames.size()) {
                ruleName = ruleNames.get(ruleIndex);
            } else {
                ruleName = Integer.toString(ruleIndex);
            }
            treeBuilder.append(ruleName);
        }

        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            if (ctx.getChildCount() > 0) {
                treeBuilder.append('\n');
                repeat((ctx.depth() - 1) * INDENTATION, ' ');
                treeBuilder.append(')');
            }
        }

        @Override
        public String toString() {
            return treeBuilder.toString();
        }
        
        private void repeat(int howMany, char chr) {
            for (int i = 0; i < howMany; ++i) {
                treeBuilder.append(chr);
            }
        }
    }
}
