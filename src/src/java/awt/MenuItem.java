/*
 * @(#)MenuItem.java	1.15 95/12/14 Sami Shaio
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

import java.awt.peer.MenuItemPeer;

/**
 * A String item that represents a choice in a menu.
 *
 * @version 1.15, 12/14/95
 * @author Sami Shaio
 */
public class MenuItem extends MenuComponent {
    boolean enabled = true;
    String label;

    /** 
     * Constructs a new MenuItem with the specified label.
     * @param label the label for this menu item. Note that "-" is
     * reserved to mean a separator between menu items.
     */
    public MenuItem(String label) {
	this.label = label;
    }

    /**
     * Creates the menu item's peer.  The peer allows us to modify the 
     * appearance of the menu item without changing its functionality.
     */
    public synchronized void addNotify() {
	if (peer == null) {
	    peer = Toolkit.getDefaultToolkit().createMenuItem(this);
	}
    }

    /**
     * Gets the label for this menu item.
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets the label to be the specified label.
     * @param label the label for this menu item
     */
    public void setLabel(String label) {
	this.label = label;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /**
     * Checks whether the menu item is enabled.
     */
    public boolean isEnabled() {
	return enabled;
    }

    /**
     * Makes this menu item selectable by the user.
     */
    public void enable() {
	enabled = true;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.enable();
	}
    }

    /**
     * Conditionally enables a component.
     * @param cond enabled if true; disabled otherwise.
     * @see #enable
     * @see #disable
     */
    public void enable(boolean cond) {
	if (cond) {
	    enable();
	} else {
	    disable();
	}
    }

    /**
     * Makes this menu item unselectable by the user.
     */
    public void disable() {
	enabled = false;
	MenuItemPeer peer = (MenuItemPeer)this.peer;
	if (peer != null) {
	    peer.disable();
	}
    }

    /**
     * Returns the String parameter of the menu item.
     */
    public String paramString() {
	return "label=" + label;
    }
}
