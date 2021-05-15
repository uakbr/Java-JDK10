/*
 * @(#)Label.java	1.19 95/02/03 Sami Shaio
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
 * A class that displays read-only text.
 *
 * @version 1.19 03 Feb 1995
 * @author Sami Shaio
 */
public class Label extends Component {
    private	WServer	wServer;

    boolean hFill = false;
    public String label;


    /**
     * Constructs a label.
     * @param pLabel the text of the label.
     * @param pName the name of this component.
     * @param pParent the parent window for this label.
     * @param font the font to use for this label.
     */
    public Label(String pLabel, String pName, Container pParent, Font font) {
	super(pParent, pName);
	label = pLabel;
	Window win = Window.getWindow(parent);
	wServer = win.wServer;
	wServer.labelCreate(this, label, win, font);
	hFill = false;
    }

    /** Constructs a label with the default font. */
    public Label(String pLabel, String pName, Container pParent) {
	this(pLabel, pName, pParent, null);
    }


    /** Makes this label stretch to be the width of its container. */
    public void setHFill(boolean t) {
	hFill = t;
	if (!hFill) {
	    // force getPreferredSize to be recomputed because it could
	    // have been set to hFill
	    dim = null;
	}
    }

    /** Sets the font to use for this label. */
    public void setFont(Font f) {
	wServer.labelSetFont(this, f);
    }

    /** Sets the color to draw this label. */
    public void setColor(Color c) {
	wServer.labelSetColor(this, c);
    }

    /** Sets the text to display for this label. */
    public void setText(String l) {
	if (!l.equals(label)) {
	    label = l;
	    wServer.labelSetText(this, l);
	}
    }

    /** Move this label to the given x,y position. */
    public void move(int x, int y) {
	super.move(x, y);
	wServer.labelMoveTo(this, x, y);
    }
    /** Reshapes this label to the given dimensions. */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.labelReshape(this, x, y, w, h);
    }
    /** Disposes of this label. */
    public void dispose() {
	wServer.labelDispose(this);
    }

    /** Return the preferred size of this label. */
    public Dimension getPreferredSize() {
	wServer.labelDimensions(this);

	if (hFill) {
	    dim = new Dimension(parent.width, height);
	} else {
	    dim = new Dimension(width, height);
	}
	return dim;
    }

    /** Make this label visible. */
    public void map() {
	wServer.labelShow(this);
    }

    /** Make this label invisible. */
    public void unMap() {
	wServer.labelHide(this);
    }
}
