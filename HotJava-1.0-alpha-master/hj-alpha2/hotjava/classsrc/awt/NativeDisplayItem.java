/*
 * @(#)NativeDisplayItem.java	1.17 95/02/02 Sami Shaio
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

import java.io.*;

/**
 * This class is used to create a DisplayItem object that encapsulates a
 * native gui element. It translates the protocols used on DisplayItem
 * to those that Component understands.
 *
 * @see DisplayItem
 * @see Component
 * @version 1.17 02 Feb 1995
 * @author Sami Shaio
 */
public class NativeDisplayItem extends DisplayItem {
    Component	component;

    public NativeDisplayItem() {}

    public NativeDisplayItem(Component c) {
	setComponent(c);
    }

    /** Sets the component for this item. */
    public void setComponent(Component c) {
	c.unMap();
	component = c;
	width = c.width;
	height = c.height;
	x = y = 0;
    }

    public Component getComponent() {
	return component;
    }

    public void move(int x, int y) {
	super.move(x, y);
    }

   public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	if (component != null && parent != null) {
	    component.reshape(x + parent.scrollX, y + parent.scrollY, w, h);
	}
    }

    public void resize(int w, int h) {
	super.resize(w, h);
	if (component != null) {
	    component.resize(w, h);
	}
    }

    public void deactivate() {
	unMap();
	component.move(0, 0);
    }

    public void setColor(Color c) {
	component.setForeground(c);
    }

    public void map() {
	component.map();
    }

    public void unMap() {
	component.unMap();
    }

    public void destroy() {
	component.dispose();
	component = null;
    }

    public void paint(Window window, int x, int y) {
	if (!component.mapped && component != null) {
	    component.move(x, y);
	    map();
	}
    }
}
