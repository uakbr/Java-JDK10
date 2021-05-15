/*
 * @(#)RowLayout.java	1.5 95/02/16 Sami Shaio
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
public class RowLayout extends GapsLayout {
    boolean	isCentered;
    int		maxheight;

    public RowLayout(boolean centered) {
	isCentered = centered;
    }

    /**
     * Minimum Dimension
     */
    public Dimension minDimension(Container pTarget) {
	Dimension dim;
	int nmembers = pTarget.children.length();
	int i;

	dim = new Dimension(0,0);

	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable m = pTarget.getChild(i);
	    Dimension d = m.getPreferredSize();

	    if (m instanceof Component) {
		Component comp = (Component)m;

		dim.height = Math.max(dim.height,
				      d.height + comp.marginHeight);
		dim.width += d.width + comp.marginWidth;
	    }
	}
	
	maxheight = dim.height;
	return dim;
    }

    public Dimension getPreferredSize(Container pTarget) {

	return minDimension(pTarget);
    }

    /** 
     * Center the elements in the given row if there is any slack.
     */
    void centerComponents(Container pTarget,
			  int x,
			  int height,
			  int maxwidth,
			  int rowStart,
			  int rowEnd) {
	int r;
	int yoff;

	if (x < maxwidth) {
	    x = (maxwidth - x) / 2;
	} else {
	    x = 0;
	}
	if (x > 0) {
	    for (r=rowEnd-1; r >= rowStart; r--) {
		Layoutable rc = pTarget.getChild(r);
		if (rc instanceof Component) {
		    Component c = (Component)rc;
		    yoff = (height - c.height) / 2;
		    if (yoff < 0) {
			yoff = 0;
		    }
		    c.move(c.x + x, c.y + yoff);
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

		if (comp instanceof Container) {
		    comp.reshape(cx, comp.y, d.width, maxheight);
		}
		int yoff = (maxheight - comp.height) / 2;
		if (yoff < 0) {
		    yoff = 0;
		}
		if (comp.width != d.width ||
		    comp.height != d.height) {
		    if (cx + d.width > pTarget.width) {
			d.width = pTarget.width - cx;
		    }
		    if (cy + d.height > pTarget.height) {
			d.height = pTarget.height - cy;
		    }
		    comp.reshape(cx, cy + yoff, d.width, d.height);
		} else {
		    if (isCentered) {
			comp.x = cx;
			comp.y = cy + yoff;
		    } else {
			comp.move(cx, cy + yoff);
		    }
		}
		cx += d.width + comp.marginWidth;
	    }
	}
	if (isCentered) {
	    centerComponents(pTarget, cx, maxheight, pTarget.width, 0, nmembers);
	}
    }
}
