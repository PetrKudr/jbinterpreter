/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;
import ru.spb.petrk.ast.AST;
import ru.spb.petrk.ast.ASTKindUtils;
import ru.spb.petrk.ast.ASTUtils;
import ru.spb.petrk.ast.BinaryOperator;
import ru.spb.petrk.ast.FloatingLiteral;
import ru.spb.petrk.ast.IntegerLiteral;
import ru.spb.petrk.ast.LambdaExpr;
import ru.spb.petrk.ast.ProgramStmt;
import ru.spb.petrk.ast.RefExpr;
import ru.spb.petrk.ast.StringLiteral;
import ru.spb.petrk.ast.UnaryOperator;
import ru.spb.petrk.ast.VarDeclStmt;

/**
 *
 * @author petrk
 */
public class ParserTest {
    
    @Test
    public void testOutStatement() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    IntegerLiteral 1\n",
                parse("out 1")
        );
    }
    
    @Test
    public void testPrintStatement() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  PrintStmt \n" +
                "    StringLiteral \"Hello, World!\"\n",
                parse("print \"Hello, World!\"")
        );
    }
    
    @Test
    public void testVarDeclaration() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  VarDeclStmt a\n" +
                "    IntegerLiteral 1\n",
                parse("var a = 1")
        );
    }
    
    @Test
    public void testMapOperatorWithPower() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    MapOperator \n" +
                "      SequenceExpr \n" +
                "        IntegerLiteral 0\n" +
                "        IntegerLiteral 10\n" +
                "      LambdaExpr [i]\n" +
                "        BinaryOperator ^\n" +
                "          UnaryOperator -\n" +
                "            IntegerLiteral 1\n" +
                "          RefExpr i\n",
                parse("out map({0, 10}, i -> (-1)^i)")
        );
    }
    
    @Test
    public void testOperatorsPrecedence() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    BinaryOperator -\n" +
                "      BinaryOperator +\n" +
                "        IntegerLiteral 1\n" +
                "        IntegerLiteral 2\n" +
                "      BinaryOperator /\n" +
                "        BinaryOperator *\n" +
                "          IntegerLiteral 3\n" +
                "          IntegerLiteral 4\n" +
                "        BinaryOperator ^\n" +
                "          FloatingLiteral 5.0\n" +
                "          IntegerLiteral 6\n",
                parse("out 1 + 2 - 3 * 4 / 5.0 ^ 6")
        );
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    BinaryOperator -\n" +
                "      BinaryOperator +\n" +
                "        BinaryOperator *\n" +
                "          BinaryOperator ^\n" +
                "            FloatingLiteral 4.0\n" +
                "            IntegerLiteral 3\n" +
                "          IntegerLiteral 2\n" +
                "        BinaryOperator /\n" +
                "          IntegerLiteral 5\n" +
                "          IntegerLiteral 1\n" +
                "      IntegerLiteral 6\n",
                parse("out 4.0 ^ 3 * 2 + 5 / 1 - 6")
        );
    }
    
    @Test
    public void testSequences() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    SequenceExpr \n" +
                "      IntegerLiteral 0\n" +
                "      IntegerLiteral 2\n",
                parse("out {0, 2}")
        );
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    MapOperator \n" +
                "      SequenceExpr \n" +
                "        IntegerLiteral 1\n" +
                "        IntegerLiteral 3\n" +
                "      LambdaExpr [x]\n" +
                "        SequenceExpr \n" +
                "          IntegerLiteral 1\n" +
                "          RefExpr x\n",
                parse("out map({1, 3}, x -> {1, x})")
        );
    }
    
    @Test
    public void testReduceOperator() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    ReduceOperator \n" +
                "      SequenceExpr \n" +
                "        IntegerLiteral 1\n" +
                "        IntegerLiteral 2\n" +
                "      IntegerLiteral 0\n" +
                "      LambdaExpr [a, b]\n" +
                "        BinaryOperator +\n" +
                "          RefExpr a\n" +
                "          RefExpr b\n",
                parse("out reduce({1, 2}, 0, a b -> a + b)")
        );
        assertEquals(
                "ProgramStmt \n" +
                "  OutStmt \n" +
                "    ReduceOperator \n" +
                "      MapOperator \n" +
                "        SequenceExpr \n" +
                "          IntegerLiteral 1\n" +
                "          IntegerLiteral 2\n" +
                "        LambdaExpr [elem]\n" +
                "          BinaryOperator *\n" +
                "            RefExpr elem\n" +
                "            IntegerLiteral 2\n" +
                "      IntegerLiteral 0\n" +
                "      LambdaExpr [a, b]\n" +
                "        BinaryOperator +\n" +
                "          RefExpr a\n" +
                "          RefExpr b\n",
                parse("out reduce(map({1, 2}, elem -> elem * 2), 0, a b -> a + b)")
        );
    }
    
    @Test
    public void testSeveralStatements() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  VarDeclStmt a\n" +
                "    IntegerLiteral 1\n" +
                "  PrintStmt \n" +
                "    StringLiteral \"a = \"\n" +
                "  OutStmt \n" +
                "    RefExpr a\n", 
                parse(
                        "var a = 1\n" +
                        "print \"a = \"\n" +
                        "out a"
                )
        );
    }
    
    @Test
    public void testExampleProgram() throws Exception {
        assertEquals(
                "ProgramStmt \n" +
                "  VarDeclStmt n\n" +
                "    IntegerLiteral 500\n" +
                "  VarDeclStmt sequence\n" +
                "    MapOperator \n" +
                "      SequenceExpr \n" +
                "        IntegerLiteral 0\n" +
                "        RefExpr n\n" +
                "      LambdaExpr [i]\n" +
                "        BinaryOperator /\n" +
                "          BinaryOperator ^\n" +
                "            UnaryOperator -\n" +
                "              IntegerLiteral 1\n" +
                "            RefExpr i\n" +
                "          BinaryOperator +\n" +
                "            BinaryOperator *\n" +
                "              FloatingLiteral 2.0\n" +
                "              RefExpr i\n" +
                "            IntegerLiteral 1\n" +
                "  VarDeclStmt pi\n" +
                "    BinaryOperator *\n" +
                "      IntegerLiteral 4\n" +
                "      ReduceOperator \n" +
                "        RefExpr sequence\n" +
                "        IntegerLiteral 0\n" +
                "        LambdaExpr [x, y]\n" +
                "          BinaryOperator +\n" +
                "            RefExpr x\n" +
                "            RefExpr y\n" +
                "  PrintStmt \n" +
                "    StringLiteral \"pi = \"\n" +
                "  OutStmt \n" +
                "    RefExpr pi\n",
                parse(
                    "var n = 500\n" +
                    "var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))\n" +
                    "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
                    "print \"pi = \"\n" +
                    "out pi"
                )
        );
    }
    
    private String parse(String input) {
        List<ASTUtils.ParserError> errors = new ArrayList<>();
        ProgramStmt program = ASTUtils.parse(input, errors);
        if (program != null) {
            return ast2String(program);
        } else {
            return errors.toString();
        }
    }
    
    private static final int INDENTATION = 2;
    
    private String ast2String(AST ast) {
        StringBuilder sb = new StringBuilder();
        ast2String(sb, 0, ast);
        return sb.toString();
    }
    
    private void ast2String(StringBuilder sb, int indentation, AST ast) {
        repeat(sb, indentation, ' ');
        astPrintNodeDetails(sb, ast);
        sb.append('\n');
        for (AST child : ast.getChildren()) {
            ast2String(sb, indentation + INDENTATION, child);
        }
    }
    
    private void astPrintNodeDetails(StringBuilder sb, AST ast) {
        sb.append(getASTNodeName(ast)).append(' ');
        if (ASTKindUtils.isUnaryOperator(ast)) {
            sb.append(((UnaryOperator) ast).isMinus() ? "-" : "+");
        } else if (ASTKindUtils.isBinaryOperator(ast)) {
            switch (((BinaryOperator) ast).getOperation()) {
                case PLUS:
                    sb.append('+');
                    break;
                case MINUS:
                    sb.append('-');
                    break;
                case MULTIPLY:
                    sb.append('*');
                    break;
                case DIVIDE:
                    sb.append('/');
                    break;
                case POWER:
                    sb.append('^');
                    break;
            }
        } else if (ASTKindUtils.isFloatingLiteral(ast)) {
            sb.append(((FloatingLiteral) ast).getValue());
        } else if (ASTKindUtils.isIntegerLiteral(ast)) {
            sb.append(((IntegerLiteral) ast).getValue());
        } else if (ASTKindUtils.isLambdaExpr(ast)) {
            sb.append(((LambdaExpr) ast).getParams());
        } else if (ASTKindUtils.isRefExpr(ast)) {
            sb.append(((RefExpr) ast).getName());
        } else if (ASTKindUtils.isStringLiteral(ast)) {
            sb.append(((StringLiteral) ast).getString());
        } else if (ASTKindUtils.isVarDeclStmt(ast)) {
            sb.append(((VarDeclStmt) ast).getName());
        }
    }
    
    private void repeat(StringBuilder sb, int howMany, char chr) {
        for (int i = 0; i < howMany; ++i) {
            sb.append(chr);
        }
    }
    
    private String getASTNodeName(AST ast) {
        if (ASTKindUtils.isUnaryOperator(ast)) {
            return "UnaryOperator";
        } else if (ASTKindUtils.isBinaryOperator(ast)) {
            return "BinaryOperator";
        } else if (ASTKindUtils.isFloatingLiteral(ast)) {
            return "FloatingLiteral";
        } else if (ASTKindUtils.isIntegerLiteral(ast)) {
            return "IntegerLiteral";
        } else if (ASTKindUtils.isLambdaExpr(ast)) {
            return "LambdaExpr";
        } else if (ASTKindUtils.isMapOperator(ast)) {
            return "MapOperator";
        } else if (ASTKindUtils.isOutStmt(ast)) {
            return "OutStmt";
        } else if (ASTKindUtils.isPrintStmt(ast)) {
            return "PrintStmt";
        } else if (ASTKindUtils.isReduceOperator(ast)) {
            return "ReduceOperator";
        } else if (ASTKindUtils.isRefExpr(ast)) {
            return "RefExpr";
        } else if (ASTKindUtils.isSequenceExpr(ast)) {
            return "SequenceExpr";
        } else if (ASTKindUtils.isStringLiteral(ast)) {
            return "StringLiteral";
        } else if (ASTKindUtils.isVarDeclStmt(ast)) {
            return "VarDeclStmt";
        } else if (ASTKindUtils.isProgramStmt(ast)) {
            return "ProgramStmt";
        }
        return "Unexpected!";
    }
}
