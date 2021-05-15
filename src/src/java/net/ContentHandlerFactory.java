/*
 * @(#)ContentHandlerFactory.java	1.1 95/08/20
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
 * This interface defines a factory for ContentHandler instances.  It is used by the
 * URLStreamHandler class to create ContentHandlers for various streams.
 * 
 * @version 1.1, 08/20/95
 * @author James Gosling
 */
public interface ContentHandlerFactory {
   
    /**
     * Creates a new ContentHandler to read an object from a URLStreamHandler.
     * @param mimetype	The mime type for which a content handler is desired.
     */
    ContentHandler createContentHandler(String mimetype);
}
