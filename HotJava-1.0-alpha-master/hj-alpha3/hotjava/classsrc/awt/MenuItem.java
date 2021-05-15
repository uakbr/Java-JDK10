/*
 * @(#)MenuItem.java	1.14 95/01/31 Sami Shaio
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

/**
 * A string item that represents a choice in a menu.
 *
 * @version 1.14 31 Jan 1995
 * @author Sami Shaio
 */
public class MenuItem {
    int			pData;

    public int  	index;
    public Menu		menu;
    public String	label;


    protected MenuItem() {
    }

    /** Constructs a MenuItem.
     * @param l is the label for this menu item.
     * @param m is the menu to add this item to.
     */
    public MenuItem(String l, Menu m) {
	label = l;
	menu = m;
	index = menu.nItems();
	menu.wServer.menuItemCreate(this, label, menu, false);
	menu.addMenuItem(this);
    }

    /**
     * Constructs a MenuItem that has a checkmark associated with it.
     */
    public MenuItem(String l, Menu m, boolean isToggle) {
	label = l;
	menu = m;
	index = menu.nItems();
	menu.wServer.menuItemCreate(this, label, menu, isToggle);
	menu.addMenuItem(this);
    }

    /**
     * Sets the state of this MenuItem if it is a toggle.
     */
    public void setMark(boolean t) {
	menu.wServer.menuItemSetMark(this, t);
    }

    /**
     * Returns the state of this MenuItem. Only valid for a toggle.
     */
    public boolean getMarkState() {
	return menu.wServer.menuItemGetMark(this);
    }

    /**
     * Makes this menu item selectable by the user.
     */
    public void enable() {
	menu.wServer.menuItemEnable(this);
    }

    
    /**
     * Makes this menu item unselectable by the user.
     */
    public void disable() {
	menu.wServer.menuItemDisable(this);
    }

    /**
     * Dispose of this menu item. This removes it from the menu which
     * contains it.
     */
    public void dispose() {
	menu.wServer.menuItemDispose(this);
    }

    public void selected() {
	menu.selected(index);
    }
}
