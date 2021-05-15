/*
 * @(#)Exception.java	1.12 95/01/31  
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

import java.lang.*;

/**
 * An object signalling that an exceptional condition has occurred.
 * All exceptions are a subclass of Exception. An exception contains
 * a snapshot of the execution stack, this snapshot is used to print
 * a stack backtrace. An exception also contains a message string.
 * An example of catching an exception:
 * <pre>
 *	try {
 *	    int a[] = new int[2];
 *	    a[4];
 *	} catch (ArrayIndexOutOfBoundsException e) {
 *	    System.out.println("an exception occurred: " + e.getMessage());
 *	    e.printStackTrace();
 *	}
 * </pre>
 * @version 	1.12, 31 Jan 1995
 */
public class Exception {
    /* If you change this, change the number of
     * pcN variables in StandardDefs.gt,
     * class Exception
     */
    private static final int TRACE_BACK_SIZE = 10;
    
    private int pc0, pc1, pc2, pc3, pc4, pc5, pc6, pc7, pc8, pc9;
    /**
     * Specific detail about the exception.  For example,
     * for FileNotFoundExceptions, this contains the name of
     * the file that couldn't be found.
     */
    private String detailMessage;

    /**
     * Constructs an exception with no detail message. The stack
     * trace is automatically filled in.
     */
    public Exception() {
	fillInStackTrace();
    }

    /**
     * Constructs an exception with the specified detail message.
     * The stack trace is automatically filled in.
     * @param message	the detail message
     */
    public Exception(String message) {
	fillInStackTrace();
	detailMessage = message;
    }

    /**
     * Gets the message.
     * @return the detail message of the exception
     */
    public String getMessage() {
	return detailMessage;
    }

    /**
     * Returns a short description of the exception.
     * @return a string describing the exception
     */
    public String toString() {
	String s = getClass().getName();
	if (detailMessage != null) {
	    s = s + ": " + detailMessage;
	}
	return s;
    }

    /**
     * Prints the exception and the exception's stack trace.
     */
    public native void printStackTrace();

    /**
     * Fills in the excecution stack trace. This is useful only
     * when rethrowing an exception. For example:
     * <pre>
     *	   try {
     *	        a = b / c;
     *	   } catch(ArithmeticException e) {
     *		a = Number.MAX_VALUE;
     *	        throw e.fillInStackTrace();
     *	   }
     * </pre>
     * @return the exception itself
     * @see Exception#printStackTrace
     */
    public native Exception fillInStackTrace();
}



