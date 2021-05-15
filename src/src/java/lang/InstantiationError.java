/*
 * @(#)InstantiationError.java	1.2 95/08/23  
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
 * Signals that the interpreter has tried to instantiate an abstract class or
 * an interface.
 * @version 	1.2, 08/23/95
 */


public
class InstantiationError extends IncompatibleClassChangeError {
    /**
     * Constructs an InstantiationError with no detail message.  A detail 
     * message is a String that describes this particular exception.
     */
    public InstantiationError() {
	super();
    }

    /**
     * Constructs an InstantiationError with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the String that contains the detail message
     */
    public InstantiationError(String s) {
	super(s);
    }
}
