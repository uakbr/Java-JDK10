/*
 * @(#)DIBitmap.java	1.9 95/03/14 Patrick Naughton
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

/*-
 *	Device Independant Bitmap representation.
 */

package awt;

import browser.Observable;

/**
 * This class holds a device independant 8 bit deep image and its colormap.
 *
 * @version 1.9 14 Mar 1995
 * @author Patrick Naughton
 */
public class DIBitmap extends Observable {
    public int width;
    public int height;
    public int num_colors;
    public byte red[];
    public byte green[];
    public byte blue[];
    public byte raster[];
    public int trans_index;
    public int generation;

    /**
     * This constructor is dangerous in that it creates a DIBitmap with
     * invalid fields. However, it is needed by subclassers who cannot
     * provide the necessary information at construction time, since they are
     * reading the image from a file, for example.
     */

    public DIBitmap () {
    }

    /**
     * This constructor takes the passed in raster as the correct bits for an
     * image.
     */

    public DIBitmap (int w, int h, int n,
	    byte r[], byte g[], byte b[], byte d[]) {

	if (w <= 0 || h <= 0 ||
		r == null || r.length != n ||
		g == null || g.length != n ||
		b == null || b.length != n ||
		d == null || d.length != w * h)
	    throw new DataFormatException();

	width = w;
	height = h;
	num_colors = n;
	red = r;
	green = g;
	blue = b;
	raster = d;
    }

    /**
     * This constructor allocates the space for an image of the specified
     * size.  The contents of the image and colormap are undefined.
     */

    public DIBitmap (int w, int h) {
	this(w, h, 256,
	     new byte[256],
	     new byte[256],
	     new byte[256],
	     new byte[w * h]);
    }

    /**
     * Touches this bitmap, ie, if it is cached anywhere it
     * must be reloaded.
     */
    public void touch() {
	generation++;
    }
}

