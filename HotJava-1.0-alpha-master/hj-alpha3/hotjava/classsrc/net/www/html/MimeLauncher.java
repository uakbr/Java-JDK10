/*
 * @(#)MimeLauncher.java	1.3 95/02/07  James Gosling
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

class MimeLauncher extends Thread {
    MimeEntry m;
    InputStream is;
    URL u;
    String GenericTempTemplate;

    MimeLauncher (MimeEntry M, InputStream IS, URL U, String gtt,
		  String name) {
        super(name);
	m = M;
	is = IS;
	u = U;
	GenericTempTemplate = gtt;
    }

    public void run() {
	String c = m.command;
	int inx = 0;
	boolean substituted = false;
	String ofn = m.TempNameTemplate;
	if (ofn == null)
	    ofn = GenericTempTemplate;
	while ((inx = ofn.indexOf("%s")) >= 0)
	    ofn = ofn.substring(0, inx) + System.currentTime() + ofn.substring(inx + 2);
	OutputStream os = new FileOutputStream(ofn);
	byte buf[] = new byte[2048];
	int i;
	while ((i = is.read(buf)) >= 0)
	    os.write(buf, 0, i);
	is.close();
	os.close();
	while ((inx = c.indexOf("%t")) >= 0)
	    c = c.substring(0, inx) + u.content_type + c.substring(inx + 2);
	while ((inx = c.indexOf("%s")) >= 0) {
	    c = c.substring(0, inx) + ofn + c.substring(inx + 2);
	    substituted = true;
	}
	if (!substituted)
	    c = c + " <" + ofn;
	System.exec(c);
    }
}

