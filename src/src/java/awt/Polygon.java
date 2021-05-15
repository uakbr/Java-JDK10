/*
 * @(#)Polygon.java	1.8 95/12/14 Sami Shaio
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
package java.awt;

/**
 * A polygon consists of a list of x and y coordinates.
 *
 * @version 	1.8, 12/14/95
 * @author 	Sami Shaio
 * @author      Herb Jellinek
 */
public class Polygon {
    /**
     * The total number of points.
     */
    public int npoints = 0;

    /**
     * The array of x coordinates.
     */
    public int xpoints[] = new int[4];

    /**
     * The array of y coordinates.
     */
    public int ypoints[] = new int[4];
    
    /*
     * Bounds of the polygon.
     */
    Rectangle bounds = null;
    
    /**
     * Creates an empty polygon.
     */
    public Polygon() {
    }

    /**
     * Constructs and initializes a Polygon from the specified parameters.
     * @param xpoints the array of x coordinates
     * @param ypoints the array of y coordinates
     * @param npoints the total number of points in the Polygon
     */
    public Polygon(int xpoints[], int ypoints[], int npoints) {
	this.npoints = npoints;
	this.xpoints = new int[npoints];
	this.ypoints = new int[npoints];
	System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
	System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);	
    }
    
    /*
     * Calculate the bounding box of the points passed to the constructor.
     * Sets 'bounds' to the result.
     */
    void calculateBounds(int xpoints[], int ypoints[], int npoints) {
	int boundsMinX = Integer.MAX_VALUE;
	int boundsMinY = Integer.MAX_VALUE;
	int boundsMaxX = Integer.MIN_VALUE;
	int boundsMaxY = Integer.MIN_VALUE;
	
	for (int i = 0; i < npoints; i++) {
	    int x = xpoints[i];
	    boundsMinX = Math.min(boundsMinX, x);
	    boundsMaxX = Math.max(boundsMaxX, x);
	    int y = ypoints[i];
	    boundsMinY = Math.min(boundsMinY, y);
	    boundsMaxY = Math.max(boundsMaxY, y);
	}
	bounds = new Rectangle(boundsMinX, boundsMinY,
			       boundsMaxX - boundsMinX,
			       boundsMaxY - boundsMinY);
    }

    /*
     * Update the bounding box to fit the point x, y.
     */
    void updateBounds(int x, int y) {
	bounds.x = Math.min(bounds.x, x);
	bounds.width = Math.max(bounds.width, x - bounds.x);
	bounds.y = Math.min(bounds.y, y);
	bounds.height = Math.max(bounds.height, y - bounds.y);
    }	

    /**
     * Appends a point to a polygon.  If inside(x, y) or another
     * operation that calculates the bounding box has already been
     * performed, this method updates the bounds accordingly.

     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     */
    public void addPoint(int x, int y) {
	if (npoints == xpoints.length) {
	    int tmp[];

	    tmp = new int[npoints * 2];
	    System.arraycopy(xpoints, 0, tmp, 0, npoints);
	    xpoints = tmp;

	    tmp = new int[npoints * 2];
	    System.arraycopy(ypoints, 0, tmp, 0, npoints);
	    ypoints = tmp;
	}
	xpoints[npoints] = x;
	ypoints[npoints] = y;
	npoints++;
	if (bounds != null) {
	    updateBounds(x, y);
	}
    }

    /**
     * Determines the area spanned by this Polygon.
     * @return a Rectangle defining the bounds of the Polygon.
     */
    public Rectangle getBoundingBox() {
	if (bounds == null) {
	    calculateBounds(xpoints, ypoints, npoints);
	}
	return bounds;
    }
    
    /**
     * Determines whether the point (x,y) is inside the Polygon. Uses
     * an even-odd insideness rule (also known as an alternating
     * rule).

     * @param x the X coordinate of the point to be tested
     * @param y the Y coordinate of the point to be tested
     *
     * <p>Based on code by Hanpeter van Vliet <hvvliet@inter.nl.net>.
     */
    public boolean inside(int x, int y) {
	if (getBoundingBox().inside(x, y)) {
	    int hits = 0;
	    
	    // Walk the edges of the polygon
	    for (int i = 0; i < npoints; i++) {
		int j = (i + 1) % npoints;
		
		int dx = xpoints[j] - xpoints[i];
		int dy = ypoints[j] - ypoints[i];
		
		// ignore horizontal edges completely
		if (dy != 0) {
		    // Check to see if the edge intersects
		    // the horizontal halfline through (x, y)
		    int rx = x - xpoints[i];
		    int ry = y - ypoints[i];
		    
		    // Quick and dirty way to deal with vertices
		    // that fall exactly on the halfline
		    if (ypoints[i] == y) ry--;
		    if (ypoints[j] == y) dy++;
		    
		    float s = (float)ry / (float)dy;
		    if (s >= 0.0 && s <= 1.0) {
			if ((int)(s * dx) > rx) hits++;
		    }
		}
	    }
	    
	    return (hits % 2) != 0;
	}
	return false;
    }
}
