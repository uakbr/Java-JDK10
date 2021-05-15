/*
 * @(#)IndexColorModel.java	1.12 95/12/14 Jim Graham
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

package java.awt.image;

/**
 * A ColorModel class that specifies a translation from pixel values
 * to alpha, red, green, and blue color components for pixels which
 * represent indices into a fixed colormap.  An optional transparent
 * pixel value can be supplied which indicates a completely transparent
 * pixel, regardless of any alpha value recorded for that pixel value.
 * This color model is similar to an X11 PseudoColor visual.
 * <p>Many of the methods in this class are final.  The reason for
 * this is that the underlying native graphics code makes assumptions
 * about the layout and operation of this class and those assumptions
 * are reflected in the implementations of the methods here that are
 * marked final.  You can subclass this class for other reaons, but
 * you cannot override or modify the behaviour of those methods.
 *
 * @see ColorModel
 *
 * @version	1.12 12/14/95
 * @author 	Jim Graham
 */
public class IndexColorModel extends ColorModel {
    private byte red[];
    private byte green[];
    private byte blue[];
    private byte alpha[];
    private int map_size;

    private int transparent_index;

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, and blue components.  Pixels described by this color
     * model will all have alpha components of 255 (fully opaque).
     * All of the arrays specifying the color components must have
     * at least the specified number of entries.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[]) {
	this(bits, size, r, g, b, -1);
    }

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, and blue components.  Pixels described by this color
     * model will all have alpha components of 255 (fully opaque),
     * except for the indicated transparent pixel.  All of the arrays
     * specifying the color components must have at least the specified
     * number of entries.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     * @param trans	The index of the transparent pixel.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], int trans) {
	super(bits);
	if ((bits > 8) || (size > (1 << bits))) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	map_size = size;
	red = new byte[256];
	System.arraycopy(r, 0, red, 0, size);
	green = new byte[256];
	System.arraycopy(g, 0, green, 0, size);
	blue = new byte[256];
	System.arraycopy(b, 0, blue, 0, size);
	setTransparentPixel(trans);
    }

    /**
     * Constructs an IndexColorModel from the given arrays of red,
     * green, blue and alpha components.  All of the arrays specifying
     * the color components must have at least the specified number
     * of entries.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param r		The array of red color components.
     * @param g		The array of green color components.
     * @param b		The array of blue color components.
     * @param a		The array of alpha value components.
     */
    public IndexColorModel(int bits, int size,
			   byte r[], byte g[], byte b[], byte a[]) {
	this(bits, size, r, g, b, -1);
	alpha = new byte[256];
	System.arraycopy(a, 0, alpha, 0, size);
    }

    /**
     * Constructs an IndexColorModel from a single arrays of packed
     * red, green, blue and optional alpha components.  The array
     * must have enough values in it to fill all of the needed
     * component arrays of the specified size.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param cmap	The array of color components.
     * @param start	The starting offset of the first color component.
     * @param hasalpha	Indicates whether alpha values are contained in
     *			the cmap array.
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha) {
	this(bits, size, cmap, start, hasalpha, -1);
    }

    /**
     * Constructs an IndexColorModel from a single arrays of packed
     * red, green, blue and optional alpha components.  The specified
     * transparent index represents a pixel which will be considered
     * entirely transparent regardless of any alpha value specified
     * for it.  The array must have enough values in it to fill all
     * of the needed component arrays of the specified size.
     * @param bits	The number of bits each pixel occupies.
     * @param size	The size of the color component arrays.
     * @param cmap	The array of color components.
     * @param start	The starting offset of the first color component.
     * @param hasalpha	Indicates whether alpha values are contained in
     *			the cmap array.
     * @param trans	The index of the fully transparent pixel.
     */
    public IndexColorModel(int bits, int size, byte cmap[], int start,
			   boolean hasalpha, int trans) {
	// REMIND: This assumes the ordering: RGB[A]
	super(bits);
	if ((bits > 8) || (size > (1 << bits))) {
	    throw new ArrayIndexOutOfBoundsException();
	}
	map_size = size;
	red = new byte[256];
	green = new byte[256];
	blue = new byte[256];
	if (hasalpha) {
	    alpha = new byte[256];
	}
	int j = start;
	for (int i = 0; i < size; i++) {
	    red[i] = cmap[j++];
	    green[i] = cmap[j++];
	    blue[i] = cmap[j++];
	    if (hasalpha) {
		alpha[i] = cmap[j++];
	    }
	}
	setTransparentPixel(trans);
    }

    /**
     * Returns the size of the color component arrays in this IndexColorModel.
     */
    final public int getMapSize() {
	return map_size;
    }

    /**
     * Returns the index of the transparent pixel in this IndexColorModel
     * or -1 if there is no transparent pixel.
     */
    final public int getTransparentPixel() {
	return transparent_index;
    }

    /**
     * Copies the array of red color components into the given array.  Only
     * the initial entries of the array as specified by getMapSize() are
     * written.
     */
    final public void getReds(byte r[]) {
	System.arraycopy(red, 0, r, 0, map_size);
    }

    /**
     * Copies the array of green color components into the given array.  Only
     * the initial entries of the array as specified by getMapSize() are
     *  written.
     */
    final public void getGreens(byte g[]) {
	System.arraycopy(green, 0, g, 0, map_size);
    }

    /**
     * Copies the array of blue color components into the given array.  Only
     * the initial entries of the array as specified by getMapSize() will
     * be written.
     */
    final public void getBlues(byte b[]) {
	System.arraycopy(blue, 0, b, 0, map_size);
    }

    /**
     * Copies the array of alpha transparency values into the given array.  Only
     * the initial entries of the array as specified by getMapSize() will
     * be written.
     */
    final public void getAlphas(byte a[]) {
	if (alpha != null) {
	    System.arraycopy(alpha, 0, a, 0, map_size);
	} else {
	    for (int i = 0; i < map_size; i++) {
		a[i] = (byte) 255;
	    }
	    if (transparent_index >= 0) {
		a[transparent_index] = 0;
	    }
	}
    }

    private void setTransparentPixel(int trans) {
	if (trans >= map_size || trans < 0) {
	    trans = -1;
	} else if (alpha != null) {
	    alpha[trans] = 0;
	}
	transparent_index = trans;
    }

    /**
     * Returns the red color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getRed(int pixel) {
	return red[pixel];
    }

    /**
     * Returns the green color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getGreen(int pixel) {
	return green[pixel];
    }

    /**
     * Returns the blue color compoment for the specified pixel in the
     * range 0-255.
     */
    final public int getBlue(int pixel) {
	return blue[pixel];
    }

    /**
     * Returns the alpha transparency value for the specified pixel in the
     * range 0-255.
     */
    final public int getAlpha(int pixel) {
	return ((pixel == transparent_index) ? 0 : ((alpha != null)
						    ? alpha[pixel]
						    : 255));
    }

    /**
     * Returns the color of the pixel in the default RGB color model.
     * @see ColorModel#getRGBdefault
     */
    final public int getRGB(int pixel) {
	return (getAlpha(pixel) << 24)
	    | ((red[pixel] & 0xff) << 16)
	    | ((green[pixel] & 0xff) << 8)
	    | (blue[pixel] & 0xff);
    }
}
