/*
 * @(#)TextDisplayItem.java	1.19 95/04/10 Jonathan Payne
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
package awt;

import java.util.*;

/**
 * A DisplayItem that displays text that can have font and color
 * properties associated with it.
 *
 * @version 1.19 10 Apr 1995
 * @author Jonathan Payne
 */
public class TextDisplayItem extends DisplayItem {
    static final boolean    debug = false;
    protected Font font;
    protected FontMetrics metrics;

    public abstract String getText();

    public void setFont(Font f) {
	font = f;
	metrics = null;
	invalidate();
    }

    public void setup(Font f, Color c, int x, int y, int w, int h) {
	font = f;
	metrics = null;
	fgColor = c;
	reshape(x, y, w, h);
    }

    public void validate() {
	Dimension s = getPreferredSize();
	resize(s.width, s.height);
	super.validate();
    }

    public Dimension getPreferredSize() {
	if (metrics == null && parent != null) {
	    metrics = parent.getFontMetrics(font);
	}
	return (metrics == null) ? new Dimension(0,0) :
	    new Dimension(metrics.stringWidth(getText()), metrics.height);
    }
    public void moveBaseline(int y) {
	if (metrics == null && parent != null) {
	    metrics = parent.getFontMetrics(font);
	}
	move(x, y - metrics.ascent);
    }

    public String toString() {
	return "[" + super.toString() + ", " + getText() + "]";
    }

    public void paint(awt.Window window, int x, int y) {
	if (!valid)
	    validate();
	if (metrics == null) {
	    metrics = window.getFontMetrics(font);
	}

	if (debug) {
	    window.setForeground(Color.white);
	    window.fillRect(x, y, width, height);
	    window.setForeground(Color.red);
	    window.drawLine(x, y + metrics.ascent,
			    x + width, y + metrics.ascent);
	    window.drawLine(x, y + metrics.height / 2,
			    x + width, y + metrics.height / 2);
	}
	window.setFont(font);
	window.setForeground(fgColor);
	window.drawString(getText(), x, y + metrics.ascent);
    }
}
