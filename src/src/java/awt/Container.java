/*
 * @(#)Container.java	1.38 95/12/14 Arthur van Hoff
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

import java.io.PrintStream;
import java.awt.peer.ContainerPeer;

/**
 * A generic Abstract Window Toolkit(AWT) container object is a component 
 * that can contain other AWT components.
 *
 * @version 	1.38, 12/14/95
 * @author 	Arthur van Hoff
 * @author 	Sami Shaio
 */
public abstract class Container extends Component {

    /**
     * The number of components in this container.
     */
    int ncomponents;

    /** 
     * The components in this container.
     */
    Component component[] = new Component[4];

    /** 
     * Layout manager for this container.
     */
    LayoutManager layoutMgr;

    /**
     * Constructs a new Container. Containers should not be subclassed or 
     * instantiated directly.
     */
    Container() {
    }

    /** 
     * Returns the number of components in this panel.
     * @see #getComponent
     */
    public int countComponents() {
	return ncomponents;
    }

    /** 
     * Gets the nth component in this container.
     * @param n the number of the component to get
     * @exception ArrayIndexOutOfBoundsException If the nth value does not 
     * exist.
     */
    public synchronized Component getComponent(int n) {
	if ((n < 0) || (n >= ncomponents)) {
	    throw new ArrayIndexOutOfBoundsException("No such child: " + n);
	}
	return component[n];
    }

    /**
     * Gets all the components in this container.
     */
    public synchronized Component[] getComponents() {
	Component list[] = new Component[ncomponents];
	System.arraycopy(component, 0, list, 0, ncomponents);
	return list;
    }

    /**
     * Returns the insets of the container. The insets indicate the size of
     * the border of the container. A Frame, for example, will have a top inset
     * that corresponds to the height of the Frame's title bar. 
     * @see LayoutManager
     */
    public Insets insets() {
	ContainerPeer peer = (ContainerPeer)this.peer;
	return (peer != null) ? peer.insets() : new Insets(0, 0, 0, 0);
    }

    /** 
     * Adds the specified component to this container.
     * @param comp the component to be added
     */
    public Component add(Component comp) {
	return add(comp, -1);
    }

    /** 
     * Adds the specified component to this container at the given position.
     * @param comp the component to be added 
     * @param pos the position at which to insert the component. -1
     * means insert at the end.
     * @see #remove
     */
    public synchronized Component add(Component comp, int pos) {
	if (pos > ncomponents || (pos < 0 && pos != -1)) {
	    throw new IllegalArgumentException("illegal component position");
	}
	// check to see that comp isn't one of this container's parents
	if (comp instanceof Container) {
	    for (Container cn = this; cn != null; cn=cn.parent) {
		if (cn == comp) {
		    throw new IllegalArgumentException("adding container's parent to itself");
		}
	    }
	}

	if (comp.parent != null) {
	    comp.parent.remove(comp);
	}
	if (ncomponents == component.length) {
	    Component newcomponents[] = new Component[ncomponents * 2];
	    System.arraycopy(component, 0, newcomponents, 0, ncomponents);
	    component = newcomponents;
	}
	if (pos == -1 || pos==ncomponents) {
	    component[ncomponents++] = comp;
	} else {
	    System.arraycopy(component, pos, component, pos+1, ncomponents-pos);
	    component[pos] = comp;
	    ncomponents++;
	}
	comp.parent = this;
	invalidate();
	if (peer != null) {
	    comp.addNotify();
	}
	return comp;
    }

    /**
     * Adds the specified component to this container. The component
     * is also added to the layout manager of this container using the
     * name specified
.
     * @param name the component name
     * @param comp the component to be added
     * @see #remove
     * @see LayoutManager
     */
    public synchronized Component add(String name, Component comp) {
	Component c = add(comp);
	LayoutManager layoutMgr = this.layoutMgr;
	if (layoutMgr != null) {
	    layoutMgr.addLayoutComponent(name, comp);
	}
	return c;
    }

    /** 
     * Removes the specified component from this container.
     * @param comp the component to be removed
     * @see #add
     */
    public synchronized void remove(Component comp) {
	if (comp.parent == this)  {
	    for (int i = 0 ; i < ncomponents ; i++) {
		if (component[i] == comp) {
		    if (peer != null) {
			comp.removeNotify();
		    }
		    if (layoutMgr != null) {
			layoutMgr.removeLayoutComponent(comp);
		    }
		    comp.parent = null;
		    System.arraycopy(component, i + 1, component, i, ncomponents - i - 1);
		    component[--ncomponents] = null;
		    invalidate();
		    return;
		}
	    }
	}
    }

