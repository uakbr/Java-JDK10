/*
 * @(#)URL.java	1.27 95/12/18
 * 
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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
 * Class URL represents a Uniform Reference Locator -- a reference
 * to an object on the World Wide Web. This is a constant object,
 * once it is created, its fields cannot be changed.
 *
 * @version 	1.27, 12/18/95
 * @author 	James Gosling
 */

public final class URL {
    /** 
     * The protocol to use (ftp, http, nntp, ... etc.) . 
     */
    private String protocol;

    /** 
     * The host name in which to connect to. 
     */
    private String host;

    /** 
     * The protocol port to connect to. 
     */
    private int port = -1;

    /** 
     * The specified file name on that host. 
     */
    private String file;

    /** 
     * # reference. 
     */
    private String ref;

    /**
     * The URLStreamHandler for this URL.
     */
    URLStreamHandler handler;

    /** 
     * Creates an absolute URL from the specified protocol,
     * host, port and file.
     * @param protocol the protocol to use
     * @param host the host to connect to
     * @param port the port at that host to connect to
     * @param file the file on that host
     * @exception MalformedURLException If an unknown protocol is 
     * found. 
     */
    public URL(String protocol, String host, int port, String file)
	throws MalformedURLException {
	this.protocol = protocol;
	this.host = host;
	this.file = file;
	this.port = port;
	if ((handler = getURLStreamHandler(protocol)) == null) {
	    throw new MalformedURLException("unknown protocol: " + protocol);
	}
    }

    /** 
     * Creates an absolute URL from the specified protocol,
     * host, and file.  The port number used will be the default for the
     * protocol.
     * @param protocol the protocol to use
     * @param host the host to connect to
     * @param file the file on that host
     * @exception MalformedURLException If an unknown protocol is 
     * found. 
     */
    public URL(String protocol, String host, String file) throws MalformedURLException {
	this(protocol, host, -1, file);
    }

    /**
     * Creates a URL from the unparsed absolute URL.
     * @param spec the URL String to parse
     */
    public URL(String spec) throws MalformedURLException {
	this(null, spec);
    }

    /** 
     * Creates a URL from the unparsed URL in the specified context.If
     * spec is an absolute URL it is used as is. Otherwise it isparsed
     * in terms of the context.  Context may be null (indicating no
     * context).

     * @param context the context to parse the URL to
     * @param spec the URL String to parse
     * @exception MalformedURLException If the protocol is equal to null. 
     */
    public URL(URL context, String spec) throws MalformedURLException {
	String original = spec;
	int i, limit, c;
	int start = 0;
	String newProtocol = null;

	try {
	    limit = spec.length();
	    while ((limit > 0) && (spec.charAt(limit - 1) <= ' ')) {
		limit--;	//eliminate trailing whitespace
	    }
	    while ((start < limit) && (spec.charAt(start) <= ' ')) {
		start++;	// eliminate leading whitespace
	    }

	    if (spec.regionMatches(true, start, "url:", 0, 4)) {
		start += 4;
	    }
	    for (i = start ; (i < limit) && ((c = spec.charAt(i)) != '/') ; i++) {
		if (c == ':') {
		    newProtocol = spec.substring(start, i).toLowerCase();
		    start = i + 1;
		    break;
		}
	    }
	    // Only use our context if the protocols match.
	    if ((context != null) && ((newProtocol == null) ||
				    newProtocol.equals(context.protocol))) {
		protocol = context.protocol;
		host = context.host;
		port = context.port;
		file = context.file;
	    } else {
		protocol = newProtocol;
	    }

	    if (protocol == null) {
		throw new MalformedURLException("no protocol: "+original);
	    }

	    if ((handler = getURLStreamHandler(protocol)) == null) {
		throw new MalformedURLException("unknown protocol: "+protocol);
	    }

	    i = spec.indexOf('#', start);
	    if (i >= 0) {
		ref = spec.substring(i + 1, limit);
		limit = i;
	    }
	    handler.parseURL(this, spec, start, limit);

	} catch(MalformedURLException e) {
	    throw e;
	} catch(Exception e) {
	    throw new MalformedURLException(original + ": " + e);
	}
    }

    /**
     * Sets the fields of the URL. This is not a public method so that 
     * only URLStreamHandlers can modify URL fields. URLs are 
     * otherwise constant.
     *
     * REMIND: this method will be moved to URLStreamHandler
     *
     * @param protocol the protocol to use
     * @param host the host name to connecto to
     * @param port the protocol port to connect to
     * @param file the specified file name on that host
     * @param ref the reference
     */
    protected void set(String protocol, String host, int port, String file, String ref) {
	this.protocol = protocol;
	this.host = host;
	this.port = port;
	this.file = file;
	this.ref = ref;
    }

    /**
     * Gets the port number. Returns -1 if the port is not set.
     */
    public int getPort() {
	return port;
    }

