/*
 * @(#)MimeTable.java	1.2 95/01/31 James Gosling
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

package net.www.html;
import java.io.*;

class MimeTable {
    private MimeEntry root, last;

    void add(MimeEntry m) {
	if (m != null) {
	    if (last == null)
		root = m;
	    else
		last.next = m;
	    last = m;
	}
    }

    MimeEntry find(String type) {
	for (MimeEntry p = root; p != null; p = p.next)
	    if (p.matches(type))
		return p;
	return null;
    }

    void ParseMailcap(InputStream is) {
	try {
	    char b[] = new char[200];
	    int i = 0;
	    int c;
	    int slot = 0;
	    int eqpos = -1;
	    String MimeType = null;
	    String Command = null;
	    String TempName = null;
	    while ((c = is.read()) >= 0) {
		if (c == ';' || c == '\n') {
		    while (i > 0 && b[i - 1] < ' ')
			i--;
		    if (slot <= 1) {
			String s;
			if (slot == 0 && i > 0 && b[i - 1] == '*')
			    i--;/* '*' is recognized by the trailing slash */
			if (i == 0)
			    s = "";
			else
			    s = String.copyValueOf(b, 0, i);
			if (slot == 0)
			    MimeType = s;
			else
			    Command = s;
		    } else if (i > 0) {
			String key;
			String value;
			if (eqpos >= 0) {
			    key = String.valueOf(b, 0, eqpos);
			    value = String.copyValueOf(b, eqpos + 1, i - eqpos - 1);
			} else {
			    key = String.valueOf(b, 0, i);
			    value = null;
			}
			if (key.equalsIgnoreCase("nametemplate"))
			    TempName = value;
		    }
		    slot++;
		    i = 0;
		    if (c == '\n') {
			if (slot >= 2)
			    add(new MimeEntry(MimeType.toLowerCase(), Command, TempName));
			slot = 0;
		    }
		} else if (c == '#' && i == 0) {
		    while ((c = is.read()) >= 0 && c != '\n');
		} else {
		    if (c == '\\') {
			c = is.read();
			if (c == '\n')	/* line  continuation */
			    continue;
		    }
		    if ((c > ' ' || i > 0) && i < b.length) {
			if (c == '=')
			    eqpos = i;
			b[i++] = (char) c;
		    }
		}
	    }
	} catch(Exception e) {
	}
    }

    abstract String TempTemplate();
}
