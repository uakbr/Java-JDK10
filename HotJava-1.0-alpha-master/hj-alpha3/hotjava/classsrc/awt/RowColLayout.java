/*
 * @(#)RowColLayout.java	1.18 95/02/17 Arthur van Hoff
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
 * RowColLayout arranges components in rows and columns.
 * @version 1.18 17 Feb 1995
 * @author Arthur van Hoff, Sami Shaio
 */

public class RowColLayout extends GapsLayout {
    int rows;
    int cols;
    boolean isPacked;
    public static RowColLayout oneColumn;
    public static RowColLayout splitVert;
    public static RowColLayout splitHorz;

    static {
	oneColumn = new RowColLayout(0, 1);
	splitVert = new RowColLayout(1, 2);
	splitHorz = new RowColLayout(2, 1);
    }

    /**
     * Constructor, rows can be 0, meaning any
     * number of rows.
     */
    public RowColLayout(int pRows, int pCols) {
	rows = pRows;
	cols = pCols;
	setGaps(4,4,4,4);
	isPacked = false;
    }
    
    /**
     * Constructor, rows can be 0, meaning any number of rows. If
     * packed is true then columns will vary in width according to the
     * widest member in each column.
     * @param pRows the number of rows, can be 0 for an arbitrary number.
     * @param pCols the number of columns.
     * @param packed if true then each column will have the width of
     * its widest member rather than the width of the widest member for
     * all the columns.
     */
    public RowColLayout(int pRows, int pCols, boolean packed) {
	this(pRows, pCols);
	isPacked = packed;
    }

    /**
     * Preferred Dimension
     */
    public Dimension getPreferredSize(Container pTarget) {
	Dimension dim = new Dimension(0, 0);
	int maxrow = 0;
	int row = 0;
	int rowh = 0;
	int n;
	int nmembers = pTarget.nChildren();
	Layoutable m;

	//System.out.println("RowColLayout(" + cols + ")");
	for (n = 0 ; n < nmembers; n++) {
	    if ((n % cols) == 0) {
		// starting a new row
		if (row > maxrow) {
		    maxrow = row;
		}
		rowh += dim.height;
		row = 0;
	    }
		
	    m = pTarget.getChild(n);
	    Dimension d = m.getPreferredSize();
	    //System.out.println("Component " + n + ": " + d.width + ", " +
	    //d.height); 
	    row += d.width + ((Component)m).marginWidth;
	    dim.height = max(dim.height,
			     d.height + ((Component)m).marginHeight);
	}
	
	dim.width = maxrow;
	dim.height += rowh;
	if (rows == 0) {
	    int r = (nmembers + cols - 1) / cols;

	    dim.width += (cols - 1) * (gaps[EAST] + gaps[WEST]);
	    dim.height += (r - 1) * (gaps[NORTH] + gaps[SOUTH]);
	} else {
	    dim.width += (cols - 1) * (gaps[EAST] + gaps[WEST]);
	    dim.height += (rows - 1) * (gaps[NORTH] + gaps[SOUTH]);
	}

	int insets[] = getInsets(pTarget);
	dim.width += insets[EAST] + insets[WEST];
	dim.height += insets[NORTH] + insets[SOUTH];
	//System.out.println("RowColLayout.getPreferredSize= " + dim.width
	//+ ", " + dim.height);
	return dim;
    }

    /**
     * Layout the container
     */
    public void layout(Container pTarget) {
	int insets[] = getInsets(pTarget);
	int r, c, w, h, n, x, y;
	int nmembers = pTarget.nChildren();
	int colWidths[] = new int[cols];

	//System.out.println("RowColLayout.layout");
	if (isPacked) {
	    // figure out width for each column
	    for (c=0; c < cols; c++) {
		colWidths[c] = 0;
	    }
	    for (r = n = 0; n < nmembers ; r++) {
		for (c = 0 ; (n < nmembers) && (c < cols) ; c++, n++) {
		    Layoutable m = pTarget.getChild(n);
		    Dimension d = m.getPreferredSize();

		    colWidths[c] = max(colWidths[c], d.width);
		}
	    }
	}
	if (rows == 0) {
	    if (!isPacked) {
		w = pTarget.width;
		w -= insets[WEST] + insets[EAST];
		w -= (cols - 1) * (gaps[WEST] + gaps[EAST]);
		w /= cols;
		for (c=0; c < cols; c++) {
		    colWidths[c] = w;
		}
	    } else {
		// add any slack to the last column
		w = 0;
		for (c=0; c < (cols - 1); c++) {
		    w += colWidths[c];
		}
		colWidths[cols - 1] = pTarget.width - w - insets[WEST]
		    - insets[EAST] - gaps[WEST] - gaps[EAST];
	    }
	    for (r = n = 0, y = insets[NORTH] ; n < nmembers ; r++) {
		h = 0;
		x = insets[WEST];
		for (c = 0 ; (n < nmembers) && (c < cols) ; c++, n++) {
		    Layoutable m = pTarget.getChild(n);
		    Dimension d = m.getPreferredSize();
		    Component comp = (Component)m;
		    int mw, mh;

		    mw = comp.marginWidth;
		    mh = comp.marginHeight;
		    h = max(h, d.height + mh);
		    m.reshape(x, y, colWidths[c], d.height);
		    x += colWidths[c] + gaps[WEST] + gaps[EAST];
		}
		y += h + gaps[NORTH] + gaps[SOUTH];
	    }
	} else {
	    w = pTarget.width;
	    w -= insets[WEST] + insets[EAST];
	    w -= (cols - 1) * (gaps[WEST] + gaps[EAST]);
	    w /= cols;
	    h = pTarget.height;
	    h -= insets[NORTH] + insets[SOUTH];
	    h -= (rows - 1) * (gaps[NORTH] + gaps[SOUTH]);
	    h /= rows;
	    for (r = n = 0, y = insets[NORTH] ; n < nmembers ; r++) {
		x = insets[WEST];
		for (c = 0 ; (n < nmembers) && (c < cols) ; c++, n++) {
		    Layoutable m = pTarget.getChild(n);
		    m.reshape(x, y, w, h);
		    x += w + gaps[WEST] + gaps[EAST];
		}
		y += h + gaps[NORTH] + gaps[SOUTH];
	    }
	}
    }
}


