/*
 * @(#)ThreadGroup.java	1.21 95/12/06  
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

import java.io.PrintStream;

/**
 * A group of Threads. A Thread group can contain a set of Threads
 * as well as a set of other Thread groups. A Thread can access its
 * Thread group, but it can't access the parent of its Thread group.
 * This makes it possible to encapsulate a Thread in a Thread group
 * and stop it from manipulating Threads in the parent group.
 *
 * @version 	1.21, 12/06/95
 * @author	Arthur van Hoff
 */
public
class ThreadGroup {
    ThreadGroup parent;
    String name;
    int maxPriority;
    boolean destroyed;
    boolean daemon;

    int nthreads;
    Thread threads[];

    int ngroups;
    ThreadGroup groups[];

    /**
     * Creates an empty Thread group that is not in any Thread group. 
     * This method is used to create the system Thread group.
     */
    private ThreadGroup() {	// called from C code
	this.name = "system";
	this.maxPriority = Thread.MAX_PRIORITY;
    }

    /**
     * Creates a new ThreadGroup. Its parent will be the Thread group
     * of the current Thread.
     * @param name the name of the new Thread group created
     */
    public ThreadGroup(String name) {
	this(Thread.currentThread().getThreadGroup(), name);
    }

    /**
     * Creates a new ThreadGroup with a specified name in the specified Thread group.
     * @param parent the specified parent Thread group
     * @param name the name of the new Thread group being created
     * @exception NullPointerException If the given thread group is equal to null.
     */
    public ThreadGroup(ThreadGroup parent, String name) {
	if (parent == null) {
	    throw new NullPointerException();
	}
	parent.checkAccess();
	this.name = name;
	this.maxPriority = parent.maxPriority;
	this.daemon = parent.daemon;
	this.parent = parent;
	parent.add(this);
    }

    /**
     * Gets the name of this Thread group.
     */
    public final String getName() {
	return name;
    }

    /**
     * Gets the parent of this Thread group.
     */
    public final ThreadGroup getParent() {
	return parent;
    }

    /**
     * Gets the maximum priority of the group. Threads that are
     * part of this group cannot have a higher priority than the maximum priority.
     */
    public final int getMaxPriority() {
	return maxPriority;
    }

    /**
     * Returns the daemon flag of the Thread group. A daemon Thread group
     * is automatically destroyed when it is found empty after a Thread
     * group or Thread is removed from it.
     */
    public final boolean isDaemon() {
	return daemon;
    }

    /**
     * Changes the daemon status of this group.
     * @param daemon the daemon boolean which is to be set.
     */
    public final void setDaemon(boolean daemon) {
	checkAccess();
	this.daemon = daemon;
    }

    /**
     * Sets the maximum priority of the group. Threads
     * that are already in the group <b>can</b> have a higher priority than the
     * set maximum.
     * @param pri the priority of the Thread group
     */
    public final synchronized void setMaxPriority(int pri) {
	checkAccess();
	if (pri < Thread.MIN_PRIORITY) {
	    maxPriority = Thread.MIN_PRIORITY;
	} else if (pri < maxPriority) {
	    maxPriority = pri;
	}
	for (int i = 0 ; i < ngroups ; i++) {
	    groups[i].setMaxPriority(i);
	}
    }

    /**
     * Checks to see if this Thread group is a parent of or is equal to
     * another Thread group.
     * @param g the Thread group to be checked
     * @return true if this Thread group is equal to or is the parent of another Thread
     * group; false otherwise.
     */
    public final boolean parentOf(ThreadGroup g) {
	for (; g != null ; g = g.parent) {
	    if (g == this) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Checks to see if the current Thread is allowed to modify this group.
     * @exception SecurityException If the current Thread is not allowed 
     * to access this Thread group.
     */
    public final void checkAccess() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkAccess(this);
	}
    }

    /**
     * Returns an estimate of the number of active Threads in the
     * Thread group.
     */
    public synchronized int activeCount() {
	int n = nthreads;
	for (int i = 0 ; i < ngroups ; i++) {
	    n += groups[i].activeCount();
	}
	return n;
    }

