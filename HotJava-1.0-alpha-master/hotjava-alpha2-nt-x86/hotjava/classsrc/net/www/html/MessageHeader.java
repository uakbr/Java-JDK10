/*
 * @(#)MessageHeader.java	1.4 95/03/15 James Gosling, Jonathan Payne
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
 *	news stream opener
 */

package net.www.html;

import java.io.InputStream;
import java.io.PrintStream;

/** An RFC 844 or MIME message header.  Includes methods
    for parsing headers from incoming streams, fetching
    values, setting values, and printing headers.
    Key values of null are legal: they indicate lines in
    the header that don't have a valid key, but do have
    a value (this isn't legal according to the standard,
    but lines like this are everywhere). */
public
class MessageHeader {
    private String keys[];
    private String values[];
    private int nkeys;

    public MessageHeader () {
    }
    public MessageHeader (InputStream is) {
	parseHeader(is);
    }

    /** Find the value that corresponds to this key.
	It finds only the first occurrance of the key.
	Returns null if not found. */
    public String findValue(String k) {
	if (k == null) {
	    for (int i = nkeys; --i >= 0;)
		if (keys[i] == null)
		    return values[i];
	} else
	    for (int i = nkeys; --i >= 0;)
		if (k.equalsIgnoreCase(keys[i]))
		    return values[i];
	return null;
    }

    /** Find the next value that corresponds to this key.
     *	It finds the first value that follows v. To iterate
     *	over all the values of a key use:
     *	<pre>
     *		for(String v=h.findValue(k); v!=null; v=h.findNextValue(k, v)) {
     *		    ...
     *		}
     *	</pre>
     */
    public String findNextValue(String k, String v) {
	boolean foundV = false;
	if (k == null) {
	    for (int i = nkeys; --i >= 0;)
		if (keys[i] == null)
		    if (foundV)
			return values[i];
		    else if (values[i] == v)
			foundV = true;
	} else
	    for (int i = nkeys; --i >= 0;)
		if (k.equalsIgnoreCase(keys[i]))
		    if (foundV)
			return values[i];
		    else if (values[i] == v)
			foundV = true;
	return null;
    }

    /** Prints the key-value pairs represented by this
	header.  Also prints the RFC required blank line
	at the end. Omits pairs with a null key. */
    public void print(PrintStream p) {
	for (int i = 0; i < nkeys; i++)
	    if (keys[i] != null)
		p.print(keys[i] + ": " + values[i] + "\n");
	p.print("\n");
    }

    /** Adds a key value pair to the end of the
	header.  Duplicates are allowed */
    public void add(String k, String v) {
	if (keys == null || nkeys >= keys.length) {
	    String nk[] = new String[nkeys + 4];
	    String nv[] = new String[nkeys + 4];
	    if (keys != null)
		System.arraycopy(keys, 0, nk, 0, nkeys);
	    if (values != null)
		System.arraycopy(values, 0, nv, 0, nkeys);
	    keys = nk;
	    values = nv;
	}
	keys[nkeys] = k;
	values[nkeys] = v;
	nkeys++;
    }

    /** Sets the value of a key.  If the key already
	exists in the header, it's value will be
	changed.  Otherwise a new key/value pair will
	be added to the end of the header. */
    public void set(String k, String v) {
	for (int i = nkeys; --i >= 0;)
	    if (k.equalsIgnoreCase(keys[i])) {
		values[i] = v;
		return;
	    }
	add(k, v);
    }

    /** Convert a message-id string to canonical form (strips off
	leading and trailing <>s) */
    public String canonicalID(String id) {
	if (id == null)
	    return "";
	int st = 0;
	int len = id.length();
	boolean substr = false;
	int c;
	while (st < len && ((c = id.charAt(st)) == '<' ||
			    c <= ' '))
	    st++, substr = true;
	while (st < len && ((c = id.charAt(len - 1)) == '>' ||
			    c <= ' '))
	    len--, substr = true;
	return substr ? id.substring(st, len) : id;
    }

    /** Parse a MIME header from an input stream. */
    public void parseHeader(InputStream is) {
	    nkeys = 0;
	    if (is == null)
		return;
	    char s[] = new char[10];
	    int firstc = is.read();
	    while (firstc != '\n' && firstc != '\r' && firstc >= 0) {
		int len = 0;
		int keyend = -1;
		int c;
		boolean inKey = firstc > ' ';
		s[len++] = (char) firstc;
	parseloop:
		while ((c = is.read()) >= 0) {
		    switch (c) {
		      case ':':
			if (inKey && len > 0)
			    keyend = len;
			inKey = false;
			break;
		      case '\t':
			c = ' ';
		      case ' ':
			inKey = false;
			break;
		      case '\r':
		      case '\n':
			firstc = is.read();
			if (c == '\r' && firstc == '\n') {
			    firstc = is.read();
			    if (firstc == '\r')
				firstc = is.read();
			}
			if (firstc == '\n' || firstc == '\r' || firstc > ' ')
			    break parseloop;
			/* continuation */
			c = ' ';
			break;
		    }
		    if (len >= s.length) {
			char ns[] = new char[s.length * 2];
			System.arraycopy(s, 0, ns, 0, len);
			s = ns;
		    }
		    s[len++] = (char) c;
		}
		while (len > 0 && s[len - 1] <= ' ')
		    len--;
		String k;
		if (keyend <= 0) {
		    k = null;
		    keyend = 0;
		} else {
		    k = String.copyValueOf(s, 0, keyend);
		    if (keyend < len && s[keyend] == ':')
			keyend++;
		    while (keyend < len && s[keyend] <= ' ')
			keyend++;
		}
		String v;
		if (keyend >= len)
		    v = new String();
		else
		    v = String.copyValueOf(s, keyend, len - keyend);
		add(k, v);
	    }
    }
}
