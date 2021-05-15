/*
 * @(#)Handler.java	1.8 95/05/15 Sami Shaio
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

/*-
 *	doc urls point either into the local filesystem or externally
 *      through an http url.
 */

package net.www.protocol.doc;

import java.io.*;
import net.www.protocol.http.*;
import net.www.protocol.file.*;
import net.www.html.*;

class Handler extends URLStreamHandler {
    /*
     * Attempt to find a load the given url using the local
     * filesystem. If that fails, then try opening using http.
     */
    public synchronized InputStream openStream(URL u) {
	net.www.protocol.http.Handler	http;
	net.www.protocol.file.Handler	file;

	file = new net.www.protocol.file.Handler();
	try {
	    InputStream	is;
	    URL ru = new URL("file", "~", u.file);

	    is = file.openStream(ru);
	    u.setType(ru.content_type);
	    return is;
	} catch (Exception e) {
	    String	host = System.getenv("HOTJAVA_HOST");
	    String	release = System.getenv("HOTJAVA_RELEASE");

	    if (host == null) {
		host = "java.sun.com";
	    }
	    if (release == null) {
		release = "1.0alpha3";
	    }

	    String dir;
	    if (!u.file.startsWith("/")) {
		dir = "/" + release + "/";
	    } else {
		dir = "/" + release;
	    }
	    String uri = "http://"+host+dir+u.file;
	    URL ru = new URL(null, uri);

	    http = new net.www.protocol.http.Handler();
	    InputStream is = http.openStream(ru);
	    u.setType(ru.content_type);
	    return is;
	}
    }
}

