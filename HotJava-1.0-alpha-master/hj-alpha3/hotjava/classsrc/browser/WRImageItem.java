/*
 * @(#)WRImageItem.java	1.62 95/04/26 Jonathan Payne
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
package browser;

import awt.*;
import net.www.html.*;
import java.util.Hashtable;
import java.io.InputStream;
import java.io.FileOutputStream;

/**
 * Class WRImageItem is the display item for all images that appear
 * in HotJava documents.  It handles alignment queries, delayed
 * image loading, image caching and following hypertext links if the
 * image is part of an anchor.
 * Instances of this class are observers of instances of
 * ImageHandle.  That way this display item will get notified
 * when the ImageHandle finishes reading an image across the net.
 * @version 1.62, 26 Apr 1995
 * @author Jonathan Payne
 * @author Chris Warth
 */

public class WRImageItem extends ImageDisplayItem implements Alignable, Observer {
    static Hashtable	alignHT = new Hashtable();

    /**
     * "Image Delayed" icon.
     */
    static public Image imageDelayedImage = null;

    /**
     * "Image Failed" icon.
     */
    static public Image imageFailedImage = null;

    /**
     * "Image Loading" icon.
     */
    static public Image imageLoadingImage = null;

    /**
     * "Image Ref Delayed" icon.
     */
    static public Image imageRefDelayedImage = null;

    /**
     * "Image Ref Failed" icon.
     */
    static public Image imageRefFailedImage = null;
    
    static {
	alignHT.put("top", 	new Integer(A_TOP));
	alignHT.put("texttop", 	new Integer(A_TEXTTOP));
	alignHT.put("middle", 	new Integer(A_MIDDLE));
	alignHT.put("absmiddle",new Integer(A_ABSMIDDLE));
	alignHT.put("bottom", 	new Integer(A_BOTTOM));
	alignHT.put("baseline", new Integer(A_BASELINE));
	alignHT.put("absbottom",new Integer(A_ABSBOTTOM));
    }

    public static int convertAlign(String align) {
	if (align == null) {
	    return A_BOTTOM;
	}
	Integer v = (Integer)alignHT.get(align);
	if (v != null) {
	    return v.intValue();
	}
	v = (Integer)alignHT.get(align.toLowerCase());
	return (v != null) ? v.intValue() : A_BASELINE;
    }

    Image delayImage;
    int	    align;
    Color normalColor;
    Color highlightColor;
    String error;
    protected boolean	paintImage;
    protected TagRef	imgTag;
    protected URL	imageUrl;
    protected URL	anchorUrl;
    protected boolean	ismap;
    protected byte	borderThickness = 0;
    URL			documentURL;
    protected int	tagWidth = -1;
    protected int	tagHeight = -1;

    /**
     * Creates a new WRImageItem in the specified WRWindow.  The
     * image source is retrieved from the specified TagRef which
     * must contain a "src" attribute.  If this image contains a
     * reference to another URL, then that is specified by the href
     * argument.
     */
    public WRImageItem (WRWindow w, TagRef imgTag, String href) {
	super(null);

	documentURL = w.document().url();
	imageUrl = getImageUrl(documentURL, w, imgTag);
	this.imgTag = imgTag;
	setAnchor(href);

	Image	img = null;
	int wd = 38;		// the dimensions of the load-status icons
	int ht = 38;
	String	attr;

	if ((attr = imgTag.getAttribute("width")) != null) {
	    try {
		tagWidth = wd = Integer.parseInt(attr);
	    } catch (NumberFormatException ee) {
	    }
	}
	if ((attr = imgTag.getAttribute("height")) != null) {
	    try {
		tagHeight = ht = Integer.parseInt(attr);
	    } catch (NumberFormatException ee) {
	    }
	}
	resizeFromImageSize(wd, ht);

	align = convertAlign(imgTag.getAttribute("align"));
	highlightColor = Color.red;

	ismap = imgTag.getAttribute("ismap") != null;

	checkImage(w);
    }

    public void nuke() {
	if (imageUrl != null) {
	    ImageCache.flushHandle(imageUrl);
	    ImageCache.flushHandle(imageUrl, tagWidth, tagHeight);
	}
    }

