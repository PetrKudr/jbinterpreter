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
import ru.spb.petrk.interpreter.astbased.ASTBasedInterpreter;

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
    public void testCalcPi() throws Exception {
        assertEquals(
                "pi = 3.143588659585789",
                interpret(
                        "var n = 500\n" +
                        "var sequence = map({0, n}, i -> (-1)^i / (2.0 * i + 1))\n" +
                        "var pi = 4 * reduce(sequence, 0, x y -> x + y)\n" +
                        "print \"pi = \"\n" +
                        "out pi"
                )
        );
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
        return new ASTBasedInterpreter();
    }
}
