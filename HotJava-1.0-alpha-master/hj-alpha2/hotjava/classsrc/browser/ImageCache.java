/*
 * @(#)ImageCache.java	1.11 95/03/14 Chris Warth
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

import awt.Image;
import awt.Window;
import net.www.html.URL;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

public class ImageCache extends Object {
    private static Hashtable img_table = new Hashtable(10);
    private static ImageCache fetcher = null;
    private static Vector handles = new Vector();
    private static ImageReader imageReader;

    public static void flush() {
	fetcher = null;
	handles = new Vector();
	if (imageReader != null) {
	    imageReader.stop();
	    imageReader = null;
	}
	img_table = new Hashtable(10);
    }

    static boolean emptyQueue() {
	return imageReader.emptyQueue();
    }


    /*
     * put a new key into the cache.  if the key is already there
     * simply return the handle associated with the existing entry.
     * Othrewise create a new entry and put it into the cache.
     */
    public static ImageHandle lookupHandle(Window win, URL url) {
	return lookupHandle(win, url, -1, -1);
    }

    public static ImageHandle lookupHandle(Window win, URL url, int w, int h) {
	String rawkey = url.toExternalForm();
	String key = rawkey;
	ImageHandle ih;

	if (w > 0 || h > 0)
	    key = rawkey + "@" + w + "x" + h;
	ih = (ImageHandle) img_table.get(key);
	if (ih == null) {
	    if (key != rawkey) {
		ih = (ImageHandle) img_table.get(rawkey);
	    }
	    if (ih == null) {
		ih = new ImageHandle(win, url);
		if (img_table.put(rawkey, ih) != null) {
		    System.out.println("Overrode " + ih);
		}
	    }
	    if (key != rawkey) {
		ih = new ScaledImageHandle(ih, w, h);
		if (img_table.put(key, ih) != null) {
		    System.out.println("Overrode " + ih);
		}
	    }
	}
	return ih;
    }

    public static void fetch(ImageHandle ih) {
	if (!ih.isFetching()) {
	    if (imageReader == null)  {
		imageReader = new ImageReader();
	    }
	    ih.setFetching();
	    imageReader.add(ih);
	}
    }
    public static void cancelFetch(ImageHandle ih) {
	if (ih.isFetching() && (imageReader != null)) {
	    imageReader.remove(ih);
	    ih.fetching = false;
	}
    }

    /**
     * Returns an enumeration of all the images in the cache.
     */
    public static Enumeration getImages() {
	return img_table.elements();
    }
}