    public void checkImage(Window w) {
	if (error != null) {
	    nuke();
	}
	setImage((Image)null);
	error = null;
	if (imageUrl != null) {
	    Object o;
	    ImageHandle h = ImageCache.lookupHandle(w, imageUrl,
						    tagWidth, tagHeight);
	    if (WRWindow.delayImageLoading) {
		o = h.checkForImage(this);
	    } else {
		o = h.getImage(this, false);
	    }
	    if (o != null) {
		if (o instanceof Image) {
		    setImage((Image) o);
		} else if (o != null) {
		    error  = (String) o;
		}
		imageSizeKnown = true;
	    }
	}

	maybeInitLoadIcons(w);
    }

    private void maybeInitLoadIcons(Window w) {
	if (imageDelayedImage == null) {
	    final String base = "doc:demo/images/";
	    
	    imageDelayedImage = getLoadIcon(w, base+"image-delayed.gif");
	    imageFailedImage = getLoadIcon(w, base+"image-failed.gif");
	    imageLoadingImage = getLoadIcon(w, base+"image-loading.gif");
	    imageRefDelayedImage = getLoadIcon(w,
					       base+"image-ref-delayed.gif");
	    imageRefFailedImage = getLoadIcon(w, base+"image-ref-failed.gif");
	}
    }

    public static URL getImageUrl(URL context, WRWindow w, TagRef ref) {
	String	imageSrc = ref.getAttribute("src");
	URL	imageUrl = null;

	if (imageSrc != null) {
	    try {
		imageUrl = new URL(context, imageSrc);
	    } catch (MalformedURLException e) {
		System.out.println("Cannot build url for image " + imageSrc +
				   " in document " + context.toExternalForm());
	    }
	}
	return imageUrl;
    }

    public static void kickImage(WRWindow w, ImgTagRef ref) {
	URL imageUrl = getImageUrl(w.document().url(), w, ref);
	String	attr;
	int wd = -1, ht = -1;

	if ((attr = ref.getAttribute("width")) != null) {
	    try {
		wd = Integer.parseInt(attr);
	    } catch (NumberFormatException ee) {
	    }
	}
	if ((attr = ref.getAttribute("height")) != null) {
	    try {
		ht = Integer.parseInt(attr);
	    } catch (NumberFormatException ee) {
	    }
	}

	if (imageUrl != null) {
	    ImageHandle	ih = ImageCache.lookupHandle(w, imageUrl, wd, ht);
	    if (ref.di != null) {
		((WRImageItem)ref.di).painted = false;
	    }
	    Object o = ih.getImage((Observer)ref.di, false);
	    if ((o != null) && (ref.di != null)) {
		if (o instanceof Image) {
		    ((WRImageItem)ref.di).setImage((Image)o);
		} else {
		    ((WRImageItem)ref.di).error = (String)o;
		}
		((WRImageItem)ref.di).imageSizeKnown = true;
	    }
	}
    }

    public void setAnchor(String href) {
	/*
	 * Now get the URL out of the anchor, if we have one.  This
	 * needs to be done before we set the image, because it keys
	 * off of anchorUrl to decide in its size (i.e., whether to
	 * make room for an anchor border).
	 */
	if (href != null) {
	    anchorUrl = new URL(documentURL, href);
	    setColorFromUrl(anchorUrl);
	} else {
	    setColor(Color.black);
	}
	initializeBorderThickness();
    }

    boolean setColorFromUrl(URL url) {
	Color	c = fgColor;

	if (hotjava.history.seen(url)) {
	    setColor(hotjava.visitedAnchorColor);
	} else {
	    setColor(hotjava.anchorColor);
	}
	return c != fgColor;
    }

    /**
     * Get alignment for formatting.
     */
    public int getAlign() {
	return align;
    }

    static private Image getLoadIcon(Window w, String name) {
	URL imgURL = new URL(null, name);
	InputStream iStream = null;
	
	try {
	    iStream = imgURL.openStream();
	} catch (Exception e) { }

	if (iStream == null) {
	    return null;
	} else {
	    GifImage gif = new GifImage(iStream, null);
	    return w.createImage(gif);
	}
    }
    
    protected void initializeBorderThickness() {
	if (anchorUrl != null) {
	    borderThickness = 2;
	    try {
		String	border = imgTag.getAttribute("border");

		if (border != null) {
		    borderThickness = (byte) Integer.parseInt(border);
		}
		if (borderThickness < 0) {
		    borderThickness = 0;
		}
	    } catch (NumberFormatException e) {
		borderThickness = 2;
	    }
	}
    }

    public void resizeFromImage(Image img) {
	if (img != null) {
	    resizeFromImageSize(img.width, img.height);
	}
    }

