/*
 * @(#)Lock.java	1.7 95/01/31  
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

package java.util;

/**
 * The Lock class provides a simple, useful interface to a lock.
 * Unlike monitors which synchronize access to an object, locks
 * synchronize access to an arbitrary set of resources (objects,
 * methods, variables, etc.). <p>
 *
 * The programmer using locks must be responsible for clearly defining
 * the semantics of their use and should handle deadlock avoidance in
 * the face of exceptions. <p>
 *
 * For example, if you want to protect a set of method invocations with
 * a lock, and one of the methods may throw an exception, you must be
 * prepared to release the lock similarly to the following example:
 * <pre>
 *	class SomeClass {
 *	    Lock myLock = new Lock();

 *	    void someMethod() {
 *	        myLock.lock();
 *		try {
 *	    	    StartOperation();
 *		    ContinueOperation();
 *		    EndOperation();
 *		} finally {
 *	    	    myLock.unlock();
 *		}
 *	    }
 *	}
 * </pre>
 *
 * @version 	1.7, 31 Jan 1995
 * @author 	Peter King
 */
public
class Lock {
    private boolean locked = false;

    /**
     * Create a lock, which is initially not locked.
     */
    public Lock () {
    }

    /**
     * Acquire the lock.  If someone else has the lock, wait until it
     * has been freed, and then try to acquire it again.  This method
     * will not return until the lock has been acquired.
     */
    public final synchronized void lock() {
	while (locked) {
	    wait();
	}
	locked = true;
    }

    /**
     * Release the lock.  If someone else is waiting for the lock, the
     * will be notitified so they can try to acquire the lock again.
     */
    public final synchronized void unlock() {
	locked = false;
	notifyAll();
    }
}
