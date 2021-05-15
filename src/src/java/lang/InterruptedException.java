/*
 * @(#)InterruptedException.java	1.5 95/09/08  
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
 * An exception indicated that some thread has interrupted this thread.
 * <p>
 *
 * @see Thread#interrupt
 * @see Thread#interrupted
 * @version 	1.5, 09/08/95
 * @author      Frank Yellin
 */
public
class InterruptedException extends Exception {
    /**
     * Constructs an InterruptedException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public InterruptedException() {
	super();
    }

    /**
     * Constructs an InterruptedException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public InterruptedException(String s) {
	super(s);
    }
}
