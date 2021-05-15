/*
 * @(#)Handler.java	1.2 95/03/29
 * 
 * Copyright (c) 1995 Sun Microsystems, Inc.  All Rights reserved Permission to
 * use, copy, modify, and distribute this software and its documentation for
 * NON-COMMERCIAL purposes and without fee is hereby granted provided that
 * this copyright notice appears in all copies. Please refer to the file
 * copyright.html for further important copyright and licensing information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
 * ITS DERIVATIVES.
 */

package net.www.protocol.gopher;

import java.io.*;
import java.util.*;
import net.*;
import net.www.html.URL;
import net.www.html.URLStreamHandler;

/**
 * A class to handle the gopher protocol.
 */

class Handler extends URLStreamHandler {
    public InputStream openStream(URL u) {
	return new gopherFetcher().openStream(u);
    }
}

/** Class to maintain the state of a gopher fetch and handle the protocol */
class gopherFetcher extends NetworkClient implements Runnable {
    PipedOutputStream os;
    URL u;
    int gtype;
    String gkey;

    /** Given a url, setup to fetch the gopher document it refers to */
    InputStream openStream(URL u) {
	this.u = u;
	this.os = os;
	int i = 0;
	String s = u.file;
	int limit = s.length();
	int c = '1';
	while (i < limit && (c = s.charAt(i)) == '/')
	    i++;
	gtype = c == '/' ? '1' : c;
	if (i < limit)
	    i++;
	gkey = s.substring(i);
	try {
	    openServer(u.host, u.port <= 0 ? 70 : u.port);
	} catch(UnknownHostException e) {

	    /*
	     * We may have to punch through the sun firewall
	     */
	    openServer("sun-barr.ebay.sun.com", 3666);
	    serverOutput.print(u.host + " " + (u.port <= 0 ? 70 : u.port) + "\n");
	    serverOutput.flush();
	    /* Now we have to read two bogus lines from the front */
	    i = 0;
	    while ((c = serverInput.read()) >= 0)
		if (c == '\n' && ++i >= 2)
		    break;
	    if (c < 0) {
		closeServer();
		throw new UnknownHostException(u.host);
	    }
	}
	switch (gtype) {
	  case '0':
	  case '7':
	    u.setType(URL.content_plain);
	    break;
	  case '1':
	    u.setType(URL.content_html);
	    break;
	  case 'g':
	  case 'I':
	    u.setType(URL.content_gif);
	    break;
	  default:
	    u.setType(URL.content_unknown);
	    break;
	}
	if (gtype != '7') {
	    serverOutput.print(decodePercent(gkey) + "\r\n");
	    serverOutput.flush();
	} else if ((i = gkey.indexOf('?')) >= 0) {
	    serverOutput.print(decodePercent(gkey.substring(0, i) + "\t" +
					   gkey.substring(i + 1) + "\r\n"));
	    serverOutput.flush();
	    u.setType(URL.content_html);
	} else
	    u.setType(URL.content_html);
	if (u.content_type == URL.content_html) {
	    os = new PipedOutputStream();
	    PipedInputStream ret = new PipedInputStream();
	    ret.connect(os);
	    new Thread(this).start();
	    return ret;
	}
	return new GopherInputStream(this, serverInput);
    }

    /** Translate all the instances of %NN into the character they represent */
    private String decodePercent(String s) {
	if (s == null || s.indexOf('%') < 0)
	    return s;
	int limit = s.length();
	char d[] = new char[limit];
	int dp = 0;
	for (int sp = 0; sp < limit; sp++) {
	    int c = s.charAt(sp);
	    if (c == '%' && sp + 2 < limit) {
		int s1 = s.charAt(sp + 1);
		int s2 = s.charAt(sp + 2);
		if ('0' <= s1 && s1 <= '9')
		    s1 = s1 - '0';
		else if ('a' <= s1 && s1 <= 'f')
		    s1 = s1 - 'a' + 10;
		else if ('A' <= s1 && s1 <= 'F')
		    s1 = s1 - 'A' + 10;
		else
		    s1 = -1;
		if ('0' <= s2 && s2 <= '9')
		    s2 = s2 - '0';
		else if ('a' <= s2 && s2 <= 'f')
		    s2 = s2 - 'a' + 10;
		else if ('A' <= s2 && s2 <= 'F')
		    s2 = s2 - 'A' + 10;
		else
		    s2 = -1;
		if (s1 >= 0 && s2 >= 0) {
		    c = (s1 << 4) | s2;
		    sp += 2;
		}
	    }
	    d[dp++] = (char) c;
	}
	return new String(d, 0, dp);
    }

