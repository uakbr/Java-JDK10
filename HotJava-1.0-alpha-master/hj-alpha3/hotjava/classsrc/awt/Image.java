/*
 * @(#)Image.java	1.10 95/02/17 Patrick Naughton
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

/**
 *	Abstract Image.
 * 
 * @version 1.10 17 Feb 1995
 * @author Patrick Naughton
 */
public class Image {
    public Window win;
    public int width;
    public int height;

    public Image (Window wswin) {
	win = wswin;
    }

    public DIBitmap getDIBitmap() {
	return win.retrieveDIBitmap(this);
    }

    public void finalize() {
	dispose();
    }

    public void dispose() {
	win.disposeImage(this);
	win = null;
    }

    private int pData;
}
