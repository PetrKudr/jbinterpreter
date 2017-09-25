/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;
import static ru.spb.petrk.interpreter.InterpreterTest.interpret;

/**
 *
 * @author petrk
 */
public class InterpreterErrorsTest extends TestCase {
    
    @Test
    public void testIncorrectSequence() throws Exception {
        assertEquals(
                "line 1:6 mismatched types: expected \"Integer\", but found \"Float\"\n",
                interpret("out {0.5, 1}")
        );
        assertEquals(
                "line 1:9 mismatched types: expected \"Integer\", but found \"Float\"\n",
                interpret("out {1, 1.5}")
        );
    }
    
    @Test
    public void testBinaryOperationWithSequence() throws Exception {
        assertEquals(
                "line 1:9 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out 1 + {1, 2}")
        );        
        assertEquals(
                "line 1:5 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out {1, 2} - 1")
        );  
        assertEquals(
                "line 1:9 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out 1 * {1, 2}")
        );        
        assertEquals(
                "line 1:5 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out {1, 2} / 1")
        );  
        assertEquals(
                "line 1:5 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out {1, 2} ^ 1")
        ); 
    }
    
    @Test
    public void testUnaryMinusWithSequence() throws Exception {
        assertEquals(
                "line 1:6 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out -{1, 2}")
        );        
    }
    
    @Test
    public void testMapOnFloat() throws Exception {
        assertEquals(
                "line 1:9 mismatched types: expected \"Sequence\", but found \"Float\"\n",
                interpret("out map(1.5, x -> x + 1)")
        );        
    }
    
    @Test
    public void testReduceOnInteger() throws Exception {
        assertEquals(
                "line 1:12 mismatched types: expected \"Sequence\", but found \"Integer\"\n",
                interpret("out reduce(5, 0, x y -> x + 1)")
        );        
    }
    
    @Test
    public void testReduceWithWrongNeutral() throws Exception {
        assertEquals(
                "line 1:35 mismatched types: expected \"Number\", but found \"Sequence\"\n",
                interpret("out reduce({0, 1}, {0, 0}, x y -> x + 1)")
        );        
    }
    
    @Test
    public void testReduceWithIncorrectLambdaParams() throws Exception {
        assertEquals(
                "line 1:23 parameters of the lambda cannot have the same name\n",
                interpret("out reduce({0, 1}, 0, x x -> x + 1)")
        );        
    }
    
    @Test
    public void testUnresolvedVariable() throws Exception {
        assertEquals(
                "line 1:5 unresolved variable: \"unknown\"\n",
                interpret("out unknown")
        );        
    }
    
    @Test
    public void testRedeclaredVariable() throws Exception {
        assertEquals(
                "line 2:1 redeclaration of variable \"a\"\n",
                interpret(
                        "var a = 1\n" +
                        "var a = 2"
                )
        );        
    }
}
