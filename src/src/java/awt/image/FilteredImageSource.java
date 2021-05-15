/*
 * @(#)FilteredImageSource.java	1.15 95/12/14 Jim Graham
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

import java.awt.Image;
import java.awt.image.ImageFilter;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.Hashtable;
import java.awt.image.ColorModel;

/**
 * This class is an implementation of the ImageProducer interface which
 * takes an existing image and a filter object and uses them to produce
 * image data for a new filtered version of the original image.
 * Here is an example which filters an image by swapping the red and
 * blue compents:
 * <pre>
 * 
 *	Image src = getImage("doc:///demo/images/duke/T1.gif");
 *	ImageFilter colorfilter = new RedBlueSwapFilter();
 *	Image img = createImage(new FilteredImageSource(src.getSource(),
 *							colorfilter));
 * 
 * </pre>
 *
 * @see ImageProducer
 *
 * @version	1.15 12/14/95
 * @author 	Jim Graham
 */
public class FilteredImageSource implements ImageProducer {
    ImageProducer src;
    ImageFilter filter;

    /**
     * Constructs an ImageProducer object from an existing ImageProducer
     * and a filter object.
     * @see ImageFilter
     * @see java.awt.Component#createImage
     */
    public FilteredImageSource(ImageProducer orig, ImageFilter imgf) {
	src = orig;
	filter = imgf;
    }

    private Hashtable proxies;

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void addConsumer(ImageConsumer ic) {
	if (proxies == null) {
	    proxies = new Hashtable();
	}
	if (!proxies.contains(ic)) {
	    ImageFilter imgf = filter.getFilterInstance(ic);
	    proxies.put(ic, imgf);
	    src.addConsumer(imgf);
	}
    }

    /**
     * Determines whether an ImageConsumer is on the list of consumers 
     * currently interested in data for this image.
     * @return true if the ImageConsumer is on the list; false otherwise
     * @see ImageConsumer
     */
    public synchronized boolean isConsumer(ImageConsumer ic) {
	return (proxies != null && proxies.contains(ic));
    }

    /**
     * Removes an ImageConsumer from the list of consumers interested in
     * data for this image.
     * @see ImageConsumer
     */
    public synchronized void removeConsumer(ImageConsumer ic) {
	if (proxies != null) {
	    ImageFilter imgf = (ImageFilter) proxies.get(ic);
	    if (imgf != null) {
		src.removeConsumer(imgf);
		proxies.remove(ic);
		if (proxies.isEmpty()) {
		    proxies = null;
		}
	    }
	}
    }

    /**
     * Adds an ImageConsumer to the list of consumers interested in
     * data for this image, and immediately starts delivery of the
     * image data through the ImageConsumer interface.
     * @see ImageConsumer
     */
    public void startProduction(ImageConsumer ic) {
	if (proxies == null) {
	    proxies = new Hashtable();
	}
	ImageFilter imgf = (ImageFilter) proxies.get(ic);
	if (imgf == null) {
	    imgf = filter.getFilterInstance(ic);
	    proxies.put(ic, imgf);
	}
	src.startProduction(imgf);
    }

    /**
     * Requests that a given ImageConsumer have the image data delivered
     * one more time in top-down, left-right order.  The request is
     * handed to the ImageFilter for further processing, since the
     * ability to preserve the pixel ordering depends on the filter.
     * @see ImageConsumer
     */
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
	if (proxies != null) {
	    ImageFilter imgf = (ImageFilter) proxies.get(ic);
	    if (imgf != null) {
		imgf.resendTopDownLeftRight(src);
	    }
	}
    }
}
