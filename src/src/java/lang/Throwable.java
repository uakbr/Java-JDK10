/*
 * @(#)Throwable.java	1.24 95/12/06  
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
 * An object signalling that an exceptional condition has occurred.
 * All exceptions are a subclass of Exception. An exception contains
 * a snapshot of the execution stack, this snapshot is used to print
 * a stack backtrace. An exception also contains a message string.
 * Here is an example of how to catch an exception:
 * <pre>
 *	try {
 *	    int a[] = new int[2];
 *	    a[4];
 *	} catch (ArrayIndexOutOfBoundsException e) {
 *	    System.out.println("an exception occurred: " + e.getMessage());
 *	    e.printStackTrace();
 *	}
 * </pre>
 * @version 	1.24, 12/06/95
 */
public class Throwable {
    /**
     * Native code saves some indication of the stack backtrace in this
     * slot.
     */
    private Object backtrace;	
    
    /**
     * Specific details about the Throwable.  For example,
     * for FileNotFoundThrowables, this contains the name of
     * the file that could not be found.
     */
    private String detailMessage;

    /**
     * Constructs a new Throwable with no detail message. The stack
     * trace is automatically filled in.
     */
    public Throwable() {
	fillInStackTrace();
    }

    /**
     * Constructs a new Throwable with the specified detail message.
     * The stack trace is automatically filled in.
     * @param message	the detailed message
     */
    public Throwable(String message) {
	fillInStackTrace();
	detailMessage = message;
    }

    /**
     * Gets the detail message of the Throwable.  A detail message
     * is a String that describes the Throwable that has taken place.
     * @return the detail message of the throwable.
     */
    public String getMessage() {
	return detailMessage;
    }

    /**
     * Returns a short description of the Throwable.
     */
    public String toString() {
	String s = getClass().getName();
	String message = getMessage();
	return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * Prints the Throwable and the Throwable's stack trace.
     */
    public void printStackTrace() { 
        System.err.println(this);
	printStackTrace0(System.err);
    }

    public void printStackTrace(java.io.PrintStream s) { 
        s.println(this);
	printStackTrace0(s);
    }

    private native void printStackTrace0(java.io.PrintStream s);

    /**
     * Fills in the excecution stack trace. This is useful only
     * when rethrowing a Throwable. For example:
     * <p>
     * <pre>
     *	   try {
     *	        a = b / c;
     *	   } catch(ArithmeticThrowable e) {
     *		a = Number.MAX_VALUE;
     *	        throw e.fillInStackTrace();
     *	   }
     * </pre>
     * @return the Throwable itself.
     * @see Throwable#printStackTrace
     */
    public native Throwable fillInStackTrace();
}
