/*
 * @(#)DisplayItemWindow.java	1.42 95/02/16 Jonathan Payne
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

import java.util.*;

/**
 * DisplayItemWindow is a subclass of Window that can display embedded
 * DisplayItem's. This task involves mainly distributing input to the
 * right DisplayItem and causing the appropriate items to be displayed
 * in response to damage or scrolling events.
 * @version 1.42 16 Feb 1995
 * @author Jonathan Payne
 */
public class DisplayItemWindow extends Window {
    DIWUpdaterThread	updater = null;
    Vector		nativeItems;
    Scrollbar		sb = null;
    DisplayItem		items[] = new DisplayItem[0];
    int			used = 0;
    protected int	logicalWidth;
    protected int	logicalHeight;
    protected DisplayItem   currentInputItem;
    protected boolean	    stickyTracking = true;
    Event		tEvent = new Event();	    /* translated event */
    protected boolean 	valid = false;
    protected int	scrollX;
    protected int   	scrollY;
    public FocusManager fm = new FocusManager(this);

    /** Creates a DisplayItemWindow that is contained inside a Frame. */
    public DisplayItemWindow(Frame f, String name) {
	super(f,name, Color.lightGray, 600,600);
	nativeItems = new Vector();
	setLayout(null);
    }

    /** Creates a DisplayItemWindow that is contained inside a Window */
    public DisplayItemWindow(Window parent, String client) {
	super(parent, client, Color.lightGray, 150, 150);
	setMargin(2);
	nativeItems = new Vector();
	setLayout(null);
    }

    /** Set the scrollbar for this DisplayItemWindow to s */
    public void setScrollbar(Scrollbar s) {
	sb = s;
	invalidate();
    }

    /** Indicates that this DisplayItemWindow needs updating. */
    public void invalidate() {
	valid = false;
    }

    /** Updates the scrollbar to reflect the current size of the
     *  window.
     */
    public void updateScrollbar() {
	if (sb != null && height > 0 && logicalHeight > 0) {
	    sb.setValues(-scrollY, height, 0, logicalHeight);
	}
    }

    /** Marks this window as valid (not in need of repainting) */
    public void validate() {
	updateScrollbar();
	valid = true;
    }

    public synchronized void handleExpose(int x, int y, int w, int h) {
	graphics.clipRect(x, y, w, h);
//	graphics.setForeground(Color.red);
//	graphics.fillRect(x, y, w, h);
//	update();
//	System.out.println("Expose: " + x + ", " + y + ", " + w + ", " + h);
//	Thread.sleep(500);
	paintRange(y, y + h);
	graphics.clearClip();
    }

    /** Called in response to a resize action by the user. Redoes the
     * window layout and marks the window as invalid.
     */
    public void handleResize() {
	if (theLayout != null)
	    theLayout.layout(this);
	invalidate();
    }


    /** Adds an named DisplayItem to the window. */
    public synchronized void addItem(DisplayItem di, String name) {
	if (name != null) {
	    addChild(di, name);
	}
	addItem(di);
    }


    /** Adds an unnamed DisplayItem to the window. */
    public synchronized void addItem(DisplayItem di) {
	di.setParent(this);
	if (di instanceof NativeDisplayItem) {
	    addNativeItem((NativeDisplayItem)di);
	}
	if (used == items.length) {
	    int	amt = (used == 0) ? 40 : (int)(used * 1.5);
		
	    DisplayItem	newItems[] = new DisplayItem[amt];
	    System.arraycopy(items, 0, newItems, 0, items.length);
	    items = newItems;
	}
	items[used++] = di;
	invalidate();
    }

    /** Returns the array of items in this window. */
    public DisplayItem getItems()[] {
	return items;
    }

    /** Adds a DisplayItem that is a subclass of NativeDisplayItem.
     * This method is needed because DisplayItem's that are native
     * gui components require special treatment.
     * @see awt.NativeDisplayItem
     */
    protected synchronized void addNativeItem(NativeDisplayItem di) {
	int i = nativeItems.size();

	while (--i >= 0) {
	    if ((NativeDisplayItem)nativeItems.elementAt(i) == di) {
		break;
	    }
	}
	if (i < 0) {
	    nativeItems.addElement(di);
	}
    }

    /**
     * Clears the list of native display items.
     */
    protected synchronized void clearNativeItems() {
	nativeItems.removeAllElements();
    }


    /**
     * Notifies this window that the dimensions of a DisplayItem have
     * changed.
     */
    void childChanged(DisplayItem child) {
//	if (logicalWidth < child.x + child.width) {
//	    logicalWidth = child.x + child.width;
//	}
//	if (logicalHeight < child.y + child.height) {
//	    logicalHeight = child.y + child.height;
//	}
    }

    /**
     * Returns the item at the given index.
     */
    public DisplayItem nthItem(int n) {
	if (n >= used) {
	    throw new ArrayIndexOutOfBoundsException(n + " >= " + used);
	}
	return items[n];
    }

