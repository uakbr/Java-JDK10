/*
 * @(#)WRTextItem.java	1.22 95/04/10 Jonathan Payne
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

import awt.*;
import net.www.html.*;
import java.io.*;

/**
 * An instance of class WRTextItem is created for each piece of text
 * in an html document.  WRTextItem handles following links to other
 * documents.
 * @version 1.22, 10 Apr 1995
 * @author Jonathan Payne
 */

public class WRTextItem extends TextDisplayItem {
    /** Whether or not to underline anchors. */
    public static boolean   underlineAnchors = true;

    /** This is the tagref that contains the href for this anchor. */
    TagRef  anchor;

    /** This is the default color, to reset to when we release the
	mouse button to follow this link. */
    Color normalColor;

    /** This is the url to go to if we're clicked on. */
    URL	    anchorUrl;

    /** This is the position in the document that this text
	widget refers to.  It is not possible to reach into
	the string object to get its offset, so we keep that
	information here. */
    int	    offset;
    int	    length;
    byte    text[];

    public String getText() {
	return new String(text, 0, offset, length);
    }

    public WRTextItem(String str) {
	text = new byte[length = str.length()];
	for (int i = 0 ; i < length ; i++) {
	    text[i] = (byte)str.charAt(i);
	}
    }

    public WRTextItem(DisplayItemWindow w, byte text[], int offset1, int offset2, TagRef a) {
	this.text = text;
	this.offset = offset1;
	this.length = offset2 - offset1;

	if ((anchor = a) != null) {
	    String  href = anchor.getAttribute("href");
	    if (href != null) {
		anchorUrl = new URL(((WRWindow) w).document().url(), href);
		setColorFromUrl(anchorUrl);
	    }
	}
	normalColor = fgColor;
    }

    boolean setColorFromUrl(URL url) {
	Color	c = fgColor;

	if (hotjava.history.seen(url)) {
	    setColor(hotjava.visitedAnchorColor);
	} else {
	    setColor(hotjava.anchorColor);
	}
	normalColor = fgColor;
	return c != fgColor;
    }

    public void setup(Font f, Color c, int x, int y, int w, int h) {
	if (normalColor == null) {
	    normalColor = c;
	}
	super.setup(f, normalColor, x, y, w, h);
    }

    public void trackStart(Event e) {
	if (anchorUrl != null) {
	    setColor(Color.red);
	    requestUpdate();
	}
    }

    public void trackEnter(Event e) {
	if (anchorUrl != null) {
	    WRWindow	parent = (WRWindow) this.parent;

	    parent.status("Go to " + anchorUrl.toExternalForm());
	}
    }

    public void trackExit(Event e) {
	if (anchorUrl != null) {
	    ((WRWindow) parent).status("");
	    if (fgColor != normalColor) {
		fgColor = normalColor;
		requestUpdate();
	    }
	}
    }

    public void trackStop(Event e) {
	if (anchorUrl != null) {
	    WRWindow    mw = (WRWindow) parent;
	    fgColor = normalColor;
	    requestUpdate();
	    if (anchorUrl != null) {
		hotjava.history.addUrl(anchorUrl);
		if (setColorFromUrl(anchorUrl)) {
		    requestUpdate();
		}
		mw.pushURL(anchorUrl);
	    }
	}
    }

    public void paint(awt.Window window, int x, int y) {
	if (!valid) {
	    validate();
	}
	if (metrics == null) {
	    metrics = window.getFontMetrics(font);
	}

	window.setFont(font);
	window.setForeground(fgColor);
	window.drawBytes(text, offset, length, x, y + metrics.ascent);

	if (underlineAnchors && (anchorUrl != null)) {
	    window.drawLine(x,
			    y + metrics.ascent + 2,
			    x + width, y + metrics.ascent + 2);
	}
    }

    public Dimension getPreferredSize() {
	if (metrics == null && parent != null) {
	    metrics = parent.getFontMetrics(font);
	}
	if (metrics == null) {
	    return new Dimension(0,0);
	} else {
	    // Note: The underscore is drawn at y+metrics.ascent+2, so
	    // height must be metrics.ascent+3.
	    int ht = metrics.height;
	    if (underlineAnchors && (anchorUrl != null)) {
		ht = Math.max(ht, metrics.ascent + 3);
	    }
	    return new Dimension(metrics.bytesWidth(text, offset, length), ht);
	}
    }
}

