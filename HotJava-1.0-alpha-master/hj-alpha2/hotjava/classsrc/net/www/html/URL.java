/*
 * @(#)URL.java	1.57 95/03/20 Jonathan Payne, Chris Warth, James Gosling
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

import java.util.*;
import java.io.*;
import awt.GifImage;
import awt.XbmImage;
import awt.Xpm2Image;
import net.InetAddress;
import net.UnknownHostException;
import net.www.html.MalformedURLException;
import browser.Observer;
import browser.Observable;

/**
 * Class URL represents a Uniform Reference Locator -- a textual reference
 * to an object on the World Wide Web.  The public instance variables contains
 * the broken apart fields of the parsed url.
 */

public class URL {
    private static Hashtable content_table = new Hashtable();
    public static String content_unknown = "Unknown";
    public static String content_octet;
    public static String content_oda;
    public static String content_pdf;
    public static String content_postscript;
    public static String content_richtext;
    public static String content_bcpio;
    public static String content_cpio;
    public static String content_dvi;
    public static String content_gtar;
    public static String content_hdf;
    public static String content_latex;
    public static String content_netcdf;
    public static String content_shar;
    public static String content_sv4cpio;
    public static String content_sv4crc;
    public static String content_tar;
    public static String content_tex;
    public static String content_texinfo;
    public static String content_troff;
    public static String content_man;
    public static String content_me;
    public static String content_ms;
    public static String content_ustar;
    public static String content_source;
    public static String content_zip;
    public static String content_basic;
    public static String content_aiff;
    public static String content_wav;
    public static String content_gif;
    public static String content_ief;
    public static String content_jpeg;
    public static String content_tiff;
    public static String content_rast;
    public static String content_anymap;
    public static String content_bitmap;
    public static String content_graymap;
    public static String content_pixmap;
    public static String content_rgb;
    public static String content_xbitmap;
    public static String content_xpixmap;
    public static String content_xwindowdump;
    public static String content_rfc822;
    public static String content_html;
    public static String content_plain;
    public static String content_values;
    public static String content_setext;
    public static String content_mpeg;
    public static String content_quicktime;
    public static String content_msvideo;
    public static String content_movie;

    /**
	The static initializer for this class must be completed before 
	the static initializer for URLStreamHandler.  Therefore we create 
	a static method here that is explcitly called by the static initializer of 
        URLStreamHeandler, thus guarrenteeing the correct order of initialization.
    */
    static void classInit() {
	String ctarray [] = {
	    content_octet = "application/octet-stream",
	    content_oda = "application/oda",
	    content_pdf = "application/pdf",
	    content_postscript = "application/postscript",
	    content_richtext = "application/rtf",
	    content_bcpio = "application/x-bcpio",
	    content_cpio = "application/x-cpio",
	    content_dvi = "application/x-dvi",
	    content_gtar = "application/x-gtar",
	    content_hdf = "application/x-hdf",
	    content_latex = "application/x-latex",
	    content_netcdf = "application/x-netcdf",
	    content_shar = "application/x-shar",
	    content_sv4cpio = "application/x-sv4cpio",
	    content_sv4crc = "application/x-sv4crc",
	    content_tar = "application/x-tar",
	    content_tex = "application/x-tex",
	    content_texinfo = "application/x-texinfo",
	    content_troff = "application/x-troff",
	    content_man = "application/x-troff-man",
	    content_me = "application/x-troff-me",
	    content_ms = "application/x-troff-ms",
	    content_ustar = "application/x-ustar",
	    content_source = "application/x-wais-source",
	    content_zip = "application/zip",
	    content_basic = "audio/basic",
	    content_aiff = "audio/x-aiff",
	    content_wav = "audio/x-wav",
	    content_gif = "image/gif",
	    content_ief = "image/ief",
	    content_jpeg = "image/jpeg",
	    content_tiff = "image/tiff",
	    content_rast = "image/x-cmu-rast",
	    content_anymap = "image/x-portable-anymap",
	    content_bitmap = "image/x-portable-bitmap",
	    content_graymap = "image/x-portable-graymap",
	    content_pixmap = "image/x-portable-pixmap",
	    content_rgb = "image/x-rgb",
	    content_xbitmap = "image/x-xbitmap",
	    content_xpixmap = "image/x-xpixmap",
	    content_xwindowdump = "image/x-xwindowdump",
	    content_rfc822 = "message/rfc822",
	    content_html = "text/html",
	    content_plain = "text/plain",
	    content_values = "text/tab-separated-values",
	    content_setext = "text/x-setext",
	    content_mpeg = "video/mpeg",
	    content_quicktime = "video/quicktime",
	    content_msvideo = "video/x-msvideo",
	    content_movie = "video/x-sgi-movie"
	};

	for (int i = 0; i < ctarray.length; i++) {
	    String ct = ctarray[i];

	    URL.content_table.put(ct, ct);
	}

    }

