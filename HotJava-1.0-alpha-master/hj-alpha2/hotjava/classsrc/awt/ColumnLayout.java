/*
 * @(#)ColumnLayout.java	1.8 95/02/16 Sami Shaio
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
package awt;
import Math.max;
import Math.min;
public class ColumnLayout extends GapsLayout {
    int maxheight;
    int maxwidth;
    boolean isCentered;

    public ColumnLayout(boolean isCentered) {
	this.isCentered = isCentered;
    }

    public ColumnLayout() {
	this(false);
    }

    /**
     * Preferred Dimension
     */
    public Dimension getPreferredSize(Container pTarget) {
	Dimension dim;
	int nmembers = pTarget.children.length();
	int lastMarginHeight = 0;
	int lastMarginWidth = 0;
	int i;

	dim = new Dimension(0,0);

	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable m = pTarget.getChild(i);
	    Dimension d = m.getPreferredSize();

	    if (m instanceof Component) {
		Component comp = (Component)m;

		lastMarginHeight = comp.marginHeight;
		lastMarginWidth = comp.marginWidth;
		dim.height += d.height + comp.marginHeight;
		dim.width = Math.max(d.width + comp.marginWidth,
				     dim.width);
	    }
	}
	
	maxheight = dim.height;
	maxwidth = dim.width;
	return dim;
    }

    public Dimension minDimension(Container pTarget) {
	return getPreferredSize(pTarget);
    }

    /** 
     * Center the elements in the given row if there is any slack.
     */
    void centerComponents(Container pTarget,
			  int y,
			  int width,
			  int maxheight,
			  int rowStart,
			  int rowEnd) {
	int r;
	int xoff;

	if (y < maxheight) {
	    y = (maxheight - y) / 2;
	} else {
	    y = (y - maxheight) / 2;
	}
	if (y > 0) {
	    for (r=rowEnd-1; r >= rowStart; r--) {
		Layoutable rc = pTarget.getChild(r);
		if (rc instanceof Component) {
		    Component c = (Component)rc;
		    xoff = ((width - c.width) / 2);
		    if (xoff < 0) {
			xoff = 0;
		    }
		    c.move(c.x + xoff, c.y + y);
		}
	    }
	} else {
	    // we need to move the components to their positions
	    // because this wasn't done in expectation that they would
	    // get moved again to be centered.
	    for (r=rowEnd-1; r >= rowStart; r--) {
		Layoutable rc = pTarget.getChild(r);
		if (rc instanceof Component) {
		    Component c = (Component)rc;
		    c.move(c.x, c.y);
		}
	    }	    
	}
    }

    public void layout(Container pTarget) {
	Dimension dim;
	int nmembers = pTarget.children.length();
	int cx = pTarget.x;
	int cy = pTarget.y;
	int w, h;
	int i;

	if (pTarget instanceof Window) {
	    cx = cy = 0;
	}

	dim = new Dimension(0,0);

	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable m = pTarget.getChild(i);
	    Dimension d = m.getPreferredSize();

	    if (m instanceof Component) {
		Component comp = (Component)m;

		if (comp.width != d.width ||
		    comp.height != d.height) {
		    if (cx + d.width > pTarget.width) {
			d.width = pTarget.width - cx;
		    }
		    if (cy + d.height > pTarget.height) {
			d.height = pTarget.height - cy;
		    }
		    comp.reshape(cx, cy, d.width, d.height);
		} else {
		    if (isCentered) {
			comp.x = cx;
			comp.y = cy;
		    } else {
			comp.move(cx, cy);
		    }
		}
		if (comp instanceof Container) {
		    comp.reshape(cx, cy, pTarget.width, d.height);
		}
		cy += d.height + comp.marginHeight;
	    }
	}
	if (isCentered) {
	    centerComponents(pTarget, cy, pTarget.width, pTarget.height, 0, nmembers);
	}
    }
}