    /**
     * Copies, into the specified array, references to every active Thread in this Thread group.
     * You can use the activeCount() method to get an estimate of how big
     * the array should be.
     * @param list an array of Threads
     * @return the number of Threads put into the array
     */
    public int enumerate(Thread list[]) {
	return enumerate(list, 0, true);
    }

    /**
     * Copies, into the specified array, references to every active Thread in this Thread group.
     * You can use the activeCount() method to get an estimate of how big
     * the array should be.
     * @param list an array list of Threads
     * @param recurse a boolean indicating whether a Thread has reapearred
     * @return the number of Threads placed into the array.
     */
    public int enumerate(Thread list[], boolean recurse) {
	return enumerate(list, 0, recurse);
    }

    private synchronized int enumerate(Thread list[], int n, boolean recurse) {
	int nt = nthreads;
	if (nt > list.length - n) {
	    nt = list.length - n;
	}
	if (nt > 0) {
	    System.arraycopy(threads, 0, list, n, nt);
	    n += nt;
	}
	if (recurse) {
	    for (int i = 0 ; i < ngroups ; i++) {
		n = groups[i].enumerate(list, n, true);
	    }
	}
	return n;
    }

    /**
     * Returns an estimate of the number of active groups in the
     * Thread group.
     */
    public synchronized int activeGroupCount() {
	int n = ngroups;
	for (int i = 0 ; i < ngroups ; i++) {
	    n += groups[i].activeGroupCount();
	}
	return n;
    }

    /**
     * Copies, into the specified array, references to every active Thread group in this Thread 
     * group.  You can use the activeGroupCount() method to get an estimate of how big
     * the array should be.
     * @param list an array of Thread groups
     * @return the number of Thread groups placed into the array.
     */
    public int enumerate(ThreadGroup list[]) {
	return enumerate(list, 0, true);
    }

    /**
     * Copies, into the specified array, references to every active Thread group in this Thread 
     * group.  You can use the activeGroupCount() method to get an estimate of how big
     * the array should be.
     * @param list an array list of Thread groups
     * @param recurse a boolean indicating if a Thread group has reappeared
     * @return the number of Thread groups placed into the array.
     */
    public int enumerate(ThreadGroup list[], boolean recurse) {
	return enumerate(list, 0, recurse);
    }

    private synchronized int enumerate(ThreadGroup list[], int n, boolean recurse) {
	int ng = ngroups;
	if (ng > list.length - n) {
	    ng = list.length - n;
	}
	if (ng > 0) {
	    System.arraycopy(groups, 0, list, n, ng);
	    n += ng;
	}
	if (recurse) {
	    for (int i = 0 ; i < ngroups ; i++) {
		n = groups[i].enumerate(list, n, true);
	    }
	}
	return n;
    }

    /**
     * Stops all the Threads in this Thread group and all of its sub groups.
     */
    public final synchronized void stop() {
	checkAccess();
	for (int i = 0 ; i < ngroups ; i++) {
	    groups[i].stop();
	}
	for (int i = 0 ; i < nthreads ; i++) {
	    threads[i].stop();
	}
    }

    /**
     * Suspends all the Threads in this Thread group and all of its sub groups.
     */
    public final synchronized void suspend() {
	checkAccess();
	for (int i = 0 ; i < ngroups ; i++) {
	    groups[i].suspend();
	}
	for (int i = 0 ; i < nthreads ; i++) {
	    threads[i].suspend();
	}
    }

    /**
     * Resumes all the Threads in this Thread group and all of its sub groups.
     */
    public final synchronized void resume() {
	checkAccess();
	for (int i = 0 ; i < ngroups ; i++) {
	    groups[i].resume();
	}
	for (int i = 0 ; i < nthreads ; i++) {
	    threads[i].resume();
	}
    }

    /**
     * Destroys a Thread group. This does <b>NOT</b> stop the Threads
     * in the Thread group.
     * @exception IllegalThreadStateException If the Thread group is not empty
     * 		or if the Thread group was already destroyed.
     */
    public final synchronized void destroy() {
	checkAccess();
	if (destroyed || (nthreads > 0)) {
	    throw new IllegalThreadStateException();
	}
	while (ngroups > 0) {
	    groups[0].destroy();
	}
	if (parent != null) {
	    destroyed = true;
	    groups = null;
	    threads = null;
	    parent.remove(this);
	}
    }

