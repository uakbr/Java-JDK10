/*
 * @(#)Win32MimeTable.java	1.4 95/05/12  
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

package net.www.html;

import java.io.*;

/** OS dependent class to find mime description files. */
class Win32MimeTable extends MimeTable {
    Win32MimeTable() {
	InputStream is = null;
	String slist[] = {
	    System.getenv("MAILCAP"),
	    System.getenv("HOME") + "\\.mailcap",
	    System.getenv("HOTJAVA_HOME") + "\\lib\\mailcap"
	};
	for (int i = 0; i < slist.length; i++) {
	    if (slist[i] != null) {
		try {
		    is = new FileInputStream(slist[i]);
		    break;
		} catch(Exception e) {
		}
	    }
	}
	if (is != null) {
	    ParseMailcap(is);
	    is.close();
	}
	add(new MimeEntry (URL.content_postscript, "loadtofile"));
	add(new MimeEntry (URL.content_dvi, "loadtofile"));
	add(new MimeEntry (URL.content_troff, "loadtofile"));
	add(new MimeEntry (URL.content_man, "loadtofile"));
	add(new MimeEntry (URL.content_me, "loadtofile"));
	add(new MimeEntry (URL.content_ms, "loadtofile"));
	add(new MimeEntry (URL.content_mpeg, "loadtofile"));
	add(new MimeEntry (URL.content_tar, "loadtofile"));
	add(new MimeEntry (URL.content_gtar, "loadtofile"));
	add(new MimeEntry (URL.content_hdf, "loadtofile"));
	add(new MimeEntry (URL.content_netcdf, "loadtofile"));
	add(new MimeEntry (URL.content_shar, "loadtofile"));
	add(new MimeEntry (URL.content_sv4cpio, "loadtofile"));
	add(new MimeEntry (URL.content_sv4crc, "loadtofile"));
	add(new MimeEntry (URL.content_zip, "loadtofile"));
	add(new MimeEntry (URL.content_bcpio, "loadtofile"));
	add(new MimeEntry (URL.content_cpio, "loadtofile"));
	add(new MimeEntry (URL.content_octet, "loadtofile"));
	add(new MimeEntry (URL.content_ustar, "loadtofile"));
	add(new MimeEntry ("audio/", "loadtofile"));
	add(new MimeEntry ("image/", "loadtofile"));
    }

    String TempTemplate() {
	return System.getenv("TMP") + "\\%s.wrt";
    }
}
