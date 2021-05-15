/*
 * @(#)Thread.java	1.27 95/05/12  
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package java.lang;

/**
 * Thread objects are the basis for multi-threaded programming.  To
 * create a new thread of execution, declare a new class which is a
 * subclass of Thread.  Override the run() method with a method whose
 * body is the code you want to execute in the thread.  Then create
 * an instance of the class and call the start() method to create the
 * thread and run the run method. For example:<p>
 * <pre>
 *	class PrimeThread extends Thread {
 *	    public void run() {
 *		// compute primes...
 *	    }
 *	}
 * </pre>
 * To start this thread you need to do the following:
 * <pre>
 *	PrimeThread p = new PrimeThread();
 *	p.start();
 *	...
 * </pre>
 * Another way to create a thread is by using the Runnable interface.
 * This way any object that implements the Runnable interface can be
 * run in a thread. For example:
 * <pre>
 *	class Primes Implements Runnable {
 *	    public void run() {
 *		// compute primes...
 *	    }
 *	}
 * </pre>
 * To start this thread you need to do the following:
 * <pre>
 *	Primes p = new Primes();
 *	new Thread(p).start();
 *	...
 * </pre>
 * The interpreter runs until all threads that are not deamon threads
 * have died. A thread dies when its run method returns, or when the
 * stop method is called.<p>
 * When a new thread is created, it inherits the priority and the
 * daemon flag from its parent (ie: the thread that created it).
 * @version 	1.27, 12 May 1995
 */
public
class Thread implements Runnable {
    private char	name[];
    private int         priority;
    private Thread	threadQ;
    private int 	PrivateInfo;
    private int		eetop;

    /* Whether or not to single_step this thread. */
    private boolean	single_step;

    /* Whether or not the thread is a daemon thread. */
    private boolean	daemon = false;

    /* Set to true if thread is asked to exit before it starts running */
    private boolean	stillborn = false;

    /* What will be run. */
    private Runnable target;

    /* The system queue of threads is linked through activeThreadQueue. */
    private static Thread activeThreadQ;

    /* For autonumbering anonymous threads. */
    private static int threadInitNumber;
    private synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    /* Does this do anything? */
    private static int	SingleSteppingThreads = 0;


    /**
     * The minimum priority that a Thread can have.
     */
    public final static int MIN_PRIORITY = 1;

    /**
     * The default priority that is assigned to a Thread.
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * The maximum priority that a Thread can have.
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * Returns a reference to the currently executing thread object.
     * @returns the current thread
     */
    public static native Thread currentThread();

    /**
     * Causes the currently executing thread object to yield.
     * (i.e., if there are other runnable threads they will be
     * scheduled next).
     */
    public static native void yield();

    /**
     * Causes the currently executing thread to sleep for the specified
     * number of milliseconds.
     * @params millis	the length of time to sleep in milliseconds
     */
    public static native void sleep(int millis);

    /**
     * Constructs a new thread. Threads created this way must have
     * overridden their run() method to actually do anything.
     */
    public Thread() {
	Thread parent = currentThread();
	this.name = ("Thread-" + nextThreadNum()).toCharArray();
	this.daemon = parent.isDaemon();
	this.priority = parent.getPriority();
	setThreadPriority(priority);
    }

    /**
     * Constructs a new thread that applies the run method to
     * the specified target.
     * @param target	the object who's run method is called
     */
    public Thread(Runnable target) {
	Thread parent = currentThread();
	this.name = ("Thread-" + nextThreadNum()).toCharArray();
	this.daemon = parent.isDaemon();
	this.priority = parent.getPriority();
	this.target = target;
	setThreadPriority(priority);
    }

    /**
     * Constructs a new thread with the specified name.
     * @param name	the name of the new thread
     */
    public Thread(String name) {
	Thread parent = currentThread();
	this.name = name.toCharArray();
	this.daemon = parent.isDaemon();
	this.priority = parent.getPriority();
	setThreadPriority(priority);
    }

    /**
     * Constructs a new thread with the specified name and applies
     * the run() method on the specified target.
     * @param target	the object whose run method is called
     * @param name	the name of the new thread
     */
    public Thread(Runnable target, String name) {
	Thread parent = currentThread();
	this.name = name.toCharArray();
	this.daemon = parent.isDaemon();
	this.priority = parent.getPriority();
	this.target = target;
	setThreadPriority(priority);
    }

    /**
     * Starts a thread. This will cause the run() method to
     * be called. This method will return immediately.
     * @exception IllegalStateException The thread was already started
     * @see Thread#run
     * @see Thread#stop
     */
    public synchronized native void start();

    /**
     * Post an object to another thread, to be thrown when it
     * resumes.  If the object being posted is an instance of class
     * Exception the stack trace of the thread being posted to will
     * be filled in the instance.  This routine is used by
     * Thread.stop() to asynchronously terminate threads.
     * @param instance to post
     * @exception IllegalStateException If the target thread is not started
     */
    public native void postException(Object exception);