    /** only relevant if we're a post url */
    public String postData = null;

    /** only relevant if we're a post url */
    public URL	fromUrl;

    /** What protocol to use (ftp, http, nntp, ... etc) */
    public String protocol;

    /** Host name to which to connect */
    public String host;

    /** "File name" on that host */
    public String file;

    /** # reference */
    public String ref;

    /** Protocol port to connect to */
    public int	  port = -1;

    /** Format of media, only valid after the stream has been opened */
    public String content_type;

    /** Create an absolute URL from the specified protocol,
	host, and file. */
    public URL (String p, String h, String f) {
	protocol = p;
	host = h;
	file = f;
    }

    public boolean isPostURL() {
	return postData != null;
    }

    /** Create a POST URL */
    public URL(URL url, String post, URL from) {
	this(url.protocol, url.host, url.file);
	postData = post;
	fromUrl = from;
    }

    /** Create a URL from the unparsed url in the context of
	the specified context.  If spec is an absolute URL,
	cool, otherwise, parse it in terms of the context.
	Context may be null (indicating no context). */
    public URL (URL context, String spec) {
	String	original = spec;
	int i, limit, c;
	int start = 0;
	String	newProtocol = null;

	try {
	    limit = spec.length();
	    while (limit > 0 && spec.charAt(limit - 1) <= ' ')
		limit--;	/* eliminate trailing whitespace */
	    while (start < limit && spec.charAt(start) <= ' ')
		start++;	/* eliminate leading whitespace */
	    if (spec.startsWith("url:", start))
		start += 4;
	    for (i = start; i < limit && (c = spec.charAt(i)) != '/'; i++)
		if (c == ':') {
		    newProtocol = spec.substring(start, i);
		    start = i + 1;
		    break;
		}

	    /* Only use our context if the protocols match. */
	    if (context != null && (newProtocol == null ||
				    newProtocol.equals(context.protocol))) {
		copyURL(context);
	    } else {
		protocol = newProtocol;
	    }

	    if (protocol == null) {
		/* turns into MalformedException below */
		throw new Exception();
	    }

	    if (start <= limit - 2 &&
		    spec.charAt(start) == '/' &&
		    spec.charAt(start + 1) == '/') {
		start += 2;
		i = spec.indexOf('/', start);
		if (i < 0)
		    i = limit;
		int prn = spec.indexOf(':', start);
		port = -1;
		if (prn < i && prn >= 0) {
		    try {
			port = Integer.parseInt(spec.substring(prn + 1, i));
		    } catch(Exception e) {} /* ignore bogus port numbers */
		    if (prn > start)
			host = spec.substring(start, prn);
		} else
		    host = spec.substring(start, i);
		start = i;
		file = null;
	    } else if (host == null)
		host = "";
	    i = spec.indexOf('#', start);
	    if (i >= 0) {
		ref = spec.substring(i + 1, limit);
		limit = i;
	    }
	    else ref = null;
	    if (start < limit)
		if (spec.charAt(start) == '/')
		    file = spec.substring(start, limit);
		else {
		    file = (file != null ?
			    file.substring(0, file.lastIndexOf('/')) : "")
			+ "/" + spec.substring(start, limit);
		}
	    if (file == null || file.length() == 0)
		file = "/";
	    while ((i = file.indexOf("/./")) >= 0) {
		file = file.substring(0, i) + file.substring(i + 2);
	    }
	    while ((i = file.indexOf("/../")) >= 0) {
		if ((limit = file.lastIndexOf('/', i - 1)) >= 0)
		    file = file.substring(0, limit) + file.substring(i + 3);
		else
		    file = file.substring(i + 3);
	    }
	} catch (Exception e) {
	    throw new MalformedURLException(original + ": " + e);
	}
    }

