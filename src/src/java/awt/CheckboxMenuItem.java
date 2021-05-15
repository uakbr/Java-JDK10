/*
 * @(#)CheckboxMenuItem.java	1.8 95/09/08 Sami Shaio
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
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

import java.awt.peer.CheckboxMenuItemPeer;

/**
 * This class produces a checkbox that represents a choice in a menu.
 *
 * @version 1.8, 09/08/95
 * @author 	Sami Shaio
 */
public class CheckboxMenuItem extends MenuItem {
    boolean state = false;

    /**
     * Creates the checkbox item with the specified label.
     * @param label the button label
     */
    public CheckboxMenuItem(String label) {
	super(label);
    }

    /**
     * Creates the peer of the checkbox item.  This peer allows us to
     * change the look of the checkbox item without changing its 
     * functionality.
     */
    public synchronized void addNotify() {
	peer = Toolkit.getDefaultToolkit().createCheckboxMenuItem(this);
	super.addNotify();
    }

    /**
     * Returns the state of this MenuItem. This method is only valid for a 
     * Checkbox.
     */
    public boolean getState() {
	return state;
    }

    /**
     * Sets the state of this MenuItem if it is a Checkbox.
     * @param t the specified state of the checkbox
     */
    public void setState(boolean t) {
	state = t;
	CheckboxMenuItemPeer peer = (CheckboxMenuItemPeer)this.peer;
	if (peer != null) {
	    peer.setState(t);
	}
    }

    /**
     * Returns the parameter String of this button.
     */
    public String paramString() {
	return super.paramString() + ",state=" + state;
    }
}