    public void resizeFromImageSize(int w, int h) {
	int incr = borderThickness * 2;

	if (tagWidth > 0)
	    w = tagWidth;
	if (tagHeight > 0)
	    h = tagHeight;

	resize(w + incr, h + incr);
    }

    boolean painted = false;

    /**
     * Draw the image if it's not null and if more than half its height
     * and width will be visible.
     */
    static private final void maybeDrawImage(Window w, Image i, int x, int y,
					     int inWidth, int inHeight) {
	if (i != null && (i.width / 2) < inWidth && (i.height / 2) < inHeight) {
	    w.drawImage(i, x, y);
	}
    }
    
    public synchronized void paint(Window w, int x, int y) {
	try {
	    if (pic == null || borderThickness > 0) {
		drawBorder(w, x, y);
	    }

	    if (pic == null) {
		// use drawBorder's own logic against it!
		int offset = (borderThickness == 0) ? 2 : borderThickness;
		w.clipRect(x + offset, y + offset,
			   width - 2 * offset, height - 2 * offset);

		if (!WRWindow.delayImageLoading && error == null) {
		    w.setForeground(Color.lightGray);
		    w.fillRect(x + offset, y + offset,
			       width - offset * 2, height - offset * 2);
		    maybeDrawImage(w, imageLoadingImage, x + offset, y + offset,
				   width, height);
		} else {
		    w.setForeground((error == null) ? Color.yellow : Color.red);
		    if (width < offset * 2 || height < offset * 2) {
			w.clearClip();
			w.fillRect(x, y, width, height);
		    } else {
			w.fillRect(x + offset, y + offset,
				   width - offset * 2, height - offset * 2);
		    }

		    if ((anchorUrl == null) || ismap) {
			maybeDrawImage(w, (error == null) ? imageDelayedImage :
				       imageFailedImage, x + offset, y + offset,
				       width, height);
		    } else {
			maybeDrawImage(w, (error == null) ?
				       imageRefDelayedImage :
				       imageRefFailedImage,
				       x + offset, y + offset, width, height);
		    }
		}
	    } else {
		super.paint(w, x + borderThickness, y + borderThickness);
	    }
	    painted = true;
	    full = false;
	} finally {
	    w.clearClip();
	}
    }

    void drawBorder(Window w, int x, int y) {
	if (pic == null && borderThickness == 0) {
	    if (width >= 2 && height >= 2) {
		w.paint3DRect(x, y, width, height, false, true);
	    }
	} else {
	    int b = borderThickness;

	    w.setForeground(fgColor);
	    w.fillRect(x, y, width, b);
	    w.fillRect(x, y + height - b, width, b);
	    w.fillRect(x, y + b, b, height - b * 2);
	    w.fillRect(x + width - b, y + b, b, height - b * 2);
	}
    }

    boolean full;

    /** Update is called when the mouse is clicked over an image.
	When that occurs, we just repaint the border, if we have
	one. */
    public synchronized void update(Window w, int x, int y) {
	if (full) {
	    w.clearRect(x, y, width, height);
	    paint(w, x, y);
	} else {
	    drawBorder(w, x, y);
	}
    }

    public void trackEnter(Event e) {
	if (pic == null && (WRWindow.delayImageLoading || error != null)) {
	    trackMotion(e);
	} else if (ismap) {
	    ((WRWindow)parent).status("Image map " +
				      anchorUrl.toExternalForm());
	} else if (anchorUrl != null) {
	    ((WRWindow)parent).status("Go to " + anchorUrl.toExternalForm());
	} 
    }

    // The anchor tab is triangular, edges (0,0) - (0,tabSize) - (tabSize,0)

    final int tabSize = 14;
    
    public void trackMotion(Event e) {
	// The ref is a diagonal tab in the upper left corner, accounting for
	// the funny inequality below.
	if (pic == null && (WRWindow.delayImageLoading || error != null)) {
	    if ((anchorUrl != null) &&
		((e.x + e.y) < (2 * borderThickness) + tabSize) && !ismap) {
		((WRWindow)parent).status("Go to " +
					  anchorUrl.toExternalForm());
	    } else if (error != null) {
		((WRWindow)parent).status(error);
	    } else if (ismap) {
		((WRWindow)parent).status("Image map " +
					  imageUrl.toExternalForm());
	    } else {
		((WRWindow)parent).status("Image " +
					  imageUrl.toExternalForm());
	    }
	} 
    }

