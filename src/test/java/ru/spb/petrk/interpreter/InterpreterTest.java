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
    
    private String interpret(String input) {
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
    
    private Interpreter createInterpreter() {
        return new ASTInterpreter();
    }
}
