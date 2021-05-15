/*
 * @(#)Menu.java	1.17 95/12/14 Sami Shaio
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
package java.awt;

import java.util.Vector;
import java.awt.peer.MenuPeer;

/**
 * A Menu that is a component of a menu bar.
 *
 * @version 1.17, 12/14/95
 * @author Sami Shaio
 */
public class Menu extends MenuItem implements MenuContainer {
    Vector		items = new Vector();
    boolean		tearOff;
    boolean		isHelpMenu;

    /** 
     * Constructs a new Menu with the specified label.  This menu can
     * not be torn off - the menu will still appear on screen after
     * the the mouse button has been released.

     * @param label the label to be added to this menu 
     */
    public Menu(String label) {
	this(label, false);
    }

    /** 
     * Constructs a new Menu with the specified label. If tearOff is
     * true, the menu can be torn off - the menu will still appear on
     * screen after the the mouse button has been released.

     * @param label the label to be added to this menu
     * @param tearOff the boolean indicating whether or not the menu will be
     * able to be torn off.
     */
    public Menu(String label, boolean tearOff) {
	super(label);
	this.tearOff = tearOff;
    }

    /**
     * Creates the menu's peer.  The peer allows us to modify the 
     * appearance of the menu without changing its functionality.
     */
    public synchronized void addNotify() {
	if (peer == null) {
	    peer = Toolkit.getDefaultToolkit().createMenu(this);
	}
	int nitems = countItems();
	for (int i = 0 ; i < nitems ; i++) {
	    MenuItem mi = getItem(i);
	    mi.parent = this;
	    mi.addNotify();
	}
    }

    /**
     * Removes the menu's peer.  The peer allows us to modify the appearance
     * of the menu without changing its functionality.
     */
    public synchronized void removeNotify() {
	int nitems = countItems();
	for (int i = 0 ; i < nitems ; i++) {
	    getItem(i).removeNotify();
	}
	super.removeNotify();
    }

    /**
     * Returns true if this is a tear-off menu.  
     */
    public boolean isTearOff() {
	return tearOff;
    }

    /** 
      * Returns the number of elements in this menu.
      */
    public int countItems() {
	return items.size();
    }

    /**
     * Returns the item located at the specified index of this menu.
     * @param index the position of the item to be returned
     */
    public MenuItem getItem(int index) {
	return (MenuItem)items.elementAt(index);
    }

    /**
     * Adds the specified item to this menu.
     * @param mi the item to be added
     */
    public synchronized MenuItem add(MenuItem mi) {
	if (mi.parent != null) {
	    mi.parent.remove(mi);
	}
	items.addElement(mi);
	mi.parent = this;
	if (peer != null) {
	    mi.addNotify();
	    ((MenuPeer)peer).addItem(mi);
	}
	return mi;
    }

    /**
     * Adds an item with with the specified label to this menu.
     * @param label the text on the item
     */
    public void add(String label) {
	add(new MenuItem(label));
    }

    /**
     * Adds a separator line, or a hypen, to the menu at the current position.
     */
    public void addSeparator() {
	add("-");
    }

    /**
     * Deletes the item from this menu at the specified index.
     * @param index the position of the item to be removed 
     */
    public synchronized void remove(int index) {
	MenuItem mi = getItem(index);
	items.removeElementAt(index);
	MenuPeer peer = (MenuPeer)this.peer;
	if (peer != null) {
	    mi.removeNotify();
	    mi.parent = null;
	    peer.delItem(index);
	}
    }

    /**
     * Deletes the specified item from this menu.
     * @param item the item to be removed from the menu
     */
    public synchronized void remove(MenuComponent item) {
	int index = items.indexOf(item);
	if (index >= 0) {
	    remove(index);
	}
    }
}
