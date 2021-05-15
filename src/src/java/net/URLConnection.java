/*
 * @(#)URLConnection.java	1.18 95/12/18
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
import java.util.Date;

/**
 * A class to represent an active connection to an object
 * represented by a URL. It is an abstract class that must be
 * subclassed to implement a connection.
 *
 * @version 	1.18, 12/18/95
 * @author  James Gosling
 */
abstract public class URLConnection {
    protected URL url;

    protected boolean doInput = true;
    protected boolean doOutput = false;
    private static boolean defaultAllowUserInteraction = false;
    protected boolean allowUserInteraction = defaultAllowUserInteraction;
    private static boolean defaultUseCaches = true;
    protected boolean useCaches = defaultUseCaches;
    protected long ifModifiedSince = 0;

    protected boolean connected = false;

    /**
     * URLConnection objects go through two phases: first they are
     * created, then they are connected.  After being created, and
     * before being connected, various options can be specified
     * (eg. doInput, UseCaches, ...).  After connecting, it is an
     * Error to try to set them.  Operations that depend on being
     * connected, like getContentLength, will implicitly perform the
     * connection if necessary.  Connecting when already connected
     * does nothing.
     */
    abstract public void connect() throws IOException;


    /**
     * Constructs a URL connection to the specified URL.
     * @param url the specified URL
     */
    protected URLConnection (URL url) {
	this.url = url;
    }

    /**
     * Gets the URL for this connection.
     */
    public URL getURL() {
	return url;
    }

    /**
     * Gets the content length. Returns -1 if not known.
     */
    public int getContentLength() {
	return getHeaderFieldInt("content-length", -1);
    }

    /**
     * Gets the content type. Returns null if not known.
     */
    public String getContentType() {
	return getHeaderField("content-type");
    }

    /**
     * Gets the content encoding. Returns null if not known.
     */
    public String getContentEncoding() {
	return getHeaderField("content-encoding");
    }

    /**
     * Gets the expriation date of the object. Returns 0 if not known.
     */
    public long getExpiration() {
	return getHeaderFieldDate("expires", 0);
    }

    /**
     * Gets the sending date of the object. Returns 0 if not known.
     */
    public long getDate() {
	return getHeaderFieldDate("date", 0);
    }

    /**
     * Gets the last modified date of the object. Returns 0 if not known.
     */
    public long getLastModified() {
	return getHeaderFieldDate("last-modified", 0);
    }

    /**
     * Gets a header field by name. Returns null if not known.
     * @param name the name of the header field
     */
    public String getHeaderField(String name) {
	return null;
    }

    /**
     * Gets a header field by name. Returns null if not known.
     * The field is parsed as an integer.  This form of
     * getHeaderField exists because some connection types
     * (e.g. http-ng) have pre-parsed headers and  this allows them
     * to override this method and short-circuit the parsing.
     * @param name the name of the header field
     * @param Default the value to return if the field is missing
     *	or malformed.
     */
    public int getHeaderFieldInt(String name, int Default) {
	try {
	    return Integer.parseInt(getHeaderField(name));
	} catch(Throwable t) {}
	return Default;
    }

    /**
     * Gets a header field by name. Returns null if not known.
     * The field will be parsed as a date.  This form of
     * getHeaderField exists because some connection types
     * (eg. http-ng) have pre-parsed headers. This allows them
     * to override this method and short-circuit the parsing.
     * @param name the name of the header field
     * @param Default the value to return if the field is missing
     *	or malformed.
     */
    public long getHeaderFieldDate(String name, long Default) {
	try {
	    return Date.parse(getHeaderField(name));
	} catch(Throwable t) {}
	return Default;
    }

    /**
     * Returns the key for the nth header field. Returns null if
     * there are fewer than n fields.  This can be used to iterate
     * through all the headers in the message.
     */
    public String getHeaderFieldKey(int n) {
	return null;
    }

    /**
     * Returns the value for the nth header field. Returns null if
     * there are fewer than n fields.  This can be used in conjunction
     * with getHeaderFieldKey to iterate through all the headers in the message.
     */
    public String getHeaderField(int n) {
	return null;
    }

    /**
     * Gets the object referred to by this URL.  For example, if it
     * refers to an image the object will be some subclass of
     * Image.  The instanceof operator should be used to determine
     * what kind of object was returned.
     * @return	the object that was fetched.
     * @exception UnknownServiceException If the protocol does not
     * support content.
     */
    public Object getContent() throws IOException {
	return getContentHandler().getContent(this);
    }

