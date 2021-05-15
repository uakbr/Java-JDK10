/*
 * @(#)MenuBar.java	1.15 95/09/20 Sami Shaio
 *
 * Copyright (c) 1994,1995 Sun Microsystems, Inc. All Rights Reserved.
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
import java.awt.peer.MenuBarPeer;

/**
 * A class that encapsulates the platform's concept of a menu bar bound
 * to a Frame. In order to associate the MenuBar with an actual Frame,
 * the Frame.setMenuBar() method should be called.
 *
 * @see Frame#setMenuBar
 *
 * @version 1.15, 09/20/95
 * @author Sami Shaio
 *
 */
public class MenuBar extends MenuComponent implements MenuContainer {
    Vector menus = new Vector();
    Menu helpMenu;

    /**
     * Creates a new menu bar.
     */
    public MenuBar() {
    }

    /**
     * Creates the menu bar's peer.  The peer allows us to change the 
     * appearance of the menu bar without changing any of the menu bar's 
     * functionality.
     */
    public synchronized void addNotify() {
	peer = Toolkit.getDefaultToolkit().createMenuBar(this);

	int nmenus = countMenus();
	for (int i = 0 ; i < nmenus ; i++) {
	    getMenu(i).addNotify();
	}
    }

    /**
     * Removes the menu bar's peer.  The peer allows us to change the 
     * appearance of the menu bar without changing any of the menu bar's 
     * functionality.
     */
    public void removeNotify() {
	int nmenus = countMenus();
	for (int i = 0 ; i < nmenus ; i++) {
	    getMenu(i).removeNotify();
	}
	super.removeNotify();
    }

    /**
     * Gets the help menu on the menu bar.
     */
    public Menu getHelpMenu() {
	return helpMenu;
    }

    /**
     * Sets the help menu to the specified menu on the menu bar.
     * @param m the menu to be set
     */
    public synchronized void setHelpMenu(Menu m) {
	if (helpMenu != null) {
	    helpMenu.removeNotify();
	    helpMenu.parent = null;
	}

	helpMenu = m;
	if (m != null) {
	    m.isHelpMenu = true;
	    MenuBarPeer peer = (MenuBarPeer)this.peer;
	    if (peer != null) {
		if (m.peer == null) {
		    m.addNotify();
		}
		peer.addHelpMenu(m);
	    }
	}
    }

    /**
     * Adds the specified menu to the menu bar.
     * @param m the menu to be added to the menu bar
     */
    public synchronized Menu add(Menu m) {
	if (m.parent != null) {
	    m.parent.remove(m);
	}
	menus.addElement(m);
	m.parent = this;

	MenuBarPeer peer = (MenuBarPeer)this.peer;
	if (peer != null) {
	    if (m.peer == null) {
		m.addNotify();
	    }
	    peer.addMenu(m);
	}
	return m;
    }

    /**
     * Removes the menu located at the specified index from the menu bar.
     * @param index the position of the menu to be removed
     */
    public synchronized void remove(int index) {
	MenuBarPeer peer = (MenuBarPeer)this.peer;
	if (peer != null) {
	    Menu m = getMenu(index);
	    m.removeNotify();
	    m.parent = null;
	    peer.delMenu(index);
	}
	menus.removeElementAt(index);
    }

    /**
     * Removes the specified menu from the menu bar.
     * @param m the menu to be removed
     */
    public synchronized void remove(MenuComponent m) {
	int index = menus.indexOf(m);
	if (index >= 0) {
	    remove(index);
	}
    }

    /**
     * Counts the number of menus on the menu bar.
     */
    public int countMenus() {
	return menus.size();
    }

    /**
     * Gets the specified menu.
     * @param i the menu to be returned
     */
    public Menu getMenu(int i) {
	return (Menu)menus.elementAt(i);
    }
}
