/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 * Should be used only from Swing event dispatch thread.
 * 
 * @author petrk
 */
public class InterpreterController {
    
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    
    private ScheduledFuture<?> lastTask;
    
    public void post(Runnable task) {
        assert SwingUtilities.isEventDispatchThread();
        if (lastTask != null) {
            lastTask.cancel(true);
        }
        lastTask = service.schedule(task, 250, TimeUnit.MILLISECONDS);
    }
}
