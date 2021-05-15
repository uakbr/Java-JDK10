/*-
 * Copyright (c) 1994 by FirstPerson, Inc.
 * All rights reserved.
 *
 * @(#)TextViewer.java	1.12 94/07/19 Feb 1994
 *
 *	Arthur van Hoff, Nov 1993
 *	Arthur van Hoff, March 1994
 */

package edit;

import awt.Frame;
import awt.Window;
import awt.Scrollbar;
import awt.Graphics;
import awt.Color;

//
// The editor
//

public class TextViewer extends Window implements TextView, UpdateClient {
    /** The text */
    public Text text;

    /** The vertical scrollbar */
    protected Scrollbar sb;

    /** The number of lines in the line array */
    protected int nlines;

    /** The lines that are visible on the screen */
    protected TextViewLine line[];

    /** The offset of the top line */
    protected int top;

    /** The y position of the top line (may be negative) */
    protected int topY;

    /** True if the scrollbar needs updating */
    protected boolean scrollUpdate;

    /** Create a TextViewer containing a canvas and an optional scrollbar */
    public TextViewer(Window win, String name, boolean scroll) {
	super(win, name, win.background, 100, 100);
	if (scroll) {
	    sb = new Scrollbar(this, "TextViewerScrollbar", Scrollbar.VERTICAL, true);
	}
	nlines = 0;
	line = new TextViewLine[nlines];
    }

    /** Create a TextViewer containing a canvas and an optional scrollbar */
    public TextViewer(Frame frm, String name, boolean scroll) {
	super(frm, name, frm.background, 100, 100);
	if (scroll) {
	    sb = new Scrollbar(this, "TextViewerScrollbar", Scrollbar.VERTICAL, true);
	}
	nlines = 0;
	line = new TextViewLine[nlines];
    }

    /** Set the text of the viewer */
    public synchronized void setText(Text pText) {
	if (text == pText) {
	    return;
	}
	for (int i = 0 ; i < nlines ; i++) {
	    line[i].updated = true;
	}
	if (text != null) {
	    text.removeView(this);
	}
	top = topY = 0;
	text = pText;
	if (text != null) {
	    text.addView(this);
	}
	scrollUpdate = true;
	touch();
    }

    /** Format a line of text */
    TextViewLine formatLine(int pPos, int pWidth) {
	return new TextViewLine(this, pPos, pWidth);
    }

    /** Notify changes with the screen updater */
    void touch() {
	ScreenUpdater.updater.notify(this);
    }

    /** Called by the model to notify this view that
     * some stuff has been inserted */
    public void notifyInsert(int pos, int len) {
	int i;
	
//	print("NotifyInsert(" + pos + "," + len + ")\n");
	touch();

	if (top > pos) {
	    top += len;
	}
	
	// Correct the lines
	for (i = 0 ; i < nlines ; i++) {
	    TextViewLine ln = line[i];

	    if ((ln.tail > pos) || (ln.tail == 0)) {
		if (ln.start > pos) {
		    ln.start += len;
		    ln.end   += len;
		} else {
		    ln.updated = true;
		    if (ln.end >= pos) {
			ln.end += len;
		    }
		}
		ln.tail += len;
	    }
	}
    }
    
    /** Called by the model to notify this view that
     * some stuff has been deleted */
    public void notifyDelete(int pos, int len) {
	int i, endPos = pos + len;

//	print("NotifyDelete(" + pos + "," + len + ")\n");
	touch();
	if (top > pos) {
	    top = Math.max(pos, top - len);
	}

	// Correct the lines
	for (i = 0 ; i < nlines ; i++) {
	    TextViewLine ln = line[i];

	    if (ln.tail > pos) {
		if (ln.start >= endPos) {
		    ln.start -= len;
		    ln.end   -= len;
		    ln.tail  -= len;
		} else {
		    ln.updated = true;
		    if (ln.start > pos) {
			ln.start = pos;
		    }
		    if (ln.end > pos) {
			ln.end = (ln.end > endPos) ? ln.end - len : pos;
		    }
		    ln.tail = (ln.tail > endPos) ? ln.tail - len : pos;
		}
	    }
	}
    }
    
    /** Called by the model to notify this view that
     * some stuff has been changed */
    public void notifyChange(int pos, int len) {
	int i, endPos = pos + len;

//	print("NotifyChange(" + pos + "," + len + ")\n");
	touch();

	// Correct the lines
	for (i = 0 ; i < nlines ; i++) {
	    TextViewLine ln = line[i];
	    if ((pos < ln.tail) && (endPos > ln.start)) {
		ln.updated = true;
	    }
	}
    }

    /** Compute the start offset of the line containing the
     * specified offset */
    private int lineStartPos(int pPos) {
	char str[] = text.data;
	for (; (pPos > 0) && (str[pPos - 1] != '\n') ; pPos--);
	return pPos;
    }

