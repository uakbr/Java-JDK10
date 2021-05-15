/*
 * @(#)Object.java	1.15 95/01/31  
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
 * The root of the class hierarchy.  Every class in the system
 * has Object as its ultimate parent.  Every field and method
 * defined here is available in every object. 
 * @see		Class
 * @version 	1.15, 31 Jan 1995
 */
public class Object {
    /**
     * Returns the Class of this Object. Java has a runtime
     * representation for classes, a descriptor (a Class object),
     * which getClass returns for any object.
     * @return		the class of this object
     */
    public final native Class getClass();

    /**
     * Returns a hashcode for this object.
     * Each object in the java system has a hashcode. The hashcode
     * is a number that is usually different for different objects.
     * It is used when storing objects in hashtables.
     * Note: hashcodes can be negative as well as positive.
     * @return		the hashcode for this object.
     * @see		java.util.Hashtable
     */
    public native int hashCode();

    /**
     * Compares two objects for equality.
     * Returns a boolean that indicates whether this object is equivalent 
     * to another object. This method is used when an object is stored
     * in a hashtable.
     * @param	obj	the object to compare with
     * @return		returns true if the this object is equal to obj
     * @see		java.util.Hashtable
     */
    public boolean equals(Object obj) {
	return (this == obj);
    }

    /**
     * Copies the contents of an object into this object.  The contents
     * of an object are defined as the values of its instance variables.
     * src must be of the same class as this object.
     * @param src		the object which is copied into the current object
     * @exception	ClassCastException obj is not of the same type as
     *			this object.
     * @see		Object#clone
     */
    protected native void copy(Object src);

    /**
     * Creates a clone of this object. A new instance is allocated and
     * the copy(Object) method is called to copy the contents of this
     * object into the clone.
     * @return		a copy of this object
     * @exception	OutOfMemoryException Not enough memory
     * @see		Object#copy
     */
    protected native Object clone();

    /**
     * Returns a string that represents the value of this object.
     * @return	a printable string that represents the value of this object
     */
    public native String toString();

    /**
     * Notifies a single waiting thread. Threads that are waiting are
     * generally waiting for another thread to change some condition.
     * Thus, the thread effecting the change notifies the waiting thread
     * using notify(). Threads that want to wait for a condition to 
     * change before proceeding can call wait(). <p>
     * <em>notify() can only be called from within a synchronized method.</em>
     * @exception	InternalException Current thread is not the owner of the
     *			object's monitor.
     * @see		Object#wait
     * @see		Object#notifyall
     */
    public native void notify();

    /**
     * Notifies all of the threads waiting for a condition to change.
     * Threads that are waiting are generally waiting for another thread to 
     * change some condition. Thus, the thread effecting a change that more 
     * than one thread is waiting for notifies all the waiting threads using
     * notifyall(). Threads that want to wait for a condition to 
     * change before proceeding can call wait(). <p>
     * <em>notifyall() can only be called from within a synchronized method.</em>
     * @exception	InternalException Current thread is not the owner of the
     *			object's monitor.
     * @see		Object#wait
     * @see		Object#notify
      */
    public native void notifyAll();

    /**
     * Causes a thread to wait until it is notified or the specified timeout
     * expires. <p>
     * <em>wait() can only be called from within a synchronized method.</em>
     * @param timeout	the maximum time to wait in milliseconds
     * @exception	InternalException Current thread is not the owner of the
     *			object's monitor.
     */
    public native void wait(int timeout);

    /**
     * Causes a thread to wait until it is notified. <p>
     * <em>wait() can only be called from within a synchronized method</em>
     * @exception	InternalException Current thread is not the owner of the
     *			object's monitor.
     */
    public void wait() {
	wait(0);
    }

    /**
     * Locks the object. <em>Don't use this!</em> This was needed in the old compiler
     * to implement the synchronize statement.
     */
    public native void enterMonitor();

    /**
     * Unlock the object. <em>Don't use this!</em> This was needed in the old compiler
     * to implement the synchronize statement.
     */
    public native void exitMonitor();
}



