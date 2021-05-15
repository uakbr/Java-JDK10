/*
 * @(#)FlowLayout.java	1.20 95/01/31 Arthur van Hoff
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

import Math.*;

/**
 * Flow layout is used to layout buttons in a panel.
 * It will arrange buttons left to right until no
 * more buttons fit on the same line.
 *
 * @version 1.20 31 Jan 1995
 * @author Arthur van Hoff, Sami Shaio
 */
public class FlowLayout extends GapsLayout {
    public static FlowLayout defaultLayout;

    static {
	defaultLayout = new FlowLayout();
	defaultLayout.setGaps(4, 4, 4, 4);
    }

    /**
     * Preferred Dimension
     */
    public Dimension getPreferredSize(Container pTarget) {
	Dimension dim;
	int insets[] = getInsets(pTarget);
	int rowh = 0;
	int roww = 0;
	int w = 0, h = 0, i;
	int maxwidth = pTarget.width - insets[EAST] - insets[WEST];
	int nmembers = pTarget.children.length();

	for (i = 0 ; i < nmembers ; i++) {
	    if (i > 0) {
		w += gaps[EAST] + gaps[WEST];
	    }

	    Layoutable m = pTarget.getChild(i);
	    Dimension d = m.getPreferredSize();
	
	    w += d.width + ((Component)m).marginWidth;
	    h = Math.max(h, d.height + (2 * ((Component)m).marginHeight));

	    if ((w + d.width + ((Component)m).marginWidth) > maxwidth) {
		rowh += h + gaps[SOUTH] + gaps[NORTH];
		roww = Math.max(w, roww);
		w = 0;
		h = 0;
	    }
	}
	h += rowh + (gaps[NORTH] + gaps[SOUTH]);
	w = Math.max(w, roww);
	dim = new Dimension(w + insets[WEST] + insets[EAST],
			    h + insets[NORTH] + insets[SOUTH]);
	return dim;
    }

    public Dimension minDimension(Container pTarget) {
	Dimension dim = new Dimension(0, 0);
	int n;
	Layoutable c;
	int insets[] = getInsets(pTarget);
	int nmembers = pTarget.nChildren();

	dim.height = insets[SOUTH] + insets[NORTH] + gaps[NORTH] + gaps[SOUTH];
	dim.width = insets[EAST] + insets[WEST];
	for (n=0; n < nmembers; n++) {
	    c = pTarget.getChild(n);
	    if (c instanceof Component) {
		Component m = (Component)c;
		dim.height = Math.max(dim.height, m.height + m.marginHeight);
		dim.width += m.width + m.marginWidth + gaps[WEST] + gaps[EAST];
	    }
	}

	return dim;
    }

    /** 
     * Center the elements in the given row if there is any slack.
     */
    void centerComponents(int x,
			  int height,
			  int maxwidth,
			  int rowStart,
			  int rowEnd,
			  Container pTarget) {
	int r;
	int gapWidth = gaps[EAST]+gaps[WEST];

	if (x < maxwidth) {
	    x = (maxwidth - x) / 2;
	} else {
	    x = (x - maxwidth) / 2;
	}
	if (x > 0) {
	    for (r=rowEnd-1; r >= rowStart; r--) {
		Layoutable rc = pTarget.getChild(r);
		if (rc instanceof Component) {
		    Component c = (Component)rc;
		    c.move(c.x + x, c.y + ((height - c.height) / 2));
		}
	    }
	}
    }


    /**
     * Layout the container
     */
    public void layout(Container pTarget) {
	int insets[] = getInsets(pTarget);
	int x = insets[WEST];
	int y = insets[NORTH];
	int h = 0;
	int i;
	int maxwidth = pTarget.width - insets[EAST] - insets[WEST] -
	    gaps[EAST] - gaps[WEST];
	Layoutable m;
	int nmembers = pTarget.nChildren();
	int lastrow;
	int rowCount;
	int r;
	int slack;
	int mw, mh;
	
	h = 0;
	for (i = 0, lastrow = 0,rowCount=1; i < nmembers ; i++, rowCount++) {
	    m = pTarget.getChild(i);
	    Dimension d = m.getPreferredSize();
	    Component c;
	    if (m instanceof Component) {
		c = (Component)m;
		mw = c.marginWidth;
		mh = c.marginHeight;	    
		if ((x + d.width + mw) > maxwidth) {
		    // center the elements in the previous row if
		    // there is any slack left over.
		    centerComponents(x, h, maxwidth, lastrow, i, pTarget);
		    lastrow = i;
		    rowCount = 1;
		    x = insets[WEST];
		    y += h + gaps[NORTH] + gaps[SOUTH];
		    h = 0;
		}
		d.width = Math.min(d.width, maxwidth - x);
		if (c instanceof Container) {
		    c.reshape(x, y,
			      d.width + (2 * c.marginWidth),
			      d.height + (2 * c.marginHeight));
		} else {
		    c.reshape(x, y, d.width, d.height);
		}
		x += d.width + gaps[EAST] + gaps[WEST] + mw;
		h = Math.max(h, d.height);
	    }
	}
	centerComponents(x, h, maxwidth, lastrow, i, pTarget);
    }

}