    /**
     * Copy another URL's "vital statistics" into us.
     * @param	url The URL to copy.
     */
    protected void copyURL(URL url) {
	protocol = url.protocol;
	host = url.host;
	file = url.file;
	port = url.port;
    }

    /**
     * Compare two URLs.
     * @param	o	The URL to compare against.
     * @return	true iff they are equal, false otherwise.
     */
    public boolean equals(Object o) {
	return (o instanceof URL) && sameFile((URL) o);
    }

    /** Create an integer suitable for hash table indexing */
    public int hashCode() {
	return protocol.hashCode() ^ host.hashCode() ^ file.hashCode();
    }

    /**
     * Compare the host components of two URLs.
     * @param	o	The URL to compare against.
     * @return	true iff they are equal, false otherwise.
     */
    boolean hostsEqual(String h1, String h2) {
	if (h1.equals(h2)) {
	    return true;
	}
	try {
	    InetAddress a1 = InetAddress.getByName(h1);
	    InetAddress a2 = InetAddress.getByName(h2);
	    return a1.equals(a2);
	} catch (UnknownHostException e) {
	}
	return false;
    }

    /**
     * Compare two URLs, excluding the "ref" field: sameFile is true
     * if the true reference the same remote object, but not necessarily
     * the same subpiece of that object.
     * @param	other	The URL to compare against.
     * @return	true iff they are equal, false otherwise.
     */
    public boolean sameFile(URL other) {
	return (!isPostURL() &&
		protocol.equals(other.protocol)
		&& hostsEqual(host, other.host)
		&& port == other.port
		&& file.equals(other.file));
    }

    /**
     * Convert to a human-readable form.
     * @return	The textual representation.
     */
    public String toString() {
	String value = getClass().getName()
	    + "[protocol=" + protocol + ", host=" + host
	    + ", file=" + file;

	if (ref != null) {
	    value = value + ", ref=" + ref;
	}
	return value + "]";
    }

    /**
     * Reverse the parsing of the URL.
     * @return	The textual representation of the fully qualified URL (ie.
     *		after the context and canonicalization have been applied).
     */
    public String toExternalForm() {
	String	result = protocol + "://" + host;

	if (port != -1) {
	    result += ":" + port;
	}
	result += file;
	if (ref != null) {
	    result += "#" + ref;
	}

	return result;
    }

    public int getPort() {
	return port == -1 ? 80 : port;
    }

    static Hashtable	ht = new Hashtable();
    private URLStreamHandler getHandler() {
	URLStreamHandler sh = (URLStreamHandler) ht.get(protocol);
	if (sh == null) {
	    try {
		URL url = new URL("http", host, "/");
		sh = (URLStreamHandler) url.New("net.www.protocol." +
						protocol +
						".Handler");
	    } catch(Exception e) {
		e.printStackTrace();
		sh = new unknownHandler(protocol);
	    }
	    ht.put(protocol, sh);
	}
        return sh;
    }


    /**
     * Open an input stream to the object references by the URL.  Invokes the
     * appropriate protocol handler.  Failure is indicated by throwing an
     * exception.  The act of opening this stream determines, in a protocol
     * specific way, the content type of the object associated with this URL.
     * @return	The opened input stream.  A value of null indicates that while
     * the open was successful, there is no useful data provided by this
     * protocol, it's done for side-effect only (the usual example is the
     * "mailto" protocol).
     */
    public InputStream openStream() {
	URLStreamHandler h = getHandler();
	InputStream is = h.openStream(this);

	if (content_type == URL.content_unknown) {
	    is = getContentFromStream(is);
	}
	return is;
    }

    /**
     * Similar to openStream except that it allows the stream handler
     * to interact with the user to resolve certain problems.  For
     * example, the http handler will prompt for a user name and
     * password to handle authentication failures.  In these cases,
     * openStream would just toss an exception.
     */
    public InputStream openStreamInteractively() {
	URLStreamHandler h = getHandler();
	InputStream is = h.openStreamInteractively(this);

	if (content_type == URL.content_unknown) {
	    is = getContentFromStream(is);
	}
	return is;
    }


