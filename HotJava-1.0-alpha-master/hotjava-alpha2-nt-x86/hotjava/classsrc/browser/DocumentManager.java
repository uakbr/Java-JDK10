/*
 * @(#)DocumentManager.java	1.22 95/03/14 Jonathan Payne
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

import java.util.*;
import net.www.html.URL;
import net.www.html.PostURL;

/**
 * Class DocumentManager manages multiple html documents.  It
 * maintains a list of visited documents, and fetches ones which are
 * not currently known.  Instances of DocumentManager are created
 * by WRWindow to manage the documents of that window.  Documents are
 * fetched in the background, and multiple documents may be fetched at
 * the same time.<p>
 * This class was never completed.
 *
 * @version 1.22, 14 Mar 1995
 * @author Jonathan Payne
 */
public
class DocumentManager {
    /** Document cache, indexed by URL. */
    static Hashtable	documentCache = new Hashtable();

    /** Window we're managing documents for. */
    WRWindow    wrWindow;

    DocumentManager(WRWindow w) {
	wrWindow = w;
    }

    static public void unCacheDocument(URL url) {
	documentCache.remove(url);
	
    }

    static public void cacheDocument(DocumentInfo info) {
	URL url = info.url;

	if (url.canCache() && info.canCache() && info.doc != null) {
	    documentCache.put(url, info.doc);
	}
    }

    DocumentInfo newDocument(URL url) {
	DocumentRef	docRef = null;
	DocumentInfo	info;

	if (url.canCache())
	    docRef = (DocumentRef) documentCache.get(url);
	
	return new DocumentInfo(docRef, url);
    }
}
