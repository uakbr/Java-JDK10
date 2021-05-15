/*
 * @(#)MemoryImageSource.java	1.15 95/12/14 Jim Graham
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

import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.util.Hashtable;

/**
 * This class is an implementation of the ImageProducer interface which
 * uses an array to produce pixel values for an Image.  Here is an example
 * which calculates a 100x100 image representing a fade from black to blue
 * along the X axis and a fade from black to red along the Y axis:
 * <pre>
 * 
 *	int w = 100;
 *	int h = 100;
 *	int pix[] = new int[w * h];
 *	int index = 0;
 *	for (int y = 0; y < h; y++) {
 *	    int red = (y * 255) / (h - 1);
 *	    for (int x = 0; x < w; x++) {
 *		int blue = (x * 255) / (w - 1);
 *		pix[index++] = (255 << 24) | (red << 16) | blue;
 *	    }
 *	}
 *	Image img = createImage(new MemoryImageSource(w, h, pix, 0, w));
 * 
 * </pre>
 *
 * @see ImageProducer
 *
 * @version	1.15 12/14/95
 * @author 	Jim Graham
 */
public class MemoryImageSource implements ImageProducer {
    int width;
    int height;
    ColorModel model;
    Object pixels;
    int pixeloffset;
    int pixelscan;
    Hashtable properties;

    /**
     * Constructs an ImageProducer object which uses an array of bytes
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     byte[] pix, int off, int scan) {
	initialize(w, h, cm, (Object) pix, off, scan, null);
    }

    /**
     * Constructs an ImageProducer object which uses an array of bytes
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     byte[] pix, int off, int scan, Hashtable props) {
	initialize(w, h, cm, (Object) pix, off, scan, props);
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     int[] pix, int off, int scan) {
	initialize(w, h, cm, (Object) pix, off, scan, null);
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * to produce data for an Image object.
     * @see java.awt.Component#createImage
     */
    public MemoryImageSource(int w, int h, ColorModel cm,
			     int[] pix, int off, int scan, Hashtable props) {
	initialize(w, h, cm, (Object) pix, off, scan, props);
    }

    private void initialize(int w, int h, ColorModel cm,
			    Object pix, int off, int scan, Hashtable props) {
	width = w;
	height = h;
	model = cm;
	pixels = pix;
	pixeloffset = off;
	pixelscan = scan;
	if (props == null) {
	    props = new Hashtable();
	}
	properties = props;
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * in the default RGB ColorModel to produce data for an Image object.
     * @see java.awt.Component#createImage
     * @see ColorModel#getRGBdefault
     */
    public MemoryImageSource(int w, int h, int pix[], int off, int scan) {
	initialize(w, h, ColorModel.getRGBdefault(),
		   (Object) pix, off, scan, null);
    }

    /**
     * Constructs an ImageProducer object which uses an array of integers
     * in the default RGB ColorModel to produce data for an Image object.
     * @see java.awt.Component#createImage
     * @see ColorModel#getRGBdefault
     */
    public MemoryImageSource(int w, int h, int pix[], int off, int scan,
			     Hashtable props) {
	initialize(w, h, ColorModel.getRGBdefault(),
		   (Object) pix, off, scan, props);
    }

    // We can only have one consumer since we immediately return the data...
    private ImageConsumer theConsumer;

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void addConsumer(ImageConsumer ic) {
	theConsumer = ic;
	try {
	    produce();
	} catch (Exception e) {
	    if (theConsumer != null) {
		theConsumer.imageComplete(ImageConsumer.IMAGEERROR);
	    }
	}
	theConsumer = null;
    }

    /**
     * Determine if an ImageConsumer is on the list of consumers currently
     * interested in data for this image.
     * @return true if the ImageConsumer is on the list; false otherwise
     * @see ImageConsumer
     */
    public synchronized boolean isConsumer(ImageConsumer ic) {
	return (ic == theConsumer);
    }

    /**
     * Remove an ImageConsumer from the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void removeConsumer(ImageConsumer ic) {
	if (theConsumer == ic) {
	    theConsumer = null;
	}
    }

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image, and immediately start delivery of the
     * image data through the ImageConsumer interface.
     * @see ImageConsumer
     */
    public void startProduction(ImageConsumer ic) {
	addConsumer(ic);
    }

    /**
     * Requests that a given ImageConsumer have the image data delivered
     * one more time in top-down, left-right order.
     * @see ImageConsumer
     */
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
	// Not needed.  The data is always in TDLR format.
    }

    private void produce() {
	if (theConsumer != null) {
	    theConsumer.setDimensions(width, height);
	}
	if (theConsumer != null) {
	    theConsumer.setProperties(properties);
	}
	if (theConsumer != null) {
	    theConsumer.setColorModel(model);
	}
	if (theConsumer != null) {
	    theConsumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT |
				 ImageConsumer.COMPLETESCANLINES |
				 ImageConsumer.SINGLEPASS |
				 ImageConsumer.SINGLEFRAME);
	}
	if (theConsumer != null) {
	    if (pixels instanceof byte[]) {
		theConsumer.setPixels(0, 0, width, height, model,
				      ((byte[]) pixels), pixeloffset,
				      pixelscan);
	    } else {
		theConsumer.setPixels(0, 0, width, height, model,
				      ((int[]) pixels), pixeloffset,
				      pixelscan);
	    }
	}
	if (theConsumer != null) {
	    theConsumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
	}
    }
}
