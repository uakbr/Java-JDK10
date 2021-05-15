/*
 * @(#)TotalUnReadIndicator.java	1.6 95/04/10 James Gosling
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

class TotalUnReadIndicator extends Applet {
    int anum;
    Newsgroup group;
    Color fg;
    public void init() {
	group = newsFetcher.findGroup(getAttribute("group"));
	resize(50, 10);
    }
    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	int n;

	if (group == null || group.articles == null)
	    n = 0;
	else
	    n = group.articles.size();
	if (n > 0) {
	    String s = String.valueOf(n);
	    FontMetrics fm = g.getFontMetrics(font);
	    g.drawString(s, width - fm.stringWidth(s) - 3, height);
	}
    }
}
