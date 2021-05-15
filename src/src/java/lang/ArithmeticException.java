/*
 * @(#)ArithmeticException.java	1.12 95/08/09  
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
 * Signals that an exceptional arithmetic condition has occurred.  For
 * example, dividing by zero would invoke this class.
 *
 * @version 	1.12, 08/09/95
 */
public
class ArithmeticException extends RuntimeException {
    /**
     * Constructs an ArithmeticException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public ArithmeticException() {
	super();
    }

    /**
     * Constructs an ArithmeticException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the String that contains a detailed message
     */
    public ArithmeticException(String s) {
	super(s);
    }
}
