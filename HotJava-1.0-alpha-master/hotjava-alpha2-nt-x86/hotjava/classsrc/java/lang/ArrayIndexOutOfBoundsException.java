/*
 * @(#)ArrayIndexOutOfBoundsException.java	1.8 95/01/31  
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
 * @version 	1.8, 31 Jan 1995
 */
public
class ArrayIndexOutOfBoundsException extends Exception {
    /**
     * Constructor.
     */
    public ArrayIndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructor with the offending index.
     * @param index The index
     */
    public ArrayIndexOutOfBoundsException(int index) {
	super("Array index out of range: " + index);
    }

    /**
     * Constructor with a detail message.
     * @param s The text of the message.
     */
    public ArrayIndexOutOfBoundsException(String s) {
	super(s);
    }

}
