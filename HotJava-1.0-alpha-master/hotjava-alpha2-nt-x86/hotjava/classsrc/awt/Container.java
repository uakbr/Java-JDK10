/*
 * @(#)Container.java	1.17 95/02/16 Arthur van Hoff
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
 * A container object is a component that can contain other
 * components. Examples are windows, and frames.
 *
 * @version 1.17 16 Feb 1995
 * @author Arthur van Hoff, Sami Shaio
 */
public class Container extends Component {
    /**
     * The window server associated with this component.
     */
    public WServer	wServer;

    /**
     * The member objects (only nmembers are used)
     */
    public ChildList children = new ChildList();

    /**
     * A layout object (or 0). Used to layout the
     * member object when the container is untidy.
     */
    public ContainerLayout theLayout;

    /**
     * The insets (distances from the sides) of this
     * container.
     */
    public int insets[] = new int[4];

    /**
     * Contructor
     */
    public Container(Container pParent, String pName) {
	super(pParent, pName);
	setInsets(5,5,5,5);
    }

    /**
     * Add a named child to the container (at the end,
     * front most).
     */
    public void addChild(Layoutable c, String name) {
	children.addChild(c, name);
    }

    /**
     * Add an unnamed child to the container.
     */
    public void addChild(Layoutable c) {
	children.addChild(c, null);
    }

    /**
     * Find a member component by name.
     */
    public Layoutable getChild(String pName) {
	return children.getChild(pName);
    }

    /**
     * Find a member component by position.
     */
    public Layoutable getChild(int pos) {
	return children.getChild(pos);
    }

    /** 
     * Return the number of children in this container.
     */
    public int nChildren() {
	return children.length();
    }

    /**
     * Dispose of the container and its members, releasing
     * all of its resources.
     */
    public void dispose() {
	int 		i;
	int nmembers = children.length();

	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable w = children.getChild(i);
	    if (w instanceof Component) {
		((Component)w).dispose();
	    }
	}
	super.dispose();
    }

    /**
     * Return the preferred size for this container.
     */
    public Dimension getPreferredSize() {
	return (theLayout != null) ?
	    theLayout.getPreferredSize(this) :
	    super.getPreferredSize();
    }

    /**
     * Return the minimum acceptable size for this container.
     */
    public Dimension minDimension() {
	return (theLayout != null) ?
	    theLayout.minDimension(this) : super.minDimension();
    }

    /**
     * Map the container and its members, note that
     * super.Map() will layout the member object if
     * needed.
     */
    public synchronized void map() {
	int i;
	int nmembers = children.length();
	
	if (mapped) {
	    return;
	}

	super.map();

	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable w = children.getChild(i);
	    if (w instanceof Component) {
		((Component)w).map();
	    }
	}
    }

    /**
     * UnMap the container and its members.
     */
    public synchronized void unMap() {
	int i;
	int nmembers = children.length();
	
	if (!mapped) {
	    return;
	}

	super.unMap();
	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable w = children.getChild(i);
	    if (w instanceof Component) {
		((Component)w).unMap();
	    }
	}
    }

    /**
     * Change the layout of the container.
     */
    public synchronized void setLayout(ContainerLayout pLayout) {
	theLayout = pLayout;
	tidy = false;
    }

    /**
     * Move the container to the given position.
     */
    public void move(int pX, int pY) {
	int	nchildren = children.length();
	int	i;
	int	dx = pX - x;
	int	dy = pY - y;

	x = pX;
	y = pY;

	for (i=0; i < nchildren; i++) {
	    Layoutable m = getChild(i);

	    if (m instanceof Component) {
		Component c = (Component)m;

		c.move(c.x + dx, c.y + dy);
	    }
	}
    }

    /**
     * Layout the container and its members.
     */
    public synchronized void layout() {
	int i;
	int nmembers = children.length();

	if ((theLayout != null) && (!tidy)) {
	    theLayout.layout(this);
	}
	super.layout();
	for (i = 0 ; i < nmembers ; i++) {
	    Layoutable w = children.getChild(i);
	    if (w instanceof Component) {
		((Component)w).layout();
	    }
	}
    }

    /**
     * Set the insets of this container
     */
    public void setInsets(int n, int e, int s, int w) {
	insets[ContainerLayout.NORTH] = n;
	insets[ContainerLayout.EAST] = e;
	insets[ContainerLayout.SOUTH] = s;
	insets[ContainerLayout.WEST] = w;
    }

    /**
     * Change the insets of this container
     */
    public void addInsets(int n, int e, int s, int w) {
	insets[ContainerLayout.NORTH] += n;
	insets[ContainerLayout.EAST]  += e;
	insets[ContainerLayout.SOUTH] += s;
	insets[ContainerLayout.WEST]  += w;
	tidy = false;
    }
}
