/*
 * @(#)Handler.java	1.21 95/03/22 James Gosling
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
 *	ftp stream opener
 */

package net.www.protocol.ftp;

import java.io.*;
import net.ftp.*;
import net.UnknownHostException;
import net.www.protocol.http.Handler;
import java.util.StringTokenizer;
import net.www.html.*;
import java.util.Hashtable;

/** open an ftp input stream given a URL */
class Handler extends URLStreamHandler {
    public synchronized InputStream openStream(URL u) {
	FtpClient			ftp;
	net.www.protocol.http.Handler	http;

	if (FtpClient.useFtpProxy &&
	    FtpClient.ftpProxyHost != null &&
	    FtpClient.ftpProxyHost.length() > 0) {
	    http = new net.www.protocol.http.Handler(FtpClient.ftpProxyHost,
						     FtpClient.ftpProxyPort);
	    return http.openStreamInteractively(u);
	}

	try {
	    ftp = new FtpClient(u.host);
	} catch(UnknownHostException e) {
	    //XXX: iftp seems specific to Sun. We should
	    // probably just fail here.
	    ftp = new IftpClient(u.host);
	}

	ftp.login("anonymous", "HotJavaVa2@");

	u.setType(formatFromName(u.file));
	InputStream is;

	try {
	    ftp.binary();
	    is = ftp.get(u.file);

	    /* Try to get the size of the file in bytes.  If that's
	       successful, then create a MeteredStream. */
	    try {
		String  response = ftp.getResponseString();
		int	    offset;

		if ((offset = response.indexOf(" bytes)")) != -1) {
		    int i = offset;
		    int c;

		    while (--i >= 0 && ((c = response.charAt(i)) >= '0'
					&& c <= '9'))
			;
		    i = Integer.parseInt(response.substring(i + 1, offset));
		    if (i > 0) {
			is = new MeteredStream(is, i);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
		/* do nothing, since all we were doing was trying to
		   get the size in bytes of the file */
	    }
	} catch (FileNotFoundException e) {
	    u.setType(u.content_html);
	    ftp.cd(u.file);
	    /* if that worked, then make a directory listing
	       and build an html stream with all the files in
	       the directory */
	    ftp.ascii();

	    PipedOutputStream   os = new PipedOutputStream();

	    is = new PipedInputStream(os);
	    new FtpDirectoryThread(ftp, u, ftp.list(), os).start();
	}
	return is;
    }
}

class FtpDirectoryThread extends Thread {
    static Hashtable	images = new Hashtable();
    static String fullImagePath(String name) {
	return "doc:/demo/images/ftp/" + name + ".gif";
    }

    static {
	images.put(URL.content_gif, fullImagePath("gif"));
	images.put(URL.content_tiff, fullImagePath("tiff"));
	images.put(URL.content_basic, fullImagePath("audio"));
	images.put(URL.content_octet, fullImagePath("compress"));
	images.put(URL.content_postscript, fullImagePath("ps"));
	images.put(URL.content_plain, fullImagePath("text"));
    }

    FtpClient	    ftp;
    PrintStream	    os;
    InputStream	    is;
    URL		    url;

    FtpDirectoryThread(FtpClient ftp, URL url, InputStream is,
		       OutputStream os) {
	this.ftp = ftp;
	this.url = url;
	this.is = is;
	this.os = new PrintStream(new BufferedOutputStream(os));
    }

    /* drwxrwxr-x  30 jpayne   staff       1024 Jun 20  1994 src */

    public void run() {
	try {
	    is = new BufferedInputStream(is);

	    byte	    data[] = new byte[512];
	    String	    file;
	    DataInputStream dis = new DataInputStream(is);
	    String	    urlString = url.toExternalForm();
	    String	    title;

	    if (!urlString.endsWith("/")) {
		urlString += "/";
	    }
	    title = "Directory: " + url.file + "@" + url.host;

	    os.println("<html>\n<head>\n<title>"
		       + title + "</title>\n</head>");
	    os.println("<body>");
	    os.println("<h2>" + title + "</h2>");
	    os.println("<pre>");

	    addFile('d', "&lt;Parent Directory&gt;", null,
		    new URL(null, urlString + "../"));
	    while ((file = dis.readLine()) != null) {
		StringTokenizer	t = new StringTokenizer(file, " ");
		String	mode;
		String	size;
		String	name;

		try {
		    mode = t.nextToken();
		    t.nextToken();
		    t.nextToken();
		    t.nextToken();
		    size = t.nextToken();
		    t.nextToken();
		    t.nextToken();
		    t.nextToken();
		    name = t.nextToken();
		    if (name.equals(".") || name.equals("..")) {
			continue;
		    } else {
			addFile(mode.charAt(0) == 'd' ? 'd' : 'f',
				name, size, new URL(null, urlString + name));
		    }
		} catch (NoSuchElementException e) {
		}
	    }
	    os.println("</pre></body>\n</html>");
	} finally {
	    os.close();
	    is.close();
	    ftp.closeServer();
	}
    }

    void addImage(String name) {
	os.print("<img align=\"middle\" src=\"" + name + "\">");
    }

    String lookupImage(String contentType) {
	return (String) images.get(contentType);
    }

    void addFile(int type, String name, String size, URL url) {
	os.print("  ");
	switch (type) {
	case 'f':
	    url.setType(URLStreamHandler.formatFromName(url.file));
	    {
		String	image = lookupImage(url.content_type);

		if (image == null) {
		    image = fullImagePath("file");
		}

		addImage(image);
	    }
	    break;

	case 'd':
	    addImage(fullImagePath("directory"));
	    break;
	}
	os.print("\t<a href=\"" + url.toExternalForm() + "\">");
	os.print(name);
	if (type == 'd') {
	    os.print("/");
	} else if (size != null) {
	    os.print(" (" + size + " bytes)");
	}
	os.print("</a>");
	os.print("\n");
    }
}

