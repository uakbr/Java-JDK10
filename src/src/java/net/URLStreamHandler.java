/*
 * @(#)URLStreamHandler.java	1.12 95/12/18
 * 
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Abstract class for URL stream openers.
 * Subclasses of this class know how to create streams for particular
 * protocol types.
 *
 * @version 	1.12, 12/18/95
 * @author 	James Gosling
 */
public abstract class URLStreamHandler {
    /**
     * Opens an input stream to the object referenced by the URL.  This method should be
     * overridden by a subclass.
     * @param u the URL that this connects to
     */
    abstract protected URLConnection openConnection(URL u) throws IOException;

    /** 
     * This method is called to parse the string spec into URL u.  If
     * there is any inherited context then it has already been copied
     * into u.  The parameters <code>start</code> and
     * <code>limit</code> refer to the range of characters in spec
     * that should be parsed.  The default method uses parsing rules
     * that match the http spec, which most URL protocol families
     * follow.  If you are writing a protocol handler that has a
     * different syntax, override this routine.

     * @param	u the URL to receive the result of parsing the spec
     * @param	spec the URL string to parse
     * @param	start the character position to start parsing at.  This is
     * 		just past the ':' (if there is one).
     * @param	limit the character position to stop parsing at.  This is
     * 		the end of the string or the position of the "#"
     * 		character if present (the "#" reference syntax is
     * 		protocol independent).
     */
    protected void parseURL(URL u, String spec, int start, int limit) {
	String protocol = u.getProtocol();
	String host = u.getHost();
	int port = u.getPort();
	String file = u.getFile();
	String ref = u.getRef();

	int i;
	if ((start <= limit - 2) &&
		(spec.charAt(start) == '/') &&
		(spec.charAt(start + 1) == '/')) {
	    start += 2;
	    i = spec.indexOf('/', start);
	    if (i < 0) {
		i = limit;
	    }
	    int prn = spec.indexOf(':', start);
	    port = -1;
	    if ((prn < i) && (prn >= 0)) {
		try {
		    port = Integer.parseInt(spec.substring(prn + 1, i));
		} catch(Exception e) {
		    // ignore bogus port numbers
		}
		if (prn > start) {
		    host = spec.substring(start, prn);
		}
	    } else {
		host = spec.substring(start, i);
	    }
	    start = i;
	    file = null;
	} else if (host == null) {
	    host = "";
	}
	if (start < limit) {
	    if (spec.charAt(start) == '/') {
		file = spec.substring(start, limit);
	    } else {
		file = (file != null ?
			  file.substring(0, file.lastIndexOf('/')) : "")
		    + "/" + spec.substring(start, limit);
	    }
	}
	if ((file == null) || (file.length() == 0)) {
	    file = "/";
	}
	while ((i = file.indexOf("/./")) >= 0) {
	    file = file.substring(0, i) + file.substring(i + 2);
	}
	while ((i = file.indexOf("/../")) >= 0) {
	    if ((limit = file.lastIndexOf('/', i - 1)) >= 0) {
		file = file.substring(0, limit) + file.substring(i + 3);
	    } else {
		file = file.substring(i + 3);
	    }
	}

	u.set(protocol, host, port, file, ref);
    }

    /**
     * Reverses the parsing of the URL.  This should probably be overridden if
     * you override parseURL().
     * @param u the URL
     * @return	the textual representation of the fully qualified URL (i.e.
     *		after the context and canonicalization have been applied).
     */
    protected String toExternalForm(URL u) {
	String result = u.getProtocol() + ":";
	if ((u.getHost() != null) && (u.getHost().length() > 0)) {
	    result = result + "//" + u.getHost();
	    if (u.getPort() != -1) {
		result += ":" + u.getPort();
	    }
	}
	result += u.getFile();
	if (u.getRef() != null) {
	    result += "#" + u.getRef();
	}
	return result;
    }

    /**
     * Calls the (protected) set method out of the URL given.  Only
     * classes derived from URLStreamHandler are supposed to be able
     * to call the set() method on a URL.
     * @see URL#set
     */
    protected void setURL(URL u, String protocol, String host, int port,
			  String file, String ref) {
        u.set(protocol, host, port, file, ref);
    }
}
