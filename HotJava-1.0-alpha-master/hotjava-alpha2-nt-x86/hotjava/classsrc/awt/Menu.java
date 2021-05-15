/*
 * @(#)Menu.java	1.16 95/01/31 Sami Shaio
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
 * A Menu that is a component of a menu bar.
 *
 * @version 1.16 31 Jan 1995
 * @author Sami Shaio
 */
public class Menu extends Component  {
    Vector		pItems;
    WServer		wServer;
    Font		defaultFont;
    public String	title;

    /** Constructs a new Menu with the given title. It will be placed
     * at the end of any existing menus in mb.
     */
    public Menu(String title, MenuBar mb) {
	this(title, mb, true);
    }

    /** Constructs a new Menu with the given title. The menu will be able
     * to be torn off if tearOff is true.
     */
    public Menu(String title, MenuBar mb, boolean tearOff) {
	super(null, "");
	title = title;
	pItems = new Vector();
	wServer = mb.parent.wServer;
	defaultFont = mb.defaultFont;
	wServer.menuCreate(this, title, mb, tearOff);
	mb.addMenu(this);
    }

    /** Returns the number of elements in this menu.
      */
    public int nItems() {
	return pItems.size();
    }

    /**
     * Adds a separator line to the menu at the current position.
     */
    public void addSeparator() {
	wServer.menuAddSeparator(this);
    }

    /** Shows this menu. */
    public void show() {
	wServer.menuShow(this);
    }

    /** Hides this menu. */
    public void hide() {
	wServer.menuHide(this);
    }
    
    /** Destroys this menu. */
    public void dispose() {
	wServer.menuDispose(this);
    }

    /** Override this method to handle any items that are selected.
     * @param index is the index of the item that was selected.
     */
    public void selected(int index) {
    }

    void addMenuItem(MenuItem mi) {
	pItems.addElement(mi);
    }
}

