/*
 * @(#)OptionMenu.java	1.15 95/02/03 Sami Shaio
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
 * OptionMenu is a pop-up menu of choices. The current choice is
 * displayed as the title of the menu.
 *
 * @version 1.15 03 Feb 1995
 * @author Sami Shaio
 */
public class OptionMenu extends Component {
    private	WServer	wServer;
    private Vector pItems;
    private int sIndex = 0;

    /** the index of the current choice for this OptionMenu */
    public int selectedIndex = 0;

    /** Constructs an OptionMenu.
     * @param p is the parent window to contain this OptionMenu
     * @param label is a string describing the OptionMenu. It can be
     * null. 
     * @param pName is the name of this OptionMenu. This name may be
     * signficant for layout purposes. 
     * @see awt.BorderLayout
     */
    public OptionMenu(Container p,
		      String label,
		      String pName) {
	super(p, pName);
	Window win = Window.getWindow(p);
	wServer = win.wServer;
	wServer.optionMenuCreate(this, win, label);
	pItems = new Vector();
    }

    /**
     * Adds an item to this OptionMenu.
     */
    public void addItem(String item) {
	wServer.optionMenuAddItem(this, item, pItems.size());
	pItems.addElement(item);
    }

    /**
     * Adds a separator line to the current position in the OptionMenu.
     */
    public void addSeparator() {
	wServer.optionMenuAddSeparator(this, sIndex++);
    }

    public Dimension getPreferredSize() {
	wServer.optionMenuDimensions(this);
	return new Dimension(width, height);
    }

    /**
     * Returns the number of items in this OptionMenu. Separators
     * don't count.
     */
    public int nItems() {
	return pItems.size();
    }

    /**
     * Returns the String at the given index in the OptionMenu.
     */
    public String itemAt(int index) {
	return (String)pItems.elementAt(index);
    }

    /**
     * Overrides this method to take some action whenever the current
     * choice for the OptionMenu changes.
     * @param index is the index of the current choice.
     */
    public void selected(int index) {
    }

    /**
     * Returns the current choice as a String.
     */
    public String selectedItem() {
	return itemAt(selectedIndex);
    }

    /**
     * Select the given item.
     */
    public void select(int pos) {
	if (pItems.size() > 0) {
		wServer.optionMenuSelect(this, pos);
		selectedIndex = pos;
	}
    }

    /**
     * Disposes of this OptionMenu. It should not be used afterwards.
     */
    public void dispose() {
	wServer.optionMenuDispose(this);
    }

    /**
     * Moves this OptionMenu.
     */
    public void move(int X, int Y) {
	super.move(X,Y);
	wServer.optionMenuMoveTo(this, X, Y);
    }

    /**
     * Reshapes this OptionMenu.
     */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.optionMenuReshape(this, x, y, w, h);
    }

    /**
     * Shows this OptionMenu.
     */
    public void map() {
	wServer.optionMenuShow(this);
	mapped = true;
    }

    /**
     * Hides this OptionMenu.
     */
    public void unMap() {
	wServer.optionMenuHide(this);
	mapped = false;
    }
}
