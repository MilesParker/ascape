package org.ascape.test.model.engine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.ascape.model.Scape;
import org.ascape.runtime.NonGraphicRunner;
import org.ascape.runtime.Runner;
import org.ascape.runtime.swing.ModelApplet;

public class ParallelMultiRun extends TestCase{
    public static final int TRIALS = 5;
    
    public ParallelMultiRun() {
    }
    
    public Runner initializeModel() {
        Runner.setStartOnOpen(false);
        Runner.setDisplayGraphics(false);
        Runner.setServeGraphics(false);

        Scape rootScape = new ConwayLife();
        (new NonGraphicRunner()).setRootScape(rootScape);
        Runner modelRoot = rootScape.getRunner();
        
        modelRoot.setRestartRequested(false);
        modelRoot.setAutoRestart(false);

        modelRoot.setLatestPeriod(2500);
        return modelRoot;
    }

    /**
     * Double check that we can run an ascape model normally.
     */
    public void testSingleRun() {
        System.err.println("START: testSingleThread()");
        Runner model = initializeModel();

        model.open((ModelApplet) null, (String[]) null, false);
        model.run(true);
        System.err.println("END: testSingleThread()");
    }

    /**
     * Checks that we can run an ascape model multiple times, one after the other.
     */
    public void testMultupleRunsSerially() {
        System.err.println("START: testMultupleRunsSerially()");

        for (int i = 0; i < TRIALS; i++) {
            Runner model = initializeModel();

            model.open((ModelApplet) null, (String[]) null, false);
            model.run(true);
        }

        System.err.println("END: testMultupleRunsSerially()");
    }

    /**
     * Checks that we can run an ascape model multiple times, one after the other on a seperate
     * thread.
     * @throws InterruptedException
     */
    public void testMultipleRunsSingleThreadExecutor() throws InterruptedException {
        System.err.println("START: testMultipleRunsThreadPool()");

        ExecutorService ex = Executors.newSingleThreadExecutor();

        for (int i = 0; i < TRIALS; i++) {
            ex.submit(new Runnable() {
                public void run() {
                    Runner model = initializeModel();

                    model.open((ModelApplet) null, (String[]) null, false);
                    model.run(true);
                }
            });
        }

        ex.shutdown();
        ex.awaitTermination(300, TimeUnit.SECONDS);
        
        System.err.println("END: testMultipleRunsSingleThreadExecutor()");
    }
    
    /**
     * Tests we can run multiple ascape models at the same time on seperate threads.
     * @throws InterruptedException
     */
    public void testMultipleRunsThreadPoolExecutor() throws InterruptedException {
        System.err.println("START: testMultipleRunsThreadPool()");

        ExecutorService ex = Executors.newFixedThreadPool(TRIALS);

        for (int i = 0; i < TRIALS; i++) {
            ex.submit(new Runnable() {
                public void run() {
                    Runner model = initializeModel();

                    model.open((ModelApplet) null, (String[]) null, false);
                    model.run(true);
                }
            });
        }

        ex.shutdown();
        boolean terminatedSafely = ex.awaitTermination(300, TimeUnit.SECONDS);
        
        if(!terminatedSafely) {
            throw new InterruptedException("Timeout Period exceeded, not all tasks completed...");
        }
        
        System.err.println("END: testMultipleRunsThreadPool()");
    }
    
    public void testMultipleRunsThreadPoolExecutorSeperateOpen() throws InterruptedException {
        System.err.println("START: testMultipleRunsThreadPoolExecutorSeperateOpen()");

        ExecutorService ex = Executors.newFixedThreadPool(TRIALS);

        List<Runnable> runnables = new ArrayList<Runnable>();
        
        for(int i = 0; i < TRIALS; i++) {
            final Runner model = initializeModel();
            model.open((ModelApplet) null, (String[]) null, false);
            
            runnables.add(new Runnable() {
                public void run() {
                    model.run(true);
                }
            });
        }
        
        for(Runnable r : runnables) {
            ex.submit(r);
        }

        ex.shutdown();
        boolean terminatedSafely = ex.awaitTermination(300, TimeUnit.SECONDS);
        
        if(!terminatedSafely) {
            throw new InterruptedException("Timeout Period exceeded, not all tasks completed...");
        }

        System.err.println("END: testMultipleRunsThreadPoolExecutorSeperateOpen()");
    }
}