    public void trackExit(Event e) {
	if ((anchorUrl != null) || WRWindow.delayImageLoading ||
	    error != null) {
	    ((WRWindow)parent).status("");
	}
    }

    public void setColor(Color c) {
	if (normalColor == null) {
	    normalColor = c;
	}
	super.setColor(c);
    }

    public void trackStart(Event e) {
	if (anchorUrl != null) {
	    setColor(highlightColor);
	    requestUpdate(false);
	}
    }

    void requestUpdate(boolean full) {
	if (full) {
	    this.full = full;
	}
	super.requestUpdate();
    }

    /*
     * The user clicked on the image, either go to the reference or start
     * loading the image!
     */
    public void trackStop(Event e) {
	if (anchorUrl != null) {
	    setColor(normalColor);
	    requestUpdate(false);
	}
	// The ref is a diagonal tab in the upper left corner, accounting for
	// the funny inequality below.
	if ((pic == null) && WRWindow.delayImageLoading) {
	    if ((anchorUrl == null) ||
		((e.x + e.y) >= (2 * borderThickness) + tabSize) || ismap) {
		Image img = null;

		if (imageUrl != null) {
		    new DelayedImageFetcher(this).start();
		}
		setImage(img);
		return;
	    }
	}
	executeClick(e);
    }

    void fetchDelayedImage() {
	int ow = width;
	int oh = height;

	ImageHandle h = ImageCache.lookupHandle(parent, imageUrl,
						tagWidth, tagHeight);
	Object obj = h.getImage(this, true);

	if (obj != null) {
	    if (obj instanceof Image) {
		setImage((Image)obj);
	    } else {
		error = (String)obj;
	    }
	    imageSizeKnown = true;
	    if (ow != width || oh != height) {
		((WRWindow) parent).relayout();
	    } else {
		requestUpdate(true);
	    }
	}
    }

    protected void executeClick(Event e) {
	if (anchorUrl != null) {
	    WRWindow    mw = (WRWindow) parent;
	    URL		    url;

	    if (ismap) {
		String	urlName;

		urlName = anchorUrl.toExternalForm() + "?" + e.x + "," + e.y;
		url = new URL(null, urlName);
	    } else {
		url = anchorUrl;
	    }
	    hotjava.history.addUrl(url);
	    if (setColorFromUrl(url)) {
		requestUpdate();
	    }
	    mw.pushURL(url);
	}
    }

    boolean imageSizeKnown = false;

    public synchronized void update(Observable o) {
	ImageHandle h = (ImageHandle) o;
	Object obj;

	if (!imageSizeKnown) {
	    imageSizeKnown = true;
	    resizeFromImageSize(h.width(), h.height());
	    notifyAll();
	}

	obj = h.getImage(this, false);
	if (obj != null) {
	    if (obj instanceof Image) {
		if (getImage() != (Image) obj) {
		    setImage((Image) obj);
		}
	    } else {
		error = (String) obj;
		if (painted) {
		    requestUpdate(true);
		}
	    }
	    /* we don't care anymore - we already got what we need */
	    o.deleteObserver(this);
	    notifyAll();
	}
    }

    public void deactivate() {
	if ((pic == null) && (error == null)) {
	    ImageHandle h = ImageCache.lookupHandle(parent, imageUrl,
						    tagWidth, tagHeight);
	    if (h != null) {
		h.deleteObserver(this);
	    }
	}
	// Set the image and error to null so that images can be de-cached
	// and so the image will be reloaded if the image cache is flushed.
	setImage((Image) null);
	error = (String) null;
    }

    public synchronized void waitForImageSize() {
	while (!imageSizeKnown && !WRWindow.delayImageLoading
	       && (tagWidth <= 0 || tagHeight <= 0)) {
	    wait(2000);	// wake up every 2 seconds and make sure 
			// we haven't been killed.
	}
    }

    public synchronized void waitForImage() {
	while (getImage() == null) {
	    wait();
	}
    }

    public synchronized void setImage(Image i) {
	super.setImage(i);
	if (i != null) {
	    imageSizeKnown = true;
	    if (painted) {
		requestUpdate(true);
	    }
	}
    }
}

class DelayedImageFetcher extends Thread {
    WRImageItem	item;

    DelayedImageFetcher(WRImageItem wim) {
	item = wim;
    }

    public void run() {
	setName("DelayedImageFetcher");
	item.fetchDelayedImage();
    }
}
