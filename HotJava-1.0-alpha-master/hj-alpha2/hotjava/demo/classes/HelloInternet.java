/*
 * @(#)HelloInternet.java	1.6 95/03/14 Arthur van Hoff
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

import awt.Graphics;
import browser.Applet;

/**
 * The minimal applet.
 *
 * @author 	Arthur van Hoff
 * @version 	1.6, 14 Mar 1995
 */
class HelloInternet extends Applet {
    /**
     * Initialize the applet. Resize it to some
     * reasonable size.
     */
    public void init() {
	resize(150, 25);
    }

    /**
     * Paint the applet.
     */
    public void paint(Graphics g) {
	g.drawString("Hello Internet!", 5, 20);
    }
}
