/*
 * @(#)PixelGrabber.java	1.3 95/12/06 Jim Graham
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

package java.awt.image;

import java.util.Hashtable;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.ColorModel;
import java.awt.Image;

/**
 * The PixelGrabber class implements an ImageConsumer which can be attached
 * to an Image or ImageProducer object to retrieve a subset of the pixels
 * in that image.  Here is an example:
 * <pre>
 *
 * public abstract void handlesinglepixel(int x, int y, int pixel);
 *
 * public void handlepixels(Image img, int x, int y, int w, int h) {
 *	int[] pixels = new int[w * h];
 *	PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
 *	try {
 *	    pg.grabPixels();
 *	} catch (InterruptedException e) {
 *	    System.err.println("interrupted waiting for pixels!");
 *	    return;
 *	}
 *	if ((pg.status() & ImageObserver.ABORT) != 0) {
 *	    System.err.println("image fetch aborted or errored");
 *	    return;
 *	}
 *	for (int j = 0; j < h; j++) {
 *	    for (int i = 0; i < w; i++) {
 *		handlesinglepixel(x+i, y+j, pixels[j * w + i]);
 *	    }
 *	}
 * }
 *
 * </pre>
 *
 * @version 	1.3, 12/06/95
 * @author 	Jim Graham
 */
public class PixelGrabber implements ImageConsumer {
    ImageProducer producer;

    int dstX;
    int dstY;
    int dstW;
    int dstH;

    int[] pixelbuf;
    int dstOff;
    int dstScan;

    private boolean grabbing;
    private int flags;

    private final int GRABBEDBITS = (ImageObserver.FRAMEBITS
				     | ImageObserver.ALLBITS);
    private final int DONEBITS = (GRABBEDBITS
				  | ImageObserver.ERROR);

    /**
     * Create a PixelGrabber object to grab the (x, y, w, h) rectangular
     * section of pixels from the specified image into the given array.
     * The pixels are stored into the array in the default RGB ColorModel.
     * The RGB data for pixel (i, j) where (i, j) is inside the rectangle
     * (x, y, w, h) is stored in the array at
     * <tt>pix[(j - y) * scansize + (i - x) + off]</tt>.
     * @see ColorModel#getRGBdefault
     * @param img the image to retrieve pixels from
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image, relative to the default
     * (unscaled) size of the image
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image
     * @param w the width of the rectangle of pixels to retrieve
     * @param h the height of the rectangle of pixels to retrieve
     * @param pix the array of integers which are to be used to hold the
     * RGB pixels retrieved from the image
     * @param off the offset into the array of where to store the first pixel
     * @param scansize the distance from one row of pixels to the next in
     * the array
     */
    public PixelGrabber(Image img, int x, int y, int w, int h,
			int[] pix, int off, int scansize) {
	this(img.getSource(), x, y, w, h, pix, off, scansize);
    }

    /**
     * Create a PixelGrabber object to grab the (x, y, w, h) rectangular
     * section of pixels from the image produced by the specified
     * ImageProducer into the given array.
     * The pixels are stored into the array in the default RGB ColorModel.
     * The RGB data for pixel (i, j) where (i, j) is inside the rectangle
     * (x, y, w, h) is stored in the array at
     * <tt>pix[(j - y) * scansize + (i - x) + off]</tt>.
     * @see ColorModel#getRGBdefault
     * @param img the image to retrieve pixels from
     * @param x the x coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image, relative to the default
     * (unscaled) size of the image
     * @param y the y coordinate of the upper left corner of the rectangle
     * of pixels to retrieve from the image
     * @param w the width of the rectangle of pixels to retrieve
     * @param h the height of the rectangle of pixels to retrieve
     * @param pix the array of integers which are to be used to hold the
     * RGB pixels retrieved from the image
     * @param off the offset into the array of where to store the first pixel
     * @param scansize the distance from one row of pixels to the next in
     * the array
     */
    public PixelGrabber(ImageProducer ip, int x, int y, int w, int h,
			int[] pix, int off, int scansize) {
	producer = ip;
	dstX = x;
	dstY = y;
	dstW = w;
	dstH = h;
	dstOff = off;
	dstScan = scansize;
	pixelbuf = pix;
    }

    /**
     * Request the Image or ImageProducer to start delivering pixels and
     * wait for all of the pixels in the rectangle of interest to be
     * delivered.
     * @return true if the pixels were successfully grabbed, false on
     * abort, error or timeout
     * @exception InterruptedException 
     *            Another thread has interrupted this thread.
     */
    public boolean grabPixels() throws InterruptedException {
	return grabPixels(0);
    }