    /**
     * This disgusting hack is used to check for files that are
     * actually html but which do not have a MIME header and do not
     * end in .htm or .html.  We need to review all of the http: and ftp: and
     * file: protocol fetching code to see if we can't unify the
     * concepts so that document type is determined in only one
     * place instead of several places like it is today.
     */
    private InputStream getContentFromStream(InputStream is) 
    {
	if (!is.markSupported())
	    is = new BufferedInputStream(is);
	
	/*
	 * Check for html files that are missing a MIME header.
	 * This is sure not perfect but the pages are broken anyway
	 * so this is better than nothing.
	 */
	is.mark(10);
	byte buf[] = new byte[6];
	int n = is.read(buf);
	String head = null;
	if (n > 0) {
	    head = new String(buf, 0, 0, n);
	}
	is.reset();

	if (head != null && (head.startsWith("<!") 
	    || head.equalsIgnoreCase("<html>")
	    || head.equalsIgnoreCase("<body>")
	    || head.equalsIgnoreCase("<head>"))) {
	    setType(content_html);
	}

	return is;
    }


    /**
     * Force the content type of this URL to a specific value.
     * @param	type	The content type to use.  One of the
     *			content_* static variables in this
     *			class should be used.
     *			eg. setType(URL.content_html);
     */
    public void setType(String type) {
	content_type = type;
    }

    /**
     * Given a mime content type String (e.g. image/x-gif) produce the
     * internal URL type atom for that type.
     */
    public String mimeToContent(String type) {
	String s = (String) content_table.get(type);
	if (s == null) {
	    /* never seen this before! */
	    content_table.put(type, type);
	    s = type;
	}
	return s;
    }

    /**
     * Returns true if the data associated with this URL can be cached.
     */
    public boolean canCache() {
	return !isPostURL() && file.indexOf('?') < 0;
    }

    private static Hashtable typeht = new Hashtable();
    private static ContentHandler UnknownContentHandlerP = new UnknownContentHandler();
    private static String content_class_prefix = "net.www.content.";

    /**	Returns the content handler for the mimeType indicated by
	this URL.  Given a mime-type of the form major/minor we first look
	for a specific implementation class "net.www.content.major.minor",
	if that's missing we try "net.www.content.major.Generic",
	if that's missing we use net.www.html.UnknownContentHandler. */

    private ContentHandler contentHandler() {
	ContentHandler ret;
	if (content_type == null)
	    return UnknownContentHandlerP;
	ret = (ContentHandler) typeht.get(content_type);
	if (ret == null) {
	    try {
		int i = content_class_prefix.length();
		int j = content_type.length();
		int lastdot = 0;
		char nm[] = new char[i + j];
		content_class_prefix.getChars(0, i, nm, 0);
		content_type.getChars(0, j, nm, i);
		while (--j >= 0) {
		    char c = nm[i];
		    if (c == '/') nm[lastdot = i] = '.';
		    else if (!('A' <= c && c <= 'Z' ||
			       'a' <= c && c <= 'z' ||
			       '0' <= c && c <= 9)) nm[i] = '_';
		    i++;
		}
		String name = new String(nm);
		try {
		    ret = (ContentHandler) New(name);
		} catch(Exception e) {
		    /* Skip the search for a generic handler */
		    ret = UnknownContentHandlerP;
//		    if (lastdot <= 0) throw e;
//		    ret = (ContentHandler) New(name.substring(0,lastdot)+".Generic");
		}
	    } catch (Exception e) {
		ret = UnknownContentHandlerP;
	    }
	    typeht.put(content_type, ret);
	}
	return ret;
    }

    /**
     * Get the object referred to by this URL.  For example, if it refers to an image
     * the object will be some subclass if DIBitmap.  The instanceof operator should
     * be used to determine what kind of object was returned.
     * @return	the object that was fetched.
     */
    public Object getContent() {
	return getContent(openStream(), null);
    }

    static private MimeTable mt = null;

