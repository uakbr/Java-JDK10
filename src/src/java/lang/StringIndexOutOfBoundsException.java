/*
 * @(#)StringIndexOutOfBoundsException.java	1.11 95/08/10  
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
 * Signals that a String index is out of range.
 * @see String#charAt
 * @version 	1.11, 08/10/95
 */
public
class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {
    /**
     * Constructs a StringIndexOutOfBoundsException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public StringIndexOutOfBoundsException() {
	super();
    }

    /**
     * Constructs a StringIndexOutOfBoundsException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the String containing a detail message about the error
     */
    public StringIndexOutOfBoundsException(String s) {
	super(s);
    }

    /**
     * Constructs a StringIndexOutOfBoundsException initialized with
     * the specified index.
     * @param index the offending index
     */
    public StringIndexOutOfBoundsException(int index) {
	super("String index out of range: " + index);
    }
}


