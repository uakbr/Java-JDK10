/*
 * @(#)Properties.java	1.6 95/03/14 Arthur van Hoff
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

import java.io.*;
import java.util.*;

/**
 * Persistent properties class. Basically a hashtable that can
 * be saved/loaded from a stream.
 *
 * @see AppletDisplayItem
 * @author Chris Warth
 * @author Arthur van Hoff
 * @version 	1.6, 14 Mar 1995
 */

public
class Properties extends Hashtable {
    File source;
    boolean changed;
    
    public Properties(String source) {
	this(new File(source));
    }
    public Properties(File source) {
	this.source = source;
	if (source.canRead()) {
	    load();
	}
    }

    public boolean load() {
	try {
	    InputStream in = new BufferedInputStream(
		new FileInputStream(source.getPath()));
	    load(in);
	    in.close();
	    return true;
	} catch (Exception ex) {
	    return false;
	}
    }
    public void load(InputStream in) {
	DataInputStream data = new DataInputStream(in);

	String line;
	while ((line = data.readLine()) != null) {
	    if (line.startsWith("#")) {
		continue;
	    }
	    int index = line.indexOf('=');
	    if (index > 0) {
		put(line.substring(0, index), line.substring(index + 1));
	    }
	}
    }

    public void save(OutputStream out) {
	PrintStream prnt = new PrintStream(out);
	prnt.println("#" + new Date());
	for (Enumeration e = keys() ; e.hasMoreElements() ;) {
	    Object key = e.nextElement();
	    prnt.println(key + "=" + get(key));
	}
    }
    public boolean save() {
	if (changed) {
	    try {
		String parent = source.getParent();

		if (!source.exists()) {
		    new File(parent).mkdirs();
		}
		OutputStream out = new BufferedOutputStream(
		    new FileOutputStream(source.getPath()));
		save(out);
		out.flush();
		out.close();
		return true;
	    } catch (Exception ex) {
		return false;
	    }
	}
	return true;
    }

    public Object remove(Object key) {
	changed = true;
	return super.remove(key);
    }
    public Object put(Object key, Object value) {
	changed = true;
	return super.put(key, value);
    }
}
