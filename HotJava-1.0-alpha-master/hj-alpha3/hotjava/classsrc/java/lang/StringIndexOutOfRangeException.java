/*
 * @(#)StringIndexOutOfRangeException.java	1.7 95/01/31  
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
 * Signals string index out of range.
 * @see String#charAt
 * @version 	1.7, 31 Jan 1995
 */
public
class StringIndexOutOfRangeException extends Exception {
    /**
     * Constructor.
     */
    public StringIndexOutOfRangeException() {
	super();
    }

    /**
     * Constructor with a an offending index.
     */
    public StringIndexOutOfRangeException(int index) {
	super("String index out of range: " + index);
    }
}


