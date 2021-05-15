/*
 * @(#)LinkageError.java	1.3 95/08/13  
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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
 * LinkageError and its subclasses indicate that a class has some 
 * dependency on another class; however the  latter class has incompatibly 
 * changed after the compilation of the former class.<p>
 *
 * @version 	1.3, 08/13/95
 * @author      Frank Yellin
 */
public
class LinkageError extends Error {
    /**
     * Constructs a LinkageError with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public LinkageError() {
	super();
    }

    /**
     * Constructs a LinkageError with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public LinkageError(String s) {
	super(s);
    }
}
