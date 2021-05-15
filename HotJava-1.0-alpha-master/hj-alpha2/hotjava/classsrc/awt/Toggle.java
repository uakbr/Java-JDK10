/*
 * @(#)Toggle.java	1.12 95/02/03 Sami Shaio
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
 * A Toggle object is a gui element that has a boolean state.
 *
 * @version 1.12 03 Feb 1995
 * @author Sami Shaio
 */
public class Toggle extends Component {
    private	WServer	wServer;
    public String label;
    public RadioGroup group = null;

    /**
     * Constructs a Toggle.
     * @param pLabel is the label of this toggle button.
     * @param pName is the name of this Toggle.
     * @param pParent is the window to contain this Toggle.
     * @param group is the RadioGroup this Toggle is in. If not null,
     * then this Toggle becomes a radio button which means only one
     * Toggle in a RadioGroup may be set.
     * @param state is the initial state of this Toggle.
     */
    public Toggle(String pLabel,
		  String pName,   
		  Container pParent,
		  RadioGroup group,
		  boolean state) {
	super(pParent, pName);
	label = pLabel;
	this.group = group;
	Window win = Window.getWindow(parent);
	wServer = win.wServer;
	wServer.toggleCreate(this,
				    label,
				    win,
				    group,
				    state);
	if (state && group != null) {
	    group.setCurrent(this);
	}
    }

    /**
     * Constructs a Toggle (defaults RadioGroup to null)
     */
    public Toggle(String pLabel,
		  String pName,
		  Container pParent,
		  boolean state) {
	this(pLabel, pName, pParent, null, state);
    }

    /** Sets the state of the Toggle. */
    public void setState(boolean state) {
	if (state && group != null) {
	    group.setCurrent(this);
	}
	wServer.toggleSetState(this, state);
    }

    /** Returns the state of the Toggle. */
    public boolean getState() {
	return wServer.toggleGetState(this);
    }

    /** Moves this toggle.*/
    public void move(int x, int y) {
	super.move(x,y);
	wServer.toggleMoveTo(this, x, y);
    }

    /** Reshapes this Toggle. */
    public void reshape(int x, int y, int w, int h) {
	super.reshape(x, y, w, h);
	wServer.toggleReshape(this, x, y, w, h);
    }
	
    /** Disposes of this Toggle. */
    public void dispose() {
	wServer.toggleDispose(this);
    }

    /** Shows this Toggle. */
    public void map() {
	wServer.toggleShow(this);
	mapped = true;
    }

    /** Hides this Toggle. */
    public void unMap() {
	wServer.toggleHide(this);
	mapped = false;
    }

    
    /** Override this method to take some action when the state
     * changes.
     */
    public void selected() {
    }

    void handleStateChanged() {
	if (group != null) {
	    group.setCurrent(this);
	}
	selected();
    }
}
