/*
 * @(#)Thread.java	1.47 95/12/03  
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
 * A Thread is a single sequential flow of control within a process. This simply means
 * that while executing within a program, each thread has a beginning, a sequence, a 
 * point of execution occurring at any time during runtime of the thread and of course, an ending. 
 * Thread objects are the basis for multi-threaded programming.  Multi-threaded programming
 * allows a single program to conduct concurrently running threads that perform different tasks.
 * <p>
 * To create a new thread of execution, declare a new class which is a
 * subclass of Thread and then override the run() method with code that you
 * want executed in this Thread.  An instance of the Thread subclass should be created next
 * with a call to the start() method following the instance.  The start() method will create the 
 * thread and execute the run() method. 
 * For example:<p>
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
 *	class Primes implements Runnable {
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
 * The virtual machine runs until all Threads that are not daemon Threads
 * have died. A Thread dies when its run() method returns, or when the
 * stop() method is called.
 * <p>
 * When a new Thread is created, it inherits the priority and the
 * daemon flag from its parent (i.e.: the Thread that created it).
 * @see Runnable
 * @version 	1.47, 12/03/95
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

    /* Whether or not this thread was asked to exit before it runs.*/
    private boolean	stillborn = false;

    /* What will be run. */
    private Runnable target;

    /* The system queue of threads is linked through activeThreadQueue. */
    private static Thread activeThreadQ;

    /* The group of this thread */
    private ThreadGroup group;

    /* For autonumbering anonymous threads. */
    private static int threadInitNumber;
    private static synchronized int nextThreadNum() {
	return threadInitNumber++;
    }

    /**
     * The minimum priority that a Thread can have.  The most minimal priority is equal to 1.      
     */
    public final static int MIN_PRIORITY = 1;

    /**
     * The default priority that is assigned to a Thread.  The default priority is equal to 5.
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * The maximum priority that a Thread can have.  The maximal priority value a Thread can have is 10.
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * Returns a reference to the currently executing Thread object.
     */
    public static native Thread currentThread();

    /**
     * Causes the currently executing Thread object to yield.
     * If there are other runnable Threads they will be
     * scheduled next.
     */
    public static native void yield();

    /**	
     * Causes the currently executing Thread to sleep for the specified
     * number of milliseconds.
     * @param millis  the length of time to sleep in milliseconds
     * @exception InterruptedException 
     *            Another thread has interrupted this thread.      */
    public static native void sleep(long millis) throws InterruptedException;

    /**
     * Sleep, in milliseconds and additional nanosecond.
     * @param millis  the length of time to sleep in milliseconds
     * @param nanos   0-999999 additional nanoseconds to sleep
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public static void sleep(long millis, int nanos) throws InterruptedException {
	if (nanos > 500000)
		millis++;
	sleep(millis);
    }

    /**
     * Initialize a Thread.
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     */
    private void init(ThreadGroup g, Runnable target, String name){
	Thread parent = currentThread();
	if (g == null) {
	    g = parent.getThreadGroup();
	} else {
	    g.checkAccess();
	}
	this.group = g;
	this.daemon = parent.isDaemon();
	this.priority = parent.getPriority();
	this.name = name.toCharArray();
	this.target = target;
	setPriority0(priority);
	g.add(this);
    }

    /**
     * Constructs a new Thread. Threads created this way must have
     * overridden their run() method to actually do anything.  An example
     * illustrating this method being used is shown.
     * <p><pre>
     * import java.lang.*; 
     * <p>
     * class plain01 implements Runnable {
     *   String name; 
     *   plain01() {
     *     name = null;
     *   }
     *   plain01(String s) {
     *     name = s;
     *   }
     *   public void run() {
     *     if (name == null)
     *       System.out.println("A new thread created");
     *     else
     *       System.out.println("A new thread with name " + name + " created");
     *   }
     * }<p>
     * class threadtest01 {
     *   public static void main(String args[] ) {
     *     int failed = 0 ;
     * <p>
     * <b>   Thread t1 = new Thread();</b>  
     *     if(t1 != null) {
     *       System.out.println("new Thread() succeed");
     *     } else {
     *        System.out.println("new Thread() failed"); 
     *        failed++; 
     *     } </pre>
     * } <p>
     */
    public Thread() {
	init(null, null, "Thread-" + nextThreadNum());
    }

    /**
     * Constructs a new Thread which applies the run() method of
     * the specified target.  
     * @param target	the object whose run() method is called
     */
    public Thread(Runnable target) {
	init(null, target, "Thread-" + nextThreadNum());
    }

    /**
     * Constructs a new Thread in the specified Thread group that applies the run() method of
     * the specified target. 
     * @param group the Thread group
     * @param target	the object whose run() method is called
     */
    public Thread(ThreadGroup group, Runnable target) {
	init(group, target, "Thread-" + nextThreadNum());
    }

    /**
     * Constructs a new Thread with the specified name.  
     * @param name	the name of the new Thread
     */
    public Thread(String name) {
	init(null, null, name);
    }

    /**
     * Constructs a new Thread in the specified Thread group with the specified name.
     * @param group the Thread group
     * @param name	the name of the new Thread
     */
    public Thread(ThreadGroup group, String name) {
	init(group, null, name);
    }

    /**
     * Constructs a new Thread with the specified name and applies
     * the run() method of the specified target.  
     * @param target	the object whose run() method is called
     * @param name	the name of the new Thread
     */
    public Thread(Runnable target, String name) {
	init(null, target, name);
    }
    /**
     * Constructs a new Thread in the specified Thread group with the specified name and
     * applies the run() method of the specified target.
     * @param group the Thread group
     * @param target the object whose run() method is called
     * @param name the name of the new Thread
     */
    public Thread(ThreadGroup group, Runnable target, String name) {
	init(group, target, name);
    }

    /**
     * Starts this Thread. This will cause the run() method to
     * be called. This method will return immediately.
     * @exception IllegalThreadStateException If the thread was already started.
     * @see Thread#run
     * @see Thread#stop
     */
    public synchronized native void start();

    /**
     * The actual body of this Thread. This method is called after
     * the Thread is started. You must either override this
     * method by subclassing class Thread, or you must create
     * the Thread with a Runnable target.
     * @see Thread#start
     * @see Thread#stop
     */
    public void run() {
	if (target != null) {
	    target.run();
	}
    }

    /**
     * This method is called by the system to give a Thread
     * a chance to clean up before it actually exits.
     */
    private void exit() {
	if (group != null) {
	    group.remove(this);
	    group = null;
	}
    }

    /** 
     * Stops a Thread by tossing an object.  By default this
     * routine tosses a new instance of ThreadDeath to the target
     * Thread.  ThreadDeath is not actually a subclass of Exception,
     * but is a subclass of Object.  Users should not normally try
     * to catch ThreadDeath unless they must do some extraordinary
     * cleanup operation.  If ThreadDeath is caught it is important
     * to rethrow the object so that the thread will actually die.
     * The top-level error handler will not print out a message if
     * ThreadDeath falls through.
     *
     * @see Thread#start 
     * @see Thread#run
     */
    public final void stop() {
	stop(new ThreadDeath());
    }

    /**
     * Stops a Thread by tossing an object.  Normally, users should
     * just call the stop() method without any argument.  However, in some
     * exceptional circumstances used by the stop() method to kill a Thread,
     * another object is tossed. ThreadDeath, is not actually a subclass
     * of Exception, but is a subclass of Throwable
     * @param o the Throwable object to be thrown
     * @see Thread#start 
     * @see Thread#run 
     */
    public final synchronized void stop(Throwable o) {
	checkAccess();
	stop0(o);
    }

    /**
     * Send an interrupt to a thread.
     */
    public void interrupt() {
	throw new NoSuchMethodError();
    }

    /**
     * Ask if you have been interrupted.
     */
    public static boolean interrupted() {
	throw new NoSuchMethodError();
    }

    /**
     * Ask if another thread has been interrupted.
     */
    public boolean isInterrupted() {
	throw new NoSuchMethodError();
    }

    /**
     * Destroy a thread, without any cleanup, i.e. just toss its state;
     * any monitors it has locked remain locked.  A last resort.
     */
    public void destroy() {
	throw new NoSuchMethodError();
    }

    /**
     * Returns a boolean indicating if the Thread is active.  Having an 
     * active Thread means that the Thread has been started and has not
     * been stopped.
     */
    public final native boolean isAlive();

    /**
     * Suspends this Thread's execution.
     */
    public final void suspend() {
	checkAccess();
	suspend0();
    }

    /**
     * Resumes this Thread execution.  This method is only valid after suspend()
     * has been invoked.
     */
    public final void resume() {
	checkAccess();
	resume0();
    }

    /**
     * Sets the Thread's priority.
     * @exception IllegalArgumentException If the priority is not within the
     *		range MIN_PRIORITY, MAX_PRIORITY.
     * @see Thread#MIN_PRIORITY
     * @see Thread#MAX_PRIORITY
     * @see Thread#getPriority
     */
    public final void setPriority(int newPriority) {
	checkAccess();
	if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
	    throw new IllegalArgumentException();
	}
	if (newPriority > group.getMaxPriority()) {
	    newPriority = group.getMaxPriority();
	}
	setPriority0(priority = newPriority);
    }

    /**
     * Gets and returns the Thread's priority.
     * @see Thread#setPriority
     */
    public final int getPriority() {
	return priority;
    }

    /**
     * Sets the Thread's name.
     * @param name	the new name of the Thread
     * @see Thread#getName
     */
    public final void setName(String name) {
	checkAccess();
	this.name = name.toCharArray();
    }

    /**
     * Gets and returns this Thread's name.
     * @see Thread#setName
     */
    public final String getName() {
	return String.valueOf(name);
    }

    /**
     * Gets and returns this Thread group.
     */
    public final ThreadGroup getThreadGroup() {
	return group;
    }


    /**
     * Returns the current number of active Threads in this Thread group.
     */
    public static int activeCount() {
	return currentThread().getThreadGroup().activeCount();
    }

    /**
     * Copies, into the specified array, references to every active Thread in this 
     * Thread's group.
     * @return the number of Threads put into the array.
     */
    public static int enumerate(Thread tarray[]) {
	return currentThread().getThreadGroup().enumerate(tarray);
    }

    /**
     * Returns the number of stack frames in this Thread. The Thread
     * must be suspended when this method is called.
     * @exception	IllegalThreadStateException If the Thread is not suspended.
     */
    public native int countStackFrames();

    /**
     * Waits for this Thread to die.  A timeout in milliseconds can
     * be specified.  A timeout of 0 milliseconds means to wait
     * forever.
     * @param millis	the time to wait in milliseconds
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public final synchronized void join(long millis) throws InterruptedException {
	long base = System.currentTimeMillis();
	long now = 0;

	if (millis == 0) {
	    while (isAlive()) {
		wait(0);
	    }
	} else {
	    while (isAlive()) {
		long delay = millis - now;
		if (delay <= 0) {
		    break;
		}
		wait(delay);
		now = System.currentTimeMillis() - base;
	    }
	}
    }

    /**
     * Waits for the Thread to die, with more precise time.
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public final synchronized void join(long millis, int nanos) throws InterruptedException {
	if (nanos >= 500000 || millis == 0)
		millis++;
	join(millis);
    }


    /**
     * Waits forever for this Thread to die.
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public final void join() throws InterruptedException {
	join(0);
    }

    /**
     * A debugging procedure to print a stack trace for the
     * current Thread.
     * @see Throwable#printStackTrace
     */
    public static void dumpStack() {
	new Exception("Stack trace").printStackTrace();
    }

    /**
     * Marks this Thread as a daemon Thread or a user Thread.
     * When there are only daemon Threads left running in the
     * system, Java exits.
     * @param on	determines whether the Thread will be a daemon Thread
     * @exception IllegalThreadStateException If the Thread is active.
     * @see Thread#isDaemon
     */
    public final void setDaemon(boolean on) {
	checkAccess();
	if (isAlive()) {
	    throw new IllegalThreadStateException();
	}
	daemon = on;
    }

    /**
     * Returns the daemon flag of the Thread.
     * @see Thread#setDaemon
     */
    public final boolean isDaemon() {
	return daemon;
    }

    /**
     * Checks whether the current Thread is allowed to modify this Thread.
     * @exception SecurityException If the current Thread is not allowed 
     * to access this Thread group.
     */
    public void checkAccess() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkAccess(this);
	}
    }

    /**
     * Returns a String representation of the Thread, including the 
     * thread's name, priority and thread group.
     */
    public String toString() {
	return "Thread[" + getName() + "," + getPriority() + "," + 
			getThreadGroup().getName() + "]";
    }

    /* Some private helper methods */
    private native void setPriority0(int newPriority);
    private native void stop0(Object o);
    private native void suspend0();
    private native void resume0();
}
