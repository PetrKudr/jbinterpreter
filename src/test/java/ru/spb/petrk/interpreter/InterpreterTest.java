/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;
import org.junit.Test;
import ru.spb.petrk.interpreter.astbased.ASTInterpreter;

/**
 *
 * @author petrk
 */
public class InterpreterTest extends TestCase {
    
    @Test
    public void testHelloWorld() throws Exception {
        assertEquals(
                "Hello, World!", 
                interpret("print \"Hello, World!\"")
        );
    }
    
    @Test
    public void testMapOperator() throws Exception {
        assertEquals(
                "[1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1]",
                interpret("out map({0, 10}, i -> (-1)^i)")
        );
    }
    
    @Test
    public void testPowerOperator() throws Exception {
        assertEquals(
                "512\n" + 
                "512\n" + 
                "64",
                interpret(
                        "out 2 ^ 3 ^ 2\n" +
                        "print \"\n" +
                        "\"\n" +
                        "out (2 ^ (3 ^ 2))\n" +
                        "print \"\n" +
                        "\"\n" +
                        "out ((2 ^ 3) ^ 2)"
                )
        );
    }
    
    @Test
    public void testFractionalPowerOperator() throws Exception {
        assertEquals(
                "4.0",
                interpret("out 16 ^ (1.0 / 2)")
        );
        assertEquals(
                "3.0",
                interpret("out 27 ^ (1.0 / 3)")
        );
    }
    
    @Test
    public void testNegativePowerOperator() throws Exception {
        assertEquals(
                "2.0",
                interpret("out 0.5 ^ -1")
        );
        assertEquals(
                "4.0",
                interpret("out (1 / 2.0) ^ -2")
        );
    }
    
    @Test
    public void testOperatorsPrecedence() throws Exception {
        String expected = "2.999";
        String interpreted = interpret("out 1 + 2 - 3 * 4 / 5.0 ^ 6");
        assertTrue(interpreted.startsWith(expected));
        expected = "127.0";
        interpreted = interpret("out 4.0 ^ 3 * 2 + 5 / 1 - 6");
        assertTrue(interpreted.startsWith(expected));
    }
    
    @Test
    public void testSequences() throws Exception {
        assertEquals(
                "[0, 1, 2]",
                interpret("out {0, 2}")
        );
        assertEquals(
                "[[1], [1, 2], [1, 2, 3]]",
                interpret("out map({1, 3}, x -> {1, x})")
        );
    }
    
    @Test
    public void testReduceOperator() throws Exception {
        assertEquals(
                "3",
                interpret("out reduce({1, 2}, 0, a b -> a + b)")
        );
        assertEquals(
                "6",
                interpret("out reduce(map({1, 2}, elem -> elem * 2), 0, a b -> a + b)")
        );
    }
    
    @Test
    public void testSeveralStatements() throws Exception {
        assertEquals(
                "a = 1", 
                interpret(
                        "var a = 1\n" +
                        "print \"a = \"\n" +
                        "out a"
                )
        );
    }
    
    @Test
    public void testSequenceOfSequences() throws Exception {
        assertEquals(
                "Sum([[1], [1, 2], [1, 2, 3]]) = 10",
                interpret(
                        "var seqOfSeq = map({1, 3}, val->{1, val})\n" +
                        "print \"Sum(\" \n" +
                        "out seqOfSeq\n" +
                        "print \") = \"\n" +
                        "var seqOfSum = reduce(seqOfSeq, {0, 0}, lSeq rSeq -> {\n" +
                        "  reduce(lSeq, 0, l r -> l + r) + reduce(rSeq, 0, l r -> l + r),\n" +
                        "  reduce(lSeq, 0, l r -> l + r) + reduce(rSeq, 0, l r -> l + r)\n" +
                        "})\n" +
                        "out reduce(seqOfSum, 0, l r -> l + r)"
                )
        );
    }
    
    @Test
    public void testSumOfSequences() throws Exception {
        assertEquals(
                "10",
                interpret(
                        "var seqOfSeq = map({1, 3}, n -> {1, n})\n" +
                        "var sumsOfSeq = map(seqOfSeq, x -> reduce(x, 0, l r -> l + r))\n" +
                        "out reduce(sumsOfSeq, 0, l r -> l + r)"
                )
        );
    }
    
    @Test
    public void testCalcPi() throws Exception {
        String golden = "pi = 3.1435886595857";
        String interpreted = interpret(
                "var n = 500\n" +
                "var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))\n" +
                "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
                "print \"pi = \"\n" +
                "out pi"
        );
        assertTrue(interpreted.length() >= golden.length());
        assertTrue(interpreted.startsWith(golden));
    }
    
    /*package*/static String interpret(String input) {
        final Charset charset = StandardCharsets.UTF_8;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos, true, charset.name())) {
            createInterpreter().interpret(input, ps, ps);
            return new String(baos.toByteArray(), charset);
        } catch (UnsupportedEncodingException ex) {
            return "UTF8 is not supported?";
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }
    
    private static Interpreter createInterpreter() {
        return new ASTInterpreter();
    }
}
