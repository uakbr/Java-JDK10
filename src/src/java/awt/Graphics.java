/*
 * @(#)Graphics.java	1.29 95/12/14 Sami Shaio
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

import java.io.*;
import java.lang.*;
import java.util.*;
import java.awt.image.ImageObserver;

/**
 * Graphics is the abstract base class for all graphic 
 * contexts for various devices. 
 * 
 * @version 	1.29, 12/14/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public abstract class Graphics {

    /**
     * Constructs a new Graphics Object. Graphic contexts cannot be 
     * created directly. They must be obtained from another graphics
     * context or created by a Component.
     * @see Component#getGraphics
     * @see #create
     */
    protected Graphics() {
    }

    /**
     * Creates a new Graphics Object that is a copy of the original Graphics Object.
     */
    public abstract Graphics create();

    /**
     * Creates a new Graphics Object with the specified parameters, based on the original
     * Graphics Object. 
     * This method translates the specified parameters, x and y, to
     * the proper origin coordinates and then clips the Graphics Object to the
     * area.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the area
     * @param height the height of the area
     * @see #translate
     */
    public Graphics create(int x, int y, int width, int height) {
	Graphics g = create();
	g.translate(x, y);
	g.clipRect(0, 0, width, height);
	return g;
    }

    /**
     * Translates the specified parameters into the origin of the graphics context. All subsequent
     * operations on this graphics context will be relative to this origin.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public abstract void translate(int x, int y);

    /**
     * Gets the current color.
     * @see #setColor
     */
    public abstract Color getColor();

    /**
     * Sets the current color to the specified color. All subsequent graphics operations
     * will use this specified color.
     * @param c the color to be set
     * @see Color
     * @see #getColor
     */
    public abstract void setColor(Color c);

    /**
     * Sets the paint mode to overwrite the destination with the
     * current color. 
     */
    public abstract void setPaintMode();

    /**
     * Sets the paint mode to alternate between the current color
     * and the new specified color.  When drawing operations are
     * performed, pixels which are the current color will be changed
     * to the specified color and vice versa.  Pixels of colors other
     * than those two colors will be changed in an unpredictable, but
     * reversible manner - if you draw the same figure twice then all
     * pixels will be restored to their original values.
     * @param c1 the second color
     */
    public abstract void setXORMode(Color c1);

    /**
     * Gets the current font.
     * @see #setFont
     */
    public abstract Font getFont();

    /**
     * Sets the font for all subsequent text-drawing operations.
     * @param font the specified font
     * @see Font
     * @see #getFont
     * @see #drawString
     * @see #drawBytes
     * @see #drawChars
    */
    public abstract void setFont(Font font);

    /**
     * Gets the current font metrics.
     * @see #getFont
     */
    public FontMetrics getFontMetrics() {
	return getFontMetrics(getFont());
    }

    /**
     * Gets the current font metrics for the specified font.
     * @param f the specified font
     * @see #getFont
     * @see #getFontMetrics
     */
    public abstract FontMetrics getFontMetrics(Font f);


    /** 
     * Returns the bounding rectangle of the current clipping area.
     * @see #clipRect
     */
    public abstract Rectangle getClipRect();

    /** 
     * Clips to a rectangle. The resulting clipping area is the
     * intersection of the current clipping area and the specified
     * rectangle. Graphic operations have no effect outside of the
     * clipping area.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #getClipRect
     */
    public abstract void clipRect(int x, int y, int width, int height);

    /**
     * Copies an area of the screen.
     * @param x the x-coordinate of the source
     * @param y the y-coordinate of the source
     * @param width the width
     * @param height the height
     * @param dx the horizontal distance
     * @param dy the vertical distance
     */
    public abstract void copyArea(int x, int y, int width, int height, int dx, int dy);

    /** 
     * Draws a line between the coordinates (x1,y1) and (x2,y2). The line is drawn
     * below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     */
    public abstract void drawLine(int x1, int y1, int x2, int y2);

    /** 
     * Fills the specified rectangle with the current color. 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #drawRect
     * @see #clearRect
     */
    public abstract void fillRect(int x, int y, int width, int height);

    /** 
     * Draws the outline of the specified rectangle using the current color.
     * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
     * rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height) {
	drawLine(x, y, x + width, y);
	drawLine(x + width, y, x + width, y + height);
	drawLine(x, y, x, y + height);
	drawLine(x, y + height, x + width, y + height);
    }
    
    /** 
     * Clears the specified rectangle by filling it with the current background color
     * of the current drawing surface.
     * Which drawing surface it selects depends on how the graphics context
     * was created.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillRect
     * @see #drawRect
     */
    public abstract void clearRect(int x, int y, int width, int height);

    /** 
     * Draws an outlined rounded corner rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the horizontal diameter of the arc at the four corners
     * @see #fillRoundRect
     */
    public abstract void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    /** 
     * Draws a rounded rectangle filled in with the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the horizontal diameter of the arc at the four corners
     * @see #drawRoundRect
     */
    public abstract void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight);

    /**
     * Draws a highlighted 3-D rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param raised a boolean that states whether the rectangle is raised or not
     */
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
	Color c = getColor();
	Color brighter = c.brighter();
	Color darker = c.darker();

	setColor(raised ? brighter : darker);
	drawLine(x, y, x, y + height);
	drawLine(x + 1, y, x + width - 1, y);
	setColor(raised ? darker : brighter);
	drawLine(x + 1, y + height, x + width, y + height);
	drawLine(x + width, y, x + width, y + height - 1);
	setColor(c);
    }    

    /**
     * Paints a highlighted 3-D rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param raised a boolean that states whether the rectangle is raised or not
     */
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
	Color c = getColor();
	Color brighter = c.brighter();
	Color darker = c.darker();

	if (!raised) {
	    setColor(darker);
	}
	fillRect(x+1, y+1, width-2, height-2);
	setColor(raised ? brighter : darker);
	drawLine(x, y, x, y + height - 1);
	drawLine(x + 1, y, x + width - 2, y);
	setColor(raised ? darker : brighter);
	drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
	drawLine(x + width - 1, y, x + width - 1, y + height - 2);
	setColor(c);
    }    

    /** 
     * Draws an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillOval
     */
    public abstract void drawOval(int x, int y, int width, int height);

    /** 
     * Fills an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #drawOval
     */
    public abstract void fillOval(int x, int y, int width, int height);

    /**
     * Draws an arc bounded by the specified rectangle from startAngle to
     * endAngle. 0 degrees is at the 3-o'clock position.Positive arc
     * angles indicate counter-clockwise rotations, negative arc angles are
     * drawn clockwise. 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param startAngle the beginning angle
     * @param arcAngle the angle of the arc (relative to startAngle).
     * @see #fillArc
     */
    public abstract void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    /** 
     * Fills an arc using the current color. This generates a pie shape.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the arc
     * @param height the height of the arc
     * @param startAngle the beginning angle
     * @param arcAngle the angle of the arc (relative to startAngle).
     * @see #drawArc
     */
    public abstract void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);

    /** 
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #fillPolygon
     */
    public abstract void drawPolygon(int xPoints[], int yPoints[], int nPoints);

    /** 
     * Draws a polygon defined by the specified point.
     * @param p the specified polygon
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p) {
	drawPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Fills a polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolygon
     */
    public abstract void fillPolygon(int xPoints[], int yPoints[], int nPoints);

    /** 
     * Fills the specified polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param p the polygon
     * @see #drawPolygon
     */
    public void fillPolygon(Polygon p) {
	fillPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /** 
     * Draws the specified String using the current font and color.
     * The x,y position is the starting point of the baseline of the String.
     * @param str the String to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #drawChars
     * @see #drawBytes
     */
    public abstract void drawString(String str, int x, int y);

    /** 
     * Draws the specified characters using the current font and color.
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #drawString
     * @see #drawBytes
     */
    public void drawChars(char data[], int offset, int length, int x, int y) {
	drawString(new String(data, offset, length), x, y);
    }

    /** 
     * Draws the specified bytes using the current font and color.
     * @param data the data to be drawn
     * @param offset the start offset in the data
     * @param length the number of bytes that are drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @see #drawString
     * @see #drawChars
     */
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
	drawString(new String(data, 0, offset, length), x, y);
    }

    /** 
     * Draws the specified image at the specified coordinate (x, y). If the image is 
     * incomplete the image observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y, 
				      ImageObserver observer);

    /**
     * Draws the specified image inside the specified rectangle. The image is
     * scaled if necessary. If the image is incomplete the image observer will be
     * notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y,
				      int width, int height, 
				      ImageObserver observer);
    
    /** 
     * Draws the specified image at the specified coordinate (x, y),
     * with the given solid background Color.  If the image is 
     * incomplete the image observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y, 
				      Color bgcolor,
				      ImageObserver observer);

    /**
     * Draws the specified image inside the specified rectangle,
     * with the given solid background Color. The image is
     * scaled if necessary. If the image is incomplete the image
     * observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     */
    public abstract boolean drawImage(Image img, int x, int y,
				      int width, int height, 
				      Color bgcolor,
				      ImageObserver observer);
    
    /**
     * Disposes of this graphics context.  The Graphics context cannot be used after 
     * being disposed of.
     * @see #finalize
     */
    public abstract void dispose();

    /**
     * Disposes of this graphics context once it is no longer referenced.
     * @see #dispose
     */
    public void finalize() {
	dispose();
    }

    /**
     * Returns a String object representing this Graphic's value.
     */
    public String toString() {	
	return getClass().getName() + "[font=" + getFont() + ",color=" + getColor() + "]";
    }

}
