/*
 * @(#)Rectangle.java	1.18 95/12/14 Sami Shaio
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
 * A rectangle defined by x, y, width and height.
 *
 * @version 	1.18, 12/14/95
 * @author 	Sami Shaio
 */
public class Rectangle {

    /**
     * The x coordinate of the rectangle.
     */
    public int x;

    /**
     * The y coordinate of the rectangle.
     */
    public int y;

    /**
     * The width of the rectangle.
     */
    public int width;

    /**
     * The height of the rectangle.
     */
    public int height;

    /**
     * Constructs a new rectangle.
     */
    public Rectangle() {
    }

    /**
     * Constructs and initializes a rectangle with the specified parameters.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public Rectangle(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }

    /**
     * Constructs a rectangle and initializes it with the specified width and 
     * height parameters.
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public Rectangle(int width, int height) {
	this(0, 0, width, height);
    }

    /**
     * Constructs a rectangle and initializes it to a specified point and  
     * dimension.
     * @param p the point
     * @param d dimension 
     */
    public Rectangle(Point p, Dimension d) {
	this(p.x, p.y, d.width, d.height);
    }
    
    /**
     * Constructs a rectangle and initializes it to the specified point.
     * @param p the value of the x and y coordinate
     */
    public Rectangle(Point p) {
	this(p.x, p.y, 0, 0);
    }
    
    /**
     * Constructs a rectangle and initializes it to the specified width and 
     * height.
     * @param d the value of the width and height
     */
    public Rectangle(Dimension d) {
	this(0, 0, d.width, d.height);
    }

    /**
     * Reshapes the rectangle.
     */
    public void reshape(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
    }	

    /**
     * Moves the rectangle.
     */
    public void move(int x, int y) {
	this.x = x;
	this.y = y;
    }	

    /**
     * Translates the rectangle.
     */
    public void translate(int x, int y) {
	this.x += x;
	this.y += y;
    }	

    /**
     * Resizes the rectangle.
     */
    public void resize(int width, int height) {
	this.width = width;
	this.height = height;
    }	

    /**
     * Checks if the specified point lies inside a rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public boolean inside(int x, int y) {
	return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y-this.y) < this.height);
    }

    /**
     * Checks if two rectangles intersect.
     */
    public boolean intersects(Rectangle r) {
	return !((r.x + r.width < x) ||
		 (r.y + r.height < y) ||
		 (r.x >= x + width) ||
		 (r.y >= y + height));
    }

    /**
     * Computes the intersection of two rectangles.
     */
    public Rectangle intersection(Rectangle r) {
	int x1 = Math.max(x, r.x);
	int x2 = Math.min(x + width, r.x + r.width);
	int y1 = Math.max(y, r.y);
	int y2 = Math.min(y + height, r.y + r.height);
	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Computes the union of two rectangles.
     */
    public Rectangle union(Rectangle r) {
	int x1 = Math.min(x, r.x);
	int x2 = Math.max(x + width, r.x + r.width);
	int y1 = Math.min(y, r.y);
	int y2 = Math.max(y + height, r.y + r.height);
	return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Adds a point to a rectangle. This results in the smallest
     * rectangle that contains both the rectangle and the point.
     */
    public void add(int newx, int newy) {
	int x1 = Math.min(x, newx);
	int x2 = Math.max(x + width, newx);
	int y1 = Math.min(y, newy);
	int y2 = Math.max(y + height, newy);
	x = x1;
	y = y1;
	width = x2 - x1;
	height = y2 - y1;
    }

    /**
     * Adds a point to a rectangle. This results in the smallest
     * rectangle that contains both the rectangle and the point.
     */
    public void add(Point pt) {
	add(pt.x, pt.y);
    }

    /**
     * Adds a rectangle to a rectangle. This results in the union
     * of the two rectangles.
     */
    public void add(Rectangle r) {
	int x1 = Math.min(x, r.x);
	int x2 = Math.max(x + width, r.x + r.width);
	int y1 = Math.min(y, r.y);
	int y2 = Math.max(y + height, r.y + r.height);
	x = x1;
	y = y1;
	width = x2 - x1;
	height = y2 - y1;
    }

    /**
     * Grows the rectangle horizontally and vertically.
     */
    public void grow(int h, int v) {
	x -= h;
	y -= v;
	width += h * 2;
	height += v * 2;
    }

    /**
     * Determines whether the rectangle is empty.
     */
    public boolean isEmpty() {
	return (width <= 0) || (height <= 0);
    }

    /**
     * Returns the hashcode for this Rectangle.
     */
    public int hashCode() {
	return x ^ (y*37) ^ (width*43) ^ (height*47);
    }

    /**
     * Checks whether two rectangles are equal.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Rectangle) {
	    Rectangle r = (Rectangle)obj;
	    return (x == r.x) && (y == r.y) && (width == r.width) && (height == r.height);
	}
	return false;
    }

    /**
     * Returns the String representation of this Rectangle's values.
     */
    public String toString() {
	return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
}
