/*
 * @(#)InlineButton.java	1.5 95/03/14 James Gosling, Jonathan Payne
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
import browser.Applet;
import browser.WRWindow;
import browser.DocumentManager;
import net.www.html.URL;
import awt.*;


/** A simple inline button applet for use by the news reader.  The "label"
    attribute defines the text that will appear in the label, "href" defines
    what to do when the button is clicked.  Normally it is a URL.
    InlineButton acts almost the same as an anchor, except that it looks
    different */
public
class InlineButton extends Applet {
    String label;
    String target;
    static Font labelFont;
    boolean up = true;
    int strX, strY;

    public void init() {
	if (labelFont == null)
	    labelFont = getFont("Helvetica", Font.BOLD, 11);
	font = labelFont;
	label = getAttribute("label");
	int labelWidth = labelFont.stringWidth(label);
	target = getAttribute("href");
	int minheight = labelFont.height + 4;
	int minwidth = labelWidth + 4;
	if (width > minwidth)
	    minwidth = width;
	else
	    minwidth = (minwidth + 15) & ~15;
	if (height > minheight)
	    minheight = height;
	resize(minwidth, minheight);
	strX = (width - labelFont.stringWidth(label)) / 2;
	strY = (height - labelFont.height) / 2 + labelFont.ascent;
    }

    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
	g.setForeground(up ? Color.white : Color.darkGray);
	g.drawLine(0, 1, 0, height - 2);	// left
	g.drawLine(1, 0, width - 2, 0);	// top
	g.setForeground(up ? Color.darkGray : Color.white);
	g.drawLine(1, height - 1, width - 2, height - 1);	// bottom
	g.drawLine(width - 1, 1, width - 1, height - 2);	// right

	g.setForeground(up ? Color.black : Color.red);
	g.drawString(label, strX, strY);
    }

    public void mouseDown(int x, int y) {
	up = false;
	repaint();
    }

    private void status(String s) {
	try {
	    ((WRWindow) item.parent).status(s);
	} catch(Exception e);
    }

    private void gotoURL(URL u) {
	try {
	    status("Going to " + u.toExternalForm());
	    ((WRWindow) item.parent).pushURL(u);
	} catch(Exception e) {
	    status("Error going to " + u.toExternalForm() + "(" + e + ")");
	}
    }

    private void gotoArticle(int delta) {
	String an = documentURL.file;
	try {
	    int lSlash = an.lastIndexOf('/');
	    int articlenum = Integer.parseInt(an.substring(lSlash + 1)) + delta;
	    gotoURL(new URL(documentURL, articlenum + ""));
	} catch(Exception e) {
	    status("Error going to article");
	}
    }

    String groupName() {
	if (!documentURL.protocol.equals("news")) {
	    return "";
	}
	String gn = documentURL.file;
	int start = 0;
	while (start < gn.length() && gn.charAt(start) == '/')
	    start++;
	int nsl = gn.indexOf('/', start);
	return nsl < 0 ? gn.substring(start) : gn.substring(start, nsl);
    }

    int articleNumber() {
	if (!documentURL.protocol.equals("news"))
	    return -1;
	String gn = documentURL.file;
	int apos = gn.lastIndexOf('/');
	if (apos > 0) {
	    try {
		return Integer.parseInt(gn.substring(apos + 1));
	    } catch(Exception e) {
	    }
	}
	return -1;
    }

    public void mouseUp(int x, int y) {
	if (!up) {
	    up = true;
	    repaint();
	    if ("saverc".equals(target)) {
		status("Saving .newsrc");
		try {
		    if (newsFetcher.Newsgroups != null && newsFetcher.newsrcName != null)
			Newsgroup.writeNewsrcFile(newsFetcher.newsrcName, newsFetcher.Newsgroups);
		    status("Saved .newsrc");
		} catch(Exception e) {
		    status("Error attempting to save .newsrc");
		}
	    } else if ("catchup".equals(target)) {
		try {
		    String group = groupName();
		    Newsgroup ng = newsFetcher.findGroup(group);
		    ng.articles = null;
		} catch(Exception e) {
		}
		gotoURL(new URL(null, "news:"));
	    } else if ("unsubscribe".equals(target)) {
		try {
		    newsFetcher.findGroup(groupName()).subscribed = false;
		} catch(Exception e) {
		}
		gotoURL(new URL(null, "news:"));
	    } else if ("subscribe".equals(target)) {
		try {
		    newsFetcher.findGroup(groupName()).subscribed = true;
		} catch(Exception e) {
		}
	    } else if ("post".equals(target)) {
		String gn = groupName();
		try {
		    if (gn.length() <= 0)
			ArticlePoster.PostTo();
		    else if (gn.indexOf('@') > 0)
			ArticlePoster.PostTo(newsFetcher.news.getArticle(gn));
		    else {
			int an = articleNumber();
			if (an > 0) {
			    newsFetcher.news.setGroup(gn);
			    ArticlePoster.PostTo(newsFetcher.news.getArticle(an));
			} else
			    ArticlePoster.PostTo(gn);
		    }
		} catch(Exception e) {
		    status("Can't post -- " + e);
		}
	    } else if ("mail".equals(target)) {
		String gn = groupName();
		try {
		    if (gn.length() <= 0)
			ArticlePoster.MailTo();
		    else if (gn.indexOf('@') > 0)
			ArticlePoster.MailTo(newsFetcher.news.getArticle(gn));
		    else {
			int an = articleNumber();
			if (an > 0) {
			    newsFetcher.news.setGroup(gn);
			    ArticlePoster.MailTo(newsFetcher.news.getArticle(an));
			} else
			    ArticlePoster.MailTo();
		    }
		} catch(Exception e) {
		    status("Can't send mail -- " + e);
		}
	    } else if ("rescan".equals(target)) {
		boolean AnyChanged = false;
		Newsgroup ns[] = newsFetcher.Newsgroups;
		int limit = ns.length;
		for (int i = 0; i < limit; i++) {
		    try {
			Newsgroup p = ns[i];
			if (p == null || !p.subscribed)
			    continue;
			NewsgroupInfo gi = newsFetcher.news.getGroup(p.group.name);
			if (gi.lastArticle > p.group.lastArticle) {
			    p.articles.add(p.group.lastArticle + 1, gi.lastArticle);
			    p.group.lastArticle = gi.lastArticle;
			    AnyChanged = true;
			    DocumentManager.unCacheDocument(new URL(null, "news:///" + p.group.name));
			}
		    } catch(Exception e) {
		    }
		}
		if (AnyChanged)
		    DocumentManager.unCacheDocument(new URL(null, "news:///"));
		gotoURL(documentURL);
	    } else if ("catchup".equals(target)) {
		try {
		    newsFetcher.findGroup(groupName()).articles = null;
		    URL u = new URL(null, "news:");
		    DocumentManager.unCacheDocument(u);
		    gotoURL(u);
		} catch(Exception e);
	    } else if ("regen".equals(target)) {
		try {
		    DocumentManager.unCacheDocument(documentURL);
		    gotoURL(documentURL);
		} catch(Exception e) {
		}
	    } else if ("next".equals(target)) {
		gotoArticle(1);
	    } else if ("prev".equals(target)) {
		gotoArticle(-1);
	    } else {
		gotoURL(new URL(documentURL, target));
	    }
	}
    }
    public void mouseExit() {
	if (!up) {
	    up = true;
	    repaint();
	}
    }
}
