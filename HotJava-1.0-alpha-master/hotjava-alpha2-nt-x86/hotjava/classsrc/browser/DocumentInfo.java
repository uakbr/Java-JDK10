/*
 * @(#)DocumentInfo.java	1.31 95/03/18 Jonathan Payne
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

import net.www.http.UnauthorizedHttpRequestException;
import net.www.http.HttpClient;
import java.util.*;
import net.www.html.URL;
import net.www.html.ContentHandler;
import java.io.*;

/**
 * Class DocumentInfo maintains a bunch of information about a
 * Document, such as how this document was reached, the URL of the
 * document, current status (KNOWN, RESIDENT, etc.), its scroll
 * position the last time this document was displayed.
 * @see DocumentManager
 * @see WRWindow
 * @version 1.31, 18 Mar 1995
 * @author Jonathan Payne
 */

class DocumentInfo {
    DocumentRef	    doc;		/* where we are */
    URL		    url;		/* URL for before we create doc */
    int		    scrollY;		/* scroll position when last visited */
    boolean	    cacheable = true;	/* true if this document can be cached */
    boolean	    emptyDocument = false;	/* some "documents" are empty.
					   mailto: is the common case */
    String	    title = null;

    /**
     * Creates a new DocumentInfo from the specified URL and the
     * specified Document that referenced this new Document.  The
     * Document is not fetched at this time.
     */
    public DocumentInfo(Document doc, URL url) {
	this(new DocumentRef(doc, url), url);
    }

    public DocumentInfo(DocumentRef ref, URL url) {
	if (ref == null) {
	    ref = new DocumentRef(null, url);
	}
	doc = ref;
	this.url = url;
	scrollY = 1;		    /* > 0 means not set, so don't scroll */
    }

    public String toString() {
	return "DocumentInfo[doc=" + doc + ", url = " + url + "]";
    }

    public boolean canCache() {
	return cacheable;
    }

    public String getTitle() {
	if (title == null) {
	    try {
		title = ((Document) getContent()).getTitle();
	    } catch (Exception e) {}
	    if (title == null)
		title = "Untitled, " + url.toExternalForm();
	}
	return title;
    }

    public Object getContent() {
	if (doc != null && !emptyDocument) {
	    Object result = doc.get();
	    if (result == null) {	/* avoid double fetches of empty documents */
		title = "Untitled, " + url.toExternalForm();
		emptyDocument = true;
	    };
	    return result;
	}
	else
	    return null;
    }

    public void clearDoc() {
	if (doc != null) {
	    doc.setThing(null);
	}
	title = null;
    }

    public boolean isResident() {
	return !(doc == null || doc.check() == null);
    }

    public void setDocument(Document d) {
	doc = new DocumentRef(d, null);
    }

    public void setCacheable(boolean cc) {
	if (cc == cacheable)
	    return;
	cacheable = cc;
	if (!cc && url != null){
	    DocumentManager.unCacheDocument(url);
	    doc = null;
	}
    }

    /**
     * Compares this DocumentInfo to another one, and returns true
     * if they reference the same file.
     */
    public boolean sameAs(DocumentInfo other) {
	return other != null && other.url.sameFile(url);
    }

    /**
     * Handles this document being closed by the WRWindow.  This
     * salts away the scroll position.
     */
    public void close(WRWindow w) { 
	scrollY = w.getScrollY();
    }

    /**
     * Handles this document being opened by the WRWindow.  This
     * scrolls the window to salted scroll position from close.
     */
    public void open(WRWindow w) {
	if (scrollY <= 0) {
	    w.scrollAbsolute(0, -scrollY);
	}
    }
}
