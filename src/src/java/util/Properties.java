/*
 * @(#)Properties.java	1.21 95/12/15 Arthur van Hoff
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

package java.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Persistent properties class. This class is basically a hashtable 
 * that can be saved/loaded from a stream. If a property is not found,
 * a property list containing defaults is searched. This allows
 * arbitrary nesting.
 *
 * @author Arthur van Hoff
 * @version 	1.21, 12/15/95
 */
public
class Properties extends Hashtable {
    protected Properties defaults;

    /**
     * Creates an empty property list.
     */
    public Properties() {
	this(null);
    }

    /**
     * Creates an empty property list with specified defaults.
     * @param defaults the defaults
     */
    public Properties(Properties defaults) {
	this.defaults = defaults;
    }

    /**
     * Loads properties from an InputStream.
     * @param in the input stream
     * @exception IOException Error when reading from input stream.
     */
    public synchronized void load(InputStream in) throws IOException {
	in = Runtime.getRuntime().getLocalizedInputStream(in);

	int ch = in.read();
	while (true) {
	    switch (ch) {
	      case -1:
		return;

	      case '#':
	      case '!':
		do {
		    ch = in.read();
		} while ((ch >= 0) && (ch != '\n') && (ch != '\r'));
		continue;

	      case '\n':
	      case '\r':
	      case ' ':
	      case '\t':
		ch = in.read();
		continue;
	    }

	    // Read the key
	    StringBuffer key = new StringBuffer();
	    while ((ch >= 0) && (ch != '=') && (ch != ':') && 
		   (ch != ' ') && (ch != '\t') && (ch != '\n') && (ch != '\r')) {
		key.append((char)ch);
		ch = in.read();
	    }
	    while ((ch == ' ') && (ch == '\t')) {
		ch = in.read();
	    }
	    if ((ch == '=') || (ch == ':')) {
		ch = in.read();
	    }
	    while ((ch == ' ') && (ch == '\t')) {
		ch = in.read();
	    }

	    // Read the value
	    StringBuffer val = new StringBuffer();
	    while ((ch >= 0) && (ch != '\n') && (ch != '\r')) {
		if (ch == '\\') {
		    switch (ch = in.read()) {
		      case '\n': 
			while (((ch = in.read()) == ' ') || (ch == '\t'));
			continue;
		      case 't': ch = '\t'; break;
		      case 'n': ch = '\n'; break;
		      case 'r': ch = '\r'; break;
		      case 'u': {
			while ((ch = in.read()) == 'u');
			int d = 0;
		      loop:
			for (int i = 0 ; i < 4 ; i++, ch = in.read()) {
			    switch (ch) {
			      case '0': case '1': case '2': case '3': case '4':
			      case '5': case '6': case '7': case '8': case '9':
				d = (d << 4) + ch - '0';
				break;
			      case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
				d = (d << 4) + 10 + ch - 'a';
				break;
			      case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
				d = (d << 4) + 10 + ch - 'A';
				break;
			      default:
				break loop;
			    }	
			}
			ch = d;
		      }
		    }
		}
		val.append((char)ch);
		ch = in.read();
	    }

	    //System.out.println(key + " = '" + val + "'");
	    put(key.toString(), val.toString());
	}
    }

    /**
     * Save properties to an OutputStream. Use the header as
     * a comment at the top of the file.
     */
    public synchronized void save(OutputStream out, String header) {
	OutputStream localOut = Runtime.getRuntime().getLocalizedOutputStream(out);
	PrintStream prnt = new PrintStream(localOut, false);
	boolean localize = localOut != out;

	if (header != null) {
	    prnt.write('#');
	    prnt.println(header);
	}
	prnt.write('#');
	prnt.println(new Date());

	for (Enumeration e = keys() ; e.hasMoreElements() ;) {
	    String key = (String)e.nextElement();
	    prnt.print(key);
	    prnt.write('=');

	    String val = (String)get(key);
	    int len = val.length();
	    boolean empty = false;

	    for (int i = 0 ; i < len ; i++) {
		int ch = val.charAt(i);

		switch (ch) {
		  case '\\': prnt.write('\\'); prnt.write('\\'); break;
		  case '\t': prnt.write('\\'); prnt.write('t'); break;
		  case '\n': prnt.write('\\'); prnt.write('n'); break;
		  case '\r': prnt.write('\\'); prnt.write('r'); break;

		  default:
		    if ((ch < ' ') || (ch >= 127) || (empty && (ch == ' '))) {
			if ((ch > 255) && localize) {
			    prnt.write(ch);
			} else {
			    prnt.write('\\');
			    prnt.write('u');
			    prnt.write((ch >> 12) & 0xF);
			    prnt.write((ch >>  8) & 0xF);
			    prnt.write((ch >>  4) & 0xF);
			    prnt.write((ch >>  0) & 0xF);
			}
		    } else {
			prnt.write(ch);
		    }
		}
		empty = false;
	    }
	    prnt.write('\n');
	}
    }

    /**
     * Gets a property with the specified key. If the key is not 
     * found in this property list, tries the defaults. This method 
     * returns null if the property is not found.
     * @param key the hashtable key
     */
    public String getProperty(String key) {
	String val = (String)super.get(key);
	return ((val == null) && (defaults != null)) ? defaults.getProperty(key) : val;
    }

    /**
     * Gets a property with the specified key and default. If the 
     * key is not found in this property list, tries the defaults. 
     * This method returns defaultValue if the property is not found.
     */
    public String getProperty(String key, String defaultValue) {
	String val = getProperty(key);
	return (val == null) ? defaultValue : val;
    }

    /**
     * Enumerates all the keys.
     */
    public Enumeration propertyNames() {
	Hashtable h = new Hashtable();
	enumerate(h);
	return h.keys();
    }

    /**
     * List properties, for debugging
     */
    public void list(PrintStream out) {
	out.println("-- listing properties --");
	Hashtable h = new Hashtable();
	enumerate(h);
	for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
	    String key = (String)e.nextElement();
	    String val = (String)h.get(key);
	    if (val.length() > 40) {
		val = val.substring(0, 37) + "...";
	    }
	    out.println(key + "=" + val);
	}
	
    }
    
    /**
     * Enumerates all key/value pairs in the specified hastable.
     * @param h the hashtable
     */
    private synchronized void enumerate(Hashtable h) {
	if (defaults != null) {
	    defaults.enumerate(h);
	}
	for (Enumeration e = keys() ; e.hasMoreElements() ;) {
	    String key = (String)e.nextElement();
	    h.put(key, get(key));
	}
    }
}
