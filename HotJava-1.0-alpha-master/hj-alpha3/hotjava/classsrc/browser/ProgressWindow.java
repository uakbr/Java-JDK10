/*
 * @(#)ProgressWindow.java	1.5 95/05/13 Chris Warth
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
package browser;

import awt.*;
import net.ProgressEntry;
import net.ProgressData;
import browser.Observer;
import browser.Observable;
import browser.ProgressBusy;

public class ProgressWindow extends Window implements Observer {
    static Font	dialogFont;
    static FontMetrics fontmetrics;
    static Font	legendFont;
    static int fontheight;
    static int entryheight;

    static final int KLIMIT 	= (10 * 1024);
    static final int lmargin 	= 10;
    static final int barheight  = 12;
    static final int ystart 	= 35;

    static Color colors[] = {
	new Color(50, 220, 100),
	new Color(220, 100, 50),
	new Color(100, 120, 240),
	new Color(200, 120, 240),
	new Color(220, 240, 100),
	Color.lightGray
    };
    static String names[] = {
	"html",
	"image",
	"class",
	"audio",
	"other",
	"connecting",
    };

    ProgressBusy busyloop; //= new ProgressBusy(this);

    /**
     * Construct a progress window.
     */
    ProgressWindow(Container w, String name, Color bg, int wd, int ht) {
	super(w, name, bg, wd, ht);
	ProgressData.pdata.addObserver(this);
    }

    /*
     * This class is an observer of ProgressData.  There are
     * essentially three types of updates that are expected; an entry
     * may be created or deleted, or the values of an entry can be
     * updated.
     */
    public synchronized void update(Observable o) {
	if ((dialogFont == null) || !mapped) {
	    return;
	}

	Graphics g = new Graphics(this);

	try {
	    ProgressData pd = (ProgressData)o;
	    int id = pd.lastchanged;
	    g.clipRect(lmargin, 0, width - lmargin*2, height);

	    switch (pd.what) {
	    case ProgressData.NEW:
		paint(g, id, pd.streams[id]);
		if (busyloop != null) {
		    busyloop.wakeUp();
		}
		break;
		
	    case ProgressData.CONNECTED:
		eraseEntry(g, id);
		paint(g, id, pd.streams[id]);
		break;

	    case ProgressData.UPDATE:
		paintUpdate(g, id, pd.streams[id]);
		break;

	    case ProgressData.DELETE:
		eraseEntry(g, id);
		break;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    g.dispose();
	}
    }

    /**
     * Erase an entry
     */
    void eraseEntry(Graphics g, int id) {
	int barw = width-(2*lmargin);	    // outside bar width.

	g.setForeground(background);
	g.fillRect(lmargin, ystart + (id*entryheight), barw, entryheight);
	//g.setForeground(Color.gray);
	//g.drawRect(lmargin, ystart + fontheight + (id*entryheight), barw-1, barheight);
    }


    /**
     * Paint an entry
     */
    void paint(Graphics g, int id, ProgressEntry te) {
	// Draw the title of this bar
	if (te.label != null) {
	    String str = te.label;

	    g.setFont(dialogFont);
	    g.setForeground(Color.black);
	    g.drawString(str, lmargin, ystart + (id * entryheight) + 
			 	       fontmetrics.height - (fontmetrics.descent + 1));

	    if (te.connected) {
		int x = lmargin + fontmetrics.stringWidth(str) + 10;

		if (te.need >= KLIMIT) {
		    str = String.valueOf(te.need / 1024) + " Kb";
		} else {
		    str = String.valueOf(te.need) + " bytes";
		}

		x = Math.max(x, (width - lmargin) - fontmetrics.stringWidth(str));
		g.drawString(str, x, ystart + (id * entryheight) + 
			 	       fontmetrics.height - (fontmetrics.descent + 1));
	    }
	}

	// Draw the bar
	paintUpdate(g, id, te);
    }


    /**
     * Paint busy signal.
     */
    public synchronized void busyPaint(int id, int oldpos, int newpos) {
	if (mapped) {
	    Graphics g = new Graphics(this);
	    try {
		busyPaint(g, id, oldpos, newpos);
	    } finally {
		g.dispose();
	    }
	}
    }

    /*
     * This is called every now and then to move a small bar back
     * and forth across the status bar to indicate that the
     * connection is still in progress.  The driver behind this is
     * the thread created by the ProgressBusy class.
     */
    void busyPaint(Graphics g, int id, int oldpos, int newpos) {
	int barw = width-(2*lmargin);	    // outside bar width.
	int innerbarw = barw-2;		    // inside bar width.
	int busyw = (int) ((float) barw * .07); // width of floating busy indicator
	int busybarw = innerbarw - busyw;	    // width in which to move busy indicator

	// First erase the old indicator
	g.setForeground(background);
	g.fillRect(lmargin + 1 + (int)(((float)oldpos/100.0) * busybarw), 
		 ystart + fontheight + (id*entryheight) + 1,
		 busyw, barheight - 1);

	// Now draw the new indicator.
	g.setForeground(Color.gray);
	g.fillRect(lmargin + 1 + (int)(((float)newpos/100.0) * busybarw), 
		 ystart + fontheight + (id*entryheight) + 1,
		 busyw, barheight - 1);

    }

    /**
     * Update an entry.
     */
    void paintUpdate(Graphics g, int id, ProgressEntry te) {
	int barw = width-(2*lmargin);	    // outside bar width.
	int y = ystart + fontheight + id*entryheight;
	int ww = (int)((((float) barw)/te.need)*te.read);

	g.setForeground(Color.black);
	g.drawRect(lmargin, y, barw-1, barheight);

	g.setForeground(colors[te.type]);
	g.fillRect(lmargin + 1, y+1, ww - 1, barheight-1);

//	g.setForeground(background);
//	g.fillRect(ww + lmargin + 1, y+1, (barw - 2) - ww, barheight-1);

    }

    /**
     * Paint the legend (at the top of the window)
     */
    void paintLegend(Graphics g) {
	// initialize the font
	if (legendFont == null) {
	    legendFont = wServer.fonts.getFont("Dialog", Font.PLAIN, 12);
	}

	// Set the font
	g.setFont(legendFont);

	// Draw the legend
	int x = lmargin;
	for (int i = 0; i < colors.length; i++) {
	    g.setForeground(colors[i]);
	    g.fillRect(x + 1, 6, 13, 13);
	    g.setForeground(Color.black);
	    g.drawRect(x, 5, 14, 14);
	    x += 15 + 3;
	    x += g.drawStringWidth(names[i], x, 17) + 10;
	}

	// draw a horizontal indented line.
	g.paint3DRect(0, 25, width, 2, false, false);
    }

    void paint(Graphics g) {
	/*
	 * It is *REALLY* irritating that you cannot create fonts,
	 * etc, until after the window is actually mapped to the
	 * screen.  This torques around the design of this class
	 * quite a bit.
	 */
	if (dialogFont == null) {
	    dialogFont = wServer.fonts.getFont("Dialog", Font.ITALIC, 10);
	    fontmetrics = g.getFontMetrics(dialogFont);
	    fontheight = dialogFont.actualHeight;
	    entryheight = barheight + fontmetrics.height + 5;
	}

	paintLegend(g);

	ProgressData pd = ProgressData.pdata;
	g.clipRect(lmargin, 0, width - lmargin*2, height);
	g.setFont(dialogFont);
	for (int i = 0; i < pd.streams.length; i++) {
	    ProgressEntry te = pd.streams[i];
	    if (te != null) {
		if (te.need == te.read && te.read != 0) {
		    eraseEntry(g, i);
		} else {
		    paint(g, i, te);
		}
	    } else {
		eraseEntry(g, i);
	    }
	}
    }

    /**
     * Paint the progess dialog from scratch.
     */
    synchronized public void paint() {
	Graphics g = new Graphics(this);
	try {
	    g.setForeground(background);
	    g.fillRect(0, 0, width, height);
	    paint(g);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    g.dispose();
	}
    }
}
