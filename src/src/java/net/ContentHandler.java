/*
 * @(#)ContentHandler.java	1.4 95/12/18
 *
 * Copyright (c) 1995 Sun Microsystems, Inc.  All Rights reserved
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file copyright.html
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
 * A class to read data from a URLConnection and construct an
 * Object.  Specific subclasses of ContentHandler handle
 * specific mime types.  It is the responsibility of a ContentHandlerFactory
 * to select an appropriate ContentHandler for the mime-type
 * of the URLConnection.  Applications should never call ContentHandlers
 * directly, rather they should use URL.getContent() or
 * URLConnection.getContent()
 * @author  James Gosling
 */

abstract public class ContentHandler {
    /** 
     * Given an input stream positioned at the beginning of the
     * representation of an object, reads that stream and recreates
     * the object from it. 
     * @exception IOException  An IO error occurred while reading the object.
     */
    abstract public Object getContent(URLConnection urlc) throws IOException;
}

