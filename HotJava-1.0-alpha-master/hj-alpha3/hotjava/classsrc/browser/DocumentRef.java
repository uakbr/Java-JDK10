/*
 * @(#)DocumentRef.java	1.4 95/05/10 Jonathan Payne
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
	} catch (net.UnknownServiceException e) {
	    content = new Document(url, "<html><body><h1>Error</h1><p><i>Failed to get: </i>" + this.url.toExternalForm() + "</h1>\n<p><i>Reason is: </i>The URL contained a protocol specification that is not defined anywhere.\n");
	} catch (net.UnknownHostException e) {
	    content = new Document(url, "<html><body><h1>Error</h1><p><i>Failed to get: </i>" + this.url.toExternalForm() + "</h1>\n<p><i>Reason is: </i>The URL refers to a host whose name is not defined.\n");
	} catch (FileNotFoundException e) {
	    content = new Document(url, "<html><body><h1>Error</h1><p><i>Failed to get: </i>" + this.url.toExternalForm() + "</h1>\n<p><i>Reason is: </i>The URL refers to a document which does not exist.\n");
	} catch (Exception e) {
	    content = new Document(url, "<html><body><h1>Error</h1><p><i>Failed to get: </i>" + this.url.toExternalForm() + "</h1>\n<p><i>Reason is: </i>"+e);
	}
	return content;
    }
}
