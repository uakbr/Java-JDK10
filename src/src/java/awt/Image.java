/*
 * @(#)Image.java	1.16 95/12/14 Sami Shaio
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

import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;

/**
 * The image class is an abstract class. The image must be obtained in a 
 * platform specific way.
 *
 * @version 	1.16, 12/14/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public abstract class Image {
    /**
     * Gets the actual width of the image.  If the width is not known
     * yet then the ImageObserver will be notified later and -1 will
     * be returned.
     * @see #getHeight
     * @see ImageObserver
     */
    public abstract int getWidth(ImageObserver observer);

    /**
     * Gets the actual height of the image.  If the height is not known
     * yet then the ImageObserver will be notified later and -1 will
     * be returned.
     * @see #getWidth
     * @see ImageObserver
     */
    public abstract int getHeight(ImageObserver observer);

    /**
     * Gets the object that produces the pixels for the image.
     * This is used by the Image filtering classes and by the
     * image conversion and scaling code.
     * @see ImageProducer
     */
    public abstract ImageProducer getSource();

    /**
     * Gets a graphics object to draw into this image.
     * This will only work for off-screen images.
     * @see Graphics
     */
    public abstract Graphics getGraphics();

    /**
     * Gets a property of the image by name.  Individual property names
     * are defined by the various image formats.  If a property is not
     * defined for a particular image, this method will return the
     * UndefinedProperty object.  If the properties for this image are
     * not yet known, then this method will return null and the ImageObserver
     * object will be notified later.  The property name "comment" should
     * be used to store an optional comment which can be presented to
     * the user as a description of the image, its source, or its author.
     * @see ImageObserver
     * @see #UndefinedProperty
     */
    public abstract Object getProperty(String name, ImageObserver observer);

    /**
     * The UndefinedProperty object should be returned whenever a
     * property which was not defined for a particular image is
     * fetched.
     */
    public static final Object UndefinedProperty = new Object();

    /**
     * Flushes all resources being used by this Image object.  This
     * includes any pixel data that is being cached for rendering to
     * the screen as well as any system resources that are being used
     * to store data or pixels for the image.  The image is reset to
     * a state similar to when it was first created so that if it is
     * again rendered, the image data will have to be recreated or
     * fetched again from its source.
     */
    public abstract void flush();
}
