/*
 * @(#)URLStreamHandlerFactory.java	1.6 95/09/08
 * 
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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


/**
 * This interface defines a factory for URLStreamHandler instances.  It is used by the
 * URL class to create URLStreamHandlers for various streams.
 * 
 * @version 1.6, 09/08/95
 * @author 	Arthur van Hoff
 */
public interface URLStreamHandlerFactory {
   
    /**
     * Creates a new URLStreamHandler instance with the specified protocol.
     * @param protocol the protocol to use (ftp, http, nntp, etc.)
     */
    URLStreamHandler createURLStreamHandler(String protocol);
}
