/*
 * @(#)TagRef.java	1.11 95/01/31 Jonathan Payne
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

import java.util.Hashtable;
import java.util.Enumeration;

package net.www.html;

public class TagRef {
    public Tag	    tag;	    /* tag we're referencing */
    public int	    pos;	    /* position in text */
    public boolean  isEnd;	    /* is end tag */
    Hashtable	    attributes;	    /* specified attributes of this tag */

    public TagRef(Tag tag, int pos, boolean isEnd) {
	this.tag = tag;
	this.pos = pos;
	this.isEnd = isEnd;
    }

    public void addAttribute(String name, String value) {
	if (attributes == null)
	    attributes = new Hashtable(1);
	attributes.put(name, value);
    }	

    public String getAttribute(String name) {
	return (attributes == null) ? null
	    : (String) attributes.get(name.toLowerCase());
    }

    public String toString() {
	String tagName = tag.name;

	if (isEnd) {
	    tagName = "/" + tagName;
	}
	return getClass().getName() + "[" + tagName + ": pos = " + pos + "]";
    }

    public String toExternalForm() {
	String result = isEnd ? "</" : "<";

	result += tag.name;
	if (attributes != null) {
	    Enumeration e = attributes.keys();

	    while (e.hasMoreElements()) {
		String	key = (String) e.nextElement();

		result += " ";
		result += key + "=\"" + attributes.get(key) + "\"";
	    }
	}
	result += ">";

	return result;
    }	    
}
