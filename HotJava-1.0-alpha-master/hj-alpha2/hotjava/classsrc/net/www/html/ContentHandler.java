/*
 * @(#)ContentHandler.java	1.4 95/01/31  
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

package net.www.html;
import java.io.InputStream;
import awt.DisplayItem;

/** Defines the API for reading content of arbitrary types.  Any new MIME type
    has to define a new subclass of ContentHandler to decode and cope with
    objects of that type */
public class ContentHandler {
    /** Given an input stream positioned at the beginning of the
	representation of an object, read that stream and recreate
	the object from it */
    public Object getContent(InputStream is, URL u) {
	return null;
    }
    /** Given an input stream positioned at the beginning of the
	representation of an object, read that stream and create
	a DisplayItem that contains the object.  Returns null
	if this is a type that can't be put in a DisplayObject.
	getContent can still be called after getItem */
    public DisplayItem getItem(InputStream is, URL u) {
	return null;
    }
}
