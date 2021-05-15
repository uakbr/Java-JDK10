/*
 * @(#)ArrayIndexOutOfBoundsException.java	1.11 95/07/30  
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
 * Signals that an invalid array index has been used.
 * @version 	1.11, 07/30/95
 */
public
class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Constructs an ArrayIndexOutOfBoundsException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public ArrayIndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructs a new ArrayIndexOutOfBoundsException class initialized to 
     * the specific index.
     * @param index the index where the error occurred
     */
    public ArrayIndexOutOfBoundsException(int index) {
	super("Array index out of range: " + index);
    }

    /**
     * Constructs an ArrayIndexOutOfBoundsException class with the specified detail
     * message.  A detail message is a String that describes this particular 
     * exception.
     * @param s the String containing a detail message
     */
    public ArrayIndexOutOfBoundsException(String s) {
	super(s);
    }

}
