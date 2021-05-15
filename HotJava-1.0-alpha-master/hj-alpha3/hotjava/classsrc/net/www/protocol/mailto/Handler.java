/*
 * @(#)Handler.java	1.9 95/04/12 James Gosling, Jonathan Payne
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

/*-
 *	mailto stream opener
 */

package net.www.protocol.mailto;

import java.io.*;
import net.www.html.*;
import net.www.protocol.news.ArticlePoster;
import net.smtp.SmtpClient;

/** open an nntp input stream given a URL */
class Handler extends URLStreamHandler implements Runnable {
    private String decodePercent(String s) {
	if (s==null || s.indexOf('%') < 0)
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

    public InputStream openStream(URL u) {
	    String dest = u.file;
	    String subj = "";
	    int lastsl = dest.lastIndexOf('/');
	    if (lastsl >= 0) {
		int st = dest.charAt(0) == '/' ? 1 : 0;
		if (lastsl > st)
		    subj = dest.substring(st, lastsl);
		dest = dest.substring(lastsl + 1);
	    }
	    if (u.postData != null) {
		ArticlePoster.MailTo("Posted form",
				     decodePercent(dest),				     
				     u.postData);
	    }
	    else
		ArticlePoster.MailTo(decodePercent(subj), decodePercent(dest));
	return null;
    }
}
