/*
 * @(#)ProtocolException.java	1.6 95/08/16 Chris Warth
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

package java.net;

import java.io.IOException;

/**
 * Signals when connect gets an EPROTO.  This exception is specifically
 * caught in class Socket.
 * @version 1.6, 08/16/95
 * @author Chris Warth
 */
public 
class ProtocolException extends IOException { 

    /**
     * Constructs a new ProtocolException with the specified detail 
     * message.
     * A detail message is a String that gives a specific description
     * of this error. 
     * @param host the detail message
     */
    public ProtocolException(String host) {
	super(host);
    }
    
    /**
     * Constructs a new ProtocolException with no detail message.
     * A detail message is a String that gives a specific description
     * of this error.  
     */
    public ProtocolException() {
    }
}
