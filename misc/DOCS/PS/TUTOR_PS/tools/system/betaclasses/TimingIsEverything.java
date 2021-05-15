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
import java.awt.Graphics;

public class TimingIsEverything extends java.applet.Applet {

    public long firstClickTime = 0;
    public String displayStr;

    public void init() {
	displayStr = "Double Click Me";
    }
    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width-1, size().height-1);
	g.drawString(displayStr, 40, 30);
    }
    public boolean mouseDown(java.awt.Event evt, int x, int y) {
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
	return true;
    }
}