    /**
     * The body of the thread. This method is called after
     * the thread is started. You must either override this
     * method by subclassing class Thread, or you must create
     * the thread with a target.
     * @see Thread#start
     * @see Thread#stop
     */
    public void run() {
	if (target != null) {
	    target.run();
	}
    }

    /** 
     * Stops a thread by tossing an object.  By default this
     * routine tosses a new instance of ThreadDeath to the target
     * thread.  ThreadDeath is not actually a subclass of Exception,
     * but is a subclass of Object.  Users should not normally try
     * to catch ThreaDeath unless they must do some extraordinary
     * cleanup operation.  If ThreadDeath is caught it is important
     * to rethrow the object so that the thread will actually die.
     * The top-level error handler will not print out a message if
     * ThreadDeath falls through.
     *
     * The essential difference between this routine and
     * postException() is that if the target thread has not yet
     * started to run it will be killed imediately without trying to
     * post the object instance as an error.
     *
     * @exception IllegalStateException If the thread is not started 
     * @see Thread#start 
     * @see Thread#run
     */
    // public synchronized native void stop();
    public synchronized void stop() {
	stop(new ThreadDeath());
    }

    /**
     * Stops a thread by tossing an object.  Normally users should
     * just call Thread.Stop() with no argument.  In some
     * exceptional circumstances Used by Thread.Stop() to kill
     * anothe is tossed, "ThreadDeath", is not actually a subclass
     * of Exception, but is a subclass of Object.  The essential
     * difference between this routine and postException() is that
     * if the target thread has not yet started to run it will be
     * killed imediately without trying to post the object instance
     * as an error.
     * @exception IllegalStateException If the thread is not started 
     * @see Thread#start 
     * @see Thread#run 
     */
    public synchronized native void stop(Object o);

    /**
     * Returns a boolean indicating if the thread is active.
     * @return 	a boolean indicating whether the thread has been started
     */
    public native boolean isAlive();

    /**
     * Suspends the thread execution.
     * @exception IllegalStateException The thread is not active.
     */
    public native void suspend();

    /**
     * Resumes the thread execution.
     * @exception IllegalStateException The thread is not active.
     */
    public native void resume();

    /**
     * Sets the thread's priority.
     * @exception IllegalArgumentException The priority is not within the
     *		range MIN_PRIORITY, MAX_PRIORITY
     * @see Thread#MIN_PRIORITY
     * @see Thread#MAX_PRIORITY
     * @see Thread#getPriority
     */
    public void setPriority(int newPriority) {
	if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
	    throw new IllegalArgumentException();
	}
	setThreadPriority(priority = newPriority);
    }

    public native void setThreadPriority(int newPriority);

    /**
     * Returns the thread's priority.
     * @return the priority of the thread
     * @see Thread#setPriority
     */
    public int getPriority() {
	return priority;
    }

    /**
     * Sets the thread's name.
     * @param name	the new name of the thread
     * @see Thread#getName
     */
    public void setName(String name) {
	this.name = name.toCharArray();
    }

    /**
     * Returns the thread's name.
     * @return 	the name of the thread
     * @see Thread#setName
     */
    public String getName() {
	return String.valueOf(name);
    }

    /**
     * Returns the current number of active threads.
     * @return integer count of active threads 
     */
    public static native int activeCount();

    /**
     * Copies references to every active thread into an array.
     * @return number of Threads put into the array
     */
    public static native int enumerate(Thread tarray[]);

    /**
     * Returns the number of stack frames in this thread. The thread
     * must be suspended when this method is called.
     * @exception	IllegalStateException The thread is not suspended
     */
    public native int countStackFrames();

    /**
     * Waits for this thread to die.  A timeout in milliseconds can
     * be specified.  A timeout of 0 milliseconds means to wait
     * forever.
     * @param millis	the time to wait in milliseconds
     */
    public synchronized void join(int millis) {
	int base = System.nowMillis();
	int now = 0;

	if (millis == 0) {
	    while (isAlive()) {
		wait(0);
	    }
	} else {
	    while (isAlive()) {
		int delay = millis - now;
		if (delay <= 0) {
		    break;
		}
		wait(delay);
		now = System.nowMillis() - base;
	    }
	}
    }

    /**
     * Waits forever for this thread to die.
     */
    public void join() {
	join(0);
    }

    /**
     * A debugging procedure to print a stack trace for the
     * current thread.
     * @see Exception#printStackTrace
     */
    public static void dumpStack() {
	new Exception("Stack trace").printStackTrace();
    }

    /**
     * Marks this thread as a daemon thread or a user thread.
     * When there are only daemon threads left running in the
     * system, Java exits.
     * @param on	determines whether the thread will be a deamon thread
     * @exception IllegalStateException The thread is active
     * @see Thread#isDaemon
     */
    public void setDaemon(boolean on) {
	if (isAlive()) {
	    throw new IllegalStateException();
	}
	daemon = on;
    }

    /**
     * Returns the deamon flag of the thread.
     * @return	a boolean indicating wheter the thread is a deamon thread
     * @see Thread#setDaemon
     */
    public boolean isDaemon() {
	return daemon;
    }
}
