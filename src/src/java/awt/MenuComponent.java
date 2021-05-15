/*
 * @(#)MenuComponent.java	1.9 95/12/14 Arthur van Hoff
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

import java.awt.peer.MenuComponentPeer;

/**
 * The super class of all menu related components.
 *
 * @version 	1.9, 12/14/95
 * @author 	Arthur van Hoff
 */
public abstract class MenuComponent {
    MenuComponentPeer peer;
    MenuContainer parent;
    Font font;

    /**
     * Returns the parent container.
     */
    public MenuContainer getParent() {
	return parent;
    }

    /**
     * Gets the MenuComponent's peer.  The peer allows us to modify the
     * appearance of the menu component without changing the functionality of
     * the menu component.
     */
    public MenuComponentPeer getPeer() {
	return peer;
    }

    /**
     * Gets the font used for this MenuItem.
     * @return the font if one is used; null otherwise.
     */
    public Font getFont() {
	Font font = this.font;
	if (font != null) {
	    return font;
	}
	MenuContainer parent = this.parent;
	if (parent != null) {
	    return parent.getFont();
	}
	return null;
    }

    /**
     * Sets the font to be used for this MenuItem to the specified font.
     * @param f the font to be set
     */
    public void setFont(Font f) {
	font = f;
    }

    /**
     * Removes the menu component's peer.  The peer allows us to modify the
     * appearance of the menu component without changing the functionality of
     * the menu component.
     */
    public void removeNotify() {
	MenuComponentPeer p = (MenuComponentPeer)this.peer;
	if (p != null) {
	    p.dispose();
	    this.peer = null;
	}
    }

    /**
     * Posts the specified event to the menu.
     * @param evt the event which is to take place
     */
    public boolean postEvent(Event evt) {
	MenuContainer parent = this.parent;
	if (parent != null) {
	    parent.postEvent(evt);
	}
	return false;
    }

    /**
     * Returns the String parameter of this MenuComponent.
     */
    protected String paramString() {
	return "";
    }

    /**
     * Returns the String representation of this MenuComponent's values.
     */
    public String toString() {
	return getClass().getName() + "[" + paramString() + "]";
    }
}
