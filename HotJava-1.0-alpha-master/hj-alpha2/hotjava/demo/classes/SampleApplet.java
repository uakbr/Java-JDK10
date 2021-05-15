/*
 * @(#)SampleApplet.java	1.5 95/03/14 Arthur van Hoff
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
 * A sample applet that shows off some features and
 * prints lots of debugging statements.
 * @author 	Arthur van Hoff
 * @version 	1.5, 14 Mar 1995
 */
class SampleApplet extends Applet {
    /**
     * Applet methods
     */
    public void init() {
	System.out.println("SampleApplet: init()");
	resize(100, 100);
    }
    public void start() {
	System.out.println("SampleApplet: start()");
    }
    public void stop() {
	System.out.println("SampleApplet: stop()");
    }
    public void destroy() {
	System.out.println("SampleApplet: destroy()");
    }

    /**
     * Paint a rectangle with some wierd lines...
     */
    public void paint(Graphics g) {
	System.out.println("SampleApplet: paint()");
	g.drawRect(0, 0, width - 1, height - 1);
	for (int i = 0 ; i < width - 1 ; i += 8) {
	    g.drawLine(i, 0, width - 1, height - 1);
	}
	for (int i = 0 ; i < height - 1 ; i += 8) {
	    g.drawLine(0, 0, width - i, height - 1);
	}
    }

    /**
     * Mouse methods
     */
    public void mouseDown(int x, int y) {
	System.out.println("SampleApplet: mouseDown(" + x + "," + y + ")");
	getFocus();
	play("audio/beep.au");
    }
    public void mouseDrag(int x, int y) {
	System.out.println("SampleApplet: mouseDrag(" + x + "," + y + ")");
    }
    public void mouseUp(int x, int y) {
	System.out.println("SampleApplet: mouseUp(" + x + "," + y + ")");
    }
    public void mouseMove(int x, int y) {
	System.out.println("SampleApplet: mouseMove(" + x + "," + y + ")");
    }
    public void mouseEnter() {
	System.out.println("SampleApplet: mouseEnter()");
    }
    public void mouseExit() {
	System.out.println("SampleApplet: mouseExit()");
    }

    /**
     * Focus methods
     */
    public void gotFocus() {
	System.out.println("SampleApplet: gotFocus()");
    }
    public void lostFocus() {
	System.out.println("SampleApplet: lostFocus()");
    }
    public void keyDown(int key) {
	System.out.println("SampleApplet: keyDown(" + key + ")");
    }
}
