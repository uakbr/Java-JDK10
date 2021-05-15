/*
 * @(#)PublicMapping.java	1.1 95/04/23  
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

package html;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.Hashtable;
import net.www.html.URL;

/**
 * A class for mapping public identifiers to URLs.
 *
 * @version 	1.1, 23 Apr 1995
 * @author Arthur van Hoff
 */
public class PublicMapping {
    String dir;
    Hashtable tab = new Hashtable();

    /**
     * Create a mapping given a base URL.
     */
    public PublicMapping(String dir) {
	this.dir = dir;
	load(new FileInputStream(dir + File.separator + "public.map"));
    }

    /**
     * Load a set of mappings from a stream.
     */
    public void load(InputStream in) {
	DataInputStream data = new DataInputStream(in);

	for (String ln = data.readLine() ; ln != null ; ln = data.readLine()) {
	    if (ln.startsWith("PUBLIC")) {
		int len = ln.length();
		int i = 6;
		while ((i < len) && (ln.charAt(i) != '"')) i++;
		int j = ++i;
		while ((j < len) && (ln.charAt(j) != '"')) j++;
		String id = ln.substring(i, j);
		i = ++j;
		while ((i < len) && ((ln.charAt(i) == ' ') || (ln.charAt(i) == '\t'))) i++;
		j = i + 1;
		while ((j < len) && (ln.charAt(j) != ' ') && (ln.charAt(j) != '\t')) j++;
		String where = ln.substring(i, j);
		put(id, where);
	    }
	}
	data.close();
    }

    /**
     * Add a mapping from a public identifier to a URL.
     */
    public void put(String id, String where) {
	tab.put(id, where);
	if (where.endsWith(".dtd")) {
	    tab.put(where.substring(where.lastIndexOf(File.separatorChar) + 1, where.length() - 4), where);
	}
    }

    /**
     * Map a public identifier to a URL. You can also map
     * a DTD file name (without the .dtd) to a URL.
     */
    public InputStream get(String id) {
	return new BufferedInputStream(new FileInputStream(dir + File.separator + tab.get(id)));
    }
}
