/*
 * @(#)ImageHandle.java	1.22 95/05/12 Chris Warth
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
import java.io.InputStream;

class ImageRef extends Ref {
    public Object reconstitute() {
	return null;
    }
}

public class ImageHandle extends Observable implements Observer {
    ImageRef	image;
    URL url;	
    Window win;
    protected boolean fetching = false;
    DIBitmap	bm;

    ImageHandle(Window win, URL url) {
	image = new ImageRef();
	this.win = win;
	this.url = url;
    }

    public URL getURL() {
	return url;
    }

    // This methid is unsafe. It should be synchronized but it isn't
    public Object getImage(Observer o, boolean waitForIt) {
	Object	img = image.check();

	if (img == null) {
	    if (o != null) {
		addObserver(o);
	    }
	    ImageCache.fetch(this);
	    if (waitForIt) {
		waitForImage();
	    }
	    img = image.check();
	}
	return img;
    }

    public void setImage(Object img) {
	fetching = false;
	image.setThing(img);
	setChanged();
	synchronize (this) {
	    notifyAll();	/* notify people doing waitForImage() */
	}
	notifyObservers();
    }

    public Object checkForImage(Observer o) {
	if (o != null) {
	    addObserver(o);
	}
	return image.check();
    }

    public synchronized void waitForImage() {
	while (image.check() == null) {
	    wait();
	}
    }

    public synchronized void waitForSize() {
	while ((bm == null) && (image.check() == null)) {
	    wait();
	}
    }

    public int width() {
	if (bm != null) {
	    return bm.width;
	}
	Object	img = image.check();

	if (img != null && img instanceof Image) {
	    return ((Image) img).width;
	}
	return 20;
    }

    public int height() {
	if (bm != null) {
	    return bm.height;
	}
	Object	img = image.check();

	if (img != null && img instanceof Image) {
	    return ((Image) img).height;
	}
	return 20;
    }

    public boolean isFetching() {
	return fetching;
    }

    public void setFetching() {
	fetching = true;
    }

    public synchronized void deleteObserver(Observer o) {
	super.deleteObserver(o);
	if ((url != null) && (countObservers() == 0)) {
	    ImageCache.cancelFetch(this);
	}
    }

    /** This routine is called by the ImageReader.  The getImage()
	waits around for this method to complete. */
    public void fetchImage() {
	InputStream in = null;
	try {
	    in = url.openStreamInteractively();
	    url.getContent(in, this);
	    setImage(win.createImage(bm));
	} catch(Exception e) {
//	    e.printStackTrace();
	    setImage("Error " + e + " reading " + url.toExternalForm());
	    // We may have gotten a preliminary notification of the bitmap
	    // but now it is useless, so drop it immediately.
	    bm = null;
	} finally {
	    if (in != null) {
		in.close();
	    }
	}
	/* observers were notified in setImage, so anyone who
	   cared already got their hands on the bitmap */
	bm = null;
    }

    public synchronized void update(Observable o) {
	bm = (DIBitmap) o;
	notifyAll();
	setChanged();
	notifyObservers();
    }

    public void addObserver(Observer o) {
	super.addObserver(o);
    }

    public String toString() {
	return "ImageHandle " + url.toExternalForm();
    }
}

class ScaledImageHandle extends ImageHandle {
    int		needWidth;
    int		needHeight;
    ImageHandle	rawIH;

    ScaledImageHandle(ImageHandle rawIH, int w, int h) {
	super(rawIH.win, (URL) null);
	this.rawIH = rawIH;
	needWidth = w;
	needHeight = h;
    }

    public int width() {
	if (needWidth > 0)	
	    return needWidth;
	else
	    return rawIH.width();
    }

    public int height() {
	if (needHeight > 0)
	    return needHeight;
	else
	    return rawIH.height();
    }

    public boolean isFetching() {
	return rawIH.isFetching();
    }

    public void setFetching() {
	rawIH.setFetching();
    }

    public void fetchImage() {
	rawIH.fetchImage();
	return;
    }

    // This methid is unsafe. It should be synchronized but it isn't
    public Object getImage(Observer o, boolean waitForIt) {
	Object img = image.check();
	if (img != null) {
	    // It is ready
	    return img;
	}

	if (o != null) {
	    addObserver(o);
	}

	img = rawIH.getImage(this, waitForIt);
	if (img != null) {
	    // The unscaled version is ready
	    update(rawIH);
	    return image.check();
	} 
	return null;
    }

    public synchronized void update(Observable o) {
	Object	 rawImg = rawIH.image.check();

	if (rawImg != null) {
	    if (rawImg instanceof String) {
		setImage(rawImg);
	    } else {
		Image srcimg = (Image) rawImg;
		if (srcimg.width == needWidth && srcimg.height == needHeight) {
		    setImage(srcimg);
		} else {
		    DIBitmap rawbm = rawIH.bm;
		    if (rawbm == null) {
			rawbm = srcimg.getDIBitmap();
		    }
		    if (rawbm != null && rawbm.raster != null) {
			setImage(win.createImage(rawbm, needWidth,
						 needHeight));
		    }
		}
	    }
	}
	notifyObservers();
    }
    
    public synchronized void deleteObserver(Observer o) {
	super.deleteObserver(o);
	if (countObservers() == 0) {
	    rawIH.deleteObserver(this);
	}
    }

    public String toString() {
	return "ScaledImageHandle " + needWidth + "x" + needHeight + " - " + rawIH;
    }
}
