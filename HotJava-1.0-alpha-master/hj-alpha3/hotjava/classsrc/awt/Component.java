/*
 * @(#)Component.java	1.15 95/02/03 Arthur van Hoff
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
 * The parent class of all native GUI components.
 *
 * @version 1.15 03 Feb 1995
 * @author Arthur van Hoff, Sami Shaio
 */
public class Component implements EventHandler, Layoutable {
    /**
     * Really a pointer to platform-specific data.
     */
    int		pData;

    /**
     * The parent of the object, this may be 0 for windows.
     */
    public Container parent;


    /**
     * The symbolic name of the object. Often used for layout.
     */
    public String	name;

    /**
     * The dimensions of the object.
     */
    public int x;
    public int y;
    public int width;
    public int height;
    public int marginHeight;
    public int marginWidth;

    /**
     * True when the object is mapped.
     */
    public boolean mapped;

    /**
     * True when the object is tidy (layed out).
     */
    protected boolean tidy;


    /**
     * If not 0, then this is the minimum dimension.
     */
    protected Dimension dim;

    /**
     * Constructor
     */
    Component(Container pParent, String pName, boolean addToContainer) {
	parent = pParent;
	name = pName;
	marginWidth = 0;
	marginHeight = 0;
	if (parent != null && addToContainer) {
	    if (name == null || name.length() == 0) {
		parent.addChild(this);
	    } else {
		parent.addChild(this, name);
	    }
	}
    }

    /**
     * Constructor
     */
    Component(Container pParent, String pName) {
	this(pParent, pName, true);
    }

    /**
     * Destructor (must be called to release resources).
     */
    void dispose() {
    }

    /**
     * Maps the object (called automatically when the
     * parent is mapped). This has no effect if the parent
     * is not mapped.
     */
    public void map() {
	if (!tidy) {
	    layout();
	}
	mapped = true;
    }

    /**
     * UnMaps the object (called automatically when the
     * parent is unmapped).
     */
    public void unMap() {
	mapped = false;
    }

    /**
     * Reshapes the component. Leaves the object untidy. This
     * means that Layout must be called to tidy it up.
     */
    public void reshape(int pX, int pY, int pW, int pH) {
	x = pX;
	y = pY;
	width = pW;
	height = pH;
	tidy = false;
	if (parent != null) {
	    parent.tidy = false;
	}
    }

    /**
     * Moves the component to a new location.
     */
    public void move(int pX, int pY) {
	x = pX;
	y = pY;
    }

    /**
     * Changes the size of the component.
     */
    public void resize(int pW, int pH) {
	width = pW;
	height = pH;
	reshape(x, y, width, height);
    }

    /**
     * Calls layout() on the component (most commonly used by
     * container objects).
     */
    public void layout() {
	tidy = true;
    }

    /**
     * Returns the natural size of the object.
     */
    public Dimension getPreferredSize() {
	dim = new Dimension(width, height);
	
	return dim;
    }

    /**
     * Returns child by name, only valid for containers. Override if
     * needed. 
     */
    public Layoutable getChild(String name) {
	return null;
    }
    
    /**
     * Returns the minimum size of the object.
     */
    public Dimension minDimension() {
	return getPreferredSize();
    }

    /**
     * Sets the dimension of the component.
     */
    public void setDimension(Dimension d) {
	dim = d;
    }

    /**
     * Sets the foreground color of this component (if applicable).
     */ 
    public void setForeground(Color c) {
    }

    /**
     * Sets the dimension of the component.
     */
    public void setDimension(int pW, int pH) {
	setDimension(new Dimension(pW, pH));
    }

    /**
     * Posting Events
     */

    public boolean postEvent(Event e) {
	if (handleEvent(e)) {
	    return true;
	}
	if (parent != null) {
	    if (parent.postEvent(e)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Handle Events
     */
    public boolean handleEvent(Event e) {
	return false;
    }
}