    /**
     * Clears the list of DisplayItems for this window. The deactivate
     * method is called on each item before being removed from the list.
     *
     * @see awt.DisplayItem#deactivate
     */
    public synchronized void clearItems() {
	stopUpdater();
	currentInputItem = null;
	for (int i = used; --i >= 0; ) {
	    items[i].deactivate();
	    items[i] = null;
	}
	clearNativeItems();
	used = 0;
	items = new DisplayItem[0];
	logicalWidth = logicalHeight = 0;
	invalidate();
	startUpdater();
    }

    /**
     * Returns the number of DisplayItem's contained in this window.
     */
    public int count() {
	return used;
    }

    /**
     * Paints all of the DisplayItems contained between y0 and y1.
     */
    synchronized void paintRange(int y0, int y1) {
	if (y1 < 0 || y0 > height)
	    return;

	setForeground(background);
	fillRect(0, y0, width, y1 - y0);
	y0 -= scrollY;
	y1 -= scrollY;

	int cnt = count();
	int i = 0;

	while (--cnt >= 0) {
	    DisplayItem	di = items[i++];

	    if (di.y + di.height < y0 || di.y > y1) {
		continue;
	    }
	    di.paint(this, di.x + scrollX, di.y + scrollY);
	}
	update();
    }

    public void setScrolling(int x, int y) {
	scrollX = x;
	scrollY = y;
    }

    /** Paints all of the DisplayItems in this window. */
    public void paint() {
	if (!valid)
	    validate();
	paintRange(0, height);
    }


    /** Starts the child updater thread responsible for causing
     * repaints.
     */
    synchronized void startUpdater() {
	if (updater == null) {
	    updater = new DIWUpdaterThread(this);
	    updater.start();
	}
    }

    /** Kill updater in its tracks, because we are doing something
	which could cause the updater to display a display item that
	is no longer in this window!  Stopping it in its tracks takes
	care of that race condition. */
    synchronized void stopUpdater() {
	if (updater != null) {
	    updater.die();
	    updater = null;
	}
    }

    /** Shows this window. */
    public void map() {
	super.map();
	startUpdater();
    }

    /** Hides this window. */
    public void unMap() {
	super.unMap();
	stopUpdater();
    }

    /** Requests that the given DisplayItem be repainted. */
    public void paintChild(DisplayItem di, boolean clear) {
	if (updater != null) {
	    updater.addRequest(di, clear);
	}
    }

    /** Updates the given DisplayItem. */
    public void updateChild(DisplayItem di, boolean clear) {
	int x, y;

	x = di.x + scrollX;
	y = di.y + scrollY;
	if (y + di.height < 0 || y > height) {
	    return;
	}
	if (clear) {
	    clearRect(x, y, di.width + 1, di.height + 1);
	    di.paint(this, x, y);
	} else {
	    di.update(this, x, y);
	}
	update();
    }

    public void addUpdateRequest(DIWUpdateRequest r) {
	if (updater != null) {
	    updater.addRequest(r);
	}
    }

    /** Returns the DisplayItem which contains x,y. */
    public synchronized DisplayItem locateItem(int x, int y) {
	int i = count();

	x -= scrollX;
	y -= scrollY;
	while (--i >= 0) {
	    DisplayItem	di = items[i];

	    if (di.containsPoint(x, y)) {
		return di;
	    }
	}
	return null;
    }


    /** Translates the given event to correspond to the given
     * DisplayItem's coordinate system.
     */
    Event translate(Event e, DisplayItem di) {
	tEvent.copy(e);
	tEvent.x = e.x - (di.x + scrollX);
	tEvent.y = e.y - (di.y + scrollY);

	return tEvent;
    }

    /**
     * Forwards a mouseDown event to the DisplayItem that contains the
     * current mouse coordinates.
     */
    public void mouseDown(Event e) {
	currentInputItem = locateItem(e.x, e.y);
	if (currentInputItem != null) {
	    currentInputItem.trackStart(translate(e, currentInputItem));
	}
    }

    /**
     * Forwards a mouse drag event to the DisplayItem that contains the
     * current mouse coordinates.
     */
    public void mouseDrag(Event e) {
	DisplayItem   di;

	if (stickyTracking)
	    di = currentInputItem;
	else {
	    di = locateItem(e.x, e.y);

	    if (di != currentInputItem) {
		if (currentInputItem != null) {
		    currentInputItem.trackExit(translate(e, currentInputItem));
		}
		currentInputItem = di;
		if (di != null) {
		    di.trackEnter(translate(e, di));
		}
	    }
	}
	if (di != null) {
	    di.trackMotion(translate(e, di));
	}
    }

    /** Forwards a mouse motion event to the DisplayItem that contains the
     * current mouse coordinates.
     */
    public void mouseMoved(Event e) {
	mouseDrag(e);
    }

    public void mouseUp(Event e) {
	if (currentInputItem != null) {
	    currentInputItem.trackStop(translate(e, currentInputItem));
	}
	currentInputItem = null;
    }

