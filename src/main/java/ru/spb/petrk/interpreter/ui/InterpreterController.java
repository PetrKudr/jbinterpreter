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
    
    private ShowInterpretationTask lastTask;
    
    private ScheduledFuture<?> lastTaskFuture;
    
    public void post(ShowInterpretationTask task) {
        assert SwingUtilities.isEventDispatchThread();
        if (lastTask != null) {
            assert lastTaskFuture != null;
            lastTaskFuture.cancel(false); // no need to interrupt thread, that will be done via canceller
            lastTask.cancel();
        }
        lastTask = task;
        lastTaskFuture = service.schedule(task, 250, TimeUnit.MILLISECONDS);
    }
}
