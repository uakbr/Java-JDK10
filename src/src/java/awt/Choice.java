/*
 * @(#)Choice.java	1.16 95/12/14 Sami Shaio
 *
 * Copyright (c) 1994-1995 Sun Microsystems, Inc. All Rights Reserved.
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
package java.awt;

import java.util.*;
import java.awt.peer.ChoicePeer;

/**
 * The Choice class is a pop-up menu of choices. The current choice is
 * displayed as the title of the menu.
 *
 * @version	1.16 12/14/95
 * @author 	Sami Shaio
 * @author 	Arthur van Hoff
 */
public class Choice extends Component {
    /**
     * The items for the Choice.
     */
    Vector pItems;

    /** 
     * The index of the current choice for this Choice.
     */
    int selectedIndex = -1;

    /** 
     * Constructs a new Choice.
     */
    public Choice() {
	pItems = new Vector();
    }

    /**
     * Creates the Choice's peer.  This peer allows us to change the look
     * of the Choice without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createChoice(this);
	super.addNotify();
    }

    /**
     * Returns the number of items in this Choice.
     * @see #getItem
     */
    public int countItems() {
	return pItems.size();
    }

    /**
     * Returns the String at the specified index in the Choice.
     * @param index the index at which to begin
     * @see #countItems
     */
    public String getItem(int index) {
	return (String)pItems.elementAt(index);
    }

    /**
     * Adds an item to this Choice.
     * @param item the item to be added
     * @exception NullPointerException If the item's value is equal to null.
     */
    public synchronized void addItem(String item) {
	if (item == null) {
	    throw new NullPointerException();
	}
	pItems.addElement(item);
	ChoicePeer peer = (ChoicePeer)this.peer;
	if (peer != null) {
	    peer.addItem(item, pItems.size() - 1);
	}
	if (selectedIndex < 0) {
	    select(0);
	}
    }

    /**
     * Returns a String representation of the current choice.
     * @see #getSelectedIndex
     */
    public String getSelectedItem() {
	int selectedIndex = this.selectedIndex;
	return (selectedIndex >= 0) ? getItem(selectedIndex) : null;
    }

    /**
     * Returns the index of the currently selected item.
     * @see #getSelectedItem
     */
    public int getSelectedIndex() {
	return selectedIndex;
    }

    /**
     * Selects the item with the specified postion.
     * @param pos the choice item position
     * @exception IllegalArgumentException If the choice item position is 
     * invalid.
     * @see #getSelectedItem
     * @see #getSelectedIndex
     */
    public synchronized void select(int pos) {
	if (pos >= pItems.size()) {
	    throw new IllegalArgumentException("illegal Choice item position: " + pos);
	}
	if (pItems.size() > 0) {
	    selectedIndex = pos;
	    ChoicePeer peer = (ChoicePeer)this.peer;
	    if (peer != null) {
		peer.select(pos);
	    }
	}
    }

    /**
     * Selects the item with the specified String.
     * @param str the specified String
     * @see #getSelectedItem
     * @see #getSelectedIndex
     */
    public void select(String str) {
	int index = pItems.indexOf(str);
	if (index >= 0) {
	    select(index);
	}
    }

    /**
     * Returns the parameter String of this Choice.
     */
    protected String paramString() {
	return super.paramString() + ",current=" + getSelectedItem();
    }
}
