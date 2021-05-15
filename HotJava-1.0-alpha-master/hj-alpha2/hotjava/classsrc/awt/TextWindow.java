/*
 * @(#)TextWindow.java	1.25 95/02/23 Jonathan Payne
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
import java.io.*;

/**
 * A class that displays formatted text in a window. Also has a
 * SmoothScroller associated with it.
 *
 * @see Formatter
 * @see Text
 * @see SmoothScroller
 * @version 1.25 23 Feb 1995
 * @author Jonathan Payne
 */
public class TextWindow extends DisplayItemWindow {
    Vector	lineYValues = new Vector();
    Vector	lineItemIndexes = new Vector();

    public Formatter	formatter;  /* formatter for this text */

    public TextWindow(Window parent, String client) {
	super(parent, client);
    }

    public TextWindow(Frame parent, String client) {
	super(parent, client);
    }

    public synchronized void setFormatter(Formatter f) {
	formatter = f;
	invalidate();
    }

    public void validate() {
	if (!valid) {
	    layoutDocument();
	    super.validate();
	}
    }

    public void clearItems() {
	super.clearItems();
	lineYValues.setSize(0);
	lineItemIndexes.setSize(0);
    }

    protected void layoutDocument() {
	if (formatter == null) {
	    //setFormatter(new Formatter(this));
	    return;
	}
	clearItems();
	paintRange(0, height);
	formatter.layout();
    }

    public void startNewLine(int n) {
	startNewLine(n, formatter.getYCoordinate());
    }

    public void startNewLine(int n, int y) {
	int size = lineYValues.size();
	if (size > 0 && ((Integer) (lineYValues.elementAt(size - 1))).intValue() == y)
	    return;
	lineYValues.addElement(new Integer(y));
	lineItemIndexes.addElement(new Integer(n));
    }

    /** Return the index of the first item in a line whose
	y value is < the specified y. */
    int findItemBefore(int y) {
	int lo = 0;
	int hi = lineYValues.size();

	while (true) {
	    int diff = hi - lo;
	    int mid = (lo + hi) / 2;

	    if (diff < 2) {
		return lo;
	    }
	    Integer i = (Integer) lineYValues.elementAt(mid);

	    if (i.intValue() < y) {
		lo = mid;
	    } else {
		hi = mid;
	    }
	} 
    }

    void paintRange(int y0, int y1) {
	int cnt;
	int i, j;
	int line0, line1;

	if (y1 < 0 || y0 > height)
	    return;
	setForeground(background);
	fillRect(0, y0, width, y1 - y0);

	if (count() == 0) {
	    return;
	}

	y0 -= scrollY;
	y1 -= scrollY;

	/* Now try to find the range of items to print. */
	line0 = findItemBefore(y0);
	i = ((Integer) lineItemIndexes.elementAt(line0)).intValue();
	cnt = lineYValues.size() - line0;
	for (line1 = line0 + 1; --cnt > 0; line1 += 1) {
	    Integer anInt = (Integer) lineYValues.elementAt(line1);

	    if (anInt.intValue() >= y1) {
		break;
	    }
	}
	if (cnt > 0) {
	    j = ((Integer) lineItemIndexes.elementAt(line1)).intValue();
	} else {
	    j = count();
	}

//	System.out.println("Painting from " + i + " to " + j);
	/* Now paint them as fast as we can! */
	cnt = j - i;
	while (--cnt >= 0) {
	    DisplayItem	di = items[i++];

	    if (di.y + di.height < y0 || di.y > y1) {
		continue;
	    }
	    di.paint(this, di.x + scrollX, di.y + scrollY);
	}
	update();
    }

    public synchronized void print(PSGraphics pg) {
	int lTop, lBottom;
	int yTop, yBottom;
	int iTop, iBottom;
	int linecount = lineYValues.size() - 1;
	// REMIND: This is an extreme hack.  We replace the graphics
	// context and background color of the TextWindow and ask it to
	// redraw itself.
	Graphics g = graphics;
	Color c = background;
	graphics = (Graphics) pg;
	background = Color.white;
	try {
	    pg.setForeground(Color.black);
	    pg.setBackground(Color.white);
	    lTop = 0;
	    while (lTop < linecount) {
		yTop = ((Integer) lineYValues.elementAt(lTop)).intValue();
		yBottom = yTop + pg.outputDim.height;
		for (lBottom = lTop + 1; lBottom < linecount; lBottom++) {
		    int yThis = ((Integer) lineYValues.elementAt(lBottom))
			.intValue();
		    if (yThis >= yBottom) {
			lBottom--;
			break;
		    }
		}
		if (lTop == lBottom) {
		    lBottom++;
		}
		iTop = ((Integer) lineItemIndexes.elementAt(lTop)).intValue();
		iBottom = ((Integer) lineItemIndexes.elementAt(lBottom))
		    .intValue();
		pg.startPage();
		while (iTop < iBottom) {
		    DisplayItem di = items[iTop++];
		    if (di instanceof NativeDisplayItem) {
			continue;
		    }
		    di.paint(this, di.x, di.y - yTop);
		}
		lTop = lBottom;
		pg.endPage();
	    }
	} finally {
	    graphics = g;
	    background = c;
	}
    }

    public void dumpItemInfo() {
	int i = 0;
	int cnt = count();

	while (--cnt >= 0) {
	    DisplayItem	d = nthItem(i);

	    System.out.println(i + ": " + d);
	    i += 1;
	}
	System.out.println("Line table:");
	cnt = lineYValues.size();
	i = 0;
	while (--cnt >= 0) {
	    System.out.println(i + ": " + lineYValues.elementAt(i) + ", item = " + lineItemIndexes.elementAt(i));
	    i += 1;
	}
    }

    public void keyPressed(Event e) {
	switch (e.key) {
	case 'a' & 0x1f:
	    dumpItemInfo();
	    break;

	case 'd' & 0x1f:	/* control-D */
	    thrust(true);
	    break;

	case 'u' & 0x1f:	/* control-U */
	    thrust(false);
	    break;

	case 's' & 0x1f:	/* control-S */
	    if (scroller != null)
		scroller.brake(100);
	    break;

	  default:
	    super.keyPressed(e);
	    break;
	}
    }
}