    /**
     * Adds the specified Thread group to this group.
     * @param g the specified Thread group to be added
     * @exception IllegalThreadStateException If the Thread group has been destroyed.
     */
    private final synchronized void add(ThreadGroup g){
	if (destroyed) {
	    throw new IllegalThreadStateException();
	}
	if (groups == null) {
	    groups = new ThreadGroup[4];
	} else if (ngroups == groups.length) {
	    ThreadGroup newgroups[] = new ThreadGroup[ngroups * 2];
	    System.arraycopy(groups, 0, newgroups, 0, ngroups);
	    groups = newgroups;
	}
	groups[ngroups] = g;

	// This is done last so it doesn't matter in case the
	// thread is killed
	ngroups++;
    }

    /**
     * Removes the specified Thread group from this group.
     * @param g the Thread group to be removed
     * @return if this Thread has already been destroyed.
     */
    private synchronized void remove(ThreadGroup g) {
	if (destroyed) {
	    return;
	}
	for (int i = 0 ; i < ngroups ; i++) {
	    if (groups[i] == g) {
		System.arraycopy(groups, i + 1, groups, i, --ngroups - i);
		// Zap dangling reference to the dead group so that
		// the garbage collector will collect it.
		groups[ngroups] = null;
		break;
	    }
	}
	if (nthreads == 0) {
	    notifyAll();
	}
	if (daemon && (nthreads == 0) && (ngroups == 0)) {
	    destroy();
	}
    }
    
    /**
     * Adds the specified Thread to this group.
     * @param t the Thread to be added
     * @exception IllegalThreadStateException If the Thread group has been destroyed.
     */
    synchronized void add(Thread t) {
	if (destroyed) {
	    throw new IllegalThreadStateException();
	}
	if (threads == null) {
	    threads = new Thread[4];
	} else if (nthreads == threads.length) {
	    Thread newthreads[] = new Thread[nthreads * 2];
	    System.arraycopy(threads, 0, newthreads, 0, nthreads);
	    threads = newthreads;
	}
	threads[nthreads] = t;

	// This is done last so it doesn't matter in case the
	// thread is killed
	nthreads++;
    }

    /**
     * Removes the specified Thread from this group.
     * @param t the Thread to be removed
     * @return if the Thread has already been destroyed.
     */
    synchronized void remove(Thread t) {
	if (destroyed) {
	    return;
	}
	for (int i = 0 ; i < nthreads ; i++) {
	    if (threads[i] == t) {
		System.arraycopy(threads, i + 1, threads, i, --nthreads - i);
		// Zap dangling reference to the dead thread so that
		// the garbage collector will collect it.
		threads[nthreads] = null;
		break;
	    }
	}
	if (nthreads == 0) {
	    notifyAll();
	}
	if (daemon && (nthreads == 0) && (ngroups == 0)) {
	    destroy();
	}
    }

    /**
     * Lists this Thread group. Useful for debugging only.
     */
     public synchronized void list() {
	list(System.out, 0);
     }
     void list(PrintStream out, int indent) {
	for (int j = 0 ; j < indent ; j++) {
	    out.print(" ");
	}
	out.println(this);
	indent += 4;
	for (int i = 0 ; i < nthreads ; i++) {
	    for (int j = 0 ; j < indent ; j++) {
		out.print(" ");
	    }
	    out.println(threads[i]);
	}
	for (int i = 0 ; i < ngroups ; i++) {
	    groups[i].list(out, indent);
	}
     }

    /**
     * Called when a thread in this group exists because of
     * an uncaught exception.
     */
    public void uncaughtException(Thread t, Throwable e) {
	if (parent != null) {
	    parent.uncaughtException(t, e);
	} else if (!(e instanceof ThreadDeath)) {
	    e.printStackTrace(System.err);
	}
    }

    /**
     * Returns a String representation of the Thread group.
     */
    public String toString() {
	return getClass().getName() + "[name=" + getName() + ",maxpri=" + maxPriority + "]";
    }
}
