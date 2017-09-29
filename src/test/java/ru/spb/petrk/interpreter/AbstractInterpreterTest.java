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
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;

/**
 *
 * @author petrk
 */
public abstract class AbstractInterpreterTest extends TestCase {
    
    protected String interpret(String input) {
        final Charset charset = StandardCharsets.UTF_8;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintStream ps = new PrintStream(baos, true, charset.name())) {
            createInterpreter().interpret(input, ps, ps, new AtomicBoolean(false));
            return new String(baos.toByteArray(), charset);
        } catch (UnsupportedEncodingException ex) {
            return "UTF8 is not supported?";
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }
    
    protected abstract Interpreter createInterpreter();
}