    /**
     * Gets the protocol name.
     */
    public String getProtocol() {
	return protocol;
    }

    /**
     * Gets the host name.
     */
    public String getHost() {
	return host;
    }

    /**
     * Gets the file name.
     */
    public String getFile() {
	return file;
    }

    /**
     * Gets the ref.
     */
    public String getRef() {
	return ref;
    }

    /**
     * Compares two URLs.
     * @param	obj the URL to compare against.
     * @return	true if and only if they are equal, false otherwise.
     */
    public boolean equals(Object obj) {
	return (obj instanceof URL) && sameFile((URL)obj);
    }

    /** 
     * Creates an integer suitable for hash table indexing. 
     */
    public int hashCode() {
	int inhash = 0;
	if (!host.equals("")) {
	    try {
		inhash = InetAddress.getByName(host).hashCode();
	    } catch(UnknownHostException e) {
	    }
	}
	return protocol.hashCode() ^ inhash ^ file.hashCode();
    }

    /**
     * Compares the host components of two URLs.
     * @param h1 the URL of the first host to compare 
     * @param h2 the URL of the second host to compare 
     * @return	true if and only if they are equal, false otherwise.
     * @exception UnknownHostException If an unknown host is found.
     */
    boolean hostsEqual(String h1, String h2) {
	if (h1.equals(h2)) {
	    return true;
	}
	// Have to resolve addresses before comparing, otherwise
	// names like tachyon and tachyon.eng would compare different
	try {
	    InetAddress a1 = InetAddress.getByName(h1);
	    InetAddress a2 = InetAddress.getByName(h2);
	    return a1.equals(a2);
	} catch(UnknownHostException e) {
	} catch(SecurityException e) {
	}
	return false;
    }

    /**
     * Compares two URLs, excluding the "ref" fields: sameFile is true
     * if the true references the same remote object, but not necessarily
     * the same subpiece of that object.
     * @param	other	the URL to compare against.
     * @return	true if and only if they are equal, false otherwise.
     */
    public boolean sameFile(URL other) {
	// AVH: should we not user getPort to compare ports?
	return protocol.equals(other.protocol) &&
	       hostsEqual(host, other.host) &&
	       (port == other.port) &&
	       file.equals(other.file);
    }

    /**
     * Converts to a human-readable form.
     * @return	the textual representation.
     */
    public String toString() {
	return toExternalForm();
    }

    /**
     * Reverses the parsing of the URL.
     * @return	the textual representation of the fully qualified URL (i.e.
     *		after the context and canonicalization have been applied).
     */
    public String toExternalForm() {
	return handler.toExternalForm(this);
    }

    /** 
     * Creates (if not already in existance) a URLConnection object that
     * contains a connection to the remote object referred to by
     * the URL.  Invokes the appropriate protocol handler.  Failure is
     * indicated by throwing an exception.
     * @exception IOException If an I/O exception has occurred.
     * @see URLConnection
     */
    public URLConnection openConnection()
	throws java.io.IOException
    {
	return handler.openConnection(this);
    }

    /**
     * Opens an input stream.
     * @exception IOException If an I/O exception has occurred.
     */
    public final InputStream openStream() 			// REMIND: drop final
	throws java.io.IOException
    {
	return openConnection().getInputStream();
    }

    /**
     * Gets the contents from this opened connection.
     * @exception IOException If an I/O exception has occurred.
     */
    public final Object getContent() 				// REMIND: drop final
	throws java.io.IOException
    {
	return openConnection().getContent();
    }

    /**
     * The URLStreamHandler factory.
     */
    static URLStreamHandlerFactory factory;

    /**
     * Sets the URLStreamHandler factory.
     * @param fac the desired factory
     * @exception Error If the factory has already been defined.
     */
    public static synchronized void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
	if (factory != null) {
	    throw new Error("factory already defined");
	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSetFactory();
	}
	factory = fac;
    }

    /**
     * A table of protocol handlers.
     */
    static Hashtable handlers = new Hashtable();

    /**
     * Gets the Stream Handler.
     * @param protocol the protocol to use
     */
    static synchronized URLStreamHandler getURLStreamHandler(String protocol) {
	URLStreamHandler handler = (URLStreamHandler)handlers.get(protocol);
	if (handler == null) {
	    // Use the factory (if any)
	    if (factory != null) {
		handler = factory.createURLStreamHandler(protocol);
	    }

	    // Try java protocol handler
	    if (handler == null) {
		try {
		    String clname = "sun.net.www.protocol." + protocol + ".Handler";
		    handler = (URLStreamHandler)Class.forName(clname).newInstance();
		} catch (Exception e) {
		}
	    }
	    if (handler != null) {
		handlers.put(protocol, handler);
	    }
	}
	return handler;
    }
}
