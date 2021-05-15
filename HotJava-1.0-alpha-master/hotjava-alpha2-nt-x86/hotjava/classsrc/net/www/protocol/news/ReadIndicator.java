/*
 * @(#)ReadIndicator.java	1.4 95/03/14 James Gosling
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

package net.www.protocol.news;

import java.io.*;
import java.util.*;
import net.nntp.*;
import net.smtp.SmtpClient;
import browser.Applet;
import browser.WRWindow;
import browser.DocumentManager;
import net.TelnetInputStream;
import net.UnknownHostException;
import awt.*;

class ReadIndicator extends Applet {
    int anum;
    Newsgroup group;
    Color fg;
    public void init() {
	group = newsFetcher.findGroup(getAttribute("group"));
	anum = Integer.parseInt(getAttribute("article"));
	resize(10, 10);
    }
    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	g.drawLine(0, 4, 0, 9);
	g.drawLine(6, 4, 6, 9);
	g.drawLine(0, 4, 6, 4);
	g.drawLine(0, 9, 6, 9);
	if (group != null && !group.contains(anum)) {
	    g.setForeground(Color.red);
	    g.drawLine(0, 5, 3, 9);
	    g.drawLine(3, 9, 9, 0);
	}
    }
    public void mouseDown(int x, int y) {
	if (group != null) {
	    if (group.contains(anum))
		group.markAsRead(anum);
	    else
		group.markAsUnread(anum);
	    repaint();
	}
    }
}
