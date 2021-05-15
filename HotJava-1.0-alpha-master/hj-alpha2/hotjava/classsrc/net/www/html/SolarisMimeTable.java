/*
 * @(#)SolarisMimeTable.java	1.7 95/03/20  
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

/** OS dependent class to find mime description files */
class SolarisMimeTable extends MimeTable {
    SolarisMimeTable() {
	InputStream is = null;
	String slist[] = {
	    System.getenv("MAILCAP"),
	    System.getenv("HOME") + "/.mailcap",
	    "/etc/mailcap",
	    "/usr/etc/mailcap",
	    "/usr/local/etc/mailcap",
	    System.getenv("HOTJAVA_HOME") + "/lib/mailcap"
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
	add(new MimeEntry (URL.content_postscript, "imagetool %s; rm %s"));
	add(new MimeEntry (URL.content_dvi, "xdvi %s"));
	add(new MimeEntry (URL.content_troff, "xterm -title troff -e sh -c \"nroff %s | col | more -w ; rm %s\""));
	add(new MimeEntry (URL.content_man, "xterm -title troff -e sh -c \"nroff -man %s | col | more -w ; rm %s\""));
	add(new MimeEntry (URL.content_me, "xterm -title troff -e sh -c \"nroff -me %s | col | more -w ; rm %s\""));
	add(new MimeEntry (URL.content_ms, "xterm -title troff -e sh -c \"nroff -ms %s | col | more -w ; rm %s\""));
	add(new MimeEntry (URL.content_mpeg, "mpeg_play %s; rm %s"));
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
	add(new MimeEntry ("audio/", "audiotool %s"));
	add(new MimeEntry ("image/", "xv %s; rm %s"));
    }

    String TempTemplate() {
	return "/tmp/%s.wrt";
    }
}
