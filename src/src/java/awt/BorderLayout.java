/*
 * @(#)BorderLayout.java	1.17 95/09/08 Arthur van Hoff
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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

package java.awt;

import java.util.Hashtable;

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
 * @version 	1.17 09/08/95
 * @author 	Arthur van Hoff
 */
public class BorderLayout implements LayoutManager {
    int hgap;
    int vgap;

    Component north;
    Component west;
    Component east;
    Component south;
    Component center;

    /**
     * Constructs a new BorderLayout.
     */
    public BorderLayout() {
    }

    /**
     * Constructs a BorderLayout with the specified gaps.
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public BorderLayout(int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Adds the specified named component to the layout.
     * @param name the String name
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
	if ("Center".equals(name)) {
	    center = comp;
	} else if ("North".equals(name)) {
	    north = comp;
	} else if ("South".equals(name)) {
	    south = comp;
	} else if ("East".equals(name)) {
	    east = comp;
	} else if ("West".equals(name)) {
	    west = comp;
	}
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
	if (comp == center) {
	    center = null;
	} else if (comp == north) {
	    north = null;
	} else if (comp == south) {
	    south = null;
	} else if (comp == east) {
	    east = null;
	} else if (comp == west) {
	    west = null;
	}
    }

    /**
     * Returns the minimum dimensions needed to layout the components
     * contained in the specified target container. 
     * @param target the Container on which to do the layout
     * @see Container
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container target) {
	Dimension dim = new Dimension(0, 0);

	if ((east != null) && east.visible) {
	    Dimension d = east.minimumSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((west != null) && west.visible) {
	    Dimension d = west.minimumSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((center != null) && center.visible) {
	    Dimension d = center.minimumSize();
	    dim.width += d.width;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((north != null) && north.visible) {
	    Dimension d = north.minimumSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}
	if ((south != null) && south.visible) {
	    Dimension d = south.minimumSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}

	Insets insets = target.insets();
	dim.width += insets.left + insets.right;
	dim.height += insets.top + insets.bottom;

	return dim;
    }
    
    /**
     * Returns the preferred dimensions for this layout given the components
     * in the specified target container.
     * @param target the component which needs to be laid out
     * @see Container
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container target) {
	Dimension dim = new Dimension(0, 0);

	if ((east != null) && east.visible) {
	    Dimension d = east.preferredSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((west != null) && west.visible) {
	    Dimension d = west.preferredSize();
	    dim.width += d.width + hgap;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((center != null) && center.visible) {
	    Dimension d = center.preferredSize();
	    dim.width += d.width;
	    dim.height = Math.max(d.height, dim.height);
	}
	if ((north != null) && north.visible) {
	    Dimension d = north.preferredSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}
	if ((south != null) && south.visible) {
	    Dimension d = south.preferredSize();
	    dim.width = Math.max(d.width, dim.width);
	    dim.height += d.height + vgap;
	}

	Insets insets = target.insets();
	dim.width += insets.left + insets.right;
	dim.height += insets.top + insets.bottom;

	return dim;
    }

    /**
     * Lays out the specified container. This method will actually reshape the
     * components in the specified target container in order to satisfy the 
     * constraints of the BorderLayout object. 
     * @param target the component being laid out
     * @see Container
     */
    public void layoutContainer(Container target) {
	Insets insets = target.insets();
	int top = insets.top;
	int bottom = target.height - insets.bottom;
	int left = insets.left;
	int right = target.width - insets.right;

	if ((north != null) && north.visible) {
	    north.resize(right - left, north.height);
	    Dimension d = north.preferredSize();
	    north.reshape(left, top, right - left, d.height);
	    top += d.height + vgap;
	}
	if ((south != null) && south.visible) {
	    south.resize(right - left, south.height);
	    Dimension d = south.preferredSize();
	    south.reshape(left, bottom - d.height, right - left, d.height);
	    bottom -= d.height + vgap;
	}
	if ((east != null) && east.visible) {
	    east.resize(east.width, bottom - top);
	    Dimension d = east.preferredSize();
	    east.reshape(right - d.width, top, d.width, bottom - top);
	    right -= d.width + hgap;
	}
	if ((west != null) && west.visible) {
	    west.resize(west.width, bottom - top);
	    Dimension d = west.preferredSize();
	    west.reshape(left, top, d.width, bottom - top);
	    left += d.width + hgap;
	}
	if ((center != null) && center.visible) {
	    center.reshape(left, top, right - left, bottom - top);
	}
    }
    
    /**
     * Returns the String representation of this BorderLayout's values.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
