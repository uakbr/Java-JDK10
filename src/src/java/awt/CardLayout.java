/*
 * @(#)CardLayout.java	1.11 95/12/14 Arthur van Hoff
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

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * A layout manager for a container that contains several
 * 'cards'. Only one card is visible at a time,
 * allowing you to flip through the cards.
 *
 * @version 	1.11 12/14/95
 * @author 	Arthur van Hoff
 */

public class CardLayout implements LayoutManager {
    Hashtable tab = new Hashtable();
    int hgap;
    int vgap;

    /**
     * Creates a new card layout.
     */
    public CardLayout() {
	this(0, 0);
    }

    /**
     * Creates a card layout with the specified gaps.
     * @param hgap the horizontal gap
     * @param vgap the vertical gap
     */
    public CardLayout(int hgap, int vgap) {
	this.hgap = hgap;
	this.vgap = vgap;
    }

    /**
     * Adds the specified component with the specified name to the layout.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
	if (tab.size() > 0) {
	    comp.hide();
	}
	tab.put(name, comp);
    }

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
	for (Enumeration e = tab.keys() ; e.hasMoreElements() ; ) {
	    String key = (String)e.nextElement();
	    if (tab.get(key) == comp) {
		tab.remove(key);
		return;
	    }
	}
    }

    /** 
     * Calculates the preferred size for the specified panel.
     * @param parent the name of the parent container
     * @return the dimensions of this panel. 
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
	Insets insets = parent.insets();
	int ncomponents = parent.countComponents();
	int w = 0;
	int h = 0;

	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.preferredSize();
	    if (d.width > w) {
		w = d.width;
	    }
	    if (d.height > h) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + w + hgap*2, 
			     insets.top + insets.bottom + h + vgap*2);
    }

    /** 
     * Calculates the minimum size for the specified panel.
     * @param parent the name of the parent container
     * @return the dimensions of this panel. 
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
	Insets insets = parent.insets();
	int ncomponents = parent.countComponents();
	int w = 0;
	int h = 0;

	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = parent.getComponent(i);
	    Dimension d = comp.minimumSize();
	    if (d.width > w) {
		w = d.width;
	    }
	    if (d.height > h) {
		h = d.height;
	    }
	}
	return new Dimension(insets.left + insets.right + w + hgap*2, 
			     insets.top + insets.bottom + h + vgap*2);
    }

    /** 
     * Performs a layout in the specified panel.
     * @param parent the name of the parent container 
     */
    public void layoutContainer(Container parent) {
	synchronized (parent) {
	    Insets insets = parent.insets();
	    int ncomponents = parent.countComponents();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.reshape(hgap + insets.left, vgap + insets.top, 
				 parent.width - (hgap*2 + insets.left + insets.right), 
				 parent.height - (vgap*2 + insets.top + insets.bottom));
		}
	    }
	}
    }

    /**
     * Make sure that the Container really has a CardLayout installed.
     * Otherwise havoc can ensue!
     */
    void checkLayout(Container parent) {
	if (parent.getLayout() != this) {
	    throw new IllegalArgumentException("wrong parent for CardLayout");
	}
    }

    /**
     * Flip to the first card.
     * @param parent the name of the parent container
     */
    public void first(Container parent) {
	synchronized (parent) {
	    checkLayout(parent);
	    int ncomponents = parent.countComponents();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent(0);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the next card of the specified container.
     * @param parent the name of the container
     */
    public void next(Container parent) {
	synchronized (parent) {
	    checkLayout(parent);
	    int ncomponents = parent.countComponents();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent((i + 1 < ncomponents) ? i+1 : 0);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the previous card of the specified container.
     * @param parent the name of the parent container
     */
    public void previous(Container parent) {
	synchronized (parent) {
	    checkLayout(parent);
	    int ncomponents = parent.countComponents();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent((i > 0) ? i-1 : ncomponents-1);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the last card of the specified container.
     * @param parent the name of the parent container
     */
    public void last(Container parent) {
	synchronized (parent) {
	    checkLayout(parent);
	    int ncomponents = parent.countComponents();
	    for (int i = 0 ; i < ncomponents ; i++) {
		Component comp = parent.getComponent(i);
		if (comp.visible) {
		    comp.hide();
		    comp = parent.getComponent(ncomponents - 1);
		    comp.show();
		    parent.validate();
		    return;
		}
	    }
	}
    }

    /**
     * Flips to the specified component name in the specified container.
     * @param parent the name of the parent container
     * @param name the component name
     */
    public void show(Container parent, String name) {
	synchronized (parent) {
	    checkLayout(parent);
	    Component next = (Component)tab.get(name);
	    if ((next != null) && !next.visible){
		int ncomponents = parent.countComponents();
		for (int i = 0 ; i < ncomponents ; i++) {
		    Component comp = parent.getComponent(i);
		    if (comp.visible) {
			comp.hide();
			break;
		    }
		}
		next.show();
		parent.validate();
	    }
	}
    }
    
    /**
     * Returns the String representation of this CardLayout's values.
     */
    public String toString() {
	return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
