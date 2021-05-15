/*
 * @(#)URLHistory.java	1.5 95/03/20 Jonathan Payne
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

package browser;

import net.www.html.URL;
import java.util.Hashtable;
import java.util.Enumeration;
import net.www.html.MalformedURLException;
import java.io.*;

public class URLHistory {
    /** Hashtable of urls parsed from history file, plus new ones
	we have visited. */
    Hashtable	urlTable = new Hashtable();

    public URLHistory() {
    }

    final String makeString(char data[], int length) {
	char	chars[] = new char[length];

	System.arraycopy(data, 0, chars, 0, length);
	return new String(chars);
    }

    public boolean seen(URL u) {
	return urlTable.get(u.toExternalForm()) != null;
    }

    public void addUrl(URL u) {
	String	eform = u.toExternalForm();

	if (urlTable.get(eform) == null) {
	    urlTable.put(eform, u);
	    if (outputStream != null) {
		outputStream.println(eform + "\t0");
		outputStream.flush();
	    }
	}
    }

    /** Format of stream is

	url[ \t]time

	It's a ' ' when parsing mosaic global history and a Tab
	when parsing netscapes.  HotJava's format uses a Tab,
	and currently sticks in 0 for the last time visited.  That's
	because there's no way to get the current time in hotjava. */
    public void parseStream(InputStream is) {
	/* just in case it's not buffered */
	is = new BufferedInputStream(is);

	DataInputStream	dis = new DataInputStream(is);
	char	buffer[] = new char[256];

	dis.readLine();	/* skip the file format line */
	int	lineNumber = 1;
	int	index = 0;
	int	separatorIndex = -1;
	int	c = -1;

	do {
	    try {
inner:
		while (true) {
		    switch (c = is.read()) {
		    case '\n':
			lineNumber += 1;
			/* falls into ... */

		    case -1:
			break inner;

		    case ' ':
		    case '\t':
			if (separatorIndex == -1) {
			    separatorIndex = index;
			}
			/* falls into ... */

		    default:
			buffer[index] = (char) c;
			/* Increment after array index in case index is
			   out of bounds.  That way we can recover below. */
			index += 1;
		    }
		}
	    } catch (ArrayIndexOutOfBoundsException e) {
		if (index < buffer.length) {
		    throw e;
		}
		char	newbuf[] = new char[(int)(buffer.length * 1.5)];
		System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
		buffer = newbuf;
		buffer[index++] = (char) c;
		continue;
	    }

	    if (separatorIndex != -1) {
		try {
		    String  s = makeString(buffer, separatorIndex);

		    urlTable.put(s, s);
		} catch (MalformedURLException e) {
		    System.out.println("URL history format error, line "
				       + lineNumber + ": "
				       + makeString(buffer, separatorIndex));
		}
	    }
	    separatorIndex = -1;
	    index = 0;
	} while (c != -1);
    }

    public void writeHistoryFile(String filename) {
	FileOutputStream    fos = new FileOutputStream(filename);
	PrintStream	    os;

	os = new PrintStream(new BufferedOutputStream(fos));
	writeHistoryStream(os);
    }

    public void writeHistoryStream(PrintStream os) {
	os.println("hotjava-global-history-1");
	Enumeration e = urlTable.elements();
	while (e.hasMoreElements()) {
	    String  key = (String) e.nextElement();

	    os.print(key);
	    os.println("\t0");
	}
    }

    PrintStream	outputStream;

    public void openOutputStream(String filename) {
	try {
	    // REMIND: This should open for appending!
	    FileOutputStream    os = new FileOutputStream(filename);

	    outputStream = new PrintStream(new BufferedOutputStream(os));
	} catch (Exception e) {
	    System.err.println("Warning: not saving global history\nReason is: " + e);
	}
    }

    static public void main(String args[]) {
	URLHistory  h = new URLHistory();

	h.parseStream(new URL(null, args[0]).openStream());
	System.out.println("Parsed " + h.urlTable.size() + " entries from " + args[0]);
    }
}
