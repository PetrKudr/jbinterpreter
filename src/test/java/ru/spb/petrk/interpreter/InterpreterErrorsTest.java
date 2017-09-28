/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author petrk
 */
public abstract class InterpreterErrorsTest extends AbstractInterpreterTest {
    
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
                "line 1:9 mismatched types: expected \"Number\", but found \"Sequence of Integer\"\n",
                interpret("out 1 + {1, 2}")
        );        
        assertEquals(
                "line 1:5 mismatched types: expected \"Number\", but found \"Sequence of Integer\"\n",
                interpret("out {1, 2} - 1")
        );  
        assertEquals(
                "line 1:9 mismatched types: expected \"Number\", but found \"Sequence of Integer\"\n",
                interpret("out 1 * {1, 2}")
        );        
        assertEquals(
                "line 1:5 mismatched types: expected \"Number\", but found \"Sequence of Integer\"\n",
                interpret("out {1, 2} / 1")
        );  
        assertEquals(
                "line 1:5 mismatched types: expected \"Number\", but found \"Sequence of Integer\"\n",
                interpret("out {1, 2} ^ 1")
        ); 
    }
    
    @Test
    public void testUnaryMinusWithSequence() throws Exception {
        assertEquals(
                "line 1:6 mismatched types: expected \"Number\", but found \"Sequence of Integer\"\n",
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
                "line 1:20 the type of neutral element differs from the type of sequence elements: expected \"Number\", but found \"Sequence of Number\"\n",
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
    
    @Test
    public void testReduceNumberPlusSequence() throws Exception {
        assertEquals(
                "line 2:17 the type of neutral element differs from the type of sequence elements: expected \"Sequence of Number\", but found \"Number\"\n",
                interpret(
                        "var aux = map({1, 2}, x -> {1, x})\n" +
                        "out reduce(aux, 1.0, a b -> a + b)"
                )
        );        
    }
    
    @Test
    public void testReduceSequencePlusNumber() throws Exception {
        assertEquals(
                "line 2:17 the type of neutral element differs from the type of sequence elements: expected \"Number\", but found \"Sequence of Number\"\n",
                interpret(
                        "var aux = {1, 2}\n" +
                        "out reduce(aux, {1, 1}, a b -> a + b)"
                )
        );        
    }
    
    @Test
    public void testReduceNeutralAndElemGivesWrongType() throws Exception {
        assertEquals(
                "line 2:25 if \"a\" is the neutral element (\"Sequence of Integer\") " + 
                "and \"b\" is a sequence element (\"Sequence of Integer\"), then reduction " + 
                "has \"Integer\" type, but \"Sequence of Number\" type expected\n",
                interpret(
                        "var aux = map({1, 2}, x -> {1, x})\n" +
                        "out reduce(aux, {1, 1}, a b -> reduce(a, 0, x y -> x + y) + reduce(b, 0, x y -> x + y))"
                )
        );        
    }
}
