/*
 * @(#)URLStreamHandler.java	1.12 95/02/07 James Gosling
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

/*- 
 *	abstract class for url stream openers
 */

package net.www.html;

import java.util.Hashtable;
import java.io.*;

/** Subclasses of this class know how to create streams for particular
    protocol types */
public class URLStreamHandler {
    static Hashtable extension_map = new Hashtable();

    static {
	URL.classInit();
	setSuffix("", URL.content_unknown);
	setSuffix(".uu", URL.content_octet);
	setSuffix(".saveme", URL.content_octet);
	setSuffix(".dump", URL.content_octet);
	setSuffix(".hqx", URL.content_octet);
	setSuffix(".arc", URL.content_octet);
	setSuffix(".o", URL.content_octet);
	setSuffix(".a", URL.content_octet);
	setSuffix(".bin", URL.content_octet);
	setSuffix(".exe", URL.content_octet);
	/* Temporary only. */
	setSuffix(".z",	        URL.content_octet);
	setSuffix(".gz", URL.content_octet);

	setSuffix(".oda", URL.content_oda);
	setSuffix(".pdf", URL.content_pdf);
	setSuffix(".eps", URL.content_postscript);
	setSuffix(".ai", URL.content_postscript);
	setSuffix(".ps", URL.content_postscript);
	setSuffix(".rtf", URL.content_richtext);
	setSuffix(".dvi", URL.content_dvi);
	setSuffix(".hdf", URL.content_hdf);
	setSuffix(".latex", URL.content_latex);
	setSuffix(".cdf", URL.content_netcdf);
	setSuffix(".nc", URL.content_netcdf);
	setSuffix(".tex", URL.content_tex);
	setSuffix(".texinfo", URL.content_texinfo);
	setSuffix(".texi", URL.content_texinfo);
	setSuffix(".t", URL.content_troff);
	setSuffix(".tr", URL.content_troff);
	setSuffix(".roff", URL.content_troff);
	setSuffix(".man", URL.content_man);
	setSuffix(".me", URL.content_me);
	setSuffix(".ms", URL.content_ms);
	setSuffix(".src", URL.content_source);
	setSuffix(".wsrc", URL.content_source);
	setSuffix(".zip", URL.content_zip);
	setSuffix(".bcpio", URL.content_bcpio);
	setSuffix(".cpio", URL.content_cpio);
	setSuffix(".gtar", URL.content_gtar);
	setSuffix(".shar", URL.content_shar);
	setSuffix(".sh", URL.content_shar);
	setSuffix(".sv4cpio", URL.content_sv4cpio);
	setSuffix(".sv4crc", URL.content_sv4crc);
	setSuffix(".tar", URL.content_tar);
	setSuffix(".ustar", URL.content_ustar);
	setSuffix(".snd", URL.content_basic);
	setSuffix(".au", URL.content_basic);
	setSuffix(".aifc", URL.content_aiff);
	setSuffix(".aif", URL.content_aiff);
	setSuffix(".aiff", URL.content_aiff);
	setSuffix(".wav", URL.content_wav);
	setSuffix(".gif", URL.content_gif);
	setSuffix(".ief", URL.content_ief);
	setSuffix(".jfif", URL.content_jpeg);
	setSuffix(".jfif-tbnl", URL.content_jpeg);
	setSuffix(".jpe", URL.content_jpeg);
	setSuffix(".jpg", URL.content_jpeg);
	setSuffix(".jpeg", URL.content_jpeg);
	setSuffix(".tif", URL.content_tiff);
	setSuffix(".tiff", URL.content_tiff);
	setSuffix(".ras", URL.content_rast);
	setSuffix(".pnm", URL.content_anymap);
	setSuffix(".pbm", URL.content_bitmap);
	setSuffix(".pgm", URL.content_graymap);
	setSuffix(".ppm", URL.content_pixmap);
	setSuffix(".rgb", URL.content_rgb);
	setSuffix(".xbm", URL.content_xbitmap);
	setSuffix(".xpm", URL.content_xpixmap);
	setSuffix(".xwd", URL.content_xwindowdump);
	setSuffix(".htm", URL.content_html);
	setSuffix(".html", URL.content_html);
	setSuffix(".text", URL.content_plain);
	setSuffix(".c", URL.content_plain);
	setSuffix(".cc", URL.content_plain);
	setSuffix(".c++", URL.content_plain);
	setSuffix(".h", URL.content_plain);
	setSuffix(".pl", URL.content_plain);
	setSuffix(".txt", URL.content_plain);
	setSuffix(".java", URL.content_plain);
	setSuffix(".rtx", URL.content_richtext);
	setSuffix(".tsv", URL.content_values);
	setSuffix(".etx", URL.content_setext);
	setSuffix(".mpg", URL.content_mpeg);
	setSuffix(".mpe", URL.content_mpeg);
	setSuffix(".mpeg", URL.content_mpeg);
	setSuffix(".mov", URL.content_quicktime);
	setSuffix(".qt", URL.content_quicktime);
	setSuffix(".avi", URL.content_msvideo);
	setSuffix(".movie", URL.content_movie);
	setSuffix(".mv", URL.content_movie);
	setSuffix(".mime", URL.content_rfc822);
    }


    static private void setSuffix(String ext, String ct) {
	extension_map.put(ext, ct);
    }

    /**
     * Open an input stream to the object referenced by the URL.  Should be
     * overridden by a subclass.  Failure is indicated by throwing an
     * exception.
     * @return	The opened input stream.  A value of null indicates that while
     * the open was successful, there is no useful data provided by this
     * protocol, it's done for side-effect only (the usual example is the
     * "mailto" protocol).
     */
    abstract public InputStream openStream(URL u);

    /**
     * Similar to openStream except that it allows the stream handler
     * to interact with the user to resolve certain problems.  For
     * example, the http handler will prompt for a user name and
     * password to handle authentication failures.  In these cases,
     * openStream would just toss an exception.  If this method is
     * not overridden, it behaves exactly like openStream.
     */
    public InputStream openStreamInteractively(URL u) {
        return openStream(u);
    }

    /**
	Try to guess the contents of a file based upon its extension.
    */
    public static String formatFromName(String fname) {
	String ext = "";
	int i = fname.lastIndexOf('#');

	// Strip off the #nameref, if any.
	//
	if (i != -1) 
	    fname = fname.substring(0, i-1);
	i = fname.lastIndexOf('.');
	i = Math.max(i, fname.lastIndexOf('/'));
	i = Math.max(i, fname.lastIndexOf('?'));

	if (i != -1 && fname.charAt(i) == '.') {
	    ext = fname.substring(i).toLowerCase();
	}
	String ret = (String) extension_map.get(ext);
	return ret != null ? ret : URL.content_unknown;
    }
}