    /** Turn special characters into the %NN form */
    private String encodePercent(String s) {
	if (s == null)
	    return s;
	int limit = s.length();
	char d[] = null;
	int dp = 0;
	for (int sp = 0; sp < limit; sp++) {
	    int c = s.charAt(sp);
	    if (c <= ' ' || c == '"' || c == '%') {
		if (d == null)
		    d = s.toCharArray();
		if (dp + 3 >= d.length) {
		    char nd[] = new char[dp + 10];
		    System.arraycopy(d, 0, nd, 0, dp);
		    d = nd;
		}
		d[dp] = '%';
		int dig = (c >> 4) & 0xF;
		d[dp + 1] = (char) (dig < 10 ? '0' + dig : 'A' - 10 + dig);
		dig = c & 0xF;
		d[dp + 2] = (char) (dig < 10 ? '0' + dig : 'A' - 10 + dig);
		dp += 3;
	    } else {
		if (d != null) {
		    if (dp >= d.length) {
			char nd[] = new char[dp + 10];
			System.arraycopy(d, 0, nd, 0, dp);
			d = nd;
		    }
		    d[dp] = (char) c;
		}
		dp++;
	    }
	}
	return d == null ? s : new String(d, 0, dp);
    }

    /** This method is run as a seperate thread when an incoming gopher
	document requires translation to html */
    public void run() {
	int qpos = -1;
	try {
	    if (gtype == '7' && (qpos = gkey.indexOf('?')) < 0) {
		PrintStream ps = new PrintStream(os);
		ps.print("<html><head><title>Searchable Gopher Index</title></head>\n<body><h1>Searchable Gopher Index</h1><isindex>\n</body></html>\n");
	    } else if (gtype != '1' && gtype != '7') {
		byte buf[] = new byte[2048];
		try {
		    int n;
		    while ((n = serverInput.read(buf)) >= 0)
			    os.write(buf, 0, n);
		} catch(Exception e) {
		}
	    } else {
		PrintStream ps = new PrintStream(os);
		String title = null;
		if (gtype == '7')
		    title = "Results of searching for \"" + gkey.substring(qpos + 1)
			+ "\" on " + u.host;
		else
		    title = "Gopher directory " + gkey + " from " + u.host;
		ps.print("<html><head><title>");
		ps.print(title);
		ps.print("</title></head>\n<body>\n<H1>");
		ps.print(title);
		ps.print("</h1><dl compact>\n");
		DataInputStream ds = new DataInputStream(serverInput);
		String s;
		while ((s = ds.readLine()) != null) {
		    int len = s.length();
		    while (len > 0 && s.charAt(len - 1) <= ' ')
			len--;
		    if (len <= 0)
			continue;
		    int key = s.charAt(0);
		    int t1 = s.indexOf('\t');
		    int t2 = t1 > 0 ? s.indexOf('\t', t1 + 1) : -1;
		    int t3 = t2 > 0 ? s.indexOf('\t', t2 + 1) : -1;
		    if (t3 < 0) {
			// ps.print("<br><i>"+s+"</i>\n");
			continue;
		    }
		    String port = t3 + 1 < len ? ":" + s.substring(t3 + 1, len) : "";
		    String host = t2 + 1 < t3 ? s.substring(t2 + 1, t3) : u.host;
		    ps.print("<dt><a href=\"gopher://" + host + port + "/"
			     + s.substring(0, 1) + encodePercent(s.substring(t1 + 1, t2)) + "\">\n");
		    ps.print("<img align=middle border=0 width=25 height=32 src=doc:/demo/images/ftp/");
		    switch (key) {
		      default:
			ps.print("file");
			break;
		      case '0':
			ps.print("text");
			break;
		      case '1':
			ps.print("directory");
			break;
		      case 'g':
			ps.print("gif");
			break;
		    }
		    ps.print(".gif align=middle><dd>\n");
		    ps.print(s.substring(1, t1) + "</a>\n");
		}
		ps.print("</dl></body>\n");
		ps.close();
	    }
	} finally {
	    closeServer();
	    os.close();
	}
    }
}

/** An input stream that does nothing more than hold on to the NetworkClient
    that created it.  This is used when only the input stream is needed, and
    the network client needs to be closed when the input stream is closed. */
class GopherInputStream extends FilterInputStream {
    NetworkClient parent;

    GopherInputStream(NetworkClient o, InputStream fd) {
	super(fd);
	parent = o;
    }

    public void close() {
	parent.closeServer();
	super.close();
    }
}