    /** Append a line to the line array */
    private void appendLine(TextViewLine ln) {
	if (nlines >= line.length) {
	    TextViewLine newline[] = new TextViewLine[nlines + 32];
	    System.arraycopy(line, 0, newline, 0, nlines);
	    line = newline;
	}	
	line[nlines++] = ln;
    }

    /** Copy lines around on the screen */
    private void copyLines(Graphics g, int i) {
	TextViewLine ln;
	int dy, y2;
	
	while (i < nlines) {
	    // skip over new lines 
	    for (; (i < nlines) && line[i].updated ; i++);

	    if (i < nlines) {
		// count lines that can be copied
		for (ln = line[i++] ; (i < nlines) && (!line[i].updated) ; i++);
		dy = ln.y - ln.oldy;
		y2 = line[i - 1].y + line[i - 1].height;

		if (dy < 0) {
//		    print("scroll up " + (i - 1) + "\n");
		    // scroll up
		    g.copyArea(1, ln.oldy, width - 2, y2 - ln.y, 1 + 0, ln.oldy + dy);
		} else if (dy > 0) {
		    // scroll down
//		    print("scroll down " + (i - 1) + "\n");
		    copyLines(g, i);
		    g.copyArea(1, ln.oldy, width - 2, y2 - ln.y, 1 + 0, ln.oldy + dy);
		    return;
		}
	    }
	}
    }

    /** Update the formatting of this view */
    public synchronized void update() {
	if (text == null) {
	    return;
	}
	
	TextViewLine ln = null;
	int pos, ypos, i, j, topln;
	
	// Keep the old lines
	int noldlines = nlines;
	TextViewLine oldline[] = line;

	// Reset lines
	line = new TextViewLine[line.length];
	nlines = 0;

	// Adjust the top to the latest scroll position
	if (top < 0) {
	    top = 0;
	}
	if (top > text.length) {
	    top = text.length;
	}

	while ((topY > 0) && (top > 0)) {
	    // Format from the start pos
	    for (pos = lineStartPos(top - 1), j = 0 ; pos < top ; pos = ln.end) {
		for (; (j < noldlines) && (pos > oldline[j].start) ; j++);
		if ((j < noldlines) && (!oldline[j].updated) && (oldline[j].start == pos)) {
		    ln = oldline[j].copy();
		} else {
		    ln = formatLine(pos, width - 2);
		}
		appendLine(ln);
	    }

	    for (i = nlines - 1 ; i >= 0 ; i--) {
		ln = line[i];
		if (top >= ln.end) {
		    topY -= ln.height;
		    if (topY < 0) {
			top = ln.end;
			break;
		    }
		}
		top = ln.start;
	    }

	    // Reset lines
	    line = new TextViewLine[line.length];
	    nlines = 0;
	}

	// Format from the start pos
	for (pos = lineStartPos(top), j = 0 ; pos <= top;) {
	    for (; (j < noldlines) && (pos > oldline[j].start) ; j++);
	    if ((j < noldlines) && (!oldline[j].updated) && (oldline[j].start == pos)) {
		ln = oldline[j].copy();
	    } else {
		ln = formatLine(pos, width - 2);
	    }
	    appendLine(ln);
	    pos = ln.end;
	    if (pos >= text.length) {
		break;
	    }
	}

	// These lines have a y < 1 (above the top of the screen)
	for (ypos = 1, i = nlines - 2 ; i >= 0 ; i--) {
	    ypos -= line[i].height;
	    line[i].y = ypos;
	}

	// The top lines of the screen
	topln = nlines - 1;
	ln.y = 1;
	top = ln.start;
	ypos = ln.height + 1;

	// Format the rest
	for (; (ln.end < text.length) && (ypos < height - 1) ; pos = ln.end) {
	    for (; (j < noldlines) && (pos > oldline[j].start); j++);
	    if ((j < noldlines) && (!oldline[j].updated) && (oldline[j].start == pos)) {
		ln = oldline[j].copy();
	    } else {
		ln = formatLine(pos, width - 2);
	    }
	    ln.y = ypos;
	    ypos += ln.height;
	    if (ypos <= height - 1) {
		appendLine(ln);
	    }
	}

	// Allocate graphics context
	Graphics g = new Graphics(this);

	// The lines are now all correctly formatted, display changes
	copyLines(g, topln);

	// Display new lines and clear the background
	i = topln;
	while (true) {
	    for (; (i < nlines) && (!line[i].updated) ; i++);
	    if (i >= nlines) {
		break;
	    }
	    for (j = i + 1 ; (j < nlines) && line[j].updated ; j++);

	    // paint the background
	    paintBack(g, 1, line[i].y, width - 2, (line[j - 1].y + line[j - 1].height) - line[i].y);

	    for (; i < j ; i++) {
		ln = line[i];
		ln.paint(g, 1, ln.y);
		ln.updated = false;
	    }
	}

	// Erase any extra stuff at the bottom
	if (noldlines > 0) {
	    ln = line[nlines - 1];
	    int newbottom = ln.y + ln.height;
	    ln = oldline[noldlines - 1];
	    int oldbottom = ln.y + ln.height;
	    if (newbottom < oldbottom) {
		paintBack(g, 1, newbottom, width - 2, oldbottom - newbottom);
	    }
	}

	// Fix the scrollbar
	if ((sb != null) && scrollUpdate) {
	    sb.setValues(top, 100, 0, text.length);
	    scrollUpdate = false;
	}

	// Cleanup
	g.dispose();
    }

