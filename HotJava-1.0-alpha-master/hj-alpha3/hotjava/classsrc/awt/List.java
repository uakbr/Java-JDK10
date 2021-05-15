/*
 * @(#)List.java	1.18 95/02/03 Sami Shaio
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

import java.lang.*;
import java.util.*;

/**
 * A scrolling list of text items.
 *
 * @version 1.18 03 Feb 1995
 * @author Sami Shaio
 */
public class List extends Component implements ChoiceHandler {
    private WServer		wServer;
    private boolean		hFill;
    private boolean		vFill;
    private Vector		items;
    private ChoiceHandler	handler;

    /**
     * Creates a scrolling list.
     * @param p is the parent window for this List.
     * @param ch is the object that will handle selections for this List.
     * @param visibleLines is the number of items to show.
     * @param multipleSelections if true then multiple selections are allowed.
     * @param resizable if true then the list will attempt to grow or
     * shrink to match the width of the items it contains.
     */
    public List(Container p,
		ChoiceHandler ch,
		String pName,
		int visibleLines,
		boolean multipleSelections,
		boolean resizable) {
	super(p, pName);
	if (ch == null) {
	    handler = this;
	} else {
	    handler = ch;
	}
	Window win = Window.getWindow(p);
	wServer = win.wServer;
	wServer.listCreate(this,
				  win,
				  handler,
				  visibleLines,
				  multipleSelections,
				  resizable);
	items = new Vector();
	hFill = false;
	vFill = false;
    }

    public List(Container p,
		ChoiceHandler ch,
		String pName,
		int visibleLines,
		boolean multipleSelections) {
	this(p,ch,pName, visibleLines,multipleSelections,true);
    }

    /**
     * This is the callback method for a ChoiceHandler interface. It
     * will be called when the use single-clicks on an item in the
     * scrolling list. If pos is -1 then this indicates multiple
     * selections. Override to do something useful.
     */
    public void selected(Component c, int pos) {
    }

    /**
     * This is invoked when the user double-clicks on an item in the
     * list. Override to do something useful.
     */
    public void doubleClick(Component c, int pos) {
    }

    /**
     * Return true if the given item has been selected.
     */
    public boolean isSelected(int pos) {
	if (pos >= 0 && pos < items.size()) {
	    return wServer.listIsSelected(this, pos);
	} else {
	    throw new IllegalArgumentException();
	}
    }

    /**
     * Adds an item to the scrolling list.
     */
    public void addItem(String item) {
	wServer.listAddItem(this, item);
	items.addElement(item);
    }

    /**
     * Gets the string associated with the given position.
     */
    public String itemAt(int pos) {
	int itemCount = items.size();

	if (pos >= 0 && pos < itemCount) {
	    return (String)items.elementAt(pos);
	} else {
	    return null;
	}
    }

    /**
     * Clears the list.
     */
    public void clear() {
	int itemCount = items.size();

	if (itemCount > 0) {
	    delItems(0, itemCount - 1);
	    items.removeAllElements();
	}
    }

    /**
     * Set the number of visible lines.
     */
    public void setNVisible(int nLines) {
	wServer.listSetNVisible(this, nLines);
    }

    /**
     * Deletes one item at the given position.
     */
    public void delItem(int pos) {
	delItems(pos, pos);
    }

    /**
     * Deletes the items from start to end.
     */
    public void delItems(int start, int end) {
	int i;
	int itemCount = items.size();

	if (start >= 0 && start < itemCount &&
	    end >= 0 && end < itemCount) {
	    for (i=start; i <= end; i++) {
		items.removeElementAt(i);
	    }
	    wServer.listDelItems(this, start, end);
	} else {
	    throw new IllegalArgumentException();
	}
    }

    /** Sets whether or not this List stretches horizontally. */
    public void setHFill(boolean t) {
	hFill = t;
    }

    /** Sets whether or not this List stretches vertically. */
    public void setVFill(boolean t) {
	vFill = t;
    }

    public Dimension getPreferredSize() {
	wServer.listDimensions(this);
	return new Dimension((hFill) ? parent.width : width,
			     (vFill) ? parent.height : height);
    }

    public Dimension minDimension() {
	Dimension dim;
	wServer.listDimensions(this);
	dim =  new Dimension(width, height);
	return dim;
    }

    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.listReshape(this, x, y, w, h);
    }

    public void move(int X, int Y) {
	super.move(X,Y);
	wServer.listMoveTo(this, X, Y);
    }

    /**
     * Select the given item.
     */
    public void select(int pos) {
	wServer.listSelect(this, pos);
    }

    /**
     * Deselect the given item.
     */
    public void deselect(int pos) {
	wServer.listDeselect(this, pos);
    }

    /**
     * Force the given position to be visible.
     */
    public void makeVisible(int pos) {
	wServer.listMakeVisible(this, pos);
    }

    /**
     * Returns the number of items in the list.
     */
    public int nItems() {
	return items.size();
    }

    public void map() {
	wServer.listShow(this);
	mapped = true;
    }
    public void unMap() {
	wServer.listHide(this);
	mapped = false;
    }
    public void dispose() {
	wServer.listDispose(this);
    }
}

	