    public void setSticky(boolean on) {
	stickyTracking = on;
    }

    public int getScrollY() {
	return scrollY;
    }

    public void scrollAbsolute(int x, int y) {
	if (y != -scrollY) {
	    scrollVertically(-scrollY - y);
	}
    }

    /* Scrolling support. */

    synchronized public boolean scrollHorizontally(int dx) {
	return true;
    }

    synchronized public boolean scrollVertically(int dy) {
	int	sy = scrollY;
	boolean	hitLimit = false;

	if ((-sy - dy) > (logicalHeight - height)) {
	    dy = -logicalHeight + height - sy;
	    hitLimit = true;
	}
	scrollY += dy;
	if (scrollY > 0) {
	    scrollY = 0;
	    hitLimit = true;
	}
	updateScrollbar();
	if ((dy = sy - scrollY) != 0) {
	    scrollWindow(0, dy);
//	    paintRange(top, bottom);
	}
	return hitLimit;
    }

    /* Scrollbarable implementation */

    int lineScrollY = 20;

    public void lineUp() {
	scrollVertically(lineScrollY);
    }

    public void lineDown() {
	scrollVertically(-lineScrollY);
    }

    public void pageUp() {
	scrollVertically(height);
    }

    public void pageDown() {
	scrollVertically(-height);
    }

    public void dragAbsolute(int value) {
	scrollVertically(-scrollY - value);
    }

    SmoothScroller  scroller = null;

    void thrust(boolean forward) {
	if (scroller == null) {
	    scroller = new SmoothScroller(this);
	    Thread.currentThread().yield();
	}
	scroller.setThrust(forward ? 2000 : -2000, 50);
    }

    public void keyPressed(Event event) {

	//System.out.println("DisplayItemWindow.keyPressed:  event.key is " +
	//event.key + ", keyIsAscii is " + event.keyIsAscii);

	// If event.keyIsAscii is FALSE, that means this is
	//   a modifier key, like leftshift or rightshift or ctrl.
	//   We might need to keep track of *some* state here
	//   eventually, but for now ignore these events.
	// NOTE we'll need to manually keep track of the SHIFT state
	//   here if we want to be able to tell TAB and shift-TAB apart!
	if (!event.keyIsAscii)
	    return;

	switch (event.key) {
	// Intercept keyboard-focus changing keys:

	case '\t':		// TAB
	    fm.nextFocus();
	    break;
//  Also need two more cases, but we'll have to use a smarter input
//  model to identify these!
//	case 'YOW':		// Shift-TAB
//	    fm.prevFocus();
//	    break;
//	case 'YOW':		// HOME
//	    fm.resetFocus();
//	    break;

	// Anything else is a key we want to pass through to 
	//  whoever has the input focus:
	default:
	    DisplayItem fitem = fm.currentFocusItem();
	    if (fitem != null) {
		fitem.keyPressed(event.key);
	    }
	    break;
	}
    }
}

class DisplayItemUpdateRequest extends DIWUpdateRequest {
    DisplayItem	di;
    boolean	clear;

    DisplayItemUpdateRequest(DisplayItem di, boolean clear) {
	this.di = di;
	this.clear = clear;
    }

    void execute(DisplayItemWindow w) {
	w.updateChild(di, clear);
    }
}

class DIWUpdaterThread extends Thread {
    DisplayItemWindow	diw;
    Vector  clients = new Vector();

    DIWUpdaterThread(DisplayItemWindow diw) {
	setPriority(NORM_PRIORITY - 1);
	this.diw = diw;
    }

    public synchronized void addRequest(DIWUpdateRequest r) {
	clients.addElement(r);
	notify();
    }

    public void addRequest(DisplayItem di, boolean clear) {
	addRequest(new DisplayItemUpdateRequest(di, clear));
    }

    synchronized DIWUpdateRequest getRequest() {
	while (clients.size() == 0 && !stop) {
	    wait();
	}

	DIWUpdateRequest	r = null;

	if (!stop) {
	    r = (DIWUpdateRequest) clients.elementAt(0);
	    clients.removeElementAt(0);
	}
	return r;
    }

    synchronized void clear() {
	clients.setSize(0);
    }

    boolean stop = false;

    synchronized void die() {
	stop = true;
	notify();
    }

    public void run() {
	while (!stop) {
	    DIWUpdateRequest r = getRequest();

	    /* r == null if die() was called and it woke up
	       getNextClient() */
	    if (r != null) {
		/* This is done this way for the following
		   reason.  It is possible we have been told to
		   stop between the time we call diw.updateChild,
		   and the time we actually get the monitor.
		   updateChild is a synchronized method on diw.
		   If stop is called, we don't want to update the
		   child.  So, we enter diw's monitor ahead of
		   time, and then once we get it, we check to see
		   if we've stopped.  If we have, then we don't
		   paint the child, and essentially, break from
		   the loop. */
		synchronize (diw) {
		    if (!stop) {
			r.execute(diw);
		    }
		}
	    }
	}
    }
}
