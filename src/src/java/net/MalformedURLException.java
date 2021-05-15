/*
 * @(#)MalformedURLException.java	1.6 95/09/08
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice appears in all copies. Please
 * refer to the file "copyright.html" for further important copyright and
 * licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

package java.net;

import java.io.IOException;

/**
 * Signals that a malformed URL has occurred.
 * @version 1.6, 09/08/95
 * @author 	Arthur van Hoff
 */
public class MalformedURLException extends IOException {

    /**
     * Constructs a MalformedURLException with no detail message.  A
     * detail message is a String that describes this particular 
     * exception.
     */
    public MalformedURLException() {
    }

    /**
     * Constructs a MalformedURLException with the specified detail 
     * message.  A detail message is a String that describes this 
     * particular exception.
     * @param msg the detail message
     */
    public MalformedURLException(String msg) {
	super(msg);
    }
}
