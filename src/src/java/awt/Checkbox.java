/*
 * @(#)Checkbox.java	1.13 95/12/14 Sami Shaio
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

import java.awt.peer.CheckboxPeer;

/**
 * A Checkbox object is a graphical user interface element that has a boolean 
 * state.
 *
 * @version	1.13 12/14/95
 * @author 	Sami Shaio
 */
public class Checkbox extends Component {
    /**
     * The label of the Checkbox.
     */
    String label;

    /**
     * The state of the Checkbox.
     */
    boolean state;

    /**
     * The check box group.
     */
    CheckboxGroup group;

    /**
     * Helper function for setState and CheckboxGroup.setCurrent
     * Should remain package-private.
     */
    void setStateInternal(boolean state) {
	this.state = state;
	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setState(state);
	}
    }

    /**
     * Constructs a Checkbox with no label, no Checkbox group, and initialized  
     * to a false state.
     */
    public Checkbox() {
    }

    /**
     * Constructs a Checkbox with the specified label, no Checkbox group, and 
     * initialized to a false state.
     * @param label the label on the Checkbox
     */
    public Checkbox(String label) {
	this.label = label;
    }

    /**
     * Constructs a Checkbox with the specified label, specified Checkbox 
     * group, and specified boolean state.  If the specified CheckboxGroup
     * is not equal to null, then this Checkbox becomes a Checkbox button.  
     * If the Checkbox becomes a button, this simply means that only 
     * one Checkbox in a CheckboxGroup may be set at a time.
     * @param label the label on the Checkbox
     * @param group the CheckboxGroup this Checkbox is in
     * @param state is the initial state of this Checkbox
     */
    public Checkbox(String label, CheckboxGroup group, boolean state) {
	this.label = label;
	this.state = state;
	this.group = group;
	if (state && (group != null)) {
	    group.setCurrent(this);
	}
    }

    /**
     * Creates the peer of the Checkbox. The peer allows you to change the
     * look of the Checkbox without changing its functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createCheckbox(this);
	super.addNotify();
    }

    /**
     * Gets the label of the button.
     * @see #setLabel
     */
    public String getLabel() {
	return label;
    }

    /**
     * Sets the button with the specified label.
     * @param label the label of the button
     * @see #getLabel
     */
    public void setLabel(String label) {
	this.label = label;

	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setLabel(label);
	}
    }

    /** 
     * Returns the boolean state of the Checkbox. 
     * @see #setState
     */
    public boolean getState() {
	return state;
    }
	
    /** 
     * Sets the Checkbox to the specifed boolean state.
     * @param state the boolean state 
     * @see #getState
     */
    public void setState(boolean state) {
	CheckboxGroup group = this.group;
	if (group != null) {
	    if (state) {
		group.setCurrent(this);
	    } else if (group.getCurrent() == this) {
		state = true;
	    }
	}
	setStateInternal(state);
    }

    /**
     * Returns the checkbox group.
     * @see #setCheckboxGroup
     */
    public CheckboxGroup getCheckboxGroup() {
	return group;
    }

    /**
     * Sets the CheckboxGroup to the specified group.
     * @param g the new CheckboxGroup
     * @see #getCheckboxGroup
     */
    public void setCheckboxGroup(CheckboxGroup g) {
	CheckboxGroup group = this.group;
	if (group != null) {
	    group.setCurrent(null);
	}
	this.group = g;
	CheckboxPeer peer = (CheckboxPeer)this.peer;
	if (peer != null) {
	    peer.setCheckboxGroup(g);
	}
    }

    /**
     * Returns the parameter String of this Checkbox.
     */
    protected String paramString() {
	String str = super.paramString();
	String label = this.label;
	if (label != null) {
	    str += ",label=" + label;
	}
	return str + ",state=" + state;
    }
}
