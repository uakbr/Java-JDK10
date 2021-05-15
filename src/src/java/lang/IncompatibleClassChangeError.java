/*
 * @(#)IncompatibleClassChangeError.java	1.8 95/08/11  
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
 * Signals that an incompatible class change has occurred.
 * @version 	1.8, 08/11/95
 */
public
class IncompatibleClassChangeError extends LinkageError {
    /**
     * Constructs an IncompatibleClassChangeError with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public IncompatibleClassChangeError () {
	super();
    }

    /**
     * Constructs an IncompatibleClassChangeError with the specified detail 
     * message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public IncompatibleClassChangeError(String s) {
	super(s);
    }
}

