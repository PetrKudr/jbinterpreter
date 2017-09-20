/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.util.Arrays;
import java.util.List;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;

/**
 *
 * @author petrk
 */
public class ParseTreePrinterListener implements ParseTreeListener {

    private final static int INDENTATION = 2;

    private final List<String> ruleNames;

    private final StringBuilder treeBuilder = new StringBuilder();

    public ParseTreePrinterListener(Parser parser) {
        this.ruleNames = Arrays.asList(parser.getRuleNames());
    }

    public ParseTreePrinterListener(List<String> ruleNames) {
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
