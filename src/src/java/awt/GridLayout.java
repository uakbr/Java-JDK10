/*
 * @(#)GridLayout.java	1.11 95/12/14 Arthur van Hoff
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

/**
 * A layout manager for a container that lays out grids.
 *
 *
 * @version 1.11, 12/14/95
 * @author 
 */
public class GridLayout implements LayoutManager {
    int hgap;
    int vgap;
    int rows;
    int cols;

    /**
     * Creates a grid layout with the specified rows and columns.
     * @param rows the rows
     * @param cols the columns
     */
    public GridLayout(int rows, int cols) {
	this(rows, cols, 0, 0);
    }

    /**
     * Creates a grid layout with the specified rows, columns,
     * horizontal gap, and vertical gap.
     * @param rows the rows; zero means 'any number.'
     * @param cols the columns; zero means 'any number.'  Only one of 'rows'
     * and 'cols' can be zero, not both.
     * @param hgap the horizontal gap variable
     * @param vgap the vertical gap variable
     * @exception IllegalArgumentException If the rows and columns are invalid.
     */
    public GridLayout(int rows, int cols, int hgap, int vgap) {
	if ((rows == 0) && (cols == 0)) {
	    throw new IllegalArgumentException("invalid rows,cols");
	}

	this.rows = rows;
	this.cols = cols;
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Adds the specified component with the specified name to the layout.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Removes the specified component from the layout. Does not apply.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
    }

    /** 
     * Returns the preferred dimensions for this layout given the components
     * int the specified panel.
     * @param parent the component which needs to be laid out 
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
	Insets insets = parent.insets();
	int ncomponents = parent.countComponents();
	int nrows = rows;
	int ncols = cols;

	if (nrows > 0) {
	    ncols = (ncomponents + nrows - 1) / nrows;
	} else {
	    nrows = (ncomponents + ncols - 1) / ncols;
	}
	int w = 0;
	int h = 0;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.preferredSize();
	    if (w < d.width) {
		w = d.width;
	    }
	    if (h < d.height) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + ncols*w + (cols-1)*hgap, 
			     insets.top + insets.bottom + nrows*h + (rows-1)*vgap);
    }

    /**
     * Returns the minimum dimensions needed to layout the components 
     * contained in the specified panel.
     * @param parent the component which needs to be laid out 
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
	Insets insets = parent.insets();
	int ncomponents = parent.countComponents();
	int nrows = rows;
	int ncols = cols;

	if (nrows > 0) {
	    ncols = (ncomponents + nrows - 1) / nrows;
	} else {
	    nrows = (ncomponents + ncols - 1) / ncols;
	}
	int w = 0;
	int h = 0;
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.minimumSize();
	    if (w < d.width) {
		w = d.width;
	    }
	    if (h < d.height) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + ncols*w + (cols-1)*hgap, 
			     insets.top + insets.bottom + nrows*h + (rows-1)*vgap);
    }

    /** 
     * Lays out the container in the specified panel.  
     * @param parent the specified component being laid out
     * @see Container
     */
    public void layoutContainer(Container parent) {
	Insets insets = parent.insets();
	int ncomponents = parent.countComponents();
	int nrows = rows;
	int ncols = cols;

	if (ncomponents == 0) {
	    return;
	}
	if (nrows > 0) {
	    ncols = (ncomponents + nrows - 1) / nrows;
	} else {
	    nrows = (ncomponents + ncols - 1) / ncols;
	}
	int w = parent.width - (insets.left + insets.right);
	int h = parent.height - (insets.top + insets.bottom);
	w = (w - (ncols - 1) * hgap) / ncols;
	h = (h - (nrows - 1) * vgap) / nrows;

	for (int c = 0, x = insets.left ; c < ncols ; c++, x += w + hgap) {
	    for (int r = 0, y = insets.top ; r < nrows ; r++, y += h + vgap) {
		int i = r * ncols + c;
		if (i < ncomponents) {
		    parent.getComponent(i).reshape(x, y, w, h);
		}
	    }
	}
    }
    
    /**
     * Returns the String representation of this GridLayout's values.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + 
	    			       ",rows=" + rows + ",cols=" + cols + "]";
    }
}
