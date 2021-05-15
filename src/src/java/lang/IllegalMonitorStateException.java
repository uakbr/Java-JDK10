/*
 * @(#)IllegalMonitorStateException.java	1.2 95/12/03  
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
 * Signals that a monitor operation has been attempted when the monitor
 * is in an invalid state.  For example, trying to notify a monitor that
 * you do not own would invoke this class.
 *
 * @version 	1.2, 12/03/95
 */
public
class IllegalMonitorStateException extends RuntimeException {
    /**
     * Constructs an IllegalMonitorStateException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public IllegalMonitorStateException() {
	super();
    }

    /**
     * Constructs an IllegalMonitorStateException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     * @param s the String that contains a detailed message
     */
    public IllegalMonitorStateException(String s) {
	super(s);
    }
}