    /**
     * Calls this routine to get an InputStream that reads from the object.
     * Protocol implementors should use this if appropriate.
     * @exception UnknownServiceException If the protocol does not
     * support input.
     */
    public InputStream getInputStream() throws IOException {
	throw new UnknownServiceException("protocol doesn't support input");
    }

    /**
     * Calls this routine to get an OutputStream that writes to the object.
     * Protocol implementors should use this if appropriate.
     * @exception UnknownServiceException If the protocol does not
     * support output.
     */
    public OutputStream getOutputStream() throws IOException {
	throw new UnknownServiceException("protocol doesn't support output");
    }

    /**
     * Returns the String representation of the URL connection.
     */
    public String toString() {
	return this.getClass().getName() + ":" + url;
    }


    /** A URL connection can be used for input and/or output.  Set the DoInput
        flag to true if you intend to use the URL connection for input,
        false if not.  The default is true unless DoOutput is explicitly
        set to true, in which case DoInput defaults to false.  */
    public void setDoInput(boolean doinput) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	doInput = doinput;
    }
    public boolean getDoInput() {
	return doInput;
    }

    /** A URL connection can be used for input and/or output.  Set the DoOutput
        flag to true if you intend to use the URL connection for output,
        false if not.  The default is false. */
    public void setDoOutput(boolean dooutput) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	doOutput = dooutput;
    }
    public boolean getDoOutput() {
	return doOutput;
    }

    /** Some URL connections occasionally need to to interactions with the
        user.  For example, the http protocol may need to pop up an authentication
        dialog.  But this is only appropriate if the application is running
        in a context where there <i>is</i> a user.  The allowUserInteraction
        flag allows these interactions when true.  When it is false, they are
        not allowed and an exception is tossed. The default value can be
        set/gotten using setDefaultAllowUserInteraction, which defaults to false. */
    public void setAllowUserInteraction(boolean allowuserinteraction) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	allowUserInteraction = allowuserinteraction;
    }
    public boolean getAllowUserInteraction() {
	return allowUserInteraction;
    }

    /** Sets/gets the default value of the allowUserInteraction flag.  This default
        is "sticky", being a part of the static state of all URLConnections.  This
        flag applies to the next, and all following URLConnections that are created. */
    public static void setDefaultAllowUserInteraction(boolean defaultallowuserinteraction) {
	defaultAllowUserInteraction = defaultallowuserinteraction;
    }
    public static boolean getDefaultAllowUserInteraction() {
	return defaultAllowUserInteraction;
    }

    /** Some protocols do caching of documents.  Occasionally, it is important to be
        able to "tunnel through" and ignore the caches (e.g. the "reload" button in
        a browser).  If the UseCaches flag on a connection is true, the connection is
        allowed to use whatever caches it can.  If false, caches are to be ignored.
        The default value comes from DefaultUseCaches, which defaults to true. */
    public void setUseCaches(boolean usecaches) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	useCaches = usecaches;
    }
    public boolean getUseCaches() {
	return useCaches;
    }

    /** Some protocols support skipping fetching unless the object is newer than some amount of time.
	The ifModifiedSince field may be set/gotten to define this time. */
    public void setIfModifiedSince(long ifmodifiedsince) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	ifModifiedSince = ifmodifiedsince;
    }
    public long getIfModifiedSince() {
	return ifModifiedSince;
    }

    /** Sets/gets the default value of the UseCaches flag.  This default
        is "sticky", being a part of the static state of all URLConnections.  This
        flag applies to the next, and all following, URLConnections that are created. */
    public boolean getDefaultUseCaches() {
	return defaultUseCaches;
    }
    public void setDefaultUseCaches(boolean defaultusecaches) {
	defaultUseCaches = defaultusecaches;
    }

    /**
     * Sets/gets a general request property.
     * @param key The keyword by which the request is known (eg "accept")
     * @param value The value associated with it.
     */
    public void setRequestProperty(String key, String value) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
    }
    public String getRequestProperty(String key) {
	if (connected)
	    throw new IllegalAccessError("Already connected");
	return null;
    }

    /**
     * Sets/gets the default value of a general request property. When a
     * URLConnection is created, it is initialized with these properties.
     * @param key The keyword by which the request is known (eg "accept")
     * @param value The value associated with it.
     */
    public static void setDefaultRequestProperty(String key, String value) {
    }
    public static String getDefaultRequestProperty(String key) {
	return null;
    }

    /**
     * The ContentHandler factory.
     */
    static ContentHandlerFactory factory;

    /**
     * Sets the ContentHandler factory.
     * @param fac the desired factory
     * @exception Error If the factory has already been defined.
     */
    public static synchronized void setContentHandlerFactory(ContentHandlerFactory fac) {
	if (factory != null) {
	    throw new Error("factory already defined");
	}
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkSetFactory();
	}
	factory = fac;
    }

    private static Hashtable handlers = new Hashtable();
    private static ContentHandler UnknownContentHandlerP = new UnknownContentHandler();
    private static String content_class_prefix = "sun.net.www.content.";

    /**
     * Gets the Content Handler appropriate for this connection.
     * @param connection the connection to use.
     */
    synchronized ContentHandler getContentHandler()
    throws UnknownServiceException
    {
	String contentType = getContentType();
	ContentHandler handler = null;
	if (contentType == null)
	    throw new UnknownServiceException("no content-type");
	try {
	    handler = (ContentHandler) handlers.get(contentType);
	    if (handler != null)
		return handler;
	} catch(Exception e) {
	}
	if (factory != null)
	    handler = factory.createContentHandler(contentType);
	if (handler == null) {
	    try {
		int i = content_class_prefix.length();
		int j = contentType.length();
		char nm[] = new char[i + j];
		content_class_prefix.getChars(0, i, nm, 0);
		contentType.getChars(0, j, nm, i);
		while (--j >= 0) {
		    char c = nm[i];
		    if (c == '/')
			nm[i] = '.';
		    else if (!('A' <= c && c <= 'Z' ||
			       'a' <= c && c <= 'z' ||
			       '0' <= c && c <= '9'))
			nm[i] = '_';
		    i++;
		}
		String name = new String(nm);
		try {
		    handler = (ContentHandler) Class.forName(name).newInstance();
		} catch(Exception e) {
		    e.printStackTrace();
		    handler = UnknownContentHandlerP;
		}
	    } catch(Exception e) {
		e.printStackTrace();
		handler = UnknownContentHandlerP;
	    }
	    handlers.put(contentType, handler);
	}
	return handler;
    }

    /**
     * A useful utility routine that tries to guess the content-type
     * of an object based upon its extension.
     */
    protected static String guessContentTypeFromName(String fname) {
	String ext = "";
	int i = fname.lastIndexOf('#');

	if (i != -1)
	    fname = fname.substring(0, i - 1);
	i = fname.lastIndexOf('.');
	i = Math.max(i, fname.lastIndexOf('/'));
	i = Math.max(i, fname.lastIndexOf('?'));

	if (i != -1 && fname.charAt(i) == '.') {
	    ext = fname.substring(i).toLowerCase();
	}
	return (String) extension_map.get(ext);
    }

    static Hashtable extension_map = new Hashtable();

    static {
	setSuffix("", "content/unknown");
	setSuffix(".uu", "application/octet-stream");
	setSuffix(".saveme", "application/octet-stream");
	setSuffix(".dump", "application/octet-stream");
	setSuffix(".hqx", "application/octet-stream");
	setSuffix(".arc", "application/octet-stream");
	setSuffix(".o", "application/octet-stream");
	setSuffix(".a", "application/octet-stream");
	setSuffix(".bin", "application/octet-stream");
	setSuffix(".exe", "application/octet-stream");
	/* Temporary only. */
	setSuffix(".z", "application/octet-stream");
	setSuffix(".gz", "application/octet-stream");

	setSuffix(".oda", "application/oda");
	setSuffix(".pdf", "application/pdf");
	setSuffix(".eps", "application/postscript");
	setSuffix(".ai", "application/postscript");
	setSuffix(".ps", "application/postscript");
	setSuffix(".rtf", "application/rtf");
	setSuffix(".dvi", "application/x-dvi");
	setSuffix(".hdf", "application/x-hdf");
	setSuffix(".latex", "application/x-latex");
	setSuffix(".cdf", "application/x-netcdf");
	setSuffix(".nc", "application/x-netcdf");
	setSuffix(".tex", "application/x-tex");
	setSuffix(".texinfo", "application/x-texinfo");
	setSuffix(".texi", "application/x-texinfo");
	setSuffix(".t", "application/x-troff");
	setSuffix(".tr", "application/x-troff");
	setSuffix(".roff", "application/x-troff");
	setSuffix(".man", "application/x-troff-man");
	setSuffix(".me", "application/x-troff-me");
	setSuffix(".ms", "application/x-troff-ms");
	setSuffix(".src", "application/x-wais-source");
	setSuffix(".wsrc", "application/x-wais-source");
	setSuffix(".zip", "application/zip");
	setSuffix(".bcpio", "application/x-bcpio");
	setSuffix(".cpio", "application/x-cpio");
	setSuffix(".gtar", "application/x-gtar");
	setSuffix(".shar", "application/x-shar");
	setSuffix(".sh", "application/x-shar");
	setSuffix(".sv4cpio", "application/x-sv4cpio");
	setSuffix(".sv4crc", "application/x-sv4crc");
	setSuffix(".tar", "application/x-tar");
	setSuffix(".ustar", "application/x-ustar");
	setSuffix(".snd", "audio/basic");
	setSuffix(".au", "audio/basic");
	setSuffix(".aifc", "audio/x-aiff");
	setSuffix(".aif", "audio/x-aiff");
	setSuffix(".aiff", "audio/x-aiff");
	setSuffix(".wav", "audio/x-wav");
	setSuffix(".gif", "image/gif");
	setSuffix(".ief", "image/ief");
	setSuffix(".jfif", "image/jpeg");
	setSuffix(".jfif-tbnl", "image/jpeg");
	setSuffix(".jpe", "image/jpeg");
	setSuffix(".jpg", "image/jpeg");
	setSuffix(".jpeg", "image/jpeg");
	setSuffix(".tif", "image/tiff");
	setSuffix(".tiff", "image/tiff");
	setSuffix(".ras", "image/x-cmu-rast");
	setSuffix(".pnm", "image/x-portable-anymap");
	setSuffix(".pbm", "image/x-portable-bitmap");
	setSuffix(".pgm", "image/x-portable-graymap");
	setSuffix(".ppm", "image/x-portable-pixmap");
	setSuffix(".rgb", "image/x-rgb");
	setSuffix(".xbm", "image/x-xbitmap");
	setSuffix(".xpm", "image/x-xpixmap");
	setSuffix(".xwd", "image/x-xwindowdump");
	setSuffix(".htm", "text/html");
	setSuffix(".html", "text/html");
	setSuffix(".text", "text/plain");
	setSuffix(".c", "text/plain");
	setSuffix(".cc", "text/plain");
	setSuffix(".c++", "text/plain");
	setSuffix(".h", "text/plain");
	setSuffix(".pl", "text/plain");
	setSuffix(".txt", "text/plain");
	setSuffix(".java", "text/plain");
	setSuffix(".rtx", "application/rtf");
	setSuffix(".tsv", "text/tab-separated-values");
	setSuffix(".etx", "text/x-setext");
	setSuffix(".mpg", "video/mpeg");
	setSuffix(".mpe", "video/mpeg");
	setSuffix(".mpeg", "video/mpeg");
	setSuffix(".mov", "video/quicktime");
	setSuffix(".qt", "video/quicktime");
	setSuffix(".avi", "application/x-troff-msvideo");
	setSuffix(".movie", "video/x-sgi-movie");
	setSuffix(".mv", "video/x-sgi-movie");
	setSuffix(".mime", "message/rfc822");
    }

    static private void setSuffix(String ext, String ct) {
	extension_map.put(ext, ct);
    }

    /**
     * This method is used to check for files that have some type
     * that can be determined by inspection.  The bytes at the beginning
     * of the file are examined loosely.  In an ideal world, this routine
     * would not be needed, but in a world where http servers lie
     * about content-types and extensions are often non-standard,
     * direct inspection of the bytes can make the system more robust.
     * The stream must support marks (e.g. have a BufferedInputStream
     * somewhere).
     */
    static protected String guessContentTypeFromStream(InputStream is) throws IOException
    {
	is.mark(10);
	int c1 = is.read();
	int c2 = is.read();
	int c3 = is.read();
	int c4 = is.read();
	int c5 = is.read();
	int c6 = is.read();
	is.reset();
	if (c1 == 'G' && c2 == 'I' && c3 == 'F' && c4 == '8')
	    return "image/gif";
	if (c1 == '#' && c2 == 'd' && c3 == 'e' && c4 == 'f')
	    return "image/x-bitmap";
	if (c1 == '!' && c2 == ' ' && c3 == 'X' && c4 == 'P' && c5 == 'M' && c6 == '2')
	    return "image/x-pixmap";
	if (c1 == '<')
	    if (c2 == '!'
		    || (c6 == '>'
		    && (c2 == 'h' && (c3 == 't' && c4 == 'm' && c5 == 'l' ||
				      c3 == 'e' && c4 == 'a' && c5 == 'd')
		      || c2 == 'b' && c3 == 'o' && c4 == 'd' && c5 == 'y')))
		return "text/html";
	return null;
    }

}

class UnknownContentHandler extends ContentHandler {
    public Object getContent(URLConnection uc) throws IOException {
	return uc.getInputStream();
    }
}
