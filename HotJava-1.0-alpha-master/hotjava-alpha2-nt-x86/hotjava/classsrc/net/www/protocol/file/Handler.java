/*
 * @(#)Handler.java	1.16 95/03/22  
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

package net.www.protocol.file;

import java.io.*;
import net.www.html.*;

/**
 * Open an file input stream given a URL.
 * @author	James Gosling
 * @version 	1.16, 22 Mar 1995
 */
public class Handler extends URLStreamHandler {
    static String installDirectory;

    public synchronized InputStream openStream(URL u) {
	String fn = u.file;
	if (fn.endsWith("/")) {
	    u.setType(URL.content_html);
	    fn = fn + "index.html";
	} else {
	    u.setType(formatFromName(u.file));
	}

	fn = fn.replace('/', File.separatorChar);

	if (u.host.equals("~webrunner") || u.host.equals("~")) {
	    if (installDirectory == null) {
		installDirectory = System.getenv("HOTJAVA_HOME");
		if (installDirectory == null) {
		    installDirectory = "/usr/local/hotjava".replace('/', File.separatorChar);
		}
	    }
	    fn = installDirectory + fn;
	}
	return new FileInputStream(fn);
    }
}
