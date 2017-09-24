/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.ui;

import java.awt.Color;
import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMap;

/**
 *
 * @author petrk
 */
public class JBSyntaxAreaTokenMaker extends AbstractTokenMaker implements TokenMaker {
    
    public static void setPredefinedScheme(RSyntaxTextArea rsta) {
        SyntaxScheme scheme = rsta.getSyntaxScheme();
        scheme.getStyle(Token.RESERVED_WORD).foreground = Color.BLUE;
        scheme.getStyle(Token.FUNCTION).font = scheme.getStyle(Token.RESERVED_WORD).font;
        scheme.getStyle(Token.FUNCTION).foreground = Color.ORANGE;
        scheme.getStyle(Token.WHITESPACE).foreground = Color.BLACK;
    }

    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tokenMap = new TokenMap();
        tokenMap.put("var", Token.RESERVED_WORD);
        tokenMap.put("print", Token.RESERVED_WORD);
        tokenMap.put("out", Token.RESERVED_WORD);
        tokenMap.put("map", Token.FUNCTION);
        tokenMap.put("reduce", Token.FUNCTION);
        return tokenMap;
    }
    
    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        // This assumes all keywords were lexed as "identifiers."
        if (tokenType == Token.IDENTIFIER) {
            int value = wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                tokenType = value;
            }
        }
        super.addToken(segment, start, end, tokenType, startOffset);
    }
    
    private int finishString(char text[], int end, int current) {
        while (current < end && text[current] != '"') {
            ++current;
        }
        return current;
    }
    
    private int finishIdentifier(char text[], int end, int current) {
        while (current < end 
                && (RSyntaxUtilities.isLetterOrDigit(text[current]) || text[current] == '_')) {
            ++current;
        }
        return current - 1;
    }
    
    private int finishWhitespace(char text[], int end, int current) {
        while (current < end 
                && !RSyntaxUtilities.isLetter(text[current]) && text[current] != '"') {
            ++current;
        }
        return current - 1;
    }

    @Override
    public Token getTokenList(Segment text, int startTokenType, int startOffset) {
        resetTokenList();

        char[] array = text.array;
        int offset = text.offset;
        int count = text.count;
        int end = offset + count;

        // Token starting offsets are always of the form:
        // 'startOffset + (currentTokenStart-offset)', but since startOffset and
        // offset are constant, tokens' starting positions become:
        // 'newStartOffset+currentTokenStart'.
        int newStartOffset = startOffset - offset;

        int tokStart = offset;
        int tokEnd = offset;
        int tokType = startTokenType;
        
        switch (tokType) {
            case Token.LITERAL_STRING_DOUBLE_QUOTE:
                tokEnd = finishString(array, end, tokStart);
                addToken(text, tokStart, Math.min(tokEnd, end - 1), tokType, newStartOffset + tokStart);
                tokStart = tokEnd + 1;
                break;
                
            default:
                assert tokType == Token.NULL;
        }
        
        while (tokStart < end) {
            char chr = array[tokStart];
            if (RSyntaxUtilities.isLetter(chr)) {
                tokType = Token.IDENTIFIER;
                tokEnd = finishIdentifier(array, end, tokStart + 1);
                addToken(text, tokStart, tokEnd, tokType, newStartOffset + tokStart);
                tokStart = tokEnd + 1;
            } else if (chr == '"') {
                tokType = Token.LITERAL_STRING_DOUBLE_QUOTE;
                tokEnd = finishString(array, end, tokStart + 1);
                addToken(text, tokStart, Math.min(tokEnd, end - 1), tokType, newStartOffset + tokStart);
                tokStart = tokEnd + 1;
            } else {
                tokType = Token.WHITESPACE;
                tokEnd = finishWhitespace(array, end, tokStart + 1);
                addToken(text, tokStart, tokEnd, tokType, newStartOffset + tokStart);
                tokStart = tokEnd + 1;
            }
        }
        
        if (tokType == Token.LITERAL_STRING_DOUBLE_QUOTE && tokEnd == end) {
            // not finished string token is ok
        } else {
            addNullToken();
        }

        return firstToken;
    }
}