    /**
     * Get the object referred to by this URL.  For example, if it refers to an image
     * the object will be some subclass if DIBitmap.  The instanceof operator should
     * be used to determine what kind of object was returned.
     * @param	is	the stream to the Object.  It must have been created by an
     *			earlier call to openStream.
     * @return	the object that was fetched.  If there is no handler for the object,
     *		a stream is returned (logically the unmodified original stream, but
     *		it may have had another stream layered on top of it).  Generally the
     *		caller should prompt to save the file locally if they get a stream back.
     */
    public Object getContent(InputStream is, Observer o) {
	Object ret = null;
	
	if (is == null)
	    return null;
	if (mt == null)
	    mt = (MimeTable) new("net.www.html." + System.getOSName() + "MimeTable");
	if (!is.markSupported())
	    is = new BufferedInputStream(is);
	is.mark(1000);
	int c1 = is.read();
	int c2 = is.read();
	int c3 = is.read();
	int c4 = is.read();
	int c5 = is.read();
	int c6 = is.read();
	is.reset();

	/* First we check for a couple of well-known magic types: GIF and xbitmap */
	Checking: {
	if (c1 == 'G' && c2 == 'I' && c3 == 'F' && c4 == '8') {
	    /* dont trust the mime type:try reading it as
	     * a GIF image first */
	    ret = new GifImage(is, o);
	    content_type = content_gif;
	    return ret;
	} else if (c1 == '#' && c2 == 'd' && c3 == 'e' && c4 == 'f') {
	    ret = new XbmImage(is);
	    content_type = content_xbitmap;
	    break Checking;
	} else if (c1 == '!' && c2 == ' ' && c3 == 'X' && c4 == 'P' && 
		   c5 == 'M' && c6 == '2') {
	    ret = new Xpm2Image(is);
	    content_type = content_xpixmap;
	    break Checking;
	}
	/* Check for the Unknown content handler */
	if (content_type == content_unknown) {
	    ret = is;	/* dont bother looking for a handler */
	    break Checking;
	}

	/* Then we look for an exact match in the mime database */
	MimeEntry m = mt.find(content_type);
	if (m != null && !m.starred) {
	    ret = m.launch(is, this, mt);
	    break Checking;
	}

	/* Then we try for an external handler */
	try {
	    if (ret == null) {
		ContentHandler ch = contentHandler();
		if (ch != null && ch != UnknownContentHandlerP) {
		    ret = ch.getContent(is, this);
		    break Checking;
		}
	    }
	} catch(Exception e) {
	    is.close();
	    ret = "Failed to open "+toExternalForm()+": "+e;
	    break Checking;
	}

	/* Then we check for a generic (starred) mime database entry */
	if (m != null) {
	    ret = m.launch(is, this, mt);
	    break Checking;
	}

	/* If the stream is still intact, return the stream */
	try {
	    is.reset();
	    ret = is;
	} catch(Exception e) {
	    is.close();
	    ret = "Failed to open "+toExternalForm()+": "+e;
	}
	}
	if (o != null)
	    if (ret instanceof Observable) {
		o.update((Observable) ret);
	    }
	    else {
		/* observer requested but object not observable */
		is.close();
		throw new FormatException("Observable object expected");
	    }
	return ret;
    }


    /**
     * A simple main program for testing: fetches the object referenced by this URL
     * and prints it.
     */
    public static void main(String args[]) {
	URL url = new URL(null, args[0]);
	InputStream is = url.openStream();

	System.out.println("URL type = " + url.content_type);
	int c;
	while ((c = is.read()) != -1) {
	    System.out.write(c);
	}
	System.out.flush();
    }

    /**
     * Cache classloaders of classloaders
     */
    static Hashtable classloaders = new Hashtable();

    /**
     * Create an instance of a named class using a classloader that
     * references this URL.  It is similar to new(clname) except that
     * it specifies a URL to search.
     * @param	clname	The name of the class.
     * @return		The new instance of the class.
     */
    public Object New(String clname) {
	WWWClassLoader loader;
	if ((loader = (WWWClassLoader) classloaders.get(this)) == null) {
	    classloaders.put(this, loader = new WWWClassLoader(this));
	}
	return loader.loadClass(clname).newInstance();
    }

    /**
     * Flush the classloader cache.  This is only used when reloading a
     * document which went we want to force the system to reload all the
     * applets on that document.  Flushing the cache is not the most
     * efficient mechanism but it is the most expedient.
     */
    static synchronized public void flushClassLoader() {
	classloaders = new Hashtable();
    }

}