    /**
     * Request the Image or ImageProducer to start delivering pixels and
     * wait for all of the pixels in the rectangle of interest to be
     * delivered or until the specified timeout has elapsed.
     * @param ms the number of milliseconds to wait for the image pixels
     * to arrive before timing out
     * @return true if the pixels were successfully grabbed, false on
     * abort, error or timeout
     * @exception InterruptedException 
     *            Another thread has interrupted this thread. 
     */
    public synchronized boolean grabPixels(long ms)
	throws InterruptedException
    {
	if ((flags & DONEBITS) != 0) {
	    return (flags & GRABBEDBITS) != 0;
	}
	long end = ms + System.currentTimeMillis();
	if (!grabbing) {
	    producer.startProduction(this);
	    grabbing = true;
	    flags &= ~(ImageObserver.ABORT);
	}
	while (grabbing) {
	    long timeout;
	    if (ms == 0) {
		timeout = 0;
	    } else {
		timeout = end - System.currentTimeMillis();
		if (timeout <= 0) {
		    break;
		}
	    }
	    wait(timeout);
	}
	return (flags & GRABBEDBITS) != 0;
    }

    /**
     * Return the status of the pixels.  The ImageObserver flags
     * representing the available pixel information are returned.
     * @see ImageObserver
     * @return the bitwise OR of all relevant ImageObserver flags
     */
    public synchronized int status() {
	return flags;
    }

    /**
     * The setDimensions method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setDimensions(int width, int height) {
	return;
    }

    /**
     * The setHints method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setHints(int hints) {
	return;
    }

    /**
     * The setProperties method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setProperties(Hashtable props) {
	return;
    }

    /**
     * The setColorModel method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setColorModel(ColorModel model) {
	return;
    }

    /**
     * The setPixels method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setPixels(int srcX, int srcY, int srcW, int srcH,
			  ColorModel model,
			  byte pixels[], int srcOff, int srcScan) {
	if (srcY < dstY) {
	    int diff = dstY - srcY;
	    if (diff >= srcH) {
		return;
	    }
	    srcOff += srcScan * diff;
	    srcY += diff;
	    srcH -= diff;
	}
	if (srcY + srcH > dstY + dstH) {
	    srcH = (dstY + dstH) - srcY;
	    if (srcH <= 0) {
		return;
	    }
	}
	if (srcX < dstX) {
	    int diff = dstX - srcX;
	    if (diff >= srcW) {
		return;
	    }
	    srcOff += diff;
	    srcX += diff;
	    srcW -= diff;
	}
	if (srcX + srcW > dstX + dstW) {
	    srcW = (dstX + dstW) - srcX;
	    if (srcW <= 0) {
		return;
	    }
	}
	int dstPtr = dstOff + (srcY - dstY) * dstScan + (srcX - dstX);
	int dstRem = dstScan - dstW;
	int srcRem = srcScan - srcW;
	for (int h = srcH; h > 0; h--) {
	    for (int w = srcW; w > 0; w--) {
		pixelbuf[dstPtr++] = model.getRGB(pixels[srcOff++] & 0xff);
	    }
	    srcOff += srcRem;
	    dstPtr += dstRem;
	}
	flags |= ImageObserver.SOMEBITS;
    }

    /**
     * The setPixels method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public void setPixels(int srcX, int srcY, int srcW, int srcH,
			  ColorModel model,
			  int pixels[], int srcOff, int srcScan) {
	if (srcY < dstY) {
	    int diff = dstY - srcY;
	    if (diff >= srcH) {
		return;
	    }
	    srcOff += srcScan * diff;
	    srcY += diff;
	    srcH -= diff;
	}
	if (srcY + srcH > dstY + dstH) {
	    srcH = (dstY + dstH) - srcY;
	    if (srcH <= 0) {
		return;
	    }
	}
	if (srcX < dstX) {
	    int diff = dstX - srcX;
	    if (diff >= srcW) {
		return;
	    }
	    srcOff += diff;
	    srcX += diff;
	    srcW -= diff;
	}
	if (srcX + srcW > dstX + dstW) {
	    srcW = (dstX + dstW) - srcX;
	    if (srcW <= 0) {
		return;
	    }
	}
	int dstPtr = dstOff + (srcY - dstY) * dstScan + (srcX - dstX);
	int dstRem = dstScan - dstW;
	int srcRem = srcScan - srcW;
	for (int h = srcH; h > 0; h--) {
	    for (int w = srcW; w > 0; w--) {
		pixelbuf[dstPtr++] = model.getRGB(pixels[srcOff++]);
	    }
	    srcOff += srcRem;
	    dstPtr += dstRem;
	}
	flags |= ImageObserver.SOMEBITS;
    }

    /**
     * The imageComplete method is part of the ImageConsumer API which
     * this class must implement to retrieve the pixels.
     */
    public synchronized void imageComplete(int status) {
	grabbing = false;
	switch (status) {
	default:
	case IMAGEERROR:
	    flags |= ImageObserver.ERROR | ImageObserver.ABORT;
	    break;
	case IMAGEABORTED:
	    flags |= ImageObserver.ABORT;
	    break;
	case STATICIMAGEDONE:
	    flags |= ImageObserver.ALLBITS;
	    break;
	case SINGLEFRAMEDONE:
	    flags |= ImageObserver.FRAMEBITS;
	    break;
	}
	producer.removeConsumer(this);
	notifyAll();
    }
}
