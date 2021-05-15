/*
 * @(#)Object.java	1.32 95/12/03  
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
 * The root of the Class hierarchy.  Every Class in the system
 * has Object as its ultimate parent.  Every variable and method
 * defined here is available in every Object. 
 * @see		Class
 * @version 	1.32, 12/03/95
 */
public class Object {
    /**
     * Returns the Class of this Object. Java has a runtime
     * representation for classes- a descriptor of type Class- 
     * which the method getClass() returns for any Object.
     */
    public final native Class getClass();

    /**
     * Returns a hashcode for this Object.
     * Each Object in the Java system has a hashcode. The hashcode
     * is a number that is usually different for different Objects.
     * It is used when storing Objects in hashtables.
     * Note: hashcodes can be negative as well as positive.
     * @see		java.util.Hashtable
     */
    public native int hashCode();

    /**
     * Compares two Objects for equality.
     * Returns a boolean that indicates whether this Object is equivalent 
     * to the specified Object. This method is used when an Object is stored
     * in a hashtable.
     * @param	obj	the Object to compare with
     * @return	true if these Objects are equal; false otherwise.
     * @see		java.util.Hashtable
     */
    public boolean equals(Object obj) {
	return (this == obj);
    }

    /**
     * Creates a clone of the object. A new instance is allocated and a 
     * bitwise clone of the current object is place in the new object.
     * @return		a clone of this Object.
     * @exception	OutOfMemoryError If there is not enough memory.
     * @exception	CloneNotSupportedException Object explicitly does not
     *                      want to be cloned, or it does not support the
     *                      Cloneable interface.
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * Returns a String that represents the value of this Object.  It is recommended
     * that all subclasses override this method.
     */
    public String toString() {
	return getClass().getName() + "@" + 
                     Integer.toString(hashCode() << 1 >>> 1, 16);
    }

    /**
     * Notifies a single waiting thread on a change in condition of another thread. 
     * The thread effecting the change notifies the waiting thread
     * using notify(). Threads that want to wait for a condition to 
     * change before proceeding can call wait(). <p>
     * <em>The method notify() can only be called from within a synchronized method.</em>
     *
     * @exception	IllegalMonitorStateException If the current thread
     *			    is not the owner of the Object's monitor.
     * @see		Object#wait
     * @see		Object#notifyAll
     */
    public final native void notify();

    /**
     * Notifies all of the threads waiting for a condition to change.
     * Threads that are waiting are generally waiting for another thread to 
     * change some condition. Thus, the thread effecting a change that more 
     * than one thread is waiting for notifies all the waiting threads using
     * the method notifyAll(). Threads that want to wait for a condition to 
     * change before proceeding can call wait(). <p>
     * <em>The method notifyAll() can only be called from within a synchronized method.</em>
     *
     * @exception	IllegalMonitorStateException If the current thread
     * 			    is not the owner of the Object's monitor.
     * @see		Object#wait
     * @see		Object#notify
     */
    public final native void notifyAll();

    /**
     * Causes a thread to wait until it is notified or the specified timeout
     * expires. <p>
     * <em>The method wait() can only be called from within a synchronized method.</em>
     *
     * @param timeout	the maximum time to wait in milliseconds
     * @exception	IllegalMonitorStateException If the current thread
     *			    is not the owner of the Object's monitor.
     * @exception 	InterruptedException Another thread has interrupted
     *			    this thread. 
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * More accurate wait.
     * <em>The method wait() can only be called from within a synchronized method.</em>
     *
     * @param timeout	the maximum time to wait in milliseconds
     * @param nano      additional time, in nanoseconds range 0-999999
     * @exception	IllegalMonitorStateException If the current thread
     *			    is not the owner of the Object's monitor.
     * @exception 	InterruptedException Another thread has interrupted
     *			    this thread. 
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
	if (nanos >= 500000 || (nanos != 0 && timeout==0))
	    timeout++;
	wait(timeout);
    }

    /**
     * Causes a thread to wait forever until it is notified. <p>
     * <em>The method wait() can only be called from within a synchronized method</em>
     *
     * @exception	IllegalMonitorStateException If the current thread
     *			    is not the owner of the Object's monitor.
     * @exception 	InterruptedException Another thread has interrupted
     *			    this thread. 
     */
    public final void wait() throws InterruptedException {
	wait(0);
    }

    /**
     * Code to perform when this object is garbage collected.  
     * The default is that nothing needs to be performed.
     * 
     * Any exception thrown by a finalize method causes the finalization to
     * halt.  But otherwise, it is ignored.
     */
    protected void finalize() throws Throwable { }
}

