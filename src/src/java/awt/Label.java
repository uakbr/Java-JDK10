/*
 * @(#)Label.java	1.17 95/11/21 Sami Shaio
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

import java.awt.peer.LabelPeer;

/**
 * A component that displays a single line of read-only text.
 *
 * @version	1.17, 11/21/95
 * @author 	Sami Shaio
 */
public class Label extends Component {

    /**
     * The left alignment.
     */
    public static final int LEFT 	= 0;

    /** 
     * The center alignment.
     */
    public static final int CENTER 	= 1;

    /**
     * The right alignment.
     */
    public static final int RIGHT 	= 2;

    /**
     * The label.
     */
    String label;
    
    /**
     * The label's alignment.  The default alignment is set
     * to be left justified.
     */
    int	   alignment = LEFT;

    /**
     * Constructs an empty label.
     */
    public Label() {
	this("");
    }

    /**
     * Constructs a new label with the specified String of text.
     * @param label the text that makes up the label
     */
    public Label(String label) {
	this.label = label;
    }

    /**
     * Constructs a new label with the specified String of 
     * text and the specified alignment.
     * @param label the String that makes up the label
     * @param alignment the alignment value
     */
    public Label(String label, int alignment) {
	this.label = label;
	setAlignment(alignment);
    }

    /**
     * Creates the peer for this label.  The peer allows us to
     * modify the appearance of the label without changing its 
     * functionality.
     */
    public synchronized void addNotify() {
	peer = getToolkit().createLabel(this);
	super.addNotify();
    }

    /** 
     * Gets the current alignment of this label. 
     * @see #setAlignment
     */
    public int getAlignment() {
	return alignment;
    }

    /** 
     * Sets the alignment for this label to the specified 
     * alignment.
     * @param alignment the alignment value
     * @exception IllegalArgumentException If an improper alignment was given. 
     * @see #getAlignment
     */
    public void setAlignment(int alignment) {
	switch (alignment) {
	  case LEFT:
	  case CENTER:
	  case RIGHT:
	    this.alignment = alignment;
	    LabelPeer peer = (LabelPeer)this.peer;
	    if (peer != null) {
		peer.setAlignment(alignment);
	    }
	    return;
	}
	throw new IllegalArgumentException("improper alignment: " + alignment);
    }

    /** 
     * Gets the text of this label. 
     * @see #setText
     */
    public String getText() {
	return label;
    }

    /** 
     * Sets the text for this label to the specified text.
     * @param label the text that makes up the label 
     * @see #getText
     */
    public void setText(String label) {
	if (label != this.label && (this.label == null
				    || !this.label.equals(label))) {
	    this.label = label;
	    LabelPeer peer = (LabelPeer)this.peer;
	    if (peer != null) {
		peer.setText(label);
	    }
	}
    }

    /**
     * Returns the parameter String of this label.
     */
    protected String paramString() {
	String str = ",align=";
	switch (alignment) {
	  case LEFT:   str += "left"; break;
	  case CENTER: str += "center"; break;
	  case RIGHT:  str += "right"; break;
	}
	return super.paramString() + str + ",label=" + label;
    }
}
