/*
 * @(#)BorderLayout.java	1.22 95/01/31 Arthur van Hoff
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

/**
 * A TNT style border bag layout. It will layout a container
 * using members named "North", "South", "East", "West" and
 * "Center".
 *
 * The "North", "South", "East" and "West" components get layed out
 * according to their preferred sizes and the constraints of the
 * container's size. The "Center" component will get any space left
 * over. 
 * 
 * To use a BorderLayout in a window do the following:
 *<pre>
 *	Window win = new Window(parent, "", Color.lightGray, 200, 200);
 *	win.setLayout(new BorderLayout());
 *</pre>
 * 
 * @see awt.Window
 * @see awt.Frame
 * @version 1.22 31 Jan 1995
 * @author Arthur van Hoff
 */
public class BorderLayout extends GapsLayout {
    /**
     * Use this object to set a border layout for a window with
     * standard properties.
     */
    public static BorderLayout defaultLayout;

    static {
	defaultLayout = new BorderLayout();
	defaultLayout.setGaps(0, 0, 0, 0);
    }

    /**
     * Return the minimum dimensions needed to layout the components
     * contained in pTarget. 
     * @param pTarget is the Container on which to do the layout.
     * @see awt.Container
     */
    public Dimension minDimension(Container pTarget) {
	int insets[] = getInsets(pTarget);
	int vert = insets[NORTH] + insets[SOUTH];
	int horz = insets[WEST]  + insets[EAST];
	Dimension dim = new Dimension(horz, vert);
	Layoutable m;

	if ((m = pTarget.getChild("North")) != null) {
	    Dimension d = m.minDimension();
	    dim.width = max(d.width + ((Component)m).marginWidth, dim.width);
	    vert += d.height + ((Component)m).marginHeight + gaps[NORTH];
	}
	if ((m = pTarget.getChild("South")) != null) {
	    Dimension d = m.minDimension();
	    dim.width = max(d.width + ((Component)m).marginWidth,dim.width);
	    vert += d.height + ((Component)m).marginHeight + gaps[SOUTH];
	}
	if ((m = pTarget.getChild("East")) != null) {
	    Dimension d = m.minDimension();
	    dim.height = max(d.height, dim.height) + vert;
	    horz += d.width + gaps[EAST];
	}
	if ((m = pTarget.getChild("West")) != null) {
	    Dimension d = m.minDimension();
	    dim.height = max(d.height, dim.height) + vert;
	    horz += d.width + gaps[EAST];
	}
	if ((m = pTarget.getChild("Center")) != null) {
	    Dimension d = m.minDimension();
	    dim.width = max(d.width, dim.width) + horz;
	    dim.height = max(d.height, dim.height) + vert;
	}

	return dim;
    }
    
    /**
     * Return the preferred size for this layout given the components
     * in pTarget.
     * @param pTarget is the component which needs to be laid out.
     * @see awt.Container
     */
    public Dimension getPreferredSize(Container pTarget) {
	Dimension d = minDimension(pTarget);

	return minDimension(pTarget);
    }

    /**
     * Layout the container. This method will actually reshape the
     * components in pTarget in order to satisfy the constraints of
     * the BorderLayout object. Normally this method is invoked by the
     * container.
     * @param pTarget is the component being laid out.
     * @see awt.Container
     */
    public void layout(Container pTarget) {
	int insets[] = getInsets(pTarget);
	int top = 0;
	int bottom = pTarget.height - insets[SOUTH] - gaps[SOUTH];
	int left = insets[WEST];
	int right = pTarget.width - insets[EAST];
	Layoutable m;

	top += insets[NORTH] + gaps[NORTH];
	if ((m = pTarget.getChild("North")) != null) {
	    Dimension d = m.getPreferredSize();
	    m.reshape(left, top, right - left, d.height);
	    top += d.height;
	}
	if ((m = pTarget.getChild("South")) != null) {
	    Dimension d = m.getPreferredSize();
	    m.reshape(left, bottom - d.height, right - left, d.height);
	    bottom -= d.height;
	}
	if ((m = pTarget.getChild("East")) != null) {
	    Dimension d = m.getPreferredSize();
	    m.reshape(right - d.width, top, d.width,
		      max(d.height, bottom - top));
	    right -= (d.width + gaps[EAST]);
	} else {
	    right -= gaps[EAST];
	}
	if ((m = pTarget.getChild("West")) != null) {
	    Dimension d = m.getPreferredSize();
	    m.reshape(left, top, d.width, bottom - top);
	    left += d.width + gaps[WEST];
	} else {
	    left += gaps[WEST];
	}
	if ((m = pTarget.getChild("Center")) != null) {
	    m.reshape(left, top, right - left, bottom - top);
	}
    }
}
