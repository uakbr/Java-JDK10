/*
 * @(#)Error.java	1.5 95/08/16  
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
 * Error is a subtype of Throwable for abnormal events that should not occur.
 *
 * Do not try to catch Error's unless you really know what you're
 * doing.
 *
 * @version 	1.5, 08/16/95
 * @author      Frank Yellin
 */
public
class Error extends Throwable {
    /**
     * Constructs an Error with no specified detail message.
     * A detail message is a String that describes this particular error.
     */
    public Error() {
	super();
    }

    /**
     * Constructs an Error with the specified detail message.
     * A detail message is a String that describes this particular error
     * @param s the detail message
     */
    public Error(String s) {
	super(s);
    }
}
