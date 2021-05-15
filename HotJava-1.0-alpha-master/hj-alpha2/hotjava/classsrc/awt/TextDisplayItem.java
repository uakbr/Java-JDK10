/*
 * @(#)TextDisplayItem.java	1.18 95/02/23 Jonathan Payne
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
 * @version 1.18 23 Feb 1995
 * @author Jonathan Payne
 */
public class TextDisplayItem extends DisplayItem {
    static final boolean    debug = false;
    protected Font font;

    public abstract String getText();

    public void setFont(Font f) {
	font = f;
	invalidate();
    }

    public void setup(Font f, Color c, int x, int y, int w, int h) {
	font = f;
	fgColor = c;
	reshape(x, y, w, h);
    }

    public void validate() {
	Dimension s = getPreferredSize();
	resize(s.width, s.height);
	super.validate();
    }

    public Dimension getPreferredSize() {
	return (font == null) ? new Dimension(0,0) :
	    new Dimension(font.stringWidth(getText()), font.height);
    }
    public void moveBaseline(int y) {
	move(x, y - font.ascent);
    }

    public String toString() {
	return "[" + super.toString() + ", " + getText() + "]";
    }

    public void paint(awt.Window window, int x, int y) {
	if (!valid)
	    validate();

	if (debug) {
	    window.setForeground(Color.white);
	    window.fillRect(x, y, width, height);
	    window.setForeground(Color.red);
	    window.drawLine(x, y + font.ascent, x + width, y + font.ascent);
	    window.drawLine(x, y + font.height / 2, x + width,
			    y + font.height / 2);
	}
	window.setFont(font);
	window.setForeground(fgColor);
	window.drawString(getText(), x, y + font.ascent);
    }
}