    /** 
     * Removes all the components from this container.
     * @see #add
     * @see #remove
     */
    public synchronized void removeAll() {
	while (ncomponents > 0) {
	    Component comp = component[--ncomponents];
	    component[ncomponents] = null;

	    if (peer != null) {
		comp.removeNotify();
	    }
	    if (layoutMgr != null) {
		layoutMgr.removeLayoutComponent(comp);
	    }
	    comp.parent = null;
	}
	invalidate();
    }

    /** 
     * Gets the layout manager for this container.  
     * @see #layout
     * @see #setLayout
     */
    public LayoutManager getLayout() {
	return layoutMgr;
    }

    /** 
     * Sets the layout manager for this container.
     * @param mgr the specified layout manager
     * @see #layout
     * @see #getLayout
     */
    public void setLayout(LayoutManager mgr) {
	layoutMgr = mgr;
	invalidate();
    }

    /** 
     * Does a layout on this Container. 
     * @see #setLayout
     */
    public synchronized void layout() {
	LayoutManager layoutMgr = this.layoutMgr;
	if (layoutMgr != null) {
	    layoutMgr.layoutContainer(this);
	}
    }

    /** 
     * Validates this Container and all of the components contained within it. 
     * @see #validate
     * @see Component#invalidate
     */
    public synchronized void validate() {
	super.validate();
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (!comp.valid) {
		comp.validate();
	    }
	}
    }

    /** 
     * Returns the preferred size of this container.  
     * @see #minimumSize
     */
    public synchronized Dimension preferredSize() {
	LayoutManager layoutMgr = this.layoutMgr;
	return (layoutMgr != null) ? layoutMgr.preferredLayoutSize(this) : super.preferredSize();
    }

    /** 
     * Returns the minimum size of this container.  
     * @see #preferredSize
     */
    public synchronized Dimension minimumSize() {
	LayoutManager layoutMgr = this.layoutMgr;
	return (layoutMgr != null) ? layoutMgr.minimumLayoutSize(this) : super.minimumSize();
    }

    /** 
     * Paints the components in this container.
     * @param g the specified Graphics window
     * @see Component#paint
     * @see Component#paintAll
     */
    public void paintComponents(Graphics g) {
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		Graphics cg = g.create(comp.x, comp.y, comp.width, comp.height);
		try {
		    comp.paintAll(cg);
		} finally {
		    cg.dispose();
		}
	    }
	}
    }

    /** 
     * Prints the components in this container.
     * @param g the specified Graphics window
     * @see Component#print
     * @see Component#printAll
     */
    public void printComponents(Graphics g) {
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		Graphics cg = g.create(comp.x, comp.y, comp.width, comp.height);
		try {
		    comp.printAll(cg);
		} finally {
		    cg.dispose();
		}
	    }
	}
    }

    /**
     * Delivers an event. The appropriate component is located and
     * the event is delivered to it.
     * @param e the event
     * @see Component#handleEvent
     * @see Component#postEvent
     */
    public void deliverEvent(Event e) {
	Component comp = locate(e.x, e.y);

	if ((comp != null) && (comp != this)) {
	    e.translate(-comp.x, -comp.y);
	    comp.deliverEvent(e);
	} else {
	    postEvent(e);
	}
    }

    /**
     * Locates the component that contains the x,y position.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return null if the component is not within the x and y
     * coordinates; returns the component otherwise. 
     * @see Component#inside 
     */
    public Component locate(int x, int y) {
	if (!inside(x, y)) {
	    return null;
	}
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if ((comp != null) && comp.inside(x - comp.x, y - comp.y)) {
		return comp;
	    }
	}
	return this;
    }

    /** 
     * Notifies the container to create a peer. It will also
     * notify the components contained in this container.
     * @see #removeNotify
     */
    public synchronized void addNotify() {
	for (int i = 0 ; i < ncomponents ; i++) {
	    component[i].addNotify();
	}
	super.addNotify();
    }

    /** 
     * Notifies the container to remove its peer. It will
     * also notify the components contained in this container.
     * @see #addNotify
     */
    public synchronized void removeNotify() {
	for (int i = 0 ; i < ncomponents ; i++) {
	    component[i].removeNotify();
	}
	super.removeNotify();
    }

    /**
     * Returns the parameter String of this Container.
     */
    protected String paramString() {
	String str = super.paramString();
	LayoutManager layoutMgr = this.layoutMgr;
	if (layoutMgr != null) {
	    str += ",layout=" + layoutMgr.getClass().getName();
	}
	return str;
    }

    /**
     * Prints out a list, starting at the specified indention, to the specified
     * out stream. 
     * @param out the Stream name
     * @param indent the start of the list
     */
    public void list(PrintStream out, int indent) {
	super.list(out, indent);
	for (int i = 0 ; i < ncomponents ; i++) {
	    Component comp = component[i];
	    if (comp != null) {
		comp.list(out, indent+1);
	    }
	}
    }
}
