/*
 * @(#)ImageDisplayItem.java	1.7 95/01/31 Jonathan Payne
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
package awt;

import java.io.InputStream;
import net.www.html.URL;

/**
 * A DisplayItem for an embedded image.
 *
 * @version 1.7 31 Jan 1995
 * @author Jonathan Payne
 */
public class ImageDisplayItem extends DisplayItem {
    protected Image	pic;

    /** Constructs the DisplayItem. */
    public ImageDisplayItem(Image pic) {
	setImage(pic);
    }

    public void resizeFromImage(Image img) {
	resize(img.width, img.height);
    }

    public void setImage(Image pic) {
	this.pic = pic;
	if (pic != null) {
	    resizeFromImage(pic);
	}
    }

    public Image getImage() {
	return pic;
    }

    /** Sets the Image from a DIBitmap.
     * @see awt.DIBitmap
     */
    public void setImage(awt.DIBitmap dib) {
	if (parent != null) {
	    setImage(parent.createImage(dib));
	}
    }

    public void paint(awt.Window window, int x, int y) {
	super.paint(window, x, y);
	if (pic != null) {
	    window.drawImage(pic, x, y);
	}
    }
}