    /** Paint the background */
    protected void paintBack(Graphics g, int x, int y, int w, int h) {
	g.setForeground(background);
	g.fillRect(x, y, w, h);
    }

    /** Paint the text after a damage event */
    synchronized void paint(Graphics g) {
	int i;

	paintBack(g, 0, 0, width, height);
	
	for (i = 0 ; i < nlines ; i++) {
	    TextViewLine ln = line[i];
	    if (ln.y >= height) {
		break;
	    }
	    if (ln.updated || (ln.y + ln.height <= 0)) {
		continue;
	    }
	    ln.paint(g, 1, ln.y);
	}
    }

    /** This method is called automatically when the
     * the TextViewer is resized */
    public void layout() {
	if (!tidy) {
	    super.layout();
	    if (text != null) {
		notifyChange(0, text.length);
	    }
	} else {
	    super.layout();
	}
    }


    /** Scrolling */
    synchronized void scrollTo(int pY, int pPos, boolean pScrollUpdate) {
	touch();
	topY = pY;
	top = pPos;
	scrollUpdate = pScrollUpdate;
    }

    /** Show pos at a particular Y location */
    public synchronized void scrollTo(int pY, int pPos) {
	scrollTo(pY, pPos, true);
    }

    /** Make sure that the given position is visible in the window */
    public synchronized void scrollTo(int pPos) {
	if ((nlines > 2) && (pPos >= line[2].start) &&
	    (pPos < line[nlines - 3].start)) {
	    return;
	}
	    
	scrollTo(height / 3, pPos, true);
    }

    /** Scroll a page up */
    public synchronized void goPageUp() {
	if (nlines > 0) {
	    scrollTo(height, line[0].start);
	} else {
	    scrollTo(0, top - 500);
	}
    }

    /** Scroll a page down */
    public synchronized void goPageDown() {
	if (nlines > 0) {
	    scrollTo(0, line[nlines - 1].start);
	} else {
	    scrollTo(0, top + 500);
	}
    }

    /** Scroll to the top */
    public synchronized void goTop() {
	scrollTo(0, 0);
    }

    /** Scroll to the bottom */
    public synchronized void goBottom() {
	scrollTo(height, text.length);
    }

    /** Compute the text offset of an x,y location */
    int xyToPos(int pX, int pY) {
	int i;
	
	if (pY < 0) {
	    return top;
	}

	for (i = 0 ; i < nlines ; i++) {
	    if (pY < line[i].y + line[i].height) {
		return line[i].xToPos(pX - 1);
	    }
	}
	return (nlines > 0) ? line[nlines - 1].end : top;
    }

    /** Figure out what line a y location was in */
    int yToLine(int pY) {
	int i;

	if (pY < 0) {
	    return text.offsetToLine(top);
	}
	
	for (i = 0 ; i < nlines ; i++) {
	    if (pY < line[i].y + line[i].height) {
		return text.offsetToLine(line[i].start);
	    }
	}
	return text.offsetToLine((nlines > 0) ? line[nlines - 1].end : top);
    }

    /* Handle Events
    public boolean handleEvent(Event e) {
	switch (e.id) {
	  case DamageEvent.DAMAGE: {
	      DamageEvent evt = (DamageEvent)e;
	      paint(evt.g);
	      return true;
	  }

	  case ScrollEvent.SCROLL: {
	      ScrollEvent evt = (ScrollEvent)e;
	      scrollTo(0, evt.value, false);
	      return true;

	  }
	  case ActionEvent.ACTION: {
	      ActionEvent evt = (ActionEvent)e;
	      if (evt.label.equals("PgDn")) {
		  goPageDown();
	      } else if (evt.label.equals("PgUp")) {
		  goPageUp();
	      } else if (evt.label.equals("Home")) {
		  goTop();
	      } else if (evt.label.equals("End")) {
		  goBottom();
	      } else {
		  break;
	      }
	      return true;
	  }
	}
	return super.handleEvent(e);
    }
    */

    public void pageDown() {
	goPageDown();
    }
    public void pageUp() {
	goPageUp();
    }
    public void lineDown() {
	System.out.println("DOWN");
    }
    public void lineUp() {
	System.out.println("UP");
    }
    public void dragAbsolute(int i) {
	scrollTo(0, i);
    }

    public void expose(int x, int y, int w, int h) {
	paint(new Graphics(this));
    }
}

