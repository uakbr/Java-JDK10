/*
 * @(#)ProgressDialog.java	1.6 95/05/13 Chris Warth
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

import awt.*;
import net.www.html.MeteredStream;
import net.www.html.URL;
import net.ProgressData; 

public class ProgressDialog extends Frame {
    static ProgressWindow tw = null;
    static String titleStr = hotjava.programName+" Progress";

    public ProgressDialog(Frame parent) {
	super(parent.wServer, true, false, parent, 433, 215, Color.lightGray);

	setTitle(titleStr);
	tw = new ProgressWindow(this, "Center", background, 300, 300);
    }

    /*
     * For testing purposes only.  It is private so it is only
     * accessible from the demo method in this class.
     */
    private ProgressDialog(WServer server) {
	super(server, true, false, null, 433, 215, Color.lightGray);

	setTitle(titleStr);
	tw = new ProgressWindow(this, "Center", background, 300, 300);
    }


    public void demo() {
	URL url = new URL(null, "http://somplace.org/file.html");
	URL o2 = new URL(null, "http://somplaceelse.org/otherfile.html");

	URL.classInit();
	ProgressData.pdata.register(url);
	Thread.currentThread().sleep(5000);
	ProgressData.pdata.register(o2);
	Thread.currentThread().sleep(5000);
	ProgressData.pdata.unregister(o2);
	Thread.currentThread().sleep(7000);
	ProgressData.pdata.connected(url);

	for (int i = 10; i < 1000; i += 50) {
	    ProgressData.pdata.update(url, i, 1000);
	    Thread.currentThread().sleep(200);
	}
	ProgressData.pdata.update(url,1000, 1000);
	ProgressData.pdata.unregister(url);
    }

    /*
	This class can be tested independently of the rest of the browser.
	just say 

	    java -cs browser.ProgressDialog

    */
    public static void main(String args[]) {
	WServer server;
	ProgressDialog tDialog = null;

	server = new WServer();
	server.start();
	tDialog = new ProgressDialog(server);
	tDialog.map();
	tDialog.resize();
	tDialog.demo();
    }

}
