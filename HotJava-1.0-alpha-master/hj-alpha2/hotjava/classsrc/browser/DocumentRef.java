/*
 * @(#)DocumentRef.java	1.2 95/03/14 Jonathan Payne
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

package browser;

import net.www.html.URL;
import java.io.InputStream;

public class DocumentRef extends Ref {
    URL	url;
    Object  hardThing = null;

    public DocumentRef(URL u) {
	url = u;
    }

    public DocumentRef(Document d, URL u) {
	this(u);
	setThing(d);

	/* if url is null, we don't know how to reconstitute this,
	   so we make a hard reference to the object */
	if (u == null) {
	    hardThing = d;
	}
    }

    public Object reconstitute() {
	Object	content = null;

	if (hardThing != null) {
	    return hardThing;
	}

	try {
	    InputStream	is = url.openStreamInteractively();
	    if (url.content_type == URL.content_html) {
		content = new Document(url, is);
	    } else {
		content = url.getContent(is, null);

		if (content == null) {
		    /* launched -- nothing to do */
//		    setCacheable(false);
		} else if (content instanceof Thread) {
		    /* a launchable thing: launch it */
		    ((Thread) content).start();
//		    setCacheable(false);
		} else if (content instanceof String) {
		    content = new Document(url, (String) content);
		}
	    }
	} catch (Exception e) {
	    content = new Document(url, "Failed to get: " + this.url.toExternalForm() + "\nReason is: "+e);
	}
	return content;
    }
}
