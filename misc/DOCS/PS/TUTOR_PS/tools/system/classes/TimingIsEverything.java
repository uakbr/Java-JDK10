/*
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

import browser.Applet;
import awt.Graphics;

class TimingIsEverything extends Applet {

    public long firstClickTime = 0;
    public String displayStr;

    public void init() {
	resize (300, 50);
	displayStr = "Double Click Me";
    }
    public void paint(Graphics g) {
	g.drawRect(0, 0, 299, 49);
	g.drawString(displayStr, 40, 30);
    }
    public void mouseDown(int x, int y) {
	long clickTime = System.currentTimeMillis();
	long clickInterval = clickTime - firstClickTime;
	if (clickInterval < 200) {
	    displayStr = "Double Click!! (Interval = " + clickInterval + ")";
	    firstClickTime = 0;
	} else {
	    displayStr = "Single Click!!";
	    firstClickTime = clickTime;
	}
	repaint();
    }
}
